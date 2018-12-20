package sample;

import javafx.fxml.FXML;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.io.File;

public class Controller {
    @FXML
    private Text output;

    @FXML
    public void open(){
        FileChooser ch=new FileChooser();
        ch.getExtensionFilters().add(new FileChooser.ExtensionFilter("MVL", "*.mvl"));
        File f=ch.showOpenDialog(null);
        if(f!=null) {
            String str=Main.parse(f);
            output.setText(str);
            Main.layoutResults(f.getName(),str);
        }
    }

    @FXML
    public void opedDirectory(){
        DirectoryChooser dc=new DirectoryChooser();
        File folder=dc.showDialog(null);
        if(folder!=null){
            StringBuilder out=new StringBuilder();
            surfFolder(folder,out);
            Main.layoutResults(folder.getName(),out.toString());
        }
    }

    @FXML
    public void findClasses(){
        FileChooser ch=new FileChooser();
        ch.getExtensionFilters().add(new FileChooser.ExtensionFilter("MVL", "*.mvl"));
        File f=ch.showOpenDialog(null);
        if(f!=null) {
            String str=Main.parseClasses(f);
            output.setText(str);
            //Main.layoutResults(f.getName(),str);
        }
    }

    private void surfFolder(File folder,StringBuilder out){
        String LF="\r\n";
        File[] files = folder.listFiles();
        for(File f:files){
            if(f.isDirectory()){
                surfFolder(f,out);
            }else if(f.getName().endsWith(".mvl")){
                String res=Main.parse(f);
                out.append(
                        "< file: "+f.getAbsolutePath()+">"+LF
                        +res
                        +"</file: "+f.getAbsolutePath()+">"+LF+LF+LF+LF+LF);
            }
        }
    }
}
