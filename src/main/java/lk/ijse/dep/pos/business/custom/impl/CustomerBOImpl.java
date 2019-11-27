package lk.ijse.dep.pos.business.custom.impl;

import lk.ijse.dep.pos.business.custom.CustomerBO;
import lk.ijse.dep.pos.business.exception.AlreadyExistsInOrderException;
import lk.ijse.dep.pos.dao.custom.CustomerDAO;
import lk.ijse.dep.pos.dao.custom.OrderDAO;
import lk.ijse.dep.pos.dto.CustomerDTO;
import lk.ijse.dep.pos.entity.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
@Transactional
public class CustomerBOImpl implements CustomerBO {

    @Autowired
    private CustomerDAO customerDAO ;
    @Autowired
    private OrderDAO orderDAO ;

    @Override
    public void saveCustomer(CustomerDTO customer) throws Exception {

        customerDAO.save(new Customer(customer.getId(), customer.getName(), customer.getAddress()));

    }

    @Override
    public void updateCustomer(CustomerDTO customer) throws Exception {
         customerDAO.update(new Customer(customer.getId(), customer.getName(), customer.getAddress()));

    }

    @Override
    public void deleteCustomer(String customerId) throws Exception {
        if (orderDAO.existsByCustomerId(customerId)){
            throw new AlreadyExistsInOrderException("Customer already exists in an order, hence unable to delete");
        }
         customerDAO.delete(customerId);

    }

    @Override
    @Transactional(readOnly = true)

    public List<CustomerDTO> findAllCustomers() throws Exception {
        List<Customer> alCustomers = customerDAO.findAll();
        List<CustomerDTO> dtos = new ArrayList<>();
        for (Customer customer : alCustomers) {
            dtos.add(new CustomerDTO(customer.getCustomerId(), customer.getName(), customer.getAddress()));
        }
        return dtos;

    }

    @Override
    @Transactional(readOnly = true)
    public String getLastCustomerId() throws Exception {
        String lastCustomerId = customerDAO.getLastCustomerId();
        return lastCustomerId;
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerDTO findCustomer(String customerId) throws Exception {
        Customer customer = customerDAO.find(customerId);
        return new CustomerDTO(customer.getCustomerId(),
                customer.getName(), customer.getAddress());
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getAllCustomerIDs() throws Exception {
        List<Customer> customers = customerDAO.findAll();
        List<String> ids = new ArrayList<>();
        for (Customer customer : customers) {
            ids.add(customer.getCustomerId());
        }
        return ids;
    }
}
