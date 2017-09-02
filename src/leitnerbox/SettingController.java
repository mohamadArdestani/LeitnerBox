/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package leitnerbox;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.Optional;
import java.util.Properties;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import static leitnerbox.LeitnerBox.config;
import static leitnerbox.LeitnerBox.prop;

/**
 * FXML Controller class
 *
 * @author ma
 */
public class SettingController implements Initializable {
    @FXML
    private BorderPane background;
    @FXML
    private Label settingLabel;
    @FXML
    private Label settingLanguageLabel;
    @FXML
    private ChoiceBox<String> langList;
    @FXML
    private Label settingColorLabel;
    @FXML
    private ColorPicker chooseColor;
    @FXML
    private Label settingDateLabel;
    @FXML
    private ChoiceBox<String> dateList;
    @FXML
    private Button settingOkButton;
    @FXML
    private Button settingCancelButton;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
     background.setStyle(config.getProperty("color"));  
     String[] lang={"Persian","English"};   
     String[] date={"Jalali","Gregorian"};
     langList.getItems().addAll(lang);
     langList.getSelectionModel().selectFirst();
     dateList.getItems().addAll(date);
     dateList.getSelectionModel().selectFirst();
     chooseColor.setValue(Color.rgb(95, 126, 196));
    }    
    //set new setting
    @FXML
    private void setSetting() throws IOException, Exception {
         String lang=langList.getValue();
        String date=dateList.getValue();
         double red = (chooseColor.getValue().getRed()*255);
         int x=(int)red;
         double green =chooseColor.getValue().getGreen()*255;
         int y=(int)green;
         double blue =chooseColor.getValue().getBlue()*255;
         int z=(int)blue;
         String opacity =String.valueOf(chooseColor.getValue().getOpacity());
        String hex1 = Integer.toHexString(x);
        String hex2 = Integer.toHexString(y);
        String hex3 = Integer.toHexString(z);
        String hexColor="-fx-background:"+"#"+hex1+hex2+hex3+";";
        Properties prop = new Properties();
	OutputStream output = null;
        try{
            output = new FileOutputStream(new File("src/leitnerbox/config.properties"));
           if(lang.equals("Persian")){
               prop.put("lang", "fa");
           } else{
               prop.put("lang", "en");
           }
               prop.put("date",date);
               prop.put("color",hexColor);  
          prop.store(output,null);
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            output.close();
        }
        reLaunch();
        cancel();
    }
    
    //close setting window without change setting
    @FXML
    private void cancel() {
         Stage stage=(Stage)settingCancelButton.getScene().getWindow();
        stage.close();
    }
    private void reLaunch() throws IOException, Exception{
                  Stage stage=(Stage)settingCancelButton.getScene().getWindow();
                  Stage mainStage = (Stage) stage.getUserData();
                  LeitnerBox lb=new LeitnerBox();
                  lb.start(mainStage);
    }
}
