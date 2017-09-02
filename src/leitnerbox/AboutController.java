/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package leitnerbox;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import static leitnerbox.LeitnerBox.config;

/**
 * FXML Controller class
 *
 * @author ma
 */
public class AboutController implements Initializable {

    @FXML
    private BorderPane background;
    @FXML
    private Button aboutButton;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
         background.setStyle(config.getProperty("color"));
    }    
    
}
