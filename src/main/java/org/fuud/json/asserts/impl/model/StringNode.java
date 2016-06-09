package org.fuud.json.asserts.impl.model;

import org.fuud.json.asserts.impl.diff.Difference;
import org.fuud.json.asserts.impl.diff.JsonComparator;
import org.fuud.json.asserts.impl.parse.CharAndPosition;
import org.fuud.json.asserts.impl.parse.Context;
import org.fuud.json.asserts.impl.parse.JsonParseException;
import org.fuud.json.asserts.impl.parse.Source;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

public class StringNode extends ValueNode<StringNode> {
    private final String value;
    private final List<CommentNode> commentNodes;

    public StringNode(String value) {
        this(value, new StringNodeComparator(), emptyList());
    }

    public StringNode(String value, JsonComparator<StringNode> comparator, List<CommentNode> commentNodes) {
        super(comparator);
        this.value = Objects.requireNonNull(value);
        this.commentNodes = commentNodes;
    }

    public String getValue() {
        return value;
    }

    public static StringNode parse(Source source, Context context, List<CommentNode> commentNodes) throws IOException {
        final CharAndPosition startToken = source.readNextNonSpaceChar();
        if (startToken.getCharacter() != '"') {
            throw new JsonParseException("\"", startToken);
        }
        final List<String> args = commentNodes.stream().flatMap(commentNode -> commentNode.getAnnotations().stream()).collect(Collectors.toList());
        return new StringNode(
                readAndUnescapeChars(source),
                context.getStringNodeComparatorCreator().create(args),
                commentNodes
        );
    }

    public static StringNode parse(Source source) throws IOException {
        return parse(source, new Context(), emptyList());
    }

    private static String readAndUnescapeChars(Source source) throws IOException {
        String result = "";
        while (true) {
            CharAndPosition characterAndPos = source.readNextChar();
            final char character = characterAndPos.getCharacter();

            if (character == '"') {
                return result;
            }

            if (Character.isISOControl(character)) {
                throw new JsonParseException(characterAndPos);
            }
            if (character == '\\') {
                CharAndPosition escapedCharPos = source.readNextChar();
                final char escapedChar = escapedCharPos.getCharacter();
                if (escapedChar == 'u') {
                    final String hexUnicodeChar = source.readChars(4);
                    try {
                        final char unicodeChar = (char) Integer.parseInt(hexUnicodeChar, 16);
                        result += unicodeChar;
                    } catch (NumberFormatException e) {
                        throw new JsonParseException("invalid unicode escape sequence: " + hexUnicodeChar + " starting at position " + (escapedCharPos.getPosition() + 1));
                    }
                } else if (escapedChar == '"' || escapedChar == '\\' || escapedChar == '/') {
                    result += escapedChar;
                } else if (escapedChar == 'b') {
                    result += '\b';
                } else if (escapedChar == 'f') {
                    result += '\f';
                } else if (escapedChar == 'n') {
                    result += '\n';
                } else if (escapedChar == 'r') {
                    result += '\r';
                } else if (escapedChar == 't') {
                    result += '\t';
                } else {
                    throw new JsonParseException("ubfnrt\"", escapedCharPos);
                }
            } else {
                result += character;
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StringNode)) return false;

        StringNode that = (StringNode) o;

        return value.equals(that.value);

    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return "" +
                commentNodes.stream().map(comment -> comment + "\n").collect(Collectors.joining()) +
                '"' + value + '"';
    }

    public static boolean canStartWith(char firstChar) {
        return firstChar == '"';
    }

    public static class StringNodeComparator implements JsonComparator<StringNode> {
        @Override
        public List<Difference> compare(StringNode leftNode, Node rightNode) {
            if (rightNode instanceof StringNode) {
                StringNode right = (StringNode) rightNode;
                if (right.getValue().equals(leftNode.getValue())) {
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
