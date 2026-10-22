package ru.job4j.grabber.utils;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

class HabrCareerDateTimeParserTest {

    private final DateTimeParser parser = new HabrCareerDateTimeParser();

    @Test
    void whenMoscowOffsetThenSameLocalTime() {
        LocalDateTime result = parser.parse("2026-10-22T14:27:16+03:00");
        assertThat(result).isEqualTo(LocalDateTime.of(2026, 10, 22, 14, 27, 16));
    }

    @Test
    void whenUtcOffsetThenSameLocalTime() {
        LocalDateTime result = parser.parse("2026-10-22T11:27:16+00:00");
        assertThat(result).isEqualTo(LocalDateTime.of(2026, 10, 22, 11, 27, 16));
    }

    @Test
    void whenNegativeOffsetThenSameLocalTime() {
        LocalDateTime result = parser.parse("2026-12-22T23:59:59-05:00");
        assertThat(result).isEqualTo(LocalDateTime.of(2026, 12, 22, 23, 59, 59));
    }
}