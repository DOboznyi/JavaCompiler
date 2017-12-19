import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello World!");
        LexicalAnalyser LA = new LexicalAnalyser();
        int nn;
        do {
            nn = LA.LxAnlzr();
        } while (LA.nodes[nn].ndOp != tokType._EOF);

        System.out.println("Lexical analyzing completed!");

        SyntaxAnalyser SA = new SyntaxAnalyser();
        int size = SA.synAnalysis(LA.nodes);
        LA.nodes = SA.thread.pointer;


        ArrayList<lxNode> arr_class_list = new ArrayList<>();

        int i;
        for (i = 0; i < size; i++) {
            arr_class_list.add(LA.nodes[i]);
            if (arr_class_list.get(i).ndOp == tokType._opbr) {
                i++;
                break;
            }
        }
        arr_class_list.add(LA.nodes[size - 1]);
        lxNode[] arr_class = arr_class_list.toArray(new lxNode[arr_class_list.size() + 1]);
        SA.makeTree(arr_class,0);
        for (int j = 0; j < arr_class.length - 1; j++) {
            arr_class_list.set(j, arr_class[j]);
        }

        ArrayList<ArrayList<lxNode>> arr_methods_list = new ArrayList<>();
        //lxNode[][] arr_methods = new lxNode[x.nodes.length][x.nodes.length];
        boolean flag = false;
        int a = 0;
        int num = 0;
        int start;
        for (; i < size; i++) {
            if (i > size - 2) {
                break;
            }
            start = i;
            arr_methods_list.add(new ArrayList<>());
            while (a != 0 || !flag) {
                if (i >= size - 2) {
                    break;
                }
                //start = i;
                //arr_methods[num][id] = x.nodes[i];
                arr_methods_list.get(num).add(LA.nodes[i]);
                switch (LA.nodes[i].ndOp) {
                    case _opbr:
                        flag = true;
                        a++;
                        break;
                    case _ocbr:
                        a--;
                        break;
                }
                i++;
                //id++;
            }
            if (i > size - 2) {
                break;
            }
            lxNode[] arr_methods = arr_methods_list.get(num).toArray(new lxNode[arr_methods_list.get(num).size() + 1]);
            SA.makeTree(arr_methods,start);
            for (int j = 0; j < arr_methods.length - 1; j++) {
                arr_methods_list.get(num).set(j, arr_methods[j]);
            }
            if (arr_methods[3].ndOp==tokType._nam){
                char [] _main = "main".toCharArray();
                boolean f = true;
                int start_n = arr_methods[3].start;
                for (int z=0;z<_main.length;z++){
                    if (LA.imgBuf[start_n+z]!=_main[z]){
                        f=false;
                        break;
                    }
                }
                if (f){
                    arr_methods_list.get(num).get(8).prnNd = 6+start;
                    arr_methods_list.get(num).get(8).pstNd = null;
                    arr_methods_list.get(num).get(8).prvNd = null;
                    arr_methods_list.get(num).get(6).prvNd = arr_methods_list.get(num).get(8);
                    arr_methods_list.get(num).get(7).prvNd = arr_methods_list.get(num).get(8);

                    arr_methods_list.get(num).get(6).prnNd = 5+start;
                    arr_methods_list.get(num).get(7).prnNd = 5+start;
                    arr_methods_list.get(num).get(6).pstNd = null;
                    arr_methods_list.get(num).get(7).pstNd = null;
                    arr_methods_list.get(num).get(5).pstNd = arr_methods_list.get(num).get(6);

                    arr_methods_list.get(num).get(5).prvNd = null;
                    arr_methods_list.get(num).get(3).prvNd = null;
                    arr_methods_list.get(num).get(3).pstNd = null;

                    arr_methods_list.get(num).get(5).prnNd = 4+start;
                    arr_methods_list.get(num).get(3).prnNd = 4+start;
                    arr_methods_list.get(num).get(4).pstNd = arr_methods_list.get(num).get(5);
                    arr_methods_list.get(num).get(9).pstNd = arr_methods_list.get(num).get(5);
                    arr_methods_list.get(num).get(4).prvNd = arr_methods_list.get(num).get(3);
                    arr_methods_list.get(num).get(9).prvNd = arr_methods_list.get(num).get(3);

                    arr_methods_list.get(num).get(4).prnNd = 2+start;
                    arr_methods_list.get(num).get(9).prnNd = 2+start;
                    arr_methods_list.get(num).get(4).ndOp = tokType._brkz;
                    arr_methods_list.get(num).get(9).ndOp = tokType._brkz;
                    arr_methods_list.get(num).get(1).prnNd = 2+start;
                    arr_methods_list.get(num).get(1).prvNd = null;
                    arr_methods_list.get(num).get(1).pstNd = null;
                    arr_methods_list.get(num).get(2).prvNd = arr_methods_list.get(num).get(1);
                    arr_methods_list.get(num).get(2).pstNd = arr_methods_list.get(num).get(4);

                    arr_methods_list.get(num).get(2).prnNd = start;
                    arr_methods_list.get(num).get(0).prvNd = null;
                    arr_methods_list.get(num).get(0).pstNd = arr_methods_list.get(num).get(2);

                    arr_methods_list.get(num).get(0).prnNd = 10+start;
                    arr_methods_list.get(num).get(10).prvNd = arr_methods_list.get(num).get(0);
                    arr_methods_list.get(num).get(arr_methods_list.get(num).size()-1).prvNd = arr_methods_list.get(num).get(0);
                }
            }
            //id = 0;
            num++;
            //arr_methods_list.add(new ArrayList<lxNode>());
            flag = false;
            a = 0;
        }


        ArrayList<lxNode> nodes = new ArrayList<>();
        int last_method = 0;
        int last_EOS = 0;
        for (i = 0; i < arr_class_list.size() - 1; i++) {
            nodes.add(arr_class_list.get(i));
        }
        int start_node = nodes.size()-1;
        flag = true;
        for (int j = 0; j < arr_methods_list.size(); j++) {
            for (i = 0; i < arr_methods_list.get(j).size(); i++) {
                nodes.add(arr_methods_list.get(j).get(i));
                if (nodes.get(nodes.size() - 1).ndOp == tokType._opbz && flag) {
                    if (last_EOS != 0) {
                        nodes.get(nodes.size() - 1).prnNd = last_EOS;
                        nodes.get(last_EOS).pstNd = nodes.get(nodes.size() - 1);
                    }
                    flag = false;
                    last_method = nodes.size() - 1;
                }
            }
            if (last_EOS != 0) {
                nodes.get(nodes.size() - 1).prnNd = last_EOS;
            }
            if (j != arr_methods_list.size() - 1) {
            nodes.add(new lxNode(tokType._EOS));
            flag = true;

                if (last_EOS != 0) {
                    nodes.get(last_EOS).prnNd = nodes.size() - 1;
                    nodes.get(nodes.size() - 1).prvNd = nodes.get(last_EOS);
                } else {
                    if (last_method != 0) {
                        nodes.get(last_method).prnNd = nodes.size() - 1;
                        nodes.get(nodes.size() - 2).prnNd = nodes.size() - 1;
                        nodes.get(nodes.size() - 1).prvNd = nodes.get(last_method);
                    }
                }
                last_EOS = nodes.size() - 1;
            }
        }
        //nodes.remove(nodes.size() - 1);
        nodes.add(arr_class_list.get(arr_class_list.size() - 1));
        if (last_EOS!=0){
            nodes.get(last_EOS).prnNd = start_node;
            nodes.get(start_node).pstNd = nodes.get(last_EOS);
            nodes.get(nodes.size() - 1).pstNd = nodes.get(last_EOS);
        }
        else{
            if (last_method!=0){
                nodes.get(last_method).prnNd = start_node;
                nodes.get(start_node).pstNd = nodes.get(last_method);
                nodes.get(nodes.size() - 1).pstNd = nodes.get(last_EOS);
            }
        }

        //int nr = 0;
        //int nc = 1;
        //x.nodes[0].prnNd = -1;
        //do
        //nr = SA.nxtProd(x.nodes, nr, nc);
        //while (++nc < nn);

        SemanticAnalyser SemA = new SemanticAnalyser(LA.imgBuf,LA.ndxNds);

        System.out.println("Syntax analyzing completed!");

        SemA.Analyze(arr_methods_list,nodes,LA.imgBuf);

        System.out.println("Semantic analyzing completed!");

        CodeGenerator CD = new CodeGenerator(LA.path,LA.imgBuf);

        CD.generate_code(arr_methods_list,nodes);

        System.out.println("Code generation completed!");

        //System.out.println("Lexical analyzing completed!");
        //System.out.println("Error on " + 3 + " lexem: \n have to be \"" + "{" + "\" but we have \"" + "int" + "\"");
        //
        //System.out.println("\nNon declared type!\n");
        //System.out.println("Error at x = " + 4 + " y = " + 1 + " \n");
        //System.out.println("Press Any Key To Continue...");
        //System.out.println("Semantic analyzing completed!");
    }
}
