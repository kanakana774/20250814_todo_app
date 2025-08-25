package jp.aevic.todo.testTools;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * テスト内でのみ使用する便利パーツをまとめておくクラス
 */
public class TestUtil {
    /**
     * ObjectをJSONに変換
     *
     * @param object 変換元のObject
     * @return JSONに変換したものを返却
     */
    public static String convertJSON(Object object) {
        //変換に使用するMapperの設定
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

        try {
            //JSONへの変換
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            //テストでしか使用しないので、スタックトレースで確認
            e.printStackTrace();
            //JSON型への変換が失敗したら、nullを返却
            return null;
        }
    }
}