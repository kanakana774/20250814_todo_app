package jp.aevic.todo.app.controller.tag;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jp.aevic.todo.entity.tag.TagEntity;
import jp.aevic.todo.form.tag.TagDeleteForm;
import jp.aevic.todo.form.tag.TagPostFrom;
import jp.aevic.todo.form.tag.TagPutForm;
import jp.aevic.todo.logic.service.tag.TagService;
import jp.aevic.todo.query.tag.GetTagsQuery;
import jp.aevic.todo.queryCondition.tag.GetTagsQueryCondition;
import jp.aevic.todo.util.LocationUtil;
import jp.aevic.todo.util.statics.CreatedLocationPaths;

/**
 * TagのControllerクラス
 */
@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping(value = "/tags")
public class TagController {

    // DI対象クラス
    private final LocationUtil locationUtil;
    private final TagService service;

    /**
     * コンストラクタ
     * 
     * @param locationUtil
     * @param service
     */
    public TagController(LocationUtil locationUtil, TagService service) {
        // コンストラクタインジェクション
        this.locationUtil = locationUtil;
        this.service = service;
    }

    /**
     * 新規tag登録
     * 
     * @param form
     * @return
     */
    @PostMapping
    public ResponseEntity<String> postTag(@RequestBody @Validated TagPostFrom form) {

        TagEntity tagEntity = new TagEntity();
        tagEntity.setName(form.getName());
        tagEntity.setVersion(0);

        int tagId = service.postTag(tagEntity);

        // httpレスポンスヘッダにURIを指定するため、ResponseEntityを生成して返す
        URI location = locationUtil.create(CreatedLocationPaths.TAG, tagId);
        return ResponseEntity.created(location).build();
    }

    /**
     * 一件取得
     * 
     * @param tagId
     * @return
     */
    @GetMapping(path = "/{tagId}")
    public TagEntity getTagById(@PathVariable String tagId) {
        return service.getTagById(Integer.parseInt(tagId));
    }

    /**
     * 一覧取得
     * 
     * @param query
     * @return
     */
    @GetMapping
    public List<TagEntity> getAllTags(GetTagsQuery query) {
        GetTagsQueryCondition queryCondition = new GetTagsQueryCondition();
        queryCondition.setName(query.getName());
        return service.getAllTags(queryCondition);
    }

    /**
     * 更新
     * 
     * @param tagId
     * @param form
     * @return
     */
    @PutMapping(path = "/{tagId}")
    public ResponseEntity<String> putTag(
            @PathVariable String tagId, @RequestBody @Validated TagPutForm form) {

        TagEntity tagEntity = new TagEntity();
        tagEntity.setTagId(Integer.parseInt(tagId));
        tagEntity.setName(form.getName());
        tagEntity.setVersion(form.getVersion());

        service.putTag(tagEntity);

        // ResponseEntityを生成して返す
        return ResponseEntity.noContent().build();
    }

    /**
     * 削除
     * 
     * @param tagId
     * @param form
     * @return
     */
    @DeleteMapping(path = "/{tagId}")
    public ResponseEntity<String> deleteTag(
            @PathVariable String tagId, @RequestBody @Validated TagDeleteForm form) {

        TagEntity tagEntity = new TagEntity();
        tagEntity.setTagId(Integer.parseInt(tagId));
        tagEntity.setVersion(form.getVersion());

        service.deleteTag(tagEntity);

        // ResponseEntityを生成して返す
        return ResponseEntity.noContent().build();
    }
}
