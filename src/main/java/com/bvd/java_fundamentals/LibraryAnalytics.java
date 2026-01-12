package com.bvd.java_fundamentals;

import java.util.List;

public class LibraryAnalytics {

    public static void main(String[] args) {
        List<String> csv = LibraryUtil.loadResourceFile("loans/libraryLoans.csv");
        var parsed = LibraryUtil.parseCsvLines(csv);
        var valid = parsed.get("valid");

        System.out.println("Valid: " + valid.size());
        System.out.println("Malformed: " + parsed.get("malformed").size());
        System.out.println("Loans by genre: " + LibraryUtil.loansByGenre(valid));
        System.out.println("Top authors: " + LibraryUtil.topAuthorsByLoans(valid, 2));
        System.out.println("Diverse members: " + LibraryUtil.membersWithGenreDiversity(valid, 3));
        System.out.println("Contains Dune: " + LibraryUtil.isBookPresent(valid, "Dune"));
    }
}
