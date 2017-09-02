/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package leitnerbox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import static leitnerbox.LessonController.file;
import static leitnerbox.LessonController.ob;
import static leitnerbox.LeitnerBox.config;
import static leitnerbox.LeitnerBox.resource;
import static leitnerbox.LeitnerBox.prop;
/**
 * FXML Controller class
 *
 * @author ma
 */
public class EndController implements Initializable {
    @FXML
    private VBox background;
    @FXML
    private BarChart<String,Integer> endlevelChart;
    @FXML
    private Label statisticLabel;
    @FXML
    private Button homeButton;
    @FXML
    private PieChart sessionChart;
    @FXML
    private Label trueLable;
    @FXML
    private Label trueLableValue;
    @FXML
    private Label falseLable;
    @FXML
    private Label falseLabelValue;
    private int level0;
    private int level1;
    private int level2;
    private int level3;
    private int level4;
    private int level5;
    private int level6;
    private int level7;
    private int level8;
    private int level9;
    private int finished;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        background.setStyle(config.getProperty("color"));
        setLevelNumber();
        drawLevelChart();
    }
     private void setLevelNumber(){
         ob.forEach(card->{
           String level=card.getLevel();
           switch(level){
               case "0":
                   level0++;
                   break;
               case "1":
                   level1++;
                   break;
               case "2":
                   level2++;
                   break;
               case "3":
                   level3++;
                   break;
               case "4":
                   level4++;
                   break;
               case "5":
                   level5++;
                   break;
               case "6":
                   level6++;
                   break;
               case "7":  
                   level7++;
                   break;
               case "8":
                   level8++;
                   break;
               case "9":
                   level9++;
                   break;
               default:
                   finished++;
                   break;
           }
         });
        
     }
     private void drawLevelChart(){
        CategoryAxis xAxis    = new CategoryAxis();
        xAxis.setLabel(prop.getProperty("end.level"));
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel(prop.getProperty("end.number"));
        XYChart.Series dataSeries= new XYChart.Series();
        dataSeries.setName(file);
        dataSeries.getData().add(new XYChart.Data("level0",level0));
        dataSeries.getData().add(new XYChart.Data("level1",level1));
        dataSeries.getData().add(new XYChart.Data("level2",level2));
        dataSeries.getData().add(new XYChart.Data("level3",level3));
        dataSeries.getData().add(new XYChart.Data("level4",level4));
        dataSeries.getData().add(new XYChart.Data("level5",level5));
        dataSeries.getData().add(new XYChart.Data("level6",level6));
        dataSeries.getData().add(new XYChart.Data("level7",level7));
        dataSeries.getData().add(new XYChart.Data("level8",level8));
        dataSeries.getData().add(new XYChart.Data("level9",level9));
        dataSeries.getData().add(new XYChart.Data("finished",finished));
        endlevelChart.getData().add(dataSeries);
    }
      void drawSessionChart(int trueNumber,int falseNumber){
        PieChart.Data slice1 = new PieChart.Data("True",trueNumber);
        PieChart.Data slice2 = new PieChart.Data("False",falseNumber);
        sessionChart.getData().add(slice1);
        sessionChart.getData().add(slice2);  
        if(trueNumber!=0){
            float i=((float)trueNumber/(float)(trueNumber+falseNumber))*100;
            String value=String.format("%.2f",i)+"%";
               trueLableValue.setText(value); 
        }
        if(falseNumber!=0){
            float i=((float)falseNumber/(float)(trueNumber+falseNumber))*100;
            String value=String.format("%.2f",i)+"%";
              falseLabelValue.setText(value);
        }
                     
     }
     @FXML
     private void home() throws IOException{
         ob.clear();
         
       Parent root=FXMLLoader.load(getClass().getResource("view/main.fxml"),resource);           
                     Stage stage=(Stage)homeButton.getScene().getWindow();
                     Scene scene=null;
                     if(stage.isMaximized()){
                         scene=new Scene(root,stage.getWidth(),stage.getHeight()); 
                     }else{
                         scene=new Scene(root);
                     }
                     stage.setScene(scene);   
                     stage.setTitle(prop.getProperty("home"));
                     stage.getIcons().add(new Image(getClass().getResource("image/logo.png").toString()));
                     stage.show();            
     }
}
