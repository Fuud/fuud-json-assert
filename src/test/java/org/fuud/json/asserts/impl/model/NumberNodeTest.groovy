package org.fuud.json.asserts.impl.model

import org.fuud.json.asserts.impl.parse.Source
import spock.lang.Specification
import spock.lang.Unroll


class NumberNodeTest extends Specification {
    @Unroll
    def "invalid number"(String invalidJson) {
        when:
            NumberNode.parse(new Source(invalidJson))
        then:
            IOException e = thrown(IOException)
            e.getMessage() != null
        where:
            invalidJson | _
            'abc'       | _
            '123a'      | _
            '1.1.1'     | _
    }

    @Unroll
    def "valid number"(String rawString, String expectedValue) {
        when:
            NumberNode numberNode = NumberNode.parse(new Source(rawString))
            NumberNode numberNodeQuoute = NumberNode.parse(new Source(rawString + "\""))
            NumberNode numberNodeSpace = NumberNode.parse(new Source(rawString + " "))
            NumberNode numberNodeNewLine = NumberNode.parse(new Source(rawString + "\n"))
            NumberNode numberNodeComma = NumberNode.parse(new Source(rawString + ","))
        then:
            NumberNode expected = new NumberNode(new BigDecimal(expectedValue))
            numberNode == expected
            numberNodeQuoute == expected
            numberNodeSpace == expected
            numberNodeNewLine == expected
            numberNodeComma == expected
        where:
            rawString   | expectedValue
            '\r\t 123'  | '123'
            '-123'      | '-123'
            '123.45'    | '123.45'
            '123.45e3'  | '123450'
            '123.45E3'  | '123450'
            '123.45E+3' | '123450'
            '123.45E-3' | '0.123450'

    }
}
