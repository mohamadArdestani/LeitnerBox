/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package leitnerbox;

import com.github.junrar.Archive;
import com.github.junrar.exception.RarException;
import com.github.junrar.impl.FileVolumeManager;
import com.github.junrar.rarfile.FileHeader;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.jsoup.Jsoup;
import static leitnerbox.LeitnerBox.config;
import static leitnerbox.LeitnerBox.prop;
/**
 * FXML Controller class
 *
 * @author ma
 */
public class ConvertController implements Initializable {
    @FXML
    private BorderPane background;
    @FXML
    private TextField convertPath;
    @FXML
    private Button convertButton;
    private File importFile;
    String xmlPat = null;
    boolean imageStatus=false;
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
         fileChooser.setTitle(prop.getProperty("convert.filechooser.title"));
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.getExtensionFilters().addAll(
        new FileChooser.ExtensionFilter("JML", "*.jml"),
        new FileChooser.ExtensionFilter("Jmemorize in RAR archive", "*.rar"),
        new FileChooser.ExtensionFilter("Jmemorize in ZIP archive", "*.zip"),
        new FileChooser.ExtensionFilter("anki vocabulary", "*.apkg"));
        importFile = fileChooser.showOpenDialog(stage);
        if(importFile!=null){
        convertPath.setText(importFile.getName());
        }    
    }
    @FXML
    private void convert() throws IOException, DocumentException, RarException{
        if(importFile!=null){
        Stage stage=(Stage)convertButton.getScene().getWindow();
        byte[] buffer = new byte[1024];
        String name="LT_"+importFile.getName().substring(0,importFile.getName().lastIndexOf("."));
        File tempFile=new File(importFile.getParent()+"/"+name+".zip");
        FileOutputStream out=new FileOutputStream(tempFile);
        ZipOutputStream zos=new ZipOutputStream(new BufferedOutputStream(out));
        String xmlPath = null; 
        tempFile.getParentFile().mkdir();
        String convert=importFile.getName();
        String extention=convert.substring(convert.lastIndexOf(".")+1,convert.length()).toLowerCase();
        switch(extention){
            case "jml":
             try{
                ZipFile zp=new ZipFile(importFile);
                Enumeration e=zp.entries();
                
                while (e.hasMoreElements()) {
                    ZipEntry ze = (ZipEntry) e.nextElement();
                     if(ze.getName().endsWith(".xml")){
                         InputStream in=zp.getInputStream(ze);
                        List<Card> xml = convertXml(in);
                        if(xml!=null){
                         xmlPath = createNewXml(xml,name);
                        }
                        ZipEntry newZe=new ZipEntry(name+".xml");
                     zos.putNextEntry(newZe);
                        InputStream inXml=new FileInputStream(xmlPath); 

                           int len;
                           while ((len = inXml.read(buffer))>0 ) {
                               zos.write(buffer, 0, len);
                           }
                         zos.closeEntry();
                         inXml.close();
                         in.close();
                         Path temp=Paths.get(xmlPath);
                         Files.deleteIfExists(temp);
                     }else if(ze.getName().startsWith("images")){
                         try (InputStream in = zp.getInputStream(ze)) {
                        zos.putNextEntry(ze);
                           int len;
                           while ((len = in.read(buffer)) > 0) {
                               zos.write(buffer, 0, len);
                           }
                           zos.closeEntry();
                       }          
                     }

                }
                }catch(IOException | DocumentException ex){
                    FileInputStream fis = new FileInputStream(importFile);
                     List<Card> xml = convertXml(fis);
                        if(xml!=null){
                         xmlPath = createNewXml(xml,name);
                        }
                        ZipEntry newZe=new ZipEntry(name+".xml");
                     zos.putNextEntry(newZe);
                        InputStream inXml=new FileInputStream(xmlPath);
                           int len;
                           while ((len = inXml.read(buffer)) > 0) {
                               zos.write(buffer, 0, len);
                           }
                           zos.close();
                           inXml.close();
                           fis.close();
                           out.close();
                           Path temp=Paths.get(xmlPath);
                           Files.deleteIfExists(temp);
                }
             zos.close();
                break;
            case "zip":
                Path f=null;
                try{  
                
                ZipFile zp=new ZipFile(importFile);
                Enumeration e=zp.entries();
                while(e.hasMoreElements()){                   
                    ZipEntry ze=(ZipEntry) e.nextElement();
                    if(ze.getName().endsWith(".jml")){
                        f=Files.createTempFile("jml",".jml");
                        FileOutputStream outJml=new FileOutputStream(f.toFile()); 
                        InputStream jml=zp.getInputStream(ze);
                         int lenj;
                           while ((lenj = jml.read(buffer))>0 ) {
                               outJml.write(buffer, 0, lenj);
                           }
                           outJml.close();
                           jml.close();
                        ZipFile zpjml=new ZipFile(f.toFile());
                     zpjml.stream().forEach((jmlEntry)->{
                     if(jmlEntry.getName().endsWith(".xml")){
                        try {
                            InputStream in = null;
                            try {
                                in = zpjml.getInputStream(jmlEntry);
                            } catch (IOException ex) {
                                Logger.getLogger(ConvertController.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            List<Card> xml = convertXml(in);
                            if(xml!=null){
                                 xmlPat = createNewXml(xml,name);
                            }
                            ZipEntry newZe=new ZipEntry(name+".xml");
                            zos.putNextEntry(newZe);
                            InputStream inXml=new FileInputStream(xmlPat);
                            
                            int len;
                            while ((len = inXml.read(buffer))>0 ) {
                                zos.write(buffer, 0, len);
                            }
                            zos.closeEntry();
                            inXml.close();
                            in.close();
                            
                        } catch (DocumentException ex) {
                            Logger.getLogger(ConvertController.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (IOException ex) {
                            Logger.getLogger(ConvertController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                     }else if(ze.getName().startsWith("images")){
                         try (InputStream in = zp.getInputStream(ze)) {
                        zos.putNextEntry(ze);
                           int len;
                           while ((len = in.read(buffer)) > 0) {
                               zos.write(buffer, 0, len);
                           }
                           zos.closeEntry();
                       } catch (IOException ex) {          
                            Logger.getLogger(ConvertController.class.getName()).log(Level.SEVERE, null, ex);
                        }          
                     }

                });
                     
                    }
                    else if(ze.getName().endsWith(".xml")){
                     InputStream inFile=zp.getInputStream(ze);  
                        List<Card> xml = convertXml(inFile);
                        if(xml!=null){
                         xmlPath = createNewXml(xml,ze.getName());
                        }
                        ZipEntry newZe=new ZipEntry(ze.getName().substring(0,ze.getName().lastIndexOf("."))+".xml");
                     zos.putNextEntry(newZe);
                        try (InputStream inXml = new FileInputStream(xmlPath)) {
                            int len;
                            while ((len = inXml.read(buffer))>0 ) {
                                zos.write(buffer, 0, len);
                            }
                        }  
                        zos.closeEntry();    
                    }else if(ze.getName().startsWith("images")){
                        ZipEntry newZe=new ZipEntry(ze.getName());
                     zos.putNextEntry(newZe);
                        InputStream inFile=zp.getInputStream(ze);
                       int len;
                           while ((len = inFile.read(buffer))>0 ) {
                               zos.write(buffer, 0, len);
                           } 
                           zos.closeEntry();
                           inFile.close();
                    }
                }
                         zos.close();
                         if(xmlPath!=null){
                         Path temp=Paths.get(xmlPath);
                         Files.deleteIfExists(temp);
                         
                         }
                }
                catch(Exception ex){
                        System.out.println(ex.getMessage());
                        ex.printStackTrace();
                    }finally{
                Path tem=Paths.get(xmlPat);
                Files.deleteIfExists(tem);
                }
                break;           
            case "rar": 
                String filename = importFile.getAbsolutePath();
		File f1 = new File(filename);
		Archive a = null;
		try {
			a = new Archive(new FileVolumeManager(f1));
		} catch (RarException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (a != null) {
			FileHeader fh = a.nextFileHeader();
			while (fh != null) {
			
                            if(fh.getFileNameString().endsWith(".jml")){
                                        Path tempJml=Files.createTempFile("jml",".jml");
                                        InputStream in;
                                try (FileOutputStream outJml = new FileOutputStream(tempJml.toFile())) {
                                    in = a.getInputStream(fh);
                                    int lenj;
                                    while ((lenj = in.read(buffer))>0 ) {
                                        outJml.write(buffer, 0, lenj);
                                    }
                                }
                                         in.close();
                                      ZipFile zpjml=new ZipFile(tempJml.toFile());
                                       zpjml.stream().forEach((jmlEntry)->{ 
                                       if(jmlEntry.getName().endsWith(".xml")){
                                    try {
                                        InputStream x = null;
                                        try {
                                            x = zpjml.getInputStream(jmlEntry);
                                        } catch (IOException ex) {
                                            Logger.getLogger(ConvertController.class.getName()).log(Level.SEVERE, null, ex);
                                        }
                                        List<Card> xml = convertXml(x);
                                        if(xml!=null){
                                             xmlPat = createNewXml(xml,name);
                                        }
                                        ZipEntry newZe=new ZipEntry(name+".xml");
                                        zos.putNextEntry(newZe);
                                        InputStream inXml=new FileInputStream(xmlPat);

                                        int len;
                                        while ((len = inXml.read(buffer))>0 ) {
                                            zos.write(buffer, 0, len);
                                        }
                                        zos.closeEntry();
                                        inXml.close();
                                        x.close();
                                        Path temp=Paths.get(xmlPat);
                                        Files.deleteIfExists(temp);
                                    } catch (DocumentException ex) {
                                        Logger.getLogger(ConvertController.class.getName()).log(Level.SEVERE, null, ex);
                                    } catch (IOException ex) {
                                        Logger.getLogger(ConvertController.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                 }else if(jmlEntry.getName().startsWith("images")){
                                    
                                     InputStream inImag = null;
                                           try {
                                               inImag = zpjml.getInputStream(jmlEntry);
                                           } catch (IOException ex) {
                                               Logger.getLogger(ConvertController.class.getName()).log(Level.SEVERE, null, ex);
                                           }
{
                                               try {
                                                   ZipEntry newZe=new ZipEntry(jmlEntry.getName());
                                                   zos.putNextEntry(newZe);
                                                   int len;
                                                   while ((len = inImag.read(buffer)) > 0) {
                                                       zos.write(buffer, 0, len);
                                                   }
                                                   
                                                   zos.closeEntry();
                                                   inImag.close();
                                               } catch (IOException ex) {
                                                   Logger.getLogger(ConvertController.class.getName()).log(Level.SEVERE, null, ex);
                                               }
                                   }          
                                 }

                            });

                    
                            }
                        else if(fh.getFileNameString().endsWith(".xml")){
                                        InputStream in=a.getInputStream(fh);
                                        List<Card> xml = convertXml(in);
                                        if(xml!=null){
                                         xmlPath = createNewXml(xml,name);
                                        }
                                        ZipEntry newZe=new ZipEntry(name+".xml");            
                                        zos.putNextEntry(newZe);
                                        InputStream inXml = new FileInputStream(xmlPath);
                                           int len;
                                           while ((len = inXml.read(buffer))>0 ) {
                                               zos.write(buffer, 0, len);
                                           }
                                           in.close();
                                           zos.closeEntry();
                                           inXml.close();
                                        }
                        else if(fh.getFileNameString().startsWith("images")){
                           
                                         ZipEntry newZe=new ZipEntry(fh.getFileNameString());
                                         zos.putNextEntry(newZe);
                                           InputStream inFile=a.getInputStream(fh);
                                            int len;
                                              while ((len = inFile.read(buffer))>0 ) {
                                                zos.write(buffer, 0, len);
                                            }
                           
                                            inFile.close();
                                            zos.closeEntry();
                    
                                    }
                                
				fh = a.nextFileHeader();
                               
			}
                   zos.close(); 
		}     
                break;
            case "apkg":
              try{
               Map<String, String> map = new HashMap<>();
               Map<String, String> map1 = new HashMap<>();
               Path collection = null;
               Path media = null;
               ObservableMap<String, String> jsonPairs=FXCollections.observableMap(map1);
               List<String> images=new ArrayList();
               ObservableMap<String, String> imgFile=FXCollections.observableMap(map);
                ZipFile zp=new ZipFile(importFile);
                Enumeration e=zp.entries();
                while(e.hasMoreElements()){ 
                    ZipEntry ze=(ZipEntry) e.nextElement();
                    if(ze.getName().startsWith("collection")){
                        collection=Files.createTempFile("collection",".anki");
                        FileOutputStream outCollection=new FileOutputStream(collection.toFile());
                        InputStream in=zp.getInputStream(ze);
                         int lenj;
                           while ((lenj = in.read(buffer))>0 ) {
                               outCollection.write(buffer, 0, lenj);
                           }
                           in.close();
                           outCollection.close();
                           
                    }else if(ze.getName().startsWith("media")){
                       media=Files.createTempFile("media","x"); 
                        FileOutputStream outMedia=new FileOutputStream(media.toFile());
                        InputStream in=zp.getInputStream(ze);
                         int lenj;
                           while ((lenj = in.read(buffer))>0 ) {
                               outMedia.write(buffer, 0, lenj);
                           }
                           in.close();
                           outMedia.close();
                           
                    }    
                    }
                    if(collection!=null){
                    List<Card> notes = ankiConvert(collection.toFile());
                    notes.forEach((Card card)->{
                        images.add(card.getImg());
                    });
                        String newXml = createNewXml(notes,name);
                        ZipEntry newZe=new ZipEntry(name+".xml");
                        zos.putNextEntry(newZe);
                        InputStream in=new FileInputStream(newXml);
                        int len;
                        while ((len = in.read(buffer))>0 ) {
                           zos.write(buffer, 0, len);
                           }
                        zos.closeEntry();
                        in.close();
                       Files.deleteIfExists(collection);
                       Path x=Paths.get(newXml);
                       Files.deleteIfExists(x);
                    }
                    if(media.toFile().exists()){
                     jsonPairs = getFileFromJson(media.toFile());
                        for (String key : images) {
                            if(jsonPairs.containsKey(key)){
                                String v=jsonPairs.get(key);
                                imgFile.put(v,key);
                        }
                         Files.deleteIfExists(media); 
                    }
                    ZipFile zp2=new ZipFile(importFile);
                    Enumeration e2=zp2.entries();
                    while(e2.hasMoreElements()){
                        ZipEntry ze2=(ZipEntry) e2.nextElement();
                        if(imgFile.containsKey(ze2.getName())){
                          ZipEntry newZe=new ZipEntry("images/"+imgFile.get(ze2.getName())); 
                          InputStream in=zp.getInputStream(ze2);
                          zos.putNextEntry(newZe);
                          int len;
                          while((len=in.read(buffer))>0){
                            zos.write(buffer, 0, len);  
                          }
                          zos.closeEntry();
                          in.close();
                        }
                    }
                    }
                zos.close();
              }catch(Exception e){
                  e.printStackTrace();
              }finally{
               zos.close();  
              }
               
                break;
        }
        stage.close();
        }   
    }
    private List<Card> ankiConvert(File ankiLesson) throws UnsupportedEncodingException{
        int i=1;
        List<Card> notes=new ArrayList();
         Connection conn = null;
         Statement query=null;
        try {
            // db parameters
            String url = "jdbc:sqlite:"+ankiLesson.getAbsolutePath();
            // create a connection to the database
            try{
            conn = DriverManager.getConnection(url);
            }catch(Exception e){
            System.out.println("Connection to SQLite has been established.");
            }
            query=conn.createStatement();
            String sql="SELECT flds,sfld FROM 'notes'";
            ResultSet rs=query.executeQuery(sql);
            while(rs.next()){
                
                org.jsoup.nodes.Document doc =  Jsoup.parse(rs.getString("flds"));
                org.jsoup.nodes.Document doc1 =  Jsoup.parse(rs.getString("sfld"));
                String front=doc1.text();
                String back=doc.text().replaceAll("","").replace(front,"");
                String image=doc.getElementsByTag("img").attr("src");
                Card card=new Card();
                card.setId(String.valueOf(i));
                card.setFrontside(front.trim().replaceAll("(\\[)(.*)|(\\()(.*)|(\\{)(.*)",""));
                card.setBackside(back.trim().replaceFirst("(/.*/)",""));
                card.setLevel("0");
                card.setTesthit("0");
                card.setChapter("All");
                if(image!=null){
                card.setImg(image);
                }
                notes.add(card);
                i++;
            }
            rs.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
        return notes;
    }
    private ObservableMap<String,String> getFileFromJson(File file) throws IOException{
         Map<String, String> map = new HashMap<String, String>();
        ObservableMap<String,String> obj=FXCollections.observableMap(map);
        byte[] bytes = Files.readAllBytes(file.toPath());
        String content=new String(bytes);
        String newContent = content.substring(content.indexOf("{"),content.lastIndexOf("}"));
        List<String> contentList = new ArrayList<>(Arrays.asList(newContent.split(",")));
        if(contentList!=null){
        contentList.forEach((String e)->{
         List<String> pair = new ArrayList<>(Arrays.asList(e.split(":")));
         String key=pair.get(1).trim().replace("\"","");
         String value=pair.get(0).trim().replace("\"","");
         obj.put(key,value);
        });
        }  
        return obj;
    }

    private List<Card> convertXml(InputStream in) throws DocumentException{   
        SAXReader reader=new SAXReader();
       Document document= reader.read(in);
       List<Card> cards=new ArrayList();
       List<Node> nodes=document.selectNodes("//Card");
       if(nodes!=null){
           int i=1; 
            for (Node node : nodes) {
                Card card=new Card();
                String xpath="//Card[@Frontside='"+node.valueOf("@Frontside")+"']"+"/Side/image";
                Node image = document.selectSingleNode(xpath);
                org.jsoup.nodes.Document frontSide=  Jsoup.parse(node.valueOf("@Frontside"));
                org.jsoup.nodes.Document backSide=  Jsoup.parse(node.valueOf("@Backside"));
                card.setFrontside(frontSide.text());
                card.setBackside(backSide.text());
                card.setLevel("0");
                card.setId(String.valueOf(i));
                card.setChapter("All");
                card.setTesthit("0");
                if(image!=null){
                    card.setImg(image.valueOf("@id"));
                }   cards.add(card);
                i++;
                image=null;
            }
        return cards;
       }else{
           return null;
       }
        
    }
    private String createNewXml(List<Card> cards,String name) throws IOException{
        Document document=DocumentHelper.createDocument();
        document.setName(name);
        Element root=document.addElement("lesson");
                root.addAttribute("name",name)
                     .addAttribute("chapters","All");   
        for (Card card : cards) {
            Element node = root.addElement("card")
                    .addAttribute("id",card.getId())
                    .addAttribute("frontside",card.getFrontside())
                    .addAttribute("backside",card.getBackside())
                    .addAttribute("chapter",card.getChapter())
                    .addAttribute("testhit",card.getTesthit())
                    .addAttribute("level",card.getLevel());
                    if(card.getImg()!=null){
                    node.addAttribute("img",card.getImg());
                     }
        }
        File tempFile=File.createTempFile("leitnerBox","xml");
        try (Writer out = new BufferedWriter(new OutputStreamWriter(
			new FileOutputStream(tempFile), "UTF8"))) {
            document.write(out);
        }
        return tempFile.getAbsolutePath();
    }
}
