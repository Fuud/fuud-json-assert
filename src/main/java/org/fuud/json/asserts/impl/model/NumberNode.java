package org.fuud.json.asserts.impl.model;

import org.fuud.json.asserts.impl.diff.Difference;
import org.fuud.json.asserts.impl.diff.JsonComparator;
import org.fuud.json.asserts.impl.parse.Context;
import org.fuud.json.asserts.impl.parse.JsonParseException;
import org.fuud.json.asserts.impl.parse.Source;
import org.fuud.json.asserts.impl.parse.TextAndNextChar;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

public class NumberNode extends ValueNode<NumberNode> {
    private final BigDecimal value;
    private final List<CommentNode> commentNodes;

    public NumberNode(BigDecimal value) {
        this(value, new NumberNodeComparator(), new ArrayList<>());
    }

    public NumberNode(BigDecimal value, JsonComparator<NumberNode> comparator, List<CommentNode> commentNodes) {
        super(comparator);
        this.value = Objects.requireNonNull(value);
        this.commentNodes = commentNodes;
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
        if (!(o instanceof NumberNode)) return false;

        NumberNode that = (NumberNode) o;

        return value.compareTo(that.value) == 0;

    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    public static NumberNode parse(Source source) throws IOException {
        return parse(source, new Context(), emptyList());
    }

    public static NumberNode parse(Source source, Context context, List<CommentNode> commentNodes) throws IOException {
        source.skipWhitespaces();
        final int startPosition = source.getPosition();
        TextAndNextChar numberAsString = source.readUntilEofOr(
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
            value = new BigDecimal(numberAsString.getText());
        } catch (NumberFormatException e) {
            throw new JsonParseException("Can not parse number value " + numberAsString + " starting from position " + startPosition, e);
        }
        final List<String> args = commentNodes.stream().flatMap(commentNode -> commentNode.getAnnotations().stream()).collect(Collectors.toList());
        return new NumberNode(value,
                context.getNumberNodeComparatorCreator().create(args),
                commentNodes);
    }

    public static boolean canStartWith(char firstChar) {
        return Character.isDigit(firstChar) || firstChar == '+' || firstChar == '-';
    }

    public static class NumberNodeComparator implements JsonComparator<NumberNode> {
        @Override
        public List<Difference> compare(NumberNode leftNode, Node rightNode) {
            if (rightNode instanceof NumberNode) {
                NumberNode right = (NumberNode) rightNode;
                if (right.value.compareTo(leftNode.value) == 0) {
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
