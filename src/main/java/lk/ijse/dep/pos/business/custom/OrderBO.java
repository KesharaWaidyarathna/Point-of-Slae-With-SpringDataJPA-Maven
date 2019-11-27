package lk.ijse.dep.pos.business.custom;

import lk.ijse.dep.pos.business.SuperBO;
import lk.ijse.dep.pos.dto.OrderDTO;
import lk.ijse.dep.pos.dto.OrderDTO2;

import java.util.List;

public interface OrderBO extends SuperBO {

    int getLastOrderId() throws Exception;

    void placeOrder(OrderDTO orderDTO) throws Exception;

    List<OrderDTO2> getOrderInfo(String query) throws Exception;

}
