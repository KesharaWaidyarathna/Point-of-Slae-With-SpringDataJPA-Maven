/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lk.ijse.dep.pos.controller;

import lk.ijse.dep.pos.AppInitializer;
import lk.ijse.dep.pos.business.custom.ItemBO;
import lk.ijse.dep.pos.business.exception.AlreadyExistsInOrderException;
import com.jfoenix.controls.JFXTextField;
import lk.ijse.dep.pos.dto.ItemDTO;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lk.ijse.dep.pos.util.ItemTM;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * FXML Controller class
 *
 * @author ranjith-suranga
 */
public class ManageItemFormController implements Initializable {

    public JFXTextField txtCode;
    public JFXTextField txtDescription;
    public JFXTextField txtQtyOnHand;
    public TableView<ItemTM> tblItems;
    public JFXTextField txtUnitPrice;
    @FXML
    private Button btnSave;
    @FXML
    private Button btnDelete;
    @FXML
    private AnchorPane root;

    private ItemBO itemBO = AppInitializer.ctx.getBean(ItemBO.class);

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tblItems.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("code"));
        tblItems.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("description"));
        tblItems.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("qtyOnHand"));
        tblItems.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("unitPrice"));

        txtCode.setDisable(true);
        txtDescription.setDisable(true);
        txtQtyOnHand.setDisable(true);
        txtUnitPrice.setDisable(true);
        btnDelete.setDisable(true);
        btnSave.setDisable(true);

        try {
            List<ItemDTO> allItems = itemBO.findAllItems();
            ObservableList<ItemTM> items = tblItems.getItems();

            for (ItemDTO item : allItems) {
                items.add(new ItemTM(item.getCode(), item.getDescription(),
                        item.getQtyOnHand(), item.getUnitPrice()));
            }
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR,"Something went wrong, please contact DEPPO").show();
            Logger.getLogger("lk.ijse.dep.pos.controller").log(Level.SEVERE, null,e);
        }

        tblItems.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ItemTM>() {
            @Override
            public void changed(ObservableValue<? extends ItemTM> observable, ItemTM oldValue, ItemTM newValue) {
                ItemTM selectedItem = tblItems.getSelectionModel().getSelectedItem();

                if (selectedItem == null) {
                    btnSave.setText("Save");
                    btnDelete.setDisable(true);
                    return;
                }

                btnSave.setText("Update");
                btnSave.setDisable(false);
                btnDelete.setDisable(false);
                txtDescription.setDisable(false);
                txtQtyOnHand.setDisable(false);
                txtUnitPrice.setDisable(false);
                txtCode.setText(selectedItem.getCode());
                txtDescription.setText(selectedItem.getDescription());
                txtQtyOnHand.setText(selectedItem.getQtyOnHand() + "");
                txtUnitPrice.setText(selectedItem.getUnitPrice() + "");

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

    @FXML
    private void btnSave_OnAction(ActionEvent event) {
        if (btnSave.getText().equals("Save")) {
            ObservableList<ItemTM> items = tblItems.getItems();
            ItemTM newItem = new ItemTM(
                    txtCode.getText(),
                    txtDescription.getText(),
                    Integer.parseInt(txtQtyOnHand.getText()),
                    Double.parseDouble(txtUnitPrice.getText()));
            ItemDTO item = new ItemDTO(txtCode.getText(),
                    txtDescription.getText(),
                    Integer.parseInt(txtQtyOnHand.getText()),
                    Double.parseDouble(txtUnitPrice.getText()));
            try {
                itemBO.saveItem(item);
                items.add(newItem);
                btnAddNew_OnAction(event);
            } catch (Exception e) {
                new Alert(Alert.AlertType.ERROR,"Something went wrong, please contact DEPPO").show();
                Logger.getLogger("lk.ijse.dep.pos.controller").log(Level.SEVERE, null,e);
            }
        } else {
            ItemTM selectedItem = tblItems.getSelectionModel().getSelectedItem();
            try {
                itemBO.updateItem(new ItemDTO(selectedItem.getCode(),
                        txtDescription.getText(),
                        Integer.parseInt(txtQtyOnHand.getText()),
                        Double.parseDouble(txtQtyOnHand.getText())));
                selectedItem.setDescription(txtDescription.getText());
                selectedItem.setQtyOnHand(Integer.parseInt(txtQtyOnHand.getText()));
                selectedItem.setUnitPrice(Double.parseDouble(txtQtyOnHand.getText()));
                tblItems.refresh();
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
                "Are you sure whether you want to delete this item?",
                ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> buttonType = alert.showAndWait();
        if (buttonType.get() == ButtonType.YES) {
            ItemTM selectedItem = tblItems.getSelectionModel().getSelectedItem();
            try {
                itemBO.deleteItem(selectedItem.getCode());
                tblItems.getItems().remove(selectedItem);
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
        txtCode.clear();
        txtDescription.clear();
        txtQtyOnHand.clear();
        txtUnitPrice.clear();
        tblItems.getSelectionModel().clearSelection();
        txtDescription.setDisable(false);
        txtQtyOnHand.setDisable(false);
        txtUnitPrice.setDisable(false);
        txtDescription.requestFocus();
        btnSave.setDisable(false);

        // Generate a new id
        int maxCode = 0;
        try {
            String lastItemCode = itemBO.getLastItemCode();
            if (lastItemCode == null) {
                maxCode = 0;
            } else {
                maxCode = Integer.parseInt(lastItemCode.replace("I", ""));
            }

            maxCode = maxCode + 1;
            String code = "";
            if (maxCode < 10) {
                code = "I00" + maxCode;
            } else if (maxCode < 100) {
                code = "I0" + maxCode;
            } else {
                code = "I" + maxCode;
            }
            txtCode.setText(code);
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR,"Something went wrong, please contact DEPPO").show();
            Logger.getLogger("lk.ijse.dep.pos.controller").log(Level.SEVERE, null,e);
        }

    }

//============================ DB Related Operations ==========================


}
