package lk.ijse.dep.pos.dao;

import lk.ijse.dep.pos.entity.CustomEntity;

import java.util.List;

public interface QueryDAO {

    CustomEntity getOrderInfo(int orderId) throws Exception;

    CustomEntity getOrderInfo2(int orderId) throws Exception;

    /**
     *
     * @param query customerId, customerName, orderId, orderDate
     * @return
     * @throws Exception
     */
    List<CustomEntity> getOrdersInfo(String query) throws Exception;

}
