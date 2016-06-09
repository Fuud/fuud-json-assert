package org.fuud.json.asserts.impl.parse;

import java.util.Optional;

public class TextAndNextChar {
    private final String text;
    private final int startPosition;
    private final int endPositionExclusive;
    private final Optional<CharAndPosition> mayBeNextChar;

    public TextAndNextChar(String text, int startPosition, CharAndPosition nextChar) {
        this.text = text;
        this.startPosition = startPosition;
        this.endPositionExclusive = nextChar.getPosition();
        this.mayBeNextChar = Optional.of(nextChar);
    }

    public TextAndNextChar(String text, int startPosition, int endPositionExclusive) {
        this.text = text;
        this.startPosition = startPosition;
        this.endPositionExclusive = endPositionExclusive;
        this.mayBeNextChar = Optional.empty();
    }

    public String getText() {
        return text;
    }

    public Optional<CharAndPosition> getMayBeNextChar() {
        return mayBeNextChar;
    }

    public int getStartPosition() {
        return startPosition;
    }

    public int getEndPositionExclusive() {
        return endPositionExclusive;
    }
}
