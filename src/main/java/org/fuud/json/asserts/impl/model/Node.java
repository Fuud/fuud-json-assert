package org.fuud.json.asserts.impl.model;

import org.fuud.json.asserts.impl.diff.Difference;
import org.fuud.json.asserts.impl.diff.JsonComparator;

import java.util.List;

public class Node<TThis extends Node<TThis>> {
    private JsonComparator<TThis> comparator;

    public Node(JsonComparator<TThis> comparator) {
        this.comparator = comparator;
    }

    public JsonComparator getComparator() {
        return comparator;
    }

    public void setComparator(JsonComparator<TThis> comparator) {
        this.comparator = comparator;
    }

    @SuppressWarnings("unchecked")
    public final List<Difference> compare(Node right) {
        return comparator.compare((TThis) this, right);
    }
}
