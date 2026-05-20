import java.util.List;

public class Main {
    public static void main(String[] args) {
        String validIfElseProgram =
                "সংখ্যা বয়স = ৫;\n" +
                "যদি (বয়স) {\n" +
                "    সংখ্যা মোট = বয়স + ১;\n" +
                "} নাহলে {\n" +
                "    সংখ্যা মোট = ০;\n" +
                "}\n";

        String nestedIfElseProgram =
                "সংখ্যা a = ১;\n" +
                "যদি (a) {\n" +
                "    যদি (a) {\n" +
                "        সংখ্যা b = ২;\n" +
                "    } নাহলে {\n" +
                "        সংখ্যা b = ৩;\n" +
                "    }\n" +
                "} নাহলে {\n" +
                "    সংখ্যা c = ৪;\n" +
                "}\n";

        String invalidIfElseProgram =
                "সংখ্যা a = ১;\n" +
                "যদি (a {\n" +
                "    সংখ্যা b = ২;\n" +
                "} নাহলে {\n" +
                "    সংখ্যা c = ৩;\n" +
                "}\n";

        runParserTest("Valid If-Else Program", validIfElseProgram);
        runParserTest("Nested If-Else Program", nestedIfElseProgram);
        runParserTest("Invalid If-Else Program", invalidIfElseProgram);
    }

    private static void runParserTest(String title, String source) {
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
        ProgramNode program = parser.parseProgram();

        System.out.println("========== AST Preview ==========");
        System.out.println(program);
        System.out.println("=================================");
    }
}