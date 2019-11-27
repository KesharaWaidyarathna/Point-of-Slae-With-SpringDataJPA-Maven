package lk.ijse.dep.pos.dao;

import lk.ijse.dep.pos.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemDAO extends JpaRepository<Item, String> {

    String getLastItemCode() throws Exception;
}
