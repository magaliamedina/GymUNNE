package com.example.gimnasio_unne;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Environment;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class GenerarPDF {

    String NOMBRE_DIRECTORIO = "GymUNNE";
    //no permite un nombre con 2 puntos
    String NOMBRE_DOCUMENTO = ""+Utiles.obtenerFechaActual("GMT-3")+"-"+Utiles.getHoraActual("GMT-3")+".pdf";

    public void crearPDF(Bitmap barchart) {
        Document documento=new Document(PageSize.A4.rotate());
        try {
            File file = crearFichero(NOMBRE_DOCUMENTO);
            FileOutputStream ficheroPDF = new FileOutputStream(file.getAbsolutePath());

            PdfWriter writer = PdfWriter.getInstance(documento, ficheroPDF);

            documento.open();
            //documento.add(new Paragraph("Reporte de alumnos inscriptos a los grupos:\n\n"));
            /*documento.add(new Paragraph("Tabla \n\n"));
            PdfPTable tabla = new PdfPTable(5);
            for(int i=0;i<15;i++) {
                tabla.addCell("CELDA "+i);
            }
            documento.add(tabla);*/
            agregarGraficos(documento, barchart);
        } catch (DocumentException e){
        } catch (IOException e){
        } finally {
            documento.close();
        }
    }

    public void agregarGraficos(Document document,Bitmap imagen) throws DocumentException {
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            imagen.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] imageInByte = stream.toByteArray();
            Image image = Image.getInstance(imageInByte);
            image.scaleAbsolute(PageSize.A4.rotate());
            image.setAbsolutePosition(0, 0);
            document.add(image);
        } catch(IOException ex) { return; }
    }

    public File crearFichero(String nombreFichero) {
        File ruta = getRuta();
        File fichero = null;
        if(ruta!= null){
            fichero= new File(ruta, nombreFichero);
        }
        return fichero;
    }

    public File getRuta() {
        File ruta =null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            ruta= new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),NOMBRE_DIRECTORIO);
            if(ruta!=null){
                if(!ruta.mkdirs()) {
                    if(!ruta.exists()){
                      return null;
                    }
                }
            }
        }
        return ruta;
    }
}
