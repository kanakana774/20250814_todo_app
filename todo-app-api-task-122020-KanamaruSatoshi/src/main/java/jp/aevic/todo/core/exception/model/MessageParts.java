package jp.aevic.todo.core.exception.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * logのメッセージの生成に必要なリクエストのpathとmethodのBean内部クラス
 */
@AllArgsConstructor
@Getter
public class MessageParts {
    //リクエストされたpath
    private String path;
    //リクエストされたmethod
    private String method;
    //リクエストされたユーザーエージェント
    private String userAgent;
}
