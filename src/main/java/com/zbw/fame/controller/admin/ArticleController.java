package com.zbw.fame.controller.admin;

import com.zbw.fame.controller.BaseController;
import com.zbw.fame.model.Articles;
import com.zbw.fame.model.Users;
import com.zbw.fame.service.ArticlesService;
import com.zbw.fame.service.LogsService;
import com.zbw.fame.util.FameUtil;
import com.zbw.fame.util.RestResponse;
import com.zbw.fame.util.Types;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 后台文章管理 Controller
 *
 * @author zbw
 * @create 2017/7/11 19:52
 */
@RestController
@RequestMapping("/admin/article")
public class ArticleController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(ArticleController.class);

    @Autowired
    private ArticlesService articlesService;

    @Autowired
    private LogsService logsService;


    /**
     * 文章信息列表
     *
     * @param page
     * @return
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    public RestResponse index(@RequestParam(required = false, defaultValue = "1") Integer page) {
        List<Articles> articles = articlesService.getArticles(page);
        return RestResponse.ok(articles);
    }

    /**
     * 单个文章信息
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/{id}")
    public RestResponse showArticle(@PathVariable Integer id) {
        Articles article = articlesService.get(id);
        if (null == article) {
            return this.error_404();
        }
        return RestResponse.ok(article);
    }

    /**
     * 保存文章
     *
     * @param id
     * @param title
     * @param content
     * @param tags
     * @param category
     * @param status
     * @param allowComment
     * @return
     */
    @RequestMapping(value = "", method = RequestMethod.POST)
    public RestResponse saveArticle(@RequestParam(value = "id", required = false) Integer id,
                                    @RequestParam(value = "title") String title,
                                    @RequestParam(value = "content") String content,
                                    @RequestParam(value = "tags") String tags,
                                    @RequestParam(value = "category") String category,
                                    @RequestParam(value = "status", defaultValue = Types.DRAFT) String status,
                                    @RequestParam(value = "allowComment", defaultValue = "false") Boolean allowComment) {
        Users user = this.user();
        if (null == user) {
            return RestResponse.fail("未登陆，请先登陆");
        }
        Articles article = new Articles();
        if (!StringUtils.isEmpty(id)) {
            article.setId(id);
        }
        article.setTitle(title);
        article.setContent(content);
        article.setTags(tags);
        article.setCategory(category);
        article.setStatus(status);
        article.setAllowComment(allowComment);
        article.setAuthorId(user.getId());
        articlesService.saveArticle(article);
        return RestResponse.ok("保存文章成功");
    }

    /**
     * 删除文章
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public RestResponse deleteArticle(@PathVariable Integer id) {
        if (articlesService.deleteArticle(id)) {
            logsService.save(Types.LOG_ACTION_DELETE, "id:" + id, Types.LOG_MESSAGE_DELETE_ARTICLE, Types.LOG_TYPE_OPERATE, FameUtil.getIp());
            return RestResponse.ok("删除文章成功");
        } else {
            return RestResponse.fail();
        }
    }

    @RequestMapping(value = "/count", method = RequestMethod.GET)
    public RestResponse count() {
        return RestResponse.ok(articlesService.count());
    }

}