package org.fuud.json.asserts.impl.model

import org.fuud.json.asserts.impl.parse.Source
import spock.lang.Specification
import spock.lang.Unroll


class ObjectNodeTest extends Specification {
    @Unroll
    def "valid objects"(String json, ObjectNode expected) {
        when:
            ObjectNode parsed = ObjectNode.parse(new Source(json))
        then:
            parsed == expected
        where:
            json                 | expected
            '{}'                 | new ObjectNode([])
            '{"a":"b"}'          | new ObjectNode([new ObjectPropertyNode("a", new StringNode("b"))])
            '{"a":"b", "c":123}' | new ObjectNode([new ObjectPropertyNode("a", new StringNode("b")), new ObjectPropertyNode("c", new NumberNode(123))])
    }
}
