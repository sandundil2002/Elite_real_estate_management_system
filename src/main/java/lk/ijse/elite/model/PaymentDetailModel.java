package lk.ijse.elite.model;

import lk.ijse.elite.dto.PaymentdetailDto;
import lk.ijse.elite.utill.SQLUtill;
import java.sql.SQLException;

public class PaymentDetailModel {
    public static boolean savePaymentDetail(PaymentdetailDto dto) throws SQLException, ClassNotFoundException {
        return SQLUtill.sql("INSERT INTO payment_details VALUES (?,?,?)",dto.getProperty_id(),dto.getPayment_id(),dto.getMethod());
    }
}
