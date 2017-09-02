/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package leitnerbox;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import static leitnerbox.LeitnerBox.config;
import static leitnerbox.LeitnerBox.prop;
import static leitnerbox.LeitnerBox.resource;
import static leitnerbox.LessonController.ob;
import static leitnerbox.LessonController.chapter;
import static leitnerbox.LessonController.ownerStage;
import static leitnerbox.LessonController.selectedCards;

/**
 * FXML Controller class
 *
 * @author ma
 */
public class ReadingController implements Initializable {
    @FXML
    private BorderPane background;
    @FXML
    private Tab selectSectionLabel;
    @FXML
    private ComboBox<String> chapterList;
    @FXML
    private Label cardOrderLabel;
    @FXML
    private RadioButton allCardRadioButton;
    @FXML
    private ToggleGroup group;
    @FXML
    private RadioButton unreadCardRadioButton;
    @FXML
    private RadioButton expiredCardRadioButton;
    @FXML
    private RadioButton selectedCardRadioButton;
    @FXML
    private CheckBox restrictionCheckBox;
    @FXML
    private RadioButton timeRadioButton;
    @FXML
    private ToggleGroup restrictiongroup;
    @FXML
    private TextField timeValue;
    @FXML
    private RadioButton cardnumberRadioButton;
    @FXML
    private TextField cardnumberValue;
    @FXML
    private RadioButton frontSideRadioButton;
    @FXML
    private ToggleGroup cardgroup;
    @FXML
    private RadioButton backSideRadioButton;
    @FXML
    private ComboBox<String> planList;
    @FXML
    private Label delayLevel0;
    @FXML
    private Label days0Label;
    @FXML
    private TextField days0Value;
    @FXML
    private Label hour0Label;
    @FXML
    private TextField hour0Value;
    @FXML
    private Label delayLevel1;
    @FXML
    private Label days1Label;
    @FXML
    private TextField days1Value;
    @FXML
    private Label hour1Label;
    @FXML
    private TextField hour1Value;
    @FXML
    private Label delayLevel2;
    @FXML
    private Label days2Label;
    @FXML
    private TextField days2Value;
    @FXML
    private Label hour2Label;
    @FXML
    private TextField hour2Value;
    @FXML
    private Label delayLevel3;
    @FXML
    private Label days3Label;
    @FXML
    private TextField days3Value;
    @FXML
    private Label hour3Label;
    @FXML
    private TextField hour3Value;
    @FXML
    private Label delayLevel4;
    @FXML
    private Label days4Label;
    @FXML
    private TextField days4Value;
    @FXML
    private Label hour4Label;
    @FXML
    private TextField hour4Value;
    @FXML
    private Label delayLevel5;
    @FXML
    private Label days5Label;
    @FXML
    private TextField days5Value;
    @FXML
    private Label hour5Label;
    @FXML
    private TextField hour5Value;
    @FXML
    private Label delayLevel6;
    @FXML
    private Label days6Label;
    @FXML
    private TextField days6Value;
    @FXML
    private Label hour6Label;
    @FXML
    private TextField hour6Value;
    @FXML
    private Label delayLevel7;
    @FXML
    private Label days7Label;
    @FXML
    private TextField days7Value;
    @FXML
    private Label hour7Label;
    @FXML
    private TextField hour7Value;
    @FXML
    private Label delayLevel8;
    @FXML
    private Label days8Label;
    @FXML
    private TextField days8Value;
    @FXML
    private Label hour8Label;
    @FXML
    private TextField hour8Value;
    @FXML
    private Label delayLevel9;
    @FXML
    private Label days9Label;
    @FXML
    private TextField days9Value;
    @FXML
    private Label hour9Label;
    @FXML
    private TextField hour9Value;
    @FXML
    private Label planLabel;
    @FXML
    private Label pronounciationLabel;
    @FXML
    private CheckBox enableCheckBox;
    @FXML
    private ChoiceBox<String> engineList;
    @FXML
    private Label engineLabel;
    @FXML
    private Label voiceLabel;
    @FXML
    private ChoiceBox<String> voiceList;
    @FXML
    private Label cardOptionLabel;
    @FXML
    private RadioButton shuffleCardRadioButton;
    @FXML
    private ToggleGroup cardoptiongroup;
    @FXML
    private RadioButton regularCardRadioButton;
    @FXML
    private Button optionCancelButton;
    @FXML
    private Tab scheduleTab;
    @FXML
    private Button optionOkButton;        
    List<String> items;
    static ObservableMap<String,String> option = FXCollections.observableHashMap();
    static ObservableList<Card> filterCards=FXCollections.observableArrayList();
    

    /**
     * Initializes the controller class
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        background.setStyle(config.getProperty("color"));
        fillPlanList();
        fillSectionList(LessonController.chapters);
        fillVoiceLists();
       cardnumberValue.setTextFormatter(new TextFormatter<>(filter));
       timeValue.setTextFormatter(new TextFormatter<>(filter));
       days0Value.setTextFormatter(new TextFormatter<>(filter));
       days1Value.setTextFormatter(new TextFormatter<>(filter));
       days2Value.setTextFormatter(new TextFormatter<>(filter));
       days3Value.setTextFormatter(new TextFormatter<>(filter)); 
       days4Value.setTextFormatter(new TextFormatter<>(filter)); 
       days5Value.setTextFormatter(new TextFormatter<>(filter));
       days6Value.setTextFormatter(new TextFormatter<>(filter));
       days7Value.setTextFormatter(new TextFormatter<>(filter));
       days8Value.setTextFormatter(new TextFormatter<>(filter));
       days9Value.setTextFormatter(new TextFormatter<>(filter));
       hour0Value.setTextFormatter(new TextFormatter<>(filter));
       hour1Value.setTextFormatter(new TextFormatter<>(filter));
       hour2Value.setTextFormatter(new TextFormatter<>(filter));
       hour3Value.setTextFormatter(new TextFormatter<>(filter));
       hour4Value.setTextFormatter(new TextFormatter<>(filter));
       hour5Value.setTextFormatter(new TextFormatter<>(filter));
       hour6Value.setTextFormatter(new TextFormatter<>(filter));
       hour7Value.setTextFormatter(new TextFormatter<>(filter));
       hour8Value.setTextFormatter(new TextFormatter<>(filter));
       hour9Value.setTextFormatter(new TextFormatter<>(filter)); 
       
    }    
    void fillSectionList(String chapters){
        items=Arrays.asList(chapters.split(","));
        ArrayList filterItems=new ArrayList(items);
        filterItems.retainAll(chapter);
        Platform.runLater(()->{
         chapterList.getItems().addAll(filterItems);
    });
       
        if(LessonController.select==true){
            selectedCardRadioButton.setDisable(false);
            LessonController.select=false;
        } 
        if(LessonController.expiredNumber==0){
            expiredCardRadioButton.setDisable(true);
            allCardRadioButton.setDisable(true);
            unreadCardRadioButton.setSelected(true);
        }
        if(LessonController.unlearnedNumber==0){
            unreadCardRadioButton.setDisable(true);
             allCardRadioButton.setDisable(true);
             expiredCardRadioButton.setSelected(true);
        }
    }
    private void fillPlanList(){
        String[] items={prop.getProperty("reading.expotional"),prop.getProperty("reading.linear"),prop.getProperty("reading.weekly"),prop.getProperty("reading.monthly"),prop.getProperty("reading.custom")};
        planList.getItems().addAll(items);
        planList.getSelectionModel().selectFirst();
                days0Value.setText("1");
                days1Value.setText("2");
                days2Value.setText("4");
                days3Value.setText("8");
                days4Value.setText("16");
                days5Value.setText("32");
                days6Value.setText("64");
                days7Value.setText("128");
                days8Value.setText("256");
                days9Value.setText("512");
                hour0Value.setText("0");
                hour1Value.setText("0");
                hour2Value.setText("0");
                hour3Value.setText("0");
                hour4Value.setText("0");
                hour5Value.setText("0");
                hour6Value.setText("0");
                hour7Value.setText("0");
                hour8Value.setText("0");
                hour9Value.setText("0");
    }
    private void fillVoiceLists(){
        String[] items={"kevin16","kevin"};
        voiceList.getItems().addAll(items);
        voiceList.getSelectionModel().selectFirst();
        engineList.getItems().add("FreeTTS");
        engineList.getSelectionModel().selectFirst();
    }
    @FXML
    private void restriction(){
         if(restrictionCheckBox.isSelected()){
            timeRadioButton.setDisable(false);
            cardnumberRadioButton.setDisable(false);
        } else{
             timeRadioButton.setDisable(true);
            cardnumberRadioButton.setDisable(true);
            timeValue.setDisable(true);
            cardnumberValue.setDisable(true);
         }
    }
    @FXML
    private void timeRestriction(){
        if(timeRadioButton.isSelected()){
           timeValue.setDisable(false); 
           cardnumberValue.clear();
        }else{
            timeValue.setDisable(true);
        }
    }
    @FXML
    private void cardRestriction(){
        if (cardnumberRadioButton.isSelected()) {
            cardnumberValue.setDisable(false);
        } else {
            cardnumberValue.setDisable(true);
        }
    }
    @FXML
    private void planChange(){
        String item1=prop.getProperty("reading.expotional");
        String item2=prop.getProperty("reading.linear");
        String item3=prop.getProperty("reading.weekly");
        String item4=prop.getProperty("reading.monthly");
        String item5=prop.getProperty("reading.custom");
        String plan=planList.getSelectionModel().getSelectedItem();
        if(plan==item1){
           
                days0Value.setText("1");
                days1Value.setText("2");
                days2Value.setText("4");
                days3Value.setText("8");
                days4Value.setText("16");
                days5Value.setText("32");
                days6Value.setText("64");
                days7Value.setText("128");
                days8Value.setText("256");
                days9Value.setText("512");
                hour0Value.setText("0");
                hour1Value.setText("0");
                hour2Value.setText("0");
                hour3Value.setText("0");
                hour4Value.setText("0");
                hour5Value.setText("0");
                hour6Value.setText("0");
                hour7Value.setText("0");
                hour8Value.setText("0");
                hour9Value.setText("0");
        }else if(plan==item2){ 
                days0Value.setText("1");
                days1Value.setText("2");
                days2Value.setText("3");
                days3Value.setText("4");
                days4Value.setText("5");
                days5Value.setText("6");
                days6Value.setText("7");
                days7Value.setText("8");
                days8Value.setText("9");
                days9Value.setText("10");
                hour0Value.setText("0");
                hour1Value.setText("0");
                hour2Value.setText("0");
                hour3Value.setText("0");
                hour4Value.setText("0");
                hour5Value.setText("0");
                hour6Value.setText("0");
                hour7Value.setText("0");
                hour8Value.setText("0");
                hour9Value.setText("0");
        }else if(plan==item3){
                days0Value.setText("7");
                days1Value.setText("14");
                days2Value.setText("21");
                days3Value.setText("28");
                days4Value.setText("35");
                days5Value.setText("42");
                days6Value.setText("49");
                days7Value.setText("56");
                days8Value.setText("63");
                days9Value.setText("70");
                hour0Value.setText("0");
                hour1Value.setText("0");
                hour2Value.setText("0");
                hour3Value.setText("0");
                hour4Value.setText("0");
                hour5Value.setText("0");
                hour6Value.setText("0");
                hour7Value.setText("0");
                hour8Value.setText("0");
                hour9Value.setText("0");
        }else if(plan==item4){
                days0Value.setText("30");
                days1Value.setText("60");
                days2Value.setText("90");
                days3Value.setText("120");
                days4Value.setText("150");
                days5Value.setText("180");
                days6Value.setText("210");
                days7Value.setText("240");
                days8Value.setText("270");
                days9Value.setText("300");
                hour0Value.setText("0");
                hour1Value.setText("0");
                hour2Value.setText("0");
                hour3Value.setText("0");
                hour4Value.setText("0");
                hour5Value.setText("0");
                hour6Value.setText("0");
                hour7Value.setText("0");
                hour8Value.setText("0");
                hour9Value.setText("0");
        }else{
                days0Value.setText("");
                days1Value.setText("");
                days2Value.setText("");
                days3Value.setText("");
                days4Value.setText("");
                days5Value.setText("");
                days6Value.setText("");
                days7Value.setText("");
                days8Value.setText("");
                days9Value.setText("");
                hour0Value.setText("");
                hour1Value.setText("");
                hour2Value.setText("");
                hour3Value.setText("");
                hour4Value.setText("");
                hour5Value.setText("");
                hour6Value.setText("");
                hour7Value.setText("");
                hour8Value.setText("");
                hour9Value.setText("");
               
        }
                
    }
    @FXML
    private void getOption() throws IOException{
        // get reading option
        String chapter=chapterList.getValue();
        option.put("chapter", chapter);
        if (allCardRadioButton.isSelected()) {
            option.put("selectedCard","allCard");
        } else if(unreadCardRadioButton.isSelected()) {
            option.put("selectedCard","unreadCard");
        }else if(expiredCardRadioButton.isSelected()){
            option.put("selectedCard","expiredCard");
        }else{
            option.put("selectedCard","selected");
        } 
        if (restrictionCheckBox.isSelected()){
            if(timeRadioButton.isSelected()&timeValue!=null){
                option.put("time", timeValue.getText());
            }else if(cardnumberRadioButton.isSelected()&cardnumberValue!=null){
                option.put("cardNumber",cardnumberValue.getText());
            }
        }
        if(frontSideRadioButton.isSelected()){
            option.put("frontSide","true");
        }else{
            option.put("backSide","true");
        }
        // get scheule option
        if(days0Value.getText().isEmpty()){
            option.put("level0",null);
        }else{
            option.put("level0",days0Value.getText());
        }
        if(days1Value.getText().isEmpty()){
            option.put("level1",null);
        }else{
            option.put("level1",days1Value.getText());
        }
        if(days2Value.getText().isEmpty()){
            option.put("level2",null);
        }else{
            option.put("level2",days2Value.getText());
        }
        if(days3Value.getText().isEmpty()){
            option.put("level3",null);
        }else{
            option.put("level3",days3Value.getText());
        }
        if(days4Value.getText().isEmpty()){
            option.put("level4",null);
        }else{
            option.put("level4",days4Value.getText());
        }
        if(days5Value.getText().isEmpty()){
            option.put("level5",null);
        }else{
            option.put("level5",days5Value.getText());
        }
        if(days6Value.getText().isEmpty()){
            option.put("level6",null);
        }else{
            option.put("level6",days6Value.getText());
        }
        if(days7Value.getText().isEmpty()){
            option.put("level7",null);
        }else{
            option.put("level7",days7Value.getText());
        }
        if(days8Value.getText().isEmpty()){
            option.put("level8",null);
        }else{
            option.put("level8",days8Value.getText());
        }
        if(days9Value.getText().isEmpty()){
            option.put("level9",null);
        }else{
            option.put("level9",days9Value.getText());
        }
        if (hour0Value.getText().isEmpty() || hour0Value.getText()=="0") {
            option.put("hour0",null);
        } else {
            option.put("hour0",hour0Value.getText());
        }
        if (hour1Value.getText().isEmpty() || hour1Value.getText()=="0") {
            option.put("hour1",null);
        } else {
            option.put("hour1",hour1Value.getText());
        }
        if (hour2Value.getText().isEmpty() || hour2Value.getText()=="0") {
            option.put("hour2",null);
        } else {
            option.put("hour2",hour2Value.getText());
        }
        if (hour3Value.getText().isEmpty() || hour3Value.getText()=="0") {
            option.put("hour3",null);
        } else {
            option.put("hour3",hour3Value.getText());
        }
        if (hour4Value.getText().isEmpty() || hour4Value.getText()=="0") {
            option.put("hour4",null);
        } else {
            option.put("hour4",hour4Value.getText());
        }
        if (hour5Value.getText().isEmpty() || hour5Value.getText()=="0") {
            option.put("hour5",null);
        } else {
            option.put("hour5",hour5Value.getText());
        }
        if (hour6Value.getText().isEmpty() || hour6Value.getText()=="0") {
            option.put("hour6",null);
        } else {
            option.put("hour6",hour6Value.getText());
        }
        if (hour7Value.getText().isEmpty() || hour7Value.getText()=="0") {
            option.put("hour7",null);
        } else {
            option.put("hour7",hour7Value.getText());
        }
        if (hour8Value.getText().isEmpty() || hour8Value.getText()=="0") {
            option.put("hour8",null);
        } else {
            option.put("hour8",hour8Value.getText());
        }
        if (hour9Value.getText().isEmpty() || hour9Value.getText()=="0") {
            option.put("hour9",null);
        } else {
            option.put("hour9",hour9Value.getText());
        }
        // get advanced option
        if(enableCheckBox.isSelected()){
            option.put("pronounciation","enable");
            option.put("engine", engineList.getValue());
            option.put("voice", voiceList.getValue());
        }else{
            option.put("engine", engineList.getValue());
            option.put("voice", voiceList.getValue());
        }
        
        if(shuffleCardRadioButton.isSelected()){
            option.put("cardOrder", "shuffle");
        }
        else{
             option.put("cardOrder","regular");
        }
        filterCards();
        start();
        closeOption();
    }
    
    private void filterCards(){
        if(option.get("selectedCard").equals("selected")&&!selectedCards.isEmpty()){
            filterCards=(ObservableList<Card>) selectedCards;
        }else{
         FilteredList<Card> filteredData = new FilteredList(ob,filterByOption(option.get("selectedCard"),option.get("chapter")));
         if("shuffle".equals(option.get("cardOrder"))){
            FXCollections.shuffle(ob);
            filterCards=filteredData;           
         }
         else{
             filterCards=filteredData;
         }
        }
    }
    private void start() throws IOException{
        LessonController.expiredNumber=0;
        LessonController.unlearnedNumber=0;
       FXMLLoader fxmlLoader=new FXMLLoader(getClass().getResource("view/review.fxml"),resource);
       ReviewController controller=fxmlLoader.getController();
                    Parent root=fxmlLoader.load();
                     Scene scene=new Scene(root,ownerStage.getWidth(),ownerStage.getHeight());
                     ownerStage.setScene(scene);
                     ownerStage.getIcons().add(new Image(getClass().getResource("image/logo.png").toString()));
                     ownerStage.setTitle("Review");
                     ownerStage.show();   
    }
    @FXML
    private void closeOption(){
         Stage stage=(Stage)optionOkButton.getScene().getWindow();
         stage.close();
    }
    
     UnaryOperator<Change> filter = change -> {
    String text = change.getText();

    if (text.matches("[0-9]*")) {
        return change;
    }

    return null;
};  
   Predicate<Card> filterByOption(String selectedCard,String chapter){
    if(chapter==null){   
       if(selectedCard=="allCard"){
          return  (Card p)->p.getStatus().equalsIgnoreCase("unlearned") || p.getStatus().equalsIgnoreCase("expired");
             }
       else if(selectedCard=="unreadCard"){
          return  (Card p)->p.getStatus().equalsIgnoreCase("unlearned");
       }
       else if(selectedCard=="expiredCard"){
          return  (Card p)->p.getStatus().equalsIgnoreCase("expired");
       }
       else{
          return null; 
       }      
   }
     else{  
       if(selectedCard=="allCard"){
          return  (Card p)->(p.getStatus().equalsIgnoreCase("unlearned") || p.getStatus().equalsIgnoreCase("expired"))&& p.getChapter().equals(chapter);
             }
       else if(selectedCard=="unreadCard"){
          return  (Card p)->p.getStatus().equalsIgnoreCase("unlearned") && p.getChapter().equals(chapter);
       }
       else if(selectedCard=="expiredCard"){
          return  (Card p)->p.getStatus().equalsIgnoreCase("expired") && p.getChapter().equals(chapter);
       }
       else{
          return null; 
       }
   }  
}
   }
   
