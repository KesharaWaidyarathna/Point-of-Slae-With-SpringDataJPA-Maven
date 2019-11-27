package lk.ijse.dep.pos.controller;

import lk.ijse.dep.pos.AppInitializer;
import lk.ijse.dep.pos.business.custom.CustomerBO;
import lk.ijse.dep.pos.business.custom.ItemBO;
import lk.ijse.dep.pos.business.custom.OrderBO;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import lk.ijse.dep.pos.dto.CustomerDTO;
import lk.ijse.dep.pos.dto.ItemDTO;
import lk.ijse.dep.pos.dto.OrderDTO;
import lk.ijse.dep.pos.dto.OrderDetailDTO;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;
import lk.ijse.dep.pos.util.OrderDetailTM;
import org.hibernate.Session;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PlaceOrderFormController {
    public JFXTextField txtDescription;
    public JFXTextField txtCustomerName;
    public JFXTextField txtQtyOnHand;
    public JFXButton btnSave;
    public TableView<OrderDetailTM> tblOrderDetails;
    public JFXTextField txtUnitPrice;
    public JFXComboBox<String> cmbCustomerId;
    public JFXComboBox<String> cmbItemCode;
    public JFXTextField txtQty;
    public Label lblTotal;
    public JFXButton btnPlaceOrder;
    public AnchorPane root;
    public Label lblId;
    public Label lblDate;
    public JFXButton btnAddNewOrder;

    private boolean readOnly = false;

    private List<ItemDTO> tempItems = new ArrayList<>();

    private CustomerBO customerBO = AppInitializer.ctx.getBean(CustomerBO.class);
    private ItemBO itemBO = AppInitializer.ctx.getBean(ItemBO.class);
    private OrderBO orderBO = AppInitializer.ctx.getBean(OrderBO.class);

    public void initialize() {

        // Let's map columns with table model
        tblOrderDetails.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("code"));
        tblOrderDetails.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("description"));
         tblOrderDetails.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("qty"));
        tblOrderDetails.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        tblOrderDetails.getColumns().get(4).setCellValueFactory(new PropertyValueFactory<>("total"));
        tblOrderDetails.getColumns().get(5).setCellValueFactory(new PropertyValueFactory<>("btnDelete"));

        try {

            List<String> ids = customerBO.getAllCustomerIDs();
            cmbCustomerId.setItems(FXCollections.observableArrayList(ids));

            List<String> codes = itemBO.getAllItemCodes();
            cmbItemCode.setItems(FXCollections.observableArrayList(codes));

            tempItems = itemBO.findAllItems();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR,"Something went wrong, please contact DEPPO").show();
            Logger.getLogger("lk.ijse.dep.pos.controller").log(Level.SEVERE, null,e);
        }

        // When customer id is selected
        cmbCustomerId.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                String selectedCustomerID = cmbCustomerId.getSelectionModel().getSelectedItem();
                enablePlaceOrderButton();
                txtCustomerName.setText("");
                if (selectedCustomerID == null) {
                    return;
                }
                try {
                    CustomerDTO customer = customerBO.findCustomer(selectedCustomerID);
                    txtCustomerName.setText(customer.getName());
                } catch (Exception e) {
                    new Alert(Alert.AlertType.ERROR,"Something went wrong, please contact DEPPO").show();
                    Logger.getLogger("lk.ijse.dep.pos.controller").log(Level.SEVERE, null,e);
                }
            }
        });

        // When item code is selected
        cmbItemCode.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                String selectedItemCode = cmbItemCode.getSelectionModel().getSelectedItem();

                if (selectedItemCode == null) {
                    txtQtyOnHand.clear();
                    txtDescription.clear();
                    txtUnitPrice.clear();
                    txtQty.clear();
                    txtQty.setEditable(false);
                    btnSave.setDisable(true);
                    return;
                }

                txtQty.setEditable(true);
                if (!readOnly) {
                    btnSave.setDisable(false);
                }
                try {
                    ItemDTO item = itemBO.findItem(selectedItemCode);
                    txtDescription.setText(item.getDescription());
                    txtUnitPrice.setText(item.getUnitPrice() + "");
                    txtQtyOnHand.setText(item.getQtyOnHand() + "");
                } catch (Exception e) {
                    new Alert(Alert.AlertType.ERROR,"Something went wrong, please contact DEPPO").show();
                    Logger.getLogger("lk.ijse.dep.pos.controller").log(Level.SEVERE, null,e);
                }
            }
        });

        // When a table row is selected
        tblOrderDetails.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<OrderDetailTM>() {
            @Override
            public void changed(ObservableValue<? extends OrderDetailTM> observable, OrderDetailTM oldValue, OrderDetailTM newValue) {

                OrderDetailTM selectedOrderDetail = tblOrderDetails.getSelectionModel().getSelectedItem();
                if (selectedOrderDetail == null) {
                    if (!readOnly) {
                        btnSave.setText("Add");
                    }
                    return;
                }
                for (ItemDTO tempItem : tempItems) {
                    if (tempItem.getCode().equals(selectedOrderDetail.getCode())) {
                        try {
                            ItemDTO item = itemBO.findItem(selectedOrderDetail.getCode());
                            tempItem.setQtyOnHand(item.getQtyOnHand());
                        } catch (Exception e) {
                            new Alert(Alert.AlertType.ERROR,"Something went wrong, please contact DEPPO").show();
                            Logger.getLogger("lk.ijse.dep.pos.controller").log(Level.SEVERE, null,e);
                        }
                    }
                }
                cmbItemCode.getSelectionModel().select(selectedOrderDetail.getCode());
                txtQty.setText(selectedOrderDetail.getQty() + "");
                // Don't think about this now...!
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        txtQty.requestFocus();
                        txtQty.selectAll();
                    }
                });
                if (!readOnly) {
                    btnSave.setText("Update");
                }
            }
        });

        reset();
    }

    private void reset() {
        // Initialize controls to their default states
        lblDate.setText(LocalDate.now() + "");

        btnPlaceOrder.setDisable(true);
        btnSave.setDisable(true);
        txtCustomerName.setEditable(false);
        txtCustomerName.clear();
        txtDescription.setEditable(false);
        txtUnitPrice.setEditable(false);
        txtQtyOnHand.setEditable(false);
        txtQty.setEditable(false);
        cmbCustomerId.getSelectionModel().clearSelection();
        cmbItemCode.getSelectionModel().clearSelection();
        tblOrderDetails.getItems().clear();
        lblTotal.setText("Total : 0.00");

        // Generate the new order id
        int maxOrderId = 0;
        try {
            maxOrderId = orderBO.getLastOrderId();
            maxOrderId++;
            if (maxOrderId < 10) {
                lblId.setText("OD00" + maxOrderId);
            } else if (maxOrderId < 100) {
                lblId.setText("OD0" + maxOrderId);
            } else {
                lblId.setText("OD" + maxOrderId);
            }
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR,"Something went wrong, please contact DEPPO").show();
            Logger.getLogger("lk.ijse.dep.pos.controller").log(Level.SEVERE, null,e);
        }
    }

    public void btnAddNew_OnAction(ActionEvent actionEvent) {
        reset();
    }

    public void btnAdd_OnAction(ActionEvent actionEvent) {
        int qty = Integer.parseInt(txtQty.getText());
        int qtyOnHand = Integer.parseInt(txtQtyOnHand.getText());
        double unitPrice = Double.parseDouble(txtUnitPrice.getText());

        // Let's validate the qty.
        if (qty <= 0 || qty > qtyOnHand) {
            new Alert(Alert.AlertType.ERROR, "Invalid Qty", ButtonType.OK).show();
            txtQty.requestFocus();
            txtQty.selectAll();
            return;
        }

        String selectedItemCode = cmbItemCode.getSelectionModel().getSelectedItem();
        ObservableList<OrderDetailTM> details = tblOrderDetails.getItems();

        boolean isExists = false;
        for (OrderDetailTM detail : tblOrderDetails.getItems()) {
            if (detail.getCode().equals(selectedItemCode)) {
                isExists = true;

                if (btnSave.getText().equals("Update")) {
                    detail.setQty(qty);
                } else {
                    detail.setQty(detail.getQty() + qty);
                }
                detail.setTotal(detail.getQty() * detail.getUnitPrice());
                tblOrderDetails.refresh();
                break;
            }
        }

        if (!isExists) {
            JFXButton btnDelete = new JFXButton("Delete");
            OrderDetailTM detailTM = new OrderDetailTM(cmbItemCode.getSelectionModel().getSelectedItem(),
                    txtDescription.getText(),
                    qty,
                    unitPrice,
                    qty * unitPrice,
                    btnDelete
            );
            btnDelete.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    for (ItemDTO tempItem : tempItems) {
                        if (tempItem.getCode().equals(detailTM.getCode())) {
                            // Let's restore the qty
                            int qtyOnHand = tempItem.getQtyOnHand() + detailTM.getQty();
                            tempItem.setQtyOnHand(qtyOnHand);
                            break;
                        }
                    }
                    tblOrderDetails.getItems().remove(detailTM);
                    calculateTotal();
                    enablePlaceOrderButton();
                    cmbItemCode.requestFocus();
                    cmbItemCode.getSelectionModel().clearSelection();
                    tblOrderDetails.getSelectionModel().clearSelection();
                }
            });
            details.add(detailTM);
        }

        updateQty(selectedItemCode, qty);
        // Calculate the grand total
        calculateTotal();
        enablePlaceOrderButton();
        cmbItemCode.requestFocus();
        cmbItemCode.getSelectionModel().clearSelection();
        tblOrderDetails.getSelectionModel().clearSelection();

    }

    private void updateQty(String selectedItemCode, int qty) {
        for (ItemDTO item : tempItems) {
            if (item.getCode().equals(selectedItemCode)) {
                item.setQtyOnHand(item.getQtyOnHand() - qty);
                break;
            }
        }
    }

    private void calculateTotal() {
        ObservableList<OrderDetailTM> details = tblOrderDetails.getItems();

        double total = 0;
        for (OrderDetailTM detail : details) {
            total += detail.getTotal();
        }

        // Let's format the total
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(2);
        nf.setMinimumFractionDigits(2);
        nf.setGroupingUsed(false);

        lblTotal.setText("Total : " + nf.format(total));
    }

    public void btnPlaceOrder_OnAction(ActionEvent actionEvent) {
        int orderId = Integer.parseInt(lblId.getText().replace("OD", ""));
        List<OrderDetailDTO> orderDetails = new ArrayList<>();
        for (OrderDetailTM item : tblOrderDetails.getItems()) {
            orderDetails.add(new OrderDetailDTO(item.getCode(), item.getQty(), item.getUnitPrice()));
        }

        OrderDTO order = new OrderDTO(orderId, null, cmbCustomerId.getSelectionModel().getSelectedItem(), orderDetails);
        try {
            orderBO.placeOrder(order);
      /*      JasperReport jasperReport = (JasperReport) JRLoader.loadObject(this.getClass().getResourceAsStream("/report/order-report.jasper"));
            Map<String, Object> params = new HashMap<>();
            params.put("orderId", orderId + "");
            EntityManager entityManager = AppInitializer.ctx.getBean(EntityManagerFactory.class).createEntityManager();
            entityManager.unwrap(Session.class).doWork(connection -> {

                JasperPrint jasperPrint = null;
                try {
                    jasperPrint = JasperFillManager.fillReport(jasperReport, params, connection);
                } catch (JRException e) {
                    e.printStackTrace();
                }
                JasperViewer.viewReport(jasperPrint, false);

            });*/

        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR,"Something went wrong, please contact DEPPO").show();
            Logger.getLogger("lk.ijse.dep.pos.controller").log(Level.SEVERE, null,e);
        }

        reset();
    }

    @FXML
    private void navigateToHome(MouseEvent event) throws IOException {
        if (readOnly) {
            ((Stage) (txtQty.getScene().getWindow())).close();
            return;
        }
        URL resource = this.getClass().getResource("/view/MainForm.fxml");
        Parent root = FXMLLoader.load(resource);
        Scene scene = new Scene(root);
        Stage primaryStage = (Stage) (this.root.getScene().getWindow());
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
    }

/*    public void txtQty_OnKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER){
            if (!btnSave.isDisable()){
                btnAdd_OnAction(null);
            }
        }
    }*/

    public void txtQty_OnAction(ActionEvent actionEvent) {
        if (!btnSave.isDisable()) {
            btnAdd_OnAction(actionEvent);
        }
    }

    private void enablePlaceOrderButton() {
        String selectedCustomer = cmbCustomerId.getSelectionModel().getSelectedItem();
        int size = tblOrderDetails.getItems().size();
        if (selectedCustomer == null || size == 0) {
            btnPlaceOrder.setDisable(true);
        } else {
            btnPlaceOrder.setDisable(false);
        }
    }

/*
    public void initializeForSearchOrderForm(String orderId) {

        // Let's disable a few
        btnPlaceOrder.setDisable(true);
        btnSave.setDisable(true);
        btnAddNewOrder.setDisable(true);
        cmbCustomerId.setDisable(true);
        cmbItemCode.setDisable(true);
        txtQty.setDisable(true);

        // Let's set read only flag to true
        readOnly = true;

        lblId.setText(orderId);

        // Let's find the order
        for (Order order : DB.orders) {
            if (order.getOrderId().equals(orderId)) {

                // Setting the order date
                lblDate.setText(order.getOrderDate() + "");
                // Setting the customer id, this will automatically set the customer name because we have already set a
                // listener
                cmbCustomerId.getSelectionModel().select(order.getCustomerId());

                ObservableList<OrderDetailTM> orderDetails = tblOrderDetails.getItems();

                for (OrderDetail od : order.getOrderDetails()) {

                    // Let's find the item description
                    String itemDescription = null;
                    for (ItemTM item : DB.items) {
                        if (item.getCode().equals(od.getItemCode())) {
                            itemDescription = item.getDescription();
                            break;
                        }
                    }
                    orderDetails.add(new OrderDetailTM(od.getItemCode(), itemDescription,
                            od.getQty(), od.getUnitPrice(),
                            od.getQty() * od.getUnitPrice(), null));
                }
                calculateTotal();
                tblOrderDetails.getSelectionModel().select(0);
                break;
            }
        }
*/
    //============================ DB Related Codes =================================


}
