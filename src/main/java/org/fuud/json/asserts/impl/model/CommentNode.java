package org.fuud.json.asserts.impl.model;

import org.fuud.json.asserts.impl.parse.*;

import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class CommentNode {
    private final String comment;
    private final boolean isMultiline;
    private final List<String> annotations;

    public CommentNode(String comment, boolean isMultiline, List<String> annotations) {
        this.comment = Objects.requireNonNull(comment);
        this.isMultiline = isMultiline;
        this.annotations = annotations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CommentNode)) return false;

        CommentNode that = (CommentNode) o;

        if (isMultiline != that.isMultiline) return false;
        return comment.equals(that.comment);

    }

    @Override
    public int hashCode() {
        int result = comment.hashCode();
        result = 31 * result + (isMultiline ? 1 : 0);
        return result;
    }

    public String toString() {
        if (isMultiline) {
            return "/*" + comment + "*/";
        } else {
            return "//" + comment;
        }
    }

    public String getComment() {
        return comment;
    }

    public boolean isMultiline() {
        return isMultiline;
    }

    public List<String> getAnnotations() {
        return annotations;
    }

    public static boolean canStartWith(char firstChar) {
        return firstChar == '/';
    }

    public static CommentNode parse(Source source) throws IOException {
        final CharAndPosition firstCharAndPosition = source.readNextNonSpaceChar();
        final CharAndPosition secondCharAndPosition = source.readNextNonSpaceChar();

        if (firstCharAndPosition.getCharacter() == '/') {
            if (secondCharAndPosition.getCharacter() == '/') {
                return parseSingleLineComment(source);
            } else if (secondCharAndPosition.getCharacter() == '*') {
                return parseMultiLineComment(source);
            } else {
                throw new JsonParseException("*/", secondCharAndPosition);
            }
        }
        throw new JsonParseException("/", firstCharAndPosition);
    }

    private static CommentNode parseMultiLineComment(Source source) throws EOFException {
        final TextAndNextChar comment = source.readUntil("*/");
        source.readNextChar(); //skip *
        source.readNextChar(); //skip /
        return new CommentNode(comment.getText(), true, extractAnnotations(source.subSource(comment.getStartPosition(), comment.getEndPositionExclusive())));
    }

    private static CommentNode parseSingleLineComment(Source source) throws EOFException {
        final TextAndNextChar comment = source.readUntilEofOr(character -> character == '\n');
        if (comment.getMayBeNextChar().isPresent()){
            source.readNextChar();
        }
        return new CommentNode(comment.getText(), false, extractAnnotations(source.subSource(comment.getStartPosition(), comment.getEndPositionExclusive())));
    }

    private static List<String> extractAnnotations(Source source) throws EOFException {
        final ArrayList<String> result = new ArrayList<>();

        while (true) {
            final TextAndNextChar beforeAnnotation = source.readUntilEofOr('@');
            if (!beforeAnnotation.getMayBeNextChar().isPresent()) { //annotation exists
                break;
            }
            String annotation = "";
            TextAndNextChar annotationText = source.readUntilEofOr(character -> Character.isWhitespace(character) || isOpenBracket(character));
            annotation += annotationText.getText();
            final Optional<CharAndPosition> mayBeNextChar = annotationText.getMayBeNextChar();
            if (mayBeNextChar.isPresent()) {
                source.readNextChar(); // it was read
                char nextChar = mayBeNextChar.get().getCharacter();
                if (isOpenBracket(nextChar)) {
                    annotation += nextChar;
                    char expectedClosedBracket = closedBracketFor(nextChar);
                    annotation += source.readUntil(expectedClosedBracket).getText();
                    source.readNextChar(); // close bracket
                    annotation += expectedClosedBracket;
                }
            }
            result.add(annotation);
        }

        return result;
    }

    private static char closedBracketFor(char openBracketChar) {
        if (openBracketChar == '(') {
            return ')';
        }
        if (openBracketChar == '[') {
            return ']';
        }
        if (openBracketChar == '{') {
            return '}';
        }
        throw new IllegalArgumentException("unexpected char " + openBracketChar);
    }


    private static boolean isOpenBracket(Character character) {
        return "{[(".contains("" + character);
    }
}
