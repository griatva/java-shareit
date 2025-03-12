package ru.practicum.shareit.server.request;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.ApplicationContext;
import org.springframework.http.*;
import ru.practicum.shareit.server.request.dto.ItemRequestDto;
import ru.practicum.shareit.server.request.dto.ItemRequestWithItemInfoDto;
import ru.practicum.shareit.server.user.dto.UserDto;

import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ItemRequestIntegrationTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void create_shouldReturn200AndCreatedDto() {
        //creating requestor
        UserDto userDto = new UserDto(null, "Piotr Ivanov", "piotr7@gmail.com");
        ResponseEntity<UserDto> responseUser = testRestTemplate.postForEntity("/users", userDto, UserDto.class);
        UserDto requestor = responseUser.getBody();
        assertNotNull(requestor);
        long requestorId = requestor.getId();

        //creating request
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Sharer-User-Id", String.valueOf(requestorId));
        headers.setContentType(MediaType.APPLICATION_JSON);

        ItemRequestDto itemRequestDto = new ItemRequestDto(null, "Need a drill", null);
        HttpEntity<ItemRequestDto> requestEntity = new HttpEntity<>(itemRequestDto, headers);

        ResponseEntity<ItemRequestDto> responseRequest = testRestTemplate.exchange(
                "/requests", HttpMethod.POST, requestEntity, ItemRequestDto.class);

        //checks
        assertThat(responseRequest.getStatusCode()).isEqualTo(HttpStatus.OK);

        ItemRequestDto createdRequest = responseRequest.getBody();
        assertNotNull(createdRequest);
        assertNotNull(createdRequest.getId());
        assertNotNull(createdRequest.getCreated());
        assertThat(createdRequest.getDescription()).isEqualTo("Need a drill");
    }

    @Test
    void getAllByRequestorIdWithSort_shouldReturn200AndListDto() {

        //creating requestor
        UserDto userDto = new UserDto(null, "ivan Ivanov", "ivan7@gmail.com");
        ResponseEntity<UserDto> responseUser = testRestTemplate.postForEntity("/users", userDto, UserDto.class);
        UserDto requestor = responseUser.getBody();
        assertNotNull(requestor);
        long requestorId = requestor.getId();

        //creating requests
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Sharer-User-Id", String.valueOf(requestorId));
        headers.setContentType(MediaType.APPLICATION_JSON);

        ItemRequestDto itemRequestDto1 = new ItemRequestDto(null, "Need a drill", null);
        HttpEntity<ItemRequestDto> requestEntity1 = new HttpEntity<>(itemRequestDto1, headers);
        ResponseEntity<ItemRequestDto> responseRequest1 = testRestTemplate.exchange(
                "/requests", HttpMethod.POST, requestEntity1, ItemRequestDto.class);
        ItemRequestDto createdRequest1 = responseRequest1.getBody();
        assertNotNull(createdRequest1);

        ItemRequestDto itemRequestDto2 = new ItemRequestDto(null, "Need a laptop", null);
        HttpEntity<ItemRequestDto> requestEntity2 = new HttpEntity<>(itemRequestDto2, headers);
        ResponseEntity<ItemRequestDto> responseRequest2 = testRestTemplate.exchange(
                "/requests", HttpMethod.POST, requestEntity2, ItemRequestDto.class);
        ItemRequestDto createdRequest2 = responseRequest2.getBody();
        assertNotNull(createdRequest2);

        // getting requests
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<ItemRequestWithItemInfoDto[]> response = testRestTemplate.exchange(
                "/requests", HttpMethod.GET, requestEntity, ItemRequestWithItemInfoDto[].class);

        //checks
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        ItemRequestWithItemInfoDto[] requests = response.getBody();
        assertNotNull(requests);
        assertThat(requests.length).isEqualTo(2);

        // checking fields
        ItemRequestWithItemInfoDto request1 = requests[0];
        ItemRequestWithItemInfoDto request2 = requests[1];
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SS");

        assertThat(request1.getId()).isEqualTo(createdRequest2.getId());
        assertThat(request1.getDescription()).isEqualTo(createdRequest2.getDescription());
        assertThat(request1.getCreated().format(formatter)).isEqualTo(createdRequest2.getCreated().format(formatter));

        assertThat(request2.getId()).isEqualTo(createdRequest1.getId());
        assertThat(request2.getDescription()).isEqualTo(createdRequest1.getDescription());
        assertThat(request2.getCreated().format(formatter)).isEqualTo(createdRequest1.getCreated().format(formatter));

    }

}