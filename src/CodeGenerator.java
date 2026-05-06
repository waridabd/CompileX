public class CodeGenerator {

    public String generateProgram(ProgramNode program) {

        StringBuilder sb = new StringBuilder();

        sb.append("public class GeneratedCode {\n");
        sb.append("public static void main(String[] args) {\n");

        sb.append("\n");

        sb.append("}\n}");
        return sb.toString();
    }
}