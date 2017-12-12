public class Main {

    public static void main(String[] args) {
        System.out.println("Hello World!");
        LexicalAnalyser x = new LexicalAnalyser();
        int nn = -1;
        do
        {
            nn = x.LxAnlzr();
        } while (x.nodes[nn].ndOp != tokType._EOF);

        SyntaxAnalyser SA = new SyntaxAnalyser();
        synNode mainNode = SA.synAnalysis(x.nodes);
        //System.out.println("Lexical analyzing completed!");
        //System.out.println("Error on " + 3 + " lexem: \n have to be \"" + "{" + "\" but we have \"" + "int" + "\"");
        //System.out.println("Syntax analyzing completed!");
        //System.out.println("\nNon declared type!\n");
        //System.out.println("Error at x = " + 4 + " y = " + 1 + " \n");
        //System.out.println("Press Any Key To Continue...");
        //System.out.println("Semantic analyzing completed!");
    }
}
