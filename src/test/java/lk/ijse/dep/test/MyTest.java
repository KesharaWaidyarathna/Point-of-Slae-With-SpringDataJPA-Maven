package lk.ijse.dep.test;

import lk.ijse.dep.pos.AppConfig;
import static org.junit.Assert.*;

import lk.ijse.dep.pos.dao.CustomerDAO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@ContextConfiguration(classes = {AppConfig.class})
@RunWith(SpringRunner.class)
public class MyTest {

    @Autowired
    private CustomerDAO customerDAO;

    @Test
    public void myTest() throws Exception {

        String lastCustomerId = customerDAO.getLastCustomerId();
        assertNotNull(lastCustomerId);

    }

}
