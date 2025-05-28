package ru.practicum.shareit.server.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.server.exception.DuplicatedDataException;
import ru.practicum.shareit.server.exception.NotFoundException;
import ru.practicum.shareit.server.user.dto.UserDto;
import ru.practicum.shareit.server.user.dto.UserUpdateDto;

import java.util.HashSet;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Captor
    private ArgumentCaptor<User> userArgumentCaptor;

    @Test
    @DisplayName("Должен выбросить исключение и не сохранять юзера в БД, если email уже существует в БД")
    void create_shouldThrowExceptionAndNotSaveUserIntoDB_ifEmailAlreadyExist() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(new User()));
        assertThrows(DuplicatedDataException.class, () -> userService.create(new UserDto(
                null, "Ivan Ivanov", "ivan@gmail.com")));
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Должен сохранять корректный User в БД")
    void create_shouldSaveCorrectUserIntoDB() {
        //given
        UserDto userDto = new UserDto(null, "Ivan Ivanov", "ivan@gmail.com");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocationOnMock -> {
            User savedUser = invocationOnMock.getArgument(0);
            savedUser.setId(1L);
            return savedUser;
        });
        User userExpected = new User(1L, userDto.getName(), userDto.getEmail(), new HashSet<>());

        //when
        userService.create(userDto);

        //then
        verify(userRepository, times(1)).save(userArgumentCaptor.capture());
        User userActual = userArgumentCaptor.getValue();

        assertThat(userActual, notNullValue());
        assertThat(userExpected.getId(), equalTo(userActual.getId()));
        assertThat(userExpected.getName(), equalTo(userActual.getName()));
        assertThat(userExpected.getEmail(), equalTo(userActual.getEmail()));
        assertThat(userExpected.getItems().size(), equalTo(userActual.getItems().size()));

    }

    @Test
    @DisplayName("Должен вернуть корректный UserDto")
    void create_shouldReturnCorrectUserDto() {
        //given
        UserDto userDto = new UserDto(null, "Ivan Ivanov", "ivan@gmail.com");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocationOnMock -> {
            User savedUser = invocationOnMock.getArgument(0);
            savedUser.setId(10L);
            return savedUser;
        });
        UserDto userDtoExpected = new UserDto(10L, "Ivan Ivanov", "ivan@gmail.com");

        //when
        UserDto userDtoActual = userService.create(userDto);

        //then

        assertThat(userDtoExpected.getId(), equalTo(userDtoActual.getId()));
        assertThat(userDtoExpected.getName(), equalTo(userDtoActual.getName()));
        assertThat(userDtoExpected.getEmail(), equalTo(userDtoActual.getEmail()));
    }

    @Test
    @DisplayName("Должен выбросить исключение и не обновлять юзера в БД, если юзер не найден")
    void update_shouldThrowExceptionAndNotUpdateUserInDB_ifUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> userService.update(1L, new UserUpdateDto(
                "Ivan Ivanov", "ivan@gmail.com")));
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Должен выбросить исключение и не обновлять юзера в БД, если юзер не найден")
    void update_shouldThrowExceptionAndNotUpdateUserInDB_ifEmailAlreadyExist() {

        User oldUser = new User(
                1L, "Ivan Ivanov", "ivan@gmail.com", new HashSet<>());
        User existingUserWithSameEmail = new User(
                10L, "Stepan Stepanov", "stepan@gmail.com", new HashSet<>());

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(oldUser));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(existingUserWithSameEmail));

        UserUpdateDto updates = new UserUpdateDto(
                "Stepan Ivanov", "stepan@gmail.com");


        assertThrows(DuplicatedDataException.class, () -> userService.update(1L, updates));
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Должен вернуть неизмененный UserDto, если все поля в UserUpdateDto равны null")
    void update_shouldReturnUnchangedUserDto_ifAllFieldsInUserUpdateDtoEqualNull() {
        User oldUser = new User(
                1L, "Ivan Ivanov", "ivan@gmail.com", new HashSet<>());
        UserUpdateDto updates = new UserUpdateDto(null, null);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(oldUser));

        UserDto userDtoExpected = new UserDto(oldUser.getId(), oldUser.getName(), oldUser.getEmail());

        UserDto userDtoActual = userService.update(1L, updates);

        assertThat(userDtoExpected.getId(), equalTo(userDtoActual.getId()));
        assertThat(userDtoExpected.getName(), equalTo(userDtoActual.getName()));
        assertThat(userDtoExpected.getEmail(), equalTo(userDtoActual.getEmail()));
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Должен изменить только поле name, если только оно передается в UserUpdateDto")
    void update_shouldReturnUserDtoWithChangedName_ifOnlyNameProvidedInUserUpdateDto() {
        //given
        User oldUser = new User(
                1L, "Ivan Ivanov", "ivan@gmail.com", new HashSet<>());
        UserUpdateDto updates = new UserUpdateDto("Stepan Ivanov", null);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(oldUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        UserDto userDtoExpected = new UserDto(oldUser.getId(), updates.getName(), oldUser.getEmail());

        //when
        UserDto userDtoActual = userService.update(1L, updates);

        //then
        assertThat(userDtoExpected.getId(), equalTo(userDtoActual.getId()));
        assertThat(userDtoExpected.getName(), equalTo(userDtoActual.getName()));
        assertThat(userDtoExpected.getEmail(), equalTo(userDtoActual.getEmail()));
        verify(userRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Должен изменить только поле email, если только оно передается в UserUpdateDto")
    void update_shouldReturnUserDtoWithChangedEmail_ifOnlyEmailProvidedInUserUpdateDto() {
        //given
        User oldUser = new User(
                1L, "Ivan Ivanov", "ivan@gmail.com", new HashSet<>());
        UserUpdateDto updates = new UserUpdateDto(null, "stepan@gmail.com");

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(oldUser));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        UserDto userDtoExpected = new UserDto(oldUser.getId(), oldUser.getName(), updates.getEmail());

        //when
        UserDto userDtoActual = userService.update(1L, updates);

        //then
        assertThat(userDtoExpected.getId(), equalTo(userDtoActual.getId()));
        assertThat(userDtoExpected.getName(), equalTo(userDtoActual.getName()));
        assertThat(userDtoExpected.getEmail(), equalTo(userDtoActual.getEmail()));
        verify(userRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Должен изменить name и email, если оба поля передаются в UserUpdateDto")
    void update_shouldReturnUserDtoWithChangedNameAndEmail_ifBothProvidedInUserUpdateDto() {
        //given
        User oldUser = new User(
                1L, "Ivan Ivanov", "ivan@gmail.com", new HashSet<>());
        UserUpdateDto updates = new UserUpdateDto("Stepan Ivanov", "stepan@gmail.com");

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(oldUser));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        UserDto userDtoExpected = new UserDto(oldUser.getId(), updates.getName(), updates.getEmail());

        //when
        UserDto userDtoActual = userService.update(1L, updates);

        //then
        assertThat(userDtoExpected.getId(), equalTo(userDtoActual.getId()));
        assertThat(userDtoExpected.getName(), equalTo(userDtoActual.getName()));
        assertThat(userDtoExpected.getEmail(), equalTo(userDtoActual.getEmail()));
        verify(userRepository, times(1)).save(any());
    }


    @Test
    @DisplayName("Должен выбросить исключение, если юзер не найден")
    void getById_shouldThrowException_ifUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> userService.getById(1L));
    }

    @Test
    @DisplayName("Должен вернуть корректный UserDto")
    void getById_shouldReturnCorrectUserDto() {
        User user = new User(
                10L, "Ivan Ivanov", "ivan@gmail.com", new HashSet<>());
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        UserDto userDtoExpected = new UserDto(user.getId(), user.getName(), user.getEmail());

        UserDto userDtoActual = userService.getById(10L);

        assertThat(userDtoExpected.getId(), equalTo(userDtoActual.getId()));
        assertThat(userDtoExpected.getName(), equalTo(userDtoActual.getName()));
        assertThat(userDtoExpected.getEmail(), equalTo(userDtoActual.getEmail()));
    }


    @Test
    void deleteById_shouldCallMethodDeleteById() {
        userService.deleteById(1L);
        verify(userRepository, times(1)).deleteById(any());
    }
}