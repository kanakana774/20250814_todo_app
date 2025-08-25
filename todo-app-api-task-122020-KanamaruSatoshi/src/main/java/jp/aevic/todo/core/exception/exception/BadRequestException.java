package jp.aevic.todo.core.exception.exception;

import jp.aevic.todo.core.exception.statics.ErrorCodes;
import org.springframework.http.HttpStatus;

/**
 * BadRequestExceptionの独自例外クラス
 */
public class BadRequestException extends TodoRuntimeException {
    /**
     * コンストラクタ
     *
     * @param errorCodes エラー内容に対応したエラーコード
     */
    public BadRequestException(ErrorCodes errorCodes) {
        super(HttpStatus.BAD_REQUEST, errorCodes);
    }
}
