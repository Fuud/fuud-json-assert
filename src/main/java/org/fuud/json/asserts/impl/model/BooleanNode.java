package org.fuud.json.asserts.impl.model;

import org.fuud.json.asserts.impl.parse.CharAndPosition;
import org.fuud.json.asserts.impl.parse.JsonParseException;
import org.fuud.json.asserts.impl.parse.Source;

import java.io.IOException;

public class BooleanNode implements ValueNode, Node {
    private final boolean value;

    public BooleanNode(boolean value) {
        this.value = value;
    }

    public boolean isValue() {
        return value;
    }

    @Override
    public String toString() {
        return Boolean.toString(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BooleanNode)) return false;

        BooleanNode that = (BooleanNode) o;

        return value == that.value;

    }

    @Override
    public int hashCode() {
        return (value ? 1 : 0);
    }

    public static BooleanNode parse(Source source) throws IOException {
        final CharAndPosition firstCharAndPos = source.readNextNonSpaceChar();
        final char char1 = firstCharAndPos.getCharacter();
        final char char2 = source.readNextChar().getCharacter();
        final char char3 = source.readNextChar().getCharacter();
        final char char4 = source.readNextChar().getCharacter();

        if ("true".equals("" + char1 + char2 + char3 + char4)) {
            return new BooleanNode(true);
        }

        final char char5 = source.readNextChar().getCharacter();
        if ("false".equals("" + char1 + char2 + char3 + char4 + char5)) {
            return new BooleanNode(false);
        }

        throw new JsonParseException("Invalid boolean value " + char1 + char2 + char3 + char4 + char5 + " at position " + firstCharAndPos.getPosition());
    }

    public static boolean canStartWith(char firstChar) {
        return firstChar == 't' || firstChar == 'f';
    }
}
