package ru.practicum.shareit.server.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.server.exception.NotFoundException;
import ru.practicum.shareit.server.item.Item;
import ru.practicum.shareit.server.item.ItemRepository;
import ru.practicum.shareit.server.item.dto.ItemProposedDto;
import ru.practicum.shareit.server.request.dto.ItemRequestDto;
import ru.practicum.shareit.server.request.dto.ItemRequestWithItemInfoDto;
import ru.practicum.shareit.server.user.User;
import ru.practicum.shareit.server.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    @Captor
    private ArgumentCaptor<ItemRequest> itemRequestArgumentCaptor;


    @Test
    @DisplayName("Должен выбросить исключение и не сохранять запрос в БД, если юзер не найден")
    void create_shouldThrowExceptionAndNotSaveRequestIntoDB_ifRequestorNotFound() {
        User user = new User(1L, "Ivan Ivanov", "ivan@gmail.com", null);
        ItemRequestDto itemRequestDto = new ItemRequestDto(null, "Нужен шуруповерт", null);

        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.create(user.getId(), itemRequestDto));

        verify(itemRequestRepository, never()).save(any());
    }

    @Test
    @DisplayName("Должен сохранять корректный ItemRequest")
    void create_ShouldSaveCorrectItemRequest() {
        //given
        User requestor = new User(1L, "Ivan Ivanov", "ivan@gmail.com", null);
        ItemRequest itemRequestExpected = new ItemRequest(1L, "Нужен шуруповерт", requestor, LocalDateTime.now());
        ItemRequestDto itemRequestDto = new ItemRequestDto(null, "Нужен шуруповерт", null);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(requestor));
        when(itemRequestRepository.save(any(ItemRequest.class))).thenAnswer(invocationOnMock -> {
            ItemRequest savedRequest = invocationOnMock.getArgument(0);
            savedRequest.setId(1L);
            return savedRequest;
        });

        //when
        itemRequestService.create(requestor.getId(), itemRequestDto);

        //then
        verify(itemRequestRepository, times(1)).save(itemRequestArgumentCaptor.capture());
        ItemRequest itemRequestActual = itemRequestArgumentCaptor.getValue();

        assertThat(itemRequestActual, notNullValue());
        assertThat(itemRequestActual.getId(), equalTo(itemRequestExpected.getId()));
        assertThat(itemRequestActual.getDescription(), equalTo(itemRequestExpected.getDescription()));
        assertThat(itemRequestActual.getCreateDate(), notNullValue());
        assertThat(itemRequestActual.getRequestor().getId(), equalTo(requestor.getId()));

    }

    @Test
    @DisplayName("Должен вернуть корректный ItemRequestDto")
    void create_shouldReturnCorrectItemRequestDto() {
        //given
        User requestor = new User(1L, "Ivan Ivanov", "ivan@gmail.com", null);
        ItemRequestDto itemRequestDto = new ItemRequestDto(null, "Нужен шуруповерт", null);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(requestor));
        when(itemRequestRepository.save(any(ItemRequest.class))).thenAnswer(invocation -> {
            ItemRequest savedRequest = invocation.getArgument(0);
            savedRequest.setId(1L);
            return savedRequest;
        });

        //when
        ItemRequestDto itemRequestDtoActual = itemRequestService.create(requestor.getId(), itemRequestDto);

        verify(itemRequestRepository, times(1)).save(itemRequestArgumentCaptor.capture());
        ItemRequest savedItemRequest = itemRequestArgumentCaptor.getValue();
        ItemRequestDto itemRequestDtoExpected = new ItemRequestDto(
                savedItemRequest.getId(),
                savedItemRequest.getDescription(),
                savedItemRequest.getCreateDate());

        //then
        assertThat(itemRequestDtoActual, notNullValue());
        assertThat(itemRequestDtoActual.getId(), equalTo(itemRequestDtoExpected.getId()));
        assertThat(itemRequestDtoActual.getDescription(), equalTo(itemRequestDtoExpected.getDescription()));
        assertThat(itemRequestDtoActual.getCreated(), equalTo(itemRequestDtoExpected.getCreated()));
    }

    @Test
    @DisplayName("Должен выбросить исключение, если юзер не найден")
    void getAllByRequestorIdWithSort_shouldThrowException_ifRequestorNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> itemRequestService.getAllByRequestorIdWithSort(1L));
        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    @DisplayName("Должен вернуть пустой лист, если у юзера нет своих запросов")
    void getAllByRequestorIdWithSort_shouldReturnEmptyList_ifUserDasNotHaveRequests() {
        //given
        User requestor = new User(1L, "Ivan Ivanov", "ivan@gmail.com", null);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(requestor));
        when(itemRequestRepository.findByRequestorId(anyLong())).thenReturn(Collections.emptyList());

        //when
        List<ItemRequestWithItemInfoDto> requests = itemRequestService.getAllByRequestorIdWithSort(requestor.getId());

        //then
        assertThat(requests.isEmpty(), is(true));
        verify(itemRequestRepository, times(1)).findByRequestorId(anyLong());
        verify(itemRepository, never()).findByRequestIdIn(anyList());
    }

    @Test
    @DisplayName("Должен вернуть лист с корректно заполненными ItemRequestWithItemInfoDto " +
            "с сортировкой по дате создания от новых к старым")
    void getAllByRequestorIdWithSort_shouldReturnItemRequestWithItemInfoDtoListWithSortByDateDesc() {
        //given
        User requestor = new User(1L, "Ivan Ivanov", "ivan@gmail.com", null);
        User owner1 = new User(2L, "Piotr Petrov", "piotr@gmail.com", null);
        User owner2 = new User(3L, "Vasiliy Vasilyev", "vasya@gmail.com", null);

        ItemRequest itemRequest1 = new ItemRequest(1L, "Нужна отвертка", requestor,
                LocalDateTime.of(2025, 3, 1, 23, 18, 10, 10));
        ItemRequest itemRequest2 = new ItemRequest(2L, "Нужен шуруповерт", requestor,
                LocalDateTime.of(2025, 3, 2, 23, 18, 10, 10));
        List<ItemRequest> requests = List.of(itemRequest1, itemRequest2);

        Item item1 = new Item(1L, owner1, "Отвертка", "Крестовая отвертка", true, 1L);
        Item item2 = new Item(2L, owner2, "Шуруповерт1", "Желтый шуруповерт", true, 2L);
        Item item3 = new Item(3L, owner2, "Шуруповерт2", "Красный шуруповерт", true, 2L);
        owner1.setItems(Set.of(item1));
        owner2.setItems(Set.of(item2, item3));
        List<Item> items = List.of(item1, item2, item3);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(requestor));
        when(itemRequestRepository.findByRequestorId(anyLong())).thenReturn(requests);
        when(itemRepository.findByRequestIdIn(anyList())).thenReturn(items);


        ItemRequestWithItemInfoDto itemRequestWithItemInfoDtoExpected1 = new ItemRequestWithItemInfoDto(
                itemRequest1.getId(),
                itemRequest1.getDescription(),
                itemRequest1.getCreateDate(),
                List.of(new ItemProposedDto(item1.getId(), item1.getName(), item1.getOwner().getId())));

        ItemRequestWithItemInfoDto itemRequestWithItemInfoDtoExpected2 = new ItemRequestWithItemInfoDto(
                itemRequest2.getId(),
                itemRequest2.getDescription(),
                itemRequest2.getCreateDate(),
                List.of(new ItemProposedDto(item2.getId(), item2.getName(), item2.getOwner().getId()),
                        new ItemProposedDto(item3.getId(), item3.getName(), item3.getOwner().getId())));

        List<ItemRequestWithItemInfoDto> requestListExpected = List.of(
                itemRequestWithItemInfoDtoExpected2, itemRequestWithItemInfoDtoExpected1);

        //when
        List<ItemRequestWithItemInfoDto> requestListActual =
                itemRequestService.getAllByRequestorIdWithSort(requestor.getId());

        //then
        assertThat(requestListActual, notNullValue());
        assertThat(requestListExpected.size(), equalTo(requestListActual.size()));

        for (ItemRequestWithItemInfoDto requestExpected : requestListExpected) {
            ItemRequestWithItemInfoDto requestActual = requestListActual.get(requestListExpected.indexOf(requestExpected));

            assertThat(requestExpected.getId(), equalTo(requestActual.getId()));
            assertThat(requestExpected.getDescription(), equalTo(requestActual.getDescription()));
            assertThat(requestExpected.getCreated(), equalTo(requestActual.getCreated()));

            List<ItemProposedDto> itemListExpected = requestExpected.getItems();
            List<ItemProposedDto> itemListActual = requestActual.getItems();

            for (ItemProposedDto itemExpected : itemListExpected) {
                ItemProposedDto itemActual = itemListActual.get(itemListExpected.indexOf(itemExpected));

                assertThat(itemExpected.getId(), equalTo(itemActual.getId()));
                assertThat(itemExpected.getName(), equalTo(itemActual.getName()));
                assertThat(itemExpected.getOwnerId(), equalTo(itemActual.getOwnerId()));
            }

        }
        assertTrue(requestListActual.getFirst().getCreated()
                .isAfter(requestListActual.getLast().getCreated()));
    }


    @Test
    @DisplayName("Должен вернуть пустой лист, если в БД нет запросов")
    void getAllWithSort_shouldReturnEmptyList_ifThereAreNoRequestsInDB() {
        when(itemRequestRepository.findAllByOrderByCreateDateDesc()).thenReturn(Collections.emptyList());
        List<ItemRequestDto> requests = itemRequestService.getAllWithSort();
        assertTrue(requests != null && requests.isEmpty());
    }

    @Test
    @DisplayName("Должен вернуть лист со всеми ItemRequestDto в базе с сортировкой по дате создания от новых к старым")
    void getAllWithSort_shouldReturnItemRequestDtoListWithSortByDateDesc() {
        //given
        User requestor1 = new User(1L, "Ivan Ivanov", "ivan@gmail.com", null);
        User requestor2 = new User(1L, "Piotr Petrov", "piotr@gmail.com", null);

        ItemRequest itemRequest1 = new ItemRequest(1L, "Нужна отвертка", requestor1,
                LocalDateTime.of(2025, 3, 1, 23, 18, 10, 10));
        ItemRequest itemRequest2 = new ItemRequest(2L, "Нужен шуруповерт", requestor2,
                LocalDateTime.of(2025, 3, 2, 23, 18, 10, 10));

        List<ItemRequest> requests = List.of(itemRequest2, itemRequest1);
        when(itemRequestRepository.findAllByOrderByCreateDateDesc()).thenReturn(requests);
        List<ItemRequestDto> requestsDtoListExpected = List.of(
                new ItemRequestDto(2L, "Нужен шуруповерт",
                        LocalDateTime.of(2025, 3, 2, 23, 18, 10, 10)),
                new ItemRequestDto(1L, "Нужна отвертка",
                        LocalDateTime.of(2025, 3, 1, 23, 18, 10, 10)));

        //when
        List<ItemRequestDto> requestsDtoListActual = itemRequestService.getAllWithSort();

        //then
        assertTrue(requestsDtoListActual != null && !requestsDtoListActual.isEmpty());

        for (ItemRequestDto requestDtoExpected : requestsDtoListExpected) {
            ItemRequestDto requestDtoActual = requestsDtoListActual.get(requestsDtoListExpected.indexOf(requestDtoExpected));

            assertThat(requestDtoExpected.getId(), equalTo(requestDtoActual.getId()));
            assertThat(requestDtoExpected.getDescription(), equalTo(requestDtoActual.getDescription()));
            assertThat(requestDtoExpected.getCreated(), equalTo(requestDtoActual.getCreated()));
        }
        assertTrue(requestsDtoListActual.getFirst().getCreated()
                .isAfter(requestsDtoListActual.getLast().getCreated()));
    }

    @Test
    @DisplayName("Должен выбросить исключение, если запрос не найден")
    void getById_shouldThrowException_ifRequestNotFound() {
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> itemRequestService.getById(1L));
    }

    @Test
    @DisplayName("Должен вернуть ItemRequestWithItemInfoDto с корректно заполненными полями")
    void getById_shouldReturnCorrectItemRequestWithItemInfoDto() {
        //given
        User requestor = new User(1L, "Ivan Ivanov", "ivan@gmail.com", null);
        User owner1 = new User(2L, "Piotr Petrov", "piotr@gmail.com", null);
        User owner2 = new User(3L, "Vasiliy Vasilyev", "vasya@gmail.com", null);
        ItemRequest itemRequest = new ItemRequest(1L, "Нужна отвертка", requestor,
                LocalDateTime.of(2025, 3, 1, 23, 18, 10, 10));
        Item item1 = new Item(1L, owner1, "Отвертка1", "Крестовая отвертка", true, 1L);
        Item item2 = new Item(2L, owner2, "Отвертка2", "Многофункциональная отвертка", true, 1L);
        List<Item> items = List.of(item1, item2);

        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));
        when(itemRepository.findByRequestId(anyLong())).thenReturn(items);

        ItemRequestWithItemInfoDto requestExpected = new ItemRequestWithItemInfoDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getCreateDate(),
                List.of(new ItemProposedDto(item1.getId(), item1.getName(), item1.getOwner().getId()),
                        new ItemProposedDto(item2.getId(), item2.getName(), item2.getOwner().getId())));

        //when
        ItemRequestWithItemInfoDto requestActual = itemRequestService.getById(1L);

        //then
        assertThat(requestExpected.getId(), equalTo(requestActual.getId()));
        assertThat(requestExpected.getDescription(), equalTo(requestActual.getDescription()));
        assertThat(requestExpected.getCreated(), equalTo(requestActual.getCreated()));

        List<ItemProposedDto> itemListExpected = requestExpected.getItems();
        List<ItemProposedDto> itemListActual = requestActual.getItems();

        for (ItemProposedDto itemDtoExpected : itemListExpected) {
            ItemProposedDto itemDtoActual = itemListActual.get(itemListExpected.indexOf(itemDtoExpected));
            assertThat(itemDtoExpected.getId(), equalTo(itemDtoActual.getId()));
            assertThat(itemDtoExpected.getName(), equalTo(itemDtoActual.getName()));
            assertThat(itemDtoExpected.getOwnerId(), equalTo(itemDtoActual.getOwnerId()));
        }
    }

}