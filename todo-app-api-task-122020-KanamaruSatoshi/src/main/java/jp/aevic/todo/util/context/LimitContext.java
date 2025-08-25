package jp.aevic.todo.util.context;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 登録上限に関する外部設定された値をもってくるクラス
 */
@ConfigurationProperties(prefix = "todo")
@Getter
@AllArgsConstructor
public class LimitContext {
    //TODOに紐づけることのできるタグの上限数
    private int linkedTagLimit;
}
