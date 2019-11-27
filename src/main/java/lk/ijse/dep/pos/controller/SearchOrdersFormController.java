package lk.ijse.dep.pos.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lk.ijse.dep.pos.AppInitializer;
import lk.ijse.dep.pos.business.custom.OrderBO;
import lk.ijse.dep.pos.dto.OrderDTO2;
import lk.ijse.dep.pos.util.OrderTM;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
import lk.ijse.dep.pos.db.DB;
import lk.ijse.dep.pos.db.Order;
import lk.ijse.dep.pos.db.OrderDetail;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import lk.ijse.dep.pos.util.CustomerTM;
import lk.ijse.dep.pos.util.OrderTM;

import java.io.IOException;
import java.net.URL;
*/
public class SearchOrdersFormController {

    public TextField txtSearch;
    public TableView<OrderTM> tblOrders;
    public AnchorPane root;
    OrderBO orderBO = AppInitializer.ctx.getBean(OrderBO.class);

    public void initialize() throws Exception {
        // Let's map
        tblOrders.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("orderId"));
        tblOrders.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("orderDate"));
        tblOrders.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("customerId"));
        tblOrders.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("customerName"));
        tblOrders.getColumns().get(4).setCellValueFactory(new PropertyValueFactory<>("total"));

        ObservableList<OrderTM> olOrders = tblOrders.getItems();

        loadTable();

       /* for (Order order : DB.orders) {
            String orderId = order.getOrderId();
            String orderDate = order.getOrderDate().toString();
            String customerId = order.getCustomerId();
            String customerName = null;

            for (CustomerTM customer : DB.customers) {
                if (customer.getId().equals(customerId)) {
                    customerName = customer.getName();
                    break;
                }
            }

            double total = 0.0;
            for (OrderDetail orderDetail : order.getOrderDetails()) {
                double orderDetailTotal = orderDetail.getQty() * orderDetail.getUnitPrice();
                total += orderDetailTotal;
            }

            OrderTM orderTM = new OrderTM(orderId, orderDate, customerId, customerName, total);
            olOrders.add(orderTM);
        }

        ObservableList<OrderTM> olAllOrders =
                FXCollections.observableArrayList(olOrders);*/

        txtSearch.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {

                try {
                    loadTable();
                } catch (Exception e) {
                    new Alert(Alert.AlertType.INFORMATION,"Something went wrong.. contact DEPPO").show();
                    Logger.getLogger("lk.ijse.dep.pos").log(Level.SEVERE,null,e);
                }

            }
        });

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

   /* public void tblOrders_OnMouseClicked(MouseEvent mouseEvent) throws IOException {
        if (mouseEvent.getClickCount() == 2) {

            URL resource = this.getClass().getResource("/lk.ijse.dep.pos.view/PlaceOrderForm.fxml");
            FXMLLoader fxmlLoader = new FXMLLoader(resource);
            Parent root = fxmlLoader.load();
            Scene placeOrderScene = new Scene(root);
            Stage secondaryStage = new Stage();
            secondaryStage.setScene(placeOrderScene);
            secondaryStage.centerOnScreen();
            secondaryStage.setTitle("View Order");
            secondaryStage.setResizable(false);

            PlaceOrderFormController ctrl = fxmlLoader.getController();
            OrderTM selectedOrder = tblOrders.getSelectionModel().getSelectedItem();
            ctrl.initializeForSearchOrderForm(selectedOrder.getOrderId());

            secondaryStage.show();
        }
    }*/

    /**
     *
     * @throws Exception
     */


    public void loadTable() throws Exception {


        List<OrderDTO2> orderInfo = orderBO.getOrderInfo('%'+txtSearch.getText()+'%');

        ObservableList<OrderTM> items = tblOrders.getItems();
        items.clear();

        for (OrderDTO2 order : orderInfo) {
            items.add(new OrderTM(order.getOrderId()+"",order.getOrderDate()+"",order.getCustomerId(),order.getCustomerName(),order.getTotal()));

        }
        tblOrders.setItems(items);

    }
}
