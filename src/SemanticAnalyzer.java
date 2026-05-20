import java.util.List;

public class SemanticAnalyzer {
    private final ProgramNode program;
    private SymbolTable symbolTable = new SymbolTable();

    public SemanticAnalyzer(ProgramNode program) {
        this.program = program;
    }

    public void analyze() {
        try {
            analyzeStatements(program.getStatements());
            System.out.println("✓ সেমান্টিক বিশ্লেষণ সফলভাবে সম্পন্ন হয়েছে।");
        } catch (RuntimeException e) {
            System.out.println("✗ সেমান্টিক ত্রুটি: " + e.getMessage());
        }
    }

    private void analyzeStatements(List<StmtNode> statements) {
        for (StmtNode stmt : statements) {
            if (stmt instanceof DeclarationNode) {
                analyzeDeclaration((DeclarationNode) stmt);
            } else if (stmt instanceof AssignmentNode) {
                analyzeAssignment((AssignmentNode) stmt);
            } else if (stmt instanceof IfNode) {
                analyzeIf((IfNode) stmt);
            }
        }
    }

    private void analyzeDeclaration(DeclarationNode node) {
        String varName = node.getVariableName();
        String declaredType = node.getTypeName();
        ExprNode expr = node.getExpression();

        if (symbolTable.exists(varName)) {
            throw new RuntimeException("ডুপ্লিকেট ডিক্লারেশন -> " + varName);
        }

        String exprType = evaluateExpression(expr);

        if (!declaredType.equals(exprType)) {
            throw new RuntimeException("টাইপ মেলেনি -> " + varName);
        }

        symbolTable.declare(varName, declaredType);
    }

    private void analyzeAssignment(AssignmentNode node) {
        String varName = node.getVariableName();
        ExprNode expr = node.getExpression();

        if (!symbolTable.exists(varName)) {
            throw new RuntimeException("ঘোষণা করা হয়নি -> " + varName);
        }

        String exprType = evaluateExpression(expr);
        String varType = symbolTable.getType(varName);

        if (!varType.equals(exprType)) {
            throw new RuntimeException("ভুল টাইপ assign -> " + varName);
        }
    }

    private void analyzeIf(IfNode node) {
        String condType = evaluateCondition(node.getCondition());
        if (!condType.equals("boolean")) {
            throw new RuntimeException("if-এর শর্তটি boolean হওয়া প্রয়োজন");
        }

        SymbolTable savedTable = symbolTable;

        symbolTable = new SymbolTable(savedTable);
        analyzeStatements(node.getThenBranch());

        if (node.hasElse()) {
            symbolTable = new SymbolTable(savedTable);
            analyzeStatements(node.getElseBranch());
        }

        symbolTable = savedTable;
    }

    private String evaluateCondition(ExprNode expr) {
        if (expr instanceof BinaryNode) {
            BinaryNode bin = (BinaryNode) expr;
            String op = bin.getOperator();

            if (op.equals("==") || op.equals("!=") ||
                op.equals("<")  || op.equals(">")  ||
                op.equals("<=") || op.equals(">=")) {

                String leftType  = evaluateExpression(bin.getLeft());
                String rightType = evaluateExpression(bin.getRight());

                if (!leftType.equals(rightType)) {
                    throw new RuntimeException("শর্তে দুই পাশের টাইপ একই হওয়া প্রয়োজন");
                }

                return "boolean";
            }
        }

        throw new RuntimeException("if-এর শর্তে তুলনামূলক অপারেটর (==, !=, <, >, <=, >=) প্রয়োজন");
    }

    private String evaluateExpression(ExprNode expr) {
        if (expr instanceof LiteralNode) {
            LiteralNode literal = (LiteralNode) expr;

            if (literal.getLiteralType() == TokenType.NUMBER) {
                return "সংখ্যা";
            }

            if (literal.getLiteralType() == TokenType.STRING) {
                return "বাক্য";
            }
        }

        if (expr instanceof VariableNode) {
            VariableNode var = (VariableNode) expr;

            if (!symbolTable.exists(var.getName())) {
                throw new RuntimeException("ঘোষণা করা হয়নি -> " + var.getName());
            }

            return symbolTable.getType(var.getName());
        }

        if (expr instanceof BinaryNode) {
            BinaryNode bin = (BinaryNode) expr;

            String leftType  = evaluateExpression(bin.getLeft());
            String rightType = evaluateExpression(bin.getRight());

            if (!leftType.equals("সংখ্যা") || !rightType.equals("সংখ্যা")) {
                throw new RuntimeException("শুধু সংখ্যা টাইপে arithmetic করা যাবে");
            }

            return "সংখ্যা";
        }

        throw new RuntimeException("অজানা এক্সপ্রেশন টাইপ");
    }
}