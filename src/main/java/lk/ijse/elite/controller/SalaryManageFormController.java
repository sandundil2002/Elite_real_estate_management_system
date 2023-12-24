package lk.ijse.elite.controller;

import com.jfoenix.controls.JFXComboBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import lk.ijse.elite.db.DbConnection;
import lk.ijse.elite.dto.EmployeeDto;
import lk.ijse.elite.dto.SalaryDto;
import lk.ijse.elite.model.EmployeeModel;
import lk.ijse.elite.model.SalaryModel;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class SalaryManageFormController {
    public TextField txtSalaryid;
    public TextField txtEmployeeid;
    
    public TextField txtName;
    public JFXComboBox cmdPosition;
    public DatePicker dtpDate;
    public TextField txtAmount;

    public void initialize() throws SQLException {
        autoGenarateId();
        loadAllEmployees();
        dtpDate.setValue(java.time.LocalDate.now());

        cmdPosition.getSelectionModel().selectedItemProperty().addListener((observableValue, s, t1) -> {
            try {
                EmployeeDto employeeDto = EmployeeModel.searchEmployeePosition(t1.toString());
                txtEmployeeid.setText(employeeDto.getEmpid());
                txtName.setText(employeeDto.getName());
                txtAmount.setText(employeeDto.getBasicSalary());
            } catch (SQLException e) {
                new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
            } catch (ClassNotFoundException e) {
                new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
            }
        });
        
    }

    public void btnPayOnAction(ActionEvent actionEvent) {
        String salaryid = txtSalaryid.getText();
        String employeeid = txtEmployeeid.getText();
        String name = txtName.getText();
        String amount = txtAmount.getText();
        String position = cmdPosition.getValue().toString();
        String date = dtpDate.getValue().toString();

        var dto = new SalaryDto(salaryid, employeeid, date, amount);
        var model = new SalaryModel();

        try {
            boolean isSaved = model.saveSalary(dto);
            if (isSaved) {
                new Alert(Alert.AlertType.CONFIRMATION, "Salary Paid Succesfull").show();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ClassNotFoundException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }
    }

    private void loadAllEmployees() {
        ObservableList<String> obList = FXCollections.observableArrayList();

        try {
            List<EmployeeDto> empList = EmployeeModel.loadAllEmployees();

            for (EmployeeDto employeeDto  : empList) {
                obList.add(employeeDto.getPosition());
            }
            cmdPosition.setItems(obList);
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        } catch (ClassNotFoundException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }
    }

    public void btnClearOnAction() {
        txtSalaryid.clear();
        txtEmployeeid.clear();
        txtName.clear();
        txtAmount.clear();
        cmdPosition.getSelectionModel().clearSelection();
        dtpDate.setValue(java.time.LocalDate.now());
    }

    private void autoGenarateId() throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();
        ResultSet rst = connection.prepareStatement("SELECT salary_id FROM salary ORDER BY salary_id DESC LIMIT 1").executeQuery();
        if (rst.next()) {
            String oldId = rst.getString(1);
            String substring = oldId.substring(1, 4);
            int intId = Integer.parseInt(substring);
            intId++;
            if (intId < 10) {
                txtSalaryid.setText("S00" + intId);
            } else if (intId < 100) {
                txtSalaryid.setText("S0" + intId);
            } else {
                txtSalaryid.setText("S" + intId);
            }
        } else {
            txtSalaryid.setText("S001");
        }
    }
}
