package org.fuud.json.asserts.impl.model.compare

import org.fuud.json.asserts.impl.diff.Difference
import org.fuud.json.asserts.impl.model.*
import org.fuud.json.asserts.impl.parse.Context
import org.fuud.json.asserts.impl.parse.Source
import spock.lang.Specification
import spock.lang.Unroll

import static org.fuud.json.asserts.impl.diff.Difference.DiffType.MISSING
import static org.fuud.json.asserts.impl.diff.Difference.DiffType.NOT_EQUALS
import static org.fuud.json.asserts.impl.diff.Difference.DiffType.NOT_EXPECTED
import static org.fuud.json.asserts.impl.diff.Difference.DiffType.TYPE_MISMATCH

class ObjectNodeCompareTest extends Specification {

    @Unroll
    def "type mismatch"(Node right) {
        when:
            ObjectNode left = new ObjectNode([])
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
    }

    @Unroll
    def "not equals"(String leftJson, String rightJson, List<Difference> expectedDiff) {
        setup:
            ObjectNode left = ObjectNode.parse(new Source(leftJson), new Context())
            ObjectNode right = ObjectNode.parse(new Source(rightJson), new Context())
        when:
            List<Difference> differences = left.compare(right)
        then:
            differences == expectedDiff
        where:
            leftJson    | rightJson   | expectedDiff
            '{}'        | '{"a":"b"}' | [new Difference(["a"], NOT_EXPECTED)]
            '{"a":"b"}' | '{}'        | [new Difference(["a"], MISSING)]
            '{"a":"b"}' | '{"a":"c"}' | [new Difference(["a"], NOT_EQUALS)]
            '{"a":"b"}' | '{"a":{}}'  | [new Difference(["a"], TYPE_MISMATCH)]
    }

    @Unroll
    def "equals"() {
        when:
            ObjectNode left = new ObjectNode([new ObjectPropertyNode("a", new NullNode())])
            ObjectNode right = new ObjectNode([new ObjectPropertyNode("a", new NullNode())])
            List<Difference> differences = left.compare(right)

        then:
            differences == []
    }
}
