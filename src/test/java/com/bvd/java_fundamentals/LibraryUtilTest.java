package com.bvd.java_fundamentals;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class LibraryUtilTest {

    @Test
    void csvParsing_validAndMalformed() {
        List<String> lines = List.of(
                "L1,M1,2024-01-01,Book A,Fantasy,Author A,10",
                "BAD_LINE"
        );

        var parsed = LibraryUtil.parseCsvLines(lines);

        assertEquals(1, parsed.get("valid").size());
        assertEquals(1, parsed.get("malformed").size());
    }

    @Test
    void loansByGenre_sorted() {
        var loans = List.of(
                new BookLoan("1","M1",null,"A","Fantasy","X",5),
                new BookLoan("2","M2",null,"B","Classic","Y",5)
        );

        Map<String, Long> result = LibraryUtil.loansByGenre(loans);
        assertEquals(List.of("Classic","Fantasy"), result.keySet().stream().toList());
    }

    @Test
    void topAuthors() {
        var loans = List.of(
                new BookLoan("1","M1",null,"A","G","A1",5),
                new BookLoan("2","M2",null,"B","G","A1",5),
                new BookLoan("3","M3",null,"C","G","A2",5)
        );

        assertEquals(List.of("A1"), LibraryUtil.topAuthorsByLoans(loans, 1));
    }

    @Test
    void diversity() {
        var loans = List.of(
                new BookLoan("1","M1",null,"A","G1","A",5),
                new BookLoan("2","M1",null,"B","G2","A",5)
        );

        assertEquals(List.of("M1"), LibraryUtil.membersWithGenreDiversity(loans, 2));
    }

    @Test
    void findBook() {
        var loans = List.of(
                new BookLoan("1","M1",null,"Dune","SF","A",5)
        );

        assertTrue(LibraryUtil.findFirstBookContaining(loans,"du").isPresent());
        assertTrue(LibraryUtil.isBookPresent(loans,"DUNE"));
    }

    @Test
    void jsonParsing() {
        List<String> json = List.of(
                "{\"loanId\":\"L1\",\"memberId\":\"M1\",\"loanDate\":\"2024-01-01\",\"bookTitle\":\"Dune\",\"genre\":\"SF\",\"author\":\"Frank\",\"daysLoaned\":10}"
        );

        var loans = LibraryUtil.parseJsonLines(json);
        assertEquals(1, loans.size());
        assertEquals("Dune", loans.get(0).bookTitle());
    }
}
