public class indStrUS// структура індексу у вигляді двійкового дерева
{
    lxNode pKyStr;//вказівник на вузол
    indStrUS pLtPtr;//вказівник вліво
    indStrUS pRtPtr;//вказівник вправо
    int dif;

    public indStrUS(lxNode pKyStr, indStrUS pLtPtr, indStrUS pRtPtr, int dif) {
        this.pKyStr = pKyStr;
        this.pLtPtr = pLtPtr;
        this.pRtPtr = pRtPtr;
        this.dif = dif;
    }
}