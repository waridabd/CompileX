import java.util.ArrayList;
import java.util.List;

public class SemanticAnalyzer {
    private final List<Token> tokens;
    private int current = 0;
    private final SymbolTable symbolTable = new SymbolTable();
    private final List<String> errors = new ArrayList<>();

    public SemanticAnalyzer(List<Token> tokens) {
        this.tokens = tokens;
    }

    public void analyze() {
        while (!isAtEnd()) {
            try {
                analyzeStatement();
            } catch (RuntimeException e) {
                errors.add(e.getMessage());
                synchronize();
            }
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

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    private void analyzeStatement() {
        if (match(TokenType.TYPE_SHONGKHA, TokenType.TYPE_BAKKO)) {
            Token typeToken = previous();
            analyzeDeclaration(typeToken);
            return;
        }

        if (match(TokenType.IDENTIFIER)) {
            Token identifier = previous();
            analyzeAssignment(identifier);
            return;
        }

        throw new RuntimeException("সেমান্টিক ত্রুটি: অবৈধ স্টেটমেন্ট -> " + peek().lexeme);
    }

    private void analyzeDeclaration(Token typeToken) {
        String declaredType = mapType(typeToken.type);

        Token nameToken = consume(TokenType.IDENTIFIER, "ভ্যারিয়েবল নাম দরকার");
        consume(TokenType.ASSIGN, "= দরকার");

        String exprType = parseExpressionType();

        if (!declaredType.equals(exprType)) {
            throw new RuntimeException(
                    "সেমান্টিক ত্রুটি: টাইপ মেলেনি -> " + nameToken.lexeme +
                    " এর টাইপ " + declaredType + ", কিন্তু expression এর টাইপ " + exprType
            );
        }

        symbolTable.declare(nameToken.lexeme, declaredType);
        consume(TokenType.SEMICOLON, "; দরকার");
    }

    private void analyzeAssignment(Token nameToken) {
        if (!symbolTable.exists(nameToken.lexeme)) {
            throw new RuntimeException("সেমান্টিক ত্রুটি: আগে ঘোষণা করা হয়নি -> " + nameToken.lexeme);
        }

        consume(TokenType.ASSIGN, "= দরকার");
        String exprType = parseExpressionType();

        String varType = symbolTable.lookup(nameToken.lexeme).getType();
        if (!varType.equals(exprType)) {
            throw new RuntimeException(
                    "সেমান্টিক ত্রুটি: " + nameToken.lexeme +
                    " এ " + exprType + " assign করা যাবে না, expected type: " + varType
            );
        }

        consume(TokenType.SEMICOLON, "; দরকার");
    }

    private String parseExpressionType() {
        String leftType = parseTermType();

        while (match(TokenType.PLUS, TokenType.MINUS)) {
            Token operator = previous();
            String rightType = parseTermType();
            leftType = combineArithmeticTypes(leftType, rightType, operator);
        }

        return leftType;
    }

    private String parseTermType() {
        String leftType = parseFactorType();

        while (match(TokenType.MULTIPLY, TokenType.DIVIDE)) {
            Token operator = previous();
            String rightType = parseFactorType();
            leftType = combineArithmeticTypes(leftType, rightType, operator);
        }

        return leftType;
    }

    private String parseFactorType() {
        if (match(TokenType.NUMBER)) {
            return "সংখ্যা";
        }

        if (match(TokenType.STRING)) {
            return "বাক্য";
        }

        if (match(TokenType.IDENTIFIER)) {
            Token id = previous();
            return symbolTable.lookup(id.lexeme).getType();
        }

        if (match(TokenType.LPAREN)) {
            String innerType = parseExpressionType();
            consume(TokenType.RPAREN, "বন্ধনী ঠিক নেই");
            return innerType;
        }

        throw new RuntimeException("সেমান্টিক ত্রুটি: ভুল expression -> " + peek().lexeme);
    }

    private String combineArithmeticTypes(String leftType, String rightType, Token operator) {
        if (!leftType.equals("সংখ্যা") || !rightType.equals("সংখ্যা")) {
            throw new RuntimeException(
                    "সেমান্টিক ত্রুটি: arithmetic operator '" + operator.lexeme +
                    "' শুধু সংখ্যা টাইপের জন্য"
            );
        }
        return "সংখ্যা";
    }

    private String mapType(TokenType type) {
        if (type == TokenType.TYPE_SHONGKHA) return "সংখ্যা";
        if (type == TokenType.TYPE_BAKKO) return "বাক্য";
        throw new RuntimeException("অজানা টাইপ");
    }

    private void synchronize() {
        while (!isAtEnd() && !check(TokenType.SEMICOLON)) {
            advance();
        }
        if (check(TokenType.SEMICOLON)) {
            advance();
        }
    }

    private Token consume(TokenType type, String message) {
        if (check(type)) return advance();
        throw new RuntimeException("সেমান্টিক ত্রুটি: " + message + " -> পাওয়া গেছে: " + peek().lexeme);
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

    private boolean check(TokenType type) {
        if (isAtEnd()) return false;
        return peek().type == type;
    }

    private Token advance() {
        if (!isAtEnd()) current++;
        return previous();
    }

    private boolean isAtEnd() {
        return peek().type == TokenType.EOF;
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token previous() {
        return tokens.get(current - 1);
    }
}