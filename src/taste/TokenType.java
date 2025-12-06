package taste;

public enum TokenType {
    // Keywords
    KEY_INTEGER,    // Chunk
    KEY_FLOAT,      // Slurp
    KEY_STRING,     // Wrap
    KEY_BOOLEAN,    // Taste
    KEY_PRINT,      // burp
    KEY_INPUT,      // swallow
    KEY_DECLARE,    // grab
    KEY_IF,         // chew
    KEY_ELSE,       // spit
    KEY_ELSE_IF,    // nibble
    
    // Values
    VAL_TRUE,       // Yum
    VAL_FALSE,      // Yuck
    
    // Delimiters
    LBRACE,         // {
    RBRACE,         // }
    LPAREN,         // (
    RPAREN,         // )
    ASSIGN,         // =
    COMMA,          // ,
    
    // Operators
    OP_ADD,         // +
    OP_SUB,         // -
    OP_MULT,        // *
    OP_DIV,         // /
    OP_MOD,         // %
    
    // Comparison
    OP_EQ,          // ==
    OP_NEQ,         // !=
    OP_GT,          // >
    OP_LT,          // <
    
    // Logical
    OP_AND,         // &
    OP_OR,          // ><
    
    // Literals & Identifiers
    IDENTIFIER, 
    LIT_INTEGER, 
    LIT_FLOAT, 
    LIT_STRING, 
    LIT_CHAR, 
    
    EOF
}
