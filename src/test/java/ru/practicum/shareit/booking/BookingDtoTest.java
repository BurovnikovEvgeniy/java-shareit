package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingDtoTest {

    @Autowired
    private JacksonTester<BookingDto> json;

    private static final String DATE_START = "2024-05-11T11:30:59";
    private static final String DATE_END = "2024-05-11T12:35:59";

    private BookingDto bookingDto = null;

    @BeforeEach
    public void init() {
        bookingDto = BookingDto.builder()
                .itemId(1L)
                .start(LocalDateTime.parse(DATE_START))
                .end(LocalDateTime.parse(DATE_END))
                .build();
    }

    @Test
    public void startSerializes() throws IOException {
        assertThat(json.write(bookingDto)).extractingJsonPathStringValue("$.start")
                .isEqualTo(DATE_START);
    }

    @Test
    public void endSerializes() throws IOException {
        assertThat(json.write(bookingDto)).extractingJsonPathStringValue("$.end")
                .isEqualTo(DATE_END);
    }
}
