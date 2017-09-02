/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package leitnerbox;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.ResourceBundle;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
/**
 *
 * @author ma
 */
public class LeitnerBox extends Application {
   static Properties prop = new Properties(); 
   static Properties config = new Properties();
   static ResourceBundle resource;
    @Override
    public void start(Stage stage) throws Exception {     
       File configSource=new File("src/leitnerbox/config.properties");
        FileInputStream in=new FileInputStream(configSource);
        config.load(in);
        in.close();
        File properties=new File("src/leitnerbox/bundles/"+config.getProperty("lang")+".properties");
        FileInputStream inProp=new FileInputStream(properties);
        prop.load(inProp);
        inProp.close();
        String rb="leitnerbox.bundles."+config.getProperty("lang");
        resource=ResourceBundle.getBundle(rb);
        Parent root = FXMLLoader.load(getClass().getResource("view/main.fxml"),resource);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.getIcons().add(new Image(getClass().getResource("image/logo.png").toString()));
        stage.setTitle(prop.getProperty("home"));
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
