package org.fuud.json.asserts.impl.model.compare

import org.fuud.json.asserts.impl.diff.Difference
import org.fuud.json.asserts.impl.model.*
import spock.lang.Specification
import spock.lang.Unroll

class BooleanNodeCompareTest extends Specification {

    @Unroll
    def "type mismatch"(Node right) {
        when:
            BooleanNode left = new BooleanNode(true)
            List<Difference> differences = left.compare(right)

        then:
            differences == [new Difference([], Difference.DiffType.TYPE_MISMATCH)]

        where:
            right                                                                          | _
            new StringNode("abc")                                                          | _
            new NullNode()                                                                 | _
            new NumberNode(123.5)                                                          | _
            new ObjectNode([])                                                             | _
            new ObjectPropertyNode("this comparison should never happen", new NullNode()) | _
    }

    @Unroll
    def "not equals"(){
        when:
            BooleanNode left = new BooleanNode(true)
            BooleanNode right = new BooleanNode(false)
            List<Difference> differences = left.compare(right)

        then:
            differences == [new Difference([], Difference.DiffType.NOT_EQUALS)]
    }

    @Unroll
    def "equals"(){
        when:
            BooleanNode left = new BooleanNode(true)
            BooleanNode right = new BooleanNode(true)
            List<Difference> differences = left.compare(right)

        then:
            differences == []
    }
}
