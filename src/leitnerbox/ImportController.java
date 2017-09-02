/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package leitnerbox;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import static leitnerbox.LeitnerBox.config;
import static leitnerbox.LeitnerBox.prop;
import static leitnerbox.LeitnerBox.resource;
import org.xml.sax.SAXException;


/**
 * FXML Controller class
 *
 * @author ma
 */
public class ImportController implements Initializable {
    @FXML
    private AnchorPane background;
    @FXML
    private TextField importPath;
    @FXML
    private Button importChooseFileButton;
    @FXML
    private Button importButton;
    private File importFile;
    private File imageDir;
    private Path source;
    private String fileName;
    private String destDir;
    private File newFile;
    private boolean newXmlFile=false;
    private String newXmlName;
    private String message;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
         background.setStyle(config.getProperty("color"));
    }
    @FXML
    private void importFile() throws IOException{
        Stage stage=new Stage();
        FileChooser fileChooser=new FileChooser();
         fileChooser.setTitle(prop.getProperty("import.filechooser.title"));
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.getExtensionFilters().addAll(
        new FileChooser.ExtensionFilter("ZIP", "*.zip"));
        importFile = fileChooser.showOpenDialog(stage);
        if(importFile!=null){
        importPath.setText(importFile.getName());
        }
        
    }
    @FXML
    private void importZip() throws IOException, FileNotFoundException, SAXException, TransformerException, TransformerConfigurationException, ParserConfigurationException, InterruptedException{
    unZip();
    Parent root=FXMLLoader.load(getClass().getResource("view/main.fxml"),resource);         
                     Scene scene=new Scene(root);
    Stage stage=(Stage) importButton.getScene().getWindow();
    stage.setOnHidden(event->{
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(ImportController.class.getName()).log(Level.SEVERE, null, ex);
        }
        Stage mainStage = (Stage) stage.getUserData();
        mainStage.setScene(scene);
        mainStage.show();
    });
    stage.close();
}
    private void unZip() throws FileNotFoundException, IOException, SAXException, TransformerException, TransformerConfigurationException, ParserConfigurationException{
      if(importFile!=null) {
        byte[] buffer = new byte[1024];
        String name=importFile.getName();
        fileName=name.substring(0,name.lastIndexOf("."));
        destDir="src/leitnerbox/lessons";
        newFile = new File(destDir + File.separator +fileName);
        imageDir=new File(destDir +File.separator +fileName+File.separator+"images"); 
        ZipFile zis=new ZipFile(importFile);
        Enumeration e=zis.entries();
        while(e.hasMoreElements()){
           ZipEntry ze=(ZipEntry) e.nextElement();
           if(ze.getName().endsWith(".xml")){   
               try (InputStream in = zis.getInputStream(ze)) { 
                   boolean validateXml = validateXml(in); 
                    if(validateXml==true){
                      if(!newFile.exists()){
                        newFile.mkdirs();
                        imageDir.mkdirs();
                        System.out.println(imageDir);
                        }else{
                            dialog();
                         }
               File xmlFile;
               if(newXmlFile==true){  
                    xmlFile = new File(newFile+File.separator+newXmlName+".xml"); 
               }else{
                    xmlFile = new File(newFile+File.separator+ze.getName());   
               }
               xmlFile.getParentFile().mkdirs();
               
                   InputStream inXml = zis.getInputStream(ze);
                   FileOutputStream fos = new FileOutputStream(xmlFile);
                   int len;
                   while ((len = inXml.read(buffer))>0 ) {
                       fos.write(buffer, 0, len);
                   }
                 fos.close();
                 inXml.close();
               }
               else{
                   Alert alert=new Alert(Alert.AlertType.ERROR);
                   alert.setHeaderText(prop.getProperty("import.alert.error.head"));
                   alert.setContentText(prop.getProperty("import.alert.error.text")+message);
                   alert.showAndWait();
                   break;
               }
               }  
           }
           else {
               try (InputStream in = zis.getInputStream(ze)) {
                   File dir=new File(newFile+File.separator+ze.getName());
                   dir.getParentFile().mkdirs();
                   FileOutputStream fos = new FileOutputStream(dir);
                   int len;
                   while ((len = in.read(buffer)) > 0) {
                       fos.write(buffer, 0, len);
                   }
                   fos.close();
               }          
           } 
        }  
           zis.close();
      }    
    }
    private void dialog(){
        TextInputDialog dialog = new TextInputDialog();
                            dialog.setTitle(prop.getProperty("caution"));
                            dialog.setHeaderText(prop.getProperty("import.dialog.head"));
                            dialog.setContentText(prop.getProperty("import.dialog.text"));
                            Optional<String> result = dialog.showAndWait();
                            if (result.isPresent()){
                                newXmlFile=true;
                                newXmlName=result.get();
                            newFile=new File(destDir + File.separator +result.get());
                            imageDir=new File(destDir +File.separator +result.get()+File.separator+"images"); 
                            if(newFile.exists()){
                                  dialog();
                              }else{
                                newFile.getParentFile().mkdirs();
                                imageDir.mkdirs();
                                System.out.println(imageDir);
                              }
    }else{
                    dialog.close();
                            }       
    }
    private boolean validateXml(InputStream in) throws SAXException, IOException, TransformerConfigurationException, TransformerException, ParserConfigurationException{
    try
    {
        SchemaFactory factory = 
            SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = factory.newSchema(new StreamSource(new File("src/leitnerbox/sample.xsd")));
        Validator validator = schema.newValidator();
        validator.validate(new StreamSource(in));
        return true;
    }
    catch(Exception ex)
    {
        message=ex.getMessage();
        return false;
    }
   } 
}


