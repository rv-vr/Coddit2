package taste;

public class Token {
    private final TokenType type;
    private final String value;
    private final int line;

    public Token(TokenType type, String value, int line) {
        this.type = type;
        this.value = value;
        this.line = line;
    }

    public TokenType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }
    
    public int getLine() {
        return line;
    }

    @Override
    public String toString() {
        return String.format("<%s, '%s', Line: %d>", type, value, line);
    }
}

