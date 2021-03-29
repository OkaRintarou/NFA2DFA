import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

/**
 * 表示自动机中一个状态的类
 */
public class Node {
    /**
     * 该状态中包含的子状态集合，在NFA中只有一个元素，DFA中可能有多个元素
     */
    TreeSet<String> nameSet = new TreeSet<>();
    /**
     * 转换函数集合（不含空串）
     */
    HashMap<String, String> changeSet = new HashMap<>();
    /**
     * 将子状态集合转换为字符串，方便查找
     */
    String name;
    /**
     * 该状态的类型
     */
    NodeType type;
    /**
     * 含空串的转换函数可到达的状态集，该项可能为空
     */
    ArrayList<String> e = new ArrayList<>();

    /**
     * getter of e
     * @return 含空串的转换函数可到达的状态集
     */
    public ArrayList<String> getE() {
        return this.e;
    }

    /**
     * 添加含空串的转换函数可到达的状态
     * @param e 状态名
     */
    public void addE(String e) {
        this.e.add(e);
    }

    /**
     * getter of changeSet
     * @return 转换函数集（不含空串）
     */
    public HashMap<String, String> getChangeSet() {
        return changeSet;
    }

    /**
     * 判断该状态是否为起始状态
     * @return boolean
     */
    public boolean isSTART() {
        return this.type == NodeType.START || this.type == NodeType.START_END;
    }

    /**
     * 得到该状态的类型
     * @return 状态类型（字符串）
     */
    public String getType() {
        return type.toString();
    }


    /**
     * setter of type
     * @param type 状态名（字符串）
     */
    public void setType(String type) {
        this.type = NodeType.valueOf(type);
    }

    /**
     * setter of type
     * @param type 状态类型（使用枚举类）
     */
    public void setType(NodeType type) {
        this.type = type;
    }

    /**
     * getter of nameSet
     * @return 该状态的子状态集合
     */
    public TreeSet<String> getNameSet() {
        return nameSet;
    }


    /**
     * getter of name
     * @return 状态名
     */
    public String getName() {
        return name;
    }

    /**
     * 添加子状态
     * @param name 子状态名
     */
    public void addName(String name) {
        nameSet.add(name);
    }

    /**
     * 由子状态集生成状态名
     */
    public void generateName() {
        StringBuilder stringBuilder = new StringBuilder();
        for (var item : nameSet) {
            stringBuilder.append(item).append(",");
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        this.name = stringBuilder.toString();
    }

    /**
     * 添加转换函数
     * @param charSet 输入
     * @param name 到达状态
     */
    public void addChange(String charSet, String name) {
        changeSet.put(charSet, name);
    }
}
