package org.fuud.json.asserts.impl.parse;

import org.fuud.json.asserts.impl.diff.ComparatorCreator;
import org.fuud.json.asserts.impl.model.*;

public class Context {
    private ComparatorCreator<ArrayNode> arrayNodeComparatorCreator = args -> new ArrayNode.ArrayNodeJsonComparator();
    private ComparatorCreator<BooleanNode> booleanNodeComparatorCreator = args -> new BooleanNode.BooleanNodeComparator();
    private ComparatorCreator<NullNode> nullNodeComparatorCreator = args -> new NullNode.NullNodeComparator();
    private ComparatorCreator<NumberNode> numberNodeComparatorCreator = args -> new NumberNode.NumberNodeComparator();
    private ComparatorCreator<ObjectNode> objectNodeComparatorCreator = args -> new ObjectNode.ObjectNodeComparator();
    private ComparatorCreator<ObjectPropertyNode> objectPropertyNodeComparatorCreator = args -> new ObjectPropertyNode.ObjectPropertyNodeComparator();
    private ComparatorCreator<StringNode> stringNodeComparatorCreator = args -> new StringNode.StringNodeComparator();

    public ComparatorCreator<ArrayNode> getArrayNodeComparatorCreator() {
        return arrayNodeComparatorCreator;
    }

    public void setArrayNodeComparatorCreator(ComparatorCreator<ArrayNode> arrayNodeComparatorCreator) {
        this.arrayNodeComparatorCreator = arrayNodeComparatorCreator;
    }

    public ComparatorCreator<BooleanNode> getBooleanNodeComparatorCreator() {
        return booleanNodeComparatorCreator;
    }

    public void setBooleanNodeComparatorCreator(ComparatorCreator<BooleanNode> booleanNodeComparatorCreator) {
        this.booleanNodeComparatorCreator = booleanNodeComparatorCreator;
    }

    public ComparatorCreator<NullNode> getNullNodeComparatorCreator() {
        return nullNodeComparatorCreator;
    }

    public void setNullNodeComparatorCreator(ComparatorCreator<NullNode> nullNodeComparatorCreator) {
        this.nullNodeComparatorCreator = nullNodeComparatorCreator;
    }

    public ComparatorCreator<NumberNode> getNumberNodeComparatorCreator() {
        return numberNodeComparatorCreator;
    }

    public void setNumberNodeComparatorCreator(ComparatorCreator<NumberNode> numberNodeComparatorCreator) {
        this.numberNodeComparatorCreator = numberNodeComparatorCreator;
    }

    public ComparatorCreator<ObjectNode> getObjectNodeComparatorCreator() {
        return objectNodeComparatorCreator;
    }

    public void setObjectNodeComparatorCreator(ComparatorCreator<ObjectNode> objectNodeComparatorCreator) {
        this.objectNodeComparatorCreator = objectNodeComparatorCreator;
    }

    public ComparatorCreator<ObjectPropertyNode> getObjectPropertyNodeComparatorCreator() {
        return objectPropertyNodeComparatorCreator;
    }

    public void setObjectPropertyNodeComparatorCreator(ComparatorCreator<ObjectPropertyNode> objectPropertyNodeComparatorCreator) {
        this.objectPropertyNodeComparatorCreator = objectPropertyNodeComparatorCreator;
    }

    public ComparatorCreator<StringNode> getStringNodeComparatorCreator() {
        return stringNodeComparatorCreator;
    }

    public void setStringNodeComparatorCreator(ComparatorCreator<StringNode> stringNodeComparatorCreator) {
        this.stringNodeComparatorCreator = stringNodeComparatorCreator;
    }
}
