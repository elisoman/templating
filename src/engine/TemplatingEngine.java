package engine;
import java.util.Map;


public class TemplatingEngine {

    private static final String SUBSTITUTION_START_SEQ = "${";
    private static final String SUBSTITUTION_END_SEQ = "}";

    private static final char SUBSTITUTION_START_CHAR = '$';
    private static final char ESCAPE_CHAR = '@';

    /**
     * Takes a template string that contains variables that start with a "${"
     * and ends with a "}" with a string in between. The string sequence is then
     * replaced with the value that relates to a variable found in the variables
     * map provided.
     * <p>
     * A literal "${" can be added to the template by escaping the sequence with
     * a '@'.
     *
     * <p>
     * <b>Example:</b><br>
     * template - "A variable starts with @${ as of ${day}." <br>
     * variables - { day : "Monday" } <br>
     * result - "A variable starts with ${ as of Monday"
     *
     * <p>
     * A literal '@' can be added before a "${" by escaping the '@' with an '@'.
     * <p>
     * <b>Example:</b><br>
     * template - "Hello ${name}@@${emailAccount}" <br>
     * variables - { name : "Bob" } <br>
     * result - "Hello Bob@unittesters.com"
     *
     * @param variables
     *            A map that contains variable names (minus the "${" and "}")
     *            and the values to replace those variable names.
     *
     * @param template
     *            A string that contains variables with the following syntax
     *            ${variableName}. The variable will be replaced with the value
     *            found in the supplied variables map.
     *
     * @return A string with the values substituted for all variables found.
     *
     * @throws IllegalArgumentException
     *             If a variable is found but is not in the provided variables
     *             map.
     * @throws IllegalArgumentException
     *             If a non-escaped "${" is found in the template and no closing
     *             '}' is found afterwards.
     */
    public static String substitute(Map<String, String> variables, String template) {
        if (!template.contains(SUBSTITUTION_START_SEQ)
                && !template.contains(SUBSTITUTION_END_SEQ)
                && template.indexOf(ESCAPE_CHAR) != -1) {
            return template;
        }

        StringBuilder result = new StringBuilder("");

        for (int i = 0; i < template.length(); i++) {
            char c = template.charAt(i);

            //Check for the escape sequence which start with '@' and the remaining string
            //is long enough to contain the substitution start characters '${'.
            if (c == ESCAPE_CHAR && (i + 1) < template.length()) {

                i = handleEscapeFound(template, i, result);

            //Check if the char is '$' and the following char is '{'.
            //If this is the case handle the substitution.
            } else if (c == SUBSTITUTION_START_CHAR && (i + 1) < template.length()
                    && template.charAt(i + 1) == '{') {

                i = handleSubstituionStartFound(template, variables, i, result);

            //Nothing to do but add the result.
            } else {
                result.append(c);
            }
        }

        return result.toString();
    }

    private static int handleEscapeFound(String template, int currentIndex, StringBuilder result) {
        //Get the next character following the '@'
        char escapedValue = template.charAt(currentIndex +1);

        //If the character following the '@' is a '@'
        //then add only one '@' and increment the index.
        if (escapedValue == ESCAPE_CHAR) {
            currentIndex ++;
            result.append(escapedValue);

        //Check if the following char is the '$' and the
        //remaining template has enough room to contain a
        //'{'.
        } else if (escapedValue == SUBSTITUTION_START_CHAR
                && currentIndex + SUBSTITUTION_START_SEQ.length() < template
                        .length()) {

            char nextEscapedValue = template.charAt(currentIndex + SUBSTITUTION_START_SEQ.length());

            //If the characters following the '@' are "${"
            //then add the "${" to the result and increment index
            if (nextEscapedValue == '{') {
                result.append(escapedValue);
                result.append(nextEscapedValue);
                currentIndex += SUBSTITUTION_START_SEQ.length();

            } else {
                result.append(ESCAPE_CHAR);
            }

        } else {
            result.append(ESCAPE_CHAR);
        }

        return currentIndex;
    }

    private static int handleSubstituionStartFound(String template,
            Map<String, String> variables, int currentIndex,
            StringBuilder result) {

        //Get the index of the next '{' following the "${" found.
        int variableEnd = template.substring(currentIndex).indexOf(SUBSTITUTION_END_SEQ);

        //If the '}' is not found in the remaining template string
        //then throw an exception.
        if (variableEnd == -1) {
            throw new IllegalArgumentException(
                    "Invalid template provided.  '${' provided without closing '}': "
                            + template);
        }

        //Get the string in between the "${" and "}".
        String variable = template.substring(
                currentIndex + SUBSTITUTION_START_SEQ.length(), variableEnd + currentIndex);

        //Get the variable value from the variables map.
        String substitution = variables.get(variable);

        //Throw exception if the variable is not in the variables map.
        if (substitution == null) {
            throw new IllegalArgumentException(
                    "Template contains invalid variable: "
                            + variable);
        }

        //Add the substitution to the result.
        result.append(substitution);

        //Increment the index.
        return currentIndex += variableEnd;
    }
}
