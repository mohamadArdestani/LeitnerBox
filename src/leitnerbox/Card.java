/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package leitnerbox;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author ma
 */
public class Card {
    private int id;
    private String frontside;
    private String backside;
    private String chapter;
    private int level;
    private int testhit;
    private Date expiredtime;
    private String img;
    private String status;
    public Card(){}
    public Card(String frontside,String backside){
        this.frontside=frontside;
        this.backside=backside;
    }
    public Card(String frontside,String backside,String image){
        this.frontside=frontside;
        this.backside=backside;
        this.img=image;
    }
    public void setId(String id){
        this.id=Integer.parseInt(id);
    }
    public String getId(){
        return String.valueOf(id);
    }
    public void setFrontside(String frontside){
        this.frontside=frontside;
    }
    public String getFrontside(){
        return frontside;
    }
    public void setBackside(String backside){
        this.backside=backside;
    }
    public String getBackside(){
        return backside;
    }
    public void setChapter(String chapter){
        this.chapter=chapter;
    }
    public String getChapter(){
        return chapter;
    }
    public void setLevel(String level){
        String replaceAll = level.replaceAll("[^a-zA-Z0-9]", "");
        this.level=Integer.parseInt(replaceAll);
    }
    public String getLevel(){
        return String.valueOf(level);
    }
    public void setTesthit(String testhit){
        String replaceAll = testhit.replaceAll("[\"]", "");
        this.testhit=Integer.parseInt(replaceAll);
    }
    public String getTesthit(){
        return String.valueOf(testhit);
    }
    public void setExpiredtime(){
        this.expiredtime=null;
    }
    public void setExpiredtime(String expiredtime) throws ParseException{
        if (expiredtime==""){
        setExpiredtime();
        }
        else{
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date=format.parse(expiredtime);
        this.expiredtime=date;
        }
    }
    public Date getExpiredtime(){
        if(expiredtime==null){
          return null;  
        }else{
        return expiredtime;
        }
        
    }
    public void setImg(String img){
        this.img=img;
    } 
    public String getImg(){
        return img;
    }
    public void setStatus(String status){
        this.status=status;
    }
    public String getStatus(){
       return status; 
    }
    public void reset(){
        setLevel("0");
        setExpiredtime();
        setStatus("");
    }
    @Override
    public String toString(){
        return this.frontside+","+this.backside;
    }
}
