package jp.aevic.todo.testTools;

/**
 * テスト用の境界値を定数で定義するクラス
 */
public class BoundaryValue {
    //0文字
    public static final String ZERO_CHARACTER;
    //空白1文字
    public static final String ONE_SPACE_CHARACTER;

    static {
        ZERO_CHARACTER = "";
        ONE_SPACE_CHARACTER = " ";
    }
}
