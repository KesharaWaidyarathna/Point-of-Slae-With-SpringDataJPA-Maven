package lk.ijse.dep.pos.entity;

import javax.persistence.*;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "`order`")
public class Order implements SuperEntity{

    @Id
    private int id;
    private Date date;
    @JoinColumn(name = "customer_Id",referencedColumnName = "customerId",nullable = false)
    @ManyToOne(cascade = {CascadeType.PERSIST,CascadeType.DETACH,CascadeType.MERGE,CascadeType.REMOVE,CascadeType.REFRESH})
    private
    Customer customer;
    @OneToMany(mappedBy = "order",cascade = CascadeType.PERSIST)
    private
    List<OrderDetail>orderDetails=new ArrayList<>();

    public Order(int id, Date date, Customer customer) {
        this.setId(id);
        this.setDate(date);
        this.setCustomer(customer);
    }

    public Order() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", date=" + date +
                ", customer=" + customer +
                '}';
    }

    public List<OrderDetail> getOrderDetails() {
        return orderDetails;
    }

    public void addOrderDetails(OrderDetail orderDetails) {
        getOrderDetails().add(orderDetails);
    }
}
