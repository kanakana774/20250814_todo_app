package jp.aevic.todo.core.exception;

import io.micrometer.observation.Observation;
import jp.aevic.todo.core.exception.exception.BadRequestException;
import jp.aevic.todo.core.exception.exception.TodoRuntimeException;
import jp.aevic.todo.core.exception.model.MessageParts;
import jp.aevic.todo.core.exception.statics.ErrorCodes;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.Locale;
import java.util.Objects;

/**
 * 例外が起きた際の独自ログ出力クラス
 */
@Component
@AllArgsConstructor
public class MyExceptionLogger {
    //スタックトレースにエラー情報出力
    private final Logger logger = LoggerFactory.getLogger(MyExceptionLogger.class);

    //依存クラス
    private final MessageSource messageSource;

    //スタックトレースへのDEBUGレベルのログの出力
    private void logLoggerDebug(String uri, String requestMethod, String userAgent,
                                String errorMessage) {
        String format =
                "URI: {}, method: {}, user-agent: {}, ErrorMessage: {}";
        //DEBUGログの出力
        logger.debug(format, uri, requestMethod, userAgent, errorMessage);
    }

    /**
     * 例外のDEBUGレベルのログ出力メソッド
     * クラス名,エラーコード,エラーメッセージを出力
     *
     * @param ex 例外
     */
    public void printDebugLog(TodoRuntimeException ex, WebRequest webRequest) {
        MessageParts messageParts = getMessageParts(webRequest);

        //DEBUGログの出力
        logLoggerDebug(messageParts.getPath(),
                messageParts.getMethod(),
                messageParts.getUserAgent(),
                messageSource.getMessage(Objects.requireNonNull(ex.getCode().getCode()),
                        new String[]{ex.getMessage()}, Locale.getDefault()));

        //例外クラスのロガーの生成
        Logger loggerOriginClass = LoggerFactory.getLogger(ex.getClass());

        //DEBUGログの出力
        loggerOriginClass.debug("class: {}, {}: {}",
                ex.getClass().getName(),
                ex.getCode().getCode(),
                messageSource.getMessage(ex.getDetailMessageCode(), ex.getDetailMessageArguments(),
                        Locale.getDefault()));
    }

    /**
     * 例外のErrorレベルのログ出力メソッド
     * URI,method,エラーメッセージを出力
     *
     * @param ex         例外
     * @param webRequest リクエストデータ
     */
    public void printErrorLog(TodoRuntimeException ex, WebRequest webRequest) {
        //ロガーの生成
        Logger loggerOriginClass = LoggerFactory.getLogger(ex.getClass());

        //ERRORログの出力
        //出力内容が問題ないことは目視で担保
        loggerOriginClass.error("{}ErrorMessage: {}",
                commonLog(webRequest, ex),
                messageSource.getMessage(ex.getCode().getCode(), new String[]{ex.getMessage()},
                        Locale.getDefault()), ex);
    }

    /**
     * BadRequestExceptionのDEBUGレベルのログ出力メソッド
     * クラス名,エラーコード,エラーメッセージと
     * 不正のあったパラメーターのfield名,fieldエラーコード,エラーの起きた理由を出力
     *
     * @param ex BadRequestException
     */
    public void printBadRequestDebugLog(BadRequestException ex, WebRequest webRequest) {
        MessageParts messageParts = getMessageParts(webRequest);

        //DEBUGログの出力
        logLoggerDebug(messageParts.getPath(),
                messageParts.getMethod(),
                messageParts.getUserAgent(),
                messageSource.getMessage(Objects.requireNonNull(ex.getCode().getCode()),
                        new String[]{ex.getMessage()}, Locale.getDefault()));

        //例外クラスのロガーの生成
        Logger loggerOriginClass = LoggerFactory.getLogger(ex.getClass());

        //例外クラスのDEBUGログの出力
        loggerOriginClass.debug("class: {}, ErrorCode: {} ErrorMessage: {}",
                ex.getClass().getName(),
                ex.getCode().getCode(),
                messageSource.getMessage(Objects.requireNonNull(ex.getCode().getCode()),
                        new String[]{ex.getMessage()}, Locale.getDefault()));
    }

    /**
     * NoHandlerFoundExceptionのDEBUGレベルのログ出力メソッド
     * URI,メソッド,エラーコード,エラーメッセージを出力する
     *
     * @param ex NoHandlerFoundException
     */
    public void printNoHandlerFoundExceptionDebugLog(NoHandlerFoundException ex) {
        //ログに出力
        logger.debug("URI: {}, method: {}, ErrorCode: {}, ErrorMessage: {}",
                ex.getRequestURL(),
                ex.getHttpMethod(),
                ErrorCodes.NOT_FOUND_PATH.getCode(),
                messageSource.getMessage(ErrorCodes.NOT_FOUND_PATH.getCode(),
                        new String[]{ex.getMessage()}, Locale.getDefault()));
    }

    /**
     * TodoRuntimeException(独自の例外)以外のERRORレベルのログ出力メソッド
     * URI,メソッド,例外の生じたクラス名,エラーメッセージを出力
     *
     * @param ex         投げられる可能性のある例外
     * @param webRequest リクエストデータ
     */
    public void printExceptionErrorLog(Throwable ex, WebRequest webRequest) {
        logger.error("{} ErrorMessage: {}",
                commonLog(webRequest, ex),
                messageSource.getMessage(ErrorCodes.INTERNAL_UNEXPECTED_ERROR.getCode(),
                        new String[]{ex.getMessage()}, Locale.getDefault()), ex);
    }

    /**
     * ハンドリングできない例外(getErrorでエラーを抽出できないような詳細不明なエラー)の場合のERRORレベルのログ出力メソッド
     * URI,メソッド,エラーメッセージを出力
     *
     * @param webRequest リクエストデータ
     */
    public void printUnexpectedErrorLog(WebRequest webRequest) {
        //ログ生成に必要な部品を格納したMessagePartsの生成
        MessageParts messageParts = getMessageParts(webRequest);
        String format =
                "URI: {}, method: {}, user-agent: {}, ErrorMessage: {}, ErrorDetail: 抽出できないエラーが発生しました。";
        logger.error(format,
                messageParts.getPath(),
                messageParts.getMethod(),
                messageParts.getUserAgent(),
                messageSource.getMessage(ErrorCodes.INTERNAL_UNEXPECTED_ERROR.getCode(),
                        new String[]{}, Locale.getDefault()));
    }

    /**
     * 全てのログに共通で出力する文字列を生成するメソッド
     *
     * @param webRequest リクエストデータ
     * @param ex         例外
     * @return ログの共通部分の文字列
     */
    private String commonLog(WebRequest webRequest, Throwable ex) {
        //ログ生成に必要な部品を格納したMessagePartsの生成
        MessageParts messageParts = getMessageParts(webRequest);

        return "URI: " + messageParts.getPath()
                + ", method: " + messageParts.getMethod()
                + ", user-agent: " + messageParts.getUserAgent()
                + ", Exception: " + ex.getClass().getName()
                + ", ";
    }

    /**
     * logのメッセージの生成に必要なリクエストの情報を含んだMessagePartsを生成する
     * 取得場所はリクエスト情報を追跡して保持しているorg.springframework.web.filter.ServerHttpObservationFilter.contextから取得
     *
     * @param webRequest リクエストデータ
     * @return logのメッセージの生成に必要なリクエストの情報で構成されるBean
     */
    private MessageParts getMessageParts(WebRequest webRequest) {
        Observation.Context observation =
                (Observation.Context) webRequest.getAttribute(
                        "org.springframework.web.filter.ServerHttpObservationFilter.context", 0);
        String method = Objects.requireNonNull(
                        Objects.requireNonNull(observation).getLowCardinalityKeyValue("method"))
                .getValue();
        String path = Objects.requireNonNull(
                observation.getHighCardinalityKeyValue("http.url")).getValue();
        String userAgent = webRequest.getHeader("User-Agent");
        return new MessageParts(path, method, userAgent);
    }
}