package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.*;

public class Main extends Application {

    static String[] specialUselessWords =new String[]{"public","opened","local", "continuous", "private", "hybrid", "compound"};
    static String[] classWords=new String[]{"package","class"},
                    allSpecialWords = new String[]{"public","opened","local", "continuous", "private", "hybrid", "compound","package","class"};

    @Override
    public void start(Stage primaryStage) throws Exception{
        System.setErr(new PrintStream("ErrorLog.log"));
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
    }

    static public String parse(File file){
//        try {
            String TAB="    ",LF="\r\n";
//            Scanner sc = new Scanner(file);
//            sc.useDelimiter(LF);
            String out="";
//            MvlClass mvlClass=null;
//
//            boolean fistOne=true;
//
//            List<MvlClass> mvlClasses=new ArrayList<>();
//
//            while(sc.hasNext()){
//                String line=sc.nextLine();
//                line=shift(line);
//                if((line.contains("package ") || line.contains("class ")) && line.endsWith(" is")){
//                    //class or package found
//                    String className=getClassName(line);
//
//                    if(fistOne){
//                        fistOne=false;
//                        mvlClass=new MvlClass(className);
//                        mvlClasses.add(mvlClass);
//                    }else{
//                        parseClass(className,sc,mvlClasses);
//                    }
//
//                }else if(line.startsWith("link")){
//                    //link found
//                    mvlClass.addLink(getLink(line));
//                }
//            }
        try {
            List<MvlClass> mvlClasses = findClasses(file);
            MvlClass mvlClass;
            for (int i = 0; i < mvlClasses.size(); i++) {
                mvlClasses.get(i).clustering();
            }
            out = "All classes is OK!" + LF;
            StringBuilder layout = new StringBuilder();
            for (int i = 0; i < mvlClasses.size(); i++) {

                mvlClass = mvlClasses.get(i);
                List<MvlLink> badLinks = mvlClass.checkClusters();
                if (!badLinks.isEmpty()) {
                    out = "";
                    layout.append("In " + mvlClass.getName() + LF);
                    for (int j = 0; j < badLinks.size(); j++) {
                        MvlLink l = badLinks.get(j);
                        layout.append(TAB + l.toString() + LF);
                    }
                    layout.append(LF + LF);
                }
            }
            out += layout.toString();
            return out;
        }catch (Exception|Error err){
            System.err.print(err.getMessage());
            return err.getMessage();
        }
//        }catch(IOException ex){
//            return ex.getMessage();
//        }
    }

    public static String parseClasses(File f){
        List<MvlClass> clss=findClasses(f);
        StringBuilder sb=new StringBuilder();

        for (MvlClass mc:clss
             ) {
            sb.append(mc.getName()+System.lineSeparator());
        }
        return sb.toString();
    }

    public static List<MvlClass> findClasses(File file){
        List<MvlClass> mvlClasses = new ArrayList<>();
        try {
            String TAB = "    ", LF = "\r\n";
            Scanner sc = new Scanner(file);
            sc.useDelimiter(LF);

            MvlClass mvlClass = null;

            boolean fistOne = true;

            while (sc.hasNext()) {
                String line = sc.nextLine();
                line = shift(line);
                line = clearLine(line);
                if(line.startsWith("--"))
                    continue;

                if ((line.startsWith("package ") || line.startsWith("class ")) && line.endsWith(" is")) {
                    //class or package found
                    String className = getClassName(line);

                    if (fistOne) {
                        fistOne = false;
                        mvlClass = new MvlClass(className);
                        mvlClasses.add(mvlClass);
                    } else {
                        parseClass(className, sc, mvlClasses);
                    }

                } else if (line.startsWith("link")) {
                    //link found
                    mvlClass.addLink(getLink(line));
                }
            }
        }catch(Exception | Error err){
            System.err.print(err.getMessage());
        }
        return mvlClasses;
    }

    private static String clearLine(String str){
        StringBuilder out=new StringBuilder(str);
        StringBuilder tmp=new StringBuilder(100);
        loop:
        for(int i=0;i<out.length();i++){
            char ch=out.charAt(i);
            if(ch!=' '){
                tmp.append(ch);
            }else{
                for(String s: specialUselessWords){
                    if(isEqual(s,tmp)){
                        out.delete(0,tmp.length()+1);
                        tmp.setLength(0);
                        i=-1;
                        continue loop;
                    }
                }
                return out.toString();
            }
        }
        return out.toString();
    }

    public static void layoutResults(String filePrefix, String data){
        Calendar time = Calendar.getInstance();
        String sec = dateToStr(Integer.toString(time.get(Calendar.SECOND))),
                    min = dateToStr(Integer.toString(time.get(Calendar.MINUTE))),
                    hour = dateToStr(Integer.toString(time.get(Calendar.HOUR_OF_DAY))),
                    day = dateToStr(Integer.toString(time.get(Calendar.DAY_OF_MONTH))),
                    month = dateToStr(Integer.toString(time.get(Calendar.MONTH) + 1)),
                    year = dateToStr(Integer.toString(time.get(Calendar.YEAR)));

        Path p = FileSystems.getDefault().getPath("Parsing of "
                    + filePrefix+" "
                    + hour + "_" + min + "_" + sec + "__"
                    + day + "." + month + "." + year + ".txt");

        //ByteChannel bc = java.nio.file.Files.newByteChannel(p, StandardOpenOption.APPEND, StandardOpenOption.CREATE_NEW);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(new File(p.toUri())))) {

            bw.write(data);

        }catch(IOException ex){
            System.err.print(ex.getMessage());
        }
    }

    private static String dateToStr(String str){
        if(str.length()==1)
            str="0".concat(str);
        return str;
    }

    private static String shift(String str){
        StringBuilder out=new StringBuilder();
        for(int i=0;i<str.length();i++){
            char ch=str.charAt(i);
            if(Character.isAlphabetic(ch)){
                out.append(str.substring(i));
                break;
            }
        }
        return out.toString();
    }

    private static void parseClass(String name,Scanner scanner,List<MvlClass> list){
        String stopKey="end "+name+";";
        MvlClass cls=new MvlClass(name);
        list.add(cls);
        while(scanner.hasNext()){
            String line=scanner.nextLine();
            line=shift(line);
            if(line.equals(stopKey)){
                return;
            }else if((line.contains("package ") || line.contains("class ")) && line.endsWith(" is")) {
                //class or package found
                String className = getClassName(line);

                parseClass(className, scanner, list);
            }else if(line.startsWith("link")){
                //link found
                cls.addLink(getLink(line));
            }
        }
    }

    private static String getClassName(String line){

        StringBuilder out=new StringBuilder(line);
        int len=line.length();
        StringBuilder tmp=new StringBuilder(100);
        loop:
        for(int i=0;i<out.length();i++){
            char ch=out.charAt(i);
            if(ch!=' '){
                tmp.append(ch);
            }else{
                 for(String s: allSpecialWords){
                     if(isEqual(s,tmp)){
                         out.delete(0,tmp.length()+1);
                         tmp.setLength(0);
                         i=-1;
                         continue loop;
                     }
                 }
                 return tmp.toString();
            }
        }
        return null;
    }

    private static boolean isEqual(String str,StringBuilder sb){
        int len;
        if((len=str.length())==sb.length()){
            for(int i=0;i<len;i++){
                if(str.charAt(i)!=sb.charAt(i)){
                    return false;
                }
            }
            return true;
        }else{
            return false;
        }
    }

    private static MvlLink getLink(String str){
        if(str.startsWith("link(")){
            String name=str.substring("link(".length(),str.indexOf(' '));
            String tmp=str.substring(str.indexOf(")")+2);
            String source=tmp.substring(0,tmp.indexOf(','));
            String dest=tmp.substring(tmp.indexOf(' ')+1,tmp.indexOf(';'));
            return new MvlLink(name,source,dest);
        }else{
            return null;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

}
