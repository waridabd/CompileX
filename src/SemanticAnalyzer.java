import java.util.ArrayList;
import java.util.List;

public class SemanticAnalyzer {

    private final ProgramNode program;
    private final SymbolTable symbolTable = new SymbolTable();
    private final List<String> errors = new ArrayList<>();

    public SemanticAnalyzer(ProgramNode program) {
        this.program = program;
    }

    public void analyze() {
        for (StmtNode stmt : program.statements) {
            try {
                analyzeStatement(stmt);
            } catch (RuntimeException e) {
                errors.add(e.getMessage());
            }
        }

        System.out.println("\n===== Semantic Analysis =====");
        if (errors.isEmpty()) {
            System.out.println("✓ কোনো সেমান্টিক ত্রুটি নেই");
        } else {
            for (String e : errors) {
                System.out.println(e);
            }
        }

        symbolTable.printTable();
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    private void analyzeStatement(StmtNode stmt) {
        if (stmt instanceof DeclarationNode) {
            analyzeDeclaration((DeclarationNode) stmt);
        } else if (stmt instanceof AssignmentNode) {
            analyzeAssignment((AssignmentNode) stmt);
        }
    }

    private void analyzeDeclaration(DeclarationNode node) {
        String exprType = evaluateExpression(node.value);

        if (!node.type.equals(exprType)) {
            throw new RuntimeException("টাইপ মেলেনি -> " + node.name);
        }

        symbolTable.declare(node.name, node.type);
    }

    private void analyzeAssignment(AssignmentNode node) {
        if (!symbolTable.exists(node.name)) {
            throw new RuntimeException("ঘোষণা করা হয়নি -> " + node.name);
        }

        String exprType = evaluateExpression(node.value);
        String varType = symbolTable.getType(node.name);

        if (!varType.equals(exprType)) {
            throw new RuntimeException("ভুল টাইপ assign -> " + node.name);
        }
    }

    private String evaluateExpression(ExprNode expr) {

        if (expr instanceof LiteralNode) {
            Object val = ((LiteralNode) expr).value;

            if (val instanceof Double) return "সংখ্যা";
            if (val instanceof String) return "বাক্য";
        }

        if (expr instanceof VariableNode) {
            return symbolTable.getType(((VariableNode) expr).name);
        }

        if (expr instanceof BinaryNode) {
            BinaryNode bin = (BinaryNode) expr;

            String left = evaluateExpression(bin.left);
            String right = evaluateExpression(bin.right);

            if (!left.equals("সংখ্যা") || !right.equals("সংখ্যা")) {
                throw new RuntimeException("Arithmetic শুধু সংখ্যা");
            }

            return "সংখ্যা";
        }

        return "";
    }
}