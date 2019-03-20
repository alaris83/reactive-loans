package com.handywork.loanchecker.service.impl;

import com.handywork.loanchecker.client.dto.LoanDto;
import com.handywork.loanchecker.TestData;
import com.handywork.loanchecker.client.ZonkyApiClient;
import com.handywork.loanchecker.service.PrintService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LoanServiceImplTest {

    @Mock
    private PrintService printService;

    @Mock
    private ZonkyApiClient zonkyApiClient;

    @Captor
    private ArgumentCaptor<LoanDto> loanDtoArgumentCaptor;

    private LoanServiceImpl service;

    @Before
    public void setUp() {
        service = new LoanServiceImpl(printService, zonkyApiClient);
    }

    @Test
    public void shouldRunApiClientAndReturnCount() {
        when(zonkyApiClient.getMarketplaceAfterDatePublished(any(LocalDateTime.class))).thenReturn(Flux.just(TestData.LOAD_DTO_1, TestData.LOAD_DTO_2));

        StepVerifier.create(service.checkAndPrintNewLoans(LocalDateTime.now()))
                .expectNext(2L)
                .verifyComplete();

        verify(printService, times(2)).printToConsole(loanDtoArgumentCaptor.capture());

        final List<LoanDto> actualPrintValues = loanDtoArgumentCaptor.getAllValues();
        assertThat(actualPrintValues).containsExactlyInAnyOrder(TestData.LOAD_DTO_1, TestData.LOAD_DTO_2);
    }

    @Test
    public void shouldRunApiClientWhenNoNewLoansFound() {
        when(zonkyApiClient.getMarketplaceAfterDatePublished(any(LocalDateTime.class))).thenReturn(Flux.empty());

        StepVerifier.create(service.checkAndPrintNewLoans(LocalDateTime.now()))
                .expectNext(0L)
                .verifyComplete();

        verifyZeroInteractions(printService);
    }

    @Test
    public void shouldRunApiClientAndThrowExceptionWhenClientFails() {
        when(zonkyApiClient.getMarketplaceAfterDatePublished(any(LocalDateTime.class))).thenReturn(Flux.error(new IllegalStateException("We have a problem")));

        StepVerifier.create(service.checkAndPrintNewLoans(LocalDateTime.now()))
                .expectError(IllegalStateException.class);

        verifyZeroInteractions(printService);
    }
}