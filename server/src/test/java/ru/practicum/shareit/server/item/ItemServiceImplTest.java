package ru.practicum.shareit.server.item;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.server.booking.Booking;
import ru.practicum.shareit.server.booking.BookingRepository;
import ru.practicum.shareit.server.booking.dto.BookingShortDto;
import ru.practicum.shareit.server.booking.enums.Status;
import ru.practicum.shareit.server.exception.ForbiddenExcepton;
import ru.practicum.shareit.server.exception.NotFoundException;
import ru.practicum.shareit.server.exception.ValidationException;
import ru.practicum.shareit.server.item.dto.CommentDto;
import ru.practicum.shareit.server.item.dto.ItemDto;
import ru.practicum.shareit.server.item.dto.ItemUpdateDto;
import ru.practicum.shareit.server.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.server.request.ItemRequestRepository;
import ru.practicum.shareit.server.user.User;
import ru.practicum.shareit.server.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    @Captor
    private ArgumentCaptor<Item> itemArgumentCaptor;
    @Captor
    private ArgumentCaptor<Comment> commentArgumentCaptor;

    @Test
    @DisplayName("Должен выбросить исключение и не сохранять item в БД, если владелец не найден")
    void create_shouldThrowExceptionAndNotSaveItemIntoDB_ifOwnerNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        ItemDto itemDto = new ItemDto(
                null,
                "Шуруповерт",
                "Красный шупуповерт",
                true,
                2L,
                null);
        assertThrows(NotFoundException.class, () -> itemService.create(1L, itemDto));
        verify(itemRepository, never()).save(any());
    }

    @Test
    @DisplayName("Должен выбросить исключение и не сохранять item в БД, если владелец не найден")
    void create_shouldThrowExceptionAndNotSaveItemIntoDB_ifRequestNotFound() {
        User owner = new User(1L, "Ivan Ivanov", "ivan@gmail.com", new HashSet<>());
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(itemRequestRepository.existsById(anyLong())).thenReturn(false);

        ItemDto itemDto = new ItemDto(
                null,
                "Шуруповерт",
                "Красный шупуповерт",
                true,
                2L,
                null);

        assertThrows(NotFoundException.class, () -> itemService.create(1L, itemDto));
        verify(itemRepository, never()).save(any());
    }

    @Test
    @DisplayName("Должен сохранять корректный Item")
    void create_shouldSaveCorrectItem() {
        User owner = new User(1L, "Ivan Ivanov", "ivan@gmail.com", new HashSet<>());
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(itemRequestRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.save(any(Item.class))).thenAnswer(invocationOnMock -> {
            Item savedItem = invocationOnMock.getArgument(0);
            savedItem.setId(1L);
            return savedItem;
        });

        ItemDto itemDto = new ItemDto(
                null,
                "Шуруповерт",
                "Красный шупуповерт",
                true,
                2L,
                null);

        Item itemExpected = new Item(
                1L,
                owner,
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                itemDto.getRequestId());

        itemService.create(owner.getId(), itemDto);

        verify(itemRepository, times(1)).save(itemArgumentCaptor.capture());
        Item itemActual = itemArgumentCaptor.getValue();

        assertThat(itemExpected.getId(), equalTo(itemActual.getId()));
        assertThat(itemExpected.getOwner(), equalTo(itemActual.getOwner()));
        assertThat(itemExpected.getName(), equalTo(itemActual.getName()));
        assertThat(itemExpected.getDescription(), equalTo(itemActual.getDescription()));
        assertThat(itemExpected.getAvailable(), equalTo(itemActual.getAvailable()));
        assertThat(itemExpected.getRequestId(), equalTo(itemActual.getRequestId()));

    }

    @Test
    @DisplayName("Должен вернуть корректный ItemDto")
    void create_shouldReturnCorrectItemDto() {
        User owner = new User(1L, "Ivan Ivanov", "ivan@gmail.com", new HashSet<>());
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(itemRequestRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.save(any(Item.class))).thenAnswer(invocationOnMock -> {
            Item savedItem = invocationOnMock.getArgument(0);
            savedItem.setId(1L);
            return savedItem;
        });

        ItemDto itemDto = new ItemDto(
                null,
                "Шуруповерт",
                "Красный шупуповерт",
                true,
                2L,
                null);

        ItemDto itemDtoExpected = new ItemDto(
                1L,
                "Шуруповерт",
                "Красный шупуповерт",
                true,
                2L,
                null);

        ItemDto itemDtoActual = itemService.create(owner.getId(), itemDto);

        assertThat(itemDtoExpected.getId(), equalTo(itemDtoActual.getId()));
        assertThat(itemDtoExpected.getName(), equalTo(itemDtoActual.getName()));
        assertThat(itemDtoExpected.getDescription(), equalTo(itemDtoActual.getDescription()));
        assertThat(itemDtoExpected.getAvailable(), equalTo(itemDtoActual.getAvailable()));
        assertThat(itemDtoExpected.getRequestId(), equalTo(itemDtoActual.getRequestId()));

    }

    @Test
    @DisplayName("Должен выкинуть исключение, если вещь не найдена")
    void update_shouldThrowException_ifItemNotFound() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());
        ItemUpdateDto itemUpdateDto = new ItemUpdateDto(
                "Шуруповерт",
                "Красный шупуповерт",
                true
        );

        assertThrows(NotFoundException.class, () -> itemService.update(1L, itemUpdateDto, 1L));
        verify(itemRepository, never()).save(any());

    }

    @Test
    @DisplayName("Должен выкинуть исключение, если владелец не найден")
    void update_shouldThrowException_ifOwnerNotFound() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(new Item()));
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        ItemUpdateDto itemUpdateDto = new ItemUpdateDto(
                "Шуруповерт",
                "Красный шупуповерт",
                true
        );

        assertThrows(NotFoundException.class, () -> itemService.update(1L, itemUpdateDto, 1L));
        verify(itemRepository, never()).save(any());
    }

    @Test
    @DisplayName("Должен выкинуть исключение, если переданный юзер не является владельцем вещи")
    void update_shouldThrowException_ifUserIsNotOwner() {
        User providedOwner = new User(1L, "Ivan Ivanov", "ivan@gmail.com", new HashSet<>());
        User actualOwner = new User(15L, "Piotr Petrov", "piotr@gmail.com", new HashSet<>());

        Item item = new Item(1L, actualOwner, "Отвертка",
                "Крестовая отвертка", true, null);

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(providedOwner));

        ItemUpdateDto itemUpdateDto = new ItemUpdateDto(
                "Шуруповерт",
                "Красный шупуповерт",
                true
        );

        assertThrows(ForbiddenExcepton.class, () -> itemService.update(
                item.getId(), itemUpdateDto, providedOwner.getId()));
        verify(itemRepository, never()).save(any());
    }

    @Test
    @DisplayName("Должен поменять в Item только переданные поля в ItemUpdateDto")
    void update_shouldChangeAllowedFields() {
        User actualOwner = new User(15L, "Piotr Petrov", "piotr@gmail.com", new HashSet<>());
        Item item = new Item(1L, actualOwner, "Отвертка",
                "Крестовая отвертка", true, null);

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(actualOwner));
        when(itemRepository.save(any(Item.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        ItemUpdateDto itemUpdateDto = new ItemUpdateDto(
                "Шуруповерт",
                "Красный шупуповерт",
                false
        );

        Item itemExpected = new Item(1L, actualOwner, "Шуруповерт",
                "Красный шупуповерт", false, null);


        //when
        itemService.update(item.getId(), itemUpdateDto, actualOwner.getId());

        verify(itemRepository, times(1)).save(itemArgumentCaptor.capture());
        Item itemActual = itemArgumentCaptor.getValue();

        assertThat(itemExpected.getId(), equalTo(itemActual.getId()));
        assertThat(itemExpected.getOwner(), equalTo(itemActual.getOwner()));
        assertThat(itemExpected.getName(), equalTo(itemActual.getName()));
        assertThat(itemExpected.getDescription(), equalTo(itemActual.getDescription()));
        assertThat(itemExpected.getAvailable(), equalTo(itemActual.getAvailable()));
        assertThat(itemExpected.getRequestId(), equalTo(itemActual.getRequestId()));
    }

    @Test
    @DisplayName("Должен выкинуть исключение, если вещь не найдена")
    void getById_shouldThrowException_ifItemNotFound() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> itemService.getById(1L, 1L));
    }

    @Test
    @DisplayName("Должен выкинуть исключение, если юзер не найден")
    void getById_shouldThrowException_ifRequestorNotFound() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(new Item()));
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.getById(1L, 1L));
    }


    @Test
    @DisplayName("Должен вернуть ItemWithBookingsDto с незаполненными полями о букинге," +
            "если реквестор - не владелец вещи")
    void getById_shouldReturnItemWithBookingsDtoWithNullBookingFields_ifRequesterIsNotTheOwnerOfItem() {
        User requestor = new User(1L, "Ivan Ivanov", "ivan@gmail.com", new HashSet<>());
        User owner = new User(15L, "Piotr Petrov", "piotr@gmail.com", new HashSet<>());
        User commentAuthor1 = new User(17L, "Vasia Vasilyev", "vasia@gmail.com", new HashSet<>());
        User commentAuthor2 = new User(25L, "Anna Alekseeva", "anna@gmail.com", new HashSet<>());
        Item item = new Item(1L, owner, "Отвертка",
                "Крестовая отвертка", true, null);

        Comment comment1 = new Comment(1L, "Отличная отвертка", item, commentAuthor1,
                LocalDateTime.of(2025, 1, 2, 15, 30, 10, 10));
        Comment comment2 = new Comment(2L, "Ужасная отвертка", item, commentAuthor2,
                LocalDateTime.of(2025, 3, 8, 15, 30, 10, 10));
        List<Comment> comments = List.of(comment1, comment2);

        CommentDto commentDto1 = new CommentDto(comment1.getId(), comment1.getText(), comment1.getItem().getId(),
                comment1.getAuthor().getId(), comment1.getAuthor().getName(), comment1.getCreated());
        CommentDto commentDto2 = new CommentDto(comment2.getId(), comment2.getText(), comment2.getItem().getId(),
                comment2.getAuthor().getId(), comment2.getAuthor().getName(), comment2.getCreated());
        List<CommentDto> commentsDtoExpected = List.of(commentDto1, commentDto2);


        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(requestor));
        when(commentRepository.findByItemId(anyLong())).thenReturn(comments);

        ItemWithBookingsDto itemWithBookingsDtoExpected = new ItemWithBookingsDto(
                item.getId(), item.getName(), item.getDescription(), item.getAvailable(), item.getRequestId(),
                null, null, commentsDtoExpected
        );

        ItemWithBookingsDto itemWithBookingsDtoActual = itemService.getById(item.getId(), requestor.getId());

        assertThat(itemWithBookingsDtoExpected.getId(), equalTo(itemWithBookingsDtoActual.getId()));
        assertThat(itemWithBookingsDtoExpected.getName(), equalTo(itemWithBookingsDtoActual.getName()));
        assertThat(itemWithBookingsDtoExpected.getDescription(), equalTo(itemWithBookingsDtoActual.getDescription()));
        assertThat(itemWithBookingsDtoExpected.getAvailable(), equalTo(itemWithBookingsDtoActual.getAvailable()));
        assertThat(itemWithBookingsDtoExpected.getLastBooking(), equalTo(itemWithBookingsDtoActual.getLastBooking()));
        assertThat(itemWithBookingsDtoExpected.getNextBooking(), equalTo(itemWithBookingsDtoActual.getNextBooking()));

        List<CommentDto> commentsDtoActual = itemWithBookingsDtoActual.getComments();

        for (CommentDto commentDtoExpected : commentsDtoExpected) {
            CommentDto commentDtoActual = commentsDtoActual.get(commentsDtoExpected.indexOf(commentDtoExpected));
            assertThat(commentDtoExpected.getId(), equalTo(commentDtoActual.getId()));
            assertThat(commentDtoExpected.getText(), equalTo(commentDtoActual.getText()));
            assertThat(commentDtoExpected.getItemId(), equalTo(commentDtoActual.getItemId()));
            assertThat(commentDtoExpected.getAuthorId(), equalTo(commentDtoActual.getAuthorId()));
            assertThat(commentDtoExpected.getAuthorName(), equalTo(commentDtoActual.getAuthorName()));
            assertThat(commentDtoExpected.getCreated(), equalTo(commentDtoActual.getCreated()));
        }
    }


    @Test
    @DisplayName("Должен вернуть ItemWithBookingsDto с заполненными полями о букинге," +
            "если реквестор - это владелец вещи")
    void getById_shouldReturnItemWithBookingsDtoWithPopulatedBookingFields_ifRequesterIsTheOwnerOfItem() {
        //given
        User requestor = new User(1L, "Ivan Ivanov", "ivan@gmail.com", new HashSet<>());
        User commentAuthor1 = new User(17L, "Vasia Vasilyev", "vasia@gmail.com", new HashSet<>());
        User commentAuthor2 = new User(25L, "Anna Alekseeva", "anna@gmail.com", new HashSet<>());
        User booker3 = new User(27L, "Ira Alekseeva", "ira@gmail.com", new HashSet<>());
        User booker4 = new User(29L, "Sofia Alekseeva", "sofia@gmail.com", new HashSet<>());
        Item item = new Item(1L, requestor, "Отвертка",
                "Крестовая отвертка", true, null);

        Comment comment1 = new Comment(1L, "Отличная отвертка", item, commentAuthor1,
                LocalDateTime.of(2025, 1, 2, 15, 30, 10, 10));
        Comment comment2 = new Comment(2L, "Ужасная отвертка", item, commentAuthor2,
                LocalDateTime.of(2025, 3, 8, 15, 30, 10, 10));
        List<Comment> comments = List.of(comment1, comment2);

        CommentDto commentDto1 = new CommentDto(comment1.getId(), comment1.getText(), comment1.getItem().getId(),
                comment1.getAuthor().getId(), comment1.getAuthor().getName(), comment1.getCreated());
        CommentDto commentDto2 = new CommentDto(comment2.getId(), comment2.getText(), comment2.getItem().getId(),
                comment2.getAuthor().getId(), comment2.getAuthor().getName(), comment2.getCreated());
        List<CommentDto> commentsDtoExpected = List.of(commentDto1, commentDto2);

        Booking booking1 = new Booking(1L,
                LocalDateTime.of(2024, 8, 1, 10, 20, 0, 0),
                LocalDateTime.of(2024, 8, 10, 10, 20, 0, 0),
                item, commentAuthor1, Status.APPROVED);
        Booking booking2 = new Booking(2L,
                LocalDateTime.of(2024, 9, 1, 10, 20, 0, 0),
                LocalDateTime.of(2024, 9, 10, 10, 20, 0, 0),
                item, commentAuthor2, Status.APPROVED);
        Booking booking3 = new Booking(3L,
                LocalDateTime.of(2025, 5, 1, 10, 20, 0, 0),
                LocalDateTime.of(2025, 5, 10, 10, 20, 0, 0),
                item, booker3, Status.APPROVED);
        Booking booking4 = new Booking(4L,
                LocalDateTime.of(2025, 6, 1, 10, 20, 0, 0),
                LocalDateTime.of(2025, 6, 10, 10, 20, 0, 0),
                item, booker4, Status.APPROVED);
        List<Booking> itemBookings = List.of(booking1, booking2, booking3, booking4);

        BookingShortDto lastBookingExpected = new BookingShortDto(booking2.getStart(), booking2.getEnd());
        BookingShortDto nextBookingExpected = new BookingShortDto(booking3.getStart(), booking3.getEnd());

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(requestor));
        when(commentRepository.findByItemId(anyLong())).thenReturn(comments);
        when(bookingRepository.findByItemId(anyLong())).thenReturn(itemBookings);


        ItemWithBookingsDto itemWithBookingsDtoExpected = new ItemWithBookingsDto(
                item.getId(), item.getName(), item.getDescription(), item.getAvailable(), item.getRequestId(),
                lastBookingExpected, nextBookingExpected, commentsDtoExpected
        );

        //when
        ItemWithBookingsDto itemWithBookingsDtoActual = itemService.getById(item.getId(), requestor.getId());

        //then
        assertThat(itemWithBookingsDtoExpected.getId(), equalTo(itemWithBookingsDtoActual.getId()));
        assertThat(itemWithBookingsDtoExpected.getName(), equalTo(itemWithBookingsDtoActual.getName()));
        assertThat(itemWithBookingsDtoExpected.getDescription(), equalTo(itemWithBookingsDtoActual.getDescription()));
        assertThat(itemWithBookingsDtoExpected.getAvailable(), equalTo(itemWithBookingsDtoActual.getAvailable()));
        assertThat(itemWithBookingsDtoExpected.getLastBooking(), equalTo(itemWithBookingsDtoActual.getLastBooking()));
        assertThat(itemWithBookingsDtoExpected.getNextBooking(), equalTo(itemWithBookingsDtoActual.getNextBooking()));

        List<CommentDto> commentsDtoActual = itemWithBookingsDtoActual.getComments();

        for (CommentDto commentDtoExpected : commentsDtoExpected) {
            CommentDto commentDtoActual = commentsDtoActual.get(commentsDtoExpected.indexOf(commentDtoExpected));
            assertThat(commentDtoExpected.getId(), equalTo(commentDtoActual.getId()));
            assertThat(commentDtoExpected.getText(), equalTo(commentDtoActual.getText()));
            assertThat(commentDtoExpected.getItemId(), equalTo(commentDtoActual.getItemId()));
            assertThat(commentDtoExpected.getAuthorId(), equalTo(commentDtoActual.getAuthorId()));
            assertThat(commentDtoExpected.getAuthorName(), equalTo(commentDtoActual.getAuthorName()));
            assertThat(commentDtoExpected.getCreated(), equalTo(commentDtoActual.getCreated()));
        }

    }

    @Test
    @DisplayName("Должен выкинуть исключение, если юзер не найден")
    void getAllItemsByOwnerWithBookings_shouldThrowException_ifUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> itemService.getAllItemsByOwnerWithBookings(1L));
    }

    @Test
    @DisplayName("Должен вернуть пустой лист, если у юзера нет вещей")
    void getAllItemsByOwnerWithBookings_shouldReturnEmptyList_ifUserDasNotHaveItems() {

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        when(itemRepository.findByOwnerId(anyLong())).thenReturn(Collections.emptyList());

        List<ItemWithBookingsDto> itemWithBookingsDtoListActual = itemService.getAllItemsByOwnerWithBookings(1L);

        assertTrue(itemWithBookingsDtoListActual.isEmpty());
    }

    @Test
    @DisplayName("Должен вернуть лист с ItemWithBookingsDto")
    void getAllItemsByOwnerWithBookings_shouldReturnItemWithBookingsDtoList() {
        //given
        User owner = new User(1L, "Ivan Ivanov", "ivan@gmail.com", new HashSet<>());
        User commentAuthor1 = new User(17L, "Vasia Vasilyev", "vasia@gmail.com", new HashSet<>());
        User commentAuthor2 = new User(25L, "Anna Alekseeva", "anna@gmail.com", new HashSet<>());
        User booker3 = new User(27L, "Ira Alekseeva", "ira@gmail.com", new HashSet<>());
        User booker4 = new User(29L, "Sofia Alekseeva", "sofia@gmail.com", new HashSet<>());

        Item item1 = new Item(1L, owner, "Отвертка",
                "Крестовая отвертка", true, null);
        Item item2 = new Item(2L, owner, "Шуруповерт",
                "Красный шуруповерт", true, null);
        List<Item> itemList = List.of(item1, item2);

        Comment comment1 = new Comment(1L, "Отличная отвертка", item1, commentAuthor1,
                LocalDateTime.of(2025, 1, 2, 15, 30, 10, 10));
        Comment comment2 = new Comment(2L, "Ужасная отвертка", item1, commentAuthor2,
                LocalDateTime.of(2025, 3, 8, 15, 30, 10, 10));
        List<Comment> commentList1 = List.of(comment1, comment2);

        Comment comment3 = new Comment(3L, "Отличный шуруповерт", item2, commentAuthor1,
                LocalDateTime.of(2025, 1, 2, 15, 30, 10, 10));
        Comment comment4 = new Comment(4L, "Ужасный шуруповерт", item2, commentAuthor2,
                LocalDateTime.of(2025, 3, 8, 15, 30, 10, 10));
        List<Comment> commentList2 = List.of(comment3, comment4);

        CommentDto commentDto1 = new CommentDto(comment1.getId(), comment1.getText(), comment1.getItem().getId(),
                comment1.getAuthor().getId(), comment1.getAuthor().getName(), comment1.getCreated());
        CommentDto commentDto2 = new CommentDto(comment2.getId(), comment2.getText(), comment2.getItem().getId(),
                comment2.getAuthor().getId(), comment2.getAuthor().getName(), comment2.getCreated());
        List<CommentDto> commentsDtoExpected1 = List.of(commentDto1, commentDto2);

        CommentDto commentDto3 = new CommentDto(comment3.getId(), comment3.getText(), comment3.getItem().getId(),
                comment3.getAuthor().getId(), comment3.getAuthor().getName(), comment3.getCreated());
        CommentDto commentDto4 = new CommentDto(comment4.getId(), comment4.getText(), comment4.getItem().getId(),
                comment4.getAuthor().getId(), comment4.getAuthor().getName(), comment4.getCreated());
        List<CommentDto> commentsDtoExpected2 = List.of(commentDto3, commentDto4);

        Booking booking1 = new Booking(1L,
                LocalDateTime.of(2024, 8, 1, 10, 20, 0, 0),
                LocalDateTime.of(2024, 8, 10, 10, 20, 0, 0),
                item1, commentAuthor1, Status.APPROVED);
        Booking booking2 = new Booking(2L,
                LocalDateTime.of(2024, 9, 1, 10, 20, 0, 0),
                LocalDateTime.of(2024, 9, 10, 10, 20, 0, 0),
                item1, commentAuthor2, Status.APPROVED);
        Booking booking3 = new Booking(3L,
                LocalDateTime.of(2025, 5, 1, 10, 20, 0, 0),
                LocalDateTime.of(2025, 5, 10, 10, 20, 0, 0),
                item1, booker3, Status.APPROVED);
        Booking booking4 = new Booking(4L,
                LocalDateTime.of(2025, 6, 1, 10, 20, 0, 0),
                LocalDateTime.of(2025, 6, 10, 10, 20, 0, 0),
                item1, booker4, Status.APPROVED);

        Booking booking5 = new Booking(5L,
                LocalDateTime.of(2024, 7, 1, 10, 20, 0, 0),
                LocalDateTime.of(2024, 7, 10, 10, 20, 0, 0),
                item2, commentAuthor1, Status.APPROVED);
        Booking booking6 = new Booking(6L,
                LocalDateTime.of(2024, 8, 1, 10, 20, 0, 0),
                LocalDateTime.of(2024, 8, 10, 10, 20, 0, 0),
                item2, commentAuthor2, Status.APPROVED);
        Booking booking7 = new Booking(7L,
                LocalDateTime.of(2025, 6, 1, 10, 20, 0, 0),
                LocalDateTime.of(2025, 6, 10, 10, 20, 0, 0),
                item2, booker3, Status.APPROVED);
        Booking booking8 = new Booking(8L,
                LocalDateTime.of(2025, 7, 1, 10, 20, 0, 0),
                LocalDateTime.of(2025, 7, 10, 10, 20, 0, 0),
                item2, booker4, Status.APPROVED);

        List<Booking> itemsBookings = List.of(booking1, booking2, booking3, booking4,
                booking5, booking6, booking7, booking8);

        BookingShortDto lastBookingExpected1 = new BookingShortDto(booking2.getStart(), booking2.getEnd());
        BookingShortDto nextBookingExpected1 = new BookingShortDto(booking3.getStart(), booking3.getEnd());
        BookingShortDto lastBookingExpected2 = new BookingShortDto(booking6.getStart(), booking6.getEnd());
        BookingShortDto nextBookingExpected2 = new BookingShortDto(booking7.getStart(), booking7.getEnd());

        ItemWithBookingsDto itemWithBookingsDtoExpected1 = new ItemWithBookingsDto(
                item1.getId(), item1.getName(), item1.getDescription(), item1.getAvailable(), item1.getRequestId(),
                lastBookingExpected1, nextBookingExpected1, commentsDtoExpected1
        );

        ItemWithBookingsDto itemWithBookingsDtoExpected2 = new ItemWithBookingsDto(
                item2.getId(), item2.getName(), item2.getDescription(), item2.getAvailable(), item2.getRequestId(),
                lastBookingExpected2, nextBookingExpected2, commentsDtoExpected2
        );

        List<ItemWithBookingsDto> itemWithBookingsDtoListExpected = List.of(
                itemWithBookingsDtoExpected1, itemWithBookingsDtoExpected2);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(itemRepository.findByOwnerId(anyLong())).thenReturn(itemList);
        when(bookingRepository.findByItemIdIn(anyList())).thenReturn(itemsBookings);
        when(commentRepository.findByItemId(1L)).thenReturn(commentList1);
        when(commentRepository.findByItemId(2L)).thenReturn(commentList2);

        //when
        List<ItemWithBookingsDto> itemWithBookingsDtoListActual = itemService.getAllItemsByOwnerWithBookings(owner.getId());

        //then
        for (ItemWithBookingsDto itemWithBookingsDtoExpected : itemWithBookingsDtoListExpected) {
            ItemWithBookingsDto itemWithBookingsDtoActual = itemWithBookingsDtoListActual
                    .get(itemWithBookingsDtoListExpected.indexOf(itemWithBookingsDtoExpected));

            assertThat(itemWithBookingsDtoExpected.getId(), equalTo(itemWithBookingsDtoActual.getId()));
            assertThat(itemWithBookingsDtoExpected.getName(), equalTo(itemWithBookingsDtoActual.getName()));
            assertThat(itemWithBookingsDtoExpected.getDescription(), equalTo(itemWithBookingsDtoActual.getDescription()));
            assertThat(itemWithBookingsDtoExpected.getAvailable(), equalTo(itemWithBookingsDtoActual.getAvailable()));
            assertThat(itemWithBookingsDtoExpected.getLastBooking(), equalTo(itemWithBookingsDtoActual.getLastBooking()));
            assertThat(itemWithBookingsDtoExpected.getNextBooking(), equalTo(itemWithBookingsDtoActual.getNextBooking()));

            List<CommentDto> commentsDtoExpected = itemWithBookingsDtoExpected.getComments();
            List<CommentDto> commentsDtoActual = itemWithBookingsDtoActual.getComments();

            for (CommentDto commentDtoExpected : commentsDtoExpected) {
                CommentDto commentDtoActual = commentsDtoActual.get(commentsDtoExpected.indexOf(commentDtoExpected));
                assertThat(commentDtoExpected.getId(), equalTo(commentDtoActual.getId()));
                assertThat(commentDtoExpected.getText(), equalTo(commentDtoActual.getText()));
                assertThat(commentDtoExpected.getItemId(), equalTo(commentDtoActual.getItemId()));
                assertThat(commentDtoExpected.getAuthorId(), equalTo(commentDtoActual.getAuthorId()));
                assertThat(commentDtoExpected.getAuthorName(), equalTo(commentDtoActual.getAuthorName()));
                assertThat(commentDtoExpected.getCreated(), equalTo(commentDtoActual.getCreated()));
            }
        }
    }

    @Test
    @DisplayName("Должен вернуть пустой лист, если строка null")
    void getAllItemsByText_shouldReturnEmptyList_ifTextIsNull() {
        List<ItemDto> itemDtoList = itemService.getAllItemsByText(null);
        assertTrue(itemDtoList.isEmpty());
    }

    @Test
    @DisplayName("Должен вернуть пустой лист, если строка пустая")
    void getAllItemsByText_shouldReturnEmptyList_ifTextIsEmpty() {
        List<ItemDto> itemDtoList = itemService.getAllItemsByText("");
        assertTrue(itemDtoList.isEmpty());
    }

    @Test
    @DisplayName("Должен вернуть пустой лист, если строка состоит из пробелов")
    void getAllItemsByText_shouldReturnEmptyList_ifTextConsistsOfSpaces() {
        List<ItemDto> itemDtoList = itemService.getAllItemsByText("  ");
        assertTrue(itemDtoList.isEmpty());
    }


    @Test
    @DisplayName("Должен выкинуть исключение, если не нашлось букинга этой вещи этим юзером")
    void createComment_shouldThrowException_ifBookingOfThisItemByThisUserNotFound() {
        when(bookingRepository.findByBookerIdAndItemId(anyLong(), anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> itemService.createComment(1L, 2L, new CommentDto()));
        verify(commentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Должен выкинуть исключение, если срок бронировая не закончился")
    void createComment_shouldThrowException_ifBookingTermHasNotExpired() {
        User owner = new User(1L, "Vasiliy Alekseev", "vasiliy@gmail.com", new HashSet<>());
        User booker = new User(2L, "Sofia Alekseeva", "sofia@gmail.com", new HashSet<>());
        Item item = new Item(3L, owner, "Отвертка",
                "Крестовая отвертка", true, null);

        Booking booking = new Booking(8L,
                LocalDateTime.of(2025, 1, 1, 10, 20, 0, 0),
                LocalDateTime.now().plusMonths(1),
                item, booker, Status.APPROVED);

        when(bookingRepository.findByBookerIdAndItemId(anyLong(), anyLong())).thenReturn(Optional.of(booking));

        assertThrows(ValidationException.class, () -> itemService.createComment(booker.getId(), item.getId(), new CommentDto()));
        verify(commentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Должен сохранить правильный Comment")
    void createComment_shouldSaveCorrectComment() {
        User owner = new User(1L, "Vasiliy Alekseev", "vasiliy@gmail.com", new HashSet<>());
        User booker = new User(2L, "Sofia Alekseeva", "sofia@gmail.com", new HashSet<>());
        Item item = new Item(3L, owner, "Отвертка",
                "Крестовая отвертка", true, null);

        Booking booking = new Booking(4L,
                LocalDateTime.of(2025, 1, 1, 10, 20, 0, 0),
                LocalDateTime.of(2025, 2, 2, 10, 20, 0, 0),
                item, booker, Status.APPROVED);

        CommentDto commentDto = new CommentDto(
                null,
                "Прекрасная отвертка",
                item.getId(),
                booker.getId(),
                booker.getName(),
                null);

        Comment commentExpected = new Comment(
                1L,
                commentDto.getText(),
                item,
                booker,
                LocalDateTime.now()
        );

        when(bookingRepository.findByBookerIdAndItemId(anyLong(), anyLong())).thenReturn(Optional.of(booking));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(commentRepository.save(any(Comment.class))).thenAnswer(invocationOnMock -> {
            Comment savedComment = invocationOnMock.getArgument(0);
            savedComment.setId(1L);
            return savedComment;
        });

        itemService.createComment(booker.getId(), item.getId(), commentDto);

        verify(commentRepository, times(1)).save(commentArgumentCaptor.capture());
        Comment commentActual = commentArgumentCaptor.getValue();

        assertThat(commentActual, notNullValue());
        assertThat(commentExpected.getId(), equalTo(commentActual.getId()));
        assertThat(commentExpected.getText(), equalTo(commentActual.getText()));
        assertThat(commentExpected.getItem(), equalTo(commentActual.getItem()));
        assertThat(commentActual.getCreated(), notNullValue());

    }

    @Test
    @DisplayName("Должен вернуть правильный CommentDto")
    void createComment_shouldReturnCorrectCommentDto() {
        //given
        User owner = new User(1L, "Vasiliy Alekseev", "vasiliy@gmail.com", new HashSet<>());
        User booker = new User(2L, "Sofia Alekseeva", "sofia@gmail.com", new HashSet<>());
        Item item = new Item(3L, owner, "Отвертка",
                "Крестовая отвертка", true, null);

        Booking booking = new Booking(4L,
                LocalDateTime.of(2025, 1, 1, 10, 20, 0, 0),
                LocalDateTime.of(2025, 2, 2, 10, 20, 0, 0),
                item, booker, Status.APPROVED);

        CommentDto commentDto = new CommentDto(
                null,
                "Прекрасная отвертка",
                item.getId(),
                booker.getId(),
                booker.getName(),
                null);

        when(bookingRepository.findByBookerIdAndItemId(anyLong(), anyLong())).thenReturn(Optional.of(booking));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(commentRepository.save(any(Comment.class))).thenAnswer(invocationOnMock -> {
            Comment savedComment = invocationOnMock.getArgument(0);
            savedComment.setId(1L);
            return savedComment;
        });

        //when
        CommentDto commentDtoActual = itemService.createComment(booker.getId(), item.getId(), commentDto);

        //then
        verify(commentRepository, times(1)).save(commentArgumentCaptor.capture());
        Comment savedComment = commentArgumentCaptor.getValue();
        CommentDto commentDtoExpected = new CommentDto(
                1L,
                "Прекрасная отвертка",
                item.getId(),
                booker.getId(),
                booker.getName(),
                savedComment.getCreated());

        assertThat(commentDtoActual, notNullValue());
        assertThat(commentDtoExpected.getId(), equalTo(commentDtoActual.getId()));
        assertThat(commentDtoExpected.getText(), equalTo(commentDtoActual.getText()));
        assertThat(commentDtoExpected.getItemId(), equalTo(commentDtoActual.getItemId()));
        assertThat(commentDtoExpected.getAuthorId(), equalTo(commentDtoActual.getAuthorId()));
        assertThat(commentDtoExpected.getAuthorName(), equalTo(commentDtoActual.getAuthorName()));
        assertThat(commentDtoExpected.getCreated(), equalTo(commentDtoActual.getCreated()));
    }
}