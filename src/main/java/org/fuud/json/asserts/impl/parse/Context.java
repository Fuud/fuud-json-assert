package org.fuud.json.asserts.impl.parse;

import org.fuud.json.asserts.impl.diff.ComparatorCreator;
import org.fuud.json.asserts.impl.model.*;

public class Context {
    private ComparatorCreator arrayNodeComparatorCreator = args -> new ArrayNode.ArrayNodeJsonComparator();
    private ComparatorCreator booleanNodeComparatorCreator = args -> new BooleanNode.BooleanNodeComparator();
    private ComparatorCreator nullNodeComparatorCreator = args -> new NullNode.NullNodeComparator();
    private ComparatorCreator numberNodeComparatorCreator = args -> new NumberNode.NumberNodeComparator();
    private ComparatorCreator objectNodeComparatorCreator = args -> new ObjectNode.ObjectNodeComparator();
    private ComparatorCreator objectPropertyNodeComparatorCreator = args -> new ObjectPropertyNode.ObjectPropertyNodeComparator();
    private ComparatorCreator stringNodeComparatorCreator = args -> new StringNode.StringNodeComparator();

    public ComparatorCreator getArrayNodeComparatorCreator() {
        return arrayNodeComparatorCreator;
    }

    public void setArrayNodeComparatorCreator(ComparatorCreator arrayNodeComparatorCreator) {
        this.arrayNodeComparatorCreator = arrayNodeComparatorCreator;
    }

    public ComparatorCreator getBooleanNodeComparatorCreator() {
        return booleanNodeComparatorCreator;
    }

    public void setBooleanNodeComparatorCreator(ComparatorCreator booleanNodeComparatorCreator) {
        this.booleanNodeComparatorCreator = booleanNodeComparatorCreator;
    }

    public ComparatorCreator getNullNodeComparatorCreator() {
        return nullNodeComparatorCreator;
    }

    public void setNullNodeComparatorCreator(ComparatorCreator nullNodeComparatorCreator) {
        this.nullNodeComparatorCreator = nullNodeComparatorCreator;
    }

    public ComparatorCreator getNumberNodeComparatorCreator() {
        return numberNodeComparatorCreator;
    }

    public void setNumberNodeComparatorCreator(ComparatorCreator numberNodeComparatorCreator) {
        this.numberNodeComparatorCreator = numberNodeComparatorCreator;
    }

    public ComparatorCreator getObjectNodeComparatorCreator() {
        return objectNodeComparatorCreator;
    }

    public void setObjectNodeComparatorCreator(ComparatorCreator objectNodeComparatorCreator) {
        this.objectNodeComparatorCreator = objectNodeComparatorCreator;
    }

    public ComparatorCreator getObjectPropertyNodeComparatorCreator() {
        return objectPropertyNodeComparatorCreator;
    }

    public void setObjectPropertyNodeComparatorCreator(ComparatorCreator objectPropertyNodeComparatorCreator) {
        this.objectPropertyNodeComparatorCreator = objectPropertyNodeComparatorCreator;
    }

    public ComparatorCreator getStringNodeComparatorCreator() {
        return stringNodeComparatorCreator;
    }

    public void setStringNodeComparatorCreator(ComparatorCreator stringNodeComparatorCreator) {
        this.stringNodeComparatorCreator = stringNodeComparatorCreator;
    }
}
