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


        lxNode[] arr_class = new lxNode[x.nodes.length];

        int i;
        for (i = 0; i < size; i++) {
            arr_class[i] = x.nodes[i];
            if (arr_class[i].ndOp == tokType._opbr) {
                i++;
                break;
            }
        }
        arr_class[i] = x.nodes[size - 1];
        SA.makeTree(arr_class);

        lxNode[][] arr_methods = new lxNode[x.nodes.length][x.nodes.length];
        boolean flag = false;
        int a = 0;
        int id = 0;
        int num = 0;
        int start = 0;
        for (; i < size; i++) {
            while (a != 0 || !flag) {
                start = i;
                arr_methods[num][id] = x.nodes[i];
                switch (arr_methods[num][id].ndOp) {
                    case _opbr:
                        flag = true;
                        a++;
                        break;
                    case _ocbr:
                        a--;
                        break;
                }
                i++;
                if (i>=size-2){
                    break;
                }
                id++;
            }
            if (i>size-2){
                break;
            }
            SA.makeTree(arr_methods[num]);
            id=0;
            num++;
            flag = false;
            a = 0;
        }


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
