package jp.aevic.todo.form.todo;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * todo削除時のformクラス
 */
@Data
public class TodoDeleteForm {
    @NotNull
    // 更新回数
    private Integer version;
}
