package ru.practicum.shareit.server.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.ApplicationContext;
import org.springframework.http.*;
import org.springframework.test.context.TestPropertySource;
import ru.practicum.shareit.server.booking.dto.BookingDto;
import ru.practicum.shareit.server.item.dto.ItemDto;
import ru.practicum.shareit.server.user.dto.UserDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestPropertySource(locations = "classpath:application-test.properties")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BookingIntegrationTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void getBookingsByBookerIdWithFilter_shouldReturnFilteredBookings() {
        // Создаем пользователя
        UserDto bookerDto = new UserDto(null, "Alex Petrov", "alex@gmail.com");
        ResponseEntity<UserDto> responseBooker = testRestTemplate.postForEntity("/users", bookerDto, UserDto.class);
        assertNotNull(responseBooker.getBody());
        Long bookerId = responseBooker.getBody().getId();

        // Создаем вещи для бронирования
        ItemDto itemDto1 = new ItemDto(null, "Drill", "Red drill", true, null, null);
        ItemDto itemDto2 = new ItemDto(null, "Hammer", "Heavy hammer", true, null, null);
        Long itemId1 = createItem(bookerId, itemDto1).getId();
        Long itemId2 = createItem(bookerId, itemDto2).getId();

        // Создаем различные бронирования для фильтрации
        Booking bookingPast = createBooking(bookerId, itemId1, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1));
        Booking bookingFuture = createBooking(bookerId, itemId1, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
        Booking bookingCurrent = createBooking(bookerId, itemId2, LocalDateTime.now().minusHours(1), LocalDateTime.now().plusHours(1));
        Booking bookingWaiting = createBooking(bookerId, itemId2, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
        Booking bookingRejected = createBooking(bookerId, itemId1, LocalDateTime.now().minusDays(5), LocalDateTime.now().minusDays(4));

        approveOrRejectBooking(bookerId, bookingPast.getId(), true);
        approveOrRejectBooking(bookerId, bookingFuture.getId(), true);
        approveOrRejectBooking(bookerId, bookingCurrent.getId(), true);
        approveOrRejectBooking(bookerId, bookingRejected.getId(), false);

        // Получаем бронирования с фильтрацией по статусу "ALL"
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Sharer-User-Id", String.valueOf(bookerId));
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<BookingDto[]> responseAll = testRestTemplate.exchange(
                "/bookings", HttpMethod.GET, requestEntity, BookingDto[].class);

        assertThat(responseAll.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertNotNull(responseAll.getBody());
        assertThat(responseAll.getBody().length).isEqualTo(5);

        // Получаем бронирования с фильтрацией по статусу "PAST"
        ResponseEntity<BookingDto[]> responsePast = testRestTemplate.exchange(
                "/bookings?state=PAST", HttpMethod.GET, requestEntity, BookingDto[].class);
        assertThat(responsePast.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertNotNull(responsePast.getBody());
        assertThat(responsePast.getBody().length).isEqualTo(2);

        // Получаем бронирования с фильтрацией по статусу "FUTURE"
        ResponseEntity<BookingDto[]> responseFuture = testRestTemplate.exchange(
                "/bookings?state=FUTURE", HttpMethod.GET, requestEntity, BookingDto[].class);
        assertThat(responseFuture.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertNotNull(responseFuture.getBody());
        assertThat(responseFuture.getBody().length).isEqualTo(2);

        // Получаем бронирования с фильтрацией по статусу "CURRENT"
        ResponseEntity<BookingDto[]> responseCurrent = testRestTemplate.exchange(
                "/bookings?state=CURRENT", HttpMethod.GET, requestEntity, BookingDto[].class);
        assertThat(responseCurrent.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertNotNull(responseCurrent.getBody());
        assertThat(responseCurrent.getBody().length).isEqualTo(1);

        // Получаем бронирования с фильтрацией по статусу "WAITING"
        ResponseEntity<BookingDto[]> responseWaiting = testRestTemplate.exchange(
                "/bookings?state=WAITING", HttpMethod.GET, requestEntity, BookingDto[].class);
        assertThat(responseWaiting.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertNotNull(responseWaiting.getBody());
        assertThat(responseWaiting.getBody().length).isEqualTo(1);

        // Получаем бронирования с фильтрацией по статусу "REJECTED"
        ResponseEntity<BookingDto[]> responseRejected = testRestTemplate.exchange(
                "/bookings?state=REJECTED", HttpMethod.GET, requestEntity, BookingDto[].class);
        assertThat(responseRejected.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertNotNull(responseRejected.getBody());
        assertThat(responseRejected.getBody().length).isEqualTo(1);
    }

    private ItemDto createItem(Long ownerId, ItemDto itemDto) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Sharer-User-Id", String.valueOf(ownerId));
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ItemDto> requestEntity = new HttpEntity<>(itemDto, headers);

        ResponseEntity<ItemDto> response = testRestTemplate.exchange(
                "/items", HttpMethod.POST, requestEntity, ItemDto.class);
        assertThat(response.getBody()).isNotNull();
        return response.getBody();
    }

    private Booking createBooking(Long bookerId, Long itemId, LocalDateTime start, LocalDateTime end) {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setStart(start);
        bookingDto.setEnd(end);
        bookingDto.setItemId(itemId);

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Sharer-User-Id", String.valueOf(bookerId));
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<BookingDto> requestEntity = new HttpEntity<>(bookingDto, headers);

        ResponseEntity<Booking> response = testRestTemplate.exchange(
                "/bookings", HttpMethod.POST, requestEntity, Booking.class);

        return response.getBody();
    }

    private void approveOrRejectBooking(Long ownerId, Long bookingId, boolean approved) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Sharer-User-Id", String.valueOf(ownerId));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-HTTP-Method-Override", "PATCH");

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        String url = "/bookings/" + bookingId + "?approved=" + approved;

        ResponseEntity<BookingDto> response = testRestTemplate.exchange(
                url, HttpMethod.PATCH, requestEntity, BookingDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

}