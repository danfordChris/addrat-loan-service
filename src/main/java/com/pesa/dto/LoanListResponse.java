package com.pesa.dto;

import java.util.List;

public class LoanListResponse {

    private List<LoanResponse> content;
    private Long totalElements;
    private Integer totalPages;
    private Integer currentPage;
    private Integer pageSize;

    public LoanListResponse() {
    }

    public LoanListResponse(List<LoanResponse> content, Long totalElements, Integer totalPages,
                           Integer currentPage, Integer pageSize) {
        this.content = content;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
    }

    public List<LoanResponse> getContent() {
        return content;
    }

    public void setContent(List<LoanResponse> content) {
        this.content = content;
    }

    public Long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(Long totalElements) {
        this.totalElements = totalElements;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private List<LoanResponse> content;
        private Long totalElements;
        private Integer totalPages;
        private Integer currentPage;
        private Integer pageSize;

        public Builder content(List<LoanResponse> content) {
            this.content = content;
            return this;
        }

        public Builder totalElements(Long totalElements) {
            this.totalElements = totalElements;
            return this;
        }

        public Builder totalPages(Integer totalPages) {
            this.totalPages = totalPages;
            return this;
        }

        public Builder currentPage(Integer currentPage) {
            this.currentPage = currentPage;
            return this;
        }

        public Builder pageSize(Integer pageSize) {
            this.pageSize = pageSize;
            return this;
        }

        public LoanListResponse build() {
            return new LoanListResponse(content, totalElements, totalPages, currentPage, pageSize);
        }
    }
}
