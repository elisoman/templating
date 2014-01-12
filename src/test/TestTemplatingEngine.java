package test;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;

import engine.TemplatingEngine;

public class TestTemplatingEngine {

    @Test
    public void noSubstitutions() {
        String before = "test string with no substitutions";

        String result = TemplatingEngine.substitute(
                new HashMap<String, String>(), before);

        Assert.assertEquals(before, result);
    }

    @Test
    public void oneSubstitution() {
        String before = "${name}";

        Map<String, String> variables = new HashMap<String, String>();

        variables.put("name", "Bob");

        String result = TemplatingEngine.substitute(variables, before);

        Assert.assertEquals("Bob", result);
    }

    @Test
    public void twoSubstitutionsInRow() {
        String before = "${name}${num}";

        Map<String, String> variables = new HashMap<String, String>();

        variables.put("name", "Bob");
        variables.put("num", "123");

        String result = TemplatingEngine.substitute(variables, before);

        Assert.assertEquals("Bob123", result);
    }

    @Test
    public void twoSubstitutionsInRowWithText() {
        String before = "Your id is ${name}${num}. Thank you.";

        Map<String, String> variables = new HashMap<String, String>();

        variables.put("name", "Bob");
        variables.put("num", "123");

        String result = TemplatingEngine.substitute(variables, before);

        Assert.assertEquals("Your id is Bob123. Thank you.", result);
    }

    @Test
    public void oneSubstitutionWithText() {
        String before = "Hi ${name}. How are you?";

        Map<String, String> variables = new HashMap<String, String>();

        variables.put("name", "Bob");

        String result = TemplatingEngine.substitute(variables, before);

        Assert.assertEquals("Hi Bob. How are you?", result);
    }

    @Test
    public void twoSubstitutionWithText() {
        String before = "Hi ${name}. How are you this ${timeOfDay}?";

        Map<String, String> variables = new HashMap<String, String>();

        variables.put("name", "Bob");
        variables.put("timeOfDay", "evening");

        String result = TemplatingEngine.substitute(variables, before);

        Assert.assertEquals("Hi Bob. How are you this evening?", result);
    }

    @Test
    public void endsWithSubstitution() {
        String before = "Hi ${name}. How are you this ${timeOfDay}";

        Map<String, String> variables = new HashMap<String, String>();

        variables.put("name", "Bob");
        variables.put("timeOfDay", "evening");

        String result = TemplatingEngine.substitute(variables, before);

        Assert.assertEquals("Hi Bob. How are you this evening", result);
    }

    @Test
    public void startsAndEndsWithSubstitution() {
        String before = "${name}, how are you this ${timeOfDay}";

        Map<String, String> variables = new HashMap<String, String>();

        variables.put("name", "Bob");
        variables.put("timeOfDay", "evening");

        String result = TemplatingEngine.substitute(variables, before);

        Assert.assertEquals("Bob, how are you this evening", result);
    }

    @Test
    public void oneSubstitutionWithDollarSign() {
        String before = "Hi ${name}. Send $5";

        Map<String, String> variables = new HashMap<String, String>();

        variables.put("name", "Bob");

        String result = TemplatingEngine.substitute(variables, before);

        Assert.assertEquals("Hi Bob. Send $5", result);
    }

    @Test
    public void oneSubstitutionEndsWithDollarSign() {
        String before = "Hi ${name}. Send $";

        Map<String, String> variables = new HashMap<String, String>();

        variables.put("name", "Bob");

        String result = TemplatingEngine.substitute(variables, before);

        Assert.assertEquals("Hi Bob. Send $", result);
    }

    @Test
    public void invalidTemplateNoClosingBrace() {
        String before = "Hi ${name. Send $5.";

        Map<String, String> variables = new HashMap<String, String>();

        variables.put("name", "Bob");
        try {
            TemplatingEngine.substitute(variables, before);
        } catch (IllegalArgumentException iae) {
            Assert.assertTrue(iae.getMessage().startsWith("Invalid template provided"));
            return;
        }

        Assert.assertTrue("Should have thrown IllegalArgumentException.", false);
    }

    @Test
    public void missingVariableInTemplate() {
        String before = "Hi ${names}. Send $5.";

        Map<String, String> variables = new HashMap<String, String>();

        variables.put("name", "Bob");
        try {
            TemplatingEngine.substitute(variables, before);
        } catch (IllegalArgumentException iae) {
            Assert.assertTrue(iae.getMessage().startsWith("Template contains invalid variable: names"));
            return;
        }

        Assert.assertTrue("Should have thrown IllegalArgumentException.", false);
    }

    @Test
    public void startTemplateEscaped() {
        String before = "Hi @${Bob. Send 5.";

        Map<String, String> variables = new HashMap<String, String>();

        variables.put("name", "Bob");

        String result = TemplatingEngine.substitute(variables, before);

        Assert.assertEquals("Hi ${Bob. Send 5.", result);
    }

    @Test
    public void startsWithEscapeCharacter() {
        String before = "@Hi ${name}";

        Map<String, String> variables = new HashMap<String, String>();

        variables.put("name", "Bob");

        String result = TemplatingEngine.substitute(variables, before);

        Assert.assertEquals("@Hi Bob", result);
    }

    @Test
    public void endsWithEscapeCharacter() {
        String before = "Hi ${name}@";

        Map<String, String> variables = new HashMap<String, String>();

        variables.put("name", "Bob");

        String result = TemplatingEngine.substitute(variables, before);

        Assert.assertEquals("Hi Bob@", result);
    }

    @Test
    public void startsAndEndsWithEscapeCharacter() {
        String before = "@Hi ${name}@";

        Map<String, String> variables = new HashMap<String, String>();

        variables.put("name", "Bob");

        String result = TemplatingEngine.substitute(variables, before);

        Assert.assertEquals("@Hi Bob@", result);
    }

    @Test
    public void getLiteralEscapeCharBeforeSubstitutionStart() {
        String before = "Hello ${name}@@${emailAccount}";

        Map<String, String> variables = new HashMap<String, String>();

        variables.put("name", "Bob");
        variables.put("emailAccount", "unittesters.com");

        String result = TemplatingEngine.substitute(variables, before);

        Assert.assertEquals("Hello Bob@unittesters.com", result);
    }

    @Test
    public void endsWithEscapeCharSubstitutionStartChar() {
        String before = "Hello ${name}@$";

        Map<String, String> variables = new HashMap<String, String>();

        variables.put("name", "Bob");
        variables.put("emailAccount", "unittesters.com");

        String result = TemplatingEngine.substitute(variables, before);

        Assert.assertEquals("Hello Bob@$", result);
    }

    @Test
    public void escapedEscapeCharAndEscapedSubstitutionStartSeq() {
        String before = "Hello @@@${${name}";

        Map<String, String> variables = new HashMap<String, String>();

        variables.put("name", "Bob");
        variables.put("emailAccount", "unittesters.com");

        String result = TemplatingEngine.substitute(variables, before);

        Assert.assertEquals("Hello @${Bob", result);
    }

    @Test
    public void escapedSubstitutionStartSeqAndEscapedEscapeChar() {
        String before = "Hello @${@@${name}";

        Map<String, String> variables = new HashMap<String, String>();

        variables.put("name", "Bob");
        variables.put("emailAccount", "unittesters.com");

        String result = TemplatingEngine.substitute(variables, before);

        Assert.assertEquals("Hello ${@Bob", result);
    }

}
