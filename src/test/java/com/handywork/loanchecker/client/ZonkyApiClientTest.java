package com.handywork.loanchecker.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit.WireMockClassRule;
import com.handywork.loanchecker.TestData;
import com.handywork.loanchecker.client.dto.LoanDto;
import com.handywork.loanchecker.configuration.ZonkyConfiguration;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.Arrays;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ZonkyApiClientTest {

    private static final int PORT = 9080;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @ClassRule
    public static WireMockClassRule wireMockClassRule = new WireMockClassRule(PORT);

    @Autowired
    private ZonkyConfiguration zonkyConfiguration;

    @Autowired
    private ZonkyApiClient zonkyApiClient;

    @Before
    public void setup() {
        wireMockClassRule.resetAll();
    }

    @Test
    public void shouldGetLoanDataFromZonkyApiWhenNoNewDataFound() {

        wireMockClassRule.stubFor(get(urlPathEqualTo(zonkyConfiguration.getLoansMarketplacePath()))
                .withHeader("X-Page", equalTo("0"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", "application/json")
                        .withHeader("X-Total", "0")
                        .withBody("[]")));

        StepVerifier.create(zonkyApiClient.getMarketplaceAfterDatePublished(LocalDateTime.now()))
                .verifyComplete();

    }

    @Test
    public void shouldGetLoanDataFromZonkyApiWhenOnePageContainsAllData() throws Exception {

        wireMockClassRule.stubFor(get(urlPathEqualTo(zonkyConfiguration.getLoansMarketplacePath()))
                        .withHeader("X-Page", equalTo("0"))
                        .willReturn(aResponse()
                                .withStatus(HttpStatus.OK.value())
                                .withHeader("Content-Type", "application/json")
                                .withHeader("X-Total", "2")
                                .withBody(objectMapper.writeValueAsString(Arrays.asList(TestData.LOAD_DTO_1, TestData.LOAD_DTO_2)))));

        StepVerifier.create(zonkyApiClient.getMarketplaceAfterDatePublished(LocalDateTime.now()))
                .expectNext(TestData.LOAD_DTO_1, TestData.LOAD_DTO_2)
                .verifyComplete();

    }

    @Test
    public void shouldGetLoanDataFromZonkyApiWhenPaginationIsApplied() throws Exception {
        final LoanDto loanDto3 = TestData.generateLoadDto(3);
        final LoanDto loanDto4 = TestData.generateLoadDto(4);

        wireMockClassRule.stubFor(get(urlPathEqualTo(zonkyConfiguration.getLoansMarketplacePath()))
                .withHeader("X-Page", equalTo("0"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", "application/json")
                        .withHeader("X-Total", "4")
                        .withBody(objectMapper.writeValueAsString(Arrays.asList(TestData.LOAD_DTO_1, TestData.LOAD_DTO_2)))));

        wireMockClassRule.stubFor(get(urlPathEqualTo(zonkyConfiguration.getLoansMarketplacePath()))
                .withHeader("X-Page", equalTo("1"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", "application/json")
                        .withHeader("X-Total", "4")
                        .withBody(objectMapper.writeValueAsString(Arrays.asList(loanDto3, loanDto4)))));

        StepVerifier.create(zonkyApiClient.getMarketplaceAfterDatePublished(LocalDateTime.now()))
                .expectNext(TestData.LOAD_DTO_1, TestData.LOAD_DTO_2, loanDto3, loanDto4)
                .verifyComplete();
    }

    @Test
    public void shouldHandleExceptionFromZonkyApiWhen4xxRecieved() {

        wireMockClassRule.stubFor(get(urlPathEqualTo(zonkyConfiguration.getLoansMarketplacePath()))
                .withHeader("X-Page", equalTo("0"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.BAD_REQUEST.value())
                        .withHeader("Content-Type", "application/json")
                        .withHeader("X-Total", "2")
                        .withBody("Something goes wrong with your request")));

        StepVerifier.create(zonkyApiClient.getMarketplaceAfterDatePublished(LocalDateTime.now()))
                .verifyError(IllegalArgumentException.class);
    }

    @Test
    public void shouldHandleExceptionFromZonkyApiWhen5xxRecieved() {

        wireMockClassRule.stubFor(get(urlPathEqualTo(zonkyConfiguration.getLoansMarketplacePath()))
                .withHeader("X-Page", equalTo("0"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .withHeader("Content-Type", "application/json")
                        .withHeader("X-Total", "2")
                        .withBody("Something goes wrong.")));

        StepVerifier.create(zonkyApiClient.getMarketplaceAfterDatePublished(LocalDateTime.now()))
                .verifyError(IllegalStateException.class);
    }
}