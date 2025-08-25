package jp.aevic.todo.mapper.todo;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import jp.aevic.todo.entity.todo.TodoTagEntity;

/**
 * TodoTagMapperインタフェース
 * 
 */
@Mapper
public interface TodoTagMapper {
    /**
     * 新規todo_tag登録
     * 
     * @param todoTagEntities
     */
    public void insertTodoTag(List<TodoTagEntity> todoTagEntities);

    /**
     * todoIdより削除
     * 
     * @param todoId
     */
    public void deleteTodoTagByTodoId(int todoId);
}
