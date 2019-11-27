package lk.ijse.dep.pos.dao;

import lk.ijse.dep.pos.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerDAO extends JpaRepository<Customer, String> {

    String getLastCustomerId() throws Exception;

}
