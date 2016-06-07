package org.fuud.json.asserts.impl.model

import org.fuud.json.asserts.impl.parse.Source
import spock.lang.Specification
import spock.lang.Unroll


class BooleanNodeTest extends Specification {
    @Unroll
    def "valid value"(String validJson, boolean expectedValue) {
        when:
            BooleanNode booleanNode = BooleanNode.parse(new Source(validJson))
        then:
            booleanNode == new BooleanNode(expectedValue)
        where:
            validJson       | expectedValue
            "true"          | true
            "false"         | false
            "   \t true"    | true
            "   \r\n false" | false

    }

    @Unroll
    def "invalid value"(String validJson) {
        when:
            BooleanNode.parse(new Source(validJson))
        then:
            thrown(IOException)
        where:
            validJson | _
            "tru"     | _
            "fals"    | _
            "truA"    | _
            "falsB"   | _
    }
}
