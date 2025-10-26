package com.myarea.myarea.response;

import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
public class PageResponse<T> {
    private List<T> content;     // 현재 페이지의 데이터 (예: List<PostDto>)
    private int page;            // 현재 페이지 번호
    private int size;            // 페이지당 개수
    private int totalPages;      // 전체 페이지 수
    private long totalElements;  // 전체 게시글 개수
    private boolean first;       // 첫 페이지 여부
    private boolean last;        // 마지막 페이지 여부

    public PageResponse(Page<T> pageData) {
        this.content = pageData.getContent();
        this.page = pageData.getNumber();
        this.size = pageData.getSize();
        this.totalPages = pageData.getTotalPages();
        this.totalElements = pageData.getTotalElements();
        this.first = pageData.isFirst();
        this.last = pageData.isLast();
    }
}
