package jp.aevic.todo.mapper.tag;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import jp.aevic.todo.entity.tag.TagEntity;
import jp.aevic.todo.queryCondition.tag.GetTagsQueryCondition;

/*
 * TagMapperインタフェース
 * 
 */
@Mapper
public interface TagMapper {

    /**
     * タグの新規登録
     * 
     * @param tagEntity
     */
    public void insertTag(TagEntity tagEntity);

    /**
     * 1件取得
     * 
     * @param tagId
     * @return
     */
    public TagEntity selectById(int tagId);

    /**
     * 複数件取得
     * 
     * @param tagIds
     * @return
     */
    public List<TagEntity> selectByIds(List<Integer> tagIds);

    /**
     * 一覧取得
     * 
     * @param queryCondition
     * @return
     */
    public List<TagEntity> selectAll(GetTagsQueryCondition queryCondition);

    /**
     * 更新
     * 
     * @param tagEntity
     * @return 更新件数
     */
    public int updateTag(TagEntity tagEntity);

    /**
     * 削除
     * 
     * @param tagEntity
     * @return 削除件数
     */
    public int deleteTag(TagEntity tagEntity);
}
