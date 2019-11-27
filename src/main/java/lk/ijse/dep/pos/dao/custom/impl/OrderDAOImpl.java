package lk.ijse.dep.pos.dao.custom.impl;

import lk.ijse.dep.pos.dao.CrudDAOImpl;
import lk.ijse.dep.pos.dao.custom.OrderDAO;
import lk.ijse.dep.pos.entity.Order;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import javax.persistence.Query;
import java.sql.ResultSet;

@Repository
public class OrderDAOImpl extends CrudDAOImpl<Order,Integer> implements OrderDAO {

    @Override
    public int getLastOrderId() throws Exception {
        Query nativeQuery = entityManager.createNativeQuery("SELECT id FROM `Order` ORDER BY id DESC LIMIT 1");
        return nativeQuery.getResultList().size()>0? (int) nativeQuery.getSingleResult():0;

    }

    @Override
    public boolean existsByCustomerId(String customerId) throws Exception {
        return entityManager.createNativeQuery("SELECT * FROM `Order` WHERE customerId=?1").setParameter(1,customerId)!=null;

    }
}
