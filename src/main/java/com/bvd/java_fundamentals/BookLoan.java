package com.bvd.java_fundamentals;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

public record BookLoan(
        String loanId,
        String memberId,
        LocalDate loanDate,
        String bookTitle,
        String genre,
        String author,
        int daysLoaned
) {

    @JsonCreator
    public static BookLoan fromJson(
            @JsonProperty("loanId") String loanId,
            @JsonProperty("memberId") String memberId,
            @JsonProperty("loanDate") String loanDate,
            @JsonProperty("bookTitle") String bookTitle,
            @JsonProperty("genre") String genre,
            @JsonProperty("author") String author,
            @JsonProperty("daysLoaned") String daysLoaned
    ) {
        return new BookLoan(
                loanId,
                memberId,
                loanDate == null ? null : LocalDate.parse(loanDate),
                bookTitle,
                genre,
                author,
                daysLoaned == null ? 0 : Integer.parseInt(daysLoaned)
        );
    }

    @Override
    public String toString() {
        return "BookLoan{" +
                "loanId='" + loanId + '\'' +
                ", memberId='" + memberId + '\'' +
                ", loanDate=" + loanDate +
                ", bookTitle='" + bookTitle + '\'' +
                ", genre='" + genre + '\'' +
                ", author='" + author + '\'' +
                ", daysLoaned=" + daysLoaned +
                '}';
    }
}

