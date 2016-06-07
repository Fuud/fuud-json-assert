package org.fuud.json.asserts.impl.model.compare

import org.fuud.json.asserts.impl.diff.Difference
import org.fuud.json.asserts.impl.model.*
import spock.lang.Specification
import spock.lang.Unroll

import static org.fuud.json.asserts.impl.diff.Difference.DiffType.NOT_EQUALS
import static org.fuud.json.asserts.impl.diff.Difference.DiffType.TYPE_MISMATCH

class ObjectPropertyNodeCompareTest extends Specification {

    @Unroll
    def "type mismatch"(Node right) {
        when:
            ObjectPropertyNode left = new ObjectPropertyNode("this comparison should never happen", new NullNode())
            List<Difference> differences = left.compare(right)

        then:
            differences == [new Difference([], TYPE_MISMATCH)]

        where:
            right                 | _
            new BooleanNode(true) | _
            new NullNode()        | _
            new NumberNode(123.5) | _
            new ObjectNode([])    | _
            new StringNode("abc") | _
    }

    @Unroll
    def "values not equals"(List<Difference> valueDifference, List<Difference> expectedDifferences) {
        when:
            ObjectPropertyNode left = new ObjectPropertyNode("property1", new Node() {
                @Override
                List<Difference> compare(Node other) {
                    return valueDifference
                }
            })
            ObjectPropertyNode right = new ObjectPropertyNode("property1", new NullNode())
            List<Difference> differences = left.compare(right)

        then:
            differences == expectedDifferences

        where:
            valueDifference                               | expectedDifferences
            [new Difference([], NOT_EQUALS)]              | [new Difference(["property1"], NOT_EQUALS)]
            [new Difference(["subproperty"], NOT_EQUALS)] | [new Difference(["property1", "subproperty"], NOT_EQUALS)]
    }

    @Unroll
    def "names not equals"() {
        when:
            ObjectPropertyNode left = new ObjectPropertyNode("property1", new NullNode())
            ObjectPropertyNode right = new ObjectPropertyNode("property2", new StringNode("abc"))
            List<Difference> differences = left.compare(right)

        then:
            differences == [new Difference([], Difference.DiffType.NOT_EQUALS)]
    }

    @Unroll
    def "equals"() {
        when:
            ObjectPropertyNode left = new ObjectPropertyNode("property1", new StringNode("abc"))
            ObjectPropertyNode right = new ObjectPropertyNode("property1", new StringNode("abc"))
            List<Difference> differences = left.compare(right)

        then:
            differences == []
    }
}
