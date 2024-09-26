package com.joblinker.domain.dto;

public class Meta {
    private int page;      // Current page number (0-based index)
    private int pageSize;  // Number of records per page
    private int pages;     // Total number of pages
    private Long total;    // Total number of records
    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }
}
