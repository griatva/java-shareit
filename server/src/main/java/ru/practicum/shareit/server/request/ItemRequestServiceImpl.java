package ru.practicum.shareit.server.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.server.exception.NotFoundException;
import ru.practicum.shareit.server.item.Item;
import ru.practicum.shareit.server.item.ItemMapper;
import ru.practicum.shareit.server.item.ItemRepository;
import ru.practicum.shareit.server.item.dto.ItemProposedDto;
import ru.practicum.shareit.server.request.dto.ItemRequestDto;
import ru.practicum.shareit.server.request.dto.ItemRequestWithItemInfoDto;
import ru.practicum.shareit.server.user.User;
import ru.practicum.shareit.server.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequestDto create(Long requestorId, ItemRequestDto itemRequestDto) {
        User requestor = findUserById(requestorId);

        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto);
        itemRequest.setRequestor(requestor);
        itemRequest.setCreateDate(LocalDateTime.now());

        ItemRequest savedItemRequest = itemRequestRepository.save(itemRequest);

        return ItemRequestMapper.toItemRequestDto(savedItemRequest);
    }

    @Override
    public List<ItemRequestWithItemInfoDto> getAllByRequestorIdWithSort(Long requestorId) {
        findUserById(requestorId);
        List<ItemRequest> requests = itemRequestRepository.findByRequestorId(requestorId);
        if (requests.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> requestIds = requests.stream().map(ItemRequest::getId).toList();
        List<Item> items = itemRepository.findByRequestIdIn(requestIds);

        Map<Long, List<ItemProposedDto>> itemsByRequest = items.stream()
                .collect(Collectors.groupingBy(
                        Item::getRequestId,
                        Collectors.collectingAndThen(Collectors.toList(), ItemMapper::toItemProposedDtoList)
                ));

        return requests.stream()
                .map(request -> ItemRequestMapper.toItemRequestWithItemInfoDto(
                        request,
                        itemsByRequest.getOrDefault(request.getId(), List.of())
                ))
                .sorted(Comparator.comparing(ItemRequestWithItemInfoDto::getCreated).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getAllWithSort() {
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByOrderByCreateDateDesc();
        return ItemRequestMapper.toItemRequestDtoList(itemRequests);
    }

    @Override
    public ItemRequestWithItemInfoDto getById(Long requestId) {
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос с id " + requestId + " не найден"));

        List<ItemProposedDto> proposedItems = ItemMapper.toItemProposedDtoList(
                itemRepository.findByRequestId(requestId));

        return ItemRequestMapper.toItemRequestWithItemInfoDto(itemRequest, proposedItems);
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
    }

}
