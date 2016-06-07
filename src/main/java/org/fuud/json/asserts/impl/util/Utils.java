package org.fuud.json.asserts.impl.util;

import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Utils {
    public static String IDENT = "  ";

    public static final Function<String, String> addIdent = source ->
            Stream.of(source.split("\n")).
                    map(line -> IDENT + line).
                    collect(Collectors.joining("\n"));

    public static final Function<String, String> addIdentExceptFirstLine = source ->
            Stream.of(source.split("\n")).
                    collect(Collectors.joining("\n" + IDENT));

}
