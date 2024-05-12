package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.exception.entity.EntityNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
import ru.practicum.shareit.request.dto.ItemRequestDtoServiceImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoService;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static ru.practicum.shareit.request.ItemRequestMapper.itemRequestMapper;

@ExtendWith(MockitoExtension.class)
class RequestServiceImplTest {

    @Mock
    private ItemRequestRepository requestRepository;

    @Mock
    private UserDtoService userService;

    @InjectMocks
    private ItemRequestDtoServiceImpl requestService;

    private final User user = User.builder()
            .id(1L)
            .name("username")
            .email("email@email.com")
            .build();

    private final UserDto userDto = UserDto.builder()
            .id(1L)
            .name("username")
            .email("email@email.com")
            .build();

    private final Item item = Item.builder()
            .id(1L)
            .name("item name")
            .description("description")
            .available(true)
            .owner(user)
            .build();

    private final ItemRequest request = ItemRequest.builder()
            .id(1L)
            .description("request description")
            .items(List.of(item))
            .build();

    @Test
    void addNewRequest() {
        ItemRequestDto requestDto = itemRequestMapper.toRequestDto(request);
        ItemRequestDtoOut expectedRequestDto = itemRequestMapper.toRequestDtoOut(request);
        when(userService.findById(user.getId())).thenReturn(userDto);
        when(requestRepository.save(any(ItemRequest.class))).thenReturn(request);

        ItemRequestDtoOut actualRequestDto = requestService.add(user.getId(), requestDto);

        assertEquals(expectedRequestDto, actualRequestDto);
    }

    @Test
    void getUserRequests() {
        List<ItemRequestDtoOut> expectedRequestsDto = List.of(itemRequestMapper.toRequestDtoOut(request));
        when(userService.findById(user.getId())).thenReturn(userDto);
        when(requestRepository.findAllByRequesterId(userDto.getId())).thenReturn(List.of(request));

        List<ItemRequestDtoOut> actualRequestsDto = requestService.getUserRequests(userDto.getId());

        assertEquals(expectedRequestsDto, actualRequestsDto);
    }

    @Test
    void getAllRequests() {
        List<ItemRequestDtoOut> expectedRequestsDto = List.of(itemRequestMapper.toRequestDtoOut(request));
        when(userService.findById(user.getId())).thenReturn(userDto);
        when(requestRepository.findAllByRequester_IdNotOrderByCreatedDesc(anyLong(), any(PageRequest.class)))
                .thenReturn(List.of(request));

        List<ItemRequestDtoOut> actualRequestsDto = requestService.getAllRequests(userDto.getId(), PageRequest.of(0 / 10, 10));

        assertEquals(expectedRequestsDto, actualRequestsDto);
    }

    @Test
    void getRequestById() {
        ItemRequestDtoOut expectedRequestDto = itemRequestMapper.toRequestDtoOut(request);
        when(userService.findById(user.getId())).thenReturn(userDto);
        when(requestRepository.findById(request.getId())).thenReturn(Optional.of(request));

        ItemRequestDtoOut actualRequestDto = requestService.getRequestById(userDto.getId(), request.getId());

        assertEquals(expectedRequestDto, actualRequestDto);
    }

    @Test
    void getRequestByIdWhenRequestIdIsNotValidShouldThrowObjectNotFoundException() {
        when(userService.findById(user.getId())).thenReturn(userDto);
        when(requestRepository.findById(request.getId())).thenReturn(Optional.empty());

        EntityNotFoundException requestNotFoundException = assertThrows(EntityNotFoundException.class,
                () -> requestService.getRequestById(userDto.getId(), request.getId()));

        assertEquals(requestNotFoundException.getMessage(),"Запрос с id=" + request.getId() + " не был найден");
    }
}
