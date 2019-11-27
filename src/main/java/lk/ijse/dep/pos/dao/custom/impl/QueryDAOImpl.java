package lk.ijse.dep.pos.dao.custom.impl;

import lk.ijse.dep.pos.dao.custom.QueryDAO;
import lk.ijse.dep.pos.entity.CustomEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Repository
public class QueryDAOImpl  implements QueryDAO {

    @PersistenceContext
private EntityManager entityManager;

    @Override
    public CustomEntity getOrderInfo(int orderId) throws Exception {
        return null;
      /*  ResultSet rst = CrudUtil.execute("SELECT C.customerId, C.name, O.date  FROM Customer C INNER JOIN `Order` O ON C.customerId=O.customerId WHERE O.id=?", orderId);
        if (rst.next()){
            return new CustomEntity(orderId,
                    rst.getString(1),
                    rst.getString(2),
                    rst.getDate(3));
        }else{
            return null;
        }*/
    }

    @Override
    public CustomEntity getOrderInfo2(int orderId) throws Exception {
        return null;
   /*     ResultSet rst = CrudUtil.execute("SELECT O.id, C.customerId, C.name, O.date, SUM(OD.qty * OD.unitPrice) AS Total  FROM Customer C INNER JOIN `Order` O ON C.customerId=O.customerId\" +\n" +
                "                \" INNER JOIN OrderDetail OD on O.id = OD.orderId WHERE O.id=? GROUP BY orderId", orderId);
        if (rst.next()){
            return new CustomEntity(rst.getInt(1),
                    rst.getString(2),
                    rst.getString(3),
                    rst.getDate(4),
                    rst.getDouble(5));
        }else{
            return null;
        }*/
    }

    @Override
    public List<CustomEntity> getOrdersInfo(String query) throws Exception {

        List<Object[]> resultList = entityManager.createNativeQuery("SELECT O.id, C.customerId, C.name, O.date, SUM(OD.qty * OD.unitPrice) AS Total  FROM Customer C INNER JOIN `Order` O ON C.customerId=O.customer_Id INNER JOIN OrderDetail OD on O.id = OD.orderId WHERE O.id LIKE ?1 OR C.customerId LIKE ?1 OR C.name LIKE ?1 OR O.date LIKE ?1 GROUP BY O.id").setParameter(1, query).getResultList();

        ArrayList<CustomEntity> list = new ArrayList<>();

        for (Object[] clo : resultList) {
            list.add(new CustomEntity((int)clo[0],(String) clo[1],(String) clo[2],(Date) clo[3],(Double) clo[4]));
        }

        return list;
    }
}
