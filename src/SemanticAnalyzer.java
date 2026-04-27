public class SemanticAnalyzer {
    private SymbolTable symbolTable;

    public SemanticAnalyzer(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }

    public void checkAssignment(String varName, String exprType) {
        String varType = symbolTable.lookup(varName).getType();

        if (!varType.equals(exprType)) {
            throw new RuntimeException("Type mismatch: cannot assign " + exprType + " to " + varType);
        }
    }
}