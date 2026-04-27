import java.util.HashMap;

public class SymbolTable {
    private HashMap<String, SymbolInfo> table = new HashMap<>();

    public void declare(String name, String type) {
        if (table.containsKey(name)) {
            throw new RuntimeException("Duplicate declaration: " + name);
        }
        table.put(name, new SymbolInfo(type));
    }

    public SymbolInfo lookup(String name) {
        if (!table.containsKey(name)) {
            throw new RuntimeException("Undeclared variable: " + name);
        }
        return table.get(name);
    }
}