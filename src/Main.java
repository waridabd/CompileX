import java.io.IOException;
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

        runTest("সঠিক প্রোগ্রাম", validProgram, true);
        runTest("সিনট্যাক্স ভুল প্রোগ্রাম", syntaxErrorProgram, false);
        runTest("Undeclared variable test", undeclaredProgram, false);
        runTest("Type mismatch test", typeMismatchProgram, false);
        runTest("Duplicate declaration test", duplicateProgram, false);
    }

    private static void runTest(String title, String source, boolean generateCode) {
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

        if (parser.hasErrors()) {
            System.out.println("সিনট্যাক্স ত্রুটি থাকায় সেমান্টিক বিশ্লেষণ শুরু হয়নি।");
            return;
        }

        SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer(tokens);
        semanticAnalyzer.analyze();

        if (generateCode) {
            generateJavaAndRun(program, "GeneratedProgram");
        }
    }

    private static void generateJavaAndRun(ProgramNode program, String className) {
        CodeGenerator codeGenerator = new CodeGenerator(className);
        String fileName = className + ".java";

        try {
            String generatedCode = codeGenerator.generate(program);

            System.out.println("\n========== Generated Java Code ==========");
            System.out.println(generatedCode);
            System.out.println("=========================================\n");

            codeGenerator.writeToFile(program, fileName);
            System.out.println("✓ Generated Java file তৈরি হয়েছে: " + fileName);

            if (compileGeneratedFile(fileName)) {
                runGeneratedClass(className);
            }
        } catch (IOException e) {
            System.out.println("✗ Java file লিখতে সমস্যা হয়েছে: " + e.getMessage());
        }
    }

    private static boolean compileGeneratedFile(String fileName) {
        try {
            ProcessBuilder compileProcessBuilder = new ProcessBuilder(
                    "javac", "-encoding", "UTF-8", fileName
            );
            compileProcessBuilder.inheritIO();

            Process compileProcess = compileProcessBuilder.start();
            int exitCode = compileProcess.waitFor();

            if (exitCode == 0) {
                System.out.println("✓ Generated Java code সফলভাবে compile হয়েছে।");
                return true;
            } else {
                System.out.println("✗ Generated Java code compile হয়নি।");
                return false;
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("✗ Compile step-এ সমস্যা হয়েছে: " + e.getMessage());
            return false;
        }
    }

    private static void runGeneratedClass(String className) {
        try {
            ProcessBuilder runProcessBuilder = new ProcessBuilder("java", className);
            runProcessBuilder.inheritIO();

            Process runProcess = runProcessBuilder.start();
            int exitCode = runProcess.waitFor();

            if (exitCode == 0) {
                System.out.println("✓ Generated Java program সফলভাবে run হয়েছে।");
            } else {
                System.out.println("✗ Generated Java program run করতে সমস্যা হয়েছে।");
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("✗ Run step-এ সমস্যা হয়েছে: " + e.getMessage());
        }
    }
}