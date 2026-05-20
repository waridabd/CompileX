public class Token {
    public final TokenType type;
    public final String lexeme;
    public final String value;
    public final int line;

    public Token(TokenType type, String lexeme, String value, int line) {
        this.type   = type;
        this.lexeme = lexeme;
        this.value  = value;
        this.line   = line;
    }

    public Token(TokenType type, String lexeme, int line) {
        this(type, lexeme, lexeme, line);
    }

    @Override
    public String toString() {
        if (type == TokenType.NUMBER && !lexeme.equals(value)) {
            return String.format(
                "Token{ type=%-18s  lexeme=%-15s  value=%-10s  line=%d }",
                type, "\"" + lexeme + "\"", "\"" + value + "\"", line);
        }
        return String.format(
            "Token{ type=%-18s  lexeme=%-15s  line=%d }",
            type, "\"" + lexeme + "\"", line);
    }
}