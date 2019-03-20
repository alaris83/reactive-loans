package com.handywork.loanchecker.client;

import com.handywork.loanchecker.client.dto.LoanDto;
import com.handywork.loanchecker.client.dto.MarketplaceLoansResponse;
import com.handywork.loanchecker.configuration.ZonkyConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static com.handywork.loanchecker.util.ZonkyConstants.X_PAGE_HEADER;
import static com.handywork.loanchecker.util.ZonkyConstants.X_SIZE_HEADER;
import static com.handywork.loanchecker.util.ZonkyConstants.X_TOTAL_HEADER;

/**
 * Simple Zonky API client with using WebFlux Spring client.
 */
@Component
public class ZonkyApiClient {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    private final ZonkyConfiguration config;

    private final WebClient client;

    @Autowired
    public ZonkyApiClient(ZonkyConfiguration zonkyConfiguration) {
        this.config = zonkyConfiguration;
        this.client = WebClient.builder()
                .baseUrl(this.config.getServiceUri())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE)
                .build();
    }

    /**
     * Get Marketplace loans after specific published date.
     *
     * @param datePublished the published date
     * @return reactive stream of all loans published after {@code datePublished}
     */
    public Flux<LoanDto> getMarketplaceAfterDatePublished(final LocalDateTime datePublished) {

        return this.getMarketplaceAfterDatePublished(0, datePublished)
                .flatMapMany(firstResponse -> {
                    // after first request count how many iteration we need to get all data
                    final int totalLoops = (int) Math.ceil((double) firstResponse.getTotal() / firstResponse.getPageSize());
                    // prepare publishers for for each page
                    final List<Flux<LoanDto>> publishers = new ArrayList<>();
                    // add loans from first response
                    publishers.add(firstResponse.getLoans());
                    for (int i = 1; i < totalLoops; i++) {
                        publishers.add(this.getMarketplaceAfterDatePublished(i, datePublished).flatMapMany(MarketplaceLoansResponse::getLoans));
                    }
                    // merge by default supports concurrent execution of all publishers, them merge them into single flux
                    return Flux.merge(publishers);
                });
    }

    private Mono<MarketplaceLoansResponse> getMarketplaceAfterDatePublished(final int page, final LocalDateTime datePublished) {

        return client.get()
                .uri(builder -> builder
                        .path(config.getLoansMarketplacePath())
                        .queryParam("datePublished__gt", formatter.format(datePublished))
                        .build())
                .header(X_PAGE_HEADER, Integer.toString(page))
                .header(X_SIZE_HEADER, Integer.toString(config.getBatchSize()))
                .exchange() // because  we need to pick value from header we cannot use retrieve method and map body directly
                .flatMap(clientResponse -> {
                    if (clientResponse.statusCode().is2xxSuccessful()) {
                        return Mono.just(new MarketplaceLoansResponse()
                                .setPage(page)
                                .setPageSize(config.getBatchSize()).setTotal(Integer.parseInt(clientResponse.headers().asHttpHeaders().getFirst(X_TOTAL_HEADER)))
                                .setLoans(clientResponse.bodyToFlux(LoanDto.class)));
                    } else if (clientResponse.statusCode().is4xxClientError()) {
                        return clientResponse.bodyToMono(String.class).flatMap(err -> Mono.error(new IllegalArgumentException("Bad request to Zonky API client: " + err)));
                    }
                    return clientResponse.bodyToMono(String.class).flatMap(err -> Mono.error(new IllegalStateException("Zonky API is not available due: " + err)));
                });
    }

}