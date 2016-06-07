package org.fuud.json.asserts.impl.model;

import org.fuud.json.asserts.impl.parse.CharAndPosition;
import org.fuud.json.asserts.impl.parse.JsonParseException;
import org.fuud.json.asserts.impl.parse.Source;

import java.io.IOException;
import java.util.Objects;

public class StringNode implements ValueNode, Node {
    private final String value;

    public StringNode(String value) {
        this.value = Objects.requireNonNull(value);
    }

    public String getValue() {
        return value;
    }

    public static StringNode parse(Source source) throws IOException {
        final CharAndPosition startToken = source.readNextNonSpaceChar();
        if (startToken.getCharacter() != '"') {
            throw new JsonParseException("\"", startToken);
        }
        return new StringNode(readAndUnescapeChars(source));
    }

    private static String readAndUnescapeChars(Source source) throws IOException {
        String result = "";
        while (true) {
            CharAndPosition characterAndPos = source.readNextChar();
            final char character = characterAndPos.getCharacter();

            if (character == '"') {
                return result;
            }

            if (Character.isISOControl(character)) {
                throw new JsonParseException(characterAndPos);
            }
            if (character == '\\') {
                CharAndPosition escapedCharPos = source.readNextChar();
                final char escapedChar = escapedCharPos.getCharacter();
                if (escapedChar == 'u') {
                    final String hexUnicodeChar = source.readChars(4);
                    try {
                        final char unicodeChar = (char) Integer.parseInt(hexUnicodeChar, 16);
                        result += unicodeChar;
                    } catch (NumberFormatException e) {
                        throw new JsonParseException("invalid unicode escape sequence: " + hexUnicodeChar + " starting at position " + (escapedCharPos.getPosition() + 1));
                    }
                } else if (escapedChar == '"' || escapedChar == '\\' || escapedChar == '/') {
                    result += escapedChar;
                } else if (escapedChar == 'b') {
                    result += '\b';
                } else if (escapedChar == 'f') {
                    result += '\f';
                } else if (escapedChar == 'n') {
                    result += '\n';
                } else if (escapedChar == 'r') {
                    result += '\r';
                } else if (escapedChar == 't') {
                    result += '\t';
                } else {
                    throw new JsonParseException("ubfnrt\"", escapedCharPos);
                }
            } else {
                result += character;
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StringNode)) return false;

        StringNode that = (StringNode) o;

        return value.equals(that.value);

    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return '"' + value + '"';
    }

    public static boolean canStartWith(char firstChar) {
        return firstChar == '"';
    }
}
