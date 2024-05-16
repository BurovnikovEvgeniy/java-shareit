package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoOut;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.item.dto.ItemDtoService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.item.ItemMapper.itemMapper;
import static ru.practicum.shareit.utils.Constants.USER_HEADER;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemDtoService itemDtoService;


    private final User user = User.builder()
            .id(1L)
            .name("username")
            .email("my@email.com")
            .build();

    private final Item item = Item.builder()
            .id(1L)
            .name("item name")
            .description("description")
            .owner(user)
            .build();


    @Test
    void createItemWhenItemIsValid() throws Exception {
        Long userId = 0L;
        ItemDto itemDtoToCreate = ItemDto.builder()
                .description("some item description")
                .name("some item name")
                .available(true)
                .build();

        when(itemDtoService.add(userId, itemDtoToCreate)).thenReturn(itemMapper.toItemDtoOut(itemMapper.toItem(itemDtoToCreate)));

        String result = mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_HEADER, userId)
                        .content(objectMapper.writeValueAsString(itemDtoToCreate)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        ItemDto resultItemDto = objectMapper.readValue(result, ItemDto.class);
        assertEquals(itemDtoToCreate.getDescription(), resultItemDto.getDescription());
        assertEquals(itemDtoToCreate.getName(), resultItemDto.getName());
        assertEquals(itemDtoToCreate.getAvailable(), resultItemDto.getAvailable());
    }

    @Test
    void createItemWhenItemIsNotValidShouldReturnBadRequest() throws Exception {
        Long userId = 0L;
        ItemDto itemDtoToCreate = ItemDto.builder()
                .description(" ")
                .name(" ")
                .available(null)
                .build();

        when(itemDtoService.add(userId, itemDtoToCreate)).thenReturn(itemMapper.toItemDtoOut(itemMapper.toItem(itemDtoToCreate)));

        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_HEADER, userId)
                        .content(objectMapper.writeValueAsString(itemDtoToCreate)))
                .andExpect(status().isBadRequest());

        verify(itemDtoService, never()).add(userId, itemDtoToCreate);
    }

    @Test
    void updateWhenItemIsValidShouldReturnStatusIsOk() throws Exception {
        Long itemId = 0L;
        Long userId = 0L;
        ItemDto itemDtoToCreate = ItemDto.builder()
                .description("some item description")
                .name("some item name")
                .available(true)
                .build();

        when(itemDtoService.update(userId, itemId, itemDtoToCreate)).thenReturn(itemMapper.toItemDtoOut(itemMapper.toItem(itemDtoToCreate)));

        String result = mockMvc.perform(patch("/items/{itemId}", itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_HEADER, userId)
                        .content(objectMapper.writeValueAsString(itemDtoToCreate)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        ItemDto resultItemDto = objectMapper.readValue(result, ItemDto.class);
        assertEquals(itemDtoToCreate.getDescription(), resultItemDto.getDescription());
        assertEquals(itemDtoToCreate.getName(), resultItemDto.getName());
        assertEquals(itemDtoToCreate.getAvailable(), resultItemDto.getAvailable());
    }

    @Test
    void getShouldReturnStatusOk() throws Exception {
        Long itemId = 0L;
        Long userId = 0L;
        ItemDtoOut itemDtoToCreate = ItemDtoOut.builder()
                .id(itemId)
                .description("")
                .name("")
                .available(true)
                .build();

        when(itemDtoService.findById(userId, itemId)).thenReturn(itemDtoToCreate);

        String result = mockMvc.perform(MockMvcRequestBuilders.get("/items/{itemId}", itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_HEADER, userId)
                        .content(objectMapper.writeValueAsString(itemDtoToCreate)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemDtoToCreate), result);
    }

    @Test
    void getAllShouldReturnStatusOk() throws Exception {
        Long userId = 0L;
        Integer from = 0;
        Integer size = 10;
        List<ItemDtoOut> itemsDtoToExpect = List.of(ItemDtoOut.builder()
                .name("some item name")
                .description("some item description")
                .available(true)
                .build());

        when(itemDtoService.findAllByUserId(userId, from, size)).thenReturn(itemsDtoToExpect);

        String result = mockMvc.perform(MockMvcRequestBuilders.get("/items", from, size)
                        .header(USER_HEADER, userId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemsDtoToExpect), result);
    }

    @Test
    void searchItemsShouldReturnStatusOk() throws Exception {
        Long userId = 0L;
        Integer from = 0;
        Integer size = 10;
        String text = "find";
        List<ItemDtoOut> itemsDtoToExpect = List.of(ItemDtoOut.builder()
                .name("some item name")
                .description("some item description")
                .available(true)
                .build());

        when(itemDtoService.search(userId, text, from, size)).thenReturn(itemsDtoToExpect);

        String result = mockMvc.perform(MockMvcRequestBuilders.get("/items/search", from, size)
                        .header(USER_HEADER, userId)
                        .param("text", text))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemsDtoToExpect), result);
    }


    @Test
    void createCommentWhenCommentIsValidShouldReturnStatusIsOk() throws Exception {
        CommentDto comment = CommentDto.builder()
                .text("some comment")
                .build();
        CommentDtoOut commentDtoOut = CommentDtoOut.builder()
                .id(1L)
                .itemId(item.getId())
                .text(comment.getText())
                .build();

        when(itemDtoService.createComment(user.getId(), comment, item.getId())).thenReturn(commentDtoOut);

        String result = mockMvc.perform(post("/items/{itemId}/comment", item.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_HEADER, user.getId())
                        .content(objectMapper.writeValueAsString(comment)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertNotNull(result);
    }

    @Test
    void findAllItemsShouldReturnBadRequest() throws Exception {
        Integer from = -1;
        Integer size = 10;

        mockMvc.perform(get("/items")
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_HEADER, user.getId()))
                .andExpect(status().isBadRequest());

        verify(itemDtoService, never()).findAllByUserId(user.getId(), from, size);
    }

    @Test
    void searchShouldReturnBadRequest() throws Exception {
        Integer from = -1;
        Integer size = 10;
        String text = "item";

        mockMvc.perform(get("/items/search")
                        .param("text", text)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_HEADER, user.getId()))
                .andExpect(status().isBadRequest());

        verify(itemDtoService, never()).search(user.getId(), text, from, size);
    }
}
