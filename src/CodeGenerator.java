import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class CodeGenerator {
    private final String className;
    private int indentLevel = 2;

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
        indentLevel = 2;

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

        if (stmt instanceof IfNode) {
            return generateIf((IfNode) stmt);
        }

        throw new IllegalArgumentException("Unknown statement type: " + stmt.getClass().getSimpleName());
    }

    private String generateDeclaration(DeclarationNode node) {
        String javaType = mapType(node.getTypeName());
        String javaName = node.getVariableName();
        String initializer;

        if (node.getExpression() != null) {
            initializer = generateExpression(node.getExpression());
        } else {
            initializer = defaultValue(node.getTypeName());
        }

        return indent() + javaType + " " + javaName + " = " + initializer + ";\n";
    }

    private String generateAssignment(AssignmentNode node) {
        String javaName = node.getVariableName();
        String javaExpr = generateExpression(node.getExpression());
        return indent() + javaName + " = " + javaExpr + ";\n";
    }

    private String generateIf(IfNode node) {
        StringBuilder sb = new StringBuilder();

        String condition = generateExpression(node.getCondition());
        sb.append(indent()).append("if (").append(condition).append(") {\n");

        indentLevel++;
        for (StmtNode stmt : node.getThenBranch()) {
            sb.append(generateStatement(stmt));
        }
        indentLevel--;

        sb.append(indent()).append("}");

        if (node.hasElse()) {
            sb.append(" else {\n");
            indentLevel++;
            for (StmtNode stmt : node.getElseBranch()) {
                sb.append(generateStatement(stmt));
            }
            indentLevel--;
            sb.append(indent()).append("}");
        }

        sb.append("\n");
        return sb.toString();
    }

    private String generateExpression(ExprNode expr) {
        if (expr instanceof BinaryNode) {
            BinaryNode node = (BinaryNode) expr;
            String left  = generateExpression(node.getLeft());
            String right = generateExpression(node.getRight());
            return "(" + left + " " + node.getOperator() + " " + right + ")";
        }

        if (expr instanceof LiteralNode) {
            return generateLiteral((LiteralNode) expr);
        }

        if (expr instanceof VariableNode) {
            return ((VariableNode) expr).getName();
        }

        throw new IllegalArgumentException("Unknown expression type: " + expr.getClass().getSimpleName());
    }

    private String generateLiteral(LiteralNode node) {
        if (node.getLiteralType() == TokenType.STRING) {
            return "\"" + escapeJavaString(node.getValue()) + "\"";
        }

        if (node.getLiteralType() == TokenType.NUMBER) {
            return normalizeBanglaDigits(node.getValue());
        }

        return node.getValue();
    }

    private String mapType(String banglaType) {
        if ("সংখ্যা".equals(banglaType)) return "double";
        if ("বাক্য".equals(banglaType))  return "String";
        throw new IllegalArgumentException("Unsupported Bangla type: " + banglaType);
    }

    private String defaultValue(String banglaType) {
        if ("সংখ্যা".equals(banglaType)) return "0";
        if ("বাক্য".equals(banglaType))  return "\"\"";
        return "null";
    }

    private String indent() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < indentLevel; i++) {
            sb.append("    ");
        }
        return sb.toString();
    }

    private String escapeJavaString(String value) {
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\t", "\\t");
    }

    private String normalizeBanglaDigits(String input) {
        if (input == null) return "";
        StringBuilder sb = new StringBuilder();
        for (char ch : input.toCharArray()) {
            if (ch >= '\u09E6' && ch <= '\u09EF') {
                sb.append((char) ('0' + (ch - '\u09E6')));
            } else {
                sb.append(ch);
            }
        }
        return sb.toString();
    }
}