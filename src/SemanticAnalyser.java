import com.sun.org.apache.bcel.internal.classfile.Code;

import java.util.ArrayList;
import java.util.Arrays;


public class SemanticAnalyser {
    char[] imgBuf;
    private int difV;

    SemanticAnalyser(char[] imgBuf, indStrUS[] ndxNds) {
        this.imgBuf = imgBuf;
        this.ndxNds = ndxNds;
    }

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
    int msStp = 0x00007fff;    // маска стандартних типів

    recrdSMA[] ftTbl =    // таблиця припустимості типів для операцій
            {new recrdSMA(tokType._ass, datType._ui.getValue(), 32, datType._ui.getValue(), 32, datType._ui.getValue(), 32),
                    new recrdSMA(tokType._ass, datType._ui.getValue(), 32, datType._si.getValue(), 32, datType._ui.getValue(), 32),
                    new recrdSMA(tokType._ass, datType._si.getValue(), 32, datType._si.getValue(), 32, datType._si.getValue(), 32),
                    new recrdSMA(tokType._ass, datType._si.getValue(), 32, datType._f.getValue(), 32, datType._si.getValue(), 32),
                    new recrdSMA(tokType._ass, datType._f.getValue(), 32, datType._ui.getValue(), 32, datType._f.getValue(), 32),
                    new recrdSMA(tokType._ass, datType._f.getValue(), 32, datType._si.getValue(), 32, datType._f.getValue(), 32),
                    new recrdSMA(tokType._ass, datType._f.getValue(), 32, datType._f.getValue(), 32, datType._f.getValue(), 32),
                    new recrdSMA(tokType._ass, datType._f.getValue(), 32, datType._d.getValue(), 64, datType._f.getValue(), 32),
                    new recrdSMA(tokType._ass, datType._d.getValue(), 64, datType._ui.getValue(), 32, datType._d.getValue(), 32),
                    new recrdSMA(tokType._ass, datType._d.getValue(), 64, datType._si.getValue(), 32, datType._d.getValue(), 64),
                    new recrdSMA(tokType._ass, datType._d.getValue(), 64, datType._f.getValue(), 32, datType._d.getValue(), 64),
                    new recrdSMA(tokType._ass, datType._d.getValue(), 64, datType._d.getValue(), 64, datType._d.getValue(), 64),
                    new recrdSMA(tokType._ass, datType._ui.getValue() + cdPtr, 32, datType._ui.getValue() + cdPtr, 32, datType._ui.getValue() + cdPtr, 32),
                    new recrdSMA(tokType._ass, datType._ui.getValue() + cdPtr, 32, datType._si.getValue() + cdPtr, 32, datType._ui.getValue() + cdPtr, 32),
                    new recrdSMA(tokType._ass, datType._si.getValue() + cdPtr, 32, datType._ui.getValue() + cdPtr, 32, datType._si.getValue() + cdPtr, 32),
                    new recrdSMA(tokType._ass, datType._si.getValue() + cdPtr, 32, datType._si.getValue() + cdPtr, 32, datType._si.getValue() + cdPtr, 32),
                    new recrdSMA(tokType._ass, datType._f.getValue() + cdPtr, 32, datType._f.getValue() + cdPtr, 32, datType._f.getValue() + cdPtr, 32),
                    new recrdSMA(tokType._ass, datType._d.getValue() + cdPtr, 32, datType._d.getValue() + cdPtr, 32, datType._d.getValue() + cdPtr, 32),
                    new recrdSMA(tokType._lt, datType._f.getValue(), 32, datType._f.getValue(), 32, datType._ui.getValue(), 32),
                    new recrdSMA(tokType._le, datType._f.getValue(), 32, datType._f.getValue(), 32, datType._ui.getValue(), 32),
                    new recrdSMA(tokType._eq, datType._f.getValue(), 32, datType._f.getValue(), 32, datType._ui.getValue(), 32),
                    new recrdSMA(tokType._ne, datType._ui.getValue(), 32, datType._ui.getValue(), 32, datType._ui.getValue(), 32),
                    new recrdSMA(tokType._ne, datType._ui.getValue(), 32, datType._si.getValue(), 32, datType._ui.getValue(), 32),
                    new recrdSMA(tokType._ne, datType._ui.getValue(), 32, datType._f.getValue(), 32, datType._ui.getValue(), 32),
                    new recrdSMA(tokType._ne, datType._ui.getValue(), 32, datType._d.getValue(), 64, datType._ui.getValue(), 32),
                    new recrdSMA(tokType._ne, datType._si.getValue(), 32, datType._ui.getValue(), 32, datType._ui.getValue(), 32),
                    new recrdSMA(tokType._ne, datType._si.getValue(), 32, datType._si.getValue(), 32, datType._ui.getValue(), 32),
                    new recrdSMA(tokType._ne, datType._si.getValue(), 32, datType._f.getValue(), 32, datType._ui.getValue(), 32),
                    new recrdSMA(tokType._ne, datType._si.getValue(), 32, datType._d.getValue(), 64, datType._ui.getValue(), 32),
                    new recrdSMA(tokType._ne, datType._f.getValue(), 32, datType._ui.getValue(), 32, datType._ui.getValue(), 32),
                    new recrdSMA(tokType._ne, datType._f.getValue(), 32, datType._si.getValue(), 32, datType._ui.getValue(), 32),
                    new recrdSMA(tokType._ne, datType._f.getValue(), 32, datType._f.getValue(), 32, datType._ui.getValue(), 32),
                    new recrdSMA(tokType._ne, datType._f.getValue(), 32, datType._d.getValue(), 64, datType._ui.getValue(), 32),
                    new recrdSMA(tokType._ne, datType._d.getValue(), 64, datType._ui.getValue(), 32, datType._ui.getValue(), 32),
                    new recrdSMA(tokType._ne, datType._d.getValue(), 64, datType._si.getValue(), 32, datType._ui.getValue(), 32),
                    new recrdSMA(tokType._ne, datType._d.getValue(), 64, datType._f.getValue(), 32, datType._ui.getValue(), 32),
                    new recrdSMA(tokType._ne, datType._d.getValue(), 64, datType._d.getValue(), 64, datType._ui.getValue(), 32),
                    new recrdSMA(tokType._ne, datType._ui.getValue() + cdPtr, 32, datType._ui.getValue() + cdPtr, 32, datType._ui.getValue(), 32),
                    new recrdSMA(tokType._ne, datType._si.getValue() + cdPtr, 32, datType._si.getValue() + cdPtr, 32, datType._ui.getValue(), 32),
                    new recrdSMA(tokType._ne, datType._f.getValue() + cdPtr, 32, datType._f.getValue() + cdPtr, 32, datType._ui.getValue(), 32),
                    new recrdSMA(tokType._ne, datType._d.getValue() + cdPtr, 32, datType._d.getValue() + cdPtr, 32, datType._ui.getValue(), 32),
                    new recrdSMA(tokType._ge, datType._f.getValue(), 32, datType._f.getValue(), 32, datType._ui.getValue(), 32),
                    new recrdSMA(tokType._gt, datType._f.getValue(), 32, datType._f.getValue(), 32, datType._ui.getValue(), 32),
                    new recrdSMA(tokType._addU, datType._v.getValue(), 0, datType._ui.getValue(), 32, datType._ui.getValue(), 32),
                    new recrdSMA(tokType._addU, datType._v.getValue(), 0, datType._si.getValue(), 32, datType._si.getValue(), 32),
                    new recrdSMA(tokType._addU, datType._v.getValue(), 0, datType._f.getValue(), 32, datType._f.getValue(), 32),
                    new recrdSMA(tokType._addU, datType._v.getValue(), 0, datType._d.getValue(), 64, datType._d.getValue(), 64),
                    new recrdSMA(tokType._addU, datType._v.getValue(), 0, datType._ui.getValue() | cdPtr, 32, datType._ui.getValue() | cdPtr, 32),
                    new recrdSMA(tokType._addU, datType._v.getValue(), 0, datType._si.getValue() | cdPtr, 32, datType._si.getValue() | cdPtr, 32),
                    new recrdSMA(tokType._addU, datType._v.getValue(), 0, datType._f.getValue() | cdPtr, 32, datType._f.getValue() | cdPtr, 32),
                    new recrdSMA(tokType._addU, datType._v.getValue(), 0, datType._d.getValue() | cdPtr, 32, datType._d.getValue() | cdPtr, 32),
                    new recrdSMA(tokType._subU, datType._v.getValue(), 0, datType._ui.getValue(), 32, datType._ui.getValue(), 32),
                    new recrdSMA(tokType._subU, datType._v.getValue(), 0, datType._si.getValue(), 32, datType._si.getValue(), 32),
                    new recrdSMA(tokType._subU, datType._v.getValue(), 0, datType._f.getValue(), 32, datType._f.getValue(), 32),
                    new recrdSMA(tokType._subU, datType._v.getValue(), 0, datType._d.getValue(), 64, datType._d.getValue(), 64),
                    new recrdSMA(tokType._refU, datType._v.getValue(), 0, datType._ui.getValue() | cdPtr, 32, datType._ui.getValue(), 32),
                    new recrdSMA(tokType._refU, datType._v.getValue(), 0, datType._si.getValue() | cdPtr, 32, datType._si.getValue(), 32),
                    new recrdSMA(tokType._refU, datType._v.getValue(), 0, datType._f.getValue() | cdPtr, 32, datType._f.getValue(), 32),
                    new recrdSMA(tokType._refU, datType._v.getValue(), 0, datType._d.getValue() | cdPtr, 32, datType._d.getValue(), 64),
                    new recrdSMA(tokType._ptrU, datType._v.getValue(), 0, datType._ui.getValue(), 32, datType._ui.getValue() | cdPtr, 32),
                    new recrdSMA(tokType._ptrU, datType._v.getValue(), 0, datType._si.getValue(), 32, datType._si.getValue() | cdPtr, 32),
                    new recrdSMA(tokType._ptrU, datType._v.getValue(), 0, datType._f.getValue(), 32, datType._f.getValue() | cdPtr, 32),
                    new recrdSMA(tokType._ptrU, datType._v.getValue(), 0, datType._d.getValue(), 64, datType._d.getValue() | cdPtr, 32),
                    new recrdSMA(tokType._mod, datType._ui.getValue(), 32, datType._ui.getValue(), 32, datType._ui.getValue(), 32),
                    new recrdSMA(tokType._mod, datType._ui.getValue(), 32, datType._si.getValue(), 32, datType._si.getValue(), 32),
                    new recrdSMA(tokType._mod, datType._si.getValue(), 32, datType._ui.getValue(), 32, datType._si.getValue(), 32),
                    new recrdSMA(tokType._mod, datType._si.getValue(), 32, datType._si.getValue(), 32, datType._si.getValue(), 32),
                    new recrdSMA(tokType._orB, datType._ui.getValue(), 32, datType._ui.getValue(), 32, datType._ui.getValue(), 32),
                    new recrdSMA(tokType._orB, datType._ui.getValue(), 32, datType._si.getValue(), 32, datType._si.getValue(), 32),
                    new recrdSMA(tokType._orB, datType._si.getValue(), 32, datType._ui.getValue(), 32, datType._si.getValue(), 32),
                    new recrdSMA(tokType._orB, datType._si.getValue(), 32, datType._si.getValue(), 32, datType._si.getValue(), 32),
                    new recrdSMA(tokType._andB, datType._ui.getValue(), 32, datType._ui.getValue(), 32, datType._ui.getValue(), 32),
                    new recrdSMA(tokType._andB, datType._ui.getValue(), 32, datType._si.getValue(), 32, datType._si.getValue(), 32),
                    new recrdSMA(tokType._andB, datType._si.getValue(), 32, datType._ui.getValue(), 32, datType._si.getValue(), 32),
                    new recrdSMA(tokType._andB, datType._si.getValue(), 32, datType._si.getValue(), 32, datType._si.getValue(), 32),
                    new recrdSMA(tokType._xorB, datType._ui.getValue(), 32, datType._ui.getValue(), 32, datType._ui.getValue(), 32),
                    new recrdSMA(tokType._xorB, datType._ui.getValue(), 32, datType._si.getValue(), 32, datType._si.getValue(), 32),
                    new recrdSMA(tokType._xorB, datType._si.getValue(), 32, datType._ui.getValue(), 32, datType._si.getValue(), 32),
                    new recrdSMA(tokType._xorB, datType._si.getValue(), 32, datType._si.getValue(), 32, datType._si.getValue(), 32),
                    new recrdSMA(tokType._or, datType._ui.getValue(), 32, datType._ui.getValue(), 32, datType._ui.getValue(), 32),
                    new recrdSMA(tokType._and, datType._ui.getValue(), 32, datType._ui.getValue(), 32, datType._ui.getValue(), 32),
                    new recrdSMA(tokType._ixbz, datType._ui.getValue() + cdPtr, 32, datType._ui.getValue(), 32, datType._ui.getValue(), 32),
                    new recrdSMA(tokType._ixbz, datType._ui.getValue() + cdPtr, 32, datType._si.getValue(), 32, datType._ui.getValue(), 32),
                    new recrdSMA(tokType._ixbz, datType._si.getValue() + cdPtr, 32, datType._ui.getValue(), 32, datType._si.getValue(), 32),
                    new recrdSMA(tokType._ixbz, datType._si.getValue() + cdPtr, 32, datType._si.getValue(), 32, datType._si.getValue(), 32),
                    new recrdSMA(tokType._ixbz, datType._f.getValue() + cdPtr, 32, datType._ui.getValue(), 32, datType._f.getValue(), 32),
                    new recrdSMA(tokType._ixbz, datType._f.getValue() + cdPtr, 32, datType._si.getValue(), 32, datType._f.getValue(), 32),
                    new recrdSMA(tokType._ixbz, datType._d.getValue() + cdPtr, 64, datType._ui.getValue(), 32, datType._d.getValue(), 32),
                    new recrdSMA(tokType._ixbz, datType._d.getValue() + cdPtr, 32, datType._si.getValue(), 32, datType._d.getValue(), 32),
                    new recrdSMA(tokType._ixbz, datType._d.getValue() + cdPtr, 64, datType._si.getValue(), 32, datType._d.getValue(), 32),
            };

    recrdTPD[] tpTbl =    // таблиця модифікованих типів
            {new recrdTPD(tokType._void, tokType._void, tokType._void, datType._v.getValue(), 0),
                    new recrdTPD(tokType._void, tokType._extern, tokType._void, datType._v.getValue() + cdExt, 0),
                    new recrdTPD(tokType._void, tokType._const, tokType._void, datType._v.getValue() + cdCns, 0),
                    new recrdTPD(tokType._void, tokType._register, tokType._void, datType._v.getValue() + cdReg, 0),
                    new recrdTPD(tokType._void, tokType._auto, tokType._void, datType._v.getValue() + cdAut, 0),
                    new recrdTPD(tokType._void, tokType._static, tokType._void, datType._v.getValue() + cdStt, 0),
                    new recrdTPD(tokType._enum, tokType._void, tokType._void, datType._enm.getValue(), 32),
                    new recrdTPD(tokType._enum, tokType._extern, tokType._void, datType._enm.getValue() + cdExt, 32),
                    new recrdTPD(tokType._enum, tokType._const, tokType._void, datType._enm.getValue() + cdCns, 32),
                    new recrdTPD(tokType._enum, tokType._register, tokType._void, datType._enm.getValue() + cdReg, 32),
                    new recrdTPD(tokType._enum, tokType._auto, tokType._void, datType._enm.getValue() + cdAut, 32),
                    new recrdTPD(tokType._enum, tokType._static, tokType._void, datType._enm.getValue() + cdStt, 32),
                    new recrdTPD(tokType._struct, tokType._void, tokType._void, datType._str.getValue(), 0),
                    new recrdTPD(tokType._struct, tokType._extern, tokType._void, datType._str.getValue() + cdExt, 0),
                    new recrdTPD(tokType._struct, tokType._const, tokType._void, datType._str.getValue() + cdCns, 0),
                    new recrdTPD(tokType._struct, tokType._register, tokType._void, datType._str.getValue() + cdReg, 0),
                    new recrdTPD(tokType._struct, tokType._auto, tokType._void, datType._str.getValue() + cdAut, 0),
                    new recrdTPD(tokType._struct, tokType._static, tokType._void, datType._str.getValue() + cdStt, 0),
                    new recrdTPD(tokType._union, tokType._void, tokType._void, datType._unn.getValue(), 0),
                    new recrdTPD(tokType._union, tokType._extern, tokType._void, datType._unn.getValue() + cdExt, 0),
                    new recrdTPD(tokType._union, tokType._const, tokType._void, datType._unn.getValue() + cdCns, 0),
                    new recrdTPD(tokType._union, tokType._register, tokType._void, datType._unn.getValue() + cdReg, 0),
                    new recrdTPD(tokType._union, tokType._auto, tokType._void, datType._unn.getValue() + cdAut, 0),
                    new recrdTPD(tokType._union, tokType._static, tokType._void, datType._unn.getValue() + cdStt, 0),
                    new recrdTPD(tokType._unsigned, tokType._void, tokType._void, datType._ui.getValue(), 32),
                    new recrdTPD(tokType._unsigned, tokType._extern, tokType._void, datType._ui.getValue() + cdExt, 32),
                    new recrdTPD(tokType._unsigned, tokType._const, tokType._void, datType._ui.getValue() + cdCns, 32),
                    new recrdTPD(tokType._unsigned, tokType._register, tokType._void, datType._ui.getValue() + cdReg, 32),
                    new recrdTPD(tokType._unsigned, tokType._auto, tokType._void, datType._ui.getValue() + cdAut, 32),
                    new recrdTPD(tokType._unsigned, tokType._static, tokType._void, datType._ui.getValue() + cdStt, 32),
                    new recrdTPD(tokType._signed, tokType._void, tokType._void, datType._si.getValue(), 32),
                    new recrdTPD(tokType._signed, tokType._extern, tokType._void, datType._si.getValue() + cdExt, 32),
                    new recrdTPD(tokType._signed, tokType._const, tokType._void, datType._si.getValue() + cdCns, 32),
                    new recrdTPD(tokType._signed, tokType._register, tokType._void, datType._si.getValue() + cdReg, 32),
                    new recrdTPD(tokType._signed, tokType._auto, tokType._void, datType._si.getValue() + cdAut, 3),
                    new recrdTPD(tokType._signed, tokType._static, tokType._void, datType._si.getValue() + cdStt, 32),
                    new recrdTPD(tokType._char, tokType._unsigned, tokType._void, datType._uc.getValue(), 8),
                    new recrdTPD(tokType._char, tokType._unsigned, tokType._extern, datType._uc.getValue() + cdExt, 8),
                    new recrdTPD(tokType._char, tokType._unsigned, tokType._const, datType._uc.getValue() + cdCns, 8),
                    new recrdTPD(tokType._char, tokType._unsigned, tokType._register, datType._uc.getValue() + cdReg, 8),
                    new recrdTPD(tokType._char, tokType._unsigned, tokType._auto, datType._uc.getValue() + cdAut, 8),
                    new recrdTPD(tokType._char, tokType._unsigned, tokType._static, datType._uc.getValue() + cdStt, 8),
                    new recrdTPD(tokType._char, tokType._signed, tokType._void, datType._sc.getValue(), 8),//4
                    new recrdTPD(tokType._char, tokType._signed, tokType._extern, datType._sc.getValue() + cdExt, 8),//4
                    new recrdTPD(tokType._char, tokType._signed, tokType._const, datType._sc.getValue() + cdCns, 8),//4
                    new recrdTPD(tokType._char, tokType._signed, tokType._register, datType._sc.getValue() + cdReg, 8),//4
                    new recrdTPD(tokType._char, tokType._signed, tokType._auto, datType._sc.getValue() + cdAut, 8),//4
                    new recrdTPD(tokType._char, tokType._signed, tokType._static, datType._sc.getValue() + cdStt, 8),//4
                    new recrdTPD(tokType._char, tokType._void, tokType._void, datType._sc.getValue(), 8),
                    new recrdTPD(tokType._char, tokType._extern, tokType._void, datType._sc.getValue() + cdExt, 8),
                    new recrdTPD(tokType._char, tokType._const, tokType._void, datType._sc.getValue() + cdCns, 8),
                    new recrdTPD(tokType._char, tokType._register, tokType._void, datType._sc.getValue() + cdReg, 8),
                    new recrdTPD(tokType._char, tokType._auto, tokType._void, datType._sc.getValue() + cdAut, 8),
                    new recrdTPD(tokType._char, tokType._static, tokType._void, datType._sc.getValue() + cdStt, 8),
                    new recrdTPD(tokType._short, tokType._void, tokType._void, datType._si.getValue(), 16),
                    new recrdTPD(tokType._short, tokType._extern, tokType._void, datType._si.getValue() + cdExt, 16),
                    new recrdTPD(tokType._short, tokType._const, tokType._void, datType._si.getValue() + cdCns, 16),
                    new recrdTPD(tokType._short, tokType._register, tokType._void, datType._si.getValue() + cdReg, 16),
                    new recrdTPD(tokType._short, tokType._auto, tokType._void, datType._si.getValue() + cdAut, 16),
                    new recrdTPD(tokType._short, tokType._static, tokType._void, datType._si.getValue() + cdStt, 16),
                    new recrdTPD(tokType._short, tokType._unsigned, tokType._void, datType._ui.getValue(), 16),
                    new recrdTPD(tokType._short, tokType._unsigned, tokType._extern, datType._ui.getValue() + cdExt, 16),
                    new recrdTPD(tokType._short, tokType._unsigned, tokType._const, datType._ui.getValue() + cdCns, 16),
                    new recrdTPD(tokType._short, tokType._unsigned, tokType._register, datType._ui.getValue() + cdReg, 16),
                    new recrdTPD(tokType._short, tokType._unsigned, tokType._auto, datType._ui.getValue() + cdAut, 16),
                    new recrdTPD(tokType._short, tokType._unsigned, tokType._static, datType._ui.getValue() + cdStt, 16),
                    new recrdTPD(tokType._short, tokType._signed, tokType._void, datType._si.getValue(), 16),
                    new recrdTPD(tokType._short, tokType._signed, tokType._extern, datType._si.getValue() + cdExt, 16),
                    new recrdTPD(tokType._short, tokType._signed, tokType._const, datType._si.getValue() + cdCns, 16),
                    new recrdTPD(tokType._short, tokType._signed, tokType._register, datType._si.getValue() + cdReg, 16),
                    new recrdTPD(tokType._short, tokType._signed, tokType._auto, datType._si.getValue() + cdAut, 16),
                    new recrdTPD(tokType._short, tokType._signed, tokType._static, datType._si.getValue() + cdStt, 16),
                    new recrdTPD(tokType._int, tokType._void, tokType._void, datType._si.getValue(), 32),//9
                    new recrdTPD(tokType._int, tokType._extern, tokType._void, datType._si.getValue() + cdExt, 32),//9
                    new recrdTPD(tokType._int, tokType._const, tokType._void, datType._si.getValue() + cdCns, 32),//9
                    new recrdTPD(tokType._int, tokType._register, tokType._void, datType._si.getValue() + cdReg, 32),//9
                    new recrdTPD(tokType._int, tokType._auto, tokType._void, datType._si.getValue() + cdAut, 32),//9
                    new recrdTPD(tokType._int, tokType._static, tokType._void, datType._si.getValue() + cdStt, 32),//9
                    new recrdTPD(tokType._int, tokType._unsigned, tokType._void, datType._ui.getValue(), 32),
                    new recrdTPD(tokType._int, tokType._unsigned, tokType._extern, datType._ui.getValue() + cdExt, 32),
                    new recrdTPD(tokType._int, tokType._unsigned, tokType._const, datType._ui.getValue() + cdCns, 32),
                    new recrdTPD(tokType._int, tokType._unsigned, tokType._register, datType._ui.getValue() + cdReg, 32),
                    new recrdTPD(tokType._int, tokType._unsigned, tokType._auto, datType._ui.getValue() + cdAut, 32),
                    new recrdTPD(tokType._int, tokType._unsigned, tokType._static, datType._ui.getValue() + cdStt, 32),
                    new recrdTPD(tokType._int, tokType._signed, tokType._void, datType._si.getValue(), 32),
                    new recrdTPD(tokType._int, tokType._signed, tokType._extern, datType._si.getValue() + cdExt, 32),
                    new recrdTPD(tokType._int, tokType._signed, tokType._const, datType._si.getValue() + cdCns, 32),
                    new recrdTPD(tokType._int, tokType._signed, tokType._register, datType._si.getValue() + cdReg, 32),
                    new recrdTPD(tokType._int, tokType._signed, tokType._auto, datType._si.getValue() + cdAut, 32),
                    new recrdTPD(tokType._int, tokType._signed, tokType._static, datType._si.getValue() + cdStt, 32),
                    new recrdTPD(tokType._int, tokType._long, tokType._void, datType._si.getValue(), 32),
                    new recrdTPD(tokType._int, tokType._long, tokType._extern, datType._si.getValue() + cdExt, 32),
                    new recrdTPD(tokType._int, tokType._long, tokType._const, datType._si.getValue() + cdCns, 32),
                    new recrdTPD(tokType._int, tokType._long, tokType._register, datType._si.getValue() + cdReg, 32),
                    new recrdTPD(tokType._int, tokType._long, tokType._auto, datType._si.getValue() + cdAut, 32),
                    new recrdTPD(tokType._int, tokType._long, tokType._static, datType._si.getValue() + cdStt, 32),
                    new recrdTPD(tokType._long, tokType._void, tokType._void, datType._si.getValue(), 32),
                    new recrdTPD(tokType._long, tokType._extern, tokType._void, datType._si.getValue() + cdExt, 32),
                    new recrdTPD(tokType._long, tokType._const, tokType._void, datType._si.getValue() + cdCns, 32),
                    new recrdTPD(tokType._long, tokType._register, tokType._void, datType._si.getValue() + cdReg, 32),
                    new recrdTPD(tokType._long, tokType._auto, tokType._void, datType._si.getValue() + cdAut, 32),
                    new recrdTPD(tokType._long, tokType._const, tokType._void, datType._si.getValue() + cdStt, 32),
                    new recrdTPD(tokType._float, tokType._void, tokType._void, datType._f.getValue(), 32),//14
                    new recrdTPD(tokType._float, tokType._extern, tokType._void, datType._f.getValue() + cdExt, 32),//14
                    new recrdTPD(tokType._float, tokType._const, tokType._void, datType._f.getValue() + cdCns, 32),//14
                    new recrdTPD(tokType._float, tokType._register, tokType._void, datType._f.getValue() + cdReg, 32),//14
                    new recrdTPD(tokType._float, tokType._auto, tokType._void, datType._f.getValue() + cdAut, 32),//14
                    new recrdTPD(tokType._float, tokType._static, tokType._void, datType._f.getValue() + cdStt, 32),//14
                    new recrdTPD(tokType._double, tokType._void, tokType._void, datType._d.getValue(), 64),
                    new recrdTPD(tokType._double, tokType._extern, tokType._void, datType._d.getValue() + cdExt, 64),
                    new recrdTPD(tokType._double, tokType._const, tokType._void, datType._d.getValue() + cdCns, 64),
                    new recrdTPD(tokType._double, tokType._register, tokType._void, datType._d.getValue() + cdReg, 64),
                    new recrdTPD(tokType._double, tokType._auto, tokType._void, datType._d.getValue() + cdAut, 64),
                    new recrdTPD(tokType._double, tokType._static, tokType._void, datType._d.getValue() + cdStt, 64),
                    new recrdTPD(tokType._double, tokType._long, tokType._void, datType._ld.getValue(), 80),
                    new recrdTPD(tokType._double, tokType._long, tokType._extern, datType._ld.getValue() + cdExt, 80),
                    new recrdTPD(tokType._double, tokType._long, tokType._const, datType._ld.getValue() + cdCns, 80),
                    new recrdTPD(tokType._double, tokType._long, tokType._register, datType._ld.getValue() + cdReg, 80),
                    new recrdTPD(tokType._double, tokType._long, tokType._auto, datType._ld.getValue() + cdAut, 80),
                    new recrdTPD(tokType._double, tokType._long, tokType._static, datType._ld.getValue() + cdStt, 80),
                    new recrdTPD(tokType._class, tokType._void, tokType._void, datType._cls.getValue(), 0),
                    new recrdTPD(tokType._class, tokType._extern, tokType._void, datType._cls.getValue() + cdExt, 0),
                    new recrdTPD(tokType._class, tokType._const, tokType._void, datType._cls.getValue() + cdCns, 0),
                    new recrdTPD(tokType._class, tokType._register, tokType._void, datType._cls.getValue() + cdReg, 0),
                    new recrdTPD(tokType._class, tokType._auto, tokType._void, datType._cls.getValue() + cdAut, 0),
                    new recrdTPD(tokType._class, tokType._static, tokType._void, datType._cls.getValue() + cdStt, 0),
                    new recrdTPD(tokType._class, tokType._volatile, tokType._void, datType._cls.getValue() + cdVlt, 0),
            };


    recrdTMD[] tpLxMd =
            // масив кодів та ознак ключових слів типів
            {new recrdTMD(datType._v, 0, 0),    //0 _void
                    new recrdTMD(datType._v, 0, 0),    //1 _extern
                    new recrdTMD(datType._v, 0, 0),    //2 _var
                    new recrdTMD(datType._v, cdCns, 0),    //3 _const
                    new recrdTMD(datType._enm, 0, 32),    //4 _enum
                    new recrdTMD(datType._str, 0, 0),    //5 _struct/*_record*/
                    new recrdTMD(datType._unn, 0, 0),    //6 _union
                    new recrdTMD(datType._v, cdReg, 0),    //7 _register
                    new recrdTMD(datType._ui, 0, 32),    //8 _unsigned
                    new recrdTMD(datType._si, 0, 32),    //9 _signed
                    new recrdTMD(datType._si, 0, 8),    //10 _char
                    new recrdTMD(datType._si, 0, 16),    //11 _short
                    new recrdTMD(datType._si, 0, 32),    //12 _int
                    new recrdTMD(datType._si, 0, 32),    //13 _long
                    new recrdTMD(datType._si, 0, 64),    //14 _sint64
                    new recrdTMD(datType._ui, 0, 64),    //15 datType._uint64
                    new recrdTMD(datType._f, 0, 32),    //16 _float
                    new recrdTMD(datType._d, 0, 64),    //17 _double
            };

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


    ;

    class recrdTMD    // структура рядка таблиці базових типів
    {
        datType tpLx;// примірник структури ключа
        int md;    // модифікатор
        int ln;    // базова або гранична довжина даних типу

        public recrdTMD(datType tpLx, int md, int ln) {
            this.tpLx = tpLx;
            this.md = md;
            this.ln = ln;
        }
    }

    class recrdTPD    // структура рядка таблиці модифікованих типів
    {
        tokType[] kTp = new tokType[3];// примірник структури ключа
        int dTp;//enum datType примірник функціональної частини
        int ln;    // базова довжина даних типу

        public recrdTPD(tokType[] kTp, int dTp, int ln) {
            this.kTp = kTp;
            this.dTp = dTp;
            this.ln = ln;
        }

        public recrdTPD(tokType kTp1, tokType kTp2, tokType kTp3, int dTp, int ln) {
            tokType[] kTpx = {kTp1, kTp2, kTp3};
            this.kTp = kTpx;
            this.dTp = dTp;
            this.ln = ln;
        }
    }

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
                    datType._lbl.getValue(),//
                    datType._str.getValue(), datType._unn.getValue(),
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
    int cmpStr(char[] s1, char[] s2) {
        int n = 0;
        while (s1[n] == s2[n] && s1[n] != 0) n++;
        return s1[n] - s2[n];
    }

    // порівняння рядків
// порівняння терміналів за відношенням порядку
    int cmpTrm(lxNode k0, lxNode kArg)//cmpKys
    {
        int x1 = k0.start;
        char[] y1 = new char[1024];
        int j = 0;
        while (imgBuf[x1] != 0) {
            y1[j] = imgBuf[x1];
            x1++;
            j++;
        }
        int x2 = kArg.start;
        char[] y2 = new char[1024];
        j = 0;
        while (imgBuf[x2] != 0) {
            y2[j] = imgBuf[x2];
            x2++;
            j++;
        }
        int i = cmpStr(y1, y2);
        if (i != 0) return i;
        return k0.stkLength - kArg.stkLength; // порівняння номерів модулів
    }

    // вибірка через пошук за двійковим деревом
    indStrUS selBTr(lxNode kArg, indStrUS rtTb) {
        int df;
        while ((df = cmpTrm(kArg, rtTb.pKyStr)) != 0)
            if (df > 0) {
                if (rtTb.pRtPtr != null) rtTb = rtTb.pRtPtr;
                else break;
            } else {
                if (rtTb.pLtPtr != null) rtTb = rtTb.pLtPtr;
                else break;
            }
        rtTb.dif = df;
        return rtTb;
    }

    void prDtLst(lxNode nd) {
        if (nd.ndOp == tokType._comma) {
            prDtLst(nd.prvNd);
            nd.pstNd.dataType = tpLx[nd.pstNd.dataType].getValue();
            convNum(nd.pstNd); //nInCr++;//,	enum ltrTypeS ltrCls[256]);
        } else if (nd.ndOp == tokType._srcn) {
            nd.dataType = tpLx[nd.dataType].getValue();
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
                nd.pstNd.dataType = tpLx[nd.pstNd.dataType].getValue();
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
                nd.pstNd.dataType = datType._ui.getValue();
                convNum(nd.pstNd); //nInCr++;//,	enum ltrTypeS ltrCls[256]);
//		 nd.pstNd.resLength=lnCod[nd.pstNd.dataType];
            }
//	 SmAnDcl(datType._ui,nd.pstNd);
            nd.dataType = tpCod;
            if ((tpCod & msStp) >= datType._f.getValue()) tpCod -= datType._f.getValue() - 12;
            nd.resLength = lnCod[tpCod & 0x7FF];
        } else if (nd.ndOp == tokType._nam) {
            nd.dataType = tpCod;
            if ((tpCod & msStp) >= datType._f.getValue()) tpCod -= datType._f.getValue() - 12;
            nd.resLength = lnCod[tpCod & 0x7fff];
            if ((tpCod & msPtr) != 0) nd.resLength = lnFPtr;
        }
        return datType.values()[tpCod];
    }

    indStrUS[] ndxNds = new indStrUS[50];
    recrdSMA ftImp = new recrdSMA(tokType._nil, datType._v.getValue(), 0, datType._v.getValue(), 0, datType._v.getValue(), 0);// таблиця припустимості типів для операцій
    int nbBlk = 0;
    tokType[] lPrv = {tokType._void, tokType._void, tokType._void};// масив для накопичення ключових слів типа

    int SmAnlzr(lxNode nd,    // покажчик на початок масиву вузлів
                int nR)    // номер кореневого вузла
    {//enum datType
        int tPrv = 0, tPst;    // типи вузлів попередника та наступника
        int lnPrv = 0, lnPst = 0;    // довжини попередника та наступника
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
            pRtNdx = selBTr(nd, ndxNds[0]);//пошук імені
            // якщо не знайдено - неописане ім'я            !!!!!!!!!!!!!
            if (pRtNdx == null) {
                System.out.println("\nUndeclared identifier!\n");
                error(nd);
            }
            int x1 = pRtNdx.pKyStr.start;
            int j = 0;
            while (imgBuf[x1] != 0) {
                j++;
            }
            char[] y1 = new char[j];
            j = 0;
            while (imgBuf[x1] != 0) {
                y1[j] = imgBuf[x1];
                x1++;
                j++;
            }
            name = y1;
        /*	nd.pstNd=pRtNdx.pKyStr.pstNd;*/
            nd.dataType = tPrv = pRtNdx.pKyStr.dataType;
            nd.resLength = (int) pRtNdx.pKyStr.resLength;
        } else if (nd.ndOp == tokType._srcn)//якщо термінал-константа
        {
            nd.dataType = tpLx[nd.dataType].getValue();
            tPrv = nd.dataType;
            convNum(nd/*.pstNd*/); //перетворення константи на внутрішню
            //nInCr++;//,	enum ltrTypeS ltrCls[256]);
            //  nd.resLength=lnCod[tPrv];
        } else {
            if (nd.ndOp == tokType._remL)
                return datType._v.getValue();
            if (nd.prvNd != null && nd.ndOp != tokType._nam && nd.ndOp != tokType._srcn) {
                if (nd.ndOp != tokType._brkz && nd.prnNd != 0) {
                    tPrv = SmAnlzr(nd.prvNd, nR);
                    lnPrv = nd.prvNd.resLength;
                }
            } else {
                ftImp.oprd1 = tPrv = datType._v.getValue();
                ftImp.ln1 = 0;
            }
            if ((nd.pstNd != null) && (nd.ndOp != tokType._nam) && (nd.ndOp != tokType._srcn)) {
                tPst = SmAnlzr(nd.pstNd, nR);
                lnPst = nd.pstNd.resLength;
            } else {
                ftImp.oprd2 = tPst = datType._v.getValue();
                ftImp.ln2 = 0;
            }
            if (nd.ndOp == tokType._EOS) {
                nd.dataType = datType._v.getValue();
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
                if (tPrv != datType._v.getValue())
                    ftImp.ln1 = lnPrv;
                else
                    ftImp.ln1 = lnPrv = 0;
                ftImp.oprd2 = tPst & 0xffff7fff;
                if (tPst != datType._v.getValue())
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

    int nImBg = 0, nImCr = 0, nInBg = 7, nInCr = 7, nIcBg = 4, nIcCr = 4, fPrdfCnstr = 0, fLnkCnstr = 0, brkCntr = 0;
    //double fvalue64, fvalueR;
    int nPwr = 0, n32 = 0, FMode = 0;
    //char[] bcnst8_buf=new char[1024];

    class ui80_t {
        double lfvalue;
        long up80;
    }

    ui80_t c2pn32, fvalue;
    ui64_t value, valuea, value1, value1a, value0;
    double fvalue64, fvalueR;

    class ui64_t {
        long ui64;
        double fi64;
        s64 s64;
    }

    class s64 {
        int lo32;
        int hi32;
    }

    char[] bcnst8_buf = new char[1024];

    enum ltrTypeS {
        d0(0), d1(1), d2(2), d3(3), d4(4), d5(5), d6(6), d7(7), d8(8), d9(9),    // с0 десяткові цифри
        ltrhxdgtS(0x10), xZ(0x11),    // с1 літера - шістнадцяткова цифра
        xA(0x1a), xB(0x1b), xC(0x1c), xD(0x1d), xE(0x1e), xF(0x1f),    // літери - шістнадцяткові цифри
        ltrexpltS(0x2e),    // с2 літера - ознака експоненти
        ltrtpcnsS(0x30),    // с3 літера - визначник типу константи
        cO(0x36), cH(0x38),
        cB(0x4b), cD(0x4d),// с4 літери - визначники типу константи/ шістнадцяткові цифри
        ltrnmelmS(0x50),    // с4 літери, які припустимі тільки в іменах
        ltrstrlmS(0x60),    // с5 літери для обмеження рядків
        ltrtrnfmS(0x68),    // с6 ознаки початку перекодування літер рядків
        ltrcnslmS(0x70),    // с7 літери для обмеження констант
        dlmund(0x80),    // с8 допоміжні роздільники типа підкреслень _
        dlmauxS(0x90),    // с9 допоміжні роздільники типа пропусків
        dldotS(0xa0),    // с10 точка як роздільник та літера констант
        // ltrstrl1(0xb0),	// с11 ознаки початку констант
        ncS(0xb0),        // с7 некласифіковані літери
        ltrsignS(0xc0),    // с12 знак числа або порядку
        ltrsignm(0xcf),    // с12 знак числа або порядку
        dlmgrop1(0xcf + 1), dlmgrop2(0xcf + 2), dlmgrop3(0xcf + 3), dlmgrop4(0xcf + 4), dlmgrop5(0xcf + 5),// початковий елемент групового роздільника
        dlmgrop6(0xcf + 6), dlmgrop7(0xcf + 7), dlmgrop8(0xcf + 8), dlmgrop9(0xcf + 9), dlmgrop10(0xcf + 10),
        dlmunopS(0xdf),    // с13 роздільники операцій
        // dlmbrlst,	// с13 роздільники елементів списків
// dlobrct,	// с14 відкриті дужки
// dlcbrct,	// с15 закриті дужки
        ltrcodeS(256); //с16 ознака можливості кодування

        private final int id;

        ltrTypeS(int id) {
            this.id = id;
        }

        public int getValue() {
            return id;
        }
    }

    ;

    ltrTypeS[] ltrClsV = // Початок таблиці класифікаторів для Verilog HDL
            {ltrTypeS.ncS, ltrTypeS.ncS, ltrTypeS.ncS, ltrTypeS.ncS, ltrTypeS.ncS, ltrTypeS.ncS, ltrTypeS.ncS, ltrTypeS.ncS, ltrTypeS.ncS, ltrTypeS.dlmauxS, ltrTypeS.dlmauxS, ltrTypeS.ncS, ltrTypeS.ncS, ltrTypeS.dlmauxS, ltrTypeS.ncS, ltrTypeS.ncS, ltrTypeS. //16
                    ncS, ltrTypeS.ncS, ltrTypeS.ncS, ltrTypeS.ncS, ltrTypeS.ncS, ltrTypeS.ncS, ltrTypeS.ncS, ltrTypeS.ncS, ltrTypeS.ncS, ltrTypeS.ncS, ltrTypeS.ncS, ltrTypeS.ncS, ltrTypeS.ncS, ltrTypeS.ncS, ltrTypeS.ncS, ltrTypeS.ncS, ltrTypeS.    //32
                    dlmauxS, ltrTypeS.dlmgrop1, ltrTypeS.ltrstrlmS, ltrTypeS.dlmunopS, ltrTypeS.ltrnmelmS, ltrTypeS.dlmunopS, ltrTypeS.dlmgrop2, ltrTypeS.ltrcnslmS, ltrTypeS.
                    dlmunopS, ltrTypeS.dlmunopS, ltrTypeS.dlmgrop3, ltrTypeS.ltrsignS, ltrTypeS.dlmunopS, ltrTypeS.ltrsignm, ltrTypeS.dldotS, ltrTypeS.ltrstrlmS, ltrTypeS.    //48
                    d0, ltrTypeS.d1, ltrTypeS.d2, ltrTypeS.d3, ltrTypeS.d4, ltrTypeS.d5, ltrTypeS.d6, ltrTypeS.d7, ltrTypeS.
                    d8, ltrTypeS.d9, ltrTypeS.dlmunopS/*dlmgrop*/, ltrTypeS.dlmunopS/*dlmbrlst*/, ltrTypeS.dlmgrop4, ltrTypeS.dlmgrop5, ltrTypeS.dlmgrop6, ltrTypeS.dlmunopS, ltrTypeS.// 64
                    dlmunopS, ltrTypeS.xA, ltrTypeS.xB, ltrTypeS.xC, ltrTypeS.xD, ltrTypeS.ltrexpltS, ltrTypeS.xF, ltrTypeS.ltrnmelmS, ltrTypeS.
                    cH, ltrTypeS.ltrnmelmS, ltrTypeS.ltrnmelmS, ltrTypeS.ltrnmelmS, ltrTypeS.ltrnmelmS, ltrTypeS.ltrnmelmS, ltrTypeS.ltrnmelmS, ltrTypeS.cO, ltrTypeS.//80
                    ltrnmelmS, ltrTypeS.ltrnmelmS, ltrTypeS.ltrnmelmS, ltrTypeS.ltrnmelmS, ltrTypeS.ltrnmelmS, ltrTypeS.ltrnmelmS, ltrTypeS.ltrnmelmS, ltrTypeS.ltrnmelmS, ltrTypeS.
                    ltrhxdgtS/*ltrnmelm*/, ltrTypeS.ltrnmelmS, ltrTypeS.xZ/*ltrnmelm*/, ltrTypeS.dlmunopS, ltrTypeS.ltrtrnfmS, ltrTypeS.dlmunopS, ltrTypeS.dlmgrop7, ltrTypeS.dlmund, ltrTypeS.//96
                    dlmunopS, ltrTypeS.xA, ltrTypeS.xB, ltrTypeS.xC, ltrTypeS.xD, ltrTypeS.ltrexpltS, ltrTypeS.xF, ltrTypeS.ltrnmelmS, ltrTypeS.
                    cH, ltrTypeS.ltrnmelmS, ltrTypeS.ltrnmelmS, ltrTypeS.ltrnmelmS, ltrTypeS.ltrnmelmS, ltrTypeS.ltrnmelmS, ltrTypeS.ltrnmelmS, ltrTypeS.cO, ltrTypeS.//112
                    ltrnmelmS, ltrTypeS.ltrnmelmS, ltrTypeS.ltrnmelmS, ltrTypeS.ltrnmelmS, ltrTypeS.ltrnmelmS, ltrTypeS.ltrnmelmS, ltrTypeS.ltrnmelmS, ltrTypeS.ltrnmelmS, ltrTypeS.
                    ltrhxdgtS/*ltrnmelm*/, ltrTypeS.ltrnmelmS, ltrTypeS.xZ/*ltrnmelm*/, ltrTypeS.dlmunopS, ltrTypeS.dlmgrop8, ltrTypeS.dlmunopS, ltrTypeS.dlmgrop9, ltrTypeS.ncS//,ltrTypeS.128
//...
            };

    int[] bcnst32_buf = new int[1024];
    long c2p32 = 0x41f0000000000000l;
    int FPWR = 0x04000000;
    int FMIN = 0x02000000;
    char[] aTb = {2, 10, 8, 16};

    int /*LexNode::*/ convNum(lxNode nd    //, вказівник на початок масиву вузлів
// 		  unsigned n, int disp, enum ltrTypeS ltrCls[256]
    ) {
        char aSS, sSS, c = 0xff;
        int i, dispP, disp = nd.start;
        nPwr = 0;
        n32 = 0;
        FMode = 0;
// ndOp = n;;
        //nd.pstNd = (lxNode) (bcnst32_buf[nInBg]);
        if (nd.resLength != autStat.S2s.ordinal()) disp = convInt(disp, (char) 10, (char) 10, 0xFFFFFFFF);
        switch (autStat.values()[nd.resLength]) {
            case S1c:
                nd.resLength = 32;
                break;
            case S1p:
            case S2c:
                nd.resLength = 64;
//	  if(n32<(((ln)/32)<<1))n32=((ln)/32)<<1;
                if ((FMode & FPWR) != 0 || ltrClsV[imgBuf[disp]].getValue() == ltrType.ltrexplt.ordinal()) {
                    value.s64.lo32 = bcnst32_buf[nInBg];
//	 if(n32==0)
                    value.s64.hi32 = 0;
//	 else value.s64.hi32=bcnst32_buf[nInBg+2];
                    fvalueR/*.lfvalue*/ = (long) value.ui64;
                    c2pn32.up80 = c2p32;
                    fvalue64 = 1.0;
//	 fvalue.up80.mant=value.ui64;
//	 fvalue.up80.pow=0x403f;
                    for (i = 0; i < n32; i += 2) {
                        fvalue64 *= c2pn32.lfvalue;
                        value.s64.lo32 = bcnst32_buf[nInBg + i + 2];
                        fvalueR += (value.ui64) * fvalue64;
                    }
                    fvalue.lfvalue = fvalueR;
                    if (ltrClsV[imgBuf[disp]] == ltrTypeS.ltrexpltS) {
                        FMode &= ~FPWR;
                        dispP = disp + 1;
                        if ((ltrClsV[imgBuf[dispP]].getValue() & 0xf0) == ltrTypeS.ltrsignS.getValue()) disp = disp + 1;
                        disp = convInt(disp + 1, (char) 10, (char) 10, 0);
                        if (value.s64.lo32 != 0) {
                            if (imgBuf[dispP] != '-') nPwr += value.s64.lo32;
                            else nPwr -= value.s64.lo32;
                        }
                    }
                    if ((FMode & FMIN) != 0) fvalue.lfvalue = -fvalue.lfvalue;
                    FMode &= ~FMIN;
                    if (nPwr > 0) while (nPwr != 0) {
                        nPwr--;
                        fvalue.lfvalue *= 1e1;/*/=1e-1l*/
                    }
                    else while (nPwr != 0) {
                        nPwr++;
                        fvalue.lfvalue /= 1e1;/*=1e-1l*/
                    }
                    value.fi64 = fvalue.lfvalue;
                    bcnst32_buf[nInBg + 0] = value.s64.lo32;
                    bcnst32_buf[nInBg + 1] = value.s64.hi32;
                    n32 = 0;
                }
                break;
            case S2s:
                strcpy(bcnst8_buf, nIcBg, imgBuf, nImBg + 1);
                nd.pstNd = new lxNode(null, null, null, 0, 0, 0, 0, 0, 0, 0, bcnst8_buf[nIcBg]);
                nIcBg = nIcCr = nIcBg + strlen(imgBuf, nImBg + 1);
                bcnst8_buf[nIcCr - 1] = 0; // вилучити повторення
                return 0;
            //break;
            case S3c:
                nd.resLength = value.s64.lo32;
                if (nd.resLength == 0xFFFFFFFF) nd.resLength = 32;
                sSS = (char) ((ltrClsV[imgBuf[disp + 1]].getValue() & 14) >> 1);
                if (sSS > 4) sSS -= 4;
                aSS = aTb[sSS - 1];
                convInt(disp + 2, aSS, sSS, nd.resLength);
        }
        nd.stkLength = (n32 + 2) << 4;
        nInCr = nInBg - n32 - 2;
        do {
            if (nInCr < 0) break;
            c = 1;
            for (i = n32 + 1; i >= 0; i--)
                if (!((c &= bcnst32_buf[nInBg + i]) == bcnst32_buf[nInCr + i])) break;
            nInCr--;
        } while (c == 0);
        if (c == 0) {
            nInCr = nInBg += n32 + 2;
            nImBg = nImCr;
        } else {
            nd.pstNd = new lxNode(null, null, null, 0, 0, 0, 0, 0, 0, 0, bcnst32_buf[nInCr + 1]);
            nInCr = nInBg;
            nImBg = nImCr;
        }// це треба б мінімізувати, якщо вхідні коди співпадають
        return n32;
    }

    void strcpy(char[] destination, int startdes, char[] source, int sourcedes) {
        int i = sourcedes;
        int j = startdes;
        while (source[i] != 0) {
            destination[j] = source[i];
            i++;
            j++;
        }
    }

    int strlen(char[] source, int start) {
        int i = start;
        int count = 0;
        while (source[i] != 0) {
            i++;
            count++;
        }
        return count;
    }

    //------------------------------------------------------------------------
  /* converting source program to lexeme string "wstr" */
    int convInt(int nc, char aSS, char sSS, int nb) {//try to skip space after constant base like 'b, 'd, 'h
        char cwrk;
        int i = 0;
        int difV, difVM, nBit = 0;//, difV, difVM;
        value.ui64 = value1.ui64 = 0; //nPwr=0;
        if (nb == 0xFFFFFFFF) value.s64.lo32 = nb;
        n32 = 0;
        bcnst32_buf[nInBg + 1] = bcnst32_buf[nInBg] = 0;
/*  if(lwstr<=2||wstr[lwstr].code!=div_diez
     ||(wstr[lwstr-1].code!=div_equ&&wstr[lwstr-1].code!=kws_leeq))
    while (c==32||c==9) Read_symbol();
  if (c=='`')
    {scan_identifier();buffer[0]=0;}*/
        while (((cwrk = (char) ltrClsV[imgBuf/*imageBuf*/[nc]].getValue()) & 0xcf) < aSS || imgBuf[nc] == '.'
//		 ||c=='_'||c=='.'||c=='?'
//		 ||c=='X'||c=='x'||c=='Z'||c=='z'// x=0, z=-1
                ) {
            if (imgBuf[nc] != '.') {
                if (aSS == 10) {
                    difV = (cwrk & 0xf);
                    difVM = 0;
                    if ((cwrk & 0x20) != 0) difVM = 15 & (aSS - 1);
                    if ((FMode & FPWR) != 0) nPwr--;
                    for (i = 0; i <= n32; i += 2) {
                        value.s64.lo32 = bcnst32_buf[i + nInBg];
                        value.s64.hi32 = 0;
                        value1.s64.lo32 = bcnst32_buf[i + nInBg + 1];
                        value1.s64.hi32 = 0;
                        value.ui64 = value.ui64 * aSS + difV;
                        value1.ui64 = (value1.ui64 * sSS) | difVM;
                        bcnst32_buf[i + nInBg] = value.s64.lo32;
                        bcnst32_buf[i + nInBg + 1] = value1.s64.lo32;
                        difV = value.s64.hi32;
                        difVM = value1.s64.hi32;
                    }
                    if ((difV | difVM) != 0) {
                        n32++;
                        n32++;
                        bcnst32_buf[i + nInBg] = difV;
                        bcnst32_buf[i + nInBg + 1] = difVM;
                    }
                } else {
                    difV = cwrk & (aSS - 1);
                    if (cwrk == ltrTypeS.xZ.getValue()/*ltrhxdgt*/) difV = aSS - 1;
                    difVM = 0;
                    nBit += sSS;
                    if ((cwrk & 0xfe/*20*/) == ltrType.ltrhxdgt.ordinal()/*0*/) difVM = 15 & (aSS - 1);
                    for (i = 0; i <= n32; i += 2) {
                        value.s64.lo32 = bcnst32_buf[i + nInBg];
                        value.s64.hi32 = 0;
                        value1.s64.lo32 = bcnst32_buf[i + nInBg + 1];
                        value1.s64.hi32 = 0;
                        value.ui64 = (value.ui64 << sSS) + difV;
                        value1.ui64 = (value1.ui64 << sSS) | difVM;
                        bcnst32_buf[i + nInBg] = value.s64.lo32;
                        bcnst32_buf[i + nInBg + 1] = value1.s64.lo32;
                        difV = value.s64.hi32;
                        difVM = value1.s64.hi32;
                    }
                    if ((difV | difVM) != 0) {
                        n32++;
                        n32++;
                        bcnst32_buf[i + nInBg] = difV;
                        bcnst32_buf[i + nInBg + 1] = difVM;
                    }
                }
            } else if (imgBuf[nc] == '.') {
                FMode |= FPWR;
            } // треба захиститись вiд повторної крапки
//     if (strlen(imageBuf+nc)<=1025)strncat(imageBuf+nc,nc/*&c*/,1);
            //Read_symbol();
            nc++;
        }
        if (nb/*ulength.ui64*/ != 0xFFFFFFFF) {//ulength.s64.lo32=32;
            valuea.ui64 = 1;
            valuea.ui64 <<= ((nBit - 1) & 63);
            if (aSS != 10 && nBit </*ulength.s64.lo32*/nb && (valuea.ui64 & value1.ui64) != 0) {
                value1a.ui64 = 1;
                value1a.ui64 <<= (nb/*ulength.s64.lo32*/);//&63
                value1a.ui64 = value1a.ui64 - valuea.ui64;
                bcnst32_buf[i + nInBg - 1] |= value1a.ui64;
                if (value1a.s64.hi32 != 0) {
                    n32++;
                    n32++;
                    bcnst32_buf[i + nInBg + 1] = value1a.s64.hi32;
                    bcnst32_buf[i + nInBg] = 0;
                    if ((valuea.ui64 & value.ui64) != 0) bcnst32_buf[i + nInBg] |= value1a.s64.hi32;
                }
                if ((valuea.ui64 & value.ui64) != 0) bcnst32_buf[i + nInBg - 2] |= value1a.ui64;
            } else if (nBit > nb/*ulength.s64.lo32*/ && aSS != 10) {
                bcnst32_buf[i + nInBg - 1] &= valuea.s64.lo32 - 1;
                bcnst32_buf[i + nInBg - 2] &= valuea.s64.lo32 - 1;
            } else if ((i = ((nb/*ulength.s64.lo32*/ + 31) >> 5) - 1 - n32 / 2) != 0)
                while (i-- > 0) {
                    n32 += 2;
                    bcnst32_buf[n32 + nInBg] = 0;
                    bcnst32_buf[n32 + nInBg + 1] = 0;
                }//?????
        }
        return nc;
    }

    CodeGenerator CD;
    ArrayList<lxNode> nodes;
    ArrayList<method_nodes> methods;

    private method_nodes get_name_method(String name) {
        for (int i = 0; i < nodes.size(); i++) {
            if (methods.get(i).name.equals(name)) {
                return methods.get(i);
            }
        }
        return null;
    }

    private boolean check_name_method(String name) {
        for (int i = 0; i < methods.size(); i++) {
            if (methods.get(i).name.equals(name)) {
                return true;
            }
        }
        return false;
    }

    private boolean check_identifier_method(String name, method_nodes method) {
        for (int i = 0; i < method.parametrs.size(); i++) {
            if (method.parametrs.get(i).name.equals(name)) {
                return true;
            }
        }
        return false;
    }

    private identifiers get_identifier_method(String name, method_nodes method) {
        for (int i = 0; i < method.parametrs.size(); i++) {
            if (method.parametrs.get(i).name.equals(name)) {
                return method.parametrs.get(i);
            }
        }
        return null;
    }

    private void set_not_null(String name, method_nodes method) {
        for (int i = 0; i < method.parametrs.size(); i++) {
            if (method.parametrs.get(i).name.equals(name)) {
                method.parametrs.get(i).is_null = false;
                break;
            }
        }
    }

    public void error() {
        System.out.println("Error on");
        System.exit(0);
    }

    public void Analyze(ArrayList<ArrayList<lxNode>> arr_methods_list, ArrayList<lxNode> nodes, char[] imgBuf) {
        CD = new CodeGenerator(imgBuf);
        this.nodes = nodes;
        methods = new ArrayList<>();
        for (int i = 0; i < arr_methods_list.size(); i++) {
            int count = 0;
            lxNode temp = arr_methods_list.get(i).get(0);
            while (temp.ndOp != tokType._int && temp.ndOp != tokType._void) {
                temp = temp.pstNd;
            }
            if (temp.prvNd == null) {
                if (check_name_method(CD.getName(arr_methods_list.get(i).get(1)))) {
                    error();
                }
                methods.add(new method_nodes(CD.getName(arr_methods_list.get(i).get(1)), arr_methods_list.get(i).get(0).ndOp));
                temp = arr_methods_list.get(i).get(2).pstNd;
                if (temp != null) {
                    while (temp.ndOp == tokType._EOS) {
                        temp = temp.prvNd;
                    }
                    if (check_identifier_method(CD.getName(temp.pstNd), methods.get(methods.size() - 1))) {
                        error();
                    }
                    methods.get(methods.size() - 1).add_parametrs(CD.getName(temp.pstNd), temp.ndOp, false);
                    count++;
                    temp = nodes.get(temp.prnNd);
                    while (temp.ndOp != tokType._brkz) {
                        methods.get(methods.size() - 1).add_parametrs(CD.getName(temp.pstNd.pstNd), temp.pstNd.ndOp, false);
                        count++;
                        temp = nodes.get(temp.prnNd);
                    }
                }
                methods.get(methods.size() - 1).count = count;
            } else {
                methods.add(new method_nodes("main", tokType._void));
                methods.get((methods.size() - 1)).count = count;
            }
        }
        for (int i = 0; i < arr_methods_list.size(); i++) {
            lxNode temp = arr_methods_list.get(i).get(0);
            while (temp.ndOp != tokType._opbz) {
                temp = nodes.get(temp.prnNd);
            }
            check_code_block(temp, methods.get(i));
            /*while (temp.ndOp == tokType._EOS) {
                temp = temp.prvNd;
            }
            if (temp.ndOp==tokType._return){
                temp = temp.prvNd.pstNd;
            }
            check_compound(temp);
            temp = nodes.get(temp.prnNd);
            while(temp.ndOp!=tokType._opbz){
                if(temp.pstNd!=null) {
                    check_compound( temp.pstNd);
                }
                temp = nodes.get(temp.prnNd);
            }
            if (arr_methods_list.get(i).get(0).prvNd==null){
                methods.add(new method_nodes(CD.getName(arr_methods_list.get(i).get(1)),arr_methods_list.get(i).get(0).ndOp));
            }
            */
        }
    }

    private void check_code_block(lxNode start, method_nodes method) {
        lxNode temp = start.pstNd;
        while (temp.ndOp == tokType._EOS) {
            temp = temp.prvNd;
        }
        check_compound(temp, method);
        temp = nodes.get(temp.prnNd);
        while (temp.ndOp != tokType._opbz) {
            if (temp.pstNd != null) {
                check_compound(temp.pstNd, method);
            }
            temp = nodes.get(temp.prnNd);
        }
    }

    private void check_compound(lxNode start, method_nodes method) {
        lxNode temp = start;
        if (temp.ndOp == tokType._int) {
            if (check_identifier_method(CD.getName(temp.pstNd.prvNd), method)) {
                error();
            }
            //method.add_parametrs(CD.getName(temp.pstNd.prvNd), temp.ndOp);
            temp = temp.pstNd;
            //if (temp.ndOp == tokType._ass) {
            //    set_not_null(CD.getName(temp.prvNd), method);
            //}
        }
        switch (temp.ndOp) {
            case _ass:
                checkAss(temp, method);
                break;
            case _add:
                checkAdd(temp, method);
                break;
            case _nam:
                checkNam(temp, method, false);
                break;
            case _for:
                checkFor(temp, method);
                break;
            case _whileP:
                switch (temp.prvNd.ndOp) {
                    case _brkz:
                        checkWhile(temp, method);
                        break;
                    case _repeat:
                        checkRepeat(temp, method);
                        break;
                }
                break;
            case _srcn:
                checkSrcn(temp, method);
                break;
            case _brkz:
                checkOp(temp, method);
                break;
            case _return:
                if (method.type != tokType._void) {
                    temp = temp.prvNd.pstNd;
                } else error();
                check_compound(temp, method);
                break;
        }

    }

    private void checkOp(lxNode start, method_nodes method) {
        lxNode temp = start;
        String name = CD.getName(temp.prvNd);
        temp = temp.pstNd;
        int count = 0;
        if (temp != null) {
            while (temp.ndOp == tokType._comma) {
                temp = temp.prvNd;
            }
            check_compound(temp, method);
            count++;
            temp = nodes.get(temp.prnNd);
            while (temp.ndOp != tokType._brkz) {
                check_compound(temp.pstNd, method);
                temp = nodes.get(temp.prnNd);
                count++;
            }
        }
        if (get_name_method(name).count != count) {
            error(); //Неправильное количество аргументов
        }
    }

    private void checkAss(lxNode start, method_nodes method) {
        if (nodes.get(start.prnNd).ndOp==tokType._int) {
            checkNam(start.prvNd, method, true);
        }
        else{
            checkNam(start.prvNd, method,false);
        }
        check_compound(start.pstNd, method);
    }

    private void checkAdd(lxNode start, method_nodes method) {
        check_compound(start.prvNd, method);
        check_compound(start.pstNd, method);
    }

    private void checkNam(lxNode start, method_nodes method, boolean flag) {
        if (flag) {
            if (check_identifier_method(CD.getName(start), method)) {
                error();//переопределение
            }
        } else {
            if (!check_identifier_method(CD.getName(start), method)) {
                error();//не найдено
            }
            if (get_identifier_method(CD.getName(start), method).is_null) {
                error();//null
            }
        }
    }

    private void checkSrcn(lxNode start, method_nodes method) {

    }

    private void checkFor(lxNode start, method_nodes method) {
        check_compound(start.prvNd.pstNd.prvNd.prvNd, method); //start condition
        check_compound(start.prvNd.pstNd.pstNd, method);
        check_bool(start.prvNd.pstNd.prvNd.pstNd, method);
        check_code_block(start.pstNd, method);
    }

    private void checkWhile(lxNode start, method_nodes method) {
        check_bool(start.prvNd.pstNd, method);
        check_code_block(start.pstNd, method);
    }

    private void checkRepeat(lxNode start, method_nodes method) {
        check_code_block(start.prvNd.pstNd, method);
        check_bool(start.pstNd.pstNd, method);
    }

    private void check_bool(lxNode start, method_nodes method) {
        lxNode temp = start;
        switch (temp.ndOp) {//_lt, _le, _eq, _ne, _ge, _gt,       // < <= == != >= >
            case _lt:
                check_compound(temp.prvNd, method);
                check_compound(temp.pstNd, method);
                break;
            case _le:
                check_compound(temp.prvNd, method);
                check_compound(temp.pstNd, method);
                break;
            case _eq:
                check_compound(temp.prvNd, method);
                check_compound(temp.pstNd, method);
                break;
            case _ne:
                check_compound(temp.prvNd, method);
                check_compound(temp.pstNd, method);
                break;
            case _ge:
                check_compound(temp.prvNd, method);
                check_compound(temp.pstNd, method);
                break;
            case _gt:
                check_compound(temp.prvNd, method);
                check_compound(temp.pstNd, method);
                break;
        }
    }

class method_nodes {
    String name;
    tokType type;
    ArrayList<identifiers> parametrs;
    int count;

    public method_nodes(String name, tokType type) {
        this.name = name;
        this.type = type;
        parametrs = new ArrayList<>();
    }

    public void add_parametrs(String name, tokType type) {
        parametrs.add(new identifiers(name, type));
    }

    public void add_parametrs(String name, tokType type, boolean is_null) {
        parametrs.add(new identifiers(name, type, is_null));
    }
}

class identifiers {
    String name;
    tokType type;
    boolean is_null = true;

    public identifiers(String name, tokType type) {
        this.name = name;
        this.type = type;
    }

    public identifiers(String name, tokType type, boolean is_null) {
        this.name = name;
        this.type = type;
        this.is_null = is_null;
    }
}
}
