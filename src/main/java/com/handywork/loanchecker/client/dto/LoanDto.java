package com.handywork.loanchecker.client.dto;

import java.util.Objects;

/**
 * REST API read-only data model for Loan.
 */
public class LoanDto {

    // TODO define at least some subset of variables available at API, more could be added if needed
    private String url;
    private String name;
    private String story;
    private String purpose;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStory() {
        return story;
    }

    public void setStory(String story) {
        this.story = story;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoanDto loanDto = (LoanDto) o;
        return Objects.equals(url, loanDto.url) &&
                Objects.equals(name, loanDto.name) &&
                Objects.equals(story, loanDto.story) &&
                Objects.equals(purpose, loanDto.purpose);
    }

    @Override
    public int hashCode() {

        return Objects.hash(url, name, story, purpose);
    }

    @Override
    public String toString() {
        return "LoanDto{" +
                "url='" + url + '\'' +
                ", name='" + name + '\'' +
                ", story='" + story + '\'' +
                ", purpose='" + purpose + '\'' +
                '}';
    }
}
