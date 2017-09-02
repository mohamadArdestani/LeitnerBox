/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package leitnerbox;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import ir.huri.jcal.JalaliCalendar;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.dom4j.DocumentException;
import  static leitnerbox.LeitnerBox.prop;
import  static leitnerbox.LeitnerBox.config;
import  static leitnerbox.LeitnerBox.resource;


/**
 * FXML Controller class
 *
 * @author ma
 */
public class MainController implements Initializable {
    @FXML
    private ScrollPane background;
    @FXML
    ListView<String> mainList;
    @FXML
    private Label mainLabel;
    @FXML
    private ImageView mainNew;
    @FXML
    private ImageView mainImport;
    @FXML
    private ImageView mainConvert;
    @FXML
    private ImageView mainSetting;
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
       background.setStyle(config.getProperty("color"));
       if("Jalali".equals(config.getProperty("date"))){ 
       JalaliCalendar jDate=new JalaliCalendar();
       mainLabel.setText(jDate.toString());
       }else{
           Date date=new Date();
           SimpleDateFormat formatter=new SimpleDateFormat("yyyy/MM/dd");
           mainLabel.setText(formatter.format(date));
       }
        showLesson();
    }
    //fill listview of lessons
    
     void showLesson(){
         File[] lesson=new File("src/leitnerbox/lessons").listFiles();
        for (File file : lesson) {
            if (file.isDirectory()) {
               File[] list=file.listFiles();
                for (File file1 : list) {
                    if (!file1.isDirectory()) {  
                        Platform.runLater(()->{
                           mainList.getItems().add(file1.getName()); 
                        });         
                    }    
                }
            }
        }
    }
    //select lesson for reading
    @FXML
    private void chooseLesson() throws IOException, DocumentException, ParseException{
       String lesson=mainList.getFocusModel().getFocusedItem();
       if(lesson!=null){
        FXMLLoader fxmlLoader=new FXMLLoader();
        fxmlLoader.setResources(resource);
        if(config.getProperty("lang").equals("fa")){
        fxmlLoader.setLocation(getClass().getResource("view/lesson-fa.fxml"));
        }else{
          fxmlLoader.setLocation(getClass().getResource("view/lesson.fxml"));   
        }
        Parent root = fxmlLoader.load();  
                    LessonController controller=fxmlLoader.getController();
                    controller.loadFile(lesson);
                     Stage stage=(Stage)mainLabel.getScene().getWindow();
                        Scene scene=new Scene(root,stage.getWidth(),stage.getHeight());
                     stage.setScene(scene);
                     stage.getIcons().add(new Image(getClass().getResource("image/logo.png").toString()));
                     stage.setTitle(lesson);
                     stage.show();   
       }             
    }
    //go to new lesson window
    @FXML
    private void newLesson() throws IOException{
        FXMLLoader fxmlloader=new FXMLLoader(getClass().getResource("view/new.fxml"));
        fxmlloader.setResources(resource);
        Parent root=fxmlloader.load();  
       
                     Stage stage=(Stage)mainLabel.getScene().getWindow();
                     Scene scene=new Scene(root,stage.getWidth(),stage.getHeight());
                     stage.setScene(scene);
                     stage.getIcons().add(new Image(getClass().getResource("image/logo.png").toString()));
                     stage.setTitle(prop.getProperty("main.new"));
                     stage.show();
    }
    //go to import window
    @FXML
    private void importLesson() throws IOException{
        Parent root=FXMLLoader.load(getClass().getResource("view/import.fxml"),resource);         
                     Scene scene=new Scene(root);
                     Stage stage=new Stage();
                     stage.setScene(scene);
                     stage.getIcons().add(new Image(getClass().getResource("image/logo.png").toString()));
                     stage.setTitle(prop.getProperty("main.import"));
                     stage.initModality(Modality.WINDOW_MODAL);
                     stage.initOwner((Stage)mainLabel.getScene().getWindow());
                     stage.setUserData(mainImport.getScene().getWindow());
                     stage.show();
    }
    //go to convert window
     @FXML
    private void convertLesson() throws IOException{
        Parent root=FXMLLoader.load(getClass().getResource("view/convert.fxml"),resource);         
                     Scene scene=new Scene(root);
                     Stage stage=new Stage();
                     stage.setScene(scene);
                     stage.getIcons().add(new Image(getClass().getResource("image/logo.png").toString()));
                     stage.setTitle(prop.getProperty("main.convert"));
                     stage.initModality(Modality.WINDOW_MODAL);
                     stage.initOwner((Stage)mainLabel.getScene().getWindow());
                     stage.show();
    }
    //go to setting window
     @FXML
    private void setting() throws IOException{
        Stage mainStage=(Stage)mainLabel.getScene().getWindow();
        Parent root=FXMLLoader.load(getClass().getResource("view/setting.fxml"),resource);         
                     Scene scene=new Scene(root);
                     Stage stage=new Stage();
                     stage.setScene(scene);
                     stage.setUserData(mainStage);
                     stage.getIcons().add(new Image(getClass().getResource("image/logo.png").toString()));
                     stage.setTitle(prop.getProperty("main.setting"));
                     stage.initModality(Modality.WINDOW_MODAL);
                     stage.initOwner((Stage)mainLabel.getScene().getWindow());
                     stage.showAndWait();
    }
    //go to about window
     @FXML
    private void about() throws IOException{
        Stage mainStage=(Stage)mainLabel.getScene().getWindow();
        Parent root=FXMLLoader.load(getClass().getResource("view/about.fxml"),resource);         
                     Scene scene=new Scene(root);
                     Stage stage=new Stage();
                     stage.setScene(scene);
                     stage.setUserData(mainStage);
                     stage.getIcons().add(new Image(getClass().getResource("image/logo.png").toString()));
                     stage.setTitle(prop.getProperty("about"));
                     stage.initModality(Modality.WINDOW_MODAL);
                     stage.initOwner((Stage)mainLabel.getScene().getWindow());
                     stage.showAndWait();
    }
}