package lk.ijse.dep.pos.dao;

import lk.ijse.dep.pos.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ItemDAO extends JpaRepository<Item, String> {

    @Query(value = "SELECT code FROM Item ORDER BY code DESC LIMIT 1",nativeQuery = true)
    String getLastItemCode() throws Exception;
}
