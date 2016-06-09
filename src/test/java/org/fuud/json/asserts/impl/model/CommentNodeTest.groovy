package org.fuud.json.asserts.impl.model

import org.fuud.json.asserts.impl.parse.Context
import org.fuud.json.asserts.impl.parse.Source
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class CommentNodeTest extends Specification {
    def "one line comment"(String json, String text, List<String> annotations) {
        when:
            CommentNode parsed = CommentNode.parse(new Source(json), new Context())
        then:
            parsed.getComment() == text
            !parsed.isMultiline()
            parsed.getAnnotations() == annotations
        where:
            json                                                                  | text                                                                | annotations
            "// this is the comment"                                              | " this is the comment"                                              | []
            "// this should be @notNull"                                          | " this should be @notNull"                                          | ["@notNull"]
            "// should match @regex{.*}"                                          | " should match @regex{.*}"                                          | ["@regex{.*}"]
            "// should match @regex[(abc)*] or @regex([^abc]) or @regex([^abc]) " | " should match @regex[(abc)*] or @regex([^abc]) or @regex([^abc]) " | ["@regex[(abc)*]", "@regex([^abc])", "@regex([^abc])"]
            "// should match @regex([^abc])"                                      | " should match @regex([^abc])"                                      | ["@regex([^abc])"]
            "// should match @regex([^abc]) "                                     | " should match @regex([^abc]) "                                     | ["@regex([^abc])"]
    }

    def "one multiline comment"(String json, String text, List<String> annotations) {
        when:
            CommentNode parsed = CommentNode.parse(new Source(json), new Context())
        then:
            parsed.getComment() == text
            parsed.isMultiline()
            parsed.getAnnotations() == annotations
        where:
            json                                                                            | text                                                                        | annotations
            "/* this is \n the comment*/"                                                   | " this is \n the comment"                                                      | []
            "/* this should be \n@notNull */"                                               | " this should be \n@notNull "                                                  | ["@notNull"]
            "/* should match @regex{.*}*/"                                                  | " should match @regex{.*}"                                                  | ["@regex{.*}"]
            "/* should match @regex[(abc)*] \n or @regex(\n[^abc]) \n or @regex([^abc]) */" | " should match @regex[(abc)*] \n or @regex(\n[^abc]) \n or @regex([^abc]) " | ["@regex[(abc)*]", "@regex(\n[^abc])", "@regex([^abc])"]
            "/* should match @regex([^abc])*/"                                              | " should match @regex([^abc])"                                              | ["@regex([^abc])"]
            "/* should match @regex([^abc]) */"                                             | " should match @regex([^abc]) "                                             | ["@regex([^abc])"]
    }
}
