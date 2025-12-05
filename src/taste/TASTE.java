package taste;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class TASTE {
    // Theoretical Algorithm Structure Translation Engine

    // --- 1. Token Definitions ---
    public enum TokenType {
        KEY_CHUNK, KEY_SLURP, KEY_WRAP, KEY_TASTE, KEY_BURP, 
        KEY_SWALLOW, KEY_GRAB,                               
        KEY_CHEW, KEY_SPIT, KEY_NIBBLE,                      
        VAL_YUM, VAL_YUCK,                                   
        LBRACE, RBRACE, LPAREN, RPAREN, ASSIGN, COMMA,       
        OP_ADD, OP_SUB, OP_MULT, OP_DIV, OP_MOD,             
        OP_EQ, OP_NEQ,                                       
        OP_LOG_AND, OP_LOG_OTHER, OP_REL_GT, OP_REL_LT,      
        ID, LIT_INT, LIT_FLOAT, LIT_STRING, LIT_CHAR, EOF    
    }

    public static class Token {
        public final TokenType type;
        public final String value;
        public Token(TokenType type, String value) { this.type = type; this.value = value; }
        @Override public String toString() { return String.format("<%s, '%s'>", type, value); }
    }

    // --- 2. Lexer ---
    public static class Lexer {
        private final String input;
        private int pos = 0;

        public Lexer(String input) { this.input = input; }

        public List<Token> tokenize() {
            List<Token> tokens = new ArrayList<>();
            while (pos < input.length()) {
                char current = input.charAt(pos);
                if (Character.isWhitespace(current)) { pos++; continue; }
                if (Character.isDigit(current)) { tokens.add(readNumber()); continue; }
                if (Character.isLetter(current)) { tokens.add(readWord()); continue; }
                if (current == '"') { tokens.add(readString()); continue; }
                if (current == '\'') { tokens.add(readChar()); continue; }

                switch (current) {
                    case '{': tokens.add(new Token(TokenType.LBRACE, "{")); pos++; break;
                    case '}': tokens.add(new Token(TokenType.RBRACE, "}")); pos++; break;
                    case '(': tokens.add(new Token(TokenType.LPAREN, "(")); pos++; break;
                    case ')': tokens.add(new Token(TokenType.RPAREN, ")")); pos++; break;
                    case ',': tokens.add(new Token(TokenType.COMMA, ",")); pos++; break;
                    
                    case '=': 
                        if (pos + 1 < input.length() && input.charAt(pos + 1) == '=') {
                            tokens.add(new Token(TokenType.OP_EQ, "==")); pos += 2;
                        } else {
                            tokens.add(new Token(TokenType.ASSIGN, "=")); pos++; 
                        }
                        break;
                    case '!':
                         if (pos + 1 < input.length() && input.charAt(pos + 1) == '=') {
                            tokens.add(new Token(TokenType.OP_NEQ, "!=")); pos += 2;
                        } else {
                            throw new RuntimeException("Unexpected character '!'");
                        }
                        break;

                    case '+': tokens.add(new Token(TokenType.OP_ADD, "+")); pos++; break;
                    case '-': tokens.add(new Token(TokenType.OP_SUB, "-")); pos++; break;
                    case '*': tokens.add(new Token(TokenType.OP_MULT, "*")); pos++; break;
                    case '/': 
                        if (pos + 1 < input.length() && input.charAt(pos + 1) == '/') {
                            while (pos < input.length() && input.charAt(pos) != '\n') {
                                pos++;
                            }
                        } else {
                            tokens.add(new Token(TokenType.OP_DIV, "/")); pos++; 
                        }
                        break;
                    case '%': tokens.add(new Token(TokenType.OP_MOD, "%")); pos++; break;
                    case '&': tokens.add(new Token(TokenType.OP_LOG_AND, "&")); pos++; break;
                    case '>': 
                         if (pos + 1 < input.length() && input.charAt(pos + 1) == '<') {
                             tokens.add(new Token(TokenType.OP_LOG_OTHER, "><")); pos += 2;
                         } else {
                             tokens.add(new Token(TokenType.OP_REL_GT, ">")); pos++; 
                         }
                         break;
                    case '<': tokens.add(new Token(TokenType.OP_REL_LT, "<")); pos++; break;
                    default: throw new RuntimeException("Unknown character: " + current);
                }
            }
            tokens.add(new Token(TokenType.EOF, ""));
            return tokens;
        }

        private Token readNumber() {
            StringBuilder sb = new StringBuilder();
            boolean isFloat = false;
            while (pos < input.length() && (Character.isDigit(input.charAt(pos)) || input.charAt(pos) == '.')) {
                if (input.charAt(pos) == '.') { if (isFloat) break; isFloat = true; }
                sb.append(input.charAt(pos++));
            }
            return new Token(isFloat ? TokenType.LIT_FLOAT : TokenType.LIT_INT, sb.toString());
        }
        
        private Token readWord() {
            StringBuilder sb = new StringBuilder();
            while (pos < input.length() && (Character.isLetterOrDigit(input.charAt(pos)) || input.charAt(pos) == '_')) {
                sb.append(input.charAt(pos++));
            }
            String w = sb.toString();
            switch (w) {
                case "grab": return new Token(TokenType.KEY_GRAB, w);
                case "Chunk": return new Token(TokenType.KEY_CHUNK, w);
                case "Slurp": return new Token(TokenType.KEY_SLURP, w);
                case "Wrap": return new Token(TokenType.KEY_WRAP, w);
                case "Taste": return new Token(TokenType.KEY_TASTE, w);
                case "chew": return new Token(TokenType.KEY_CHEW, w);
                case "spit": return new Token(TokenType.KEY_SPIT, w);
                case "nibble": return new Token(TokenType.KEY_NIBBLE, w);
                case "Yum": return new Token(TokenType.VAL_YUM, w);
                case "Yuck": return new Token(TokenType.VAL_YUCK, w);
                case "burp": return new Token(TokenType.KEY_BURP, w);
                case "swallow": return new Token(TokenType.KEY_SWALLOW, w);
                default: return new Token(TokenType.ID, w);
            }
        }

        private Token readString() {
            pos++; // skip "
            StringBuilder sb = new StringBuilder();
            while (pos < input.length() && input.charAt(pos) != '"') sb.append(input.charAt(pos++));
            if (pos < input.length()) pos++;
            return new Token(TokenType.LIT_STRING, sb.toString());
        }

        private Token readChar() {
            pos++; // skip '
            char c = input.charAt(pos++);
            if (pos < input.length() && input.charAt(pos) == '\'') pos++;
            return new Token(TokenType.LIT_CHAR, String.valueOf(c));
        }
    }

    // --- 3. Interpreter (Parser + Execution) ---
    private final List<Token> tokens;
    private int current = 0;
    
    // MEMORY AND OUTPUT
    private final Map<String, Object> variables = new HashMap<>();
    private final StringBuilder outputBuffer = new StringBuilder();
    private boolean shouldExecute = true; 

    public TASTE(List<Token> tokens) {
        this.tokens = tokens;
    }
    
    public String getOutput() { return outputBuffer.toString(); }

    public void parse() {
        while (!isAtEnd()) {
            parseState();
        }
    }

    private void parseState() {
        if (checkType(TokenType.KEY_GRAB)) {
            advance(); // consume grab
            parseDecl();
        } else if (checkType(TokenType.KEY_CHUNK) || checkType(TokenType.KEY_SLURP) || 
            checkType(TokenType.KEY_WRAP) || checkType(TokenType.KEY_TASTE)) {
            parseDecl();
        } else if (checkType(TokenType.ID)) {
            parseAssign();
        } else if (checkType(TokenType.KEY_CHEW)) {
            parseCond();
        } else if (checkType(TokenType.KEY_NIBBLE)) {
            // Should not happen at top level if properly structured, but if it does, it's an error or handled in parseCond
             throw new RuntimeException("Syntax Error: Unexpected nibble without chew");
        } else if (checkType(TokenType.KEY_BURP)) {
            parsePrint();
        } else if (checkType(TokenType.KEY_SWALLOW)) { 
            parseInput();
        } else {
            throw new RuntimeException("Syntax Error: Unexpected " + peek().type);
        }
    }

    private void parseInput() {
        consume(TokenType.KEY_SWALLOW, "Expected 'swallow'");
        Token id = consume(TokenType.ID, "Expected variable name");
        
        if (shouldExecute) {
            String input = JOptionPane.showInputDialog("Enter value for " + id.value + ":");
            if (input == null) input = ""; 
            
            Object val;
            if (input.equalsIgnoreCase("Yum")) val = true;
            else if (input.equalsIgnoreCase("Yuck")) val = false;
            else {
                try { val = Integer.parseInt(input); } 
                catch (NumberFormatException e1) {
                    try { val = Double.parseDouble(input); } 
                    catch (NumberFormatException e2) { val = input; }
                }
            }
            variables.put(id.value, val);
        }
    }

    private void parseDecl() {
        TokenType type = parseDatype(); 
        Token nameToken = consume(TokenType.ID, "Expected ID");
        Object value = null;
        if (match(TokenType.ASSIGN)) {
            value = parseExpr();
        }
        if (shouldExecute) {
            if (value != null) {
                if (type == TokenType.KEY_CHUNK && value instanceof Number) {
                    value = ((Number)value).intValue();
                } else if (type == TokenType.KEY_SLURP && value instanceof Number) {
                    value = ((Number)value).doubleValue();
                } else if (type == TokenType.KEY_WRAP) {
                    value = String.valueOf(value);
                }
            }
            variables.put(nameToken.value, value); 
        }
    }

    private void parseAssign() {
        Token nameToken = consume(TokenType.ID, "Expected ID");
        consume(TokenType.ASSIGN, "Expected =");
        Object value = parseExpr();
        if (shouldExecute) {
            variables.put(nameToken.value, value);
        }
    }

    private void parseCond() {
        consume(TokenType.KEY_CHEW, "Expected chew");
        Object condition = parseExpr();
        boolean conditionTrue = Boolean.TRUE.equals(condition);
        boolean previousExecute = shouldExecute; 
        
        if (shouldExecute) shouldExecute = conditionTrue;
        parseBloco();
        shouldExecute = previousExecute; 
        
        boolean hasExecutedBlock = conditionTrue;

        while (match(TokenType.KEY_NIBBLE)) {
             Object nibbleCond = parseExpr();
             boolean nibbleTrue = Boolean.TRUE.equals(nibbleCond);
             
             boolean runNibble = previousExecute && !hasExecutedBlock && nibbleTrue;
             
             boolean temp = shouldExecute;
             shouldExecute = runNibble;
             parseBloco();
             shouldExecute = temp;
             
             if (runNibble) hasExecutedBlock = true;
             // If we are not executing (previousExecute is false), hasExecutedBlock doesn't matter much for logic correctness 
             // but if we were executing, and we found a true block, we mark it.
             // Actually, if previousExecute is true, hasExecutedBlock tracks if we found a true branch.
             if (previousExecute && nibbleTrue) hasExecutedBlock = true;
        }
        
        if (match(TokenType.KEY_SPIT)) {
             boolean runElse = previousExecute && !hasExecutedBlock;
             boolean temp = shouldExecute;
             shouldExecute = runElse;
             parseBloco();
             shouldExecute = temp;
        }
    }

    // --- Helper for String Interpolation ---
    // Scans text for {varName} and replaces with value if variable exists
    private String interpolate(String text) {
        StringBuilder sb = new StringBuilder();
        int length = text.length();
        for (int i = 0; i < length; i++) {
            if (text.charAt(i) == '{') {
                int end = text.indexOf('}', i);
                if (end != -1) {
                    String key = text.substring(i + 1, end);
                    // Only replace if key is not empty ({} is for positional args)
                    // and if we actually have that variable defined
                    if (!key.isEmpty() && variables.containsKey(key)) {
                        sb.append(variables.get(key));
                        i = end; // Skip past the closing brace
                        continue;
                    }
                }
            }
            sb.append(text.charAt(i));
        }
        return sb.toString();
    }

    private void parsePrint() {
        consume(TokenType.KEY_BURP, "Expected burp");
        
        List<Object> args = new ArrayList<>();
        args.add(parseExpr());
        
        while (match(TokenType.COMMA)) {
            args.add(parseExpr());
        }

        if (shouldExecute) {
            StringBuilder finalOutput = new StringBuilder();
            Object first = args.get(0);

            if (first instanceof String) {
                String template = (String) first;

                // 1. Perform Direct Interpolation: "Hello {name}"
                template = interpolate(template);

                // 2. Perform Positional Replacement if needed: "Hello {}", name
                if (template.contains("{}") && args.size() > 1) {
                    int argIndex = 1; 
                    for (int i = 0; i < template.length(); i++) {
                        if (i + 1 < template.length() && template.charAt(i) == '{' && template.charAt(i+1) == '}') {
                            if (argIndex < args.size()) {
                                finalOutput.append(args.get(argIndex++)); 
                            } else {
                                finalOutput.append("{}");
                            }
                            i++; // Skip '}'
                        } else {
                            finalOutput.append(template.charAt(i));
                        }
                    }
                } else {
                    // No positional placeholders, just use the interpolated string
                    finalOutput.append(template);
                    
                    // If there are extra args (comma separated), append them with spaces
                    if (args.size() > 1) {
                        for (int i = 1; i < args.size(); i++) {
                            finalOutput.append(" ").append(args.get(i));
                        }
                    }
                }
            } else {
                // Not a string template, just print args separated by space
                for (int i = 0; i < args.size(); i++) {
                    if (i > 0) finalOutput.append(" ");
                    finalOutput.append(args.get(i));
                }
            }
            outputBuffer.append(finalOutput).append("\n");
        }
    }

    private void parseBloco() {
        consume(TokenType.LBRACE, "Expected {");
        while (!checkType(TokenType.RBRACE) && !isAtEnd()) {
            parseState();
        }
        consume(TokenType.RBRACE, "Expected }");
    }

    private TokenType parseDatype() {
        if (match(TokenType.KEY_CHUNK)) return TokenType.KEY_CHUNK;
        if (match(TokenType.KEY_SLURP)) return TokenType.KEY_SLURP;
        if (match(TokenType.KEY_WRAP)) return TokenType.KEY_WRAP;
        if (match(TokenType.KEY_TASTE)) return TokenType.KEY_TASTE;
        throw new RuntimeException("Expected Data Type");
    }

    private Object parseExpr() {
        Object left = parseRelter();
        while (checkType(TokenType.OP_LOG_AND) || checkType(TokenType.OP_LOG_OTHER)) {
            Token op = advance();
            Object right = parseRelter();
            if (shouldExecute) {
                if (op.type == TokenType.OP_LOG_AND) left = (Boolean)left && (Boolean)right;
                else left = (Boolean)left || (Boolean)right; 
            }
        }
        return left;
    }

    private Object parseRelter() {
        Object left = parseAriExpr();
        if (match(TokenType.OP_REL_GT)) {
            Object right = parseAriExpr();
            if (shouldExecute) return ((Number)left).doubleValue() > ((Number)right).doubleValue();
        } else if (match(TokenType.OP_REL_LT)) {
            Object right = parseAriExpr();
            if (shouldExecute) return ((Number)left).doubleValue() < ((Number)right).doubleValue();
        } else if (match(TokenType.OP_EQ)) { 
            Object right = parseAriExpr();
            if (shouldExecute) return areEqual(left, right);
        } else if (match(TokenType.OP_NEQ)) { 
            Object right = parseAriExpr();
            if (shouldExecute) return !areEqual(left, right);
        }
        return left;
    }

    private boolean areEqual(Object a, Object b) {
        if (a instanceof Number && b instanceof Number) {
            return ((Number)a).doubleValue() == ((Number)b).doubleValue();
        }
        return a.equals(b);
    }

    private Object parseAriExpr() {
        Object left = parseTerm();
        while (checkType(TokenType.OP_ADD) || checkType(TokenType.OP_SUB)) {
            Token op = advance();
            Object right = parseTerm();
            if (shouldExecute) {
                if (op.type == TokenType.OP_ADD) {
                    if (left instanceof String || right instanceof String) {
                        left = left.toString() + right.toString();
                    } else if (left instanceof Integer && right instanceof Integer) {
                        left = (Integer)left + (Integer)right;
                    } else {
                        left = ((Number)left).doubleValue() + ((Number)right).doubleValue();
                    }
                } else {
                    if (left instanceof Integer && right instanceof Integer) {
                        left = (Integer)left - (Integer)right;
                    } else {
                        left = ((Number)left).doubleValue() - ((Number)right).doubleValue();
                    }
                }
            }
        }
        return left;
    }

    private Object parseTerm() {
        Object left = parseFactor();
        while (match(TokenType.OP_MULT, TokenType.OP_DIV, TokenType.OP_MOD)) {
            Token op = tokens.get(current - 1); 
            Object right = parseFactor();
            if (shouldExecute) {
                if (op.type == TokenType.OP_MULT) {
                    if (left instanceof Integer && right instanceof Integer) {
                        left = (Integer)left * (Integer)right;
                    } else {
                        left = ((Number)left).doubleValue() * ((Number)right).doubleValue();
                    }
                } else if (op.type == TokenType.OP_DIV) {
                    if (left instanceof Integer && right instanceof Integer) {
                        left = (Integer)left / (Integer)right;
                    } else {
                        left = ((Number)left).doubleValue() / ((Number)right).doubleValue();
                    }
                } else {
                    if (left instanceof Integer && right instanceof Integer) {
                        left = (Integer)left % (Integer)right;
                    } else {
                        left = ((Number)left).doubleValue() % ((Number)right).doubleValue();
                    }
                }
            }
        }
        return left;
    }

    private Object parseFactor() {
        if (match(TokenType.OP_SUB)) {
            Object val = parseFactor();
            if (shouldExecute) {
                if (val instanceof Integer) return -(Integer)val;
                if (val instanceof Double) return -(Double)val;
                throw new RuntimeException("Unary minus only applies to numbers");
            }
            return 0;
        } else if (match(TokenType.LPAREN)) {
            Object val = parseExpr();
            consume(TokenType.RPAREN, "Expected )");
            return val;
        } else if (checkType(TokenType.ID)) {
            String name = advance().value;
            if (shouldExecute) return variables.get(name);
            return 0; 
        } else {
            return parseValue();
        }
    }

    private Object parseValue() {
        if (checkType(TokenType.LIT_INT)) return Integer.parseInt(advance().value);
        if (checkType(TokenType.LIT_FLOAT)) return Double.parseDouble(advance().value);
        if (checkType(TokenType.LIT_STRING)) return advance().value;
        if (checkType(TokenType.VAL_YUM)) { advance(); return true; }
        if (checkType(TokenType.VAL_YUCK)) { advance(); return false; }
        throw new RuntimeException("Expected Value");
    }

    private Token peek() { return tokens.get(current); }
    private boolean isAtEnd() { return peek().type == TokenType.EOF; }
    private Token advance() { if (!isAtEnd()) current++; return tokens.get(current - 1); }
    private boolean checkType(TokenType t) { return !isAtEnd() && peek().type == t; }
    private boolean match(TokenType... types) {
        for (TokenType t : types) if (checkType(t)) { advance(); return true; }
        return false;
    }
    private Token consume(TokenType t, String msg) { if (checkType(t)) return advance(); throw new RuntimeException(msg); }

    // --- 4. Runner ---
    public static String run(File file) {
        if (!file.getName().endsWith(".sauce")) {
            return "❌ INDIGESTION (Error): The chef only cooks with .sauce files!";
        }

        String sourceCode;
        try {
            sourceCode = new String(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            return "❌ INDIGESTION (Error): Could not read the recipe.\n" + e.getMessage();
        }

        if (sourceCode.trim().isEmpty()) {
            return "⚠️ Plate is empty! Write some code first.";
        }

        StringBuilder resultBuilder = new StringBuilder();
        resultBuilder.append("--- Program Started ---\n");

        try {
            // 1. Lexer
            TASTE.Lexer lexer = new TASTE.Lexer(sourceCode);
            List<TASTE.Token> tokens = lexer.tokenize();

            // 2. Parser & Interpreter
            TASTE parser = new TASTE(tokens);
            parser.parse();

            // 3. Get Output
            String result = parser.getOutput();
            resultBuilder.append(result);
            resultBuilder.append("\n--- Execution Finished (Bon Appetit!) ---");

        } catch (Exception ex) {
            resultBuilder.append("\n❌ INDIGESTION (Error):\n").append(ex.getMessage());
        }
        
        return resultBuilder.toString();
    }
}
