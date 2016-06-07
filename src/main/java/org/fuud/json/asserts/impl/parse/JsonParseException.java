package org.fuud.json.asserts.impl.parse;

import java.io.IOException;

public class JsonParseException extends IOException {
    public JsonParseException(CharAndPosition description) {
        super("Unexpected char '" + description.getCharacter() + "' " +
                "with code [" + ((int) description.getCharacter()) + "] " +
                "at position " + description.getPosition());
    }

    public JsonParseException(String expectedChars, CharAndPosition description) {
        super("Expected chars '" + expectedChars + "' " +
                "but found char '" + description.getCharacter() + "' " +
                "with code [" + ((int) description.getCharacter()) + "] " +
                "at position " + description.getPosition());
    }

    public JsonParseException(String message){
        super(message);
    }

    public JsonParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
