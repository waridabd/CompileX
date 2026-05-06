import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class CodeGenerator {
    private final String className;

    public CodeGenerator() {
        this("GeneratedProgram");
    }

    public CodeGenerator(String className) {
        this.className = className;
    }

    public String getClassName() {
        return className;
    }

    public String generate(ProgramNode program) {
        StringBuilder sb = new StringBuilder();

        sb.append("public class ").append(className).append(" {\n");
        sb.append("    public static void main(String[] args) {\n");

        for (StmtNode stmt : program.getStatements()) {
            sb.append(generateStatement(stmt));
        }

        sb.append("    }\n");
        sb.append("}\n");

        return sb.toString();
    }

    public void writeToFile(ProgramNode program, String fileName) throws IOException {
        Files.writeString(Path.of(fileName), generate(program), StandardCharsets.UTF_8);
    }

    private String generateStatement(StmtNode stmt) {
        if (stmt instanceof DeclarationNode) {
            return generateDeclaration((DeclarationNode) stmt);
        }

        if (stmt instanceof AssignmentNode) {
            return generateAssignment((AssignmentNode) stmt);
        }

        throw new IllegalArgumentException("Unknown statement type: " + stmt.getClass().getSimpleName());
    }

    private String generateDeclaration(DeclarationNode node) {
        String javaType = mapType(node.getTypeName());
        String javaName = toJavaIdentifier(node.getVariableName());
        String initializer;

        if (node.getExpression() != null) {
            initializer = generateExpression(node.getExpression());
        } else {
            initializer = defaultValue(node.getTypeName());
        }

        return "        " + javaType + " " + javaName + " = " + initializer + ";\n";
    }

    private String generateAssignment(AssignmentNode node) {
        String javaName = toJavaIdentifier(node.getVariableName());
        String javaExpr = generateExpression(node.getExpression());
        return "        " + javaName + " = " + javaExpr + ";\n";
    }

    private String generateExpression(ExprNode expr) {
        if (expr instanceof BinaryNode) {
            BinaryNode node = (BinaryNode) expr;
            String left = generateExpression(node.getLeft());
            String right = generateExpression(node.getRight());
            return "(" + left + " " + node.getOperator() + " " + right + ")";
        }

        if (expr instanceof LiteralNode) {
            return generateLiteral((LiteralNode) expr);
        }

        if (expr instanceof VariableNode) {
            return toJavaIdentifier(((VariableNode) expr).getName());
        }

        throw new IllegalArgumentException("Unknown expression type: " + expr.getClass().getSimpleName());
    }

    private String generateLiteral(LiteralNode node) {
        if (node.getLiteralType() == TokenType.STRING) {
            String raw = stripQuotes(node.getValue());
            return "\"" + escapeJavaString(raw) + "\"";
        }

        if (node.getLiteralType() == TokenType.NUMBER) {
            return normalizeBanglaDigits(node.getValue());
        }

        return node.getValue();
    }

    private String mapType(String banglaType) {
        if ("সংখ্যা".equals(banglaType)) {
            return "double";
        }

        if ("বাক্য".equals(banglaType)) {
            return "String";
        }

        throw new IllegalArgumentException("Unsupported Bangla type: " + banglaType);
    }

    private String defaultValue(String banglaType) {
        if ("সংখ্যা".equals(banglaType)) {
            return "0";
        }

        if ("বাক্য".equals(banglaType)) {
            return "\"\"";
        }

        return "null";
    }

    private String toJavaIdentifier(String name) {
        return name;
    }

    private String stripQuotes(String value) {
        if (value == null) {
            return "";
        }

        if (value.length() >= 2 && value.startsWith("\"") && value.endsWith("\"")) {
            return value.substring(1, value.length() - 1);
        }

        return value;
    }

    private String escapeJavaString(String value) {
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\t", "\\t");
    }

    private String normalizeBanglaDigits(String input) {
        if (input == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();

        for (char ch : input.toCharArray()) {
            switch (ch) {
                case '০': sb.append('0'); break;
                case '১': sb.append('1'); break;
                case '২': sb.append('2'); break;
                case '৩': sb.append('3'); break;
                case '৪': sb.append('4'); break;
                case '৫': sb.append('5'); break;
                case '৬': sb.append('6'); break;
                case '৭': sb.append('7'); break;
                case '৮': sb.append('8'); break;
                case '৯': sb.append('9'); break;
                default: sb.append(ch);
            }
        }

        return sb.toString();
    }
}