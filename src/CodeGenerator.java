import static java.nio.file.StandardOpenOption.*;
import java.nio.file.*;
import java.io.*;
import java.util.ArrayList;
public class CodeGenerator {
    /*String path ="d:\\MyTest.txt";
    String text;

    public void writeToFile() {
        byte data[] = text.getBytes();
        Path p = Paths.get("./logfile.txt");

        try (OutputStream out = new BufferedOutputStream(
                Files.newOutputStream(p, CREATE, APPEND))) {
            out.write(data, 0, data.length);
        } catch (IOException x) {
            System.err.println(x);
        }
    }

    public CodeGenerator(String path) {
        text = ".586\n.model flat, stdcall\ninclude \\masm32\\include\\kernel32.inc\ninclude \\masm32\\include\\user32.inc\nincludelib \\masm32\\lib\\kernel32.lib\nincludelib \\masm32\\lib\\user32.lib";
        this.path = path + ".asm";
    }

    lxNode[] nd;
    char[] imgBuf;

    class node{
        String name;
        tokType type;

        public node(tokType type, String name ) {
            this.name = name;
            this.type = type;
        }
    }


    public void generateASM(lxNode[] nd, char[] imgBuf) {
        this.nd = nd;
        this.imgBuf = imgBuf;
        text += ".data\n";
        for (int i =1;i<nd.length;i++){
            if (nd[i-1].ndOp==tokType._int&&nd[i].ndOp==tokType._nam){
                String name = getName(nd[i]);
                text += name +" dd 0";
            }
        }
        text += ".code\n";
        for (int i = 3; i < nd.length-1; i++) {
            if (nd[i].ndOp==tokType._brkz&&nd[i-1].ndOp!=tokType._for){
                    i++;
                    while (nd[i].ndOp != tokType._brkz) {
                        i++;
                    }
                    i++;
            }
            if ((nd[i].ndOp == tokType._void)||(nd[i].ndOp == tokType._int)){
                tokType type = nd[i].ndOp;
                i++;
                String name = getName(nd[i]);
                text += getName(nd[i]) + "proc\n";
                ArrayList types = new ArrayList();
                i+=2;
                while (nd[i].ndOp!=tokType._brkz){
                    types.add(new node(nd[i].ndOp,getName(nd[i+1])));
                    i+=2;
                    if(nd[i].ndOp==tokType._comma){
                        i++;
                    }
                }
                i++;
                generateMethod(type,types,i);
            }
        }
        text += "end main";
    }

    private void generateMethod(tokType type,ArrayList types,int start){
        if (!types.isEmpty()) {
            text += "push ebp\nmov ebp, esp\n";
            for (int i = 0; i < types.size(); i++) {
                int ind = 4+4*(types.size()-1-i);
                text += "mov eax,[ebp+"+ind+"]\npush eax\n";
            }
        }
        int finish = start;
        finish++;
        while(nd[finish].ndOp!=tokType._opbz){
            finish++;
        }
        generateBlock(start,finish,types);
        if (!types.isEmpty()) {
            for (int i = 0; i < types.size(); i++) {
                text += "pop eax\n";
            }
        }
    }

    private void generateBlock(int start, int finish,ArrayList types) {
        if (finish - start == 1) {
            return;
        }
        for (int i = start +1;i<finish-1;i++){
            if (nd[i].ndOp==tokType._int){
                i++;
            }
            switch (nd[i].ndOp){
                case _nam:
                    generateAss();
                    break;
                case _for:
                    generateFor();
                    break;
                case _whileN:
                    generateWhileN();
                    break;
                case _whileP:
                    generateWhileP();
                    break;
            }
        }
    }

    private String getName(lxNode nd) {
        String name = "";
        int k = nd.start;
        while (imgBuf[k] != 0) {
            name += imgBuf[k];
            k++;
        }
        return name;
    }

    */

}
