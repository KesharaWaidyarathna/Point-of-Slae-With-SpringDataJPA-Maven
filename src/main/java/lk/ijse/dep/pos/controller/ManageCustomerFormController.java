/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lk.ijse.dep.pos.controller;

import lk.ijse.dep.pos.AppInitializer;
import lk.ijse.dep.pos.business.custom.CustomerBO;
import lk.ijse.dep.pos.business.exception.AlreadyExistsInOrderException;
import lk.ijse.dep.pos.dto.CustomerDTO;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;
import lk.ijse.dep.pos.util.CustomerTM;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * FXML Controller class
 *
 * @author ranjith-suranga
 */
public class ManageCustomerFormController implements Initializable {

    @FXML
    private Button btnSave;
    @FXML
    private Button btnDelete;
    @FXML
    private AnchorPane root;
    @FXML
    private TextField txtCustomerId;
    @FXML
    private TextField txtCustomerName;
    @FXML
    private TextField txtCustomerAddress;

    @FXML
    private TableView<CustomerTM> tblCustomers;

    private CustomerBO customerBO = AppInitializer.ctx.getBean(CustomerBO.class);

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tblCustomers.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("id"));
        tblCustomers.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("name"));
        tblCustomers.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("address"));

        txtCustomerId.setDisable(true);
        txtCustomerName.setDisable(true);
        txtCustomerAddress.setDisable(true);
        btnDelete.setDisable(true);
        btnSave.setDisable(true);

        try {
            List<CustomerDTO> allCustomers = customerBO.findAllCustomers();
            ObservableList<CustomerTM> customers = tblCustomers.getItems();
            for (CustomerDTO c : allCustomers) {
                customers.add(new CustomerTM(c.getId(), c.getName(), c.getAddress()));
            }
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR,"Something went wrong, please contact DEPPO").show();
            Logger.getLogger("lk.ijse.dep.pos.controller").log(Level.SEVERE, null,e);
        }

        tblCustomers.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<CustomerTM>() {
            @Override
            public void changed(ObservableValue<? extends CustomerTM> observable, CustomerTM oldValue, CustomerTM newValue) {
                CustomerTM selectedItem = tblCustomers.getSelectionModel().getSelectedItem();

                if (selectedItem == null) {
                    btnSave.setText("Save");
                    btnDelete.setDisable(true);
                    return;
                }

                btnSave.setText("Update");
                btnSave.setDisable(false);
                btnDelete.setDisable(false);
                txtCustomerName.setDisable(false);
                txtCustomerAddress.setDisable(false);
                txtCustomerId.setText(selectedItem.getId());
                txtCustomerName.setText(selectedItem.getName());
                txtCustomerAddress.setText(selectedItem.getAddress());
            }
        });
    }

    public void btnReport_OnAction(ActionEvent actionEvent) throws JRException {
        JasperDesign jasperDesign = JRXmlLoader.
                load(this.getClass().
                        getResourceAsStream("/report/bean-report.jrxml"));

        JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
        Map<String, Object> params = new HashMap<>();
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport,
                params, new JRBeanCollectionDataSource(tblCustomers.getItems()));

        JasperViewer.viewReport(jasperPrint);
    }

    @FXML
    private void navigateToHome(MouseEvent event) throws IOException {
        URL resource = this.getClass().getResource("/view/MainForm.fxml");
        Parent root = FXMLLoader.load(resource);
        Scene scene = new Scene(root);
        Stage primaryStage = (Stage) (this.root.getScene().getWindow());
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
    }

    @FXML
    private void btnSave_OnAction(ActionEvent event) {
        if (!txtCustomerName.getText().matches("[A-Za-z][A-Za-z. ]+")) {
            new Alert(Alert.AlertType.ERROR, "Invalid Name").show();
            return;
        }
        if (btnSave.getText().equals("Save")) {
            ObservableList<CustomerTM> customers = tblCustomers.getItems();
            CustomerDTO newCustomer = new CustomerDTO(
                    txtCustomerId.getText(),
                    txtCustomerName.getText(),
                    txtCustomerAddress.getText()
            );
            try {
                customerBO.saveCustomer(newCustomer);
                customers.add(new CustomerTM(newCustomer.getId(), newCustomer.getName(), newCustomer.getAddress()));
                btnAddNew_OnAction(event);
            } catch (Exception e) {
                new Alert(Alert.AlertType.ERROR,"Something went wrong, please contact DEPPO").show();
                Logger.getLogger("lk.ijse.dep.pos.controller").log(Level.SEVERE, null,e);
            }
        } else {
            CustomerTM selectedCustomer = tblCustomers.getSelectionModel().getSelectedItem();
            try {
                customerBO.updateCustomer(new CustomerDTO(selectedCustomer.getId(),
                        txtCustomerName.getText(),
                        txtCustomerAddress.getText()));
                selectedCustomer.setName(txtCustomerName.getText());
                selectedCustomer.setAddress(txtCustomerAddress.getText());
                tblCustomers.refresh();
                btnAddNew_OnAction(event);
            } catch (Exception e) {
                new Alert(Alert.AlertType.ERROR,"Something went wrong, please contact DEPPO").show();
                Logger.getLogger("lk.ijse.dep.pos.controller").log(Level.SEVERE, null,e);
            }
        }
    }

    @FXML
    private void btnDelete_OnAction(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Are you sure whether you want to delete this customer?",
                ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> buttonType = alert.showAndWait();
        if (buttonType.get() == ButtonType.YES) {
            CustomerTM selectedItem = tblCustomers.getSelectionModel().getSelectedItem();
            try {
                customerBO.deleteCustomer(selectedItem.getId());
                tblCustomers.getItems().remove(selectedItem);
            }catch (AlreadyExistsInOrderException e){
                new Alert(Alert.AlertType.INFORMATION,e.getMessage()).show();
            } catch (Exception e) {
                new Alert(Alert.AlertType.ERROR,"Something went wrong, please contact DEPPO").show();
                Logger.getLogger("lk.ijse.dep.pos.controller").log(Level.SEVERE, null,e);
            }
        }
    }

    @FXML
    private void btnAddNew_OnAction(ActionEvent actionEvent) {
        txtCustomerId.clear();
        txtCustomerName.clear();
        txtCustomerAddress.clear();
        tblCustomers.getSelectionModel().clearSelection();
        txtCustomerName.setDisable(false);
        txtCustomerAddress.setDisable(false);
        txtCustomerName.requestFocus();
        btnSave.setDisable(false);

        // Generate a new id
        int maxId = 0;

        try {
            String lastCustomerId = customerBO.getLastCustomerId();

            if (lastCustomerId == null) {
                maxId = 0;
            } else {
                maxId = Integer.parseInt(lastCustomerId.replace("C", ""));
            }

            maxId = maxId + 1;
            String id = "";
            if (maxId < 10) {
                id = "C00" + maxId;
            } else if (maxId < 100) {
                id = "C0" + maxId;
            } else {
                id = "C" + maxId;
            }
            txtCustomerId.setText(id);

        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR,"Something went wrong, please contact DEPPO").show();
            Logger.getLogger("lk.ijse.dep.pos.controller").log(Level.SEVERE, null,e);
        }

    }

    //============================ DB Related Operations ==========================


}

