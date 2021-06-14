/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsonparser;

import java.io.File;
import org.ini4j.Ini;

/**
 *
 * @author ricardozun
 */
public class Config {
    
    private String vRuta = "";
      private File vConfig;
      private Ini oConfig;
      private String Result = "";

    public Config(String pRuta)
    {
        vRuta="";
        vRuta = pRuta;
        vConfig=new File(vRuta);
        //System.out.println(vRuta);
    }

    public Config()
    {
        vRuta="Config.ini";
        vConfig=new File(vRuta);
        //System.out.println(vRuta);
    }

    public String ReadString(String Section, String Key, String Default){
        String Result = "";
        try{
            //System.out.println(vConfig);
            oConfig = new Ini(vConfig);
            Result =(oConfig.get(Section,Key)!=null?oConfig.fetch(Section,Key):Default);
            //System.out.println(Section+" "+Key+" "+Result);
        }catch(Exception ex){
            Result = ex.toString();
           // System.out.print(ex.toString());
        }
        return Result;
    }

     public String ReadPath(String Section, String Key, String Default){
        String Result = "";
        try{

            oConfig = new Ini(vConfig);
            Result =(oConfig.fetch(Section,Key)!=null?oConfig.fetch(Section,Key):Default);
            //System.out.println(Section+" "+Key+" "+Result);
        }catch(Exception ex){
            Result = ex.toString();
            //System.out.print(ex.toString());
        }
        return Result;
    }

     public int ReadInt (String Section, String Key, int Default){
        int Result = 0;
        try{

            oConfig = new Ini(vConfig);
            Result =(IsNumeric(oConfig.get(Section,Key))?Integer.parseInt(oConfig.get(Section,Key)):Default);
           //System.out.println(Section+" "+Key+" "+Result);
        }catch(Exception ex){
            Result =-1;
            //System.out.print(ex.toString());
        }
        return Result;
    }

     private boolean IsNumeric(String pParameter){
         try{
             Integer.parseInt(pParameter);
             return true;
         }catch(Exception ex){
             return false;
         }
     }

      public boolean WriteString (String Section, String Key, String Value){
        try{

            vConfig.setWritable(true, false);
            vConfig.createNewFile();
            oConfig = new Ini(vConfig);
            oConfig.put(Section, Key,Value);
            oConfig.store();
            return true;
        }catch(Exception ex){
            System.out.print(ex.toString());
            return false;

        }
    }

         public boolean WriteInt(String Section, String Key, int Value) {
        try {

            vConfig.setWritable(true, false);
            vConfig.createNewFile();
            oConfig = new Ini(vConfig);
            oConfig.put(Section, Key, Value);
            oConfig.store();
            //System.out.println(Section+" "+Key+" "+Value);
            return true;
        } catch (Exception ex) {
            System.out.print(ex.toString());
            return false;
        }
    }

          public Ini.Section mValMachine(String Section)
        {
            Ini.Section vEquipo=null;
           try
           {
              oConfig = new Ini(vConfig);
              vEquipo = oConfig.get(Section);
              return vEquipo;
           }catch(Exception ex)
           {
               System.out.print(ex.toString());
               return vEquipo;
           }
        }
    
}
