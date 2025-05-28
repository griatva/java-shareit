package ru.practicum.shareit.server.request;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    List<ItemRequest> findByRequestorId(Long requestorId);

    List<ItemRequest> findAllByOrderByCreateDateDesc();

}
