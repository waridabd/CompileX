import java.util.ArrayList;
import java.util.List;

public class Parser {
    private final List<Token> tokens;
    private int current = 0;
    private final List<String> errors = new ArrayList<>();

    private final List<StmtNode> statements = new ArrayList<>();

    private static class ParseError extends RuntimeException {
    }

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public ProgramNode parseProgram() {
        statements.clear();

        while (!isAtEnd()) {
            try {
                StmtNode stmt = parseStatement();
                if (stmt != null) {
                    statements.add(stmt);
                }
            } catch (ParseError e) {
                synchronize();
            }
        }

        if (errors.isEmpty()) {
            System.out.println("\n✓ পার্সিং সফলভাবে সম্পন্ন হয়েছে। কোনো সিনট্যাক্স ত্রুটি পাওয়া যায়নি।\n");
        } else {
            printErrors();
        }

        return new ProgramNode(new ArrayList<>(statements));
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public List<String> getErrors() {
        return errors;
    }

    public List<StmtNode> getStatements() {
        return statements;
    }

    private void printErrors() {
        System.out.println("\n══════════  সিনট্যাক্স ত্রুটির তালিকা  ══════════");
        for (String error : errors) {
            System.out.println(error);
        }
        System.out.println("══════════════════════════════════════════════════");
        System.out.println("⚠ মোট সিনট্যাক্স ত্রুটির সংখ্যা: " + errors.size() + "\n");
    }

    private StmtNode parseStatement() {
        if (check(TokenType.TYPE_SHONGKHA) || check(TokenType.TYPE_BAKKO)) {
            return parseDeclaration();
        }

        if (check(TokenType.IDENTIFIER)) {
            return parseAssignment();
        }

        throw error(peek(), "আপনার সিনট্যাক্স ভুল");
    }

    private DeclarationNode parseDeclaration() {
        if (!match(TokenType.TYPE_SHONGKHA, TokenType.TYPE_BAKKO)) {
            throw error(peek(), "সঠিক ডেটা টাইপ উল্লেখ করা প্রয়োজন");
        }

        Token typeToken = previous();
        Token nameToken = consume(TokenType.IDENTIFIER, "সঠিক আইডেন্টিফায়ার প্রদান করুন");
        consume(TokenType.ASSIGN, "সমান চিহ্ন (=) প্রদান করা আবশ্যক");
        ExprNode expression = parseExpression();
        consume(TokenType.SEMICOLON, "স্টেটমেন্টের শেষে সেমিকোলন (;) প্রদান করুন");

        return new DeclarationNode(typeToken.lexeme, nameToken.lexeme, expression);
    }

    private AssignmentNode parseAssignment() {
        Token nameToken = consume(TokenType.IDENTIFIER, "সঠিক আইডেন্টিফায়ার প্রদান করুন");
        consume(TokenType.ASSIGN, "সমান চিহ্ন (=) প্রদান করা আবশ্যক");
        ExprNode expression = parseExpression();
        consume(TokenType.SEMICOLON, "স্টেটমেন্টের শেষে সেমিকোলন (;) প্রদান করুন");

        return new AssignmentNode(nameToken.lexeme, expression);
    }

    private ExprNode parseExpression() {
        ExprNode expr = parseTerm();

        while (match(TokenType.PLUS, TokenType.MINUS)) {
            Token operator = previous();
            ExprNode right = parseTerm();
            expr = new BinaryNode(expr, operator.lexeme, right);
        }

        return expr;
    }

    private ExprNode parseTerm() {
        ExprNode expr = parseFactor();

        while (match(TokenType.MULTIPLY, TokenType.DIVIDE)) {
            Token operator = previous();
            ExprNode right = parseFactor();
            expr = new BinaryNode(expr, operator.lexeme, right);
        }

        return expr;
    }

    private ExprNode parseFactor() {
        if (match(TokenType.NUMBER)) {
            return new LiteralNode(previous().lexeme, TokenType.NUMBER);
        }

        if (match(TokenType.STRING)) {
            return new LiteralNode(previous().lexeme, TokenType.STRING);
        }

        if (match(TokenType.IDENTIFIER)) {
            return new VariableNode(previous().lexeme);
        }

        if (match(TokenType.LPAREN)) {
            ExprNode expr = parseExpression();
            consume(TokenType.RPAREN, "বন্ধনী সঠিকভাবে সম্পূর্ণ হয়নি");
            return expr;
        }

        throw error(peek(), "এক্সপ্রেশনটি সঠিক নয়");
    }

    private ParseError error(Token token, String message) {
        String found;

        if (token.type == TokenType.EOF) {
            found = "ফাইলের শেষ অংশ";
        } else {
            found = token.lexeme;
        }

        String fullMessage = "[সিনট্যাক্স ত্রুটি] লাইন " + token.line + ": " + message
                + " → পাওয়া গেছে: \"" + found + "\"";

        errors.add(fullMessage);
        System.err.println(fullMessage);

        return new ParseError();
    }

    private void synchronize() {
        if (!isAtEnd()) {
            advance();
        }

        while (!isAtEnd()) {
            if (previous().type == TokenType.SEMICOLON) {
                return;
            }

            switch (peek().type) {
                case TYPE_SHONGKHA:
                case TYPE_BAKKO:
                case IDENTIFIER:
                    return;
                default:
                    advance();
            }
        }
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token previous() {
        return tokens.get(current - 1);
    }

    private Token advance() {
        if (!isAtEnd()) {
            current++;
        }
        return previous();
    }

    private boolean isAtEnd() {
        return peek().type == TokenType.EOF;
    }

    private boolean check(TokenType type) {
        if (isAtEnd()) {
            return false;
        }
        return peek().type == type;
    }

    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }

    private Token consume(TokenType type, String message) {
        if (check(type)) {
            return advance();
        }
        throw error(peek(), message);
    }
}