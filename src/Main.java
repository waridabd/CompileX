import java.util.List;

public class Main {
    public static void main(String[] args) {
        String validProgram =
                "সংখ্যা বয়স = ৫;\n" +
                "সংখ্যা মোট = বয়স + ৩ * (২ + ১);\n" +
                "বাক্য নাম = \"মানুষ\";\n" +
                "মোট = মোট + ১;\n";

        String syntaxErrorProgram =
                "সংখ্যা বয়স = ৫\n" +
                "সংখ্যা মোট = বয়স + ;\n" +
                "বাক্য নাম = \"মানুষ\";\n" +
                "মোট = (১ + ২;\n";

        String undeclaredProgram =
                "সংখ্যা বয়স = ৫;\n" +
                "মোট = বয়স + ২;\n";

        String typeMismatchProgram =
                "সংখ্যা বয়স = \"মানুষ\";\n" +
                "বাক্য নাম = \"রাফি\";\n";

        String duplicateProgram =
                "সংখ্যা বয়স = ৫;\n" +
                "সংখ্যা বয়স = ১০;\n";

        runTest("সঠিক প্রোগ্রাম", validProgram);
        runTest("সিনট্যাক্স ভুল প্রোগ্রাম", syntaxErrorProgram);
        runTest("Undeclared variable test", undeclaredProgram);
        runTest("Type mismatch test", typeMismatchProgram);
        runTest("Duplicate declaration test", duplicateProgram);
    }

    private static void runTest(String title, String source) {
        System.out.println("\n==================================================");
        System.out.println(title);
        System.out.println("==================================================");
        System.out.println(source);

        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.tokenize();
        lexer.printTokens();

        if (lexer.hasErrors()) {
            System.out.println("লেক্সিকাল ত্রুটি থাকায় পার্সিং শুরু হয়নি।");
            return;
        }

        Parser parser = new Parser(tokens);
        parser.parseProgram();

        if (parser.hasErrors()) {
            System.out.println("সিনট্যাক্স ত্রুটি থাকায় সেমান্টিক বিশ্লেষণ শুরু হয়নি।");
            return;
        }

        SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer(tokens);
        semanticAnalyzer.analyze();
    }
}