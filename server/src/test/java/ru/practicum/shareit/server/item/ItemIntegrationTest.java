package ru.practicum.shareit.server.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.ApplicationContext;
import org.springframework.http.*;
import ru.practicum.shareit.server.booking.Booking;
import ru.practicum.shareit.server.booking.dto.BookingDto;
import ru.practicum.shareit.server.booking.dto.BookingShortDto;
import ru.practicum.shareit.server.item.dto.CommentDto;
import ru.practicum.shareit.server.item.dto.ItemDto;
import ru.practicum.shareit.server.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.server.user.dto.UserDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ItemIntegrationTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private ApplicationContext applicationContext;


    @Test
    void create_shouldReturn200AndCreatedItemDto() {
        // Создаем владельца
        UserDto owner = new UserDto(null, "Piotr Ivanov", "piotr85@gmail.com");
        ResponseEntity<UserDto> responseUser = testRestTemplate.postForEntity("/users", owner, UserDto.class);
        UserDto savedOwner = responseUser.getBody();
        assertNotNull(savedOwner);
        long ownerId = savedOwner.getId();

        // Создаем item
        ItemDto itemDto = new ItemDto(
                null,
                "Drill",
                "Green drill",
                true,
                null,
                null);

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Sharer-User-Id", String.valueOf(ownerId));
        headers.setContentType(MediaType.APPLICATION_JSON);


        HttpEntity<ItemDto> requestEntity = new HttpEntity<>(itemDto, headers);

        ResponseEntity<ItemDto> response = testRestTemplate.exchange(
                "/items", HttpMethod.POST, requestEntity, ItemDto.class);

        //checks
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        ItemDto itemDtoActual = response.getBody();
        assertNotNull(itemDtoActual);
        assertNotNull(itemDtoActual.getId());

        assertThat(itemDtoActual.getName()).isEqualTo(itemDto.getName());
        assertThat(itemDtoActual.getDescription()).isEqualTo(itemDto.getDescription());
        assertThat(itemDtoActual.getAvailable()).isEqualTo(itemDto.getAvailable());

    }

    @Test
    void getAllItemsByOwnerWithBookings_shouldReturnItemsWithBookings() {
        // Создаем владельца
        UserDto ownerDto = new UserDto(null, "Ivan Ivanov", "ivan14@gmail.com");
        ResponseEntity<UserDto> responseOwner = testRestTemplate.postForEntity("/users", ownerDto, UserDto.class);
        assertNotNull(responseOwner.getBody());
        Long ownerId = responseOwner.getBody().getId();

        // Создаем другого пользователя для бронирования
        UserDto bookerDto = new UserDto(null, "Alex Petrov", "alex66@gmail.com");
        ResponseEntity<UserDto> responseBooker = testRestTemplate.postForEntity("/users", bookerDto, UserDto.class);
        assertNotNull(responseBooker.getBody());
        Long bookerId = responseBooker.getBody().getId();

        // Создаем другого пользователя для будущего бронирования
        UserDto bookerDto2 = new UserDto(null, "Ira Petrova", "ira18@gmail.com");
        ResponseEntity<UserDto> responseBooker2 = testRestTemplate.postForEntity("/users", bookerDto2, UserDto.class);
        assertNotNull(responseBooker2.getBody());
        Long bookerId2 = responseBooker2.getBody().getId();

        // Создаем два предмета для владельца
        ItemDto itemDto1 = new ItemDto(
                null,
                "Drill",
                "Red drill",
                true,
                null,
                null);
        itemDto1 = createItem(ownerId, itemDto1);
        System.out.println(itemDto1.getComments());
        Long itemId1 = itemDto1.getId();
        System.out.println(itemId1);

        ItemDto itemDto2 = new ItemDto(
                null,
                "Hammer",
                "Heavy hammer",
                true,
                null,
                null);
        itemDto2 = createItem(ownerId, itemDto2);
        Long itemId2 = itemDto2.getId();
        System.out.println(itemId2);


        // Создаем бронирование для первого предмета
        Booking bookingPast1 = createBooking(bookerId, itemId1, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1));
        BookingShortDto bookingShortDtoLast1 = new BookingShortDto(bookingPast1.getStart(), bookingPast1.getEnd());

        Booking bookingFuture1 = createBooking(bookerId2, itemId1, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
        BookingShortDto bookingShortDtoNext1 = new BookingShortDto(bookingFuture1.getStart(), bookingFuture1.getEnd());

        // Создаем бронирование для второго предмета
        Booking bookingPast2 = createBooking(bookerId, itemId2, LocalDateTime.now().minusDays(3), LocalDateTime.now().minusDays(2));
        BookingShortDto bookingShortDtoLast2 = new BookingShortDto(bookingPast2.getStart(), bookingPast2.getEnd());

        Booking bookingFuture2 = createBooking(bookerId2, itemId2, LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(3));
        BookingShortDto bookingShortDtoNext2 = new BookingShortDto(bookingFuture2.getStart(), bookingFuture2.getEnd());


        // Создаем комментарии для предметов
        CommentDto comment = createComment(bookerId, bookerDto.getName(), itemId1, "Great drill!");

        // Получаем все предметы владельца с бронированиями
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Sharer-User-Id", String.valueOf(ownerId));
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<ItemWithBookingsDto[]> response = testRestTemplate.exchange(
                "/items", HttpMethod.GET, requestEntity, ItemWithBookingsDto[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertNotNull(response.getBody());
        assertThat(response.getBody().length).isEqualTo(2);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SS");

        // Проверяем для первого предмета
        ItemWithBookingsDto itemWithBookings1 = response.getBody()[0];
        assertThat(itemWithBookings1.getId()).isEqualTo(itemId1);
        assertThat(itemWithBookings1.getName()).isEqualTo(itemDto1.getName());
        assertThat(itemWithBookings1.getDescription()).isEqualTo(itemDto1.getDescription());
        assertThat(itemWithBookings1.getAvailable()).isEqualTo(itemDto1.getAvailable());
        assertThat(itemWithBookings1.getComments()).hasSize(1);

        assertThat(itemWithBookings1.getLastBooking().getStart().format(formatter))
                .isEqualTo(bookingShortDtoLast1.getStart().format(formatter));
        assertThat(itemWithBookings1.getLastBooking().getEnd().format(formatter))
                .isEqualTo(bookingShortDtoLast1.getEnd().format(formatter));

        assertThat(itemWithBookings1.getNextBooking().getStart().format(formatter))
                .isEqualTo(bookingShortDtoNext1.getStart().format(formatter));
        assertThat(itemWithBookings1.getNextBooking().getEnd().format(formatter))
                .isEqualTo(bookingShortDtoNext1.getEnd().format(formatter));


        // Проверяем для второго предмета
        ItemWithBookingsDto itemWithBookings2 = response.getBody()[1];
        assertThat(itemWithBookings2.getId()).isEqualTo(itemId2);
        assertThat(itemWithBookings2.getName()).isEqualTo(itemDto2.getName());
        assertThat(itemWithBookings2.getDescription()).isEqualTo(itemDto2.getDescription());
        assertThat(itemWithBookings2.getAvailable()).isEqualTo(itemDto2.getAvailable());
        assertThat(itemWithBookings2.getComments()).hasSize(0);

        assertThat(itemWithBookings2.getLastBooking().getStart().format(formatter))
                .isEqualTo(bookingShortDtoLast2.getStart().format(formatter));
        assertThat(itemWithBookings2.getLastBooking().getEnd().format(formatter))
                .isEqualTo(bookingShortDtoLast2.getEnd().format(formatter));

        assertThat(itemWithBookings2.getNextBooking().getStart().format(formatter))
                .isEqualTo(bookingShortDtoNext2.getStart().format(formatter));
        assertThat(itemWithBookings2.getNextBooking().getEnd().format(formatter))
                .isEqualTo(bookingShortDtoNext2.getEnd().format(formatter));

    }

    private ItemDto createItem(Long ownerId, ItemDto itemDto) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Sharer-User-Id", String.valueOf(ownerId));
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ItemDto> requestEntity = new HttpEntity<>(itemDto, headers);

        ResponseEntity<ItemDto> response = testRestTemplate.exchange(
                "/items", HttpMethod.POST, requestEntity, ItemDto.class);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isNotNull();
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

    private CommentDto createComment(Long authorId, String authorName, Long itemId, String text) {
        CommentDto commentDto = new CommentDto(null, text, itemId, authorId, authorName, LocalDateTime.now());

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Sharer-User-Id", String.valueOf(authorId));
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<CommentDto> requestEntity = new HttpEntity<>(commentDto, headers);

        ResponseEntity<CommentDto> response = testRestTemplate.exchange(
                "/items/" + itemId + "/comment", HttpMethod.POST, requestEntity, CommentDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isNotNull();
        return response.getBody();
    }


    @Test
    void createComment_shouldReturn200AndCrateComment() {

        // Создаем владельца
        UserDto ownerDto = new UserDto(null, "Ivan Ivanov", "ivan98@gmail.com");
        ResponseEntity<UserDto> responseOwner = testRestTemplate.postForEntity("/users", ownerDto, UserDto.class);
        assertNotNull(responseOwner.getBody());
        Long ownerId = responseOwner.getBody().getId();

        // Создаем другого пользователя для бронирования
        UserDto bookerDto = new UserDto(null, "Alex Petrov", "alex33@gmail.com");
        ResponseEntity<UserDto> responseBooker = testRestTemplate.postForEntity("/users", bookerDto, UserDto.class);
        assertNotNull(responseBooker.getBody());
        Long bookerId = responseBooker.getBody().getId();

        // Создаем предмет для владельца
        ItemDto itemDto1 = new ItemDto(
                null,
                "Drill",
                "Red drill",
                true,
                null,
                null);
        itemDto1 = createItem(ownerId, itemDto1);
        System.out.println(itemDto1.getComments());
        Long itemId1 = itemDto1.getId();
        System.out.println(itemId1);

        createBooking(bookerId, itemId1, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1));

        CommentDto commentDto = new CommentDto(null, "Super!", itemId1, bookerId, bookerDto.getName(), LocalDateTime.now());

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Sharer-User-Id", String.valueOf(bookerId));
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<CommentDto> requestEntity = new HttpEntity<>(commentDto, headers);

        ResponseEntity<CommentDto> response = testRestTemplate.exchange(
                "/items/" + itemId1 + "/comment", HttpMethod.POST, requestEntity, CommentDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isNotNull();
    }

}