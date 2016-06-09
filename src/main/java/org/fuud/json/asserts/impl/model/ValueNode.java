package org.fuud.json.asserts.impl.model;

import org.fuud.json.asserts.impl.diff.JsonComparator;
import org.fuud.json.asserts.impl.parse.CharAndPosition;
import org.fuud.json.asserts.impl.parse.Context;
import org.fuud.json.asserts.impl.parse.JsonParseException;
import org.fuud.json.asserts.impl.parse.Source;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class ValueNode<TThis extends ValueNode<TThis>> extends Node<TThis> {
    public ValueNode(JsonComparator<TThis> comparator) {
        super(comparator);
    }

    static ValueNode<?> parse(Source source, Context context) throws IOException {
        List<CommentNode> commentNodes = parseCommentNodes(source);

        final CharAndPosition firstCharAndPos = source.lookupForNextNonSpaceChar();
        final char firstChar = firstCharAndPos.getCharacter();

        if (StringNode.canStartWith(firstChar)) {
            return StringNode.parse(source, context, commentNodes);
        }

        if (NumberNode.canStartWith(firstChar)) {
            return NumberNode.parse(source, context, commentNodes);
        }

        if (BooleanNode.canStartWith(firstChar)) {
            return BooleanNode.parse(source, context, commentNodes);
        }

        if (NullNode.canStartWith(firstChar)) {
            return NullNode.parse(source, context, commentNodes);
        }

        if (ObjectNode.canStartWith(firstChar)) {
            return ObjectNode.parse(source, context, commentNodes);
        }

        if (ArrayNode.canStartWith(firstChar)) {
            return ArrayNode.parse(source, context, commentNodes);
        }

        throw new JsonParseException(firstCharAndPos);
    }

}
