package org.fuud.json.asserts

import junit.framework.ComparisonFailure
import spock.lang.Specification
import spock.lang.Unroll


class Test extends Specification {

    @Unroll
    def "equal should be equal #data"(String data) {
        expect:
            JsonAsserts.assertEquals(data, data)
        where:
            data               | _
            '{}'               | _
            '{"a":"a"}'        | _
            '{"a":0}'          | _
            '{"a": {"a":"a"}}' | _
            '{"a":["a", "b"]}' | _
            '["a", "b"]'       | _
            '[{"a":"a"}, "b"]' | _
    }

    @Unroll
    def "missing something in actual"(String expected, String actual, String rendering) {
        setup:
            rendering = rendering.stripIndent()
            if (rendering.startsWith("\n")) {
                rendering = rendering.substring(1);
            }
        when:
            JsonAsserts.assertEquals(expected, actual)
        then:
            def e = thrown(AssertionError)
            assertLinesEqual(rendering, e.getMessage())
        where:
            [expected, actual, rendering] << [
                    [
                            '{"a":"b"}',
                            '{}',
                            """
                            {                           {
                              "a" : "b"        +-
                            }                           }
                            """
                    ], [
                            '{"a":{"a" : "b"}}',
                            '{}',
                            """
                            {                               {
                              "a" : {             +-
                                "a" : "b"
                              }
                            }                               }
                            """
                    ], [
                            '{"a":["a", "b"]}',
                            '{}',
                            """
                            {                       {
                              "a" : [       +-
                                "a",
                                "b"
                              ]
                            }                       }
                            """
                    ], [
                            '{"a":123}',
                            '{}',
                            """
                            {                           {
                              "a" : 123        +-
                            }                           }
                            """
                    ], [
                            '{"a":true}',
                            '{}',
                            """
                            {                           {
                              "a" : true        +-
                            }                           }
                            """
                    ], [
                            '{"a":{"a" : {"a":"b"}}}',
                            '{}',
                            """
                            {                               {
                              "a" : {             +-
                                "a" : {
                                  "a" : "b"
                                }
                              }
                            }                               }
                            """
                    ], [
                            '{"a":{"a" : ["a", "b"]}}',
                            '{}',
                            """
                            {                               {
                              "a" : {             +-
                                "a" : [
                                  "a",
                                  "b"
                                ]
                              }
                            }                               }
                            """
                    ], [
                            '{"a":{"a" : 123}}',
                            '{}',
                            """
                            {                               {
                              "a" : {             +-
                                "a" : 123
                              }
                            }                               }
                            """
                    ], [
                            '{"a":{"a" : true}}',
                            '{}',
                            """
                            {                               {
                              "a" : {             +-
                                "a" : true
                              }
                            }                               }
                            """
                    ], [
                            '{"a":["a", {"a":"b"}]}',
                            '{}',
                            """
                            {                       {
                              "a" : [       +-
                                "a",
                                {
                                    "a":"b"
                                }
                              ]
                            }                       }
                            """
                    ], [
                            '{"a":["a", 123]}',
                            '{}',
                            """
                            {                       {
                              "a" : [       +-
                                "a",
                                123
                              ]
                            }                       }
                            """
                    ], [
                            '{"a":["a", true]}',
                            '{}',
                            """
                            {                       {
                              "a" : [       +-
                                "a",
                                true
                              ]
                            }                       }
                            """
                    ],

            ]

    }

    @Unroll
    def "not equals"(String expected, String actual, String rendering) {
        setup:
            rendering = rendering.stripIndent()
            if (rendering.startsWith("\n")) {
                rendering = rendering.substring(1);
            }
        when:
            JsonAsserts.assertEquals(expected, actual)
        then:
            def e = thrown(AssertionError)
            assertLinesEqual(rendering, e.getMessage())
        where:
            [expected, actual, rendering] << [
                    [
                            '{"a":"b"}',
                            '{"a":"c"}',
                            """
                            {                           {
                              "a" : "b"        <>           "a" : "c"
                            }                           }
                            """
                    ],[
                            '{"a":123}',
                            '{"a":456}',
                            """
                            {                           {
                              "a" : 123      <>           "a" : 456
                            }                           }
                            """
                    ],[
                            '{"a":true}',
                            '{"a":false}',
                            """
                            {                           {
                              "a" : true      <>           "a" : false
                            }                           }
                            """
                    ],[
                            '{"a":{"a":"b"}}',
                            '{"a":{"a":"c"}}',
                            """
                            {                           {
                              "a" : {                       "a" : {
                                "a" : "b"  <>                   "a":"c"
                              }                             }
                            }                           }
                            """
                    ],[
                            '{"a":["a","b"]}',
                            '{"a":["a","c"]}',
                            """
                            {                           {
                              "a" : [                       "a" : [
                                "a",                            "a",
                                "b"       <>                    "c"
                              ]                             ]
                            }                           }
                            """
                    ],
            ]
    }

    void assertLinesEqual(String expected, String actual) {
        List<String> expectedLines = expected.readLines()
        List<String> actualLines = actual.readLines()
        if (expectedLines.size() != actualLines.size()) {
            throw new ComparisonFailure("Different lines count", expected, actual);
        }
        List<Integer> invalidLines = new ArrayList<>()

        for (int line = 0; line < expectedLines.size(); line++) {
            String expectedLine = removeFormattingSpaces(expectedLines.get(line));
            String actualLine = removeFormattingSpaces(actualLines.get(line));
            boolean linesEquals = expectedLine.equals(actualLine)
            if (!linesEquals) {
                invalidLines.add(line)
            }
        }
        if (!invalidLines.isEmpty()) {
            throw new ComparisonFailure("Some lines are different: " + invalidLines, expected, actual);
        }

    }

    String removeFormattingSpaces(String s) {
        return s.trim().replaceAll("\\s\\s*", '');
    }
}
