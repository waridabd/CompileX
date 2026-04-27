
import java.util.ArrayList;
import java.util.List;

public class Lexer {
    private final String source;   
    private int    start   = 0;    
    private int    current = 0;    
    private int    line    = 1;    

    
    private final List<Token>  tokens = new ArrayList<>();
    private final List<String> errors = new ArrayList<>();
    private static final java.util.Map<String, TokenType> KEYWORDS =
        new java.util.HashMap<>();

    static {
        KEYWORDS.put("সংখ্যা", TokenType.TYPE_SHONGKHA);
        KEYWORDS.put("বাক্য",  TokenType.TYPE_BAKKO);
    }
    public Lexer(String source) {
        this.source = source;
    }

    public List<Token> tokenize() {
        while (!isAtEnd()) {
            start = current;
            scanToken();
        }
        tokens.add(new Token(TokenType.EOF, "", line));
        return tokens;
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public List<String> getErrors() {
        return errors;
    }


    private void scanToken() {
        char c = advance();

        switch (c) {

        
            case '=': addToken(TokenType.ASSIGN);    break;
            case '+': addToken(TokenType.PLUS);      break;
            case '-': addToken(TokenType.MINUS);     break;
            case '*': addToken(TokenType.MULTIPLY);  break;
            case '/': addToken(TokenType.DIVIDE);    break;
            case ';': addToken(TokenType.SEMICOLON); break;
            case '(': addToken(TokenType.LPAREN);    break;
            case ')': addToken(TokenType.RPAREN);    break;

        
            case '"': scanString(); break;

            
            case ' ':
            case '\r':
            case '\t':
                break;
            case '\n':
                line++;
                break;

            
            default:
                if (isBanglaDigit(c)) {
                    scanNumber();
                } else if (isBanglaLetterStart(c)) {
                    scanIdentifierOrKeyword();
                } else {
                    
                    lexError("অপরিচিত অক্ষর (unexpected character): '" + c + "'");
                }
                break;
        }
    }

    
    private void scanNumber() {
        while (!isAtEnd() && isBanglaDigit(peek())) advance();
        addToken(TokenType.NUMBER);
    }

    private void scanIdentifierOrKeyword() {
        while (!isAtEnd() && isBanglaLetterContinue(peek())) advance();

        String word = source.substring(start, current);

        TokenType type = KEYWORDS.getOrDefault(word, TokenType.IDENTIFIER);
        addToken(type);
    }


    private void scanString() {
        while (!isAtEnd() && peek() != '"') {
            if (peek() == '\n') line++;  
            advance();
        }

        if (isAtEnd()) {
            lexError("স্ট্রিং শেষ হয়নি (unterminated string literal)");
            return;
        }

        advance(); 

        String value = source.substring(start + 1, current - 1);
        tokens.add(new Token(TokenType.STRING, value, line));
        return;
    }


    private boolean isBanglaDigit(char c) {
        return c >= '\u09E6' && c <= '\u09EF';
    }

    
    private boolean isBanglaLetterStart(char c) {
        return c >= '\u0980' && c <= '\u09FF';
    }


    private boolean isBanglaLetterContinue(char c) {
        return isBanglaLetterStart(c);
    }


    private char advance() {
        return source.charAt(current++);
    }

    
    private char peek() {
        if (isAtEnd()) return '\0';
        return source.charAt(current);
    }

    
    private boolean isAtEnd() {
        return current >= source.length();
    }


    private void addToken(TokenType type) {
        String lexeme = source.substring(start, current);
        tokens.add(new Token(type, lexeme, line));
    }

    private void lexError(String message) {
        String err = "[লেক্সিকাল ত্রুটি] লাইন " + line + ": " + message;
        errors.add(err);
        System.err.println(err);
    }

    public void printTokens() {
        System.out.println("\n══════════  টোকেন তালিকা (Token List)  ══════════");
        for (Token t : tokens) {
            System.out.println(t);
        }
        System.out.println("══════════════════════════════════════════════════\n");

        if (hasErrors()) {
            System.out.println("⚠  মোট লেক্সিকাল ত্রুটি: " + errors.size());
            for (String e : errors) System.out.println("   " + e);
        } else {
            System.out.println("✓  কোনো লেক্সিকাল ত্রুটি পাওয়া যায়নি।");
        }
    }
}