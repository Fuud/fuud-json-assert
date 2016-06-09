package org.fuud.json.asserts.impl.model

import org.fuud.json.asserts.impl.parse.Context
import org.fuud.json.asserts.impl.parse.Source
import spock.lang.Specification
import spock.lang.Unroll


class NullNodeTest extends Specification {
    @Unroll
    def "valid value"(String validJson) {
        when:
            NullNode nullNode = NullNode.parse(new Source(validJson), new Context())
        then:
            nullNode == new NullNode()
        where:
            validJson    | _
            "null"       | _
            "   \t null" | _

    }

    @Unroll
    def "invalid value"(String validJson) {
        when:
            NullNode.parse(new Source(validJson), new Context())
        then:
            thrown(IOException)
        where:
            validJson | _
            "nu"      | _
            "nuAA"    | _
    }
}