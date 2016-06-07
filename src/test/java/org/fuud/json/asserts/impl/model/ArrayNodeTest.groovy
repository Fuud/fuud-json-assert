package org.fuud.json.asserts.impl.model

import org.fuud.json.asserts.impl.parse.Source
import spock.lang.Specification


class ArrayNodeTest extends Specification {
    def "test valid array #json"(String json, ArrayNode expected) {
        when:
            ArrayNode parsed = ArrayNode.parse(new Source(json))
        then:
            parsed == expected
        where:
            json                      | expected
            '[]'                      | new ArrayNode([])
            '["a"]'                   | new ArrayNode([new StringNode("a")])
            '[{"a":"b"}, ["c", "d"]]' | new ArrayNode([new ObjectNode([new ObjectPropertyNode("a", new StringNode("b"))]), new ArrayNode([new StringNode("c"), new StringNode("d")])])

    }
}
