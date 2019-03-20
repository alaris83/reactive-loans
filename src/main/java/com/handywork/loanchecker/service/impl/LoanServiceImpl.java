package com.handywork.loanchecker.service.impl;

import com.handywork.loanchecker.scheduler.LoanScheduler;
import com.handywork.loanchecker.service.LoanService;
import com.handywork.loanchecker.client.ZonkyApiClient;
import com.handywork.loanchecker.service.PrintService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

/**
 * Implementation of {@link LoanService}.
 */
@Service
public class LoanServiceImpl implements LoanService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoanScheduler.class);

    // Use separate logger to control where print will be route to
    private final PrintService printService;

    private final ZonkyApiClient client;

    @Autowired
    public LoanServiceImpl(PrintService printService, ZonkyApiClient client) {
        this.printService = printService;
        this.client = client;
    }

    @Override
    public Mono<Long> checkAndPrintNewLoans(final LocalDateTime datePublished) {
        LOGGER.debug("Running checkAndPrintNewLoans for datePublished after: {}", datePublished);

        return client.getMarketplaceAfterDatePublished(datePublished)
                .doOnNext(printService::printToConsole)
                .count();
    }
}