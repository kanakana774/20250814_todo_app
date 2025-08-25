package jp.aevic.todo.query.todo;

import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * 一覧getで使用するqueryクラス
 */
@Data
public class GetTodosQuery {
    @Min(1)
    // 取得上限数
    private Integer limit;
    // タイトル
    private String title;
}
