import static java.nio.file.StandardOpenOption.*;

import java.nio.file.*;
import java.io.*;
import java.util.ArrayList;

public class CodeGenerator {
    String path = "d:\\MyTest.txt";
    String text = "";
    String data = "";
    String code = "";
    int index=0;

    public void writeToFile() {
        try(  PrintWriter out = new PrintWriter( "d:\\filename.txt" )  ){
            out.println( text );
        }
        catch (IOException x) {
            System.err.println(x);
        }
        /*byte data[] = text.getBytes();
        Path p = Paths.get("./logfile.txt");

        try (OutputStream out = new BufferedOutputStream(
                Files.newOutputStream(p, CREATE, APPEND))) {
            out.write(data, 0, data.length);
        } catch (IOException x) {
            System.err.println(x);
        }
        */
    }

    public CodeGenerator(String path,char[] imgBuf) {
        this.imgBuf = imgBuf;
        text = ".586\r\n.model flat, stdcall\r\ninclude \\masm32\\include\\kernel32.inc\r\ninclude \\masm32\\include\\user32.inc\r\nincludelib \\masm32\\lib\\kernel32.lib\r\nincludelib \\masm32\\lib\\user32.lib\r\n";
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
    ArrayList<lxNode> nodes;
    ArrayList<ArrayList<lxNode>> arr_methods_list;

    public void generate_code(ArrayList<ArrayList<lxNode>> arr_methods_list, ArrayList<lxNode> nodes) {
        this.nodes = nodes;
        this.arr_methods_list=arr_methods_list;
        methods = new String[arr_methods_list.size()];
        for (int i = 0; i < arr_methods_list.size(); i++) {
            lxNode type = arr_methods_list.get(i).get(0);
            while (type.ndOp != tokType._void && type.ndOp != tokType._int) {
                type = type.pstNd;
            }
            String name = getName(type.pstNd.prvNd);
            lxNode temp = type.pstNd;
            ArrayList<tokType> types = new ArrayList<>();
            ArrayList<String> names = new ArrayList<>();
            if (temp.pstNd != null&& type.prvNd==null) {
                temp = temp.pstNd;
                while (temp.ndOp != tokType._int) {
                    types.add(temp.pstNd.ndOp);
                    names.add(getName(temp.pstNd.pstNd));
                    data += getName(temp.pstNd.pstNd) + "_" + name + " dd 0\r\n";
                    temp = temp.prvNd;
                }
                types.add(temp.pstNd.ndOp);
                names.add(getName(temp.pstNd));
                data += getName(temp.pstNd) + "_" + name + " dd 0\r\n";
            }
            int count = types.size();
            lxNode start = nodes.get(type.prnNd);
            while (start.ndOp != tokType._opbz) {
                start = nodes.get(start.prnNd);
            }
            code+=generate_code_method(type, name, types, names, start,count);
            //text += "push ebp\r\nmov ebp, esp\r\n";
            //for (int i = 0; i < types.size(); i++) {
            //    int ind = 4+4*(types.size()-1-i);
            //    text += "mov eax,[ebp+"+ind+"]\r\npush eax\r\n";
            //}
        }
        text+=".data\r\n"+data+".code\r\n"+code;
        writeToFile();
    }

    public String generate_code_method(lxNode type, String name, ArrayList<tokType> types, ArrayList<String> names, lxNode start, int count) {
        String res = "";
        if (type.prvNd==null) {
            res = name + " proc\r\n";
            if (types.size() != 0) {
                res += "push ebp\r\nmov ebp, esp\r\n";
                for (int i = 0; i < types.size(); i++) {
                    int ind = 4 + 4 * (types.size() - 1 - i);
                    res += "mov eax,[ebp+" + ind + "]\r\nmov dword ptr [" + names.get(i) + "], eax\r\n";
                }
            }
        }
        else res+="main: ";
        res+=generate_code_block(name,types,names,start);
        /*if (start.pstNd != null) {
            lxNode temp = start.pstNd;
            while (temp.ndOp != tokType._EOS) {
                temp = temp.prvNd;
            }
        }
        */
        if (type.prvNd==null) {
            res += "pop ebp\r\n";
            res += "ret " + 4 * count + "\r\n";
            res += name + " endp\r\n";
        }
        else res+="end main";
        return res;
    }

    private String generate_code_block(String name, ArrayList<tokType> types, ArrayList<String> names, lxNode start){
        String res = "";
        lxNode temp = start.pstNd;
        while (temp.ndOp == tokType._EOS) {
            temp = temp.prvNd;
        }
        if (temp.ndOp==tokType._return){
            temp = temp.prvNd.pstNd;
        }
        res += generate_compound(name,types,names,temp);
        temp = nodes.get(temp.prnNd);
        while(temp.ndOp!=tokType._opbz){
            if(temp.pstNd!=null) {
                res += generate_compound(name, types, names, temp.pstNd);
            }
            temp = nodes.get(temp.prnNd);
        }
        return res;
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
            data += getName(temp.pstNd.prvNd) + "_" + name + " dd 0\r\n";
            temp = temp.pstNd;
        }
        switch (temp.ndOp) {
            case _ass:
                res+=generateAss(name,types,names,temp);
                break;
            case _add:
                res+=generateAdd(name,types,names,temp);
                break;
            case _nam:
                res+=generateNam(name,types,names,temp);
                break;
            case _for:
                res+=generateFor(name,types,names,temp);
                break;
            case _whileP:
                switch (temp.prvNd.ndOp){
                    case _brkz:
                        res+=generateWhile(name,types,names,temp);
                        break;
                    case _repeat:
                        res+=generateRepeat(name,types,names,temp);
                        break;
                }
                break;
            case _srcn:
                res+=generateSrcn(name,types,names,temp);
               break;
            case _brkz:
                res+=generateOp(name,types,names,temp);
                break;
        }
        return res;
    }

    private String generateOp(String name, ArrayList<tokType> types, ArrayList<String> names, lxNode start){
        String res = "";
        lxNode temp = start;
        String method = getName(temp.prvNd);
        temp=temp.pstNd;
        if (temp!=null) {
            while (temp.ndOp == tokType._comma) {
                temp = temp.prvNd;
            }
            res+=generate_compound(name,types,names,temp);
            res+="push eax\r\n";
            temp = nodes.get(temp.prnNd);
            while(temp.ndOp!=tokType._brkz){
                res+=generate_compound(name,types,names,temp.pstNd);
                res+="push eax\r\n";
                temp = nodes.get(temp.prnNd);
            }
        }
        res+="call " + method+"\r\n";
        //if (getType(method)!=tokType._void)
        //    res+="pop eax\r\n";
        return res;
    }

    private String generateAss(String name, ArrayList<tokType> types, ArrayList<String> names, lxNode start){
        String res="";
        res+=generate_compound(name, types, names,start.pstNd);
        res+="mov dword ptr["+getName(start.prvNd)+"_"+name+"], eax\r\n";
        return res;
    }

    private String generateAdd(String name, ArrayList<tokType> types, ArrayList<String> names, lxNode start){
        String res="";
        res+=generate_compound(name, types, names,start.prvNd);
        res+="push eax\r\n";
        res+=generate_compound(name, types, names,start.pstNd);
        res+="pop edx\r\n";
        res+="add eax, edx\r\n";
        return res;
    }

    private String generateNam(String name, ArrayList<tokType> types, ArrayList<String> names, lxNode start){
        String res="";
        res+="mov eax, dword ptr["+getName(start)+"_"+name+"]\r\n";
        return res;
    }

    private String generateSrcn(String name, ArrayList<tokType> types, ArrayList<String> names, lxNode start){
        String res="";
        int value = Integer.parseInt(getName(start));
        res+="mov eax, "+value+"\r\n";
        return res;
    }

    private String generateFor(String name, ArrayList<tokType> types, ArrayList<String> names, lxNode start){
        String res="";
        res+=generate_compound(name, types, names, start.prvNd.pstNd.prvNd.prvNd); //start condition
        res+="jmp LOOP"+(index+1)+"\r\n";
        int starti = index;
        res+="LOOP"+index+":\r\n";
        index++;
        res+=generate_compound(name, types, names, start.prvNd.pstNd.pstNd);
        res+="LOOP"+index+":\r\n";
        int id = index+1;
        index+=2;
        res+=generate_bool(name, types, names, start.prvNd.pstNd.prvNd.pstNd,"LOOP"+id);
        res+=generate_code_block(name, types, names,start.pstNd);
        res+="jmp LOOP"+starti+"\r\n";
        res+="LOOP"+id+":\r\n";
        return res;
    }

    private String generateWhile(String name, ArrayList<tokType> types, ArrayList<String> names, lxNode start){
        String res="";
        int starti = index;
        res+="LOOP"+index+":\r\n";
        index++;
        res+=generate_bool(name, types, names, start.prvNd.pstNd,"LOOP"+index);
        int id = index;
        index++;
        res+=generate_code_block(name, types, names,start.pstNd);
        res+="jmp LOOP"+starti+"\r\n";
        res+="LOOP"+id+":\r\n";
        return res;
    }

    private String generateRepeat(String name, ArrayList<tokType> types, ArrayList<String> names, lxNode start){
        String res="";
        int starti = index;
        res+="LOOP"+index+":\r\n";
        index++;
        int id = index;
        index++;
        res+=generate_code_block(name, types, names,start.prvNd.pstNd);
        res+=generate_bool(name, types, names, start.pstNd.pstNd,"LOOP"+id);
        res+="jmp LOOP"+starti+"\r\n";
        res+="LOOP"+id+":\r\n";
        return res;
    }

    private String generate_bool(String name, ArrayList<tokType> types, ArrayList<String> names, lxNode start,String loop) {
        String res="";
        lxNode temp = start;
        switch (temp.ndOp) {//_lt, _le, _eq, _ne, _ge, _gt,       // < <= == != >= >
            case _lt:
                res+=generate_compound(name,types,names,temp.prvNd);
                res+="push eax\r\n";
                res+=generate_compound(name,types,names,temp.pstNd);
                res+="pop edx\r\n";
                res+="cmp eax, edx\r\n";
                res+="jge "+loop+"\r\n";
                break;
            case _le:
                res+=generate_compound(name,types,names,temp.prvNd);
                res+="push eax\r\n";
                res+=generate_compound(name,types,names,temp.pstNd);
                res+="pop edx\r\n";
                res+="cmp eax, edx\r\n";
                res+="jg "+loop+"\r\n";
                break;
            case _eq:
                res+=generate_compound(name,types,names,temp.prvNd);
                res+="push eax\r\n";
                res+=generate_compound(name,types,names,temp.pstNd);
                res+="pop edx\r\n";
                res+="cmp eax, edx\r\n";
                res+="jne "+loop+"\r\n";
                break;
            case _ne:
                res+=generate_compound(name,types,names,temp.prvNd);
                res+="push eax\r\n";
                res+=generate_compound(name,types,names,temp.pstNd);
                res+="pop edx\r\n";
                res+="cmp eax, edx\r\n";
                res+="je "+loop+"\r\n";
                break;
            case _ge:
                res+=generate_compound(name,types,names,temp.prvNd);
                res+="push eax\r\n";
                res+=generate_compound(name,types,names,temp.pstNd);
                res+="pop edx\r\n";
                res+="cmp eax, edx\r\n";
                res+="jl "+loop+"\r\n";
                break;
            case _gt:
                res+=generate_compound(name,types,names,temp.prvNd);
                res+="push eax\r\n";
                res+=generate_compound(name,types,names,temp.pstNd);
                res+="pop edx\r\n";
                res+="cmp eax, edx\r\n";
                res+="jle "+loop+"\r\n";
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
