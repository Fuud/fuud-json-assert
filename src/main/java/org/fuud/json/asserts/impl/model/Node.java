package org.fuud.json.asserts.impl.model;

import org.fuud.json.asserts.impl.diff.Difference;

import java.util.List;

public interface Node {
    default List<Difference> compare(Node other) {
        throw new UnsupportedOperationException("not implemented");
    }
}
