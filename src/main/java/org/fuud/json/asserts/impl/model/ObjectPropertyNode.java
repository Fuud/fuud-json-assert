package org.fuud.json.asserts.impl.model;

import org.fuud.json.asserts.impl.diff.Difference;
import org.fuud.json.asserts.impl.diff.JsonComparator;
import org.fuud.json.asserts.impl.parse.CharAndPosition;
import org.fuud.json.asserts.impl.parse.Context;
import org.fuud.json.asserts.impl.parse.JsonParseException;
import org.fuud.json.asserts.impl.parse.Source;
import org.fuud.json.asserts.impl.util.Utils;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

public class ObjectPropertyNode extends Node<ObjectPropertyNode> {
    private final String name;
    private final Node<?> value;
    private final List<CommentNode> commentNodes;

    public ObjectPropertyNode(String name, Node<?> value) {
        this(name, value, new ObjectPropertyNodeComparator(), emptyList());
    }

    public ObjectPropertyNode(String name, Node<?> value, JsonComparator<ObjectPropertyNode> comparator, List<CommentNode> commentNodes) {
        super(comparator);
        this.name = Objects.requireNonNull(name);
        this.value = Objects.requireNonNull(value);
        this.commentNodes = commentNodes;
    }

    public String getName() {
        return name;
    }

    public Node<?> getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "" +
                commentNodes.stream().map(comment -> comment + "\n").collect(Collectors.joining()) +
                name +
                ":" +
                Utils.addIdentExceptFirstLine.apply(
                        value.toString()
                );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ObjectPropertyNode)) return false;

        ObjectPropertyNode that = (ObjectPropertyNode) o;

        if (!name.equals(that.name)) return false;
        return value.equals(that.value);

    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + value.hashCode();
        return result;
    }

    public static ObjectPropertyNode parse(Source source) throws IOException {
        return parse(source, new Context(), emptyList());
    }

    public static ObjectPropertyNode parse(Source source, Context context, List<CommentNode> commentNodes) throws IOException {
        final StringNode nameNode = StringNode.parse(source);
        final CharAndPosition delimiter = source.readNextNonSpaceChar();
        if (delimiter.getCharacter() != ':') {
            throw new JsonParseException(":", delimiter);
        }

        final ValueNode valueNode = ValueNode.parse(source, context);
        final List<String> args = commentNodes.stream().flatMap(commentNode -> commentNode.getAnnotations().stream()).collect(Collectors.toList());
        return new ObjectPropertyNode(
                nameNode.getValue(),
                valueNode,
                context.getObjectPropertyNodeComparatorCreator().create(args),
                commentNodes);
    }

    public static class ObjectPropertyNodeComparator implements JsonComparator<ObjectPropertyNode> {

        @Override
        public List<Difference> compare(ObjectPropertyNode leftNode, Node rightNode) {
            if (rightNode instanceof ObjectPropertyNode) {
                ObjectPropertyNode right = (ObjectPropertyNode) rightNode;
                if (right.getName().equals(leftNode.getName())) {
                    final List<Difference> valuesDiff = leftNode.getValue().compare(right.getValue());
                    if (valuesDiff.isEmpty()) {
                        return emptyList();
                    } else {
                        return valuesDiff.
                                stream().
                                map(Difference.addParentPath(leftNode.getName())).
                                collect(Collectors.toList());
                    }
                } else {
                    return singletonList(new Difference(emptyList(), Difference.DiffType.NOT_EQUALS));
                }
            } else {
                return singletonList(new Difference(emptyList(), Difference.DiffType.TYPE_MISMATCH));
            }
        }
    }
}
