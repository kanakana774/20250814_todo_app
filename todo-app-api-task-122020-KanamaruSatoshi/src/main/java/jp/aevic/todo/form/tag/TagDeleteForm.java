package jp.aevic.todo.form.tag;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * タグ削除時のformクラス
 * 
 */
@Data
public class TagDeleteForm {
    @NotNull
    // 更新回数
    private Integer version;
}
