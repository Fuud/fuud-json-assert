package org.fuud.json.asserts.impl.model;

import org.fuud.json.asserts.impl.parse.CharAndPosition;
import org.fuud.json.asserts.impl.parse.JsonParseException;
import org.fuud.json.asserts.impl.parse.Source;
import org.fuud.json.asserts.impl.util.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ObjectNode implements ValueNode, Node {
    private final List<ObjectPropertyNode> children;

    public ObjectNode(List<ObjectPropertyNode> children) {
        this.children = children;
    }

    public List<ObjectPropertyNode> getChildren() {
        return children;
    }

    @Override
    public String toString() {
        return "{" +
                children.stream().map(Object::toString).map(Utils.addIdent).collect(Collectors.joining(",\n")) +
                "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ObjectNode)) return false;

        ObjectNode that = (ObjectNode) o;

        return children.equals(that.children);

    }

    @Override
    public int hashCode() {
        return children.hashCode();
    }

    public static ObjectNode parse(Source source) throws IOException {
        final CharAndPosition startChar = source.readNextNonSpaceChar();
        if (startChar.getCharacter() != '{') {
            throw new JsonParseException("{", startChar);
        }

        final CharAndPosition mayBeEnd = source.lookupForNextNonSpaceChar();
        if (mayBeEnd.getCharacter() == '}') {
            source.readNextNonSpaceChar(); // skip up to '}'
            return new ObjectNode(new ArrayList<>());
        }

        List<ObjectPropertyNode> propertyNodes = new ArrayList<>();
        while (true) {
            final ObjectPropertyNode propertyNode = ObjectPropertyNode.parse(source);
            propertyNodes.add(propertyNode);

            final CharAndPosition delimiterOrEndChar = source.readNextNonSpaceChar();

            if (delimiterOrEndChar.getCharacter() == '}') {
                break;
            }
            if (delimiterOrEndChar.getCharacter() != ',') {
                throw new JsonParseException("},", delimiterOrEndChar);
            }
        }

        return new ObjectNode(propertyNodes);
    }

    public static boolean canStartWith(char firstChar) {
        return firstChar == '{';
    }
}
