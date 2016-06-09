package org.fuud.json.asserts.impl.model;

import org.fuud.json.asserts.impl.diff.Difference;
import org.fuud.json.asserts.impl.diff.JsonComparator;
import org.fuud.json.asserts.impl.parse.CharAndPosition;
import org.fuud.json.asserts.impl.parse.Context;
import org.fuud.json.asserts.impl.parse.JsonParseException;
import org.fuud.json.asserts.impl.parse.Source;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

public class BooleanNode extends ValueNode<BooleanNode> {
    private final boolean value;
    private final List<CommentNode> commentNodes;

    public BooleanNode(boolean value) {
        this(value, new BooleanNodeComparator(), new ArrayList<>());
    }

    public BooleanNode(boolean value, JsonComparator<BooleanNode> comparator, List<CommentNode> commentNodes) {
        super(comparator);
        this.value = value;
        this.commentNodes = commentNodes;
    }

    public boolean isValue() {
        return value;
    }

    @Override
    public String toString() {
        return "" +
                commentNodes.stream().map(comment -> comment + "\n").collect(Collectors.joining()) +
                value;
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
        return parse(source, new Context(), emptyList());
    }

    public static BooleanNode parse(Source source, Context context, List<CommentNode> commentNodes) throws IOException {
        final CharAndPosition firstCharAndPos = source.readNextNonSpaceChar();
        final char char1 = firstCharAndPos.getCharacter();
        final char char2 = source.readNextChar().getCharacter();
        final char char3 = source.readNextChar().getCharacter();
        final char char4 = source.readNextChar().getCharacter();

        final List<String> args = commentNodes.stream().flatMap(commentNode -> commentNode.getAnnotations().stream()).collect(Collectors.toList());
        if ("true".equals("" + char1 + char2 + char3 + char4)) {
            return new BooleanNode(true, context.getBooleanNodeComparatorCreator().create(args), commentNodes);
        }

        final char char5 = source.readNextChar().getCharacter();
        if ("false".equals("" + char1 + char2 + char3 + char4 + char5)) {
            return new BooleanNode(false, context.getBooleanNodeComparatorCreator().create(args), commentNodes);
        }

        throw new JsonParseException("Invalid boolean value " + char1 + char2 + char3 + char4 + char5 + " at position " + firstCharAndPos.getPosition());
    }

    public static boolean canStartWith(char firstChar) {
        return firstChar == 't' || firstChar == 'f';
    }

    public static class BooleanNodeComparator implements JsonComparator<BooleanNode> {
        @Override
        public List<Difference> compare(BooleanNode leftNode, Node rightNode) {
            if (rightNode instanceof BooleanNode) {
                BooleanNode right = (BooleanNode) rightNode;
                if (right.value == leftNode.value) {
                    return emptyList();
                } else {
                    return singletonList(new Difference(emptyList(), Difference.DiffType.NOT_EQUALS));
                }
            } else {
                return singletonList(new Difference(emptyList(), Difference.DiffType.TYPE_MISMATCH));
            }
        }
    }
}
