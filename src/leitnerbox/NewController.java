/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package leitnerbox;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import static leitnerbox.LeitnerBox.config;
import static leitnerbox.LeitnerBox.prop;
import static leitnerbox.LeitnerBox.resource;
/**
 * FXML Controller class
 *
 * @author ma
 */
public class NewController implements Initializable {
    @FXML
    private ScrollPane background;
    @FXML
    private Button home;
    @FXML
    private TextField newLessonName;
    @FXML
    private ComboBox<String> newLessonchapters;
    @FXML
    private TextField newAddChapter;
    @FXML
    private Button newAddChapterButton;
    @FXML
    private Button NewChooseFileButton;
    @FXML 
    private Button newCreateCardButton;
    @FXML
    private Button newRemoveChapterButton;
    @FXML
    private Button newCreateLessonButton;
    @FXML
    private TextArea newCardFrontside;
    @FXML
    private TextArea newCardBackside;
    @FXML
    private TextField newImage;
    @FXML
    private ComboBox<String> newCardChapter;
    @FXML
    private Button newSaveButton;
    @FXML
    private Button newAddCardButton;
    @FXML
    private Button newAllcardButton;
    ArrayList<String> chaptersArray=new ArrayList<>();  
    private String lessonName;
    private String chapter;
    private Path source;
    private Path target;
    private SAXReader reader = new SAXReader();
    private Document document;
    private File xmlFile;
    private File newImageSelect;
    private String fileName;
    private Element root;  
    private int  i=1;
    private Path newTarget;
    private String newFileName;

  @Override
    public void initialize(URL url, ResourceBundle rb) {
      background.setStyle(config.getProperty("color"));
      chaptersArray.add(prop.getProperty("all"));
      populateComboBox(newLessonchapters,chaptersArray);
      populateComboBox(newCardChapter,chaptersArray);
      newLessonchapters.getSelectionModel().selectFirst();
      newCardChapter.getSelectionModel().selectFirst();
      newAddChapter.setOnKeyPressed((KeyEvent e)->{
          if (e.getCode()==KeyCode.ENTER) {
              addChapter();
          }
      });
    }  
    private void saveAndClose(){
      Stage stage=(Stage)newSaveButton.getScene().getWindow();
      stage.setOnCloseRequest(e->{
          if (!newSaveButton.isDisable()) {
       Alert alert=new Alert(AlertType.CONFIRMATION);
                 alert.setTitle(prop.getProperty("caution"));
                 alert.setHeaderText(prop.getProperty("save"));
                 alert.setContentText(prop.getProperty("new.alert.save"));
                 Optional<ButtonType> result = alert.showAndWait();
                 if (result.get()==ButtonType.OK) {
           try {
               save();
           } catch (IOException ex) {
               Logger.getLogger(NewController.class.getName()).log(Level.SEVERE, null, ex);
           }
                 }
              }
      });   
    }
//create lesson
    @FXML
    private void lessonCreate(ActionEvent event) throws IOException{         
        lessonName=newLessonName.getText();
        int lessonNameSize=lessonName.length();
        if( lessonNameSize <= 0){
            Alert alert ;
            alert = new Alert(AlertType.WARNING);
            alert.setTitle(prop.getProperty("caution"));
            alert.setHeaderText(prop.getProperty("new.alert.emptylesson.head"));
            alert.setContentText(prop.getProperty("new.alert.emptylesson.text"));
            alert.showAndWait();           
        }
        else{
            fileName="src/leitnerbox/lessons/"+newLessonName.getText()+"/"+
            newLessonName.getText()+"."+"xml";
            File lessonFolder=new File("src/leitnerbox/lessons/"+newLessonName.getText()+"/images");
            if(!lessonFolder.exists()){
                lessonFolder.mkdirs();            
            newLessonName.setDisable(true);
            newCreateCardButton.setDisable(false);
            newCreateLessonButton.setDisable(true);
            }
            else{
              Alert alert ;
            alert = new Alert(AlertType.WARNING);
            alert.setTitle(prop.getProperty("caution"));
            alert.setHeaderText(prop.getProperty("new.alert.lessonname.head"));
            alert.setContentText(prop.getProperty("new.alert.lessonname.text"));
            alert.showAndWait();            
            }     
        }     
        }
    @FXML
    //save lesson
    private void save() throws IOException{
         chapter= listToString(chaptersArray);
         root.addAttribute( "name",newLessonName.getText())
             .addAttribute( "chapters", chapter );
        try (Writer out = new BufferedWriter(new OutputStreamWriter(
			new FileOutputStream(fileName), "UTF8"))) {
            document.write(out);
             newAllcardButton.setDisable(false);
             newSaveButton.setDisable(true);
        }  
    }
    //save change of card
    private void saveChange(List<Card> list,Document file) throws IOException, DocumentException{
        if(!list.isEmpty()){       
            for (Card card : list) {
                String m=card.getId();
                 String id="/lesson/card[@id="+m+"]";
               org.dom4j.Element newCard= (org.dom4j.Element) file.selectSingleNode(id);
               newCard.addAttribute("frontside",card.getFrontside())
                       .addAttribute("backside",card.getBackside());                  
            }            
    }
    }
    //add chapter
    @FXML
    private void addChapter(){
     if(newAddChapter.getText().length()>0){
         if(!chaptersArray.contains(newAddChapter.getText())){
     String item=newAddChapter.getText();
     chaptersArray.add(item);
     if(document==null){
     }
     else{
     chapter= listToString(chaptersArray);
      root.addAttribute( "chapters", chapter );
     }
     populateComboboxItemAfterAdd(newLessonchapters,chaptersArray);
     newAddChapter.clear();
     populateComboboxItemAfterAdd(newCardChapter, chaptersArray);
         }
         else{
             Alert alert ;
            alert = new Alert(AlertType.WARNING);
            alert.setTitle(prop.getProperty("caution"));
            alert.setHeaderText(prop.getProperty("new.alret.duplicate.head"));
            alert.setContentText(prop.getProperty("new.alert.duplicate.text"));
            alert.showAndWait();  
         }
     }
     else{
        Alert alert ;
            alert = new Alert(AlertType.WARNING);
            alert.setTitle(prop.getProperty("caution"));
            alert.setHeaderText(prop.getProperty("new.alert.emptychapter.head"));
            alert.setContentText(prop.getProperty("new.alert.emptychapter.text"));
            alert.showAndWait();  
     }
    }
    //show chapter list
    @FXML
    private void populateComboBox(ComboBox combo,List list){      
        combo.getItems().addAll(list);
        combo.getSelectionModel().selectFirst();
    }
    private void populateComboboxItemAfterAdd(ComboBox combo,List list){
    combo.getItems().remove(0, list.size()-1);
    combo.getItems().addAll(list);
    combo.getSelectionModel().selectFirst();
    }
    private void populateComboboxItemAfterRemove(ComboBox combo,List list){
    combo.getItems().remove(0, list.size()+1);
    combo.getItems().addAll(list);
    combo.getSelectionModel().selectNext();
    }
    //remove chapter
    @FXML
    private void removeChapter(){
        String chapterIndex= newLessonchapters.getValue();
        if(chapterIndex!=prop.getProperty("all")){
      chaptersArray.remove(chapterIndex);
      if(document==null){          
      }
      else{
              chapter= listToString(chaptersArray); 
              root.addAttribute( "chapters", chapter );
      }
        populateComboboxItemAfterRemove(newLessonchapters, chaptersArray);
        populateComboboxItemAfterRemove(newCardChapter, chaptersArray);
        }
        else{
            Alert alert ;
            alert = new Alert(AlertType.WARNING);
            alert.setTitle(prop.getProperty("caution"));
            alert.setHeaderText(prop.getProperty("new.alert.chapter"));
            alert.setContentText(prop.getProperty("new.alert.chapter.text"));
            alert.showAndWait();  
        }
    }
//convert list to string
    private String listToString(List list){
        StringBuilder sb=new StringBuilder();
        int j=list.size();
        for(int i=0;i<j;i++){
            if(i<j-1){
        sb.append(list.get(i));
        sb.append(",");
        }
            else{
                sb.append(list.get(i));
                    }        
    }
        return sb.toString();
    }     
    //active add card section
@FXML
  private void createCard(){
      newCardFrontside.setDisable(false);
      newCardBackside.setDisable(false);
      NewChooseFileButton.setDisable(false);
      newAddCardButton.setDisable(false);
      newCreateCardButton.setDisable(true);
      chapter= listToString(chaptersArray);
            document = DocumentHelper.createDocument();
            document.setName(fileName);   
            root = document.addElement( "lesson" )
                    .addAttribute( "name",newLessonName.getText())
                    .addAttribute( "chapters", chapter );
    saveAndClose();
  }
  //add image to cards
  @FXML
  private void addImage() throws IOException{
     Stage stage=new Stage();
     FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(prop.getProperty("new.filechooser.title"));
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.getExtensionFilters().addAll(
        new FileChooser.ExtensionFilter("ALL", "*.*"),
        new FileChooser.ExtensionFilter("JPG", "*.jpg"),
        new FileChooser.ExtensionFilter("GIF", "*.gif"),
        new FileChooser.ExtensionFilter("BMP", "*.bmp"),
        new FileChooser.ExtensionFilter("PNG", "*.png"));
        newImageSelect = fileChooser.showOpenDialog(stage);
        newImage.setDisable(false);
        newImage.setText(newImageSelect.getName());
        newImage.setDisable(true);
        source=Paths.get(newImageSelect.getAbsolutePath());
        target=Paths.get("src/leitnerbox/lessons",newLessonName.getText(),"images",newImageSelect.getName());
  }
  private void dialog(){
       TextInputDialog dialog = new TextInputDialog();
                            dialog.setTitle(prop.getProperty("caution"));
                            dialog.setHeaderText(prop.getProperty("new.dialog.head"));
                            dialog.setContentText(prop.getProperty("new.dialog.text"));
                            Optional<String> result = dialog.showAndWait();
                            if (result.isPresent()){  
                               
                  String extention=newImageSelect.getName().substring(newImageSelect.getName().lastIndexOf(".")+1,newImageSelect.getName().length());
                   newFileName=result.get()+"."+extention;
                  String image="src/leitnerbox/lessons/"+newLessonName.getText()+"/images/"+result.get()+"."+extention;
                  newTarget=Paths.get(image);
                  if(newTarget.toFile().exists()){
                      dialog();
                  }
                            }else{
                    dialog.close();
                            }
  }
//  Create Card
  @FXML
  private void addCard() throws IOException{     
           if(newCardBackside.getText().isEmpty()|| newCardFrontside.getText().isEmpty()){
            Alert alert1=new Alert(AlertType.WARNING);
            alert1.setTitle(prop.getProperty("caution"));
            alert1.setHeaderText(prop.getProperty("new.emptyfield.head"));
            alert1.setContentText(prop.getProperty("new.emptyfield.text"));
            alert1.showAndWait();    
            
            }else if(newImageSelect!=null){
                 if(target.toFile().exists()){
                dialog();
                 Files.copy(source,newTarget,StandardCopyOption.COPY_ATTRIBUTES);         
            root.addElement("card")
                         .addAttribute("id",String.valueOf(i))
                         .addAttribute("frontside", newCardFrontside.getText())
                         .addAttribute("backside", newCardBackside.getText())
                         .addAttribute("chapter", newCardChapter.getSelectionModel().getSelectedItem())
                         .addAttribute("level", "0")
                         .addAttribute("expiredtime",null)
                         .addAttribute("testhit", "0")
                         .addAttribute("img",newFileName)
                         .addAttribute("status","unlearned");
                    newImageSelect=null;                  
                    newImage.clear();
                    newCardBackside.clear();
                    newCardFrontside.clear();
                    newSaveButton.setDisable(false);
                    newAllcardButton.setDisable(false);
                     i+=1;
                    }
                else{
                    Files.copy(source,target,StandardCopyOption.COPY_ATTRIBUTES);         
            root.addElement("card")
                         .addAttribute("id",String.valueOf(i))
                         .addAttribute("frontside", newCardFrontside.getText())
                         .addAttribute("backside", newCardBackside.getText())
                         .addAttribute("chapter", newCardChapter.getSelectionModel().getSelectedItem())
                         .addAttribute("level", "0")
                         .addAttribute("expiredtime",null)
                         .addAttribute("testhit", "0")
                         .addAttribute("img",newImageSelect.getName())
                         .addAttribute("status","unlearned");
                    newImageSelect=null;                  
                    newImage.clear();
                    newCardBackside.clear();
                    newCardFrontside.clear();
                    newSaveButton.setDisable(false);
                    newAllcardButton.setDisable(false);
                     i+=1; 
                 }
            }
                 else{
            String imagePath=""; 
             
            root.addElement("card")
                         .addAttribute("id",String.valueOf(i))
                         .addAttribute("frontside", newCardFrontside.getText())
                         .addAttribute("backside", newCardBackside.getText())
                         .addAttribute("chapter", newCardChapter.getSelectionModel().getSelectedItem())
                         .addAttribute("level", "0")
                         .addAttribute("expiredtime",null)
                         .addAttribute("testhit", "0")
                         .addAttribute("img",imagePath)
                         .addAttribute("status","unlearned");
                    newImage.clear();
                    newCardBackside.clear();
                    newCardFrontside.clear();
                    newSaveButton.setDisable(false); 
                    newAllcardButton.setDisable(false);
                    i+=1;
            }
            newSaveButton.setDisable(false);           
  }
  
  //see All Card 
  @FXML
  private void seeAllCard() throws IOException, DocumentException{
      FXMLLoader fxmlLoader=new FXMLLoader(getClass().getResource("view/allCard.fxml"),resource);
      Parent root=fxmlLoader.load();
      AllCardController controller=fxmlLoader.<AllCardController>getController();
      controller.fillList(document);
      controller.setLabel(lessonName);
      Scene scene=new Scene(root);
      Stage stage=new Stage();
      stage.setScene(scene);
      stage.getIcons().add(new Image(getClass().getResource("image/logo.png").toString()));
      stage.setTitle(prop.getProperty("allcard"));
      stage.setX(stage.getX()-5);
      stage.setY(stage.getY());
      stage.initModality(Modality.WINDOW_MODAL);
      stage.initOwner(newAllcardButton.getScene().getWindow());
      stage.setOnCloseRequest((WindowEvent event)->{
        NewController nc=new NewController();
          try { 
              nc.saveChange(AllCardController.changeList,document);
          } catch (IOException | DocumentException ex) {
              Logger.getLogger(NewController.class.getName()).log(Level.SEVERE, null, ex);
          }
        });
      stage.show();                                                     
  }
  //Come Back To Home
  @FXML
  private void home() throws IOException{
      if (!newSaveButton.isDisable()) {
       Alert alert=new Alert(AlertType.CONFIRMATION);
                 alert.setTitle(prop.getProperty("caution"));
                 alert.setHeaderText(prop.getProperty("home"));
                 alert.setContentText(prop.getProperty("new.alert.savelesson"));
                 ButtonType ok = new ButtonType(prop.getProperty("ok"));
                 ButtonType no = new ButtonType(prop.getProperty("no"));
                 ButtonType cancel = new ButtonType(prop.getProperty("cancel"));
                 alert.getButtonTypes().setAll(ok,no,cancel);
                 Optional<ButtonType> result = alert.showAndWait();
                 if (result.get()==ok) {
                     save();
                   Parent root=FXMLLoader.load(getClass().getResource("view/main.fxml"),resource);      
                     Stage stage=(Stage)home.getScene().getWindow();
                     Scene scene = null;
                     if(stage.isMaximized()){        
                      scene=new Scene(root,stage.getWidth(),stage.getHeight());
                     }else{
                       scene=new Scene(root);  
                     }
                     stage.setScene(scene);
                     stage.getIcons().add(new Image(getClass().getResource("image/logo.png").toString()));
                     stage.setTitle(prop.getProperty("home"));
                     stage.show();
          }else if (result.get()==no) {
              File file=new File("src/leitnerbox/lessons/"+newLessonName.getText());
                     deleteLesson(file);
                   Parent root=FXMLLoader.load(getClass().getResource("view/main.fxml"),resource);         
                     Stage stage=(Stage)home.getScene().getWindow();
                     Scene scene = null;
                     if(stage.isMaximized()){        
                      scene=new Scene(root,stage.getWidth(),stage.getHeight());
                     }else{
                       scene=new Scene(root);  
                     }
                     stage.setScene(scene);
                     stage.getIcons().add(new Image(getClass().getResource("image/logo.png").toString()));
                     stage.setTitle(prop.getProperty("home"));
                     stage.show();  
                     stage.setOnCloseRequest(null);
          }else if(result.get()==cancel){
              alert.close();
          }
      }
      else{
         Parent root=FXMLLoader.load(getClass().getResource("view/main.fxml"),resource);         
                     Stage stage=(Stage)home.getScene().getWindow();
                     Scene scene = null;
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
     
  } 
  private void deleteLesson(File file){
     if(file!=null){
         File[] contents = file.listFiles();
         for (File content : contents) {
            if(content.isDirectory()) {
                deleteLesson(content);
            }
            content.delete();
         } 
     } 
     if(file!=null){
     file.delete();
     }
  }
}
     
        
    

