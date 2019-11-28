package lk.ijse.dep.pos.entity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@NamedQuery(query = "SELECT c FROM Customer c",name="Customer.nameQuery1")
@NamedNativeQuery(query = "SELECT * FROM Customer WHERE customerId=?#{[0]}",name="Customer.namedNativeQuery",resultClass = Customer.class)
@NamedNativeQuery(query = "SELECT * FROM Customer WHERE customerId=?#{[0]} AND name=?#{[1]}",name="Customer.anotherQuery",resultClass = Customer.class)
public class Customer implements SuperEntity{

    @Id
    private String customerId;
    private String name;
    private String address;
    @OneToMany(mappedBy = "customer",cascade = {CascadeType.PERSIST,CascadeType.DETACH,CascadeType.MERGE,CascadeType.REMOVE,CascadeType.REFRESH})
    private
    List<Order>orders=new ArrayList<>();
//    private Gender gender;

    public Customer() {
    }

    public Customer(String customerId, String name, String address) {
        this.customerId = customerId;
        this.name = name;
        this.address = address;
    }

//    public Customer(String customerId, String name, String address, Gender gender) {
//        this.customerId = customerId;
//        this.name = name;
//        this.address = address;
//        this.setGender(gender);
//    }

    //    public Customer(String customerId, String name, String address) {
//        this.customerId = customerId;
//        this.name = name;
//        this.address = address;
//    }



    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

//    public Gender getGender() {
//        return gender;
//    }
//
//    public void setGender(Gender gender) {
//        this.gender = gender;
//    }

    @Override
    public String toString() {
        return "Customer{" +
                "customerId='" + customerId + '\'' +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
//                ", gender=" + gender +
                '}';
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void addOrders(Order orders) {
        orders.setCustomer(this);
        this.orders.add(orders);
    }

    public void removeOrder(Order order){
        if(order.getCustomer()!=this){
            throw new RuntimeException("invalid customer");
        }

        order.setCustomer(null);
        this.orders.remove(order);
    }
}
