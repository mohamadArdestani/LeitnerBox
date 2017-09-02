/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package leitnerbox;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import static leitnerbox.LeitnerBox.prop;
import static leitnerbox.LessonController.ob;
import static leitnerbox.LeitnerBox.config;

/**
 * FXML Controller class
 *
 * @author ma
 */
public class FindController implements Initializable {
    @FXML
    private VBox background;
    @FXML
    private Label frontsideLable;
    @FXML
    private TextField findFrontside;
    @FXML
    private Label backsideLable;
    @FXML
    private TextField findBackside;
    @FXML
    private Button findButton;
    @FXML
    private TableView<Card> findTable;
     @FXML
    private TableColumn<Card, String> frontSideColumn;

    @FXML
    private TableColumn<Card, String> backSideColumn;

    @FXML
    private TableColumn<Card, String> chapterColumn;


    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        background.setStyle(config.getProperty("color"));
        frontSideColumn.setCellValueFactory(new PropertyValueFactory<>("frontside"));
        backSideColumn.setCellValueFactory(new PropertyValueFactory<>("backside"));
        chapterColumn.setCellValueFactory(new PropertyValueFactory<>("chapter"));
    }    
     Predicate<Card> filterByFindText(String frontside,String backside){
         if(frontside.isEmpty() & backside.isEmpty()){
            
             return (Card p)->p.getFrontside().length()==0;
         }
         else if(backside.isEmpty()){
            
             return (Card p)->p.getFrontside().contains(frontside);
         }
         else if(frontside.isEmpty()){
             
           return (Card p)->p.getBackside().contains(backside);  
         } 
       else { 
             
            return (Card p)->p.getFrontside().contains(frontside) || p.getBackside().contains(backside);
         }
     } 
    @FXML
    private void find(){
        FilteredList<Card> filteredData = new FilteredList(ob,filterByFindText(findFrontside.getText(),findBackside.getText()));
        if(filteredData.isEmpty()){
            Label label=new Label();
            label.setText(prop.getProperty("find.result.label"));
            findTable.setPlaceholder(label);
        }
         SortedList<Card> sortedData = new SortedList<>(filteredData);
         sortedData.comparatorProperty().bind(findTable.comparatorProperty());
         findTable.setItems(sortedData);
        
    }
}
