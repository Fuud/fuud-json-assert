package org.fuud.json.asserts.impl.model;

import org.fuud.json.asserts.impl.diff.JsonComparator;
import org.fuud.json.asserts.impl.parse.CharAndPosition;
import org.fuud.json.asserts.impl.parse.JsonParseException;
import org.fuud.json.asserts.impl.parse.Source;

import java.io.IOException;

public abstract class ValueNode<TThis extends ValueNode<TThis>> extends Node<TThis> {
    public ValueNode(JsonComparator<TThis> comparator) {
        super(comparator);
    }

    static ValueNode<?> parse(Source source) throws IOException {
        final CharAndPosition firstCharAndPos = source.lookupForNextNonSpaceChar();
        final char firstChar = firstCharAndPos.getCharacter();

        if (StringNode.canStartWith(firstChar)) {
            return StringNode.parse(source);
        }

        if (NumberNode.canStartWith(firstChar)) {
            return NumberNode.parse(source);
        }

        if (BooleanNode.canStartWith(firstChar)) {
            return BooleanNode.parse(source);
        }

        if (NullNode.canStartWith(firstChar)) {
            return NullNode.parse(source);
        }

        if (ObjectNode.canStartWith(firstChar)) {
            return ObjectNode.parse(source);
        }

        if (ArrayNode.canStartWith(firstChar)) {
            return ArrayNode.parse(source);
        }

        throw new JsonParseException(firstCharAndPos);
    }
}
