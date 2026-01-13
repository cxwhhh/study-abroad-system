package com.study.service.news;

import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 新闻资讯服务实现类
 */
@Service
public class NewsServiceImpl implements NewsService {

    @Resource
    private NewsMapper newsMapper;

    @Override
    public List<News> getNewsList() {
        List<News> list = newsMapper.selectList();
        if (list == null || list.isEmpty()) {
            return list;
        }
        for (News item : list) {
            ensureRichContent(item);
        }
        return list;
    }

    @Override
    public News getNewsById(Long id) {
        if (id == null) {
            return null;
        }
        return ensureRichContent(newsMapper.selectById(id));
    }

    @Override
    public int createNews(News news) {
        // 设置初始浏览量和创建时间
        news.setViews(0);
        news.setCreateTime(new Date());
        ensureRichContent(news);
        return newsMapper.insert(news);
    }

    @Override
    public int updateNews(News news) {
        ensureRichContent(news);
        return newsMapper.updateById(news);
    }

    @Override
    public int deleteNews(Long id) {
        return newsMapper.deleteById(id);
    }

    private News ensureRichContent(News news) {
        if (news == null) {
            return null;
        }

        String content = safeTrim(news.getContent());
        if (content.length() >= 240) {
            return news;
        }

        String title = safeTrim(news.getTitle());
        String summary = safeTrim(news.getSummary());
        String categoryName = resolveCategoryName(safeTrim(news.getCategory()));

        StringBuilder sb = new StringBuilder();
        if (!content.isBlank()) {
            sb.append(content);
        } else if (!summary.isBlank()) {
            sb.append(summary);
        } else if (!title.isBlank()) {
            sb.append("本文围绕「").append(title).append("」整理要点，帮助你快速抓住关键信息。");
        } else {
            sb.append("本文整理了最新留学资讯要点，帮助你快速抓住关键信息。");
        }

        if (sb.length() > 0) {
            sb.append("\n\n");
        }

        String heading = !title.isBlank() ? title : categoryName + "要点整理";
        sb.append("一、信息概览").append("\n")
                .append("围绕「").append(heading).append("」，我们从时间线、适用对象、核心变化与落地建议四个角度进行梳理。")
                .append("\n\n");

        sb.append("二、核心要点").append("\n")
                .append("1）适用对象：结合你的学历背景、语言成绩、目标院校层级与专业方向判断是否匹配。").append("\n")
                .append("2）关键变化：重点关注政策口径、材料清单、截止时间、费用变化与审核周期。").append("\n")
                .append("3）风险提示：以官方发布时间与原文链接为准，避免只看二手解读导致误判。")
                .append("\n\n");

        sb.append("三、行动建议（可直接照做）").append("\n")
                .append("1）把关键节点写进日历：网申开放、材料递交、补件截止、面试/笔试、出结果。").append("\n")
                .append("2）准备一份“材料主档案”：成绩单、在读/毕业证明、护照/身份证件、简历、推荐人信息、作品集/科研证明等。").append("\n")
                .append("3）把内容拆成清单逐项核对：每完成一项就留存截图或回执，避免返工。")
                .append("\n\n");

        sb.append("四、常见问题").append("\n")
                .append("Q1：时间紧来不及怎么办？").append("\n")
                .append("优先保证“硬性门槛”材料齐全（语言/成绩/身份证明），其余材料按院校要求补全。").append("\n")
                .append("Q2：如何判断信息是否可靠？").append("\n")
                .append("优先查看学校官网、官方公告与正式邮件通知；若信息冲突，以官方为准。")
                .append("\n\n");

        sb.append("五、下一步（根据").append(categoryName).append("场景）").append("\n")
                .append("如果你希望我们结合你的背景给出更具体的路径建议，可以准备：目标国家/学校、专业方向、目前成绩与语言情况、预计入学时间。")
                .append("\n");

        news.setContent(sb.toString().trim());
        return news;
    }

    private String resolveCategoryName(String raw) {
        if (raw == null || raw.isBlank()) {
            return "留学";
        }
        String v = raw.trim();
        if ("policy".equalsIgnoreCase(v) || v.contains("政策")) {
            return "政策";
        }
        if ("application".equalsIgnoreCase(v) || v.contains("申请") || v.contains("指南")) {
            return "申请";
        }
        if ("life".equalsIgnoreCase(v) || v.contains("生活")) {
            return "生活";
        }
        if ("career".equalsIgnoreCase(v) || v.contains("就业") || v.contains("职业")) {
            return "就业";
        }
        return "留学";
    }

    private String safeTrim(String value) {
        return value == null ? "" : value.trim();
    }
}
