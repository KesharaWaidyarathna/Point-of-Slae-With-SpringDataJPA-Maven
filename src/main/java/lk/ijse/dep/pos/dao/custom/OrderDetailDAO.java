package lk.ijse.dep.pos.dao.custom;

import lk.ijse.dep.pos.dao.CrudDAO;
import lk.ijse.dep.pos.entity.OrderDetail;
import lk.ijse.dep.pos.entity.OrderDetailPK;

public interface OrderDetailDAO extends CrudDAO<OrderDetail, OrderDetailPK> {

    boolean existsByItemCode(String itemCode) throws Exception;

}
