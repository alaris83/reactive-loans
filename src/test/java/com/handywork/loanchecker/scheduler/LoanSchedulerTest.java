package com.handywork.loanchecker.scheduler;

import com.handywork.loanchecker.service.LoanService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.concurrent.Semaphore;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LoanSchedulerTest {

    @Mock
    private LoanService service;

    private LoanScheduler scheduler;

    @Before
    public void setUp() {
        scheduler = new LoanScheduler(true, 2, service);
    }

    @Test
    public void shouldNotRunSchedulerWhenSchedulerEnabledFalse() {
        scheduler = new LoanScheduler(false, 2, service);

        scheduler.execute();

        verifyZeroInteractions(service);
    }

    @Test
    public void shoudNotExcecuteServiceWhenLockPermitsAreNotAvailable() {
        ReflectionTestUtils.setField(scheduler, "semaphore", new Semaphore(0));

        scheduler.execute();

        verifyZeroInteractions(service);
    }

    @Test
    public void shouldExecuteServiceWhenAllConditionsMet() {
        when(service.checkAndPrintNewLoans(any())).thenReturn(Mono.just(1L));
        final LocalDateTime started = LocalDateTime.now();

        scheduler.execute();

        verify(service).checkAndPrintNewLoans(any(LocalDateTime.class));

        final LocalDateTime lastRunDateTime = (LocalDateTime) ReflectionTestUtils.getField(scheduler,"lastRunDateTime");
        assertThat(lastRunDateTime).isAfterOrEqualTo(started);
    }

    @Test
    public void shouldExecuteServiceWhenAllConditionsFulfil() {
        when(service.checkAndPrintNewLoans(any())).thenReturn(Mono.error(new IllegalStateException("We have a problem.")));

        scheduler.execute();

        verify(service).checkAndPrintNewLoans(any(LocalDateTime.class));
        // when reactive stream is properly handled on error this method should pass without problem
    }
}