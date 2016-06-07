package org.fuud.json.asserts.impl.model;

import org.fuud.json.asserts.impl.parse.CharAndPosition;
import org.fuud.json.asserts.impl.parse.JsonParseException;
import org.fuud.json.asserts.impl.parse.Source;
import org.fuud.json.asserts.impl.util.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ArrayNode implements ValueNode, Node {
    private final List<ValueNode> elements;

    public ArrayNode(List<ValueNode> elements) {
        this.elements = Objects.requireNonNull(elements);
    }

    public List<ValueNode> getElements() {
        return elements;
    }

    @Override
    public String toString() {
        return "[" +
                elements.stream().map(Object::toString).map(Utils.addIdent).collect(Collectors.joining(",\n")) +
                "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ArrayNode)) return false;

        ArrayNode arrayNode = (ArrayNode) o;

        return elements.equals(arrayNode.elements);

    }

    @Override
    public int hashCode() {
        return elements.hashCode();
    }

    public static ArrayNode parse(Source source) throws IOException {
        final CharAndPosition startChar = source.readNextNonSpaceChar();
        if (startChar.getCharacter() != '[') {
            throw new JsonParseException("[", startChar);
        }

        final CharAndPosition mayBeEnd = source.lookupForNextNonSpaceChar();
        if (mayBeEnd.getCharacter() == ']') {
            source.readNextNonSpaceChar(); // skip up to ']'
            return new ArrayNode(new ArrayList<>());
        }

        List<ValueNode> values = new ArrayList<>();
        while (true) {
            final ValueNode valueNode = ValueNode.parse(source);
            values.add(valueNode);

            final CharAndPosition delimiterOrEndChar = source.readNextNonSpaceChar();

            if (delimiterOrEndChar.getCharacter() == ']') {
                break;
            }
            if (delimiterOrEndChar.getCharacter() != ',') {
                throw new JsonParseException("],", delimiterOrEndChar);
            }
        }

        return new ArrayNode(values);
    }

    public static boolean canStartWith(char firstChar) {
        return firstChar == '[';
    }
}
