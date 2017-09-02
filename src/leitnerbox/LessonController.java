/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package leitnerbox;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.dom4j.Element;
import static leitnerbox.LeitnerBox.config;
import static leitnerbox.LeitnerBox.prop;
import static leitnerbox.LeitnerBox.resource;

/**
 * FXML Controller class
 *
 * @author ma
 */
public class LessonController implements Initializable {
    @FXML
    private ScrollPane background;
    @FXML
    private Button homeButton;
    @FXML
    private Button addButton;
    @FXML
    private Button editButton;
    @FXML
    private Button exportButton;
    @FXML
    private Button findButton;
    @FXML
    private Button resetButton;
    @FXML
    private Button optionButton;
    @FXML
    private Button startButton;
    @FXML
    private BarChart<String,Integer> lessonChart;
    @FXML
    private TableView<Card> lessonCardTable;
    
    @FXML
    private TableColumn<Card,String> frontsideColumn;

    @FXML
    private TableColumn<Card,String> chapterColumn;

    @FXML
    private TableColumn<Card,String> levelColumn;

    @FXML
    private TableColumn<Card, String> statusColumn;
    static ObservableList<Card>  ob=FXCollections.observableArrayList();
    static ObservableList<Card>  editedCards=FXCollections.observableArrayList();
    static String file;
    static String chapters;
    static boolean add=false;
    private Document document;
    private int obSize;
    static File lessonFile;
    private boolean reset=false;
    static boolean edit=false;
    private boolean delete=false;
    static boolean select=false;
    ObservableList<Card> deleteList=FXCollections.observableArrayList();
    static List<Card>  selectedCards=null;
    static  HashSet<String> chapter=new HashSet();
    private int finishedNumber;
    static int unlearnedNumber;
    private int learnedNumber;
    static int expiredNumber;
    static Stage ownerStage;
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        selectedCards=FXCollections.observableArrayList();
        background.setStyle(config.getProperty("color"));
        lessonCardTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        setStyleForTableRow();
        populateTableView();
        Platform.runLater(new Runnable() {
            @Override public void run() {
                FXCollections.sort(lessonCardTable.getItems(),new Comparator<Card>(){
                    @Override
                    // return 1 if rhs should be before lhs
                    //     return -1 if lhs should be before rhs
                    //     return 0 otherwise
                    public int compare(Card o1, Card o2) {
                        
                        if(o2.getStatus().equalsIgnoreCase("expired")){
                            return 1;
                        }else if(o1.getStatus().equalsIgnoreCase("unlearned")){
                            return 1;
                        }else if(o1.getStatus().equalsIgnoreCase("learned")&&o2.getStatus().equalsIgnoreCase("unlearned")){
                            return 1;
                        }else if(o1.getStatus().equalsIgnoreCase("finished")&&o2.getStatus().equalsIgnoreCase("unlearned")){
                            return 1;
                        }else if(o1.getStatus().equalsIgnoreCase("finished")){
                            return 1;
                        }else{
                            return 0;
                        }
                    }
                });
            }
        });
           
        
    }   
    void loadFile(String fileName) throws DocumentException, ParseException, FileNotFoundException, UnsupportedEncodingException{
        if(fileName!=null){
        file=fileName.substring(0,fileName.lastIndexOf("."));
        String path="src/leitnerbox/lessons/"+file+"/"+fileName;
        lessonFile=new File(path);
        SAXReader reader=new SAXReader();
        document = reader.read(lessonFile);
        Node lesson=document.selectSingleNode("//lesson");
        chapters=lesson.valueOf("@chapters");
        List<Card> cards=new ArrayList();
        List<Node> nodes=document.selectNodes("//lesson/card");
        Date date=new Date();
        for (Node nod : nodes) {
            Card card=new Card();
            card.setId(nod.valueOf("@id"));
            card.setFrontside(nod.valueOf("@frontside"));
            card.setBackside(nod.valueOf("@backside"));
            card.setChapter(nod.valueOf("@chapter"));
            card.setLevel(nod.valueOf("@level")); 
            card.setExpiredtime(nod.valueOf("@expiredtime")); 
            card.setTesthit(nod.valueOf("@testhit")); 
            card.setImg(nod.valueOf("@img")); 
            card.setStatus(nod.valueOf("@status"));
            if(card.getStatus().equalsIgnoreCase("finished")){
               card.setStatus("finished"); 
               finishedNumber++;
            }
            else if (card.getExpiredtime()==null) {
                card.setStatus("unlearned");
                unlearnedNumber++;
                addChapter(card.getChapter());
            }
            else if(card.getExpiredtime().before(date)){
                card.setStatus("expired");
                expiredNumber++;
               addChapter(card.getChapter());
            }
            else{
                card.setStatus("learned");
                learnedNumber++;
            } 
            cards.add(card);
        }
        
       ob.addAll(cards);
       obSize=ob.size();
       drawChart();
       disableStartButton();
    }
    }
    static void addChapter(String c){
        if(!chapter.contains(c)){
            chapter.add(c);
        }
    }
    private void disableStartButton(){
        if(expiredNumber==0 &&unlearnedNumber==0){
            startButton.setDisable(true);
        }
    }
    private void setStyleForTableRow(){
    lessonCardTable.setRowFactory((TableView<Card> row)->{
        return new TableRow<Card>(){
            @Override
            public void updateItem(Card item, boolean empty) {
                super.updateItem(item, empty) ;
                if (item == null) {
                    setStyle("");
                } else if (item.getStatus().equalsIgnoreCase("expired")) {
                    setStyle("-fx-background-color:#ed4b1a;");
                } else if (item.getStatus().equalsIgnoreCase("unlearned")) {
                    setStyle("-fx-background-color:#fffb3f;");
                } else if (item.getStatus().equalsIgnoreCase("learned")) {
                    setStyle("-fx-background-color:#e2ff3f;");
                }else {
                    setStyle("");
                }
            };
            
        };
    });
    }
    private void drawChart(){
        CategoryAxis xAxis    = new CategoryAxis();
        xAxis.setLabel(prop.getProperty("lesson.statuscolumn.text"));
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel(prop.getProperty("numbers"));
        XYChart.Series dataSeries= new XYChart.Series();
        dataSeries.setName(file);
        dataSeries.getData().add(new XYChart.Data("learned",learnedNumber));
        dataSeries.getData().add(new XYChart.Data("unlearned",unlearnedNumber));
        dataSeries.getData().add(new XYChart.Data("expired",expiredNumber));
        dataSeries.getData().add(new XYChart.Data("finished",finishedNumber));
        lessonChart.getData().add(dataSeries);
    }
    @FXML
    void populateTableView() throws NullPointerException{
        
            lessonCardTable.setItems(ob);
             frontsideColumn.setCellValueFactory(new PropertyValueFactory<>("frontside"));
             chapterColumn.setCellValueFactory(new PropertyValueFactory<>("chapter"));
             levelColumn.setCellValueFactory(new PropertyValueFactory<>("level"));
             statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));       
    }
    @FXML
    private void home() throws IOException{
        Stage lessonStage=(Stage)homeButton.getScene().getWindow();
        lessonStage.setOnCloseRequest(null);
        Scene scene=null;
        if(add==true || reset==true || edit==true || delete==true){
                 Alert alert=new Alert(Alert.AlertType.CONFIRMATION);
                 alert.setTitle(prop.getProperty("caution"));
                 alert.setHeaderText(prop.getProperty("lesson.home.alert.text"));
                 alert.setContentText(prop.getProperty("lesson.home.alert.content")); 
                 ButtonType ok=new ButtonType(prop.getProperty("ok"));
                 ButtonType no=new ButtonType(prop.getProperty("no"));
                 alert.getButtonTypes().setAll(ok,no);
                  Optional<ButtonType> result = alert.showAndWait();
                 if (result.get()==ok){
                    if(add==true){
                     try {
                         saveAfterAdd();
                     } catch (IOException ex) {
                         Logger.getLogger(LessonController.class.getName()).log(Level.SEVERE, null, ex);
                     }
                    } 
                     if(edit==true){
                        saveAfterEdit();
                    } 
                   try {
                       save();
                   } catch (IOException ex) {
                       Logger.getLogger(LessonController.class.getName()).log(Level.SEVERE, null, ex);
                   }
                     ob.clear();
                     add=false;
                     edit=false;
                     reset=false;
                     delete=false;
                     select=false;
                     if(!selectedCards.isEmpty()){
                         selectedCards.clear();
                     }
                     Parent root=FXMLLoader.load(getClass().getResource("view/main.fxml"),resource);
                     Stage stage=(Stage)homeButton.getScene().getWindow();
                     if(stage.isMaximized()){
                         scene=new Scene(root,stage.getWidth(),stage.getHeight()); 
                     }else{
                         scene=new Scene(root);
                     }
                     stage.setScene(scene);
                     stage.getIcons().add(new Image(getClass().getResource("image/logo.png").toString()));
                     stage.setTitle(prop.getProperty("home"));
                     stage.show();
                }else{
                     ob.clear();
                     add=false;
                     edit=false;
                     reset=false;
                     delete=false;
                     select=false;
                     if(!selectedCards.isEmpty()){
                         selectedCards.clear();
                     }
                     AddController.addImageList.forEach(path->{
                         try {
                             Files.deleteIfExists(path);
                         } catch (IOException ex) {
                             Logger.getLogger(LessonController.class.getName()).log(Level.SEVERE, null, ex);
                         }
                    });
                     Parent root=FXMLLoader.load(getClass().getResource("view/main.fxml"),resource);
                      Stage stage=(Stage)homeButton.getScene().getWindow();
                     if(stage.isMaximized()){
                         scene=new Scene(root,stage.getWidth(),stage.getHeight()); 
                     }else{
                         scene=new Scene(root);
                     }
                     stage.setScene(scene);
                     stage.getIcons().add(new Image(getClass().getResource("image/logo.png").toString()));
                     stage.setTitle(prop.getProperty("home"));
                     stage.show();          
                     }
        }else{
            ob.clear();
                     add=false;
                     edit=false;
                     reset=false;
                     delete=false;
                     select=false;
                     if(!selectedCards.isEmpty()){
                         selectedCards.clear();
                     }
                     Parent root=FXMLLoader.load(getClass().getResource("view/main.fxml"),resource);
                      Stage stage=(Stage)homeButton.getScene().getWindow();
                     if(stage.isMaximized()){
                         scene=new Scene(root,stage.getWidth(),stage.getHeight()); 
                     }else{
                         scene=new Scene(root);
                     }
                     stage.setScene(scene);
                     stage.getIcons().add(new Image(getClass().getResource("image/logo.png").toString()));
                     stage.setTitle(prop.getProperty("home"));
                     stage.show();
        }
        LessonController.unlearnedNumber=0;
        LessonController.expiredNumber=0;
    }
    @FXML
    private void add() throws IOException{
        FXMLLoader fxmlLoader=new FXMLLoader(getClass().getResource("view/add.fxml"),resource);
         Parent root=fxmlLoader.load();    
         AddController controller=fxmlLoader.getController();
         controller.loadAddCard(file,chapters);
                     Scene scene=new Scene(root);
                     Stage stage=new Stage();
                     stage.setScene(scene);
                     stage.initModality(Modality.WINDOW_MODAL);
                     stage.initOwner((Stage)homeButton.getScene().getWindow());
                     stage.getIcons().add(new Image(getClass().getResource("image/logo.png").toString()));
                     stage.setTitle(prop.getProperty("add.label"));
                     stage.show(); 
        onClose();
    } 
    @FXML
    private void edit() throws IOException{
        if(lessonCardTable.getSelectionModel().isEmpty()){
            Alert alert=new Alert(Alert.AlertType.WARNING);
                 alert.setTitle(prop.getProperty("caution"));
                 alert.setHeaderText(prop.getProperty("lesson.edit.alert.text"));
                 alert.setContentText(prop.getProperty("lesson.edit.alert.content")); 
                 alert.show();
        onClose();
        }
        else if(lessonCardTable.getSelectionModel().getSelectedItems().size()>1){
            Alert alert=new Alert(Alert.AlertType.WARNING);
                 alert.setTitle(prop.getProperty("caution"));
                 alert.setHeaderText(prop.getProperty("lesson.edit.alert.text"));
                 alert.setContentText(prop.getProperty("lesson.edit.alert.singlecontent")); 
                 alert.show();
        }
        else{
            Card card= (Card)lessonCardTable.getSelectionModel().getSelectedItem();
            FXMLLoader fxmlLoader=new FXMLLoader(getClass().getResource("view/edit.fxml"),resource);
            Parent root=fxmlLoader.load();   
            EditController controller=fxmlLoader.getController();
                           controller.editCards(card,file,chapters);
               
                     Scene scene=new Scene(root);
                     Stage stage=new Stage();
                     stage.setScene(scene);
                     stage.initModality(Modality.WINDOW_MODAL);
                     stage.initOwner((Stage)homeButton.getScene().getWindow());
                     stage.getIcons().add(new Image(getClass().getResource("image/logo.png").toString()));
                     stage.setTitle(prop.getProperty("edit.label"));
                     stage.show();
        }
    onClose();
    }
    @FXML
    private void export() throws IOException{
        FXMLLoader fxmlLoader=new FXMLLoader(getClass().getResource("view/export.fxml"),resource);
        ExportController controller=fxmlLoader.getController();
         Parent root=fxmlLoader.load();
                     Scene scene=new Scene(root);
                     Stage stage=new Stage();
                     stage.setScene(scene);
                     stage.initModality(Modality.WINDOW_MODAL);
                     stage.initOwner((Stage)homeButton.getScene().getWindow());
                     stage.getIcons().add(new Image(getClass().getResource("image/logo.png").toString()));
                     stage.setTitle(prop.getProperty("export.label"));
                     stage.show();          
    }
    @FXML
    private void find() throws IOException{
         Parent root=FXMLLoader.load(getClass().getResource("view/find.fxml"),resource);         
                     Scene scene=new Scene(root);
                     Stage stage=new Stage();
                     stage.setScene(scene);
                     stage.initModality(Modality.WINDOW_MODAL);
                     stage.initOwner((Stage)homeButton.getScene().getWindow());
                     stage.getIcons().add(new Image(getClass().getResource("image/logo.png").toString()));
                     stage.setTitle(prop.getProperty("find.label"));
                     stage.show();          
    }
    @FXML
    private void start() throws IOException{
         if(add==true || reset==true || edit==true||delete==true){    
                    if(add==true){
                     try {
                         saveAfterAdd();
                     } catch (IOException ex) {
                         Logger.getLogger(LessonController.class.getName()).log(Level.SEVERE, null, ex);
                     }
                    } 
                    if(edit==true){
                        saveAfterEdit();
                    }
                    try {
                       save();
                   } catch (IOException ex) {
                       Logger.getLogger(LessonController.class.getName()).log(Level.SEVERE, null, ex);
                   }
                    add=false;
                    edit=false;
                    reset=false;
                    delete=false;
         }
        if(!lessonCardTable.getSelectionModel().isEmpty()){
            select=true;
            selectedCards=lessonCardTable.getSelectionModel().getSelectedItems();
        }
        FXMLLoader fxmlloader=new FXMLLoader(getClass().getResource("view/reading.fxml"),resource); 
         Parent root=fxmlloader.load();
                     Scene scene=new Scene(root);
                     Stage stage=new Stage();
                     ownerStage=(Stage)homeButton.getScene().getWindow();
                     stage.setScene(scene);
                     stage.initModality(Modality.WINDOW_MODAL);
                     stage.initOwner(ownerStage);
                     stage.getIcons().add(new Image(getClass().getResource("image/logo.png").toString()));
                     stage.setTitle(prop.getProperty("reading.tab1"));
                     stage.show();          
    }
    @FXML
    private void reset() throws IOException{
        if(!lessonCardTable.getSelectionModel().isEmpty()){
        ObservableList<Card> card =FXCollections.observableArrayList();
          Alert alert=new Alert(Alert.AlertType.CONFIRMATION);
                 alert.setTitle(prop.getProperty("caution"));
                 alert.setHeaderText(prop.getProperty("lesson.reset.alert.text"));
                 alert.setContentText(prop.getProperty("lesson.reset.alert.content"));
                 ButtonType ok=new ButtonType(prop.getProperty("ok"));
                 ButtonType no=new ButtonType(prop.getProperty("no"));
                 alert.getButtonTypes().setAll(ok,no);
                 Optional<ButtonType> result = alert.showAndWait();
                 if (result.get()==ok) { 
                    card=lessonCardTable.getSelectionModel().getSelectedItems();
                     for (Card item : card) {
                         int i=ob.indexOf(item);
                        ob.get(i).setStatus("unlearned");
                        ob.get(i).setExpiredtime();
                        ob.get(i).setLevel("0");    
                     }
                     lessonCardTable.refresh();
                       saveAfterReset(card);   
                 }    
            }
       onClose();
    }
    @FXML
    private void delete() throws IOException{
        if (lessonCardTable.getSelectionModel().getSelectedItems().size()>1 || lessonCardTable.getSelectionModel().isEmpty()){
            Alert alert=new Alert(Alert.AlertType.WARNING);
                 alert.setTitle(prop.getProperty("caution"));
                 alert.setHeaderText(prop.getProperty("lesson.delete.alert.text"));
                 alert.setContentText(prop.getProperty("lesson.delete.alert.content")); 
                 alert.show();
        }
        else{
          Alert alert=new Alert(Alert.AlertType.CONFIRMATION);
                 alert.setTitle(prop.getProperty("caution"));
                 alert.setHeaderText(prop.getProperty("lesson.delete.alert1.text"));
                 alert.setContentText(prop.getProperty("lesson.delete.alert1.content"));
                 ButtonType ok=new ButtonType(prop.getProperty("ok"));
                 ButtonType no=new ButtonType(prop.getProperty("no"));
                 alert.getButtonTypes().setAll(ok,no);
                 Optional<ButtonType> result = alert.showAndWait();
                 if (result.get()==ok) { 
                    Card  card=lessonCardTable.getSelectionModel().getSelectedItem();
                    deleteList.add(card);
                    ob.remove(card);
                    delete=true;
                    saveAfterDelete();
                    onClose();  
                 }
                 alert.close();
        }
    }            
    private void save() throws IOException{
     try(Writer out = new BufferedWriter(new OutputStreamWriter(
			new FileOutputStream(lessonFile), "UTF8"))) {
            document.write(out);
                    }
}
    void saveAfterAdd() throws IOException {
        Element root= document.getRootElement();
        
            for (int i =obSize; i <ob.size(); i++) {
                 root.addElement("card")
                         .addAttribute("id",ob.get(i).getId())
                         .addAttribute("frontside",ob.get(i).getFrontside())
                         .addAttribute("backside",ob.get(i).getBackside())
                         .addAttribute("chapter",ob.get(i).getChapter())
                         .addAttribute("level",ob.get(i).getLevel())
                         .addAttribute("expiredtime",null)
                         .addAttribute("testhit",ob.get(i).getTesthit())
                         .addAttribute("img",ob.get(i).getImg()); 
    }
           
}
    private void saveAfterReset(List<Card> list) throws IOException{
         list.stream().forEach((Card resetCards) -> {
             String id=resetCards.getId();
                    Element node=(Element) document.selectSingleNode("//lesson/card[@id="+id+"]");
                    node.addAttribute("level","0")
                        .addAttribute("expiredtime",null)
                        .addAttribute("status",null);
        });
         reset=true;
     }
    private void saveAfterDelete(){
        deleteList.forEach((Card card)->{
            Element node=(Element) document.selectSingleNode("//lesson/card[@id="+card.getId()+"]");
            if (node!=null) {
                node.detach();
            }
            if(!card.getImg().isEmpty()){
            try {
               
                String imageForDelete="src/leitnerbox/lessons/"+file+"/"+"images"+"/"+card.getImg();
                Files.delete(Paths.get(imageForDelete));
            } catch (IOException ex) {
                Logger.getLogger(LessonController.class.getName()).log(Level.SEVERE, null, ex);
            }
            }
        });
        ob.stream().forEach((Card card)->{
            String id=card.getId();
            Element node=(Element) document.selectSingleNode("//lesson/card[@id="+id+"]");
            if(node!=null){
            node.detach();
            }
        });
         Element root= document.getRootElement();
         SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd HH:mm");
          int i=1;
        for (Card card : ob) {   
            Element addCard = root.addElement("card")
                    .addAttribute("id",String.valueOf(i))
                    .addAttribute("frontside",card.getFrontside())
                    .addAttribute("backside",card.getBackside())
                    .addAttribute("chapter",card.getChapter())
                    .addAttribute("level",card.getLevel())
                    .addAttribute("testhit",card.getTesthit())
                    .addAttribute("img",card.getImg())
                    .addAttribute("status",card.getStatus());
                         if(card.getExpiredtime()!=null){
                           addCard.addAttribute("expiredtime",formatter.format(card.getExpiredtime()));  
                         }
            
            i+=1;
        }
        
    }
    private void saveAfterEdit(){
         SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd HH:mm");
        editedCards.stream().forEach((Card card)->{
          Element node=(Element) document.selectSingleNode("//lesson/card[@id="+card.getId()+"]"); 
          if(node!=null){
                  node.addAttribute("id",card.getId())
                         .addAttribute("frontside",card.getFrontside())
                         .addAttribute("backside",card.getBackside())
                         .addAttribute("chapter",card.getChapter())
                         .addAttribute("level",card.getLevel())                    
                         .addAttribute("testhit",card.getTesthit())
                         .addAttribute("img",card.getImg());
                  if(card.getExpiredtime()!=null){
                      node.addAttribute("expiredtime",formatter.format(card.getExpiredtime()));
                  }
          }
                   });
        
      if(EditController.deleteImage!=null){
        EditController.deleteImage.stream().forEach((Path e)->{
                try {
                    Files.deleteIfExists(e);
                } catch (IOException ex) {
                    Logger.getLogger(LessonController.class.getName()).log(Level.SEVERE, null, ex);
                }
        });
          EditController.deleteImage.clear();
      }
       if(EditController.copyImages!=null){
           EditController.copyImages.forEach((Path target,Path source)->{
               try {
                   Files.copy(source, target,StandardCopyOption.COPY_ATTRIBUTES);
               } catch (IOException ex) {
                   Logger.getLogger(LessonController.class.getName()).log(Level.SEVERE, null, ex);
               }
               
           });
           EditController.copyImages.clear();
        }
        EditController.Image.clear();
        editedCards=null;
    }
    private void onClose(){
    Stage stage=(Stage) homeButton.getScene().getWindow();
          stage.setOnCloseRequest((WindowEvent e)->{
               if(add==true || reset==true || edit==true || delete==true){
                 Alert alert=new Alert(Alert.AlertType.CONFIRMATION);
                 alert.setTitle(prop.getProperty("caution"));
                 alert.setHeaderText(prop.getProperty("lesson.home.alert.text"));
                 alert.setContentText(prop.getProperty("lesson.home.alert.content")); 
                 ButtonType ok=new ButtonType(prop.getProperty("ok"));
                 ButtonType no=new ButtonType(prop.getProperty("no"));
                 alert.getButtonTypes().setAll(ok,no);
                  Optional<ButtonType> result = alert.showAndWait();
                 if (result.get()==ok){
                    
                    if(add==true){
                     try {
                         saveAfterAdd();
                     } catch (IOException ex) {
                         Logger.getLogger(LessonController.class.getName()).log(Level.SEVERE, null, ex);
                     }
                    } 
                    if(edit==true){
                        saveAfterEdit();
                    }
                    try {
                       save();
                   } catch (IOException ex) {
                       Logger.getLogger(LessonController.class.getName()).log(Level.SEVERE, null, ex);
                   }
                    ob.clear();
                    add=false;
                    edit=false;
                    reset=false;
                    delete=false;
                    stage.close();
                 }
                 
                 else{ 
                     ob.clear();
                    add=false;
                    edit=false;
                    reset=false;
                    delete=false;
                    AddController.addImageList.forEach(path->{
                         try {
                             Files.deleteIfExists(path);
                         } catch (IOException ex) {
                             Logger.getLogger(LessonController.class.getName()).log(Level.SEVERE, null, ex);
                         }
                    });
                    stage.close();
                 }
            }else{     
               ob.clear();
               add=false;
               edit=false;
               reset=false;
               delete=false;
                stage.close();
               }
});
          
    }
}
