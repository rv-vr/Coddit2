package taste;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TASTE {
    
    public static class RunResult {
        private final String output;
        private final Map<String, Object> variables;

        public RunResult(String output, Map<String, Object> variables) {
            this.output = output;
            this.variables = variables;
        }

        public String getOutput() {
            return output;
        }

        public Map<String, Object> getVariables() {
            return variables;
        }
    }

    public static RunResult run(File file, boolean funErrorCodes) {
        Map<String, Object> emptyVars = new HashMap<>();
        
        if (!file.getName().endsWith(".sauce")) {
            return new RunResult("❌ INDIGESTION (Error): The chef only cooks with .sauce files!", emptyVars);
        }

        String sourceCode;
        try {
            sourceCode = new String(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            return new RunResult("❌ INDIGESTION (Error): Could not read the recipe.\n" + e.getMessage(), emptyVars);
        }

        if (sourceCode.trim().isEmpty()) {
            return new RunResult("⚠️ Plate is empty! Write some code first.", emptyVars);
        }

        StringBuilder resultBuilder = new StringBuilder();
        resultBuilder.append("--- Program Started ---\n");

        try {
            // 1. Lexer
            Lexer lexer = new Lexer(sourceCode, funErrorCodes);
            List<Token> tokens = lexer.tokenize();

            // 2. Interpreter
            Interpreter interpreter = new Interpreter(tokens, file.getName(), funErrorCodes);
            interpreter.interpret();

            // 3. Get Output
            resultBuilder.append(interpreter.getOutput());
            resultBuilder.append("\n--- Execution Finished (Bon Appetit!) ---");
            
            return new RunResult(resultBuilder.toString(), interpreter.getVariables());

        } catch (Exception ex) {
            resultBuilder.append("\n❌ INDIGESTION (Error):\n").append(ex.getMessage());
            return new RunResult(resultBuilder.toString(), emptyVars);
        }
    }
}