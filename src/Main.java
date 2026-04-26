

import java.util.List;

public class Main {

    public static void main(String[] args) {

        System.out.println("╔══════════════════════════════════════════════════╗");
        System.out.println("║     বাংলা কম্পাইলার – লেক্সার পর্যালোচনা       ║");
        System.out.println("║     Bangla Compiler  –  Lexer Review Demo        ║");
        System.out.println("╚══════════════════════════════════════════════════╝\n");

        
        runTest("Test 1 – Integer declaration",
                "সংখ্যা বয়স = ২০;");


        runTest("Test 2 – String declaration",
                "বাক্য নাম = \"রাহিম\";");


        runTest("Test 3 – Arithmetic expression",
                "সংখ্যা মোট = ৫ + ১০০;");

        
        runTest("Test 4 – Multi-statement program",
                "সংখ্যা ক = ১০;\n" +
                "সংখ্যা খ = ২০;\n" +
                "সংখ্যা মোট = ক + খ;\n" +
                "বাক্য নাম = \"করিম\";");

        
        runTest("Test 5 – All operators and delimiters",
                "সংখ্যা ফলাফল = (৫ + ৩) * ২ - ১ / ৪;");

        
        runTest("Test 6 – Lexical error (unexpected character '@')",
                "সংখ্যা বয়স = ২০@;");

        
        runTest("Test 7 – Lexical error (unterminated string)",
                "বাক্য নাম = \"রাহিম;");
    }

    
    private static void runTest(String title, String source) {
        System.out.println("┌─────────────────────────────────────────────────┐");
        System.out.println("│  " + title);
        System.out.println("│  Source: " + source.replace("\n", "  |  "));
        System.out.println("└─────────────────────────────────────────────────┘");

        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.tokenize();
        lexer.printTokens();

        System.out.println(); 
    }
}