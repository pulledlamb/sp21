package byow.Core;

public class StringInputDevice implements InputSource {
    private String input;
    private int index;

    public StringInputDevice(String s) {
        index = 0;
        input = s;
    }

    public char getNextKey() {
        char returnChar = input.charAt(index);
        index += 1;
        return Character.toUpperCase(returnChar);
    }

    public boolean possibleNextInput() {
        return index < input.length();
    }
}
