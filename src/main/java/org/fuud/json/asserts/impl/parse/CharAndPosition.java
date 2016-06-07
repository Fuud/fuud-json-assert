package org.fuud.json.asserts.impl.parse;

public class CharAndPosition {
    private final int position;
    private final char character;

    public CharAndPosition(int position, char character) {
        this.position = position;
        this.character = character;
    }

    public int getPosition() {
        return position;
    }

    public char getCharacter() {
        return character;
    }
}
