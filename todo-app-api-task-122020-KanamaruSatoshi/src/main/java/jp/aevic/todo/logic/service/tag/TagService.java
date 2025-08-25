package jp.aevic.todo.logic.service.tag;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import jp.aevic.todo.core.exception.exception.NotFoundException;
import jp.aevic.todo.core.exception.exception.OptimisticLockException;
import jp.aevic.todo.core.exception.statics.ErrorCodes;
import jp.aevic.todo.entity.tag.TagEntity;
import jp.aevic.todo.mapper.tag.TagMapper;
import jp.aevic.todo.queryCondition.tag.GetTagsQueryCondition;

/**
 * ビジネスロジック用のServiceクラス
 * 
 */
@Service
public class TagService {

    // DI対象クラス
    private final TagMapper mapper;

    /**
     * コンストラクタ
     * 
     * @param mapper
     */
    public TagService(TagMapper mapper) {
        // コンストラクタインジェクション
        this.mapper = mapper;
    }

    /**
     * タグ新規作成
     * 
     * @param tagEntity
     * @return
     */
    public int postTag(TagEntity tagEntity) {
        mapper.insertTag(tagEntity);
        return tagEntity.getTagId();
    }

    /**
     * 1件取得
     * 
     * @param tagId
     * @return
     */
    public TagEntity getTagById(int tagId) {
        return findTodoOrThrow(tagId);
    }

    /**
     * 全件取得
     * 
     * @param queryCondition
     * @return
     */
    public List<TagEntity> getAllTags(GetTagsQueryCondition queryCondition) {
        return mapper.selectAll(queryCondition);
    }

    /**
     * 更新
     * 
     * @param tagEntity
     * @return
     */
    public void putTag(TagEntity tagEntity) {
        // 存在チェック
        findTodoOrThrow(tagEntity.getTagId());

        // 更新処理
        int updatedNum = mapper.updateTag(tagEntity);
        // 楽観ロックチェック
        updateOrDeleteFailedOrThrow(updatedNum);
    }

    /**
     * 削除
     * 
     * @param tagEntity
     */
    public void deleteTag(TagEntity tagEntity) {
        // 存在チェック
        findTodoOrThrow(tagEntity.getTagId());

        // 削除処理
        int deletedNum = mapper.deleteTag(tagEntity);
        // 楽観ロックチェック
        updateOrDeleteFailedOrThrow(deletedNum);
    }

    /**
     * 指定されたtodoIdが存在しなかった場合に404を投げる
     * 
     * @param tagId
     * @return
     */
    private TagEntity findTodoOrThrow(int tagId) {
        // todoIdよりtodoを検索
        TagEntity resultEntity = mapper.selectById(tagId);
        // todoが存在しなかった場合、404NotFoundを投げる
        if (Objects.isNull(resultEntity)) {
            // 404 notFound
            throw new NotFoundException(ErrorCodes.NOT_FOUND_RESOURCE);
        }
        return resultEntity;
    }

    /**
     * 楽観ロックにより更新/削除できなかった場合に409を投げる
     * 
     * @param resultNum
     */
    private void updateOrDeleteFailedOrThrow(int resultNum) {
        // 更新できなかった場合は409を投げる
        if (resultNum == 0) {
            // 409 optimistic
            throw new OptimisticLockException(ErrorCodes.OPTIMISTIC_LOCK);
        }
    }
}
