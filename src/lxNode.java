public class lxNode
{
    public tokType ndOp;	 //код типу лексеми
    public lxNode prvNd;// зв'язок з попередником
    public lxNode pstNd;// зв'язок з наступником
    public int dataType;  // код типу даних, які повертаються
    public int resLength;  //довжина результату
    public int x, y, f;//координати розміщення у вхідному файлі
    public int prnNd;//struct lxNode* prnNd;//зв'язок з батьківським вузлом
    public int stkLength;//довжина стека обробки семантики або номер модуля}
    public int start;

    public lxNode(tokType ndOp, lxNode prvNd, lxNode pstNd, int dataType, int resLength, int x, int y, int f, int prnNd, int stkLength, int start)
    {
        this.ndOp = ndOp;
        this.prvNd = prvNd;
        this.pstNd = pstNd;
        this.dataType = dataType;
        this.resLength = resLength;
        this.x = x;
        this.y = y;
        this.f = f;
        this.prnNd = prnNd;
        this.stkLength = stkLength;
        this.start = start;
    }

    public lxNode(tokType tok){
        this.ndOp = tok;
        this.prvNd = null;
        this.pstNd = null;;
        this.dataType = 0;
        this.resLength = 0;
        this.x = 0;
        this.y = 0;
        this.f = 0;
        this.prnNd = 0;
        this.stkLength = 0;
        this.start = 0;
    }
}
