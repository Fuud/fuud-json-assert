package org.fuud.json.asserts.impl.model;

import org.fuud.json.asserts.impl.diff.JsonComparator;
import org.fuud.json.asserts.impl.parse.CharAndPosition;
import org.fuud.json.asserts.impl.parse.Context;
import org.fuud.json.asserts.impl.parse.JsonParseException;
import org.fuud.json.asserts.impl.parse.Source;

import java.io.IOException;

public abstract class ValueNode<TThis extends ValueNode<TThis>> extends Node<TThis> {
    public ValueNode(JsonComparator<TThis> comparator) {
        super(comparator);
    }

    static ValueNode<?> parse(Source source, Context context) throws IOException {
        final CharAndPosition firstCharAndPos = source.lookupForNextNonSpaceChar();
        final char firstChar = firstCharAndPos.getCharacter();

        if (StringNode.canStartWith(firstChar)) {
            return StringNode.parse(source, context);
        }

        if (NumberNode.canStartWith(firstChar)) {
            return NumberNode.parse(source, context);
        }

        if (BooleanNode.canStartWith(firstChar)) {
            return BooleanNode.parse(source, context);
        }

        if (NullNode.canStartWith(firstChar)) {
            return NullNode.parse(source, context);
        }

        if (ObjectNode.canStartWith(firstChar)) {
            return ObjectNode.parse(source, context);
        }

        if (ArrayNode.canStartWith(firstChar)) {
            return ArrayNode.parse(source, context);
        }

        throw new JsonParseException(firstCharAndPos);
    }
}
