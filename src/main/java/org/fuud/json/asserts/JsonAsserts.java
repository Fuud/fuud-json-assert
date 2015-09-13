package org.fuud.json.asserts;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonAsserts {
    private static final String MISSING_IN_ACTUAL = "   +-   ";
    private static final String NOT_EQUAL = "   <>   ";

    private final Map<Integer, String> lineToFailure = new HashMap<Integer, String>();
    private final List<String> expectedRendering = new ArrayList<String>();
    private final List<String> actualRendering = new ArrayList<String>();


    private JsonAsserts() {
    }

    public static void assertEquals(String expected, String actual) {
        final Object expectedJson = parseJSON(expected);
        final Object actualJson = parseJSON(actual);

        if (expectedJson instanceof JSONObject && actualJson instanceof JSONObject) {
            new JsonAsserts().assertEquals((JSONObject) expectedJson, (JSONObject) actualJson);
        }
    }

    private void assertEquals(JSONObject expectedJson, JSONObject actualJson) {
        checkEquals(expectedJson, actualJson);
        if (!lineToFailure.isEmpty()) {
            throw new AssertionError(glueRendering());
        }
    }

    private String glueRendering() {
        int maxExpectedLineLength = 0;
        for (String expectedLine : expectedRendering) {
            maxExpectedLineLength = Math.max(expectedLine.length(), maxExpectedLineLength);
        }
        maxExpectedLineLength += 2;

        int maxFailureReasonLength = 0;
        for (String failure : lineToFailure.values()) {
            maxFailureReasonLength = Math.max(failure.length(), maxExpectedLineLength);
        }
        maxFailureReasonLength += 2;

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < expectedRendering.size(); i++) {
            String expected = expectedRendering.get(i);
            String actual = actualRendering.get(i);
            String failure = lineToFailure.containsKey(i) ? lineToFailure.get(i) : "";

            result.append(expected);
            for (int space = expected.length(); space < maxExpectedLineLength; space++) {
                result.append(" ");
            }

            final int spacesBeforeFailure = (maxFailureReasonLength - failure.length()) / 2;
            for (int space = 0; space < spacesBeforeFailure; space++) {
                result.append(" ");
            }
            result.append(failure);
            for (int space = spacesBeforeFailure + failure.length(); space < maxFailureReasonLength; space++) {
                result.append(" ");
            }
            result.append(actual);
            result.append("\n");
        }

        return result.toString();
    }

    private void checkEquals(JSONObject expectedJson, JSONObject actualJson) {
        expectedRendering.add("{");
        actualRendering.add("{");
        checkEqualsImpl(expectedJson, actualJson, 2);
        expectedRendering.add("}");
        actualRendering.add("}");
    }

    private void checkEqualsImpl(JSONObject expectedJson, JSONObject actualJson, int ident) {
        for (String expectedKey : expectedJson.keySet()) {
            Object expectedValue = expectedJson.get(expectedKey);
            String expectedLine = spaces(ident) + '"' + expectedKey + "\" : ";

            if (actualJson.has(expectedKey)) {
                final Object actualValue = actualJson.get(expectedKey);
                if (actualValue instanceof JSONObject && expectedValue instanceof JSONObject) {
                    expectedLine += "{";
                    expectedRendering.add(expectedLine);
                    actualRendering.add(expectedLine);
                    checkEqualsImpl((JSONObject) expectedValue, (JSONObject) actualValue, ident + 2);
                    expectedRendering.add(spaces(ident) + "}");
                    actualRendering.add(spaces(ident) + "}");
                } else if (actualValue instanceof String && expectedValue instanceof String) {
                    if (!actualValue.equals(expectedValue)) {
                        int line = expectedRendering.size();
                        lineToFailure.put(line, NOT_EQUAL);
                    }
                    expectedRendering.add(expectedLine + '"' + expectedValue + '"');
                    actualRendering.add(expectedLine + '"' + actualValue + '"');
                } else if (actualValue instanceof Number && expectedValue instanceof Number) {
                    if (!actualValue.equals(expectedValue)) {
                        int line = expectedRendering.size();
                        lineToFailure.put(line, NOT_EQUAL);
                    }
                    expectedRendering.add(expectedLine + expectedValue);
                    actualRendering.add(expectedLine + actualValue);
                } else if (actualValue instanceof Boolean && expectedValue instanceof Boolean) {
                    if (!actualValue.equals(expectedValue)) {
                        int line = expectedRendering.size();
                        lineToFailure.put(line, NOT_EQUAL);
                    }
                    expectedRendering.add(expectedLine + expectedValue);
                    actualRendering.add(expectedLine + actualValue);
                }else if (actualValue instanceof JSONArray && expectedValue instanceof JSONArray) {
                    expectedLine += "[";
                    expectedRendering.add(expectedLine);
                    actualRendering.add(expectedLine);
                    checkEqualsImpl((JSONArray) expectedValue, (JSONArray) actualValue, ident + 2);
                    expectedRendering.add(spaces(ident) + "]");
                    actualRendering.add(spaces(ident) + "]");
                }
            } else {
                int line = expectedRendering.size();
                lineToFailure.put(line, MISSING_IN_ACTUAL);
                if (expectedValue instanceof String) {
                    expectedLine += '"' + ((String) expectedValue) + '"';
                    expectedRendering.add(expectedLine);
                    actualRendering.add("");
                } else if (expectedValue instanceof Number) {
                    expectedLine += expectedValue;
                    expectedRendering.add(expectedLine);
                    actualRendering.add("");
                } else if (expectedValue instanceof Boolean) {
                    expectedLine += expectedValue;
                    expectedRendering.add(expectedLine);
                    actualRendering.add("");
                } else if (expectedValue instanceof JSONObject) {
                    expectedLine += "{";
                    expectedRendering.add(expectedLine);
                    actualRendering.add("");
                    renderMissingActual((JSONObject) expectedValue, ident + 2);
                    expectedRendering.add(spaces(ident) + "}");
                    actualRendering.add("");
                } else if (expectedValue instanceof JSONArray) {
                    expectedLine += "[";
                    expectedRendering.add(expectedLine);
                    actualRendering.add("");
                    renderMissingActual((JSONArray) expectedValue, ident + 2);
                    expectedRendering.add(spaces(ident) + "]");
                    actualRendering.add("");
                }
            }
        }
    }

    private void checkEqualsImpl(JSONArray expectedJson, JSONArray actualJson, int ident) {

    }

    private void renderMissingActual(JSONObject expected, int ident) {
        for (String expectedKey : expected.keySet()) {
            final Object expectedValue = expected.get(expectedKey);
            if (expectedValue instanceof String) {
                expectedRendering.add(spaces(ident) + '"' + expectedKey + '"' + " : " + '"' + expectedValue + '"');
                actualRendering.add("");
            } else if (expectedValue instanceof Number) {
                expectedRendering.add(spaces(ident) + '"' + expectedKey + '"' + " : " + expectedValue);
                actualRendering.add("");
            } else if (expectedValue instanceof Boolean) {
                expectedRendering.add(spaces(ident) + '"' + expectedKey + '"' + " : " + expectedValue);
                actualRendering.add("");
            } else if (expectedValue instanceof JSONObject) {
                expectedRendering.add(spaces(ident) + '"' + expectedKey + '"' + " : {");
                actualRendering.add("");
                renderMissingActual((JSONObject) expectedValue, ident + 2);
                expectedRendering.add(spaces(ident) + '}');
                actualRendering.add("");
            } else if (expectedValue instanceof JSONArray) {
                expectedRendering.add(spaces(ident) + '"' + expectedKey + '"' + " : [");
                actualRendering.add("");
                renderMissingActual((JSONArray) expectedValue, ident + 2);
                expectedRendering.add(spaces(ident) + ']');
                actualRendering.add("");
            } else {
                throw new NotImplementedException();
            }
        }
    }

    private void renderMissingActual(JSONArray expected, int ident) {
        final int count = expected.length();
        for (int i = 0; i < count; i++) {
            final Object expectedValue = expected.get(i);
            if (expectedValue instanceof String) {
                expectedRendering.add(spaces(ident) + '"' + expectedValue + '"' + (i == count - 1 ? "" : ","));
                actualRendering.add("");
            } else if (expectedValue instanceof Number) {
                expectedRendering.add(spaces(ident) + expectedValue);
                actualRendering.add("");
            } else if (expectedValue instanceof Boolean) {
                expectedRendering.add(spaces(ident) + expectedValue);
                actualRendering.add("");
            } else if (expectedValue instanceof JSONObject) {
                expectedRendering.add(spaces(ident) + "{");
                actualRendering.add("");
                renderMissingActual((JSONObject) expectedValue, ident + 2);
                expectedRendering.add(spaces(ident) + '}');
                actualRendering.add("");
            } else if (expectedValue instanceof JSONArray) {
                expectedRendering.add(spaces(ident) + "[");
                actualRendering.add("");
                renderMissingActual((JSONArray) expectedValue, ident + 2);
                expectedRendering.add(spaces(ident) + ']');
                actualRendering.add("");
            } else {
                throw new NotImplementedException();
            }
        }
    }

    private String spaces(int ident) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < ident; i++) {
            result.append(" ");
        }
        return result.toString();
    }

    private static Object parseJSON(final String s) throws JSONException {
        return new JSONObject("{\"json\":" + s + "}").get("json");
    }
}
