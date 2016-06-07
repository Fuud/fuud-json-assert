package org.fuud.json.asserts.impl.model.compare

import org.fuud.json.asserts.impl.diff.Difference
import org.fuud.json.asserts.impl.model.*
import spock.lang.Specification
import spock.lang.Unroll

class NumberNodeCompareTest extends Specification {

    @Unroll
    def "type mismatch"(Node right) {
        when:
            NumberNode left = new NumberNode(123.5)
            List<Difference> differences = left.compare(right)

        then:
            differences == [new Difference([], Difference.DiffType.TYPE_MISMATCH)]

        where:
            right                                                                         | _
            new BooleanNode(true)                                                         | _
            new NullNode()                                                                | _
            new StringNode("123.5")                                                       | _
            new ObjectNode([])                                                            | _
            new ObjectPropertyNode("this comparison should never happen", new NullNode()) | _
    }

    @Unroll
    def "not equals"() {
        when:
            NumberNode left = new NumberNode(123.5)
            NumberNode right = new NumberNode(666)
            List<Difference> differences = left.compare(right)

        then:
            differences == [new Difference([], Difference.DiffType.NOT_EQUALS)]
    }

    @Unroll
    def "equals"() {
        when:
            NumberNode left = new NumberNode(123.5)
            NumberNode right = new NumberNode(123.5)
            List<Difference> differences = left.compare(right)

        then:
            differences == []
    }
}
