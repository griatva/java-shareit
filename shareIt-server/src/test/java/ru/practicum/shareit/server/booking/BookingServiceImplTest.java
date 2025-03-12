package ru.practicum.shareit.server.booking;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.server.booking.dto.BookingDto;
import ru.practicum.shareit.server.booking.enums.BookingState;
import ru.practicum.shareit.server.booking.enums.Status;
import ru.practicum.shareit.server.exception.ForbiddenExcepton;
import ru.practicum.shareit.server.exception.NotFoundException;
import ru.practicum.shareit.server.exception.ValidationException;
import ru.practicum.shareit.server.item.Item;
import ru.practicum.shareit.server.item.ItemRepository;
import ru.practicum.shareit.server.item.dto.ItemDto;
import ru.practicum.shareit.server.user.User;
import ru.practicum.shareit.server.user.UserRepository;
import ru.practicum.shareit.server.user.dto.UserDto;

import java.time.LocalDateTime;
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
class BookingServiceImplTest {

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Captor
    private ArgumentCaptor<Booking> bookingArgumentCaptor;


    @Test
    @DisplayName("Должен выбросить исключение, если юзер не найден")
    void create_shouldThrowException_ifBookerNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ForbiddenExcepton.class, () -> bookingService.create(1L, new BookingDto()));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    @DisplayName("Должен выбросить исключение, если вещь не найдена")
    void create_shouldThrowException_ifItemNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        BookingDto bookingDto = new BookingDto(
                null,
                LocalDateTime.of(2025, 1, 3, 12, 15, 10, 0),
                LocalDateTime.of(2025, 1, 25, 12, 0, 0, 0),
                2L,
                null,
                null,
                null
        );

        assertThrows(NotFoundException.class, () -> bookingService.create(1L, bookingDto));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    @DisplayName("Должен выбросить исключение, если вещь недоступна")
    void create_shouldThrowException_ifItemIsNotAvailable() {
        User owner = new User(1L, "Ivan Ivanov", "ivan@gmail.com", null);
        Item item = new Item(1L, owner, "Отвертка",
                "Крестовая отвертка", false, null);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        BookingDto bookingDto = new BookingDto(
                null,
                LocalDateTime.of(2025, 1, 3, 12, 15, 10, 0),
                LocalDateTime.of(2025, 1, 25, 12, 0, 0, 0),
                2L,
                null,
                null,
                null
        );

        assertThrows(ValidationException.class, () -> bookingService.create(2L, bookingDto));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    @DisplayName("Должен установить статус WAITING и сохранить корректный booking")
    void create_shouldSetStatusToWAITINGAndSaveCorrectBooking() {
        User owner = new User(1L, "Ivan Ivanov", "ivan@gmail.com", null);
        User booker = new User(2L, "Irina Ivanova", "irina@gmail.com", null);
        Item item = new Item(3L, owner, "Отвертка",
                "Крестовая отвертка", true, null);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocationOnMock -> {
            Booking savedBooking = invocationOnMock.getArgument(0);
            savedBooking.setId(4L);
            return savedBooking;
        });

        BookingDto bookingDto = new BookingDto(
                null,
                LocalDateTime.of(2025, 1, 3, 12, 15, 10, 0),
                LocalDateTime.of(2025, 1, 25, 12, 0, 0, 0),
                3L,
                null,
                null,
                null
        );

        Booking bookingExpected = new Booking(
                4L,
                bookingDto.getStart(),
                bookingDto.getEnd(),
                item,
                booker,
                Status.WAITING
        );

        //when
        bookingService.create(booker.getId(), bookingDto);

        //then
        verify(bookingRepository, times(1)).save(bookingArgumentCaptor.capture());
        Booking bookingActual = bookingArgumentCaptor.getValue();

        assertThat(bookingActual, notNullValue());
        assertThat(bookingExpected.getId(), equalTo(bookingActual.getId()));
        assertThat(bookingExpected.getStart(), equalTo(bookingActual.getStart()));
        assertThat(bookingExpected.getEnd(), equalTo(bookingActual.getEnd()));
        assertThat(bookingExpected.getItem(), equalTo(bookingActual.getItem()));
        assertThat(bookingExpected.getBooker(), equalTo(bookingActual.getBooker()));
        assertThat(bookingExpected.getStatus(), equalTo(bookingActual.getStatus()));
    }

    @Test
    @DisplayName("Должен вернуть корректный BookingDto")
    void create_shouldReturnCorrectBookingDto() {
        User owner = new User(1L, "Ivan Ivanov", "ivan@gmail.com", null);
        User booker = new User(2L, "Irina Ivanova", "irina@gmail.com", null);
        Item item = new Item(3L, owner, "Отвертка",
                "Крестовая отвертка", true, null);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocationOnMock -> {
            Booking savedBooking = invocationOnMock.getArgument(0);
            savedBooking.setId(4L);
            return savedBooking;
        });

        ItemDto itemDto = new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequestId(),
                null);
        UserDto bookerDto = new UserDto(booker.getId(), booker.getName(), booker.getEmail());


        BookingDto bookingDto = new BookingDto(
                null,
                LocalDateTime.of(2025, 1, 3, 12, 15, 10, 0),
                LocalDateTime.of(2025, 1, 25, 12, 0, 0, 0),
                3L,
                null,
                null,
                null
        );

        BookingDto bookingDtoExpected = new BookingDto(
                4L,
                bookingDto.getStart(),
                bookingDto.getEnd(),
                item.getId(),
                itemDto,
                bookerDto,
                Status.WAITING
        );

        //when
        BookingDto bookingDtoActual = bookingService.create(booker.getId(), bookingDto);

        //then

        assertThat(bookingDtoActual, notNullValue());
        assertThat(bookingDtoExpected.getId(), equalTo(bookingDtoActual.getId()));
        assertThat(bookingDtoExpected.getStart(), equalTo(bookingDtoActual.getStart()));
        assertThat(bookingDtoExpected.getEnd(), equalTo(bookingDtoActual.getEnd()));
        assertThat(bookingDtoExpected.getItemId(), equalTo(bookingDtoActual.getItemId()));
        assertThat(bookingDtoExpected.getItem(), equalTo(bookingDtoActual.getItem()));
        assertThat(bookingDtoExpected.getBooker(), equalTo(bookingDtoActual.getBooker()));
        assertThat(bookingDtoExpected.getStatus(), equalTo(bookingDtoActual.getStatus()));
    }

    @Test
    @DisplayName("Должен выбросить исключение, если юзер не найден")
    void approveOrRejectBooking_shouldThrowException_ifOwnerNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ForbiddenExcepton.class, () -> bookingService.approveOrRejectBooking(
                1L, 2L, true));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    @DisplayName("Должен выбросить исключение, если букинг не найден")
    void approveOrRejectBooking_shouldThrowException_ifBookingNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.approveOrRejectBooking(
                1L, 2L, true));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    @DisplayName("Должен выбросить исключение, если вещь не найдена")
    void approveOrRejectBooking_shouldThrowException_ifItemNotFound() {
        Item item = new Item(3L, new User(), "Отвертка",
                "Крестовая отвертка", true, null);

        Booking booking = new Booking(
                4L,
                LocalDateTime.of(2025, 1, 3, 12, 15, 10, 0),
                LocalDateTime.of(2025, 1, 25, 12, 0, 0, 0),
                item,
                new User(),
                Status.WAITING
        );
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.approveOrRejectBooking(
                1L, booking.getId(), true));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    @DisplayName("Должен выбросить исключение, если согласовать хочет не владелец")
    void approveOrRejectBooking_shouldThrowException_ifNotOwnerTryToApprove() {
        User ownerActual = new User(1L, "Ivan Ivanov", "ivan@gmail.com", null);
        User ownerFake = new User(2L, "Irina Ivanova", "irina@gmail.com", null);
        Item item = new Item(3L, ownerActual, "Отвертка",
                "Крестовая отвертка", true, null);

        Booking booking = new Booking(
                4L,
                LocalDateTime.of(2025, 1, 3, 12, 15, 10, 0),
                LocalDateTime.of(2025, 1, 25, 12, 0, 0, 0),
                item,
                new User(),
                Status.WAITING
        );
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(ownerFake));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        assertThrows(ValidationException.class, () -> bookingService.approveOrRejectBooking(
                ownerFake.getId(), booking.getId(), true));

        verify(bookingRepository, never()).save(any());
    }

    @Test
    @DisplayName("Должен установить переданный статус и сохранить корректный букинг")
    void approveOrRejectBooking_shouldSetStatusAndSaveCorrectBooking() {
        //given
        User owner = new User(1L, "Ivan Ivanov", "ivan@gmail.com", null);
        User booker = new User(2L, "Irina Ivanova", "irina@gmail.com", null);
        Item item = new Item(3L, owner, "Отвертка",
                "Крестовая отвертка", true, null);

        Booking booking = new Booking(
                4L,
                LocalDateTime.of(2025, 1, 3, 12, 15, 10, 0),
                LocalDateTime.of(2025, 1, 25, 12, 0, 0, 0),
                item,
                booker,
                Status.WAITING
        );
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        Booking bookingExpected = new Booking(
                4L,
                LocalDateTime.of(2025, 1, 3, 12, 15, 10, 0),
                LocalDateTime.of(2025, 1, 25, 12, 0, 0, 0),
                item,
                booker,
                Status.APPROVED
        );

        //when
        bookingService.approveOrRejectBooking(owner.getId(), booking.getId(), true);

        //then
        verify(bookingRepository, times(1)).save(bookingArgumentCaptor.capture());
        Booking bookingActual = bookingArgumentCaptor.getValue();

        assertThat(bookingActual, notNullValue());
        assertThat(bookingExpected.getId(), equalTo(bookingActual.getId()));
        assertThat(bookingExpected.getStart(), equalTo(bookingActual.getStart()));
        assertThat(bookingExpected.getEnd(), equalTo(bookingActual.getEnd()));
        assertThat(bookingExpected.getItem(), equalTo(bookingActual.getItem()));
        assertThat(bookingExpected.getBooker(), equalTo(bookingActual.getBooker()));
        assertThat(bookingExpected.getStatus(), equalTo(bookingActual.getStatus()));

    }

    @Test
    @DisplayName("Должен вернуть корректный BookingDto")
    void approveOrRejectBooking_shouldReturnCorrectBookingDto() {
        //given
        User owner = new User(1L, "Ivan Ivanov", "ivan@gmail.com", null);
        User booker = new User(2L, "Irina Ivanova", "irina@gmail.com", null);
        Item item = new Item(3L, owner, "Отвертка",
                "Крестовая отвертка", true, null);

        Booking booking = new Booking(
                4L,
                LocalDateTime.of(2025, 1, 3, 12, 15, 10, 0),
                LocalDateTime.of(2025, 1, 25, 12, 0, 0, 0),
                item,
                booker,
                Status.WAITING
        );
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        ItemDto itemDto = new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequestId(),
                null);
        UserDto bookerDto = new UserDto(booker.getId(), booker.getName(), booker.getEmail());


        BookingDto bookingDtoExpected = new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                item.getId(),
                itemDto,
                bookerDto,
                Status.APPROVED
        );

        //when
        BookingDto bookingDtoActual = bookingService.approveOrRejectBooking(
                owner.getId(), booking.getId(), true);

        //then
        assertThat(bookingDtoActual, notNullValue());
        assertThat(bookingDtoExpected.getId(), equalTo(bookingDtoActual.getId()));
        assertThat(bookingDtoExpected.getStart(), equalTo(bookingDtoActual.getStart()));
        assertThat(bookingDtoExpected.getEnd(), equalTo(bookingDtoActual.getEnd()));
        assertThat(bookingDtoExpected.getItemId(), equalTo(bookingDtoActual.getItemId()));
        assertThat(bookingDtoExpected.getItem(), equalTo(bookingDtoActual.getItem()));
        assertThat(bookingDtoExpected.getBooker(), equalTo(bookingDtoActual.getBooker()));
        assertThat(bookingDtoExpected.getStatus(), equalTo(bookingDtoActual.getStatus()));
    }

    @Test
    @DisplayName("Должен выбросить исключение, если юзер не найден")
    void getById_shouldThrowException_ifOwnerNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ForbiddenExcepton.class, () -> bookingService.getById(1L, 2L));
    }

    @Test
    @DisplayName("Должен выбросить исключение, если букинг не найден")
    void getById_shouldThrowException_ifBookingNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.getById(1L, 2L));
    }

    @Test
    @DisplayName("Должен выбросить исключение, если получить бронирование хочет не владелец вещи и не букер")
    void getById_shouldThrowException_ifNotOwnerOrBookerTryToGetBooking() {
        User owner = new User(1L, "Ivan Ivanov", "ivan@gmail.com", null);
        User booker = new User(2L, "Irina Ivanova", "irina@gmail.com", null);
        User requester = new User(3L, "Valeriy Vasiliev", "valeriy@gmail.com", null);

        Item item = new Item(4L, owner, "Отвертка",
                "Крестовая отвертка", true, null);

        Booking booking = new Booking(
                5L,
                LocalDateTime.of(2025, 1, 3, 12, 15, 10, 0),
                LocalDateTime.of(2025, 1, 25, 12, 0, 0, 0),
                item,
                booker,
                Status.WAITING
        );

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(requester));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        assertThrows(ValidationException.class, () -> bookingService.getById(requester.getId(), booking.getId()));
    }

    @Test
    @DisplayName("Должен вернуть корректный BookingDto")
    void getById_shouldReturnCorrectBookingDto() {
        User owner = new User(1L, "Ivan Ivanov", "ivan@gmail.com", null);
        User booker = new User(2L, "Irina Ivanova", "irina@gmail.com", null);

        Item item = new Item(4L, owner, "Отвертка",
                "Крестовая отвертка", true, null);

        Booking booking = new Booking(
                5L,
                LocalDateTime.of(2025, 1, 3, 12, 15, 10, 0),
                LocalDateTime.of(2025, 1, 25, 12, 0, 0, 0),
                item,
                booker,
                Status.WAITING
        );

        ItemDto itemDto = new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequestId(),
                null);
        UserDto bookerDto = new UserDto(booker.getId(), booker.getName(), booker.getEmail());

        BookingDto bookingDtoExpected = new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                item.getId(),
                itemDto,
                bookerDto,
                booking.getStatus()
        );

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        BookingDto bookingDtoActual = bookingService.getById(owner.getId(), booking.getId());

        //then
        assertThat(bookingDtoActual, notNullValue());
        assertThat(bookingDtoExpected.getId(), equalTo(bookingDtoActual.getId()));
        assertThat(bookingDtoExpected.getStart(), equalTo(bookingDtoActual.getStart()));
        assertThat(bookingDtoExpected.getEnd(), equalTo(bookingDtoActual.getEnd()));
        assertThat(bookingDtoExpected.getItemId(), equalTo(bookingDtoActual.getItemId()));
        assertThat(bookingDtoExpected.getItem(), equalTo(bookingDtoActual.getItem()));
        assertThat(bookingDtoExpected.getBooker(), equalTo(bookingDtoActual.getBooker()));
        assertThat(bookingDtoExpected.getStatus(), equalTo(bookingDtoActual.getStatus()));
    }

    @Test
    @DisplayName("Должен выбросить исключение, если юзер не найден")
    void getBookingsByBookerIdWithFilter_shouldThrowException_ifOwnerNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ForbiddenExcepton.class, () -> bookingService.getBookingsByBookerIdWithFilter(
                1L, BookingState.ALL));
    }

    @Test
    @DisplayName("Должен вызвать метод findByBookerIdOrderByStartDesc, когда state = ALL")
    void getBookingsByBookerIdWithFilter_shouldCallRelevantMethod_ifStateIsALL() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));

        bookingService.getBookingsByBookerIdWithFilter(1L, BookingState.ALL);

        verify(bookingRepository, times(1))
                .findByBookerIdOrderByStartDesc(1L);
    }

    @Test
    @DisplayName("Должен вызвать метод findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc, когда state = CURRENT")
    void getBookingsByBookerIdWithFilter_shouldCallRelevantMethod_ifStateIsCURRENT() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));

        bookingService.getBookingsByBookerIdWithFilter(1L, BookingState.CURRENT);

        verify(bookingRepository, times(1))
                .findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(eq(1L), any(), any());
    }

    @Test
    @DisplayName("Должен вызвать метод findByBookerIdAndEndBeforeOrderByStartDesc, когда state = PAST")
    void getBookingsByBookerIdWithFilter_shouldCallRelevantMethod_ifStateIsPAST() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));

        bookingService.getBookingsByBookerIdWithFilter(1L, BookingState.PAST);

        verify(bookingRepository, times(1))
                .findByBookerIdAndEndBeforeOrderByStartDesc(eq(1L), any());
    }

    @Test
    @DisplayName("Должен вызвать метод findByBookerIdAndStartAfterOrderByStartDesc, когда state = FUTURE")
    void getBookingsByBookerIdWithFilter_shouldCallRelevantMethod_ifStateIsFUTURE() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));

        bookingService.getBookingsByBookerIdWithFilter(1L, BookingState.FUTURE);

        verify(bookingRepository, times(1))
                .findByBookerIdAndStartAfterOrderByStartDesc(eq(1L), any());
    }

    @Test
    @DisplayName("Должен вызвать метод findByBookerIdAndStartAfterOrderByStartDesc, когда state = WAITING")
    void getBookingsByBookerIdWithFilter_shouldCallRelevantMethod_ifStateIsWAITING() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));

        bookingService.getBookingsByBookerIdWithFilter(1L, BookingState.WAITING);

        verify(bookingRepository, times(1))
                .findByBookerIdAndStatusOrderByStartDesc(eq(1L), any());
    }

    @Test
    @DisplayName("Должен вызвать метод findByBookerIdAndStatusOrderByStartDesc, когда state = REJECTED")
    void getBookingsByBookerIdWithFilter_shouldCallRelevantMethod_ifStateIsREJECTED() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));

        bookingService.getBookingsByBookerIdWithFilter(1L, BookingState.REJECTED);

        verify(bookingRepository, times(1))
                .findByBookerIdAndStatusOrderByStartDesc(eq(1L), any());
    }

    @Test
    @DisplayName("Должен выбросить исключение, если юзер не найден")
    void getBookingsByOwnerIdWithFilter_shouldThrowException_ifOwnerNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ForbiddenExcepton.class, () -> bookingService.getBookingsByOwnerIdWithFilter(
                1L, BookingState.ALL));
    }

    @Test
    @DisplayName("Должен вернуть пустой лист, если у юзера нет вещей")
    void getBookingsByOwnerIdWithFilter_shouldReturnEmptyList_ifOwnerDasNotHaveItems() {
        User owner = new User(1L, "Ivan Ivanov", "ivan@gmail.com", new HashSet<>());

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));

        List<BookingDto> bookingDtoList = bookingService.getBookingsByOwnerIdWithFilter(
                1L, BookingState.ALL);

        assertTrue(bookingDtoList.isEmpty());
    }


    @Test
    @DisplayName("Должен вызвать метод findByItemOwnerIdOrderByStartDesc, когда state = ALL")
    void getBookingsByOwnerIdWithFilter_shouldCallRelevantMethod_ifStateIsALL() {

        User owner = new User(1L, "Ivan Ivanov", "ivan@gmail.com", new HashSet<>());
        Item item1 = new Item(2L, owner, "Отвертка",
                "Крестовая отвертка", true, null);
        Item item2 = new Item(3L, owner, "Шуруповерт",
                "Красный шупуповерт", true, null);
        owner.getItems().add(item1);
        owner.getItems().add(item2);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));

        bookingService.getBookingsByOwnerIdWithFilter(owner.getId(), BookingState.ALL);

        verify(bookingRepository, times(1))
                .findByItemOwnerIdOrderByStartDesc(owner.getId());
    }

    @Test
    @DisplayName("Должен вызвать метод findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc, " +
            "когда state = CURRENT")
    void getBookingsByOwnerIdWithFilter_shouldCallRelevantMethod_ifStateIsCURRENT() {

        User owner = new User(1L, "Ivan Ivanov", "ivan@gmail.com", new HashSet<>());
        Item item1 = new Item(2L, owner, "Отвертка",
                "Крестовая отвертка", true, null);
        Item item2 = new Item(3L, owner, "Шуруповерт",
                "Красный шупуповерт", true, null);
        owner.getItems().add(item1);
        owner.getItems().add(item2);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));

        bookingService.getBookingsByOwnerIdWithFilter(owner.getId(), BookingState.CURRENT);

        verify(bookingRepository, times(1))
                .findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(eq(owner.getId()), any(), any());
    }

    @Test
    @DisplayName("Должен вызвать метод findByItemOwnerIdAndEndBeforeOrderByStartDesc, когда state = PAST")
    void getBookingsByOwnerIdWithFilter_shouldCallRelevantMethod_ifStateIsPAST() {

        User owner = new User(1L, "Ivan Ivanov", "ivan@gmail.com", new HashSet<>());
        Item item1 = new Item(2L, owner, "Отвертка",
                "Крестовая отвертка", true, null);
        Item item2 = new Item(3L, owner, "Шуруповерт",
                "Красный шупуповерт", true, null);
        owner.getItems().add(item1);
        owner.getItems().add(item2);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));

        bookingService.getBookingsByOwnerIdWithFilter(owner.getId(), BookingState.PAST);

        verify(bookingRepository, times(1))
                .findByItemOwnerIdAndEndBeforeOrderByStartDesc(eq(owner.getId()), any());
    }

    @Test
    @DisplayName("Должен вызвать метод findByItemOwnerIdAndStartAfterOrderByStartDesc, когда state = FUTURE")
    void getBookingsByOwnerIdWithFilter_shouldCallRelevantMethod_ifStateIsFUTURE() {

        User owner = new User(1L, "Ivan Ivanov", "ivan@gmail.com", new HashSet<>());
        Item item1 = new Item(2L, owner, "Отвертка",
                "Крестовая отвертка", true, null);
        Item item2 = new Item(3L, owner, "Шуруповерт",
                "Красный шупуповерт", true, null);
        owner.getItems().add(item1);
        owner.getItems().add(item2);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));

        bookingService.getBookingsByOwnerIdWithFilter(owner.getId(), BookingState.FUTURE);

        verify(bookingRepository, times(1))
                .findByItemOwnerIdAndStartAfterOrderByStartDesc(eq(owner.getId()), any());
    }

    @Test
    @DisplayName("Должен вызвать метод findByItemOwnerIdAndStatusOrderByStartDesc, когда state = WAITING")
    void getBookingsByOwnerIdWithFilter_shouldCallRelevantMethod_ifStateIsWAITING() {

        User owner = new User(1L, "Ivan Ivanov", "ivan@gmail.com", new HashSet<>());
        Item item1 = new Item(2L, owner, "Отвертка",
                "Крестовая отвертка", true, null);
        Item item2 = new Item(3L, owner, "Шуруповерт",
                "Красный шупуповерт", true, null);
        owner.getItems().add(item1);
        owner.getItems().add(item2);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));

        bookingService.getBookingsByOwnerIdWithFilter(owner.getId(), BookingState.WAITING);

        verify(bookingRepository, times(1))
                .findByItemOwnerIdAndStatusOrderByStartDesc(eq(owner.getId()), any());
    }

    @Test
    @DisplayName("Должен вызвать метод findByItemOwnerIdAndStatusOrderByStartDesc, когда state = REJECTED")
    void getBookingsByOwnerIdWithFilter_shouldCallRelevantMethod_ifStateIsREJECTED() {

        User owner = new User(1L, "Ivan Ivanov", "ivan@gmail.com", new HashSet<>());
        Item item1 = new Item(2L, owner, "Отвертка",
                "Крестовая отвертка", true, null);
        Item item2 = new Item(3L, owner, "Шуруповерт",
                "Красный шупуповерт", true, null);
        owner.getItems().add(item1);
        owner.getItems().add(item2);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));

        bookingService.getBookingsByOwnerIdWithFilter(owner.getId(), BookingState.REJECTED);

        verify(bookingRepository, times(1))
                .findByItemOwnerIdAndStatusOrderByStartDesc(eq(owner.getId()), any());
    }
}