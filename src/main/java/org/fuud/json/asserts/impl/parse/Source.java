package org.fuud.json.asserts.impl.parse;

import org.intellij.lang.annotations.Language;

import java.io.EOFException;
import java.util.Optional;
import java.util.function.Predicate;

public class Source {
    private final String json;
    private int position;

    public Source(@Language("json") String json) {
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
            throw new EOFException("End of data was reached. Data was " + json);
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

    public TextAndNextChar readUntilEofOr(Predicate<Character> stopAt) {
        String result = "";
        int startPosition = position;
        while (position < json.length()) {
            final char currentChar = json.charAt(position);
            if (stopAt.test(currentChar)) {
                return new TextAndNextChar(result, startPosition, new CharAndPosition(position, currentChar));
            } else {
                result += currentChar;
                position++;
            }
        }
        return new TextAndNextChar(result, startPosition, position);
    }

    public TextAndNextChar readUntilEofOr(char stopAt) {
        return readUntilEofOr(character -> character == stopAt);
    }

    public TextAndNextChar readUntil(String sequence) throws EOFException {
        String result = "";
        int startPosition = position;
        while (position <= json.length() - sequence.length()) {
            final String actual = json.substring(position, position + sequence.length());
            if (actual.equals(sequence)) {
                return new TextAndNextChar(result, startPosition, new CharAndPosition(position, json.charAt(position)));
            } else {
                result += json.charAt(position);
                position++;
            }
        }
        throw new EOFException("Failed to find sequence '" + sequence + " starting from " + startPosition + " in string " + json);
    }

    public TextAndNextChar readUntil(char character) throws EOFException {
        return readUntil("" + character);
    }

    public void skipWhitespaces() {
        readUntilEofOr(character -> !Character.isWhitespace(character));
    }

    public int getPosition() {
        return position;
    }

    public Optional<CharAndPosition> lookupFor(Predicate<Character> subject) {
        while (position < json.length()) {
            final char currentChar = json.charAt(position);
            if (subject.test(currentChar)) {
                return Optional.of(new CharAndPosition(position, currentChar));
            } else {
                position++;
            }
        }
        return Optional.empty();
    }

    public Optional<CharAndPosition> lookupFor(char subject) {
        return lookupFor(character -> character == subject);
    }

    public Source subSource(int startPosition, int endPositionExclusive) {
        final Source source = new Source(json.substring(0, endPositionExclusive));
        source.position = startPosition; // to have correct position
        return source;
    }
}
