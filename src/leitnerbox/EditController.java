/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package leitnerbox;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import static leitnerbox.LeitnerBox.prop;
import static leitnerbox.LeitnerBox.config;


/**
 * FXML Controller class
 *
 * @author ma
 */
public class EditController implements Initializable {
    @FXML
    private BorderPane background;
    @FXML
    private TextFlow editLabel;
    @FXML
    private Label editFrontsideLabel;
    @FXML
    private TextArea editFronsideTextArea;
    @FXML
    private Label editBacksideLabel;
    @FXML
    private TextArea editBacksideTextArea;
    @FXML
    private Label editImageLabel;
    @FXML
    private TextField editPath;
    @FXML
    private Button editChoosefileButton;
    @FXML
    private ComboBox<String> editChapter;
    @FXML
    private Button editDeleteButton;
    @FXML
    private Button editButton;
    @FXML
    private File editImage=null;
    private Card item;
    private String fileName;
    private  List<String> chapter;
    static ObservableList<Path> Image=FXCollections.observableArrayList();
    static ObservableList<Path> deleteImage=FXCollections.observableArrayList();
    static ObservableMap<Path,Path> copyImages=FXCollections.observableHashMap();
    private Path newTarget;
    private boolean remove=false;
    private String newImage;
    private String image;
    private Path source;
    private Path target;
    private String newImagePath;
    @FXML
    private Button editButton1;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        background.setStyle(config.getProperty("color"));
        editPath.setDisable(true);
        editDeleteButton.setDisable(true);
        
    }    
    void editCards(Card card,String file,String chapters){
        item=card;
        fileName=file;
        chapter=Arrays.asList((chapters.split(",")));
         editFronsideTextArea.setText(card.getFrontside());
         editBacksideTextArea.setText(card.getBackside());
         editChapter.getItems().addAll(chapter);
         editChapter.getSelectionModel().select(card.getChapter());
         if (card.getImg()!="") {
            editPath.setText(card.getImg());
            editDeleteButton.setDisable(false);
            editChoosefileButton.setDisable(true);
         
        }
    }
    @FXML
  private void editImage() throws IOException{
     Stage stage=new Stage();
     FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(prop.getProperty("edit.filechooser.title"));
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.getExtensionFilters().addAll(
        new FileChooser.ExtensionFilter("ALL", "*.*"),
        new FileChooser.ExtensionFilter("JPG", "*.jpg"),
        new FileChooser.ExtensionFilter("GIF", "*.gif"),
        new FileChooser.ExtensionFilter("BMP", "*.bmp"),
        new FileChooser.ExtensionFilter("PNG", "*.png"));
        editImage= fileChooser.showOpenDialog(stage);
        editPath.setText(editImage.getName());
        editDeleteButton.setDisable(false);
  } 
    @FXML
    private void edit() throws IOException, ParseException{
        int i=LessonController.ob.indexOf(item);
        LessonController.chapter.remove(item.getChapter());
        
                      Card get=new Card();
                        get.setFrontside(editFronsideTextArea.getText());
                        get.setBackside(editBacksideTextArea.getText());
                        get.setChapter(editChapter.getSelectionModel().getSelectedItem());
                        get.setId(item.getId());
                        get.setLevel(item.getLevel());
                        get.setTesthit(item.getTesthit());
                        get.setStatus(item.getStatus());
                        if(item.getExpiredtime()!=null){
                         SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        get.setExpiredtime(formatter.format(item.getExpiredtime()));}
                        if(remove==true & editImage==null){
                            get.setImg("");
                            LessonController.ob.set(i, get);
                        } 
                        if(editImage==null & remove==false){
                            get.setImg(item.getImg());
                            LessonController.ob.set(i, get);
                        }                       
                        if(editImage!=null){
                         image="src/leitnerbox/lessons/"+fileName+"/"+"images"+"/"+editImage.getName();
                         source=Paths.get(editImage.getAbsolutePath());
                         target=Paths.get(image);
                         if(deleteImage.contains(target)){
                             Image.add(target);
                             get.setImg(editImage.getName());
                             LessonController.ob.set(i, get);
                             deleteImage.remove(target);
                         }
                        else if(target.toFile().exists() || Image.contains(target)){
                             dialog();
                             if(Image.contains(newTarget)) {
                                 dialog();
                                 get.setImg(newImagePath);
                            Image.add(newTarget);
                            copyImages.put(newTarget,source);
                            LessonController.ob.set(i, get);
                             } else{ 
                                  get.setImg(newImagePath);
                                 Image.add(newTarget);
                            copyImages.put(newTarget,source);
                            LessonController.ob.set(i, get);
                            }
                         }else{
                            get.setImg(editImage.getName());
                            Image.add(target);
                            copyImages.put(target,source);
                             LessonController.ob.set(i, get);
                        }
                               
                  }   
        LessonController.editedCards.add(get);                
        LessonController.edit=true;
        LessonController.chapter.add(get.getChapter());
      Stage stage=(Stage)editButton.getScene().getWindow();
      stage.close();
    }
    private void dialog(){
        TextInputDialog dialog = new TextInputDialog();
                            dialog.setTitle(prop.getProperty("caution"));
                            dialog.setHeaderText(prop.getProperty("edit.dialog.head"));
                            dialog.setContentText(prop.getProperty("edit.dialog.content"));
                            Optional<String> result = dialog.showAndWait();
                            if (result.isPresent()){
                             String extention=editImage.getName().substring(editImage.getName().lastIndexOf(".")+1,editImage.getName().length());
                             newImage="src/leitnerbox/lessons/"+fileName+"/"+"images"+"/"+result.get()+"."+extention;    
                            newTarget=Paths.get(newImage);
                            if(newTarget.toFile().exists()){
                                  dialog();
                              }else{
                              newImagePath=result.get()+"."+extention;
                              }
    }else{
                    dialog.close();
                            }
                            
    }
    @FXML
    private void deleteImage() throws IOException{
        editChoosefileButton.setDisable(false);
        String image="src/leitnerbox/lessons/"+fileName+"/"+"images"+"/"+item.getImg();
        Path sourceImage=Paths.get(image);
        deleteImage.add(sourceImage);
        if(Image.contains(sourceImage)){
           Image.remove(sourceImage);
        }
        editImage=null;
        editPath.clear();
        editDeleteButton.setDisable(true);
        remove=true;
        
    }  
}
