package com.handywork.loanchecker.service;

import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

/**
 * Loan service for handling and processing data over loan data.
 */
public interface LoanService {

    /**
     * Check new loans determinate by {@code lastRunDateTime} and print it via print service.
     *
     * @param lastRunDateTime the date time of last successful run of check and load new loans
     * @return reactive representation number of new loans
     */
    Mono<Long> checkAndPrintNewLoans(LocalDateTime lastRunDateTime);
}