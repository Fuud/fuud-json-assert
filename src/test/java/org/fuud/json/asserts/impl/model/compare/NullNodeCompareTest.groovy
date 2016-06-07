package org.fuud.json.asserts.impl.model.compare

import org.fuud.json.asserts.impl.diff.Difference
import org.fuud.json.asserts.impl.model.*
import spock.lang.Specification
import spock.lang.Unroll

class NullNodeCompareTest extends Specification {

    @Unroll
    def "type mismatch"(Node right) {
        when:
            NullNode left = new NullNode()
            List<Difference> differences = left.compare(right)

        then:
            differences == [new Difference([], Difference.DiffType.TYPE_MISMATCH)]

        where:
            right                                                                          | _
            new StringNode("abc")                                                          | _
            new BooleanNode(true)                                                                 | _
            new NumberNode(123.5)                                                          | _
            new ObjectNode([])                                                             | _
            new ObjectPropertyNode("this comparison should never happen", new NullNode()) | _
    }

    @Unroll
    def "equals"(){
        when:
            NullNode left = new NullNode()
            NullNode right = new NullNode()
            List<Difference> differences = left.compare(right)

        then:
            differences == []
    }
}
