import java.util.List;

public class Main {
    public static void main(String[] args) {
        String validProgram =
                "সংখ্যা বয়স = ৫;\n" +
                "সংখ্যা মোট = বয়স + ৩ * (২ + ১);\n" +
                "বাক্য নাম = \"মানুষ\";\n" +
                "মোট = মোট + ১;\n";

        String invalidProgram =
                "সংখ্যা বয়স = ৫\n" +
                "সংখ্যা মোট = বয়স + ;\n" +
                "বাক্য নাম = \"মানুষ\";\n" +
                "মোট = (১ + ২;\n";

        runTest("সঠিক প্রোগ্রাম", validProgram);
        runTest("ভুল প্রোগ্রাম", invalidProgram);
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
    }
}