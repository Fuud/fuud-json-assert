package org.fuud.json.asserts.impl.model;

import org.fuud.json.asserts.impl.diff.Difference;
import org.fuud.json.asserts.impl.diff.JsonComparator;
import org.fuud.json.asserts.impl.parse.CharAndPosition;
import org.fuud.json.asserts.impl.parse.Source;
import org.jetbrains.annotations.NotNull;

import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Node<TThis extends Node<TThis>> {
    private JsonComparator<TThis> comparator;

    public Node(JsonComparator<TThis> comparator) {
        this.comparator = comparator;
    }

    public JsonComparator getComparator() {
        return comparator;
    }

    public void setComparator(JsonComparator<TThis> comparator) {
        this.comparator = comparator;
    }

    @SuppressWarnings("unchecked")
    public final List<Difference> compare(Node right) {
        return comparator.compare((TThis) this, right);
    }

    @NotNull
    static List<CommentNode> parseCommentNodes(Source source) throws IOException {
        List<CommentNode> commentNodes = new ArrayList<>();

        while (true) {
            final CharAndPosition firstCharAndPos = source.lookupForNextNonSpaceChar();
            final char firstChar = firstCharAndPos.getCharacter();
            if (CommentNode.canStartWith(firstChar)){
                commentNodes.add(CommentNode.parse(source));
            }else {
                break;
            }
        }
        return commentNodes;
    }
}
