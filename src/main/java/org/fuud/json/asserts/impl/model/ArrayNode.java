package org.fuud.json.asserts.impl.model;

import org.fuud.json.asserts.impl.diff.Difference;
import org.fuud.json.asserts.impl.diff.JsonComparator;
import org.fuud.json.asserts.impl.parse.CharAndPosition;
import org.fuud.json.asserts.impl.parse.JsonParseException;
import org.fuud.json.asserts.impl.parse.Source;
import org.fuud.json.asserts.impl.util.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

public class ArrayNode extends ValueNode<ArrayNode> {
    private final List<ValueNode<?>> elements;

    public ArrayNode(List<ValueNode<?>> elements) {
        super(new ArrayNodeJsonComparator());
        this.elements = Objects.requireNonNull(elements);
    }

    public List<ValueNode<?>> getElements() {
        return elements;
    }

    @Override
    public String toString() {
        return "[\n" +
                elements.stream().map(Object::toString).map(Utils.addIdent).collect(Collectors.joining(",\n")) +
                "\n]";
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

        List<ValueNode<?>> values = new ArrayList<>();
        while (true) {
            final ValueNode<?> valueNode = ValueNode.parse(source);
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

    public static class ArrayNodeJsonComparator implements JsonComparator<ArrayNode> {
        @Override
        public List<Difference> compare(ArrayNode left, Node rightNode){
            if (rightNode instanceof ArrayNode) {
                ArrayNode right = (ArrayNode) rightNode;

                final List<ValueNode<?>> leftPropertyNames = left.elements;
                final List<ValueNode<?>> rightPropertyNames = right.elements;

                List<Difference> result = new ArrayList<>();
                for (int i = 0; i < Math.max(leftPropertyNames.size(), rightPropertyNames.size()); i++) {
                    String pathElement = "" + i;
                    if (i < leftPropertyNames.size() && i < rightPropertyNames.size()) {
                        final List<Difference> differences = left.elements.get(i).compare(right.elements.get(i));
                        result.addAll(differences.
                                stream().
                                map(Difference.addParentPath(pathElement)).
                                collect(Collectors.toList()));
                    } else if (i >= leftPropertyNames.size()) {
                        result.add(new Difference(singletonList(pathElement), Difference.DiffType.NOT_EXPECTED));
                    } else if (i >= rightPropertyNames.size()) {
                        result.add(new Difference(singletonList(pathElement), Difference.DiffType.MISSING));
                    }
                }
                return result;

            } else {
                return singletonList(new Difference(emptyList(), Difference.DiffType.TYPE_MISMATCH));
            }
        }
    }
}
