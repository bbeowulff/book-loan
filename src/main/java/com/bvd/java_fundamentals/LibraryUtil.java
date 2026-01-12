package com.bvd.java_fundamentals;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class LibraryUtil
{

    private LibraryUtil() {}

    public static List<String> loadResourceFile(String filename)
    {
        try (InputStream is = LibraryUtil.class
                .getClassLoader()
                .getResourceAsStream(filename))
        {
            if ( is == null)
            {
                throw new IllegalArgumentException("resource not found: " + filename);
            }
            return new String(is.readAllBytes(), StandardCharsets.UTF_8)
                        .lines()
                        .map(String::trim)
                        .filter(s -> !s.isBlank())
                        .toList();

        } catch (Exception e) {
            throw new IllegalStateException("Failed to load resource: " + filename, e);
        }
    }


    public static Map<String, List<BookLoan>> parseCsvLines(List<String> lines)
    {
        if (lines == null || lines.isEmpty())
        {
            return Map.of("valid", List.of(), "malformed", List.of());
        }

        record Parsed(BookLoan loan, String malformedId) {}

        Map<Boolean, List<Parsed>> partitioned = lines.stream()
                .map(line -> {
                    String id = (line == null) ? "" : extractId(line);

                    try {
                        if (line == null || line.isBlank()) {
                            return new Parsed(null, id);
                        }

                        String[] raw = line.split(",", -1);
                        for (int i = 0; i < raw.length; i++) {
                            raw[i] = raw[i] == null ? "" : raw[i].trim();
                        }

                        int end = raw.length;
                        while (end > 0 && raw[end - 1].isBlank()) end--;

                        if (end != 7) {
                            return new Parsed(null, id);
                        }

                        String loanId = raw[0];
                        String memberId = raw[1];
                        LocalDate date = LocalDate.parse(raw[2]);
                        String title = raw[3];
                        String genre = raw[4];
                        String author = raw[5];
                        int days = Integer.parseInt(raw[6]);

                        if (loanId.isBlank() || memberId.isBlank() || title.isBlank() || genre.isBlank() || author.isBlank() || days <= 0) {
                            return new Parsed(null, id);
                        }

                        return new Parsed(new BookLoan(loanId, memberId, date, title, genre, author, days), null);

                    } catch (Exception e) {
                        return new Parsed(null, id);
                    }
                })
                .collect(Collectors.partitioningBy(p -> p.loan() != null));

        List<BookLoan> valid = partitioned.get(true).stream()
                .map(Parsed::loan)
                .toList();

        List<BookLoan> malformed = partitioned.get(false).stream()
                .map(p -> new BookLoan(p.malformedId(), null, null, null, null, null, 0))
                .toList();

        return Map.of("valid", valid, "malformed", malformed);
    }


    public static List<BookLoan> parseJsonLines(List<String> lines)
    {
        ObjectMapper mapper = new ObjectMapper();
        List<BookLoan> result = new ArrayList<>();

        for (String line : lines)
        {
            try {
                result.add(mapper.readValue(line, BookLoan.class));
            } catch (Exception ignored) {
            }
        }
        return result;
    }




    private static String extractId(String line)
    {
        if (line == null) return "";
        int idx = line.indexOf(',');
        return idx > 0 ? line.substring(0, idx).trim() : line.trim();
    }


    public static Map<String, Long> loansByGenre(List<BookLoan> loans)
    {
        return loans.stream()
                .collect(Collectors.groupingBy(
                        BookLoan::genre,
                        TreeMap::new,
                        Collectors.counting()
                ));
    }

    public static List<String> topAuthorsByLoans(List<BookLoan> loans, int n)
    {
        return loans.stream()
                .collect(Collectors.groupingBy(BookLoan::author, Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(n)
                .map(Map.Entry::getKey)
                .toList();
    }

    public static List<String> membersWithGenreDiversity(List<BookLoan> loans, int k)
    {
        return loans.stream()
                .collect(Collectors.groupingBy(
                        BookLoan::memberId,
                        Collectors.mapping(BookLoan::genre, Collectors.toSet())
                ))
                .entrySet().stream()
                .filter(e -> e.getValue().size() >= k)
                .map(Map.Entry::getKey)
                .sorted()
                .toList();
    }

    public static Optional<BookLoan> findFirstBookContaining(List<BookLoan> loans, String text)
    {
        return loans.stream()
                .filter(l -> l.bookTitle().toLowerCase().contains(text.toLowerCase()))
                .findFirst();
    }

    public static boolean isBookPresent(List<BookLoan> loans, String title)
    {
        return loans.stream()
                .anyMatch(l -> l.bookTitle().trim().equalsIgnoreCase(title.trim().toLowerCase()));
    }
}
