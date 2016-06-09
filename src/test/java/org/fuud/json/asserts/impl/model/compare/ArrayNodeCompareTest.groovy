package org.fuud.json.asserts.impl.model.compare

import org.fuud.json.asserts.impl.diff.Difference
import org.fuud.json.asserts.impl.model.*
import org.fuud.json.asserts.impl.parse.Context
import org.fuud.json.asserts.impl.parse.Source
import spock.lang.Specification
import spock.lang.Unroll

import static org.fuud.json.asserts.impl.diff.Difference.DiffType.*

class ArrayNodeCompareTest extends Specification {

    @Unroll
    def "type mismatch"(Node right) {
        when:
            ArrayNode left = new ArrayNode([])
            List<Difference> differences = left.compare(right)

        then:
            differences == [new Difference([], TYPE_MISMATCH)]

        where:
            right                                                                         | _
            new BooleanNode(true)                                                         | _
            new NullNode()                                                                | _
            new NumberNode(123.5)                                                         | _
            new StringNode("abc")                                                         | _
            new ObjectPropertyNode("this comparison should never happen", new NullNode()) | _
            new ObjectNode([])                                                            | _
    }

    @Unroll
    def "not equals"(String leftJson, String rightJson, List<Difference> expectedDiff) {
        setup:
            ArrayNode left = ArrayNode.parse(new Source(leftJson), new Context())
            ArrayNode right = ArrayNode.parse(new Source(rightJson), new Context())
        when:
            List<Difference> differences = left.compare(right)
        then:
            differences == expectedDiff
        where:
            leftJson | rightJson | expectedDiff
            '[]'     | '["a"]'   | [new Difference(["0"], NOT_EXPECTED)]
            '["a"]'  | '[]'      | [new Difference(["0"], MISSING)]
            '["a"]'  | '["b"]'   | [new Difference(["0"], NOT_EQUALS)]
            '["a"]'  | '[{}]'    | [new Difference(["0"], TYPE_MISMATCH)]
    }

    @Unroll
    def "equals"() {
        when:
            ArrayNode left = new ArrayNode([new NullNode()])
            ArrayNode right = new ArrayNode([new NullNode()])
            List<Difference> differences = left.compare(right)

        then:
            differences == []
    }
}
