package ru.practicum.shareit.item.storage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query("select i " +
            "from Item as i " +
            "where i.available = true and " +
            "(lower(i.name) like lower(concat('%', :text, '%') ) or " +
            "lower(i.description) like lower(concat('%', :text, '%') ))")
    List<Item> search(@Param("text") String text, Pageable pageable);

    List<Item> findAllByOwnerId(Long ownerId, Pageable pageable);

    Page<Item> findAllByOwnerIdOrderByIdAsc(Long ownerId, Pageable pageable);
}
