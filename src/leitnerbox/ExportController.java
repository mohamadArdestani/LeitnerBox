/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package leitnerbox;

import com.lowagie.text.DocumentException;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import static leitnerbox.LeitnerBox.config;
import static leitnerbox.LessonController.ob;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import static leitnerbox.LessonController.file;
import static leitnerbox.LessonController.chapters;

/**
 * FXML Controller class
 *
 * @author ma
 */
public class ExportController implements Initializable {
    @FXML
    private BorderPane background;
    @FXML
    private Label exportLabel;
    @FXML
    private Label exportMessageLabel;
    @FXML
    private ChoiceBox<String> exportList;
    @FXML
    private Button exportButton;
    String fileName;
    List<File> listItems=new ArrayList();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        background.setStyle(config.getProperty("color"));
        fileName=LessonController.file;
        String[] items={"CSV","TXT","PDF","RAW Xml","LEITNERBOX LESSON"};
        exportList.getItems().addAll(items);
        exportList.getSelectionModel().selectFirst();
    }    
    @FXML
    void export() throws IOException, DocumentException{
        Stage stage=new Stage(); 
        String item=exportList.getValue();
        if(item=="CSV"){
             FileChooser filechooser1=new FileChooser();
        filechooser1.setInitialDirectory(new File(System.getProperty("user.home")));
        filechooser1.getExtensionFilters().add(
        new FileChooser.ExtensionFilter("csv", "*.csv"));
        filechooser1.setInitialFileName(fileName);
        File fileCsv=filechooser1.showSaveDialog(stage);
            if(fileCsv!=null){
            exportCsv(fileCsv);
            }
        }else if(item=="TXT"){
        FileChooser filechooser2=new FileChooser();
        filechooser2.setInitialDirectory(new File(System.getProperty("user.home")));
        filechooser2.setInitialFileName(fileName);
        filechooser2.getExtensionFilters().add(
        new FileChooser.ExtensionFilter("txt", "*.txt"));
        filechooser2.setInitialFileName(fileName);
        File fileTxt=filechooser2.showSaveDialog(stage);
            if(fileTxt!=null){
            exportTxt(fileTxt);
            }
        }else if(item=="PDF"){
             FileChooser filechooser3=new FileChooser();
        filechooser3.setInitialDirectory(new File(System.getProperty("user.home")));
        filechooser3.setInitialFileName(fileName);
        filechooser3.getExtensionFilters().add(
        new FileChooser.ExtensionFilter("pdf", "*.pdf"));
        filechooser3.setInitialFileName(fileName);
        File filePdf=filechooser3.showSaveDialog(stage);
            if(filePdf!=null){
            exportPdf pdf=new exportPdf();
            pdf.createPdf(ob, filePdf,fileName);
            }
        }else if(item=="RAW Xml"){
        FileChooser filechooser4=new FileChooser();
        filechooser4.setInitialDirectory(new File(System.getProperty("user.home")));
        filechooser4.setInitialFileName(fileName);
        filechooser4.getExtensionFilters().add(
        new FileChooser.ExtensionFilter("raw xml", "*.xml"));
        filechooser4.setInitialFileName(fileName);
        File fileXml=filechooser4.showSaveDialog(stage);
            if(fileXml!=null){
            exportXml(fileXml);
            }
        }
        else if(item=="LEITNERBOX LESSON"){
        FileChooser filechooser5=new FileChooser();
        filechooser5.setInitialDirectory(new File(System.getProperty("user.home")));
        filechooser5.setInitialFileName(fileName);
        filechooser5.getExtensionFilters().add(
        new FileChooser.ExtensionFilter("LEITNERBOX LESSON", "*.zip"));
        File fileXml=filechooser5.showSaveDialog(stage); 
        if(fileXml!=null){
            exportLeitnerBox(fileXml);
            }
        }
        Stage exportStage=(Stage)exportButton.getScene().getWindow();
        exportStage.close();
    }
    private void exportLeitnerBox(File ExportFile) throws FileNotFoundException, IOException{
        byte[] buffer = new byte[1024];
        FileOutputStream out=new FileOutputStream(ExportFile);
        ZipOutputStream zos=new ZipOutputStream(new BufferedOutputStream(out));
        String lessonName=ExportFile.getName().substring(0,ExportFile.getName().lastIndexOf("."));
         ZipEntry ze= new ZipEntry(lessonName+".xml");
                try {
                    zos.putNextEntry(ze);
                } catch (IOException ex) {
                    Logger.getLogger(ExportController.class.getName()).log(Level.SEVERE, null, ex);
                }
               
                  String leitnerboxXml = leitnerboxXml();
                
                FileInputStream  inxml=new FileInputStream(leitnerboxXml);
                     int le;
    		while ((le = inxml.read(buffer)) >= 0) {
                   zos.write(buffer,0, le);
               
                }
                inxml.close();
        listFile(new File("src/leitnerbox/lessons/"+file+"/images"));
        if(listItems!=null){
            listItems.forEach((File element)->{
                try {
                    zos.putNextEntry(new ZipEntry("images/"+element.getName()));
                } catch (IOException ex) {
                    Logger.getLogger(ExportController.class.getName()).log(Level.SEVERE, null, ex);
                }
                try {  
                    FileInputStream  in=new FileInputStream(element.getAbsoluteFile());
                     int len;
    		while ((len = in.read(buffer)) >= 0) {
                   zos.write(buffer,0, len);
               
                }
               in.close(); 
                try {
                    zos.closeEntry();
                } catch (IOException ex) {
                    Logger.getLogger(ExportController.class.getName()).log(Level.SEVERE, null, ex);
                }
        
            }   catch (FileNotFoundException ex) {
                    Logger.getLogger(ExportController.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(ExportController.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
                zos.close();
                
    }
    }
    private void listFile(File source){
        File[] list=source.listFiles();
        if(list!=null){
        for (File item : list) {
            if(item.isFile()){
                listItems.add(item);
            }else{
                listFile(item);
            }
        }
        }
    }
    private void exportCsv(File file) throws IOException {
        StringBuilder sb=new StringBuilder();
        ob.stream().forEach((Card card)->{
        sb.append(card.getFrontside());
        sb.append(",");
        sb.append(card.getBackside());
        sb.append("\n");
        });
        try (Writer out = new BufferedWriter(new OutputStreamWriter(
			new FileOutputStream(file), "UTF8"))) {
            out.write(sb.toString());
        }
    }
    private void exportTxt(File file) throws IOException {
        StringBuilder sb=new StringBuilder();
        ob.stream().forEach((Card card)->{
        sb.append(card.getFrontside());
        sb.append(":");
        sb.append(card.getBackside());
        sb.append(" ");
        sb.append("\n");
        });
        try (Writer out = new BufferedWriter(new OutputStreamWriter(
			new FileOutputStream(file), "UTF8"))) {
            out.write(sb.toString());
        }
    }
    private void exportXml(File file) throws IOException {
       org.dom4j.Document document=DocumentHelper.createDocument();
         Element LessonNode=document.addElement("lesson");
        for (Card card : ob) {
           Element node=LessonNode.addElement("card");
            node.addElement("frontSide")
                    .addText(card.getFrontside());
            node.addElement("backSide")
                    .addText(card.getBackside());
        }
        try (Writer out = new BufferedWriter(new OutputStreamWriter(
			new FileOutputStream(file), "UTF8"))) {
            document.write(out);
        }
    
}
    private String leitnerboxXml() throws IOException{
         org.dom4j.Document document=DocumentHelper.createDocument();
         Element LessonNode=document.addElement("lesson");
                 LessonNode.addAttribute("name", file)
                         .addAttribute("chapters",chapters);
        for (Card card : ob) {
           Element node=LessonNode.addElement("card");
                   node.addAttribute("id",card.getId());
                   node.addAttribute("frontside",card.getFrontside());
                   node.addAttribute("backside",card.getBackside());
                   node.addAttribute("chapter",card.getChapter());
                   node.addAttribute("testhit","0");
                   node.addAttribute("level","0");
                   if(!card.getImg().isEmpty()){
                       node.addAttribute("img",card.getImg());
                   }
                   
        }
        File temp=File.createTempFile("rawLitnerBox","xml");
        temp.deleteOnExit();
        try (Writer out = new BufferedWriter(new OutputStreamWriter(
			new FileOutputStream(temp), "UTF8"))) {
            document.write(out);
        }
        return temp.getAbsolutePath();
    }
}
