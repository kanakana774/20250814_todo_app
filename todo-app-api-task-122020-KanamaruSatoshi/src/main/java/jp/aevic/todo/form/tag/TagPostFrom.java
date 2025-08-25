package jp.aevic.todo.form.tag;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * タグ新規作成時のformクラス
 * 
 */
@Data
public class TagPostFrom {
    @NotBlank
    @Size(min = 1, max = 30)
    // 名前
    private String name;
}
