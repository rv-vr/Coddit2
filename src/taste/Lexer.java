package taste;

import java.util.ArrayList;
import java.util.List;

public class Lexer {
    private final String input;
    private int position = 0;
    private final int length;
    private int line = 1;
    private final boolean funErrorCodes;

    public Lexer(String input, boolean funErrorCodes) {
        this.input = input;
        this.length = input.length();
        this.funErrorCodes = funErrorCodes;
    }
    
    public Lexer(String input) {
        this(input, true);
    }

    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();
        while (position < length) {
            char current = input.charAt(position);

            if (current == '\n') {
                line++;
                position++;
                continue;
            }

            if (Character.isWhitespace(current)) {
                position++;
                continue;
            }

            if (Character.isDigit(current)) {
                tokens.add(readNumber());
                continue;
            }

            if (Character.isLetter(current)) {
                tokens.add(readWord());
                continue;
            }

            if (current == '"') {
                tokens.add(readString());
                continue;
            }

            if (current == '\'') {
                tokens.add(readChar());
                continue;
            }

            if (current == '/') {
                if (peek(1) == '/') {
                    while (position < length && input.charAt(position) != '\n') {
                        position++;
                    }
                    continue;
                }
                tokens.add(new Token(TokenType.OP_DIV, "/", line));
                position++;
                continue;
            }

            Token symbolToken = processSymbol(current);
            if (symbolToken != null) {
                tokens.add(symbolToken);
                continue;
            }

            throw error("What is this garbage? '" + current + "'", "Unknown character: " + current);
        }
        tokens.add(new Token(TokenType.EOF, "", line));
        return tokens;
    }
    
    private RuntimeException error(String fun, String generic) {
        if (funErrorCodes) {
            return new RuntimeException("LEXICAL INDIGESTION (Line " + line + "): " + fun);
        } else {
            return new RuntimeException("Syntax Error (Line " + line + "): " + generic);
        }
    }

    private Token processSymbol(char current) {
        switch (current) {
            case '{': position++; return new Token(TokenType.LBRACE, "{", line);
            case '}': position++; return new Token(TokenType.RBRACE, "}", line);
            case '(': position++; return new Token(TokenType.LPAREN, "(", line);
            case ')': position++; return new Token(TokenType.RPAREN, ")", line);
            case ',': position++; return new Token(TokenType.COMMA, ",", line);
            case '+': position++; return new Token(TokenType.OP_ADD, "+", line);
            case '-': position++; return new Token(TokenType.OP_SUB, "-", line);
            case '*': position++; return new Token(TokenType.OP_MULT, "*", line);
            case '%': position++; return new Token(TokenType.OP_MOD, "%", line);
            case '&': position++; return new Token(TokenType.OP_AND, "&", line);
            case '<': position++; return new Token(TokenType.OP_LT, "<", line);
            
            case '=': return processEquals();
            case '!': return processBang();
            case '>': return processGreater();
            
            default: return null;
        }
    }

    private Token processEquals() {
        if (peek(1) == '=') {
            position += 2;
            return new Token(TokenType.OP_EQ, "==", line);
        }
        position++;
        return new Token(TokenType.ASSIGN, "=", line);
    }

    private Token processBang() {
        if (peek(1) == '=') {
            position += 2;
            return new Token(TokenType.OP_NEQ, "!=", line);
        }
        throw error("Don't shout at me! (Unexpected '!')", "Unexpected character '!'");
    }
    
    private Token processGreater() {
        if (peek(1) == '<') {
            position += 2;
            return new Token(TokenType.OP_OR, "><", line);
        }
        position++;
        return new Token(TokenType.OP_GT, ">", line);
    }

    private Token readNumber() {
        StringBuilder sb = new StringBuilder();
        boolean isFloat = false;
        while (position < length && (Character.isDigit(input.charAt(position)) || input.charAt(position) == '.')) {
            char c = input.charAt(position);
            if (c == '.') {
                if (isFloat) break;
                isFloat = true;
            }
            sb.append(c);
            position++;
        }
        return new Token(isFloat ? TokenType.LIT_FLOAT : TokenType.LIT_INTEGER, sb.toString(), line);
    }

    private Token readWord() {
        StringBuilder sb = new StringBuilder();
        while (position < length && (Character.isLetterOrDigit(input.charAt(position)) || input.charAt(position) == '_')) {
            sb.append(input.charAt(position++));
        }
        String word = sb.toString();
        return new Token(getKeywordType(word), word, line);
    }

    private TokenType getKeywordType(String word) {
        switch (word) {
            case "Chunk": return TokenType.KEY_INTEGER;
            case "Slurp": return TokenType.KEY_FLOAT;
            case "Wrap": return TokenType.KEY_STRING;
            case "Taste": return TokenType.KEY_BOOLEAN;
            case "chew": return TokenType.KEY_IF;
            case "spit": return TokenType.KEY_ELSE;
            case "nibble": return TokenType.KEY_ELSE_IF;
            case "Yum": return TokenType.VAL_TRUE;
            case "Yuck": return TokenType.VAL_FALSE;
            case "burp": return TokenType.KEY_PRINT;
            case "swallow": return TokenType.KEY_INPUT;
            default: return TokenType.IDENTIFIER;
        }
    }

    private Token readString() {
        position++; // skip "
        StringBuilder sb = new StringBuilder();
        while (position < length && input.charAt(position) != '"') {
            if (input.charAt(position) == '\n') line++;
            sb.append(input.charAt(position++));
        }
        if (position < length) position++;
        return new Token(TokenType.LIT_STRING, sb.toString(), line);
    }

    private Token readChar() {
        position++; // skip '
        char c = input.charAt(position++);
        if (position < length && input.charAt(position) == '\'') position++;
        return new Token(TokenType.LIT_CHAR, String.valueOf(c), line);
    }

    private char peek(int offset) {
        if (position + offset >= length) return '\0';
        return input.charAt(position + offset);
    }
}
