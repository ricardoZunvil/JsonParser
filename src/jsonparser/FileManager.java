/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsonparser;

import com.sun.javafx.scene.control.skin.VirtualFlow;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import static javax.management.Query.attr;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author ricardozun
 */
public class FileManager {

    public String getDate() {
        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        String strDate = dateFormat.format(date);
        return strDate;
    }

    public String getRoot() {
        String rootFolder = System.getProperty("user.dir");
        return rootFolder;
    }

    public String readFile(String location) {

        String text = "";

        try {
            BufferedReader bf = new BufferedReader(new FileReader(location));
            String temp = "";
            String bfReaded;
            while ((bfReaded = bf.readLine()) != null) {
                temp = temp + bfReaded;
            }
            text = temp;
        } catch (Exception e) {
            System.err.println("System can't open file");
        }

        return text;
    }

    public ArrayList JsonToList(String jsonString) {
        //arreglo json se crea con la cadena json obtenida
        JSONArray arregloJson = new JSONArray(jsonString);
        //se crea arraylist para guardar registros
        ArrayList<Data> registros = new ArrayList<>();

        for (int i = 0; i < arregloJson.length(); i++) {
            // Obtener objeto a través del índice
            JSONObject posibleData = arregloJson.getJSONObject(i);

            // Acceder  al jsonObject para obtener la informacion de cada campo
            String section = posibleData.getString("section");
            String serial_number = posibleData.getString("serial_number");
            String query = posibleData.getString("query");
            String date_time = posibleData.getString("date_time");
            String message = posibleData.getString("message");
            String exception_detail = posibleData.getString("exception_detail");
            String exception_stack_trace = posibleData.getString("exception_stack_trace");

            // se crea un objeto
            Data obj = new Data(section, serial_number, query, date_time, message, exception_detail, exception_stack_trace);

            // Agregar a la lista el objeto
            registros.add(obj);
        }
        return registros;
    }

    public String generateMap(ArrayList registros) {
        HashMap<String, Integer> mensajes = new HashMap<String, Integer>();

        Data dato;//objeto

        Iterator iter = registros.iterator();//iterador ligado a la lista de registros

        while (iter.hasNext()) {
            // Cast del Objeto 
            dato = (Data) iter.next();
            //dato.message= dato.message.substring(0, 20);
            if (mensajes.isEmpty()) {
                mensajes.put(dato.message, 1);
            } else {
                if (mensajes.containsKey(dato.message)) {
                    mensajes.replace(dato.message, mensajes.get(dato.message) + 1);
                } else {
                    mensajes.put(dato.message, 1);
                }
            }

        }
        String html = "<table style='border:1px solid black'><tbody><tr style='border:1px solid black'><th>Message</th> <th>Count</th></tr>";
        for (HashMap.Entry<String, Integer> entry : mensajes.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            html += "<tr><td style='border:1px solid black' >" + key + "</td><td style='border:1px solid black'>" + value + "</td></tr>";
        }

        System.out.println("Mensajes dentro del json:");
        BiConsumer<String, Integer> biconsumer = (key, val)
                -> System.out.println(key + " = " + val);
        mensajes.forEach(biconsumer);
        return html;
    }

    public void listf(String directoryName, List<File> files) {
        File directory = new File(directoryName);

        // Get all files from a directory.
        File[] fList = directory.listFiles();
        if (fList != null) {
            for (File file : fList) {
                if (file.isFile()) {
                    files.add(file);
                } else if (file.isDirectory()) {
                    listf(file.getAbsolutePath(), files);
                }
            }
        }
    }

    public void sendMail(String path, String resume, String dtCreado) throws AddressException, MessagingException {
        Config conf = new Config();

        String to = conf.ReadString("General", "eMails", "manuel.navarro@sanmina.com").trim();
        System.out.println(to);
        //String to = "manuel.navarro@sanmina.com";//change accordingly  
        String from = "hydra.logs@sanmina.com";
        String host = "mailhub.sanmina-sci.com";//or IP address  

        InternetAddress[] iAdressArray = InternetAddress.parse(to);

        Properties properties = new Properties();

        properties.put("mail.smtp.auth", "false");
        //Put below to false, if no https is needed
        properties.put("mail.smtp.starttls.enable", "false");
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "25");

        // Setup mail server
        // Get the default Session object.
        Session session = Session.getDefaultInstance(properties, null);

        BodyPart texto = new MimeBodyPart();
        texto.setText("Resumen de log file procesado:\n");

        BodyPart htmlPart = new MimeBodyPart();
        htmlPart.setContent(resume, "text/html");

        BodyPart texto2 = new MimeBodyPart();
        texto2.setText("\nPathVirtual:\n http://148.164.96.83/virtual_path/Reynosa/" + dtCreado + "/Log.csv");

        BodyPart adjunto = new MimeBodyPart();
        adjunto.setDataHandler(new DataHandler(new FileDataSource(path)));
        adjunto.setFileName("Log.csv");

        MimeMultipart multiParte = new MimeMultipart();
        multiParte.addBodyPart(texto);
        multiParte.addBodyPart(htmlPart);
        multiParte.addBodyPart(texto2);
        multiParte.addBodyPart(adjunto);

        MimeMessage message = new MimeMessage(session);

        // Se rellena el From
        message.setFrom(new InternetAddress(from));

        // Se rellenan los destinatarios
        message.setRecipients(Message.RecipientType.TO, iAdressArray);
        
        // Se rellena el subject
        message.setSubject("Archivo log procesado (" + getDate());

        // Se mete el texto y la foto adjunta.
        message.setContent(multiParte);

        // Send message
        Transport.send(message);
        System.out.println("Sent message successfully....");
    }

    private static final String CSV_SEPARATOR = ",";

    public static void writeToCSV(ArrayList<Data> dataList, String path) {
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path), "UTF-8"));
            StringBuffer header = new StringBuffer();
            header.append("SECTION");
            header.append(CSV_SEPARATOR);
            header.append("SERIAL_NUMBER");
            header.append(CSV_SEPARATOR);
            header.append("QUERY");
            header.append(CSV_SEPARATOR);
            header.append("DATE_TIME");
            header.append(CSV_SEPARATOR);
            header.append("MESAGGE");
            header.append(CSV_SEPARATOR);
            header.append("EXCEPTION_DETAIL");
            header.append(CSV_SEPARATOR);
            header.append("EXCEPTION_STACK_TRACE");
            header.append(CSV_SEPARATOR);
            bw.write(header.toString());
            bw.newLine();
            for (Data dato : dataList) {
                StringBuffer oneLine = new StringBuffer();
                oneLine.append(dato.getSection().replace(",", " ").replace("\r", " ").replace("\n", " "));
                oneLine.append(CSV_SEPARATOR);
                oneLine.append(dato.getSerial_number().replace(",", " ").replace("\r", " ").replace("\n", " "));
                oneLine.append(CSV_SEPARATOR);
                oneLine.append(dato.getQuery().replace(",", " ").replace("\r", " ").replace("\n", " "));
                oneLine.append(CSV_SEPARATOR);
                oneLine.append(dato.getDate_time().replace(",", " ").replace("\r", " ").replace("\n", " "));
                oneLine.append(CSV_SEPARATOR);
                oneLine.append(dato.getMessage().replace(",", " ").replace("\r", " ").replace("\n", " "));
                oneLine.append(CSV_SEPARATOR);
                oneLine.append(dato.getException_detail().replace(",", " ").replace("\r", " ").replace("\n", " "));
                oneLine.append(CSV_SEPARATOR);
                oneLine.append(dato.getException_stack_trace().replace(",", " ").replace("\r", " ").replace("\n", " "));
                bw.write(oneLine.toString());
                bw.newLine();
            }
            bw.flush();
            bw.close();
        } catch (UnsupportedEncodingException e) {
            System.out.println("UnsupportedEncodingException: " + e.getMessage());
        } catch (FileNotFoundException e) {
            System.out.println("FileNotFoundException: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    } 

    static String getAttributes(String pathStr) throws IOException {
        Path p = Paths.get(pathStr);
        BasicFileAttributes view
                = Files.getFileAttributeView(p, BasicFileAttributeView.class)
                        .readAttributes();
        FileTime date = view.creationTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
        String dateCreated = df.format(date.toMillis());
        return dateCreated;
    }
}
