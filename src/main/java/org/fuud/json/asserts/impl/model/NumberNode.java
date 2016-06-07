package org.fuud.json.asserts.impl.model;

import org.fuud.json.asserts.impl.parse.JsonParseException;
import org.fuud.json.asserts.impl.parse.Source;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Objects;

public class NumberNode implements ValueNode, Node {
    private final BigDecimal value;

    public NumberNode(BigDecimal value) {
        this.value = Objects.requireNonNull(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NumberNode)) return false;

        NumberNode that = (NumberNode) o;

        return value.compareTo(that.value) == 0;

    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    public static NumberNode parse(Source source) throws IOException {
        source.skipWhitespaces();
        final int startPosition = source.getPosition();
        String numberAsString = source.readUntilEofOr(
                character ->
                        Character.isWhitespace(character) ||
                                character == '"' ||
                                character == ',' ||
                                character == '[' ||
                                character == ']' ||
                                character == '{' ||
                                character == '}');
        final BigDecimal value;
        try {
            value = new BigDecimal(numberAsString);
        } catch (NumberFormatException e) {
            throw new JsonParseException("Can not parse number value " + numberAsString + " starting from position " + startPosition, e);
        }
        return new NumberNode(value);
    }

    public static boolean canStartWith(char firstChar) {
        return Character.isDigit(firstChar) || firstChar == '+' || firstChar == '-';
    }
}
