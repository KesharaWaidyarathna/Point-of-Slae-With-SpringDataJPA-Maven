package lk.ijse.dep.pos.dto;

import java.sql.Date;

public class OrderDTO2 {

    private int orderId;
    private Date orderDate;
    private String customerId;
    private String customerName;
    private double total;

    public OrderDTO2() {
    }

    public OrderDTO2(int orderId, Date orderDate, String customerId, String customerName, double total) {
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.customerId = customerId;
        this.customerName = customerName;
        this.total = total;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return "OrderDTO2{" +
                "orderId=" + orderId +
                ", orderDate=" + orderDate +
                ", customerId='" + customerId + '\'' +
                ", customerName='" + customerName + '\'' +
                ", total=" + total +
                '}';
    }
}
