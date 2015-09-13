package org.fuud.json.asserts;

import org.fuud.json.asserts.org.json.v20141113.JSONArray;
import org.fuud.json.asserts.org.json.v20141113.JSONException;
import org.fuud.json.asserts.org.json.v20141113.JSONObject;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonAsserts {
    private static final String MISSING_IN_ACTUAL = "   +-   ";
    private static final String NOT_EQUAL = "   <>   ";
    private static final String TYPE_MISMATCH = "   --type-mismatch--   ";

    private final Map<Integer, String> lineToFailure = new HashMap<Integer, String>();
    private final List<String> expectedRendering = new ArrayList<String>();
    private final List<String> actualRendering = new ArrayList<String>();


    private JsonAsserts() {
    }

    public static void assertEquals(String expected, String actual) {
        JsonAsserts jsonAsserts = new JsonAsserts();
        jsonAsserts.assertEqualsImpl(expected, actual);
    }

    private void assertEqualsImpl(String expected, String actual) {
        final JSONArray expectedJson = wrapInArray(expected);
        final JSONArray actualJson = wrapInArray(actual);

        checkEqualsImpl(expectedJson, actualJson, 0);
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

    private void checkEqualsImpl(JSONObject expectedJson, JSONObject actualJson, int ident) {
        List<String> keys = new ArrayList<String>(expectedJson.keySet());
        int actualKeysCount = actualJson.keySet().size();

        for (int i = 0; i < keys.size(); i++) {
            String expectedKey = keys.get(i);
            Object expectedValue = expectedJson.get(expectedKey);
            String expectedLine = spaces(ident) + '"' + expectedKey + "\" : ";
            boolean isLastElementInExpected = i == keys.size() - 1;
            boolean isLastElementInActual = i == actualKeysCount - 1;

            if (!actualJson.has(expectedKey)) {
                renderMissingActual(ident, expectedValue, expectedLine, isLastElementInExpected);
            } else {
                final Object actualValue = actualJson.get(expectedKey);
                checkEquals(ident, expectedValue, actualValue, expectedLine, isLastElementInExpected, isLastElementInActual);
            }
        }
    }

    private void checkEqualsImpl(JSONArray expectedJson, JSONArray actualJson, int ident) {
        for (int i = 0; i < expectedJson.length(); i++) {
            Object expectedValue = expectedJson.get(i);
            boolean isLastElementInExpected = i == expectedJson.length() - 1;
            boolean isLastElementInActual = i == actualJson.length() - 1;
            if (i >= actualJson.length()) {
                renderMissingActual(ident, expectedValue, "", isLastElementInExpected);
            } else {
                Object actualValue = actualJson.get(i);
                checkEquals(ident, expectedValue, actualValue, "", isLastElementInExpected, isLastElementInActual);
            }
        }
    }

    private void renderMissingActual(int ident, Object expectedValue, String expectedLine, boolean isLastElementInExpected) {
        String suffix = isLastElementInExpected ? "" : ",";

        int line = expectedRendering.size();

        lineToFailure.put(line, MISSING_IN_ACTUAL);

        if (expectedValue instanceof String) {
            expectedLine += '"' + ((String) expectedValue) + '"';
            expectedRendering.add(expectedLine + suffix);
            actualRendering.add("");
        } else if (expectedValue instanceof Number) {
            expectedLine += expectedValue;
            expectedRendering.add(expectedLine + suffix);
            actualRendering.add("");
        } else if (expectedValue == null || expectedValue == JSONObject.NULL) {
            expectedLine += expectedValue;
            expectedRendering.add(expectedLine + suffix);
            actualRendering.add("");
        } else if (expectedValue instanceof Boolean) {
            expectedLine += expectedValue;
            expectedRendering.add(expectedLine + suffix);
            actualRendering.add("");
        } else if (expectedValue instanceof JSONObject) {
            expectedLine += "{";
            expectedRendering.add(expectedLine + suffix);
            actualRendering.add("");
            renderMissingActual((JSONObject) expectedValue, ident + 2);
            expectedRendering.add(spaces(ident) + "}");
            actualRendering.add("");
        } else if (expectedValue instanceof JSONArray) {
            expectedLine += "[";
            expectedRendering.add(expectedLine + suffix);
            actualRendering.add("");
            renderMissingActual((JSONArray) expectedValue, ident + 2);
            expectedRendering.add(spaces(ident) + "]");
            actualRendering.add("");
        } else {
            throw new NotImplementedException();
        }
    }

    private void checkEquals(int ident, Object expectedValue, Object actualValue, String linePrefix, boolean isLastElementInExpected, boolean isLastElementInActual) {
        String suffixForExpected = isLastElementInExpected ? "" : ",";
        String suffixForActual = isLastElementInActual ? "" : ",";
        int line = expectedRendering.size();
        if (actualValue instanceof JSONObject && expectedValue instanceof JSONObject) {
            linePrefix += "{";
            expectedRendering.add(linePrefix);
            actualRendering.add(linePrefix);
            checkEqualsImpl((JSONObject) expectedValue, (JSONObject) actualValue, ident + 2);
            expectedRendering.add(spaces(ident) + "}" + suffixForExpected);
            actualRendering.add(spaces(ident) + "}" + suffixForActual);
        } else if (actualValue instanceof String && expectedValue instanceof String) {
            if (!actualValue.equals(expectedValue)) {
                lineToFailure.put(line, NOT_EQUAL);
            }
            expectedRendering.add(linePrefix + '"' + expectedValue + '"' + suffixForExpected);
            actualRendering.add(linePrefix + '"' + actualValue + '"' + suffixForActual);
        } else if (actualValue instanceof Number && expectedValue instanceof Number) {
            if (!actualValue.equals(expectedValue)) {
                lineToFailure.put(line, NOT_EQUAL);
            }
            expectedRendering.add(linePrefix + expectedValue + suffixForExpected);
            actualRendering.add(linePrefix + actualValue + suffixForActual);
        } else if (actualValue instanceof Boolean && expectedValue instanceof Boolean) {
            if (!actualValue.equals(expectedValue)) {
                lineToFailure.put(line, NOT_EQUAL);
            }
            expectedRendering.add(linePrefix + expectedValue + suffixForExpected);
            actualRendering.add(linePrefix + actualValue + suffixForActual);
        } else if (actualValue instanceof JSONArray && expectedValue instanceof JSONArray) {
            linePrefix += "[";
            expectedRendering.add(linePrefix);
            actualRendering.add(linePrefix);
            checkEqualsImpl((JSONArray) expectedValue, (JSONArray) actualValue, ident + 2);
            expectedRendering.add(spaces(ident) + "]" + suffixForExpected);
            actualRendering.add(spaces(ident) + "]" + suffixForActual);
        } else {
            lineToFailure.put(line, TYPE_MISMATCH);
            List<String> expectedLines = renderObject(expectedValue, ident, linePrefix, suffixForExpected);
            List<String> actualLines = renderObject(actualValue, ident, linePrefix, suffixForActual);

            for (String expectedLine : expectedLines) {
                expectedRendering.add(expectedLine);
            }

            for (String actualLine : actualLines) {
                actualRendering.add(actualLine);
            }

            for (int i = expectedLines.size(); i < actualLines.size(); i++) {
                expectedRendering.add("");
            }

            for (int i = actualLines.size(); i < expectedLines.size(); i++) {
                actualRendering.add("");
            }
        }
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
            } else if (expectedValue == null || expectedValue == JSONObject.NULL) {
                expectedRendering.add(spaces(ident) + '"' + expectedKey + '"' + " : null");
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


    private List<String> renderObject(Object object, int ident, String linePrefix, String suffix) {
        List<String> result = new ArrayList<String>();
        if (object instanceof String) {
            result.add(linePrefix + '"' + object + '"' + suffix);
        } else if (object instanceof Number) {
            result.add(linePrefix + object + suffix);
        } else if (object == null || object == JSONObject.NULL) {
            result.add(linePrefix + "null" + suffix);
        } else if (object instanceof Boolean) {
            result.add(linePrefix + object + suffix);
        } else if (object instanceof JSONObject) {
            result.add(linePrefix + "{");
            renderObject((JSONObject) object, result, ident + 2);
            result.add(spaces(ident) + '}' + suffix);
        } else if (object instanceof JSONArray) {
            result.add(linePrefix + "[");
            renderArray((JSONArray) object, result, ident + 2);
            result.add(spaces(ident) + ']' + suffix);
        } else {
            throw new NotImplementedException();
        }
        return result;
    }

    private void renderObject(JSONObject object, List<String> result, int ident) {
        for (String objectKey : object.keySet()) {
            final Object objectValue = object.get(objectKey);
            if (objectValue instanceof String) {
                result.add(spaces(ident) + '"' + objectKey + '"' + " : " + '"' + objectValue + '"');
            } else if (objectValue instanceof Number) {
                result.add(spaces(ident) + '"' + objectKey + '"' + " : " + objectValue);
            } else if (objectValue == null || objectValue == JSONObject.NULL) {
                result.add(spaces(ident) + '"' + objectKey + '"' + " : null");
            } else if (objectValue instanceof Boolean) {
                result.add(spaces(ident) + '"' + objectKey + '"' + " : " + objectValue);
            } else if (objectValue instanceof JSONObject) {
                result.add(spaces(ident) + '"' + objectKey + '"' + " : {");
                renderObject((JSONObject) objectValue, result, ident + 2);
                result.add(spaces(ident) + '}');
            } else if (objectValue instanceof JSONArray) {
                result.add(spaces(ident) + '"' + objectKey + '"' + " : [");
                renderArray((JSONArray) objectValue, result, ident + 2);
                result.add(spaces(ident) + ']');
            } else {
                throw new NotImplementedException();
            }
        }
    }

    private void renderArray(JSONArray object, List<String> result, int ident) {
        final int count = object.length();
        for (int i = 0; i < count; i++) {
            final Object expectedValue = object.get(i);
            if (expectedValue instanceof String) {
                result.add(spaces(ident) + '"' + expectedValue + '"' + (i == count - 1 ? "" : ","));
            } else if (expectedValue instanceof Number) {
                result.add(spaces(ident) + expectedValue);
            } else if (expectedValue instanceof Boolean) {
                result.add(spaces(ident) + expectedValue);
            } else if (expectedValue instanceof JSONObject) {
                result.add(spaces(ident) + "{");
                renderMissingActual((JSONObject) expectedValue, ident + 2);
                result.add(spaces(ident) + '}');
            } else if (expectedValue instanceof JSONArray) {
                result.add(spaces(ident) + "[");
                renderMissingActual((JSONArray) expectedValue, ident + 2);
                result.add(spaces(ident) + ']');
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
            } else if (expectedValue == null || expectedValue == JSONObject.NULL) {
                expectedRendering.add(spaces(ident) + "null");
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

    private static JSONArray wrapInArray(final String s) throws JSONException {
        return new JSONArray("[" + s + "]}");
    }
}
