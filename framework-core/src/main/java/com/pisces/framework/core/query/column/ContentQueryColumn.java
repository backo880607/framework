package com.pisces.framework.core.query.column;

/**
 * 内容查询列
 *
 * @author jason
 * @date 2023/07/15
 */
public class ContentQueryColumn extends QueryColumn {
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    private String content;

    public ContentQueryColumn(String content) {
        this.content = content;
    }
}
