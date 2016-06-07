package org.fuud.json.asserts.impl.parse;

import java.io.EOFException;
import java.util.function.Predicate;

public class Source {
    private final String json;
    private int position;

    public Source(String json) {
        this.json = json;
        position = 0;
    }


    public CharAndPosition readNextNonSpaceChar() throws EOFException {
        final CharAndPosition charAndPosition = searchNextNonSpaceChar(json, position);
        position = charAndPosition.getPosition() + 1; // should point to next char
        return charAndPosition;
    }

    public CharAndPosition lookupForNextNonSpaceChar() throws EOFException {
        return searchNextNonSpaceChar(json, position);
    }

    private static CharAndPosition searchNextNonSpaceChar(String json, int startPosition) throws EOFException {
        int currentPosition = startPosition;
        while (currentPosition < json.length()) {
            final char character = json.charAt(currentPosition);
            if (!Character.isWhitespace(character)) {
                return new CharAndPosition(currentPosition, character);
            }
            currentPosition++;//shift anyway
        }
        throw new EOFException("Failed to read next non space token starting from position " + currentPosition + " in string " + json);
    }

    public CharAndPosition readNextChar() throws EOFException {
        if (position >= json.length()) {
            throw new EOFException("End of data was reached");
        }
        final CharAndPosition nextChar = new CharAndPosition(position, json.charAt(position));
        position++;
        return nextChar;
    }

    public String readChars(int count) throws EOFException {
        String result = "";
        for (int i = 0; i < count; i++) {
            result += readNextChar().getCharacter();
        }
        return result;
    }

    public String readUntilEofOr(Predicate<Character> stopAt) {
        String result = "";
        while (position < json.length()) {
            final char currentChar = json.charAt(position);
            if (stopAt.test(currentChar)) {
                return result;
            } else {
                result += currentChar;
                position++;
            }
        }
        return result;
    }

    public void skipWhitespaces() {
        readUntilEofOr(character -> !Character.isWhitespace(character));
    }

    public int getPosition() {
        return position;
    }
}
