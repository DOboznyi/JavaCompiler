import static java.nio.file.StandardOpenOption.*;

import java.nio.file.*;
import java.io.*;
import java.util.ArrayList;

public class CodeGenerator {
    String path = "d:\\MyTest.txt";
    String text;
    String data;
    int index=0;

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

    class node {
        String name;
        tokType type;

        public node(tokType type, String name) {
            this.name = name;
            this.type = type;
        }
    }

    String[] methods;

    public void generate_code(ArrayList<ArrayList<lxNode>> arr_methods_list, ArrayList<lxNode> nodes) {
        methods = new String[arr_methods_list.size()];
        for (int i = 0; i < arr_methods_list.size(); i++) {
            lxNode type = arr_methods_list.get(i).get(0);
            while (type.ndOp != tokType._void || type.ndOp != tokType._int) {
                type = type.pstNd;
            }
            String name = getName(type.pstNd.prvNd);
            lxNode temp = type.pstNd;
            ArrayList<tokType> types = new ArrayList<>();
            ArrayList<String> names = new ArrayList<>();
            if (temp.pstNd != null) {
                temp = temp.pstNd;
                while (temp.ndOp != tokType._int) {
                    types.add(temp.pstNd.ndOp);
                    names.add(getName(temp.pstNd.pstNd));
                    data += getName(temp.pstNd.pstNd) + "_" + name + " dd 0\n";
                    temp = temp.prvNd;
                }
                types.add(temp.pstNd.ndOp);
                names.add(getName(temp.pstNd.pstNd));
                data += getName(temp.pstNd.pstNd) + "_" + name + " dd 0\n";
            }
            lxNode start = nodes.get(type.prnNd);
            while (start.ndOp != tokType._opbz) {
                start = nodes.get(start.prnNd);
            }
            generate_code_method(type, name, types, names, start);
            //text += "push ebp\nmov ebp, esp\n";
            //for (int i = 0; i < types.size(); i++) {
            //    int ind = 4+4*(types.size()-1-i);
            //    text += "mov eax,[ebp+"+ind+"]\npush eax\n";
            //}
        }
    }

    public String generate_code_method(lxNode type, String name, ArrayList<tokType> types, ArrayList<String> names, lxNode start) {
        String res = name + " proc\n";
        if (types.size() != 0) {
            res += "push ebp\nmov ebp, esp\n";
            for (int i = 0; i < types.size(); i++) {
                int ind = 4 + 4 * (types.size() - 1 - i);
                res += "mov eax,[ebp+" + ind + "]\nmov dword ptr [" + names.get(i) + "], eax\n";
            }
        }
        if (start.pstNd != null) {
            lxNode temp = start.pstNd;
            while (temp.ndOp != tokType._EOS) {
                temp = temp.prvNd;
            }
        }
        res += name + " endp\n";
        return res;
    }

    private String generate_code_block(lxNode start){
        lxNode temp = start.pstNd;
        while (temp.ndOp != tokType._EOS) {
            temp = temp.prvNd;
        }
        return "";
    }

    private String generate_compound(String name, ArrayList<tokType> types, ArrayList<String> names, lxNode start) {
        String res="";
        lxNode temp = start;
        if (start==null){
            return res;
        }
        if (temp.ndOp == tokType._int) {
            types.add(temp.ndOp);
            names.add(getName(temp.pstNd.prvNd));
            temp = temp.pstNd;
        }
        switch (temp.ndOp) {
            case _ass:
                res+=generateAss(name,types,names,temp);
                break;
            case _add:
                generateAdd(name,types,names,temp);
                break;
            case _nam:
                generateNam(name,types,names,temp);
                break;
            case _for:
                generateFor(name,types,names,temp);
                break;
            case _whileP:
                switch (temp.prvNd.ndOp){
                    case _brkz:
                        generateWhile(name,types,names,temp);
                        break;
                    case _repeat:
                        generateRepeat(name,types,names,temp);
                        break;
                }
                break;
            case _ocbr:
                 generateOcbr(name,types,names,temp);
               break;
            case _brkz:
                //generateOp(name,types,names,temp);
                break;
        }
        return res;
    }

    public String generateAss(String name, ArrayList<tokType> types, ArrayList<String> names, lxNode start){
        String res="";
        res+=generate_compound(name, types, names,start.pstNd);
        res+="mov dword ptr["+getName(start.prvNd)+"_"+name+"], eax";
        return res;
    }

    public String generateAdd(String name, ArrayList<tokType> types, ArrayList<String> names, lxNode start){
        String res="";
        res+=generate_compound(name, types, names,start.prvNd);
        res+="push eax";
        res+=generate_compound(name, types, names,start.pstNd);
        res+="pop edx";
        res+="add eax, edx";
        return res;
    }

    public String generateNam(String name, ArrayList<tokType> types, ArrayList<String> names, lxNode start){
        String res="";
        res+="mov eax, dword ptr["+getName(start.prvNd)+"_"+name+"]";
        return res;
    }

    public String generateOcbr(String name, ArrayList<tokType> types, ArrayList<String> names, lxNode start){
        String res="";
        int value = Integer.parseInt(getName(start));
        res+="mov eax, "+value;
        return res;
    }

    public String generateFor(String name, ArrayList<tokType> types, ArrayList<String> names, lxNode start){
        String res="";
        res+=generate_compound(name, types, names, start.prvNd.pstNd.prvNd.prvNd); //start condition
        res+="jmp LOOP"+(index+1);
        int starti = index;
        res+="LOOP"+index+":\n";
        index++;
        res+=generate_compound(name, types, names, start.prvNd.pstNd.pstNd);
        res+="LOOP"+index+":\n";
        int id = index+1;
        index+=2;
        res+=generate_bool(name, types, names, start.prvNd.pstNd.prvNd.pstNd,"LOOP"+id);
        res+=generate_code_block(start.pstNd);
        res+="jmp LOOP"+starti+"\n";
        res+="LOOP"+id+":\n";
        return res;
    }

    public String generateWhile(String name, ArrayList<tokType> types, ArrayList<String> names, lxNode start){
        String res="";
        int starti = index;
        res+="LOOP"+index+":\n";
        index++;
        res+=generate_bool(name, types, names, start.prvNd.pstNd,"LOOP"+index);
        int id = index;
        index++;
        res+=generate_code_block(start.pstNd);
        res+="jmp LOOP"+starti+"\n";
        res+="LOOP"+id+":\n";
        return res;
    }

    public String generateRepeat(String name, ArrayList<tokType> types, ArrayList<String> names, lxNode start){
        String res="";
        int starti = index;
        res+="LOOP"+index+":\n";
        index++;
        int id = index;
        index++;
        res+=generate_code_block(start.pstNd);
        res+=generate_bool(name, types, names, start.prvNd.pstNd,"LOOP"+id);
        res+="jmp LOOP"+starti+"\n";
        res+="LOOP"+id+":\n";
        return res;
    }

    private String generate_bool(String name, ArrayList<tokType> types, ArrayList<String> names, lxNode start,String loop) {
        String res="";
        lxNode temp = start;
        switch (temp.ndOp) {//_lt, _le, _eq, _ne, _ge, _gt,       // < <= == != >= >
            case _lt:
                res += generateAss(name, types, names, temp);
                break;
            case _le:
                generateAdd(name, types, names, temp);
                break;
            case _eq:
                generateNam(name, types, names, temp);
                break;
            case _ne:
                generateFor(name, types, names, temp);
                break;
            case _ge:
                generateFor(name, types, names, temp);
                break;
            case _gt:
                generateFor(name, types, names, temp);
                break;
        }
        return res;
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

    private tokType getType(String name){
        return null;
    }
}
