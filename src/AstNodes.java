import java.util.List;

abstract class AstNode {
}

class ProgramNode extends AstNode {
    private final List<StmtNode> statements;

    public ProgramNode(List<StmtNode> statements) {
        this.statements = statements;
    }

    public List<StmtNode> getStatements() {
        return statements;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ProgramNode{\n");
        for (StmtNode stmt : statements) {
            sb.append("  ").append(stmt).append("\n");
        }
        sb.append("}");
        return sb.toString();
    }
}

abstract class StmtNode extends AstNode {
}

class DeclarationNode extends StmtNode {
    private final String typeName;
    private final String variableName;
    private final ExprNode expression;

    public DeclarationNode(String typeName, String variableName, ExprNode expression) {
        this.typeName = typeName;
        this.variableName = variableName;
        this.expression = expression;
    }

    @Override
    public String toString() {
        return "DeclarationNode{type=" + typeName + ", name=" + variableName + ", expr=" + expression + "}";
    }
}

class AssignmentNode extends StmtNode {
    private final String variableName;
    private final ExprNode expression;

    public AssignmentNode(String variableName, ExprNode expression) {
        this.variableName = variableName;
        this.expression = expression;
    }

    @Override
    public String toString() {
        return "AssignmentNode{name=" + variableName + ", expr=" + expression + "}";
    }
}

abstract class ExprNode extends AstNode {
}

class BinaryNode extends ExprNode {
    private final ExprNode left;
    private final String operator;
    private final ExprNode right;

    public BinaryNode(ExprNode left, String operator, ExprNode right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    @Override
    public String toString() {
        return "(" + left + " " + operator + " " + right + ")";
    }
}

class LiteralNode extends ExprNode {
    private final String value;
    private final TokenType literalType;

    public LiteralNode(String value, TokenType literalType) {
        this.value = value;
        this.literalType = literalType;
    }

    @Override
    public String toString() {
        if (literalType == TokenType.STRING) {
            return "\"" + value + "\"";
        }
        return value;
    }
}

class VariableNode extends ExprNode {
    private final String name;

    public VariableNode(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}