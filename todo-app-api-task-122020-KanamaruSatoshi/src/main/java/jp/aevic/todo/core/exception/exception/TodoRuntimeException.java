package jp.aevic.todo.core.exception.exception;

import jp.aevic.todo.core.exception.statics.ErrorCodes;
import lombok.Getter;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

/**
 * TODOアプリの独自実装のRuntimeExceptionクラス
 */
@Getter
public class TodoRuntimeException extends ErrorResponseException {
    //エラーコード
    private final ErrorCodes code;

    /**
     * コンストラクタ
     *
     * @param httpStatusCode ステータスコード
     * @param code           エラーコード
     */
    public TodoRuntimeException(HttpStatusCode httpStatusCode, ErrorCodes code) {
        super(httpStatusCode, ProblemDetail.forStatus(httpStatusCode), null, code.getCode(), null);
        this.code = code;
    }
}
