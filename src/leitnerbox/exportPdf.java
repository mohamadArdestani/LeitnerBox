
package leitnerbox;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import javafx.collections.ObservableList;

public class exportPdf {
    public void createPdf(ObservableList<Card> ob,File file,String name) throws DocumentException, IOException{
     Document document = new Document(PageSize.A4);
       BaseFont bs=BaseFont.createFont(getClass().getResource("font/arial.ttf").toString(),BaseFont.IDENTITY_H, true);
        Font font = new Font(bs,18); 
        Font font1 = new Font(bs,36);
        Paragraph header=new Paragraph("LeitnerBox Cards",font1);
        header.setAlignment(Element.ALIGN_CENTER);
        header.setSpacingAfter(10);
        Paragraph fileName=new Paragraph(name,font1);
        fileName.setAlignment(Element.ALIGN_CENTER);
        fileName.setSpacingAfter(10);
     try {
            PdfWriter.getInstance(document,
                new FileOutputStream(file));
            document.open();
            document.add(header);
            document.add(fileName);
              PdfPTable table = new PdfPTable(2);
              table.setHorizontalAlignment(Element.ALIGN_CENTER);
              PdfPCell cell0 = new PdfPCell(new Paragraph("FRONTSIDE",font));
                cell0.setBorder(0);
                cell0.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell0.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
              PdfPCell cell00 = new PdfPCell(new Paragraph("BACKSIDE",font));
                 cell00.setBorder(0);
                 cell00.setHorizontalAlignment(Element.ALIGN_CENTER);
                 cell00.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
              table.addCell(cell0);
              table.addCell(cell00);
            ob.forEach((Card card)->{
                PdfPCell cell1 = new PdfPCell(new Paragraph(card.getFrontside(),font));
                PdfPCell cell2 = new PdfPCell(new Paragraph(card.getBackside(),font));
                cell1.setBorder(0);
                cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell1.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                cell2.setBorder(0);
                cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell2.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
            table.addCell(cell1);
            table.addCell(cell2);
            });
            document.add(table);
            document.close();
        } catch(Exception e){
     
    }
}
}
