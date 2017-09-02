/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package leitnerbox;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import static leitnerbox.LeitnerBox.config;
import static leitnerbox.LeitnerBox.prop;
public class AllCardController implements Initializable {
    @FXML
    private ScrollPane background;
    @FXML
    private Label allcardLable;
    @FXML
    private TableView<Card> allCardTable;
    @FXML
    private ListView<String> allCardListView;
     @FXML
    private TableColumn<Card, String> frontSideColumn;
    @FXML
    private TableColumn<Card, String> backSideColumn;
    static List<Card> changeList=new ArrayList();
     List<Card> cards=new ArrayList<>();
     @Override
    public void initialize(URL url, ResourceBundle rb) {
        background.setStyle(config.getProperty("color"));
        allCardListView.getItems().add(0,prop.getProperty("allcard")); 
        allCardListView.setOnKeyPressed(e->{
            if (e.getCode()==KeyCode.DOWN ||e.getCode()==KeyCode.UP) {
                filterCard();
            }
        });
    } 
          
     void setLabel(String lessonName){
         this.allcardLable.setText(lessonName);
     }
     void fillList(Document file) throws DocumentException{       
            //fill listView
            Node node=file.selectSingleNode("//lesson");
            String chapter=node.valueOf("@chapters");
            List<String> myList = new ArrayList<>(Arrays.asList(chapter.split(",")));
                ObservableList ob=FXCollections.observableArrayList(myList);
                allCardListView.getItems().addAll(ob);
                allCardListView.focusModelProperty().get().getFocusedItem();
            //fill TableView            
            List<Node> cardList=file.selectNodes("//lesson/card");
            cardList.stream().forEach((Node i) -> {
                Card card=new Card();
                card.setId(i.valueOf("@id"));
                card.setFrontside(i.valueOf("@frontside"));
                card.setBackside(i.valueOf("@backside"));
                card.setChapter(i.valueOf("@chapter"));
                cards.add(card);
                allCardTable.setEditable(true);
               frontSideColumn.setResizable(true);
               frontSideColumn.setSortable(true);
               frontSideColumn.setEditable(true);
               frontSideColumn.setCellValueFactory(new PropertyValueFactory<>("frontside"));
               frontSideColumn.setCellFactory(TextFieldTableCell.forTableColumn());
               frontSideColumn.setOnEditCommit(new EventHandler<CellEditEvent<Card, String>>() {
            @Override
            public void handle(CellEditEvent<Card, String> t) {
                ((Card) t.getTableView().getItems().get(t.getTablePosition().getRow())).setFrontside(t.getNewValue());
                   changeList.add(t.getRowValue());
            }
                });
               backSideColumn.setResizable(true);
               backSideColumn.setSortable(true);
               backSideColumn.setEditable(true);
               backSideColumn.setCellValueFactory(new PropertyValueFactory<Card, String>("backside"));
               backSideColumn.setCellFactory(TextFieldTableCell.forTableColumn());
               backSideColumn.setOnEditCommit(new EventHandler<CellEditEvent<Card, String>>() {
            @Override
            public void handle(CellEditEvent<Card, String> t) {
                ((Card) t.getTableView().getItems().get(t.getTablePosition().getRow())).setBackside(t.getNewValue());                
                   changeList.add(t.getRowValue());                
            }
                });
               allCardTable.getItems().setAll(cards);          
                });     
}
     static Predicate<Card> filterByChapter(String chapter){
         if (chapter==prop.getProperty("allcard")) { 
             return (Card p)->p.getId().length()>0;
         }
         else{
            return p->p.getChapter().equals(chapter);  
         }
     }            
   @FXML  
     void filterCard(){
         String item=allCardListView.getFocusModel().getFocusedItem();
         ObservableList<Card> cardForSort=FXCollections.observableArrayList(cards);
         FilteredList<Card> filteredData = new FilteredList(cardForSort,filterByChapter(item));
         SortedList<Card> sortedData = new SortedList<>(filteredData);
         sortedData.comparatorProperty().bind(allCardTable.comparatorProperty());
         allCardTable.setItems(sortedData);
     }
}
