package com.study.service.news;

import com.study.common.Result;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/news")
public class NewsController {

    @Resource
    private NewsService newsService;

    @GetMapping
    public ResponseEntity<Result<List<News>>> list() {
        return ResponseEntity.ok(Result.success(newsService.getNewsList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Result<News>> detail(@PathVariable Long id) {
        News news = newsService.getNewsById(id);
        if (news == null) {
            return ResponseEntity.ok(Result.error(404, "新闻未找到"));
        }
        return ResponseEntity.ok(Result.success(news));
    }

    @PostMapping
    public ResponseEntity<Result<News>> create(@RequestBody News news) {
        int inserted = newsService.createNews(news);
        if (inserted <= 0) {
            return ResponseEntity.ok(Result.error("创建新闻失败"));
        }
        return ResponseEntity.ok(Result.success(news));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Result<News>> update(@PathVariable Long id, @RequestBody News news) {
        news.setId(id);
        int updated = newsService.updateNews(news);
        if (updated <= 0) {
            return ResponseEntity.ok(Result.error(404, "新闻未找到"));
        }
        News fresh = newsService.getNewsById(id);
        return ResponseEntity.ok(Result.success(fresh != null ? fresh : news));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Result<Void>> delete(@PathVariable Long id) {
        int deleted = newsService.deleteNews(id);
        if (deleted <= 0) {
            return ResponseEntity.ok(Result.error(404, "新闻未找到"));
        }
        return ResponseEntity.ok(Result.success(null));
    }
}
