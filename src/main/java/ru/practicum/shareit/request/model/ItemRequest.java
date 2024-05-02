package ru.practicum.shareit.request.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * TODO Sprint add-item-requests.
 */
@Getter
@Setter
public class ItemRequest {
    private long id;
    private String description;
    private long requestor;
    private LocalDate created;
}
