package ru.practicum.shareit.server.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.server.user.dto.UserDto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserIntegrationTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void create_shouldReturn200AndCreatedDto() {

        UserDto userDto = new UserDto(null, "Piotr Ivanov", "piotr@gmail.com");
        UserDto userDtoExpected = new UserDto(1L, "Piotr Ivanov", "piotr@gmail.com");

        ResponseEntity<UserDto> response = testRestTemplate.postForEntity("/users", userDto, UserDto.class);


        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        UserDto userDtoActual = response.getBody();
        assertNotNull(userDtoActual);
        assertNotNull(userDtoActual.getId());

        assertThat(userDtoExpected)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(userDtoActual);
    }


    @Test
    void getUserById_shouldReturnUser() {
        UserDto userDto = new UserDto(null, "Anna Ivanova", "anna@gmail.com");
        UserDto createdUser = testRestTemplate.postForObject("/users", userDto, UserDto.class);
        assertNotNull(createdUser);
        long userId = createdUser.getId();

        UserDto userDtoExpected = new UserDto(userId, "Anna Ivanova", "anna@gmail.com");

        ResponseEntity<UserDto> response = testRestTemplate.getForEntity("/users/{id}", UserDto.class, userId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        UserDto userDtoActual = response.getBody();

        assertThat(userDtoActual).isNotNull();

        assertThat(userDtoExpected)
                .usingRecursiveComparison()
                .isEqualTo(userDtoActual);

    }
}