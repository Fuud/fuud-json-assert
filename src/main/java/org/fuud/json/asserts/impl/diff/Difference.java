package org.fuud.json.asserts.impl.diff;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Difference {
    private final List<String> path;
    private final DiffType type;

    public Difference(List<String> path, DiffType type) {
        this.path = Objects.requireNonNull(path);
        this.type = Objects.requireNonNull(type);
    }

    /**
     * Path elements.
     * For object properties element is equal to property name.
     * For array element equals to element index (indexes are zero-based).
     * For example:
     * <pre>
     *     {
     *        "a" : {
     *            "b" : [
     *              "c",
     *              "d"
     *            ]
     *        }
     *     }
     * </pre>
     * Path to element "d" is ["a", "b", "1"]
     */
    public List<String> getPath() {
        return path;
    }

    public DiffType getType() {
        return type;
    }

    public enum DiffType {
        TYPE_MISMATCH,
        MISSING,
        NOT_EXPECTED,
        NOT_EQUALS
    }

    @Override
    public String toString() {
        return "Difference{" +
                "[" + path.stream().collect(Collectors.joining(".")) +
                "] " + type +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Difference)) return false;

        Difference that = (Difference) o;

        if (!path.equals(that.path)) return false;
        return type == that.type;

    }

    @Override
    public int hashCode() {
        int result = path.hashCode();
        result = 31 * result + type.hashCode();
        return result;
    }

    public static Function<Difference, Difference> addParentPath(String parentPath){
        return difference -> {
            List<String> path = new ArrayList<>();
            path.add(parentPath);
            path.addAll(difference.getPath());
            return new Difference(path, difference.getType());
        };
    }
}
