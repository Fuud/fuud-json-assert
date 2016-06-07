package org.fuud.json.asserts.impl.model;

import org.fuud.json.asserts.impl.parse.CharAndPosition;
import org.fuud.json.asserts.impl.parse.Source;

import java.io.EOFException;
import java.io.IOException;

public class NullNode implements ValueNode, Node {
    @Override
    public String toString() {
        return "null";
    }

    public static NullNode parse(Source source) throws IOException {
        final CharAndPosition firstCharAndPosition = source.readNextNonSpaceChar();
        final char char1 = firstCharAndPosition.getCharacter();
        final char char2 = source.readNextChar().getCharacter();
        final char char3 = source.readNextChar().getCharacter();
        final char char4 = source.readNextChar().getCharacter();

        if ("null".equals("" + char1 + char2 + char3 + char4)) {
            return new NullNode();
        }

        throw new IOException("Invalid null value " + char1 + char2 + char3 + char4 + " at position " + firstCharAndPosition.getPosition());
    }

    public boolean equals(Object other) {
        return other != null && other.getClass().equals(this.getClass());
    }

    @Override
    public int hashCode() {
        return 42;
    }

    public static boolean canStartWith(char firstChar) {
        return firstChar == 'n';
    }
}
