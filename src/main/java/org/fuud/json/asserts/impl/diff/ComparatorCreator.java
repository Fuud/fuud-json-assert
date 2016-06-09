package org.fuud.json.asserts.impl.diff;

import org.fuud.json.asserts.impl.model.Node;

import java.util.List;

public interface ComparatorCreator<TNode extends Node<TNode>> {
    public JsonComparator<TNode> create(List<String> args);
}
