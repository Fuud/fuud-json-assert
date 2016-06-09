package org.fuud.json.asserts.impl.model

import org.fuud.json.asserts.impl.parse.Source
import spock.lang.Specification
import spock.lang.Unroll

class StringNodeTest extends Specification {
    def "parse empty string"() {
        when:
            String json = '""';
            StringNode stringNode = StringNode.parse(new Source(json))
        then:
            stringNode == new StringNode("");
    }

    @Unroll
    def "invalid string"(String invalidString) {
        when:
            StringNode.parse(new Source(invalidString))
        then:
            IOException e = thrown(IOException)
            e.getMessage() != null
        where:
            invalidString | _
//            '"\""'        | _
            '"\\"'        | _
            '"\b"'        | _
            '"\f"'        | _
            '"\n"'        | _
            '"\r"'        | _
            '"\t"'        | _
            '"\\ut"'      | _   // invalid unicode
            '"\\u000"'    | _   // invalid unicode
            '"\\uz000"'   | _   // invalid unicode
            '"\\e"'       | _   // invalid escape
    }

    @Unroll
    def "valid string"(String rawString, String expectedValue) {
        when:
            StringNode stringNode = StringNode.parse(new Source(rawString))
        then:
            stringNode == new StringNode(expectedValue)
        where:
            rawString         | expectedValue
//            '"\""'        | _
            '"\\\\"'          | "\\"
            '"\\b"'           | "\b"
            '"\\f"'           | "\f"
            '"\\n"'           | "\n"
            '"\\r"'           | "\r"
            '"\\t"'           | "\t"
            '"\\uFF32"'       | "\uFF32"
            '"abcd echo 123"' | 'abcd echo 123'
            '   \r\n\t "abcd echo 123"' | 'abcd echo 123'
    }
}
