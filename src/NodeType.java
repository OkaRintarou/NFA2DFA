/**
 * 枚举类，标识自动机的状态
 */
public enum NodeType {
    /**
     * 表示仅是起始状态
     */
    START,
    /**
     * 表示中间状态
     */
    NORMAL,
    /**
     * 表示既是起始状态也是终止状态
     */
    START_END,
    /**
     * 表示终止状态
     */
    END
}
