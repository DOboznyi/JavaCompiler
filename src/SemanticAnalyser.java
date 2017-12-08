public class SemanticAnalyser {

    int lnFPtr = 32;    // довжина покажчика сегмента
    int lnNPtr = 32;    // довжина покажчика у сегменті
    // модифікатори
    int cdPtr = 0x00100000;    // код покажчика 1-го рівня
    int msPtr = 0xfff00000;    // маска рівня покажчика
    int cdCns = 0x00080000;    // код константного типу даних
    int cdArr = 0x00108000;    // код даних типу масиву
    int cdCna = 0x00188000;    // код масиву констант
    int cdReg = 0x00010000;    // код регістрового типу даних
    int cdStt = 0x00020000;    // код статичного типу даних
    int cdAut = 0x00030000;    // код автоматичного типу даних
    int cdExt = 0x00040000;    // код зовнішнього типу даних
    int cdVlt = 0x00070000;    // код примусового типу даних
    int msUTp = 0x00000fff;    // маска номера типу користувача

    public enum datType {
        _v(0),                    // порожній тип даних
        _uc(4), _us(5), _ui(6), _ui64(7),// стандартні цілі без знака
        _sc(8), _ss(9), _si(10), _si64(11),    // стандартні цілі зі знаком
        _f(12), _d(13), _ld(14), _rea(15),        // дані з плаваючою точкою
        _lbl(16), _strn(17),                // мітки
        // інші стандартні типи
        _geq(0x0ffe),        // загальний тип для рівності
        _gen(0x0fff),        // загальний (довільний) тип
        _enm(0x1000),        // перенумеровані типи enum
        _str(0x2000),        // структурні типи /*_record*/,
        _unn(0x3000),        // типи об’єднань union
        _cls(0x4000),        // типи класів
        _obj(0x5000),        // типи об’єктів
        _fun(0x6000),        // функціональні типи
        _ctp(0x7000),        // умовні типи мови Pascal
        _fl(0x7001), _tp(0x7002), _vl(0x7003), _vr(0x7004);    //

        private final int id;

        datType(int id) {
            this.id = id;
        }

        public int getValue() {
            return id;
        }
    }

    datType tpLx[] =    // масив кодів типів констант від типів лексем
            {datType._v,    //0 Eu - Некласифікований об'єкт
                    datType._v,    //1 S0 - Роздільник
                    datType._v,    //2 S1g - Знак числової константи
                    datType._ui,    //3 S1c - Ціле число
                    datType._f,    //4 S2c - Число з точкою
                    datType._v,    //5 S1e - Літера "e" або "E"
                    datType._v,    //6 S1q - Знак "-" або "+"
                    datType._f,    //7 S1p - Десяткові цифри порядку
                    datType._v,    //8 S1n - Елементи імені
                    datType._v,    //9 S1s - Літери рядка або символьної константи
                    datType._v,    //10 S1t - Елементи констант, які перетворюються
                    datType._strn,    //11 S2s - Ознака закінчення константи
                    datType._v,    //12 S2 - Початковий елемент групового роздільника
                    datType._v,    //13 S3 - Наступний елемент групового роздільника
                    datType._ui,    //14?S3c - Ціле число з недесятковою основою
                    datType._v,    //15?S0p - Ознака типу константи
                    datType._v,    //16 Soc- Вісімковий код
                    datType._v,    //17 Scr- Коментар-рядок
                    datType._v,    //18 Scl- Обмежений коментар
                    datType._v,    //19 Ec - Неправильна константа
                    datType._v,    //20 Ep - Неправильна константа з точкою
                    datType._v,    // Eq - Неправильна константа з порядком
                    datType._v,    // En - Неправильне ім'я
                    datType._v    // Eo - Неприпустиме сполучення операцій
            };

    class indStrUS// структура індексу у вигляді двійкового дерева
    {
        lxNode pKyStr;//вказівник на вузол
        indStrUS pLtPtr;//вказівник вліво
        indStrUS pRtPtr;//вказівник вправо
        int dif;
    }

    ;

    class recrdTMD    // структура рядка таблиці базових типів
    {
        datType tpLx;// примірник структури ключа
        int md;    // модифікатор
        int ln;    // базова або гранична довжина даних типу
    }

    class recrdTPD    // структура рядка таблиці модифікованих типів
    {
        tokType[] kTp = new tokType[3];// примірник структури ключа
        int dTp;//enum datType примірник функціональної частини
        int ln;    // базова довжина даних типу
    }

    ;

    class gnDat    // тип узагальненого акумулятора арифметичних даних
    {
        int _id;    // поле 4-байтних цілих даних
        short _sd;    // поле 2-байтних цілих даних
        char _cd;    // поле 1-байтних цілих даних
        float _fd;    // поле 4-байтних даних з плаваючою точкою
        double _dd;    // поле 8-байтних даних з плаваючою точкою
        long _i8;    // поле 8-байтних цілих даних
    }

    class recrdSMA    // структура рядка таблиці операцій
    {
        tokType oprtn;// код операції
        int oprd1, ln1;    // код типу та довжина першого аргументу
        int oprd2, ln2;    // код типу та довжина другого аргументу
        int res, lnRes;    // код типу та довжина результату
        //_fop	*pintf;	// покажчик на функцію інтерпретації
        char[] assCd;    // покажчик на текст макроса

        public recrdSMA(tokType oprtn, int oprd1, int ln1, int oprd2, int ln2, int res, int lnRes) {
            this.oprtn = oprtn;
            this.oprd1 = oprd1;
            this.ln1 = ln1;
            this.oprd2 = oprd2;
            this.ln2 = ln2;
            this.res = res;
            this.lnRes = lnRes;
        }
    }

    ;


    int lnCod[] =            // вектор довжин типів
            {0, 0, 0, 0,
                    8, 16, 32, 64,
                    8, 16, 32, 64,//
                    32, 64, 80, 48,
                    datType._lbl.ordinal(),//
                    datType._str.ordinal(), datType._unn.ordinal(),
            };

    // порівняння рядків
    int cmpStr(recrdSMA s1, recrdSMA s2) {
        int n = 0;
        if (s1.oprtn != s2.oprtn) return s1.oprtn.ordinal() - s2.oprtn.ordinal();
        if ((s1.oprd1 - s2.oprd1) != 0) return s1.oprd1 - s2.oprd1;
        if ((s1.ln1 - s2.ln1) != 0) return s1.ln1 - s2.ln1;
        if ((s1.oprd2 - s2.oprd2) != 0) return s1.oprd2 - s2.oprd2;
        return s1.ln2 - s2.ln2;
    }

    // вибірка за двійковим пошуком
    recrdSMA selBin(recrdSMA kArg, recrdSMA[] tb, int ln) {
        for (int i = 0; i < ln; i++) {
            if (cmpStr(tb[i], kArg) == 0) {
                return tb[i];
            }
        }
        return null;
    /*int i, nD=-1, nU=ln, n=(nD+nU)>>1;
 while(i=cmpStr(tb+n,kArg))
	{if(i>0)nU=n;else nD=n;
	 n=(nD+nU)>>1;
	 if(n==nD)
		 return NULL;
	}
 return &tb[n];
 */
    }

    // порівняння рядків
    int cmpStr(tokType s1[], tokType s2[]) {
        int n = 0;
        while (s1[n] == s2[n] && n < 2) n++;
        return s1[n].ordinal() - s2[n].ordinal();
    }

    // вибірка за двійковим пошуком
    recrdTPD selBin(tokType kArg[], recrdTPD[] tb, int ln) {
        int i, nD = -1, nU = ln, n = (nD + nU) >> 1;
        while ((i = cmpStr(tb[n].kTp, kArg)) != 0) {
            if (i > 0) nU = n;
            else nD = n;
            n = (nD + nU) >> 1;
            if (n == nD) return null;
        }
        return tb[n];
    }

    void error(lxNode nd) {
        System.out.println("Error at x = " + nd.x + " y = " + nd.y + " \n");
        System.out.println("Press Any Key To Continue...");
        new java.util.Scanner(System.in).nextLine();
        System.exit(1);
    }

    // порівняння рядків
// порівняння терміналів за відношенням порядку
    int cmpTrm(lxNode k0, lxNode kArg)//cmpKys
    {
        int i = cmpStr(( char*)k0.prvNd,
            (char*)kArg.prvNd);
        if (i != 0) return i;
        return k0.stkLength - kArg.stkLength; // порівняння номерів модулів
    }

    // вибірка через пошук за двійковим деревом
    indStrUS selBTr(lxNode kArg, indStrUS rtTb) {
        int df;
        while ((df = cmpTrm(kArg, rtTb.pKyStr))!=0)
            if (df > 0) {
                if (rtTb.pRtPtr!=null) rtTb = rtTb.pRtPtr;
                else break;
            } else {
                if (rtTb.pLtPtr!=null) rtTb = rtTb.pLtPtr;
                else break;
            }
        rtTb.dif = df;
        return rtTb;
    }

    void prDtLst(lxNode nd) {
        if (nd.ndOp == tokType._comma) {
            prDtLst(nd.prvNd);
            nd.pstNd.dataType = tpLx[nd.pstNd.dataType].ordinal();
            convNum(nd.pstNd); //nInCr++;//,	enum ltrTypeS ltrCls[256]);
        } else if (nd.ndOp == tokType._srcn) {
            nd.dataType = tpLx[nd.dataType].ordinal();
            convNum(nd); //nInCr++;//,	enum ltrTypeS ltrCls[256]);
        }
    }

    datType SmAnDcl(int tpCod, lxNode nd) {
        if (nd.ndOp == tokType._comma) {
            SmAnDcl(tpCod, nd.prvNd);
            SmAnDcl(tpCod, nd.pstNd);
        } else if (nd.ndOp == tokType._ass) {
            SmAnDcl(tpCod, nd.prvNd);
            if (nd.pstNd.ndOp == tokType._srcn) {
                nd.pstNd.dataType = tpLx[nd.pstNd.dataType].ordinal();
                convNum(nd.pstNd); //nInCr++;//,	enum ltrTypeS ltrCls[256]);
//		 nd.pstNd.resLength=lnCod[nd.pstNd.dataType];
            } else if (nd.pstNd.ndOp == tokType._tdbz) {
                prDtLst(nd.pstNd.pstNd);
            }
        } else if (nd.ndOp == tokType._refU)//_mul)//унарна *
        {
            if (nd.prnNd != 0) SmAnDcl(tpCod + cdPtr, nd.pstNd);
        } else if (nd.ndOp == tokType._ixbz) {
            SmAnDcl((tpCod + cdPtr) | cdArr, nd.prvNd);
            if (nd.pstNd.ndOp == tokType._srcn) {
                nd.pstNd.dataType = datType._ui.ordinal();
                convNum(nd.pstNd); //nInCr++;//,	enum ltrTypeS ltrCls[256]);
//		 nd.pstNd.resLength=lnCod[nd.pstNd.dataType];
            }
//	 SmAnDcl(_ui,nd.pstNd);
            nd.dataType = tpCod;
            if ((tpCod & msStp) >= datType._f) tpCod -= datType._f.ordinal() - 12;
            nd.resLength = lnCod[tpCod & 0x7FF];
        } else if (nd.ndOp == tokType._nam) {
            nd.dataType = tpCod;
            if ((tpCod & msStp) >= datType._f) tpCod -= datType._f.ordinal() - 12;
            nd.resLength = lnCod[tpCod & 0x7fff];
            if (tpCod & msPtr) nd.resLength = lnFPtr;
        }
        return datType.values()[tpCod];
    }

    recrdSMA ftImp = new recrdSMA(tokType._nil, datType._v.ordinal(), 0, datType._v.ordinal(), 0, datType._v.ordinal(), 0);// таблиця припустимості типів для операцій
    int nbBlk = 0;
    tokType[] lPrv = {tokType._void, tokType._void, tokType._void};// масив для накопичення ключових слів типа

    int SmAnlzr(lxNode nd,    // покажчик на початок масиву вузлів
                int nR)    // номер кореневого вузла
    {//enum datType
        int tPrv, tPst;    // типи вузлів попередника та наступника
        int lnPrv, lnPst;    // довжини попередника та наступника
        char[] name;        // робочий покажчик на і'мя
        recrdTPD pRc;
        indStrUS pRtNdx;// робочий покажчик вузла двійкового дерева імен
        if (nd.ndOp.ordinal() >= tokType._void.ordinal() && nd.ndOp.ordinal() <= tokType._string.ordinal()) {
            lPrv[0] = nd.ndOp;
            if (nd.prvNd != null/*&&nd.prvNd.ndOp<=_const*/)// якщо не одне слово визначає тип
            {
                lPrv[1] = nd.prvNd.ndOp;
                if (nd.prvNd.prvNd != null)//якщо не два слова задають тип
                    lPrv[2] = nd.prvNd.prvNd.ndOp;
                else lPrv[2] = tokType._void;
            } else {
                lPrv[1] = tokType._void;
                lPrv[2] = tokType._void;
            }
            pRc = selBin(lPrv, tpTbl, 126);//пошук складеного типа
            if (pRc != null)    // якщо тип існує
            {
                tPrv = pRc.dTp;
                lnPrv = pRc.ln;
                if (nd.ndOp.ordinal() >= tokType._enum.ordinal() && nd.ndOp.ordinal() <= tokType._union.ordinal())// якщо тип визначено користувачем
                {
                    nd.prvNd.dataType = nd.ndOp.ordinal();
                    tPrv = tPrv + (++nbBlk);
                }
//		 if(nd.ndOp==_enum)
            } else {
                System.out.println("\nNon declared type!\n");
                error(nd);
            }
            SmAnDcl(tPrv, nd.pstNd);// визначити тип
        } else if (nd.ndOp == tokType._nam)//якщо термінал-ім'я
        {
            pRtNdx = selBTr(nd, ndxNds);//пошук імені
            // якщо не знайдено - неописане ім'я            !!!!!!!!!!!!!
            if (pRtNdx == null) {
                System.out.println("\nUndeclared identifier!\n");
                error(nd);
            }
            name = (char*)pRtNdx.pKyStr.prvNd;
		/*	nd.pstNd=pRtNdx.pKyStr.pstNd;*/
            nd.dataType = tPrv = pRtNdx.pKyStr.dataType;
            nd.resLength = (int) pRtNdx.pKyStr.resLength;
        } else if (nd.ndOp == tokType._srcn)//якщо термінал-константа
        {
            nd.dataType = tpLx[nd.dataType];
            tPrv = nd.dataType;
            convNum(nd/*.pstNd*/); //перетворення константи на внутрішню
            //nInCr++;//,	enum ltrTypeS ltrCls[256]);
            //  nd.resLength=lnCod[tPrv];
        } else {
            if (nd.ndOp == tokType._remL)
                return datType._v.ordinal();
            if (nd.prvNd != null && nd.ndOp != tokType._nam && nd.ndOp != tokType._srcn) {
                if (nd.ndOp != tokType._brkz && nd.prnNd != 0) {
                    tPrv = SmAnlzr(nd.prvNd, nR);
                    lnPrv = nd.prvNd.resLength;
                }
            } else {
                ftImp.oprd1 = tPrv = datType._v.ordinal();
                ftImp.ln1 = 0;
            }
            if ((nd.pstNd != null) && (nd.ndOp != tokType._nam) && (nd.ndOp != tokType._srcn)) {
                tPst = SmAnlzr(nd.pstNd, nR);
                lnPst = nd.pstNd.resLength;
            } else {
                ftImp.oprd2 = tPst = datType._v.ordinal();
                ftImp.ln2 = 0;
            }
            if (nd.ndOp == tokType._EOS) {
                nd.dataType = datType._v.ordinal();
                nd.resLength = 0;
            } else if (nd.ndOp == tokType._brkz) {
                if (nd.prvNd == null) {
                    nd.dataType = nd.pstNd.dataType;
                    nd.resLength = nd.pstNd.resLength;
                    tPrv = nd.dataType;
                } else {
                    nd.dataType = nd.prvNd.dataType;
                    nd.resLength = nd.prvNd.resLength;
                    if (nd.prvNd != null && nd.prvNd.ndOp == tokType._refU) {
                        tPrv = tPst - 0x00100000;//cdPtr;
                        nd.dataType = tPrv;
                        if (tPrv >= 0x00100000)//cdPtr)
                            nd.resLength = 32;
                        else
                            nd.resLength = lnCod[tPrv & 0x7FF];
                    } else
                        tPrv = nd.dataType;
                }
            } else {
                ftImp.oprd1 = tPrv & 0xffff7fff;
                if (ftImp.oprtn.ordinal() >= tokType._asOr.ordinal() && ftImp.oprtn.ordinal() <= tokType._ass.ordinal())
                    tPrv &= 0xfff7ffff;
                if (tPrv != datType._v.ordinal())
                    ftImp.ln1 = lnPrv;
                else
                    ftImp.ln1 = lnPrv = 0;
                ftImp.oprd2 = tPst & 0xffff7fff;
                if (tPst != datType._v.ordinal())
                    ftImp.ln2 = lnPst;
                else
                    ftImp.ln2 = lnPst = 0;
                ftImp.oprtn = nd.ndOp;
                recrdSMA pftImp = selBin(ftImp, ftTbl, 361);
                if (ftImp.oprtn == tokType._ass && (ftImp.ln1 == 0 || ftImp.ln2 == 0)) {
                    System.out.println("\nUnresolved identificator!\n");
                    error(nd);
                }
                if (pftImp != null) {
                    nd.dataType = pftImp.res;
                    tPrv = pftImp.res;
                    nd.resLength = pftImp.lnRes;
                } else {
                    System.out.println("\nCheck types!\n");
                    error(nd);
                }
            }
        }
        return tPrv;
    }
}
