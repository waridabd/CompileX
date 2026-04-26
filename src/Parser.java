import java.util.List;

public class Parser {
    private List<Token> tokens;
    private int current = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public void parseProgram() {
        while (!isAtEnd()) {
            parseStatement();
        }
    }

    private void parseStatement() {
        if (match(TokenType.TYPE_SHONGKHA, TokenType.TYPE_BAKKO)) {
            current--;
            parseDeclaration();
        } else if (check(TokenType.IDENTIFIER)) {
            parseAssignment();
        } else {
            throw new RuntimeException("অবৈধ স্টেটমেন্ট");
        }
    }

    private void parseDeclaration() {
        if (match(TokenType.TYPE_SHONGKHA, TokenType.TYPE_BAKKO)) {
            consume(TokenType.IDENTIFIER, "সঠিক আইডেন্টিফায়ার দরকার");
            consume(TokenType.ASSIGN, "সমান চিহ্ন (=) দরকার");
            parseExpression();
            consume(TokenType.SEMICOLON, "সেমিকোলন দরকার");
        } else {
            throw new RuntimeException("সঠিক টাইপ দরকার");
        }
    }

    private void parseAssignment() {
        consume(TokenType.IDENTIFIER, "সঠিক আইডেন্টিফায়ার দরকার");
        consume(TokenType.ASSIGN, "সমান চিহ্ন (=) দরকার");
        parseExpression();
        consume(TokenType.SEMICOLON, "সেমিকোলন দরকার");
    }

    private void parseExpression() {
        parseTerm();
        while (match(TokenType.PLUS, TokenType.MINUS)) {
            parseTerm();
        }
    }

    private void parseTerm() {
        parseFactor();
        while (match(TokenType.MULTIPLY, TokenType.DIVIDE)) {
            parseFactor();
        }
    }

    private void parseFactor() {
        if (match(TokenType.NUMBER, TokenType.STRING, TokenType.IDENTIFIER)) {
            return;
        }

        if (match(TokenType.LPAREN)) {
            parseExpression();
            consume(TokenType.RPAREN, "বন্ধনী ঠিক নেই");
            return;
        }

        throw new RuntimeException("এক্সপ্রেশন ভুল");
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token previous() {
        return tokens.get(current - 1);
    }

    private Token advance() {
        if (!isAtEnd()) current++;
        return previous();
    }

    private boolean isAtEnd() {
        return peek().type == TokenType.EOF;
    }

    private boolean check(TokenType type) {
        if (isAtEnd()) return false;
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
        if (check(type)) return advance();
        throw new RuntimeException(message);
    }
}