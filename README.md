# TASTE Language Documentation

**TASTE** (Theoretical Algorithm Structure Translation Engine) is a flavor-themed programming language designed for delicious code execution.

## 📖 Grammar Specification

```ebnf
/* --- Top Level --- */
Program      ::= Statement* EOF

/* --- Statements --- */
Statement    ::= Declaration
               | Assignment
               | Conditional
               | Print
               | Input

/* --- Variable Operations --- */
Declaration  ::= DataType ID ( "=" Expression )?
Assignment   ::= ID "=" Expression

/* --- I/O Operations --- */
Print        ::= "burp" Expression ( "," Expression )*
Input        ::= "swallow" ID

/* --- Control Flow --- */
Conditional  ::= "chew" Expression Block 
                 ( "nibble" Expression Block )* 
                 ( "spit" Block )?

Block        ::= "{" Statement* "}"

/* --- Data Types & Identifiers --- */
DataType     ::= "Chunk"   /* Integer */
               | "Slurp"   /* Float */
               | "Wrap"    /* String */
               | "Taste"   /* Boolean */

ID           ::= [a-zA-Z_] [a-zA-Z0-9_]*

/* --- Expressions --- */
Expression   ::= LogicExpr

LogicExpr    ::= RelExpr ( ( "&" | "><" ) RelExpr )*
RelExpr      ::= AddExpr ( ( ">" | "<" | "==" | "!=" ) AddExpr )?
AddExpr      ::= Term ( ( "+" | "-" ) Term )*
Term         ::= Factor ( ( "*" | "/" | "%" ) Factor )

Factor       ::= "(" Expression ")"
               | ID
               | Literal
               | "-" Factor

Literal      ::= Integer
               | Float
               | String
               | "Yum"     /* True */
               | "Yuck"    /* False */
```

## 🍽️ Language Features

### Data Types
| Type | Keyword | Description | Example |
|------|---------|-------------|---------|
| Integer | `Chunk` | Whole numbers | `Chunk x = 10` |
| Float | `Slurp` | Decimal numbers | `Slurp pi = 3.14` |
| String | `Wrap` | Text | `Wrap s = "Hello"` |
| Boolean | `Taste` | True/False | `Taste isGood = Yum` |

### Keywords
*   **`Yum` / `Yuck`**: Boolean literals for `True` and `False`.
*   **`burp`**: Prints output to the console.
*   **`swallow`**: Takes input from the user.
*   **`chew`**: Starts an `if` statement.
*   **`nibble`**: Represents `else if`.
*   **`spit`**: Represents `else`.

### Operators
*   **Arithmetic**: `+`, `-`, `*`, `/`, `%`
*   **Comparison**: `>`, `<`, `==`, `!=`
*   **Logical**: 
    *   `&` (AND)
    *   `><` (OR)

## 👨‍🍳 Examples

### 1. Hello World
```taste
burp "Hello, World!"
```

### 2. Variables & Math
```taste
Chunk a = 10
Chunk b = 20
Chunk result = a + b * 2
burp "The result is: ", result
```

## 🚨 Error Codes

TASTE features two error modes: **Fun** (Default) and **Generic**. You can toggle between them in the IDE menu.

| Error Type | Fun Message (Default) | Generic Message |
| :--- | :--- | :--- |
| **Lexer Error** | `LEXICAL INDIGESTION: What is this garbage?` | `Syntax Error: Unknown character` |
| **Missing Variable** | `The ingredient 'x' was not found. You cannot serve what you have not prepared!` | `Variable 'x' not found` |
| **Type Mismatch (Math)** | `You can't mix those ingredients!` | `InvalidDataType: Cannot subtract...` |
| **Type Mismatch (Unary)** | `You can't negate a sandwich!` | `Unary minus only applies to numbers` |
| **Missing Assignment** | `Where's the sauce? (Expected '=')` | `Expected '='` |
| **Missing Identifier** | `What are we grabbing? (Expected Identifier)` | `Expected Identifier` |
| **Missing 'chew'** | `You need to chew first!` | `Expected 'chew'` |
| **Orphaned 'nibble'** | `You can't nibble if you haven't chewed!` | `Unexpected 'nibble' without 'chew'` |
| **Orphaned 'spit'** | `Don't spit if you haven't chewed!` | `Unexpected 'spit' without 'chew'` |
| **Missing 'burp'** | `Let it out! (Expected 'burp')` | `Expected 'burp'` |
| **Missing 'swallow'** | `Open wide! (Expected 'swallow')` | `Expected 'swallow'` |
| **Missing Brace '{'** | `Open the lunchbox! (Expected '{')` | `Expected '{'` |
| **Missing Brace '}'** | `Close the lunchbox! (Expected '}')` | `Expected '}'` |
| **Missing Value** | `Plate is empty! (Expected Value)` | `Expected Value` |
| **Unknown Token** | `I don't know what this flavor is` | `Unexpected token` |
| **Unknown Type** | `What kind of food is this?` | `Expected Data Type` |

### 3. Conditionals (The Taste Test)
```taste
Taste isHungry = Yum

chew isHungry {
    burp "Time to eat!"
} spit {
    burp "Maybe later."
}
```

### 4. Input & Logic
```taste
Wrap name = ""
burp "What is your name?"
swallow name

chew name == "Chef" {
    burp "Welcome back, Chef!"
} nibble name == "Critic" {
    burp "Oh no, hide the sauce!"
} spit {
    burp "Hello, ", name
}
```