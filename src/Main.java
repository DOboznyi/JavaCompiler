import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello World!");
        LexicalAnalyser x = new LexicalAnalyser();
        int nn = -1;
        do {
            nn = x.LxAnlzr();
        } while (x.nodes[nn].ndOp != tokType._EOF);

        SyntaxAnalyser SA = new SyntaxAnalyser();
        int size = SA.synAnalysis(x.nodes);
        x.nodes = SA.thread.pointer;


        ArrayList<lxNode> arr_class_list = new ArrayList<lxNode>();

        int i;
        for (i = 0; i < size; i++) {
            arr_class_list.add(x.nodes[i]);
            if (arr_class_list.get(i).ndOp == tokType._opbr) {
                i++;
                break;
            }
        }
        arr_class_list.add(x.nodes[size - 1]);
        lxNode[] arr_class = arr_class_list.toArray(new lxNode[arr_class_list.size()+1]);
        SA.makeTree(arr_class);
        for (int j=0;j<arr_class.length-1;j++){
            arr_class_list.set(j,arr_class[j]);
        }

        ArrayList<ArrayList<lxNode>> arr_methods_list=new ArrayList<>();
        //lxNode[][] arr_methods = new lxNode[x.nodes.length][x.nodes.length];
        boolean flag = false;
        int a = 0;
        int id = 0;
        int num = 0;
        int start = 0;
        for (; i < size; i++) {
            arr_methods_list.add(new ArrayList<lxNode>());
            while (a != 0 || !flag) {
                if (i>=size-2){
                    break;
                }
                start = i;
                //arr_methods[num][id] = x.nodes[i];
                arr_methods_list.get(num).add(x.nodes[i]);
                switch (x.nodes[i].ndOp) {
                    case _opbr:
                        flag = true;
                        a++;
                        break;
                    case _ocbr:
                        a--;
                        break;
                }
                i++;
                id++;
            }
            if (i>=size-2){
                break;
            }
            lxNode[] arr_methods = arr_methods_list.get(num).toArray(new lxNode[arr_methods_list.get(num).size()+1]);
            SA.makeTree(arr_methods);
            for (int j=0;j<arr_methods.length-1;j++){
                arr_methods_list.get(num).set(j,arr_methods[j]);
            }
            id=0;
            num++;
            //arr_methods_list.add(new ArrayList<lxNode>());
            flag = false;
            a = 0;
        }


        i=0;
        ArrayList<lxNode> nodes =new ArrayList<>();
        for (i=0;i<arr_class_list.size()-1;i++){
            nodes.add(arr_class_list.get(i));
        }
        for (int j = 0;j<arr_methods_list.size();j++) {
            for (i = 0; i < arr_methods_list.get(j).size(); i++) {
                nodes.add(arr_methods_list.get(j).get(i));
            }
            nodes.add(new lxNode(tokType._EOS));
        }
        nodes.remove(nodes.size()-1);
        nodes.add(arr_class_list.get(arr_class_list.size()-1));

        //int nr = 0;
        //int nc = 1;
        //x.nodes[0].prnNd = -1;
        //do
            //nr = SA.nxtProd(x.nodes, nr, nc);
        //while (++nc < nn);

        System.out.println("Syntax analyzing completed!");

        //System.out.println("Lexical analyzing completed!");
        //System.out.println("Error on " + 3 + " lexem: \n have to be \"" + "{" + "\" but we have \"" + "int" + "\"");
        //
        //System.out.println("\nNon declared type!\n");
        //System.out.println("Error at x = " + 4 + " y = " + 1 + " \n");
        //System.out.println("Press Any Key To Continue...");
        //System.out.println("Semantic analyzing completed!");
    }
}
