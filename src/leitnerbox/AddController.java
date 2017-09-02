/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package leitnerbox;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import static leitnerbox.LeitnerBox.config;
import static leitnerbox.LeitnerBox.prop;

/**
 * FXML Controller class
 *
 * @author ma
 */
public class AddController implements Initializable {
    @FXML
    private BorderPane background;
    @FXML
    private ChoiceBox<String> addChoiceBox;
    @FXML
    private TextArea frontSideTextArea;
    @FXML
    private TextArea backSideTextArea;
    @FXML
    private TextField addImagePath;
    @FXML
    private Button addChooseFileButton;
    @FXML
    private Button addButton;
    private File addImage;
    private Path source;
    private Path target;
    private  List<String> chapter;
    private  int id;
    private  String file;
    private boolean closeEvent;
    private String newImage;
    private Path newTarget;
    private String newTargetPath;
    static ObservableList<Path> addImageList=FXCollections.observableArrayList();
      

    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
     @Override
    public void initialize(URL url, ResourceBundle rb) {
        background.setStyle(config.getProperty("color"));
        closeEvent=false;   
        id=LessonController.ob.size();
    }
  @FXML
  void loadAddCard (String file,String chapters) throws IOException {
        chapter=Arrays.asList(chapters.split(","));
        addChoiceBox.getItems().addAll(chapter);
        addChoiceBox.getSelectionModel().selectFirst();
        this.file=file;    
 }
  //create new card
  @FXML
  private void addCard() throws IOException{
     Card card=new Card();
        if(frontSideTextArea.getText().isEmpty() || backSideTextArea.getText().isEmpty()||frontSideTextArea.getText()==null || backSideTextArea.getText()==null){
            Alert alert ;
            alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle(prop.getProperty("caution"));
            alert.setHeaderText(prop.getProperty("add.addcard.alert.head"));
            alert.setContentText(prop.getProperty("add.addcard.alert.content"));
            alert.showAndWait();
        }
        else if(addImage!=null){
        source=Paths.get(addImage.getAbsolutePath());
        target=Paths.get("src/leitnerbox/lessons",file,"images",addImage.getName());
        if(target.toFile().exists()){
            id+=1;
                dialog();
                               Files.copy(source,newTarget,StandardCopyOption.COPY_ATTRIBUTES);
                               addImageList.add(newTarget);
                                card.setFrontside(frontSideTextArea.getText());
                                card.setBackside(backSideTextArea.getText());
                                card.setImg(newTargetPath);
                                card.setExpiredtime();
                                card.setLevel("0");
                                card.setTesthit("0");
                                card.setChapter(addChoiceBox.getSelectionModel().getSelectedItem());
                                card.setId(String.valueOf(id));
                                card.setStatus("unlearned");
                                LessonController.ob.add(card);
                                LessonController.chapter.add(card.getChapter());
                                addImage=null;
                                frontSideTextArea.clear();
                                backSideTextArea.clear();
                                addImagePath.clear();    
                                if(LessonController.add==false){
                                     LessonController.add=true;
                                 }
                             }   
                  else{
                  id+=1;
                 Files.copy(source,target,StandardCopyOption.COPY_ATTRIBUTES);
                 addImageList.add(target);
                 card.setFrontside(frontSideTextArea.getText());
                 card.setBackside(backSideTextArea.getText());
                 card.setImg(addImage.getName());
                 card.setExpiredtime();
                 card.setLevel("0");
                 card.setTesthit("0");
                 card.setChapter(addChoiceBox.getSelectionModel().getSelectedItem());
                 card.setId(String.valueOf(id));
                 card.setStatus("unlearned");
                 LessonController.ob.add(card);
                 LessonController.chapter.add(card.getChapter());
                 addImage=null;
                 frontSideTextArea.clear();
                 backSideTextArea.clear();
                 addImagePath.clear();    
                  if(LessonController.add==false){
                     LessonController.add=true;
                  }
        }
  
        }
        else{
                id+=1;
                 card.setFrontside(frontSideTextArea.getText());
                 card.setBackside(backSideTextArea.getText());
                 card.setImg("");
                 card.setExpiredtime();
                 card.setLevel("0");
                 card.setTesthit("0");
                 card.setChapter(addChoiceBox.getSelectionModel().getSelectedItem());
                 card.setId(String.valueOf(id));
                 card.setStatus("unlearned");
                 LessonController.ob.add(card);
                 LessonController.chapter.add(card.getChapter());
                 frontSideTextArea.clear();
                 backSideTextArea.clear();
                 addImagePath.clear(); 
                  if(LessonController.add==false){
                     LessonController.add=true;
                  }
        } 
        LessonController.unlearnedNumber++;
  }
  
  private void dialog(){
        TextInputDialog dialog = new TextInputDialog();
                            dialog.setTitle(prop.getProperty("caution"));
                            dialog.setHeaderText(prop.getProperty("add.dialog.head"));
                            dialog.setContentText(prop.getProperty("add.dialog.content"));
                            Optional<String> result = dialog.showAndWait();
                            if (result.isPresent()){
                             String extention=addImage.getName().substring(addImage.getName().lastIndexOf(".")+1,addImage.getName().length());
                              newImage = "src/leitnerbox/lessons/"+file+"/"+"images"+"/"+result.get()+"."+extention;
                              newTarget = Paths.get(newImage);
                              if(newTarget.toFile().exists()){
                                  dialog();
                              }else{
                              newTargetPath=result.get()+"."+extention;
                              }
    }else{
                    dialog.close();
                            }
                            
    }
 //add image to cards
  @FXML
  private void addImage() throws IOException{
     Stage stage=new Stage();
     FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(prop.getProperty("add.filechooser.title"));
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.getExtensionFilters().addAll(
        new FileChooser.ExtensionFilter("ALL", "*.*"),
        new FileChooser.ExtensionFilter("JPG", "*.jpg"),
        new FileChooser.ExtensionFilter("GIF", "*.gif"),
        new FileChooser.ExtensionFilter("BMP", "*.bmp"),
        new FileChooser.ExtensionFilter("PNG", "*.png"));
        addImage= fileChooser.showOpenDialog(stage);
        if(addImage!=null){
        addImagePath.setText(addImage.getName());
        }
  }        
} 

      


