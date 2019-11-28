package lk.ijse.dep.pos.dao;

import lk.ijse.dep.pos.entity.Customer;
import lk.ijse.dep.pos.entity.OrderDetail;
import lk.ijse.dep.pos.entity.OrderDetailPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OrderDetailDAO extends JpaRepository<OrderDetail, OrderDetailPK> {

    @Query(value = "SELECT IF(EXISTS(SELECT * FROM OrderDetail WHERE itemCode=?1),'TRUE','FALSE')",nativeQuery = true)
    boolean existsByItemCode(String itemCode) throws Exception;



}
