package taste;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;

public class Interpreter {
    private final List<Token> tokens;
    private final String filename;
    private final boolean funErrorCodes;
    private int current = 0;
    
    private final Map<String, Object> variables = new HashMap<>();
    private final StringBuilder outputBuffer = new StringBuilder();
    private boolean shouldExecute = true;

    public Interpreter(List<Token> tokens, String filename, boolean funErrorCodes) {
        this.tokens = tokens;
        this.filename = filename;
        this.funErrorCodes = funErrorCodes;
    }

    public void interpret() {
        while (!isAtEnd()) {
            parseStatement();
        }
    }

    public String getOutput() {
        return outputBuffer.toString();
    }

    public Map<String, Object> getVariables() {
        return variables;
    }

    private void parseStatement() {
        Token token = peek();
        switch (token.getType()) {
            case KEY_INTEGER:
            case KEY_FLOAT:
            case KEY_STRING:
            case KEY_BOOLEAN:
                parseDeclaration();
                break;
            case IDENTIFIER:
                parseAssignment();
                break;
            case KEY_IF:
                parseConditional();
                break;
            case KEY_PRINT:
                parsePrint();
                break;
            case KEY_INPUT:
                parseInput();
                break;
            case KEY_ELSE_IF:
                throw error("You can't nibble if you haven't chewed!", "Unexpected 'nibble' without 'chew'");
            case KEY_ELSE:
                throw error("Don't spit if you haven't chewed!", "Unexpected 'spit' without 'chew'");
            default:
                throw error("I don't know what this flavor is: " + token.getType(), "Unexpected token: " + token.getType());
        }
    }

    private void parseDeclaration() {
        TokenType type = parseDataType();
        Token nameToken = consume(TokenType.IDENTIFIER, "What are we grabbing? (Expected Identifier)", "Expected Identifier");
        Object value = null;
        
        if (match(TokenType.ASSIGN)) {
            value = parseExpression();
        }
        
        if (shouldExecute) {
            if (value != null) {
                value = castValue(type, value);
            }
            variables.put(nameToken.getValue(), value);
        }
    }

    private Object castValue(TokenType type, Object value) {
        if (type == TokenType.KEY_INTEGER && value instanceof Number) {
            return ((Number) value).intValue();
        }
        if (type == TokenType.KEY_FLOAT && value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        if (type == TokenType.KEY_STRING) {
            return String.valueOf(value);
        }
        return value;
    }

    private void parseAssignment() {
        Token nameToken = consume(TokenType.IDENTIFIER, "Who are we serving? (Expected Identifier)", "Expected Identifier");
        consume(TokenType.ASSIGN, "Where's the sauce? (Expected '=')", "Expected '='");
        Object value = parseExpression();
        
        if (shouldExecute) {
            variables.put(nameToken.getValue(), value);
        }
    }

    private void parseConditional() {
        consume(TokenType.KEY_IF, "You need to chew first!", "Expected 'chew'");
        Object condition = parseExpression();
        boolean conditionTrue = Boolean.TRUE.equals(condition);
        boolean previousExecute = shouldExecute;
        
        if (shouldExecute) shouldExecute = conditionTrue;
        parseBlock();
        shouldExecute = previousExecute;
        
        boolean hasExecutedBlock = conditionTrue;

        while (match(TokenType.KEY_ELSE_IF)) {
            Object nibbleCond = parseExpression();
            boolean nibbleTrue = Boolean.TRUE.equals(nibbleCond);
            
            boolean runNibble = previousExecute && !hasExecutedBlock && nibbleTrue;
            
            boolean temp = shouldExecute;
            shouldExecute = runNibble;
            parseBlock();
            shouldExecute = temp;
            
            if (previousExecute && nibbleTrue) hasExecutedBlock = true;
        }
        
        if (match(TokenType.KEY_ELSE)) {
            boolean runElse = previousExecute && !hasExecutedBlock;
            boolean temp = shouldExecute;
            shouldExecute = runElse;
            parseBlock();
            shouldExecute = temp;
        }
    }

    private void parsePrint() {
        Token printToken = consume(TokenType.KEY_PRINT, "Let it out! (Expected 'burp')", "Expected 'burp'");
        List<Object> args = new ArrayList<>();
        args.add(parseExpression());
        
        while (match(TokenType.COMMA)) {
            args.add(parseExpression());
        }

        if (shouldExecute) {
            processPrintOutput(args, printToken.getLine());
        }
    }

    private void processPrintOutput(List<Object> args, int line) {
        StringBuilder finalOutput = new StringBuilder();
        Object first = args.get(0);

        if (first instanceof String) {
            String template = (String) first;
            template = interpolate(template, line);

            if (template.contains("{}") && args.size() > 1) {
                finalOutput.append(formatString(template, args));
            } else {
                finalOutput.append(template);
                for (int i = 1; i < args.size(); i++) {
                    finalOutput.append(" ").append(args.get(i));
                }
            }
        } else {
            for (int i = 0; i < args.size(); i++) {
                if (i > 0) finalOutput.append(" ");
                finalOutput.append(args.get(i));
            }
        }
        outputBuffer.append(finalOutput).append("\n");
    }

    private String formatString(String template, List<Object> args) {
        StringBuilder sb = new StringBuilder();
        int argIndex = 1;
        for (int i = 0; i < template.length(); i++) {
            if (i + 1 < template.length() && template.charAt(i) == '{' && template.charAt(i + 1) == '}') {
                if (argIndex < args.size()) {
                    sb.append(args.get(argIndex++));
                } else {
                    sb.append("{}");
                }
                i++; // Skip '}'
            } else {
                sb.append(template.charAt(i));
            }
        }
        return sb.toString();
    }

    private String interpolate(String text, int line) {
        StringBuilder sb = new StringBuilder();
        int length = text.length();
        for (int i = 0; i < length; i++) {
            if (text.charAt(i) == '{') {
                int end = text.indexOf('}', i);
                if (end != -1) {
                    String key = text.substring(i + 1, end);
                    if (!key.isEmpty()) {
                        if (variables.containsKey(key)) {
                            sb.append(variables.get(key));
                            i = end;
                            continue;
                        } else {
                             if (funErrorCodes) {
                                 throw new RuntimeException(String.format(
                                    "ERROR in %s (Line %d):\nThe ingredient '%s' was not found.\nYou cannot serve what you have not prepared!",
                                    filename, line, key
                                ));
                             } else {
                                 throw new RuntimeException(String.format(
                                    "Line %d: Variable '%s' not found",
                                    line, key
                                ));
                             }
                        }
                    }
                }
            }
            sb.append(text.charAt(i));
        }
        return sb.toString();
    }

    private void parseInput() {
        consume(TokenType.KEY_INPUT, "Open wide! (Expected 'swallow')", "Expected 'swallow'");
        Token id = consume(TokenType.IDENTIFIER, "Swallow what? (Expected Variable)", "Expected Variable");
        
        if (shouldExecute) {
            String input = JOptionPane.showInputDialog("Enter value for " + id.getValue() + ":");
            if (input == null) input = "";
            
            Object val = parseInputValue(input);
            variables.put(id.getValue(), val);
        }
    }

    private Object parseInputValue(String input) {
        if (input.equalsIgnoreCase("Yum")) return true;
        if (input.equalsIgnoreCase("Yuck")) return false;
        try { return Integer.parseInt(input); } 
        catch (NumberFormatException e1) {
            try { return Double.parseDouble(input); } 
            catch (NumberFormatException e2) { return input; }
        }
    }

    private void parseBlock() {
        consume(TokenType.LBRACE, "Open the lunchbox! (Expected '{')", "Expected '{'");
        while (!checkType(TokenType.RBRACE) && !isAtEnd()) {
            parseStatement();
        }
        consume(TokenType.RBRACE, "Close the lunchbox! (Expected '}')", "Expected '}'");
    }

    private TokenType parseDataType() {
        if (match(TokenType.KEY_INTEGER)) return TokenType.KEY_INTEGER;
        if (match(TokenType.KEY_FLOAT)) return TokenType.KEY_FLOAT;
        if (match(TokenType.KEY_STRING)) return TokenType.KEY_STRING;
        if (match(TokenType.KEY_BOOLEAN)) return TokenType.KEY_BOOLEAN;
        throw error("What kind of food is this? (Expected Type)", "Expected Data Type");
    }

    // --- Expression Parsing ---

    private Object parseExpression() {
        Object left = parseLogicalTerm();
        while (checkType(TokenType.OP_AND) || checkType(TokenType.OP_OR)) {
            Token op = advance();
            Object right = parseLogicalTerm();
            if (shouldExecute) {
                if (op.getType() == TokenType.OP_AND) left = (Boolean) left && (Boolean) right;
                else left = (Boolean) left || (Boolean) right;
            }
        }
        return left;
    }

    private Object parseLogicalTerm() {
        Object left = parseArithmeticExpression();
        if (match(TokenType.OP_GT)) {
            Object right = parseArithmeticExpression();
            if (shouldExecute) return ((Number) left).doubleValue() > ((Number) right).doubleValue();
        } else if (match(TokenType.OP_LT)) {
            Object right = parseArithmeticExpression();
            if (shouldExecute) return ((Number) left).doubleValue() < ((Number) right).doubleValue();
        } else if (match(TokenType.OP_EQ)) {
            Object right = parseArithmeticExpression();
            if (shouldExecute) return areEqual(left, right);
        } else if (match(TokenType.OP_NEQ)) {
            Object right = parseArithmeticExpression();
            if (shouldExecute) return !areEqual(left, right);
        }
        return left;
    }

    private boolean areEqual(Object a, Object b) {
        if (a instanceof Number && b instanceof Number) {
            return ((Number) a).doubleValue() == ((Number) b).doubleValue();
        }
        return a.equals(b);
    }

    private Object parseArithmeticExpression() {
        Object left = parseTerm();
        while (checkType(TokenType.OP_ADD) || checkType(TokenType.OP_SUB)) {
            Token op = advance();
            Object right = parseTerm();
            if (shouldExecute) {
                left = performArithmetic(left, right, op.getType());
            }
        }
        return left;
    }

    private Object performArithmetic(Object left, Object right, TokenType op) {
        if (op == TokenType.OP_ADD) {
            if (left instanceof String || right instanceof String) {
                return left.toString() + right.toString();
            } else if (left instanceof Integer && right instanceof Integer) {
                return (Integer) left + (Integer) right;
            } else {
                return ((Number) left).doubleValue() + ((Number) right).doubleValue();
            }
        } else {
            if (!(left instanceof Number) || !(right instanceof Number)) {
                throw error("You can't mix those ingredients! (Cannot subtract " + getType(right) + " from " + getType(left) + ")", 
                          "InvalidDataType: Cannot subtract " + getType(right) + " from " + getType(left));
            }
            if (left instanceof Integer && right instanceof Integer) {
                return (Integer) left - (Integer) right;
            } else {
                return ((Number) left).doubleValue() - ((Number) right).doubleValue();
            }
        }
    }

    private String getType(Object obj) {
        if (obj instanceof Integer) return "Integer";
        if (obj instanceof Double) return "Float";
        if (obj instanceof String) return "String";
        if (obj instanceof Boolean) return "Boolean";
        return "Unknown";
    }

    private Object parseTerm() {
        Object left = parseFactor();
        while (match(TokenType.OP_MULT, TokenType.OP_DIV, TokenType.OP_MOD)) {
            Token op = tokens.get(current - 1);
            Object right = parseFactor();
            if (shouldExecute) {
                left = performMultiplicative(left, right, op.getType());
            }
        }
        return left;
    }

    private Object performMultiplicative(Object left, Object right, TokenType op) {
        if (!(left instanceof Number) || !(right instanceof Number)) {
            throw error("Bad recipe mix! (Cannot perform math on " + getType(left) + " and " + getType(right) + ")",
                      "InvalidDataType: Cannot perform math on " + getType(left) + " and " + getType(right));
        }
        if (op == TokenType.OP_MULT) {
            if (left instanceof Integer && right instanceof Integer) {
                return (Integer) left * (Integer) right;
            } else {
                return ((Number) left).doubleValue() * ((Number) right).doubleValue();
            }
        } else if (op == TokenType.OP_DIV) {
            return ((Number) left).doubleValue() / ((Number) right).doubleValue();
        } else {
            if (left instanceof Integer && right instanceof Integer) {
                return (Integer) left % (Integer) right;
            } else {
                return ((Number) left).doubleValue() % ((Number) right).doubleValue();
            }
        }
    }

    private Object parseFactor() {
        if (match(TokenType.OP_SUB)) {
            Object val = parseFactor();
            if (shouldExecute) {
                if (val instanceof Integer) return -(Integer) val;
                if (val instanceof Double) return -(Double) val;
                throw error("You can't negate a sandwich! (Unary minus only applies to numbers)", "Unary minus only applies to numbers");
            }
            return 0;
        } else if (match(TokenType.LPAREN)) {
            Object val = parseExpression();
            consume(TokenType.RPAREN, "Close the wrapper! (Expected ')')", "Expected ')'");
            return val;
        } else if (checkType(TokenType.IDENTIFIER)) {
            Token token = advance();
            String name = token.getValue();
            if (shouldExecute) {
                if (!variables.containsKey(name)) {
                    if (funErrorCodes) {
                        throw new RuntimeException(String.format(
                            "ERROR in %s (Line %d):\nThe ingredient '%s' was not found.\nYou cannot serve what you have not prepared!",
                            filename, token.getLine(), name
                        ));
                    } else {
                        throw new RuntimeException(String.format(
                            "Line %d: Variable '%s' not found",
                            token.getLine(), name
                        ));
                    }
                }
                return variables.get(name);
            }
            return 0;
        } else {
            return parseValue();
        }
    }

    private Object parseValue() {
        if (checkType(TokenType.LIT_INTEGER)) return Integer.parseInt(advance().getValue());
        if (checkType(TokenType.LIT_FLOAT)) return Double.parseDouble(advance().getValue());
        if (checkType(TokenType.LIT_STRING)) return advance().getValue();
        if (checkType(TokenType.VAL_TRUE)) { advance(); return true; }
        if (checkType(TokenType.VAL_FALSE)) { advance(); return false; }
        throw error("Plate is empty! (Expected Value)", "Expected Value");
    }

    // --- Helper Methods ---

    private Token peek() {
        return tokens.get(current);
    }

    private boolean isAtEnd() {
        return peek().getType() == TokenType.EOF;
    }

    private Token advance() {
        if (!isAtEnd()) current++;
        return tokens.get(current - 1);
    }

    private boolean checkType(TokenType t) {
        return !isAtEnd() && peek().getType() == t;
    }

    private boolean match(TokenType... types) {
        for (TokenType t : types) {
            if (checkType(t)) {
                advance();
                return true;
            }
        }
        return false;
    }

    private RuntimeException error(String fun, String generic) {
        int line = !tokens.isEmpty() && current < tokens.size() ? tokens.get(current).getLine() : 0;
        if (funErrorCodes) {
            return new RuntimeException("INDIGESTION (Line " + line + "): " + fun);
        } else {
            return new RuntimeException("Error (Line " + line + "): " + generic);
        }
    }

    private Token consume(TokenType t, String funMsg, String genericMsg) {
        if (checkType(t)) return advance();
        throw error(funMsg, genericMsg);
    }
    
    // Legacy support for single message (treat as generic)
    private Token consume(TokenType t, String msg) {
        return consume(t, msg, msg);
    }
}
