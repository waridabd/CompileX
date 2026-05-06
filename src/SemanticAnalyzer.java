public class SemanticAnalyzer {
    private final ProgramNode program;
    private final SymbolTable symbolTable = new SymbolTable();

    public SemanticAnalyzer(ProgramNode program) {
        this.program = program;
    }

    public void analyze() {
        try {
            for (StmtNode stmt : program.getStatements()) {
                if (stmt instanceof DeclarationNode) {
                    analyzeDeclaration((DeclarationNode) stmt);
                } else if (stmt instanceof AssignmentNode) {
                    analyzeAssignment((AssignmentNode) stmt);
                }
            }

            System.out.println("✓ সেমান্টিক বিশ্লেষণ সফলভাবে সম্পন্ন হয়েছে।");
        } catch (RuntimeException e) {
            System.out.println("✗ সেমান্টিক ত্রুটি: " + e.getMessage());
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
            throw new RuntimeException("ঘোষণা করা হয়নি -> " + varName);
        }

        String exprType = evaluateExpression(expr);
        String varType = symbolTable.getType(varName);

        if (!varType.equals(exprType)) {
            throw new RuntimeException("ভুল টাইপ assign -> " + varName);
        }
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
                throw new RuntimeException("ঘোষণা করা হয়নি -> " + var.getName());
            }

            return symbolTable.getType(var.getName());
        }

        if (expr instanceof BinaryNode) {
            BinaryNode bin = (BinaryNode) expr;

            String leftType = evaluateExpression(bin.getLeft());
            String rightType = evaluateExpression(bin.getRight());

            if (!leftType.equals("সংখ্যা") || !rightType.equals("সংখ্যা")) {
                throw new RuntimeException("শুধু সংখ্যা টাইপে arithmetic করা যাবে");
            }

            return "সংখ্যা";
        }

        throw new RuntimeException("অজানা এক্সপ্রেশন টাইপ");
    }
}