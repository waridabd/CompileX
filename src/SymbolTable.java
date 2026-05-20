import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
    private final Map<String, SymbolInfo> table = new HashMap<>();
    private final SymbolTable parent;

    public SymbolTable() {
        this.parent = null;
    }

    public SymbolTable(SymbolTable parent) {
        this.parent = parent;
    }

    public void declare(String name, String type) {
        if (table.containsKey(name)) {
            throw new RuntimeException("সেমান্টিক ত্রুটি: একই ভ্যারিয়েবল আবার ঘোষণা করা হয়েছে -> " + name);
        }
        table.put(name, new SymbolInfo(name, type));
    }

    public SymbolInfo lookup(String name) {
        if (table.containsKey(name)) {
            return table.get(name);
        }
        if (parent != null) {
            return parent.lookup(name);
        }
        throw new RuntimeException("সেমান্টিক ত্রুটি: আগে ঘোষণা করা হয়নি -> " + name);
    }

    public boolean exists(String name) {
        if (table.containsKey(name)) return true;
        if (parent != null) return parent.exists(name);
        return false;
    }

    public String getType(String name) {
        return lookup(name).getType();
    }

    public void printTable() {
        System.out.println("\n========== Symbol Table ==========");
        if (table.isEmpty()) {
            System.out.println("কোনো ভ্যারিয়েবল নেই।");
        } else {
            for (SymbolInfo info : table.values()) {
                System.out.println("নাম: " + info.getName() + " | টাইপ: " + info.getType());
            }
        }
        System.out.println("==================================\n");
    }
}