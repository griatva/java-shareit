package ru.practicum.shareit.server.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.server.user.dto.UserDto;
import ru.practicum.shareit.server.user.dto.UserUpdateDto;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @SneakyThrows
    @Test
    void create_shouldCreateUserAndReturn200() {
        UserDto userDtoToCreate = new UserDto(null, "Ivan Ivanov", "ivan@gmail.com");
        UserDto createdUserDto = new UserDto(1L, "Ivan Ivanov", "ivan@gmail.com");

        when(userService.create(userDtoToCreate)).thenReturn(createdUserDto);

        String result = mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDtoToCreate))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(createdUserDto), result);
    }

    @SneakyThrows
    @Test
    void update_shouldUpdateUserAndReturn200() {
        UserUpdateDto userDtoToUpdate = new UserUpdateDto("Piotr Ivanov", "piotr@gmail.com");
        UserDto updatedUserDto = new UserDto(1L, "Piotr Ivanov", "piotr@gmail.com");

        when(userService.update(1L, userDtoToUpdate)).thenReturn(updatedUserDto);

        String result = mockMvc.perform(patch("/users/{id}", 1L)
                        .content(objectMapper.writeValueAsString(userDtoToUpdate))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(updatedUserDto), result);
    }

    @SneakyThrows
    @Test
    void getUserById_shouldCallRelevantMethodAndReturn200() {
        long userId = 1L;
        mockMvc.perform(get("/users/{id}", userId))
                .andExpect(status().isOk());

        verify(userService).getById(userId);
    }

    @SneakyThrows
    @Test
    void deleteById_shouldCallRelevantMethodAndReturn200() {
        long userId = 1L;
        mockMvc.perform(delete("/users/{id}", userId))
                .andExpect(status().isOk());

        verify(userService, times(1)).deleteById(userId);
    }

}