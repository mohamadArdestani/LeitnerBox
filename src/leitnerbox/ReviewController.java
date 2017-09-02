    /*
     * To change this license header, choose License Headers in Project Properties.
     * To change this template file, choose Tools | Templates
     * and open the template in the editor.
     */
    package leitnerbox;

    import com.sun.speech.freetts.Voice;
    import com.sun.speech.freetts.VoiceManager;
    import java.io.BufferedWriter;
    import java.io.File;
    import java.io.FileOutputStream;
    import java.io.IOException;
    import java.io.OutputStreamWriter;
    import java.io.Writer;
    import java.net.URL;
    import java.text.ParseException;
    import java.text.SimpleDateFormat;
    import java.util.ArrayList;
    import java.util.Date;
    import java.util.Optional;
    import java.util.ResourceBundle;
    import java.util.Timer;
    import java.util.TimerTask;
    import java.util.concurrent.ExecutorService;
    import java.util.concurrent.Executors;
    import java.util.logging.Level;
    import java.util.logging.Logger;
    import javafx.application.Platform;
    import javafx.fxml.FXML;
    import javafx.fxml.FXMLLoader;
    import javafx.fxml.Initializable;
    import javafx.scene.Parent;
    import javafx.scene.Scene;
    import javafx.scene.control.Alert;
    import javafx.scene.control.Button;
    import javafx.scene.control.ButtonType;
    import javafx.scene.control.Label;
    import javafx.scene.control.TextArea;
    import javafx.scene.effect.Effect;
    import javafx.scene.effect.ImageInput;
    import javafx.scene.image.Image;
    import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
    import javafx.scene.layout.BorderPane;
    import javafx.stage.Stage;
    import javafx.stage.WindowEvent;
    import static leitnerbox.ReadingController.option;
    import static leitnerbox.ReadingController.filterCards;
    import static leitnerbox.LessonController.file;
    import org.dom4j.Document;
    import org.dom4j.DocumentException;
    import org.dom4j.Element;
    import org.dom4j.io.SAXReader;
    import static leitnerbox.LeitnerBox.prop;
    import static leitnerbox.LeitnerBox.config;
    import static leitnerbox.LeitnerBox.resource;
    import static leitnerbox.LessonController.ownerStage;
    public class ReviewController implements Initializable {
        @FXML
        private BorderPane background;
        @FXML
        private Button endButton;
        @FXML
        private Label totalLable;
        @FXML
        private Label totalvaluelable;
        @FXML
        private Label correctLable;
        @FXML
        private Label correctValueLable;
        @FXML
        private Label falseLable;
        @FXML
        private Label falseValueLable;
        @FXML
        private Label cardLeftLable;
        @FXML
        private Label cardLeftValue;
        @FXML
        private Label timeLable;
        @FXML
        private Label timeValueLable;
        @FXML
        private Label skipLabel;
        @FXML
        private Label skipValueLabel;
        @FXML
        private TextArea cardTextArea;
        @FXML
        private ImageView image;
        @FXML
        private Button readButton;
        @FXML
        private Button skipButton;
        @FXML
        private Button previousButton;
        @FXML
        private Button nextButton;
        @FXML
        private Button speakButton;
        @FXML
        private Button speakerButton;
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
        private int hour0;
        private int hour1;
        private int hour2;
        private int hour3;
        private int hour4;
        private int hour5;
        private int hour6;
        private int hour7;
        private int hour8;
        private int hour9;
        private int second = 0;
        private int minute=0;
        private int hour=0;
        private int cardLeft=filterCards.size();
        private  int cardPosition=0;
        private String readButtonStatus;
        private String skipButtonStatus;
        private int correct;
        private int unCorrect;
        private int skip=0;
        private int previousIndex=1;
        private int nextIndex;
        private int Index=0;
        private  ArrayList<Integer> skipIndex=new ArrayList();
        Timer timer = new Timer();
        String timeRestriction;
        String cardNumber;
        int cardRestriction;
        SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd HH:mm");
        long Hour=60*60*1000;
        long Day=24*Hour;
        boolean close=false;
        private boolean pronounciation;
        private String VOICE_NAME="kevin16";
        private Voice voice;
        ExecutorService executor;
        Thread readAndTrueThread;
        Thread skipAndFalseThread;
        private String readText;
        private String skipText;
        private String trueText;
        private String falseText;
        /**
         * Initializes the controller class.
         */
        @Override
        public void initialize(URL url, ResourceBundle rb) {
            
            background.setStyle(config.getProperty("color"));
            cardNumber=option.get("cardNumber");
            timeRestriction=option.get("time");
            nextButton.setDisable(true);
            previousButton.setDisable(true);
            trueText=prop.getProperty("review.true.button");
            falseText=prop.getProperty("review.false.button");
            readText=prop.getProperty("review.read.button");
            skipText=prop.getProperty("review.skip.button");
            if(option.get("pronounciation")=="enable"){
                pronounciation=true;
                Effect image=new ImageInput(new Image(getClass().getResource("image/speakeron1.png").toString()));
                speakerButton.setEffect(image);
            }else{
                pronounciation=false; 
            }
            cardTextArea.setEditable(false);
            cardTextArea.setStyle("-fx-text-alignment: center");
            readButtonStatus="true";
            skipButtonStatus="true";
            if(option.get("frontSide")=="true"){
                 cardTextArea.setText(filterCards.get(0).getFrontside());
            }
            if(option.get("backSide")=="true"){
                 cardTextArea.setText(filterCards.get(0).getBackside());
            } 
             if(!filterCards.get(0).getImg().isEmpty()){ 
                 
                        String path="file:src/leitnerbox/lessons/"+file+"/images/"+filterCards.get(0).getImg(); 
                        Image img=new Image(path);
                        image.setImage(img);
                     }else{
                      image.setImage(null);  
                    } 
                   totalvaluelable.setText(String.valueOf(filterCards.size()));
                   cardLeftValue.setText(String.valueOf(cardLeft));
                   if(cardNumber!=null && Integer.valueOf(cardNumber)>0){
                    cardRestriction=filterCards.size()-Integer.valueOf(cardNumber);
                   }

            try {
                timeRestriction();

            } catch (ParseException ex) {
               Logger.getLogger(ReviewController.class.getName()).log(Level.SEVERE, null, ex);
           }
            setLevel();
            timer();  
            executor = Executors.newCachedThreadPool();
            VoiceManager voiceManager = VoiceManager.getInstance();		
            voice = voiceManager.getVoice(option.get("voice"));
            voice.allocate();
            speakButton.setOnMouseClicked(e->{
               executor.execute(voiceThread);
            });
           //create thread for read and true button
           readAndTrueThread=new Thread(new Runnable() {
                @Override
                public void run() {
                  if(readButtonStatus=="skip"){
                //click on read Button When skip Card Exist.
                 cardRestriction();
                if(Index<skipIndex.size()){
                Platform.runLater(()->{
                readButton.setText(trueText);
                skipButton.setText(falseText);
                skipButton.setDisable(false);
                previousButton.setDisable(true);
                });
                readButtonStatus="skiptrue";
                skipButtonStatus="skip-false";
                
                Card card=filterCards.get(skipIndex.get(Index));
                if(option.get("frontSide")=="true"){
                    Platform.runLater(()->{
                    cardTextArea.setText(card.getBackside());
                    });
                     }
                    if(option.get("backSide")=="true"){
                     Platform.runLater(()->{   
                    cardTextArea.setText(card.getFrontside());
                     });
                     }
                    if(!card.getImg().isEmpty()){    
                        String path="file:src/leitnerbox/lessons/"+file+"/images/"+card.getImg();        
                        Image img=new Image(path);
                        Platform.runLater(()->{
                        image.setImage(img);
                        });
                     }else{
                      Platform.runLater(()->{  
                      image.setImage(null);
                      });
                    }
                }
            }
           else if(readButtonStatus=="skiptrue"){
                 //click on true Button When skip Card Exist.
                 if(close==false){
                     close=true;
                     close();
                 }
                 Platform.runLater(()->{
            skipValueLabel.setText(String.valueOf(--skip));
            correctValueLable.setText(String.valueOf(++correct));
            readButton.setText(readText);
              skipButton.setDisable(true);
                 });
              readButtonStatus="skip";              
              Card card=filterCards.get(skipIndex.get(Index));
                   try {
                       setNewStatus(card);
                   } catch (ParseException ex) {
                       Logger.getLogger(ReviewController.class.getName()).log(Level.SEVERE, null, ex);
                   }
                    Index++;
                    if(Index<skipIndex.size()){
             Card newCard=filterCards.get(skipIndex.get(Index));
             if(option.get("frontSide")=="true"){
                 Platform.runLater(()->{
                    cardTextArea.setText(newCard.getFrontside()); 
                 });                   
                     }
                    if(option.get("backSide")=="true"){
                        Platform.runLater(()->{
                    cardTextArea.setText(newCard.getBackside());
                        });
                     }
                    if(!newCard.getImg().isEmpty()){    
                        String path="file:src/leitnerbox/lessons/"+file+"/images/"+newCard.getImg();        
                        Image img=new Image(path);
                        image.setImage(img);
                     }                       
                    }else{
                        Platform.runLater(()->{
                        readButton.setDisable(true);
                        skipButton.setDisable(true);
                        cardTextArea.setText(prop.getProperty("review.endofcard"));
                        image.setImage(null);  
                        });                        
                    }
            }
           else if(readButtonStatus=="return"){
              //click on return Button
              readButtonStatus="--";
              correct--;
              Platform.runLater(()->{
              readButton.setText(readText);
              skipButton.setDisable(false);
              nextButton.setDisable(true);
              });
              cardPosition--;
              previousIndex=1;
                   try {
                       readAndTrue();
                   } catch (ParseException ex) {
                       Logger.getLogger(ReviewController.class.getName()).log(Level.SEVERE, null, ex);
                   }  catch (InterruptedException ex) {
                          Logger.getLogger(ReviewController.class.getName()).log(Level.SEVERE, null, ex);
                      }
            }
           else if(readButtonStatus=="true")  {
              //click on read button
              cardRestriction();
                 if(cardPosition >= filterCards.size()) { 
                     if(!skipIndex.isEmpty()){
                        readButtonStatus="skip";

                    }else{
               Platform.runLater(()->{
                readButton.setDisable(true);
                skipButton.setDisable(true);
                previousButton.setDisable(true);
                nextButton.setDisable(true);
                cardTextArea.setText(prop.getProperty("review.endofcard"));
                image.setImage(null);
               });
                     }
                 }else{  
                    Card card=filterCards.get(cardPosition);
                    Platform.runLater(()->{
                      cardLeftValue.setText(String.valueOf(--cardLeft));   
                    });
                   
                    if(option.get("frontSide")=="true"){
                        Platform.runLater(()->{
                    cardTextArea.setText(card.getBackside());
                        });
                     }
                    if(option.get("backSide")=="true"){
                        Platform.runLater(()->{
                    cardTextArea.setText(card.getFrontside());
                        });
                     }
                    if(!card.getImg().isEmpty()){    
                        String path="file:src/leitnerbox/lessons/"+file+"/images/"+card.getImg();        
                        Image img=new Image(path);
                        Platform.runLater(()->{
                        image.setImage(img);
                        });
                     }
                     Platform.runLater(()->{
                      readButton.setText(trueText);
                      skipButton.setText(falseText);
                      previousButton.setDisable(true);
                     });
                      readButtonStatus="false";
                      skipButtonStatus="false";
                 }
                }        
                 else{
                     //click on true button
                      if(close==false){
                     close=true;
                     close();
                    }
                    if(filterCards.size()>cardPosition){
                    Card card=filterCards.get(cardPosition);
                    readButtonStatus="true";
                    skipButtonStatus="true";
                    image.setImage(null);
                    Platform.runLater(()->{
                    readButton.setText(readText);
                    skipButton.setText(skipText);
                    previousButton.setDisable(false);
                    correctValueLable.setText(String.valueOf(++correct)); 
                    });
                          try {
                              setNewStatus(card);
                          } catch (ParseException ex) {
                              Logger.getLogger(ReviewController.class.getName()).log(Level.SEVERE, null, ex);
                          }
                       
                    if(cardPosition<filterCards.size()-1){
                                Card newCard=filterCards.get(cardPosition+1);
                                if(option.get("frontSide")=="true"){
                                Platform.runLater(()->{
                                    cardTextArea.setText(newCard.getFrontside());
                                });
                                
                                     }
                                if(option.get("backSide")=="true"){
                                    Platform.runLater(()->{
                                    cardTextArea.setText(newCard.getBackside());
                                    });
                                
                                     }
                                if(!newCard.getImg().isEmpty()){    
                                    String path="file:src/leitnerbox/lessons/"+file+"/images/"+newCard.getImg();        
                                    Image img=new Image(path);
                                    Platform.runLater(()->{
                                    image.setImage(img);
                                    });
                     }
                                cardPosition++;
                    }else{
                                    if(skipIndex.size()>0){
                                     //go to true when skip exist
                                     readButtonStatus="skip";
                                     if(Index<skipIndex.size()){
                                         Platform.runLater(()->{
                                       readButton.setText(readText);
                                        skipButton.setText(skipText);
                                        skipButton.setDisable(true);
                                         previousButton.setDisable(true);
                                         });
                                         Card skipCard=filterCards.get(skipIndex.get(Index));
                                           if(option.get("frontSide")=="true"){
                                               Platform.runLater(()->{
                                             cardTextArea.setText(skipCard.getFrontside());
                                               });
                                             }
                                           if(option.get("backSide")=="true"){
                                               Platform.runLater(()->{
                                             cardTextArea.setText(skipCard.getBackside());
                                               });
                                              }
                                            if(card.getImg()!=""){    
                                                String path="file:src/leitnerbox/lessons/"+file+"/images/"+skipCard.getImg();        
                                                Image img=new Image(path);
                                                Platform.runLater(()->{
                                                image.setImage(img);
                                                });
                                             }else{
                                                Platform.runLater(()->{
                                              image.setImage(null);  
                                                });
                                            }
                                      }
                                    }else{
                                        Platform.runLater(()->{
                                        readButton.setDisable(true);
                                        skipButton.setDisable(true);
                                        previousButton.setDisable(true);
                                        nextButton.setDisable(true);
                                        cardTextArea.setText(prop.getProperty("review.endofcard"));
                                        image.setImage(null);
                                        });
                                        }
                            }  
                    }
                    else{
                    Platform.runLater(()->{
                    readButton.setDisable(true);
                    skipButton.setDisable(true);
                    cardTextArea.setText(prop.getProperty("review.endofcard"));
                    image.setImage(null);    
                    correctValueLable.setText(String.valueOf(++correct));
                    });
                    }    
           }
                }
           });   
                
           //create thread for skip and false button
           skipAndFalseThread=new Thread(()->{
             if(skipButtonStatus=="skip-false"){
                //click on false button when skip card exist
                 if(close==false){
                     close=true;
                     close();
                 }
                  readButtonStatus="skip";
                  Platform.runLater(()->{
                  readButton.setText(readText);
                  skipButton.setDisable(true);
                  skipValueLabel.setText(String.valueOf(--skip));
                  falseValueLable.setText(String.valueOf(++unCorrect));
                  });
                 Card card=filterCards.get(skipIndex.get(Index));
                     int testHit=Integer.parseInt(card.getTesthit())+1;
                    card.setLevel("0");
                    card.setTesthit(String.valueOf(testHit));
                    card.setStatus("unlearned");
                    card.setExpiredtime();
                    Index++;
                if(Index<skipIndex.size()){    
                    Card newCard=filterCards.get(skipIndex.get(Index));
                    if(option.get("frontSide")=="true"){
                     Platform.runLater(()->{   
                    cardTextArea.setText(newCard.getFrontside());
                     });
                     }
                    if(option.get("backSide")=="true"){
                     Platform.runLater(()->{   
                    cardTextArea.setText(newCard.getBackside());
                     });
                     }
                    if(!card.getImg().isEmpty()){    
                        String path="file:src/leitnerbox/lessons/"+file+"/images/"+newCard.getImg();        
                        Image img=new Image(path);
                        Platform.runLater(()->{
                        image.setImage(img);
                        });
                     }else{
                        Platform.runLater(()->{
                        image.setImage(null);
                        });
                    }
                    }else{
                    Platform.runLater(()->{
                        readButton.setDisable(true);
                        skipButton.setDisable(true);
                        cardTextArea.setText(prop.getProperty("review.endofcard"));
                        image.setImage(null);
                    });
                    }
                
            }
            else if(skipButtonStatus=="true"){
                if(cardPosition<filterCards.size()-1){
                //click on skip button
                skipIndex.add(cardPosition);
                cardPosition++;
                Platform.runLater(()->{
                cardLeftValue.setText(String.valueOf(--cardLeft));
                skipValueLabel.setText(String.valueOf(++skip));
                });
                Card card=filterCards.get(cardPosition);
                 if(option.get("frontSide")=="true"){
                     Platform.runLater(()->{
                    cardTextArea.setText(card.getFrontside());
                     });
                     }
                    if(option.get("backSide")=="true"){
                        Platform.runLater(()->{
                    cardTextArea.setText(card.getBackside());
                        });
                     }
                    if(!card.getImg().isEmpty()){    
                        String path="file:src/leitnerbox/lessons/"+file+"/images/"+card.getImg();        
                        Image img=new Image(path);
                        Platform.runLater(()->{
                        image.setImage(img);
                        });
                     }else{
                        Platform.runLater(()->{
                        image.setImage(null);
                        });
                    }
                    
                }
                else{
                    Platform.runLater(()->{
                   readButton.setDisable(true);
                   skipButton.setDisable(true);                
                   cardTextArea.setText(prop.getProperty("review.endofcard"));
                   image.setImage(null);  
                    });
                }
            }else{
                //click on false button
                 if(close==false){
                     close=true;
                     close();
                 }
                
                Platform.runLater(()->{
                readButton.setText(readText);
                skipButton.setText(skipText);
                previousButton.setDisable(false);
                falseValueLable.setText(String.valueOf(++unCorrect));
                });
                skipButtonStatus="true";
                readButtonStatus="true";
                image.setImage(null);
                if(filterCards.size()>cardPosition){
                    Card card=filterCards.get(cardPosition);
                     int testHit=Integer.parseInt(card.getTesthit())+1;
                    card.setLevel("0");
                    card.setTesthit(String.valueOf(testHit));
                    card.setStatus("unlearned");
                    card.setExpiredtime();
                    if(cardPosition<filterCards.size()-1){
                    Card newCard=filterCards.get(cardPosition+1);
                 if(option.get("frontSide")=="true"){
                     Platform.runLater(()->{ 
                    cardTextArea.setText(newCard.getFrontside());
                     });
                     }
                    if(option.get("backSide")=="true"){
                      Platform.runLater(()->{   
                    cardTextArea.setText(newCard.getBackside());
                      });
                     }
                    }
                    else {
                        if(skipIndex.size()>0){
                        readButtonStatus="skip";
                         Platform.runLater(()->{
                        skipButton.setDisable(true);
                        readButton.setText(readText);
                         });
                         Card newCard=filterCards.get(skipIndex.get(Index));
                         if(option.get("frontSide")=="true"){
                         Platform.runLater(()->{     
                        cardTextArea.setText(newCard.getFrontside());
                         });
                         }
                        if(option.get("backSide")=="true"){
                         Platform.runLater(()->{    
                        cardTextArea.setText(newCard.getBackside());
                         });
                         }
                        if(!card.getImg().isEmpty()){    
                            String path="file:src/leitnerbox/lessons/"+file+"/images/"+newCard.getImg();        
                            Image img=new Image(path);
                             Platform.runLater(()->{
                            image.setImage(img);
                             });
                     }
                    }

                    else{
                     Platform.runLater(()->{        
                    readButton.setDisable(true);
                    skipButton.setDisable(true);
                    previousButton.setDisable(true);
                    nextButton.setDisable(true);
                    cardTextArea.setText(prop.getProperty("review.endofcard"));
                    image.setImage(null);  
                     });
                    }
                    }
                }
                cardPosition++;
            }
                   
                   });
           
        }       
        @FXML
        private void readAndTrue() throws ParseException, InterruptedException{
            if(pronounciation==true && readButtonStatus!="return"){
            executor.execute(voiceThread);
            voiceThread.join();
            Thread.sleep(500);
            }
            executor.execute(readAndTrueThread);            
        }
        @FXML
        private void skipAndFalse() throws InterruptedException{
          if(pronounciation==true){
               executor.execute(voiceThread);
               Thread.sleep(500);
           }
            executor.execute(skipAndFalseThread);   
        }
        @FXML
        private void next(){
         if(previousIndex>1){
            previousIndex--;    
           int i=cardPosition-previousIndex;      
           if(i<filterCards.size()){
            Card card=filterCards.get(i);
            StringBuilder value=new StringBuilder();
            value.append(card.getFrontside());
            value.append("\n");
            value.append(card.getBackside());
            cardTextArea.setText(value.toString());
                if(!card.getImg().isEmpty()){    
                        String path="file:src/leitnerbox/lessons/"+file+"/images/"+card.getImg();        
                        Image img=new Image(path);
                        image.setImage(img);
                     }else{
                        image.setImage(null);
                    } 
           }else{
               nextButton.setDisable(true);
               previousIndex=1;
           }
         }else{
           nextButton.setDisable(true); 
           previousIndex=1;
           StringBuilder value=new StringBuilder();
            value.append(prop.getProperty("review.youreachto"));
            value.append("\n");
            value.append(prop.getProperty("review.lastvisitedcard"));
            cardTextArea.setText(value.toString());
           image.setImage(null);         
         }
        }
        @FXML
        private void previous(){
            int i=cardPosition-previousIndex;
            if(i>=0){
            skipButton.setDisable(true);
            nextButton.setDisable(false);
            readButtonStatus="return";
            readButton.setText(prop.getProperty("review.return.button"));
            previousIndex++;
            Card card=filterCards.get(i);
            StringBuilder value=new StringBuilder();
            value.append(card.getFrontside());
            value.append("\n");
            value.append(card.getBackside());
            cardTextArea.setText(value.toString());
                if(!card.getImg().isEmpty()){    
                        String path="file:src/leitnerbox/lessons/"+file+"/images/"+card.getImg();        
                        Image img=new Image(path);
                        image.setImage(img);
                     }else{
                        image.setImage(null);
                    }
            }else{
                previousButton.setDisable(true);
                   StringBuilder txt=new StringBuilder();
                   txt.append(prop.getProperty("review.nocard"));
                   txt.append("\n");
                   txt.append(prop.getProperty("review.nocard.click"));
                cardTextArea.setText(txt.toString());
                image.setImage(null);
                nextButton.setDisable(false);
            }
        }
        @FXML
        private void end() throws IOException, DocumentException{
            try{
             timer.cancel();
             readButton.setDisable(true);
             skipButton.setDisable(true);
             previousButton.setDisable(true);
             nextButton.setDisable(true);
             save();
             FXMLLoader fxmlloader=new FXMLLoader();
             fxmlloader.setLocation(getClass().getResource("view/end.fxml"));
             fxmlloader.setResources(resource);
            Parent root=fxmlloader.load();
            EndController controller=fxmlloader.getController();
                       controller.drawSessionChart(correct,unCorrect);  
                         Stage stage=((Stage)readButton.getScene().getWindow());
                         Scene scene=new Scene(root,stage.getWidth(),stage.getHeight());
                         stage.setScene(scene); 
                         stage.getIcons().add(new Image(getClass().getResource("image/logo.png").toString()));
                         stage.setTitle(prop.getProperty("end.lable"));
                         stage.setOnCloseRequest(null);
                         stage.show();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        private void cardRestriction(){
             if(cardRestriction==cardLeft){
                     Platform.runLater(new Runnable() {
                            @Override public void run() {
                            Alert alert=new Alert(Alert.AlertType.CONFIRMATION);
                                  alert.setHeaderText(prop.getProperty("review.cardrestriction.alert.head"));
                                  alert.setContentText(prop.getProperty("review.cardrestriction.alert.content"));
                                  ButtonType ok = new ButtonType(prop.getProperty("ok"));
                                  ButtonType no = new ButtonType(prop.getProperty("no"));
                                  alert.getButtonTypes().setAll(ok,no);
                                  Optional<ButtonType> result = alert.showAndWait();

                                  if(result.get()==no){
                                                try {     
                                         end();
                                     } catch (IOException ex) {
                                         Logger.getLogger(ReviewController.class.getName()).log(Level.SEVERE, null, ex);
                                     } catch (DocumentException ex) {   
                                    Logger.getLogger(ReviewController.class.getName()).log(Level.SEVERE, null, ex);
                                }   
                                  }else{
                                   alert.close();
                                  }   
                        }
                            });
                     }
        }
        private void timeRestriction() throws ParseException{
            if(timeRestriction!=null && Integer.valueOf(timeRestriction)>0){
               long time=Integer.parseInt(timeRestriction)*60*1000;
                timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            Platform.runLater(new Runnable() {
                            @Override public void run() {
                            Alert alert=new Alert(Alert.AlertType.CONFIRMATION);
                                  alert.setHeaderText(prop.getProperty("review.time.alert.head"));
                                  alert.setContentText(prop.getProperty("review.time.alert.content"));
                                   ButtonType ok = new ButtonType(prop.getProperty("ok"));
                                  ButtonType no = new ButtonType(prop.getProperty("no"));
                                  alert.getButtonTypes().setAll(ok,no);
                                  Optional<ButtonType> result = alert.showAndWait();

                                  if(result.get()==no){
                                                try {     
                                         end();
                                     } catch (IOException ex) {
                                         Logger.getLogger(ReviewController.class.getName()).log(Level.SEVERE, null, ex);
                                     } catch (DocumentException ex) {   
                                    Logger.getLogger(ReviewController.class.getName()).log(Level.SEVERE, null, ex);
                                }   
                                  }else{
                                   alert.close();
                                  }   
                                  }   
                            });
                            }     
                    },time);

      }
        }
        private void timer(){
                    timer.scheduleAtFixedRate(new TimerTask() {
                      @Override
                      public void run() {
                          Platform.runLater(new Runnable() {
                            @Override public void run() {
                             second++;
                             if(second==60){
                                 second=0;
                                 minute++;
                             }
                             if(minute==60){
                                 minute=0;
                                 hour++;
                             }
                             timeValueLable.setText(String.format("%1$02d %2$02d %3$02d",hour,minute,second));
                }
            });    
                        }
                            }, 0, 1000);

        }
        Thread voiceThread=new Thread(()->{       
                  voice.speak(cardTextArea.getText());   
        }); 
        private void setLevel(){
           if(option.get("level0")==null){
               level0=0;
           }else{
               level0=Integer.parseInt(option.get("level0"));
           }
           if(option.get("level1")==null){
               level1=0;
           }else{
               level1=Integer.parseInt(option.get("level1"));
           }
           if(option.get("level2")==null){
               level2=0;
           }else{
               level2=Integer.parseInt(option.get("level2"));
           }
           if(option.get("level3")==null){
               level3=0;
           }else{
               level3=Integer.parseInt(option.get("level3"));
           }
           if(option.get("level4")==null){
               level4=0;
           }else{
               level4=Integer.parseInt(option.get("level4"));
           }
           if(option.get("level5")==null){
               level5=0;
           }else{
               level5=Integer.parseInt(option.get("level5"));
           }
           if(option.get("level6")==null){
               level6=0;
           }else{
               level6=Integer.parseInt(option.get("level6"));
           }
           if(option.get("level7")==null){
               level7=-1;
           }else{
               level7=Integer.parseInt(option.get("level7"));
           }
           if(option.get("level8")==null){
               level8=0;
           }else{
               level8=Integer.parseInt(option.get("level8"));
           }
           if(option.get("level9")==null){
               level9=0;
           }else{
               level9=Integer.parseInt(option.get("level9"));
           }
           if(option.get("hour0")==null){
               hour0=0;
           }else{
               hour0=Integer.parseInt(option.get("hour0"));
           }
           if(option.get("hour1")==null){
               hour1=0;
           }else{
               hour1=Integer.parseInt(option.get("hour1"));
           }
           if(option.get("hour2")==null){
               hour2=0;
           }else{
               hour2=Integer.parseInt(option.get("hour2"));
           }
           if(option.get("hour3")==null){
               hour3=0;
           }else{
               hour3=Integer.parseInt(option.get("hour3"));
           }
           if(option.get("hour4")==null){
               hour4=0;
           }else{
               hour4=Integer.parseInt(option.get("hour4"));
           }
           if(option.get("hour5")==null){
               hour5=0;
           }else{
               hour5=Integer.parseInt(option.get("hour5"));
           }
           if(option.get("hour6")==null){
               hour6=0;
           }else{
               hour6=Integer.parseInt(option.get("hour6"));
           }
           if(option.get("hour7")==null){
               hour7=0;
           }else{
               hour7=Integer.parseInt(option.get("hour7"));
           }
           if(option.get("hour8")==null){
               hour8=0;
           }else{
               hour8=Integer.parseInt(option.get("hour8"));
           }
           if(option.get("hour9")==null){
               hour9=0;
           }else{
               hour9=Integer.parseInt(option.get("hour9"));
           }
        }
        private void save() throws DocumentException, IOException{        
          SAXReader reader=new SAXReader();
          String path="src/leitnerbox/lessons/"+file+"/"+file+".xml";
          Document document=reader.read(new File(path));
          filterCards.forEach(card->{
           Element node=(Element)document.selectSingleNode("//lesson/card[@id="+card.getId()+"]");
           node.addAttribute("level",card.getLevel())
               .addAttribute("status",card.getStatus())
               .addAttribute("testhit",card.getTesthit());
               if(card.getExpiredtime()!=null){
               node.addAttribute("expiredtime",formatter.format(card.getExpiredtime()));
               }else{
                 node.addAttribute("expiredtime",null);  
               }
          });
           try(Writer out = new BufferedWriter(new OutputStreamWriter(
			new FileOutputStream(new File(path)), "UTF8"))) {
                document.write(out);
                        }
        }
        private void setNewStatus(Card card) throws ParseException{
            String level=card.getLevel();
            int  nextLevel=Integer.parseInt(level)+1;
            int testHit=Integer.parseInt(card.getTesthit())+1;
            Date now=new Date();  
            switch(level){
                case "0":
                 Date newDate0=new Date(now.getTime()+(level0*Day)+(hour0*Hour));
                 card.setLevel(String.valueOf(nextLevel));
                 card.setTesthit(String.valueOf(testHit));
                 card.setStatus("learned"); 
                 card.setExpiredtime(formatter.format(newDate0));
                 break;
                case "1":
                 Date newDate1=new Date(now.getTime()+(level1*Day)+(hour1*Hour));
                 card.setLevel(String.valueOf(nextLevel));
                 card.setTesthit(String.valueOf(testHit));
                 card.setStatus("learned"); 
                 card.setExpiredtime(formatter.format(newDate1));
                 break;
                case "2":
                 Date newDate2=new Date(now.getTime()+(level2*Day)+(hour2*Hour));
                 card.setLevel(String.valueOf(nextLevel));
                 card.setTesthit(String.valueOf(testHit));
                 card.setStatus("learned"); 
                 card.setExpiredtime(formatter.format(newDate2));
                 break;
                case "3":
                  Date newDate3=new Date(now.getTime()+(level3*Day)+(hour3*Hour));
                 card.setLevel(String.valueOf(nextLevel));
                 card.setTesthit(String.valueOf(testHit));
                 card.setStatus("learned"); 
                 card.setExpiredtime(formatter.format(newDate3));
                 break;
                case "4":
                 Date newDate4=new Date(now.getTime()+(level4*Day)+(hour4*Hour));
                 card.setLevel(String.valueOf(nextLevel));
                 card.setTesthit(String.valueOf(testHit));
                 card.setStatus("learned"); 
                 card.setExpiredtime(formatter.format(newDate4));
                 break;
                case "5":
                 Date newDate5=new Date(now.getTime()+(level5*Day)+(hour5*Hour));
                 card.setLevel(String.valueOf(nextLevel));
                 card.setTesthit(String.valueOf(testHit));
                 card.setStatus("learned"); 
                 card.setExpiredtime(formatter.format(newDate5));
                 break;
                case "6":
                 Date newDate6=new Date(now.getTime()+(level6*Day)+(hour6*Hour));
                 card.setLevel(String.valueOf(nextLevel));
                 card.setTesthit(String.valueOf(testHit));
                 card.setStatus("learned"); 
                 card.setExpiredtime(formatter.format(newDate6));
                 break;
                case "7":
                 Date newDate7=new Date(now.getTime()+(level7*Day)+(hour7*Hour));
                 card.setLevel(String.valueOf(nextLevel));
                 card.setTesthit(String.valueOf(testHit));
                 card.setStatus("learned"); 
                 card.setExpiredtime(formatter.format(newDate7));
                 break;
                case "8":
                 Date newDate8=new Date(now.getTime()+(level8*Day)+(hour8*Hour));
                 card.setLevel(String.valueOf(nextLevel));
                 card.setTesthit(String.valueOf(testHit));
                 card.setStatus("learned"); 
                 card.setExpiredtime(formatter.format(newDate8));
                 break;
                case "9":
                Date newDate9=new Date(now.getTime()+(level9*Day)+(hour9*Hour));
                 card.setLevel(String.valueOf(nextLevel));
                 card.setTesthit(String.valueOf(testHit));
                 card.setStatus("learned"); 
                 card.setExpiredtime(formatter.format(newDate9));
                 break;
                 default:
                 card.setTesthit(String.valueOf(testHit));
                 card.setStatus("finished");  
                 card.setExpiredtime();
            }
        }
        @FXML
        private void changeSpeakerButtonStatus(){
            if(pronounciation==true){
               Effect image=new ImageInput(new Image(getClass().getResource("image/speakeroff.png").toString()));
                speakerButton.setEffect(image);
               pronounciation=false;
            }else{
               Effect image=new ImageInput(new Image(getClass().getResource("image/speakeron1.png").toString()));
                speakerButton.setEffect(image);
               pronounciation=true;
            }
        }
        private void close(){   
            ownerStage.setOnCloseRequest((WindowEvent e)->{
                Alert alert=new Alert(Alert.AlertType.WARNING);
                     alert.setTitle(prop.getProperty("caution"));
                     alert.setHeaderText(prop.getProperty("review.close.alert.head"));
                     alert.setContentText(prop.getProperty("review.close.alert.content")); 
                     ButtonType ok=new ButtonType(prop.getProperty("ok"));
                     ButtonType no=new ButtonType(prop.getProperty("no"));
                     alert.getButtonTypes().setAll(ok,no);
                     Optional<ButtonType> result = alert.showAndWait();
                     if (result.get()==ok){
                    try {
                        save();
                    } catch (DocumentException | IOException ex) {
                        Logger.getLogger(ReviewController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                     }else{
                         ownerStage.close();
                     }
            });
        }
        @FXML
        private void PressKey(KeyEvent event) throws ParseException, InterruptedException{
           if(event.getCode().equals(KeyCode.K)){
               readAndTrue();
           }else if(event.getCode().equals(KeyCode.L)) {
              skipAndFalse(); 
           }
        }
        @FXML
        private void skipPressKey(KeyEvent event) throws InterruptedException{
           if(event.getCode().equals(KeyCode.L)){
               skipAndFalse();
           } 
        }
    }
