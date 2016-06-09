package org.fuud.json.asserts.impl.model

import org.fuud.json.asserts.impl.parse.Source
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class ObjectPropertyNodeTest extends Specification {
    def "test valid object properties #json"(String json, ObjectPropertyNode expected) {
        when:
            ObjectPropertyNode parsed = ObjectPropertyNode.parse(new Source(json))
        then:
            parsed == expected
        where:
            json             | expected
            '"a":"b"'        | new ObjectPropertyNode('a', new StringNode('b'))
            '"a":123'        | new ObjectPropertyNode('a', new NumberNode(123))
            '"a":123.25'     | new ObjectPropertyNode('a', new NumberNode(123.25))
            '"a":-123.25'    | new ObjectPropertyNode('a', new NumberNode(-123.25))
            '"a":true'       | new ObjectPropertyNode('a', new BooleanNode(true))
            '"a":false'      | new ObjectPropertyNode('a', new BooleanNode(false))
            '"a":null'       | new ObjectPropertyNode('a', new NullNode())
            '"a":{"b": "c"}' | new ObjectPropertyNode('a', new ObjectNode([new ObjectPropertyNode("b", new StringNode("c"))]))
            '"a":["b", "c"]' | new ObjectPropertyNode('a', new ArrayNode([new StringNode("b"), new StringNode("c")]))
            '"a":[{}, "c"]'  | new ObjectPropertyNode('a', new ArrayNode([new ObjectNode([]), new StringNode("c")]))

    }
}
