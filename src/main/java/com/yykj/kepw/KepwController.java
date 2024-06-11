package com.yykj.kepw;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import com.yykj.kepw.support.encryption.Encr;
import com.yykj.kepw.support.encryption.PasswordEncoderException;
import com.yykj.kepw.utils.XmlParseException;
import javafx.scene.control.TextField;

public class KepwController {

    @FXML
    private TextField decryptPassword1Text;

    @FXML
    private TextField encryptPassword1Text;

    @FXML
    private TextField decryptPassword2Text;

    @FXML
    private TextField encryptPassword2Text;

    @FXML
    protected void onDecryptPWDButtonClick() throws PasswordEncoderException, XmlParseException {
        String val = decryptPassword1Text.getText();

        if(val == null || val.trim().length() == 0)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("请输入已加密过的字符串");
            alert.show();
            return;
        }
        else
        {
            if (!val.startsWith("Encrypted ")) {
                val = "Encrypted " + val;
            }
            val = val.trim();
            String mw = Encr.getInstance().decryptPassword(val);
            encryptPassword1Text.setText(mw);
        }
    }

    @FXML
    protected void onEncryptPWDButtonClick() throws PasswordEncoderException, XmlParseException {
        String val = encryptPassword2Text.getText();
        if(val == null || val.trim().length() == 0)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("请输入代加密的字符串");
            alert.show();
            return;
        }
        String mw = Encr.getInstance().encryptPassword(val);

        decryptPassword2Text.setText(mw);
    }
}