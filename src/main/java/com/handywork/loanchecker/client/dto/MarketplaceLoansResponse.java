package com.handywork.loanchecker.client.dto;

import reactor.core.publisher.Flux;

/**
 * REST API response DTO for Marketplace loans endpoint with meta-information.
 */
public class MarketplaceLoansResponse {

    private Flux<LoanDto> loans;
    private int pageSize;
    private int page;
    private int total;

    public Flux<LoanDto> getLoans() {
        return loans;
    }

    public MarketplaceLoansResponse setLoans(Flux<LoanDto> loans) {
        this.loans = loans;
        return this;
    }

    public int getPageSize() {
        return pageSize;
    }

    public MarketplaceLoansResponse setPageSize(int pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    public int getPage() {
        return page;
    }

    public MarketplaceLoansResponse setPage(int page) {
        this.page = page;
        return this;
    }

    public int getTotal() {
        return total;
    }

    public MarketplaceLoansResponse setTotal(int total) {
        this.total = total;
        return this;
    }
}