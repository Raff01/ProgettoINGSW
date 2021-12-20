package application.model;

import java.io.FileOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;


import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

public class PDFCreator {
	private static PDFCreator pdf;
	private Font font;
	private Document document;

	private static void PDFCreator() {
	}

	public static PDFCreator getIstance() {
		if (pdf == null)
			pdf = new PDFCreator();
		return pdf;
	}

	public Document getPDF() {
		pdf = null;
		document.close();
		return document;
	}

	public boolean createDocument(String name, int idOrder) {
		document = new Document();
		try {
			PdfWriter.getInstance(document, new FileOutputStream(name + Integer.toString(idOrder) + ".pdf"));
			document.open();
			Path path = Paths.get(ClassLoader.getSystemResource("Images/pdfIcon.png").toURI());
			font = FontFactory.getFont(FontFactory.COURIER, 12, BaseColor.BLACK);
			Image img = Image.getInstance(path.toAbsolutePath().toString());
			img.scalePercent(50);
			img.setAlignment(2);
			document.add(img);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public void appendText(String name) {
		Chunk chunk=new Chunk(name, font);
		try {
			document.add(new Paragraph(chunk));
		} catch (DocumentException e) {
			e.printStackTrace();
		}
	}
	public void newLine() {
		Chunk chunk=new Chunk(Chunk.NEWLINE);
		if(document!=null) {
			try {
				document.add(chunk);
			} catch (DocumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/*public void appendImage(InputStream is) {
		try {
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}*/
}