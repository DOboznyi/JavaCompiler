import java.lang.reflect.Array;
import java.util.ArrayList;
public class CodeGenerator {
    String path ="d:\\MyTest.txt";
    String text;

    public void writeToFile() {
        /*try
        {
            if (File.Exists(path))
            {
                File.Delete(path);
            }

            clearText();
            File.AppendAllText(path, text);
        }

        catch (Exception ex)
        {
            Console.WriteLine(ex.ToString());
        }
        */
    }

    public CodeGenerator(String path) {
        text = ".586\n.model flat, stdcall\ninclude \\masm32\\include\\kernel32.inc\ninclude \\masm32\\include\\user32.inc\nincludelib \\masm32\\lib\\kernel32.lib\nincludelib \\masm32\\lib\\user32.lib";
        this.path = path + ".asm";
    }

    lxNode[] nd;
    char[] imgBuf;

    void error() {
        System.out.print("\n\nFix an error and try again later\n\n");
        System.exit(0);
    }

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
        generateBlock(start,finish);
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
                    break;
                case _for:
                    break;
                case _whileN:
                    break;
                case _whileP:
                    break;
            }
        }
    }

    private void generateFor(){

    }

    private void generateOp(lxNode[] nd, int i) {

    }

    private void getOperation(lxNode nd) {
        switch (nd.ndOp) {
            case _asAdd:
            case _add:
                text += "add ";
                break;
            case _asSub:
            case _sub:
                text += "sub ";
                break;
            case _asMul:
            case _mul:
                text += "imul ";
                break;
            case _asDiv:
            case _div:
                text += "idiv ";
                break;
            case _inr:
                text += "inc ";
                break;
            case _dcr:
                text += "dcr ";
                break;
            case _ixbz:
                text += "imul ecx, " + step + "h\n";
                text += "add ";
                break;
        }
    }

    private int searchIndex(lxNode[] nd, lxNode nx) {
        for (int i = 0; i < nd.length; i++) {
            if (nx == nd[i]) {
                return i;
            }
        }
        return -1;
    }

    private node searchNode(lxNode nd) {
        node nx = null;
        SemanticAnalyser sa = new SemanticAnalyser();
        for (int i = 0; i < id.length; i++) {
            if (sa.compare_keys(id[i].name, getName(nd).ToCharArray())) {
                nx = id[i];
            }
        }
        return nx;
    }

    private String getName(lxNode nd) {
        String name = "";
        int k = (int) nd.start;
        while (imgBuf[k] != 0) {
            name += imgBuf[k];
            k++;
        }
        return name;
    }

    private String getType(node nd) {
        if (nd.type == tokType._int) {
            if (nd.lenght == 0) {
                return "word ";
            } else {
                return "dword ";
            }
        } else if (nd.type == tokType._float) {
            if (nd.lenght == 2) {
                return "qword ";
            } else {
                return "dword ";
            }
        } else {
            return "qword ";
        }
    }

    private String ValuetoHex(lxNode nd, node curr) {
        String s = getName(nd);
        if (curr.type == tokType._int) {
            int value = (int) Double.Parse(s);
            if (curr.lenght == 0) {
                text += "mov edx, " + value.ToString("X") + "h\n";
                return "dx";
            } else {
                return value.ToString("X") + "h";
            }
        } else if (curr.type == tokType._float) {
            double value = Double.Parse(s);
            if (curr.lenght == 2) {
                text += "mov edx, " + value.ToString("X") + "h\n";
                return "dx";
            } else {
                return value.ToString("X") + "h";
            }
        } else {
            return "";
        }
    }

    private void clearText() {
        text = text.Replace("push eax\r\npop eax\r\n", "");
        text = text.Replace("push ecx\r\npop ecx\r\n", "");
    }

}
