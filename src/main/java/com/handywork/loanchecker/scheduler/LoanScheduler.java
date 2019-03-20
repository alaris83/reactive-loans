package com.handywork.loanchecker.scheduler;

import com.handywork.loanchecker.service.LoanService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * Loan scheduler. In regular intervals check Zonky API and get new loans.
 */
@Component
public class LoanScheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoanScheduler.class);

    private final boolean schedulerEnabled;

    private final long lockWaitTimeout;

    private final LoanService service;

    private final Semaphore semaphore;

    private LocalDateTime lastRunDateTime;

    @Autowired
    public LoanScheduler(@Value("${loans.scheduler.enabled}") boolean schedulerEnabled,
                         @Value("${loans.scheduler.lock-wait-timeout-seconds}") long lockWaitTimeout,
                         LoanService service) {
        this.schedulerEnabled = schedulerEnabled;
        this.lockWaitTimeout = lockWaitTimeout;
        this.service = service;
        // Make sure that scheduler will run always one at the time - fixedRate does not support that,
        // we could use fixedDelay instead but then we will not execute scheduler each 5 minutes
        this.semaphore = new Semaphore(1);
        // initial setting for datePublish filter
        this.lastRunDateTime = LocalDateTime.now().minusHours(24);
    }

    @Scheduled(fixedRateString = "${loans.scheduler.fixrate-ms}")
    public void execute() {
        if (this.schedulerEnabled) {
            LOGGER.debug("Available permit : " + semaphore.availablePermits());
            try {
                if (semaphore.tryAcquire(lockWaitTimeout, TimeUnit.SECONDS)) {
                    final LocalDateTime runDateTime = LocalDateTime.now();
                    service.checkAndPrintNewLoans(lastRunDateTime)
                            .doOnSuccess(count -> {
                                lastRunDateTime = runDateTime;
                                LOGGER.info("Check and print new loans finished. Total number of new loans is: {}", count);
                            })
                            .doOnError(t -> LOGGER.error("Error when checking new loans.", t))
                            .doOnTerminate(semaphore::release) // if something goes wrong always release semaphore
                            .subscribe();
                } else {
                    LOGGER.warn("Unable to acquire a permit to schedule check and print new loans. Will try again in the next run.");
                }
            } catch (InterruptedException e) {
                LOGGER.warn("Waiting for check and print new loans scheduling permit was interrupted by timeout with message: {}", e.getMessage());
            }
        }
    }
}