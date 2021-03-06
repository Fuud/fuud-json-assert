package org.fuud.json.asserts.impl.model;

import org.fuud.json.asserts.impl.diff.Difference;
import org.fuud.json.asserts.impl.diff.JsonComparator;
import org.fuud.json.asserts.impl.parse.CharAndPosition;
import org.fuud.json.asserts.impl.parse.Context;
import org.fuud.json.asserts.impl.parse.JsonParseException;
import org.fuud.json.asserts.impl.parse.Source;
import org.fuud.json.asserts.impl.util.Utils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

public class ObjectNode extends ValueNode<ObjectNode> {
    private final Map<String, ObjectPropertyNode> children;
    private final List<CommentNode> commentNodes;

    public ObjectNode(List<ObjectPropertyNode> children) {
        this(children, new ObjectNodeComparator(), new ArrayList<>());

    }

    public ObjectNode(List<ObjectPropertyNode> children, JsonComparator<ObjectNode> comparator, List<CommentNode> commentNodes) {
        super(comparator);
        this.commentNodes = commentNodes;
        this.children = new HashMap<>();
        for (ObjectPropertyNode child : children) {
            if (this.children.containsKey(child.getName())) {
                throw new IllegalStateException("Property with key " + child.getName() + " already exists");
            }
            this.children.put(child.getName(), child);
        }
    }

    public List<ObjectPropertyNode> getChildren() {
        return new ArrayList<>(children.values());
    }

    public ObjectPropertyNode getChild(String name) {
        return children.get(name);
    }

    @Override
    public String toString() {
        return "" +
                commentNodes.stream().map(comment -> comment + "\n").collect(Collectors.joining()) +
                "{\n" +
                children.values().stream().map(Object::toString).map(Utils.addIdent).collect(Collectors.joining(",\n")) +
                "\n}";
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
        return parse(source, new Context(), emptyList());
    }

    public static ObjectNode parse(Source source, Context context, List<CommentNode> commentNodes) throws IOException {
        final List<String> args = commentNodes.stream().flatMap(commentNode -> commentNode.getAnnotations().stream()).collect(Collectors.toList());
        final JsonComparator<ObjectNode> comparator = context.getObjectNodeComparatorCreator().create(args);

        final CharAndPosition startChar = source.readNextNonSpaceChar();
        if (startChar.getCharacter() != '{') {
            throw new JsonParseException("{", startChar);
        }

        final CharAndPosition mayBeEnd = source.lookupForNextNonSpaceChar();
        if (mayBeEnd.getCharacter() == '}') {
            source.readNextNonSpaceChar(); // skip up to '}'
            return new ObjectNode(new ArrayList<>(), comparator, commentNodes);
        }

        List<ObjectPropertyNode> propertyNodes = new ArrayList<>();
        while (true) {
            final List<CommentNode> propertyComments = parseCommentNodes(source);
            final ObjectPropertyNode propertyNode = ObjectPropertyNode.parse(source, context, propertyComments);
            propertyNodes.add(propertyNode);

            final CharAndPosition delimiterOrEndChar = source.readNextNonSpaceChar();

            if (delimiterOrEndChar.getCharacter() == '}') {
                break;
            }
            if (delimiterOrEndChar.getCharacter() != ',') {
                throw new JsonParseException("},", delimiterOrEndChar);
            }
        }

        return new ObjectNode(propertyNodes,
                comparator,
                commentNodes);
    }

    public static boolean canStartWith(char firstChar) {
        return firstChar == '{';
    }

    public static class ObjectNodeComparator implements JsonComparator<ObjectNode> {
        @Override
        public List<Difference> compare(ObjectNode leftNode, Node rightNode) {
            if (rightNode instanceof ObjectNode) {
                ObjectNode right = (ObjectNode) rightNode;

                final Set<String> leftPropertyNames = leftNode.children.keySet();
                final Set<String> rightPropertyNames = right.children.keySet();

                final Set<String> missingPropertiesNames = diff(leftPropertyNames, rightPropertyNames);
                final Set<String> notExpectedPropertiesNames = diff(rightPropertyNames, leftPropertyNames);
                final Set<String> toCheckEqualityPropertyNames = diff(leftPropertyNames, missingPropertiesNames);

                List<Difference> result = new ArrayList<>();
                result.addAll(
                        missingPropertiesNames.
                                stream().
                                map(name -> new Difference(singletonList(name), Difference.DiffType.MISSING)).
                                collect(Collectors.toList()));

                result.addAll(
                        notExpectedPropertiesNames.
                                stream().
                                map(name -> new Difference(singletonList(name), Difference.DiffType.NOT_EXPECTED)).
                                collect(Collectors.toList()));

                result.addAll(
                        leftNode.children.values().
                                stream().
                                filter(objectPropertyNode -> toCheckEqualityPropertyNames.contains(objectPropertyNode.getName())).
                                flatMap(objectPropertyNode -> objectPropertyNode.compare(right.children.get(objectPropertyNode.getName())).stream()).
                                collect(Collectors.toList()));
                return result;

            } else {
                return singletonList(new Difference(emptyList(), Difference.DiffType.TYPE_MISMATCH));
            }
        }

        private static Set<String> diff(Set<String> set1, Set<String> set2) {
            Set<String> result = new HashSet<>(set1);
            result.removeAll(set2);
            return result;
        }
    }

}
