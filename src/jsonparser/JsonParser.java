/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsonparser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.mail.MessagingException;

/**
 *
 * @author Jesus
 */
public class JsonParser {

    /**
     * @param args the command line arguments
     * @throws javax.mail.MessagingException
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws MessagingException, IOException {

        List<File> files;
        files = new ArrayList<>();
        FileManager obj = new FileManager();
        String jsonString;

        //tabla html
        String resume;

        //obtenemos dia, mes y año de ejecucion
        obj.getRoot();
        // obtenemos del archivo config los paths de directorios
        Config objConfig = new Config();
        String rootFolder = objConfig.ReadString("General", "PathToLog", obj.getRoot());
        String pathToSave = objConfig.ReadString("General", "PathToSave", "/home/ricardozun/Desktop");
        String virtualDirectory = objConfig.ReadString("General", "VirtualDirectory", "/home/ricardozun/Desktop");

        //listamos las carpetas que existen en el directorio
        obj.listf(rootFolder, files);
        //iteramos cada uno de los archivos que existen
        for (int i = 0; i < files.size(); i++) {
            //verificamos que alguno termine con la extencion json
            if (files.get(i).toString().matches("(.*)json")) {
                System.out.println("procesando archivo:");
                System.out.println(files.get(i));
                System.out.println("propiedades: ");
                String creado = FileManager.getAttributes(files.get(i).getPath());
                System.out.println("creado en: " + creado);
                System.out.println("Fecha del dia: " + obj.getDate());
                if (creado.trim().equals(obj.getDate().trim())) {
                    try {
                        //generamos ruta de guardado

                        //generamos la cadena json a partir del documento
                        jsonString = obj.readFile(files.get(i).toString());
                        //llenamos un arreglo con la cadena generada
                        ArrayList<Data> registros = new ArrayList<>();
                        registros = obj.JsonToList(jsonString);
                        System.out.println("Registros dentro del json: " + registros.size());
                        resume = obj.generateMap(registros);

                        //generando arbol de carpetas despues de procesar json
                        pathToSave = pathToSave + "/procesados/" + creado;
                        File procesar = new File(pathToSave);
                        if (!procesar.exists()) {
                            if (procesar.mkdirs()) {
                                System.out.println("Directorio creado");
                            } else {
                                System.out.println("Error al crear directorio");
                            }
                        }

                        // copiar el archivo 
                        try {
                            FileWriter myWriter = new FileWriter(pathToSave + "/Log.json");
                            myWriter.write(jsonString);
                            myWriter.close();
                            System.out.println("Archivo procesado con exito!");
                            
                            File escibecsv = new File(virtualDirectory+creado);
                            if (!escibecsv.exists()) {
                                if (escibecsv.mkdirs()) {
                                    System.out.println("Directorio para csv creado");
                                } else {
                                    System.out.println("Error al crear directoriopara csv");
                                }
                            }
                            obj.writeToCSV(registros, virtualDirectory + creado + "/Log.csv");
                            System.out.println("CSV created... sending by email");

                            obj.sendMail(virtualDirectory + creado + "/" + "Log.csv", resume, creado);
                            System.out.println("Successfully wrote to the file.");
                            // eliminando de carpeta original
                            File borrar = new File(files.get(i).toString());
                            borrar.delete();
                        } catch (IOException e) {
                            e.printStackTrace();
                            System.out.println("Error" + e.getMessage());
                        }

                    } catch (Exception e) {
                        System.out.println("error!!!" + e.getMessage());
                    }
                } else {
                    System.out.println("Nada que procesar");
                }
            }

        }

    }

}
