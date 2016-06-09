package org.fuud.json.asserts.impl.model

import org.fuud.json.asserts.impl.diff.ComparatorCreator
import org.fuud.json.asserts.impl.diff.JsonComparator
import org.fuud.json.asserts.impl.parse.Context
import org.fuud.json.asserts.impl.parse.Source
import spock.lang.Specification


class CommentsAndComparatorCreaters extends Specification {
    def "array nodes"() {
        setup:
            ComparatorCreator<ArrayNode> comparatorCreator = Mock()
            Context context = new Context()
            context.arrayNodeComparatorCreator = comparatorCreator

            JsonComparator<ArrayNode> comparator1 = Mock()
            JsonComparator<ArrayNode> comparator2 = Mock()
            JsonComparator<ArrayNode> comparator3 = Mock()

        when:
            String json = """
                // @array1
                [
                    // @array2
                    [

                    ],
                    // @array3
                    [
                      "data!!!"
                    ]
                ]
            """

            ArrayNode parsed = ValueNode.parse(new Source(json), context)

        then:
            1 * comparatorCreator.create(["@array1"]) >> comparator1
            1 * comparatorCreator.create(["@array2"]) >> comparator2
            1 * comparatorCreator.create(["@array3"]) >> comparator3

            parsed.getComparator() == comparator1
            parsed.getElements().get(0).getComparator() == comparator2
            parsed.getElements().get(1).getComparator() == comparator3
    }

    def "object nodes"() {
        setup:
            ComparatorCreator<ObjectNode> comparatorCreator = Mock()
            Context context = new Context()
            context.objectNodeComparatorCreator = comparatorCreator

            JsonComparator<ObjectNode> comparator1 = Mock()
            JsonComparator<ObjectNode> comparator2 = Mock()
            JsonComparator<ObjectNode> comparator3 = Mock()

        when:
            String json = """
                // @object1
                {

                    "property1":
                    // @object2
                    {

                    },
                    "property2":
                    // @object3
                    {
                      "data!!!":"yep"
                    }
                }
            """

            ObjectNode parsed = ValueNode.parse(new Source(json), context)

        then:
            1 * comparatorCreator.create(["@object1"]) >> comparator1
            1 * comparatorCreator.create(["@object2"]) >> comparator2
            1 * comparatorCreator.create(["@object3"]) >> comparator3

            parsed.getComparator() == comparator1
            parsed.getChild("property1").getValue().getComparator() == comparator2
            parsed.getChild("property2").getValue().getComparator() == comparator3
    }

    def "boolean nodes"() {
        setup:
            ComparatorCreator<BooleanNode> comparatorCreator = Mock()
            Context context = new Context()
            context.booleanNodeComparatorCreator = comparatorCreator

            JsonComparator<BooleanNode> comparator1 = Mock()

        when:
            String json = """
                // @array1
                [
                    // @boolean1
                    true

                ]
            """

            ArrayNode parsed = ValueNode.parse(new Source(json), context)

        then:
            1 * comparatorCreator.create(["@boolean1"]) >> comparator1

            parsed.getElements().get(0).getComparator() == comparator1
    }

    def "string nodes"() {
        setup:
            ComparatorCreator<StringNode> comparatorCreator = Mock()
            Context context = new Context()
            context.stringNodeComparatorCreator = comparatorCreator

            JsonComparator<StringNode> comparator1 = Mock()

        when:
            String json = """
                // @array1
                [
                    // @string1
                    "true"

                ]
            """

            ArrayNode parsed = ValueNode.parse(new Source(json), context)

        then:
            1 * comparatorCreator.create(["@string1"]) >> comparator1

            parsed.getElements().get(0).getComparator() == comparator1
    }

    def "number nodes"() {
        setup:
            ComparatorCreator<NumberNode> comparatorCreator = Mock()
            Context context = new Context()
            context.numberNodeComparatorCreator = comparatorCreator

            JsonComparator<NumberNode> comparator1 = Mock()

        when:
            String json = """
                // @array1
                [
                    // @number1
                    123.5

                ]
            """

            ArrayNode parsed = ValueNode.parse(new Source(json), context)

        then:
            1 * comparatorCreator.create(["@number1"]) >> comparator1

            parsed.getElements().get(0).getComparator() == comparator1
    }

    def "null nodes"() {
        setup:
            ComparatorCreator<NullNode> comparatorCreator = Mock()
            Context context = new Context()
            context.nullNodeComparatorCreator = comparatorCreator

            JsonComparator<NullNode> comparator1 = Mock()

        when:
            String json = """
                // @array1
                [
                    // @null1
                    null

                ]
            """

            ArrayNode parsed = ValueNode.parse(new Source(json), context)

        then:
            1 * comparatorCreator.create(["@null1"]) >> comparator1

            parsed.getElements().get(0).getComparator() == comparator1
    }

    def "objectProperty nodes"() {
        setup:
            ComparatorCreator<ObjectPropertyNode> comparatorCreator = Mock()
            Context context = new Context()
            context.objectPropertyNodeComparatorCreator = comparatorCreator

            JsonComparator<ObjectPropertyNode> comparator1 = Mock()

        when:
            String json = """
                // @object1
                {
                    // @objectProperty1
                    "property1" : "objectProperty"

                }
            """

            ObjectNode parsed = ValueNode.parse(new Source(json), context)

        then:
            1 * comparatorCreator.create(["@objectProperty1"]) >> comparator1

            parsed.getChild("property1").getComparator() == comparator1
    }
}
