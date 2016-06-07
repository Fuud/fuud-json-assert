package org.fuud.json.asserts.impl.diff;

import org.fuud.json.asserts.impl.model.Node;

import java.util.List;

@FunctionalInterface
public interface JsonComparator<TLeft extends Node> {
    List<Difference> compare(TLeft leftNode, Node rightNode);
}
