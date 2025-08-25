package jp.aevic.todo.util.context;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * URLに関する外部設定された値をもってくるクラス
 * <p>
 * application.propertiesファイルのtodo.locationに対応した値を管理する
 * </p>
 */
@ConfigurationProperties(prefix = "todo")
@Getter
@AllArgsConstructor
public class UriContext {
    //URL生成時の元となるURL
    private final String location;
}
