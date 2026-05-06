public class SemanticAnalyzer {
    private final ProgramNode program;
    private final SymbolTable symbolTable = new SymbolTable();

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

        System.out.println("\n========== Semantic Analysis ==========");

        if (errors.isEmpty()) {
            System.out.println("✓ কোনো সেমান্টিক ত্রুটি নেই।");
        } else {
            for (String error : errors) {
                System.out.println(error);
            }
            System.out.println("⚠ মোট সেমান্টিক ত্রুটি: " + errors.size());
        }

        System.out.println("=======================================\n");

        symbolTable.printTable();
    }

    private void analyzeDeclaration(DeclarationNode node) {
        String varName = node.getVariableName();
        String declaredType = node.getTypeName();
        ExprNode expr = node.getExpression();

    private void analyzeStatement(StmtNode stmt) {

        if (stmt instanceof DeclarationNode) {
            analyzeDeclaration((DeclarationNode) stmt);
            return;
        }

        if (stmt instanceof AssignmentNode) {
            analyzeAssignment((AssignmentNode) stmt);
            return;
        }

        throw new RuntimeException("অবৈধ স্টেটমেন্ট");
    }

    private void analyzeDeclaration(DeclarationNode node) {

        String exprType = evaluate(node.value);

        if (!node.type.equals(exprType)) {
            throw new RuntimeException(
                    "টাইপ মেলেনি -> " + node.name +
                    " expected: " + node.type +
                    " but found: " + exprType
            );
        }

        symbolTable.declare(varName, declaredType);
    }

    private void analyzeAssignment(AssignmentNode node) {

        if (!symbolTable.exists(node.name)) {
            throw new RuntimeException("আগে ঘোষণা করা হয়নি -> " + node.name);
        }

        String exprType = evaluate(node.value);
        String varType = symbolTable.getType(node.name);

        if (!varType.equals(exprType)) {
            throw new RuntimeException(
                    node.name + " এ ভুল টাইপ assign -> expected " +
                    varType + " but found " + exprType
            );
        }
    }

    private String evaluate(ExprNode expr) {

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

            BinaryNode b = (BinaryNode) expr;

            String left = evaluate(b.left);
            String right = evaluate(b.right);

            if (!left.equals("সংখ্যা") || !right.equals("সংখ্যা")) {
                throw new RuntimeException("Arithmetic শুধু সংখ্যা টাইপের জন্য");
            }

            return "সংখ্যা";
        }

        throw new RuntimeException("অজানা এক্সপ্রেশন টাইপ");
    }
}