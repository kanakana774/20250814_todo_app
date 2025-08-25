package jp.aevic.todo.core.exception.exception;

import jp.aevic.todo.core.exception.statics.ErrorCodes;
import org.springframework.http.HttpStatus;

/**
 * NotFoundExceptionの独自例外クラス
 */
public class NotFoundException extends TodoRuntimeException {
    /**
     * コンストラクタ
     *
     * @param errorCodes エラー内容に対応したエラーコード
     */
    public NotFoundException(ErrorCodes errorCodes) {
        super(HttpStatus.NOT_FOUND, errorCodes);
    }
}
