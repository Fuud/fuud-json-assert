package org.fuud.json.asserts.impl.model;

import org.fuud.json.asserts.impl.diff.Difference;
import org.fuud.json.asserts.impl.diff.JsonComparator;
import org.fuud.json.asserts.impl.parse.CharAndPosition;
import org.fuud.json.asserts.impl.parse.Context;
import org.fuud.json.asserts.impl.parse.Source;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

public class NullNode extends ValueNode<NullNode> {
    private final List<CommentNode> commentNodes;

    public NullNode() {
        this(new NullNodeComparator(), new ArrayList<>());
    }

    public NullNode(JsonComparator<NullNode> comparator, List<CommentNode> commentNodes) {
        super(comparator);
        this.commentNodes = commentNodes;
    }

    @Override
    public String toString() {
        return "" +
                commentNodes.stream().map(comment -> comment + "\n").collect(Collectors.joining()) +
                null;
    }

    public static NullNode parse(Source source) throws IOException {
        return parse(source, new Context(), emptyList());
    }

    public static NullNode parse(Source source, Context context, List<CommentNode> commentNodes) throws IOException {

        final CharAndPosition firstCharAndPosition = source.readNextNonSpaceChar();
        final char char1 = firstCharAndPosition.getCharacter();
        final char char2 = source.readNextChar().getCharacter();
        final char char3 = source.readNextChar().getCharacter();
        final char char4 = source.readNextChar().getCharacter();

        if ("null".equals("" + char1 + char2 + char3 + char4)) {
            final List<String> args = commentNodes.stream().flatMap(commentNode -> commentNode.getAnnotations().stream()).collect(Collectors.toList());
            return new NullNode(context.getNullNodeComparatorCreator().create(args), commentNodes);
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

    public static class NullNodeComparator implements JsonComparator<NullNode> {
        @Override
        public List<Difference> compare(NullNode leftNode, Node rightNode) {
            if (rightNode instanceof NullNode) {
                return emptyList();
            } else {
                return singletonList(new Difference(emptyList(), Difference.DiffType.TYPE_MISMATCH));
            }
        }
    }
}
