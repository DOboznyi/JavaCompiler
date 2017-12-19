import java.util.Scanner; // импорт сканера
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;

enum tokType {
    _nil, _nam, //0 зовнішнє подання
    _srcn, _cnst,   //2 вхідне і внутрішнє кодування константи
    _if, _then, _else, _elseif, //4 if then else elseif
    _case, _switch, _default, _endcase,//8 case switch defualt endcase
    _break, _return, _whileP, _whileN, //12 break return while do
    _continue, _repeat, _untilN, _endloop, //16 continue repeat until
    _for, _to, _downto, _step,// 20 for to downto step
    _untilP, _loop, _with, _endif,  // 24
    _void, _extern, _var, _const, _enum, _struct/*_record*/, _union, _register,//
    _unsigned, _signed, _char, _short, _int, _long, _sint64, _uint64,//
    _float, _double, _label, _auto, _static, _volatile, _typedef, _sizeof,//
    _real, _array, _set, _file, _object, _string, _goto,
    _program, _function, _procedure /*task V*/,
    _macromodule, _primitive, _specify, _table, //Verilog
    _generate, _config, _liblist, _library,  //Verilog
    _incdir, _include, _design, _defaultS, _instance, _cell, _use, //Verilog
    _automatic, _endmodule, _endfunction, _endtask,   //Verilog
    _endprimitive, _endspecify, _endtable, _endgenerate, _endconfig,  //Verilog
    _endcaseV, _casex, _casez, _wait, _forever, _disable, _ifnone, //Verilog
    _pulsestyle_onevent, _pulsestyle_ondetect, _showcanceled, _noshowcanceled, //Verilog
    _vectored, _scalared, _small, _medium, _large, //Verilog
    _genvar, _parameter, _localparam, _defparam, _specparam, _PATHPULSE,  //Verilog
    _inlineF, _forward, _interrupt, _exportF, _extrn, _asmb,
    _input, _output, _inout,  //Verilog|SQL+3
    _objectP, _constructor, _desctructor, _property, _resP, _abstract, //P++9
    _class, _public, _private, _protected, _virtual, _friend, //C++16
    _new, _delete, _tryC, _catch, _throw/*raise*/, //C++20
    _initial, _always, _assign, _deassign, _force, _release, //Verilog+26
    _reg, _time, _realtime, _event, _buf, _not, //Verilog+32
    _andG, _orG, _xorG, _nandG, _norG, _xnorG,  //Verilog+38
    _tran, _tranif0, _tranif1, _rtran, _rtranif0, _rtranif1, //Verilog+44
    _tri, _trior, _triand, _trireg, _tri0, _tri1,//Verilog+50
    _wire, _wand, _wor, _wres,          //Verilog+54
    _supply0, _supply1, _highz0, _highz1, //Verilog+58
    _strong0, _strong1, _pull0, _pull1, _weak0, _weak1,  //Verilog+64
    _pulldown, _pullup, _bufif0, _bufif1, _notif0, _notif1,  //Verilog+70
    _cmos, _rcmos, _nmos, _pmos, _rnmos, _rpmos,  //Verilog+76
    _fork, _join,   // відкриті і закриті дужки паралельних операторів 2
    _opbr, _ocbr,   // відкриті і закриті дужки операторів 2
    _ctbr, _fcbr,   // відкриті і закриті дужки конкатенацій 3
    _ixbr, _scbr,   // відкриті і закриті дужки індексу 4
    _brkt, _bckt,   // відкриті і закриті дужки порядку і функцій 5
    _tdbr, _tcbr,   // відкриті і закриті дужки даних 6
    _eosP, eosS,    // паралельні та послідовні
    _EOS, _comma, _cln, _qmrk,// ; , : ?
    _asOr, _asAnd, _asXor, _asAdd,      //|= =& ^= =+
    _asSub, _asMul, _asDiv, _asMod, // -= *= /= %=
    _asShr, _asShl, _ass, _dcr, _inr,   // <<= >>= = -- ++
    _dcrN, _inrN, _mcrs, _dbcln, _eoCm, _EOF, //-- ++ //  #  ::  */
    _lt, _le, _eq, _ne, _ge, _gt,       // < <= == != >= >
    _add, _sub, _mul, _div, _fldDt, _fldPt,// + - * / . ->
    _pwr, _shLfa, _shRga, _eqB, _neB,   // ** <<< >>> === !==
    _addU, _subU, _refU, _ptrU,     // + - * & унарні
    _lmts, _eqar, _astar, _trasand, // PV+4 ..  => *> &&&
    _orR, _andR, _xorR, _norR, _nandR, _nxorR, _xornR, //V+11 & | ^ ~| ~& ~^
    _delay, _eventV, _events,        //V+14 # @ @*
    _norB, _nandB, _nxorB, _xornB, _addr, //~| ~& ~^ ^~  _ptr,
    _rem, _remL, //
    _mod, _orB, _andB, _xorB,       // %(mod) |(or) &(and) ^(xor)
    _shLft, _shRgt, _or, _and,  //<<(shl) >>(shr) ||(or) &&(and)
    _xmrk, _invB, _divI, _in, //_not, _notB, /(div)
    _posedge, _negedge, _orE,  //Verilog+3
    _frkz,  // відкриті і закриті дужки паралельних операторів 2
    _opbz,  // відкриті і закриті дужки операторів 2
    _ctbz,  // відкриті і закриті дужки конкатенацій 3
    _ixbz,  // відкриті і закриті дужки індексу 4
    _brkz,  // відкриті і закриті дужки порядку і функцій 5
    _tdbz,   // відкриті і закриті дужки даних 6
    // _pnil
    _err, _bool
};

enum ltrType {
    dgt,      //0 десяткова цифра
    ltrexplt,//1 літера-ознака експоненти
    ltrhxdgt,//2 літера-шістнадцяткова цифра
    ltrtpcns,//3 літера-визначник типу константи
    ltrnmelm,//4 літери, які припустимі тільки в іменах
    ltrstrlm,//5 літери для обмеження рядків і констант
    ltrtrnfm,//6 літери початку перекодування літер рядків
    nc,      //7 некласифіковані літери
    dldot,   //8 точка як роздільник та літера констант
    ltrsign, //9 знак числа або порядку
    dlmunop, //10 поодинокі роздільники операцій
    dlmgrop, //11 елемент групового роздільника
    dlmbrlst,//12 роздільники елементів списків
    dlobrct, //13 відкриті дужки
    dlcbrct, //14 закриті дужки
    dlmeorml,//15 кінець обмеженого коментаря
    dlmeormr,//16 кінець коментаря-рядку
    dlmaux,  //17 допоміжні роздільники типа пропусків
    ltrcode// ознака можливості вісімкового кодування
};

enum autStat {
    Eu, //0 Eu - Некласифікований об'єкт
    S0, //1 S0 - Роздільник
    S1g,    //2 S1g - Знак числової константи
    S1c,    //3 S1c - Ціле число
    S2c,    //4 S2c - Число з точкою
    S1e,    //5 S1e - Літера "e" або "E"
    S1q,    //6 S1q - Знак "-" або "+"
    S1p,    //7 S1p - Десяткові цифри порядку
    S1n,    //8 S1n - Елементи імені
    S1s,    //9 S1s - Літери рядка або символьної константи
    S1t,    //10 S1t - Елементи констант, які перетворюються
    S2s,    //11 S2s - Ознака закінчення константи
    S2, //12 S2 - Початковий елемент групового роздільника
    S3, //13 S3 - Наступний елемент групового роздільника
    S3c,    //14?S3c - Ціле число з недесятковою основою
    S0p,    //15?S0p - Ознака типу константи
    Soc,    //16 Soc- Вісімковий код
    Scr,    //17 Scr- Коментар-рядок
    Scl,    //18 Scl- Обмежений коментар
    Ec, //19 Ec - Неправильна константа
    Ep, //20 Ep - Неправильна константа з точкою
    Eq, // Eq - Неправильна константа з порядком
    En, // En - Неправильне ім'я
    Eo  // Eo - Неприпустиме сполучення операцій
};

public class LexicalAnalyser {
    static String[] oprtr = {"", "", "", "", "if", "then", "else", "elseif",
            "switch", "case", "defualt", ""/*endcase*/,
            "break", "return", "while", "while", "continue", //12
            "do", "while", "do", "for", ";", ";", ";", //17
            "while", "do", "with", "endif",
            "void", "extern", "var", "const", "enum", "struct", "union", "register",// 28
            "unsigned", "signed", "char", "short", "int", "long", "int64", "int64",//
            "float", "double", "label", "auto", "static", "volatile", "typedef", "sizeof",//
            "real", "array", "set", "file", "object", "String", "goto",
            "int main()", "function", "procedure",
            "", "", "", "", //Verilog+4
            "", "", "", "",  //Verilog+8
            "", "", "", "", "", "", "", //Verilog+15
            "", "", "", "",   //Verilog+19
            "", "", "", "", "", //Verilog+24
            "var", "", "", "", "", "", "", //Verilog+31
            "", "", "", "", //Verilog+35
            "", "", "", "", "", //Verilog+40
            "", "", "", "", "", "",  //Verilog+46
            "inline", "forward", "interrupt", "export", "extern", "tokType._asm",
            "", "", "",  //Verilog|SQL+3
            "object", "constructor", "desctructor", "property", "resP", "abstract", //P++9
            "class", "public", "private", "protected", "virtual", "friend", //C++15
            "new", "delete", "try", "catch", "throw"/*raise*/, //C++20
            "", "", "", "", "", "",  //Verilog+26
            "", "", "", "", "", "",  //Verilog+32
            "", "", "", "", "", "",  //Verilog+38
            "", "", "", "", "", "",  //Verilog+44
            "", "", "", "", "", "",  //Verilog+50
            "", "", "", "",        //Verilog+54
            "", "", "", "",        //Verilog+58
            "", "", "", "", "", "",  //Verilog+64
            "", "", "", "", "", "",  //Verilog+70
            "", "", "", "", "", "",  //Verilog+76
            "\nfork", "join", "\n{", "}", "{", "}", "[", "]", "(", ")", "{", "}",
            ",;\n", ".;\n", "; ", ",", ":", "?",
            "|=", "&=", "^=", "+=", "-=", "*=", "/=", "%=",
            ">>=", "<<=", "=", "--", "++",
            "--", "++", "#", "::", "*/", "",
            "<", "<=", "==", "!=", ">=", ">",
            "+", "-", "*", "/",
            ".", "->", "**", "<<<", ">>>", "===", "!==",
            "+", "-", "*", "&",
            ":", "=>", "*>", "&&&",    // PV+4 .. tokType._lmts,tokType._eqar,tokType._astar,tokType._trasand,
            "|", "&", "^", "~|", "~&", "~^", "^~", //V+11 tokType._orR,tokType._andR,tokType._xorR,tokType._norR,tokType._nandR,tokType._nxorR,tokType._xornR
            "#", "@", "@*", //V+14 tokType._delay,tokType._event,tokType._events,
            "~|", "~&", "~^", "^~", "&",
            "/*", "//",
            "%", "|", "&", "^",
            "<<", ">>", "||", "&&",
            "!", "~", "/", "in",
            "posedge", "negedge", "or",  //Verilog+3
            "\nfork", "\n{", "{", "[", "(", "{", "boolean"
    };

    String[] cpr = {"", "", "", "", "y", "", "", "",
            " y", "", "", ";\n76}",// 9 case
            "", "x", "y", "x", "", "xy", "x", "", // 12
            "y", "", "", "", //20
            "y", "", "", "",    //24
            "", "", "", "", "", "", "", "",
            "", "", "", "", "", "", "", "",
            "", "", "", "", "", "", "", "",
            "", "", "", "",
            "", "", "", "", "", "",
            "", "", "", "", //Verilog+4
            "", "", "", "",  //Verilog+8
            "", "", "", "", "", "", "", //Verilog+15
            "", "", "", "",   //Verilog+19
            "", "", "", "", "", //Verilog+24
            "", "", "", "", "", "", "", //Verilog+31
            "", "", "", "", //Verilog+35
            "", "", "", "", "", //Verilog+40
            "", "", "", "", "", "",  //Verilog+46
            "", "", "", "", "", "",
            "", "", "",  //Verilog|SQL+3
            "", "", "", "", "", "", //P++9
            "", "", "", "", "", "", //C++15
            "", "", "", "", ""/*raise*/, //C++20
            "", "", "", "", "", "",  //Verilog+26
            "", "", "", "", "", "",  //Verilog+32
            "", "", "", "", "", "",  //Verilog+38
            "", "", "", "", "", "",  //Verilog+44
            "", "", "", "", "", "",  //Verilog+50
            "", "", "", "",        //Verilog+54
            "", "", "", "",        //Verilog+58
            "", "", "", "", "", "",  //Verilog+64
            "", "", "", "", "", "",  //Verilog+70
            "", "", "", "", "", "",  //Verilog+76
            "", "", "", "", "", "", "", "", "", "", "", "",
            "", "", "", "", "", "",
            "", "", "", "", "", "", "", "",
            "", "", "", "", "",
            "", "", "", "", "", "",
            "", "", "", "", "", "",
            "", "", "", "",
            "", "", "", "", "", "", "",
            "", "", "", "",
            "", "", "", "",    // PV+4 .. tokType._lmts,tokType._eqar,tokType._astar,tokType._trasand,
            "", "", "", "", "", "", "", //V+11 tokType._orR,tokType._andR,tokType._xorR,tokType._norR,tokType._nandR,tokType._nxorR,tokType._xornR
            "", "", "", //V+14 tokType._delay,tokType._event,tokType._events,
            "", "", "", "", "",
            "", "", "", "", "", "",
            "", "", "", "",
            "", "", "", "",
            "", "", "",
            "", "", "", "", "", ""
    };

    //char
    tokType[] dlCds = {tokType._EOF, tokType._nil, tokType._nil, tokType._nil, tokType._nil, tokType._nil, tokType._nil, tokType._nil,
            tokType._nil, tokType._nil, tokType._nil, tokType._nil, tokType._nil, tokType._nil, tokType._nil, tokType._nil,   //16
            tokType._nil, tokType._nil, tokType._nil, tokType._nil, tokType._nil, tokType._nil, tokType._nil, tokType._nil,
            tokType._nil, tokType._nil, tokType._nil, tokType._nil, tokType._nil, tokType._nil, tokType._nil, tokType._nil,   //32
            tokType._nil, tokType._xmrk, tokType._nil, tokType._nil/*#*/, tokType._nil/*$*/, tokType._mod, tokType._andB, tokType._nil,
            tokType._brkt, tokType._bckt, tokType._mul, tokType._add, tokType._comma, tokType._sub, tokType._fldDt, tokType._div, //48
            tokType._nil, tokType._nil, tokType._nil, tokType._nil, tokType._nil, tokType._nil, tokType._nil, tokType._nil,
            tokType._nil, tokType._nil, tokType._cln, tokType._EOS, tokType._lt, tokType._ass, tokType._gt, tokType._qmrk,// 64
            tokType._nil, tokType._nil, tokType._nil, tokType._nil, tokType._nil, tokType._nil, tokType._nil, tokType._nil,
            tokType._nil, tokType._nil, tokType._nil, tokType._nil, tokType._nil, tokType._nil, tokType._nil, tokType._nil,//80
            tokType._nil, tokType._nil, tokType._nil, tokType._nil, tokType._nil, tokType._nil, tokType._nil, tokType._nil,
            tokType._nil, tokType._nil, tokType._nil, tokType._ixbr, tokType._nil, tokType._scbr, tokType._xorB, tokType._nil,//96
            tokType._nil, tokType._nil, tokType._nil, tokType._nil, tokType._nil, tokType._nil, tokType._nil, tokType._nil,
            tokType._nil, tokType._nil, tokType._nil, tokType._nil, tokType._nil, tokType._nil, tokType._nil, tokType._nil,//112
            tokType._nil, tokType._nil, tokType._nil, tokType._nil, tokType._nil, tokType._nil, tokType._nil, tokType._nil,
            tokType._nil, tokType._nil, tokType._nil, tokType._opbr, tokType._orB, tokType._ocbr, tokType._invB, tokType._nil,//128
            tokType._nil, tokType._nil, tokType._nil, tokType._nil, tokType._nil, tokType._nil, tokType._nil, tokType._nil,
            tokType._nil, tokType._nil, tokType._nil, tokType._nil, tokType._nil, tokType._nil, tokType._nil, tokType._nil,//144
            tokType._nil, tokType._nil, tokType._nil, tokType._nil, tokType._nil, tokType._nil, tokType._nil, tokType._nil,
            tokType._nil, tokType._nil, tokType._nil, tokType._nil, tokType._nil, tokType._nil, tokType._nil, tokType._nil,//160
            tokType._nil, tokType._nil, tokType._nil, tokType._nil, tokType._nil, tokType._nil, tokType._nil, tokType._nil,
            tokType._nil, tokType._nil, tokType._nil, tokType._nil, tokType._nil, tokType._nil, tokType._nil, tokType._nil,//176
            tokType._nil, tokType._nil, tokType._nil, tokType._nil, tokType._nil, tokType._nil, tokType._nil, tokType._nil,
            tokType._nil, tokType._nil, tokType._nil, tokType._nil, tokType._nil, tokType._nil, tokType._nil, tokType._nil,//192
            tokType._nil, tokType._nil, tokType._nil, tokType._nil, tokType._nil, tokType._nil, tokType._nil, tokType._nil,
            tokType._nil, tokType._nil, tokType._nil, tokType._nil, tokType._nil, tokType._nil, tokType._nil, tokType._nil,//208
            tokType._nil, tokType._nil, tokType._nil, tokType._nil, tokType._nil, tokType._nil, tokType._nil, tokType._nil,
            tokType._nil, tokType._nil, tokType._nil, tokType._nil, tokType._nil, tokType._nil, tokType._nil, tokType._nil,//224
            tokType._nil, tokType._nil, tokType._nil, tokType._nil, tokType._nil, tokType._nil, tokType._nil, tokType._nil,
            tokType._nil, tokType._nil, tokType._nil, tokType._nil, tokType._nil, tokType._nil, tokType._nil, tokType._nil,//240
            tokType._nil, tokType._nil, tokType._nil, tokType._nil, tokType._nil, tokType._nil, tokType._nil, tokType._nil,
            tokType._nil, tokType._nil, tokType._nil, tokType._nil, tokType._nil, tokType._nil, tokType._nil, tokType._nil,//256
    };

    ltrType[] ltCls =
            {ltrType.dlmaux, ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc, ltrType.dlmaux, ltrType.dlmeormr, ltrType.nc, ltrType.nc, ltrType.dlmeormr, ltrType.nc, ltrType.nc, //16
                    ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc,    //32
                    ltrType.dlmaux, ltrType.dlmgrop, ltrType.ltrstrlm, ltrType.dlmunop,
                    ltrType.ltrnmelm, ltrType.dlmgrop, ltrType.dlmgrop, ltrType.ltrstrlm,
                    ltrType.dlobrct, ltrType.dlcbrct, ltrType.dlmgrop, ltrType.ltrsign,
                    ltrType.dlmunop/*dlmbrlst*/, ltrType.ltrsign, ltrType.dldot, ltrType.dlmgrop,    //48
                    ltrType.dgt, ltrType.dgt, ltrType.dgt, ltrType.dgt, ltrType.dgt, ltrType.dgt, ltrType.dgt, ltrType.dgt,
                    ltrType.dgt, ltrType.dgt, ltrType.dlmgrop, ltrType.dlmunop/*dlmbrlst*/, ltrType.dlmgrop, ltrType.dlmgrop, ltrType.dlmgrop, ltrType.dlmunop,// 64
                    ltrType.dlmunop, ltrType.ltrhxdgt, ltrType.ltrhxdgt, ltrType.ltrhxdgt, ltrType.ltrhxdgt, ltrType.ltrexplt, ltrType.ltrhxdgt, ltrType.ltrnmelm,
                    ltrType.ltrnmelm, ltrType.ltrnmelm, ltrType.ltrnmelm, ltrType.ltrnmelm, ltrType.ltrnmelm, ltrType.ltrnmelm, ltrType.ltrnmelm, ltrType.ltrnmelm,//80
                    ltrType.ltrnmelm, ltrType.ltrnmelm, ltrType.ltrnmelm, ltrType.ltrnmelm, ltrType.ltrnmelm, ltrType.ltrnmelm, ltrType.ltrnmelm, ltrType.ltrnmelm,
                    ltrType.ltrtpcns/*ltrnmelm*/, ltrType.ltrnmelm, ltrType.ltrnmelm, ltrType.dlobrct, ltrType.ltrtrnfm, ltrType.dlcbrct, ltrType.dlmgrop, ltrType.ltrnmelm,//96
                    ltrType.dlmunop, ltrType.ltrhxdgt, ltrType.ltrhxdgt, ltrType.ltrhxdgt, ltrType.ltrhxdgt, ltrType.ltrexplt, ltrType.ltrhxdgt, ltrType.ltrnmelm,
                    ltrType.ltrnmelm, ltrType.ltrnmelm, ltrType.ltrnmelm, ltrType.ltrnmelm, ltrType.ltrnmelm, ltrType.ltrnmelm, ltrType.ltrnmelm, ltrType.ltrnmelm,//112
                    ltrType.ltrnmelm, ltrType.ltrnmelm, ltrType.ltrnmelm, ltrType.ltrnmelm, ltrType.ltrnmelm, ltrType.ltrnmelm, ltrType.ltrnmelm, ltrType.ltrnmelm,
                    ltrType.ltrtpcns/*ltrnmelm*/, ltrType.ltrnmelm, ltrType.ltrnmelm, ltrType.dlobrct, ltrType.dlmgrop, ltrType.dlcbrct, ltrType.dlmunop/*dlmgrop*/, ltrType.nc,//128
                    ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc,   //144
                    ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc,   //160
                    ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc,   //176
                    ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc,   //192
                    ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc,   //208
                    ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc,   //224
                    ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc,   //240
                    ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc, ltrType.nc,   //256
                    //...
            };

    autStat[][] nxtSts = new autStat[][]{{autStat.Eu, autStat.Eu, autStat.Eu, autStat.Eu, autStat.Eu, autStat.Eu, autStat.Eu, autStat.Eu, autStat.S0, autStat.S2, autStat.S0, autStat.S2, autStat.S0, autStat.S0, autStat.S0, autStat.S0, autStat.S0, autStat.S0},        //0 для Eu
            {autStat.S1c, autStat.S1n, autStat.S1n, autStat.S1n, autStat.S1n, autStat.S1s, autStat.Soc, autStat.Eu, autStat.S0, autStat.S2, autStat.S0, autStat.S2, autStat.S0, autStat.S0, autStat.S0, autStat.S0, autStat.S0, autStat.S0},    //1 для S0 -> S0c
            {autStat.S1c, autStat.Ec, autStat.Ec, autStat.Ec, autStat.Ec, autStat.Ec, autStat.Soc, autStat.Eu, autStat.S2c, autStat.S2, autStat.S0, autStat.S2, autStat.S0, autStat.S0, autStat.S0, autStat.S0, autStat.S0, autStat.S0},        //2 для S1g
            {autStat.S1c, autStat.S1e, autStat.Ec, autStat.Ec, autStat.Ec, autStat.Ec, autStat.Soc, autStat.Eu, autStat.S2c, autStat.S2, autStat.S0, autStat.S2, autStat.S0, autStat.S0, autStat.S0, autStat.S0, autStat.S0, autStat.S0},    //3 для S1c
            {autStat.S2c, autStat.S1e, autStat.Ec, autStat.Ec, autStat.Ec, autStat.Ec, autStat.Soc, autStat.Eu, autStat.Ec, autStat.S2, autStat.S0, autStat.S2, autStat.S0, autStat.S0, autStat.S0, autStat.S0, autStat.S0, autStat.S0},        //4 для S2c
            {autStat.S1p, autStat.Ec, autStat.Ec, autStat.Ec, autStat.Ec, autStat.Ec, autStat.Soc, autStat.Eu, autStat.S0, autStat.S1q, autStat.S0, autStat.S2, autStat.S0, autStat.S0, autStat.S0, autStat.S0, autStat.S0, autStat.S0},        //5 для S1e
            {autStat.S1p, autStat.Ec, autStat.Ec, autStat.Ec, autStat.Ec, autStat.Ec, autStat.Soc, autStat.Eu, autStat.Ec, autStat.Ec, autStat.S0, autStat.S2, autStat.S0, autStat.S0, autStat.S0, autStat.S0, autStat.S0, autStat.S0},        //6 для S1q
            {autStat.S1p, autStat.Ec, autStat.Ec, autStat.Ec, autStat.Ec, autStat.Ec, autStat.Soc, autStat.Eu, autStat.S0, autStat.S2, autStat.S0, autStat.S2, autStat.S0, autStat.S0, autStat.S0, autStat.S0, autStat.S0, autStat.S0},        //7 для S1p
            {autStat.En, autStat.S1n, autStat.S1n, autStat.S1n, autStat.S1n, autStat.En, autStat.S1n, autStat.En, autStat.S0, autStat.S2, autStat.S0, autStat.S2, autStat.S0, autStat.S0, autStat.S0, autStat.S0, autStat.S0, autStat.S0},    //8 для S1n
            {autStat.S1s, autStat.S1s, autStat.S1s, autStat.S1s, autStat.S1s, autStat.S2s, autStat.S1t, autStat.S1s, autStat.S1s, autStat.S1s, autStat.S1s, autStat.S1s, autStat.S1s, autStat.S1s, autStat.S1s, autStat.S1s, autStat.S1s, autStat.S1s},//9 для S1s
            {autStat.S1t, autStat.Ec, autStat.S1s, autStat.S1s, autStat.S1s, autStat.S2s, autStat.S1s, autStat.Ec, autStat.S0, autStat.S2, autStat.S0, autStat.S2, autStat.S0, autStat.S0, autStat.S0, autStat.S0, autStat.S0, autStat.S0},    //10 для S1t
            {autStat.S1s, autStat.Ec, autStat.S1s, autStat.S1s, autStat.S1s, autStat.S1s, autStat.S1t, autStat.Ec, autStat.S0, autStat.S2, autStat.S0, autStat.S2, autStat.S0, autStat.S0, autStat.S0, autStat.S0, autStat.S0, autStat.S0},    //11 для S2s
            {autStat.S1c, autStat.S1n, autStat.S1n, autStat.S1n, autStat.S1n, autStat.S1s, autStat.Soc, autStat.Eu, autStat.S0, autStat.S3, autStat.S0, autStat.S3, autStat.S0, autStat.S0, autStat.S0, autStat.S0, autStat.S0, autStat.S0},    //12 для S2 - Початковий елемент групового роздільника
            {autStat.S1c, autStat.S1n, autStat.S1n, autStat.S1n, autStat.S1n, autStat.S1s, autStat.Soc, autStat.Eu, autStat.S0, autStat.S3, autStat.S0, autStat.S3, autStat.S0, autStat.S0, autStat.S0, autStat.S0, autStat.S0, autStat.S0},    //13 для S3 - Наступний елемент групового роздільника
            {autStat.S0p, autStat.Ec, autStat.Ec, autStat.S0p, autStat.Ec, autStat.Ec, autStat.Ec, autStat.Ec, autStat.S0, autStat.S0, autStat.S0, autStat.S0, autStat.S0, autStat.S0, autStat.S0, autStat.S0, autStat.S0, autStat.S0},            //14 для S3c
            {autStat.S3c, autStat.Ec, autStat.Ec, autStat.S3c, autStat.Ec, autStat.Ec, autStat.Ec, autStat.Ec, autStat.S0, autStat.S0, autStat.S0, autStat.S0, autStat.S0, autStat.S0, autStat.S0, autStat.S0, autStat.S0, autStat.S0},            //15 для S0p
            {autStat.Soc, autStat.S3c, autStat.S3c, autStat.Ec, autStat.Ec, autStat.Ec, autStat.Ec, autStat.Eu, autStat.S2c, autStat.S0, autStat.S0, autStat.S0, autStat.S0, autStat.S0, autStat.S0, autStat.S0, autStat.S0, autStat.S0},        //16 для Soc- Вісімковий код
            {autStat.Scr, autStat.Scr, autStat.Scr, autStat.Scr, autStat.Scr, autStat.Scr, autStat.Scr, autStat.Scr, autStat.Scr, autStat.Scr, autStat.Scr, autStat.Scr, autStat.Scr, autStat.Scr, autStat.Scr, autStat.Scr, autStat.S0, autStat.Scr},//17 для Scr- Коментар-рядок
            {autStat.Scl, autStat.Scl, autStat.Scl, autStat.Scl, autStat.Scl, autStat.Scl, autStat.Scl, autStat.Scl, autStat.Scl, autStat.Scl, autStat.Scl, autStat.Scl, autStat.Scl, autStat.Scl, autStat.Scl, autStat.Scl, autStat.Scl, autStat.Scl},    // для Scl
            {autStat.Ec, autStat.Ec, autStat.Ec, autStat.Ec, autStat.Ec, autStat.Ec, autStat.Ec, autStat.Ec, autStat.S0, autStat.S0, autStat.S0, autStat.Eu, autStat.Eu, autStat.S0, autStat.S0, autStat.S0, autStat.S0, autStat.S0},            // для Ec
            {autStat.Ep, autStat.Ep, autStat.Ep, autStat.Ep, autStat.Ep, autStat.Ep, autStat.Ec, autStat.Ec, autStat.S0, autStat.S0, autStat.S0, autStat.S0, autStat.S0, autStat.S0, autStat.S0, autStat.S0, autStat.S0, autStat.S0},            // для Ep
            {autStat.Eq, autStat.Eq, autStat.Eq, autStat.Eq, autStat.Eq, autStat.Eq, autStat.Eq, autStat.Ec, autStat.S0, autStat.S0, autStat.S0, autStat.S0, autStat.S0, autStat.S0, autStat.S0, autStat.S0, autStat.S0, autStat.S0},            // для Eq
            {autStat.En, autStat.En, autStat.Eq, autStat.Eq, autStat.Eq, autStat.Eq, autStat.Eq, autStat.Ec, autStat.S0, autStat.S0, autStat.S0, autStat.S0, autStat.S0, autStat.S0, autStat.S0, autStat.S0, autStat.S0, autStat.S0},            // для En
            {autStat.Eo, autStat.Eo, autStat.Eo, autStat.Eq, autStat.Eq, autStat.Eq, autStat.Eq, autStat.Ec, autStat.S0, autStat.S0, autStat.S0, autStat.S0, autStat.S0, autStat.S0, autStat.S0, autStat.S0, autStat.S0, autStat.S0}           // для Eo...
    };
    // for C/C++
    recrdKWD[] tablKWD = new recrdKWD[]{
            new recrdKWD(oprtr[tokType._ne.ordinal()].toCharArray(), tokType._ne, 1),               new recrdKWD(oprtr[tokType._asMod.ordinal()].toCharArray(), tokType._asMod, 1),
            new recrdKWD(oprtr[tokType._and.ordinal()].toCharArray(), tokType._and, 1),             new recrdKWD(oprtr[tokType._asAnd.ordinal()].toCharArray(), tokType._asAnd, 1),
            new recrdKWD(oprtr[tokType._asMul.ordinal()].toCharArray(), tokType._asMul, 1),         new recrdKWD(oprtr[tokType._inr.ordinal()].toCharArray(), tokType._inr, 1),
            new recrdKWD(oprtr[tokType._asAdd.ordinal()].toCharArray(), tokType._asAdd, 1),         new recrdKWD(oprtr[tokType._dcr.ordinal()].toCharArray(), tokType._dcr, 1),
            new recrdKWD(oprtr[tokType._asSub.ordinal()].toCharArray(), tokType._asSub, 1),         new recrdKWD(oprtr[tokType._fldPt.ordinal()].toCharArray(), tokType._fldPt, 1),
            new recrdKWD(oprtr[tokType._rem.ordinal()].toCharArray(), tokType._rem, 1),             new recrdKWD(oprtr[tokType._remL.ordinal()].toCharArray(), tokType._remL, 1),// 10
            new recrdKWD(oprtr[tokType._asDiv.ordinal()].toCharArray(), tokType._asDiv, 1),         new recrdKWD(oprtr[tokType._shLft.ordinal()].toCharArray(), tokType._shLft, 1),
            new recrdKWD(oprtr[tokType._asShl.ordinal()].toCharArray(), tokType._asShl, 1),         new recrdKWD(oprtr[tokType._le.ordinal()].toCharArray(), tokType._le, 1),
            new recrdKWD(oprtr[tokType._eq.ordinal()].toCharArray(), tokType._eq, 1),               new recrdKWD(oprtr[tokType._ge.ordinal()].toCharArray(), tokType._ge, 1),        // 16
            new recrdKWD(oprtr[tokType._shRgt.ordinal()].toCharArray(), tokType._shRgt, 1),         new recrdKWD(oprtr[tokType._asShr.ordinal()].toCharArray(), tokType._asShr, 1),
            new recrdKWD(oprtr[tokType._asXor.ordinal()].toCharArray(), tokType._asXor, 1),         new recrdKWD(oprtr[tokType._auto.ordinal()].toCharArray(), tokType._auto, 1),// 20 подвыйна двокрапка
            new recrdKWD(oprtr[tokType._break.ordinal()].toCharArray(), tokType._break, 1),         new recrdKWD(oprtr[tokType._switch.ordinal()].toCharArray(), tokType._switch, 0),
            new recrdKWD(oprtr[tokType._catch.ordinal()].toCharArray(), tokType._catch, 6),         new recrdKWD(oprtr[tokType._char.ordinal()].toCharArray(), tokType._char, 1),
            new recrdKWD(oprtr[tokType._class.ordinal()].toCharArray(), tokType._class, 2),         new recrdKWD(oprtr[tokType._continue.ordinal()].toCharArray(), tokType._continue, 1),
            new recrdKWD(oprtr[tokType._const.ordinal()].toCharArray(), tokType._const, 1),         new recrdKWD(oprtr[tokType._default.ordinal()].toCharArray(), tokType._default, 1),
            new recrdKWD(oprtr[tokType._delete.ordinal()].toCharArray(), tokType._delete, 2),       new recrdKWD(oprtr[tokType._repeat.ordinal()].toCharArray(), tokType._repeat, 1),
            new recrdKWD(oprtr[tokType._double.ordinal()].toCharArray(), tokType._double, 1),       new recrdKWD(oprtr[tokType._else.ordinal()].toCharArray(), tokType._else, 1),
            new recrdKWD(oprtr[tokType._enum.ordinal()].toCharArray(), tokType._enum, 0),           new recrdKWD(oprtr[tokType._extern.ordinal()].toCharArray(), tokType._extern, 0),
            new recrdKWD(oprtr[tokType._float.ordinal()].toCharArray(), tokType._float, 0),         new recrdKWD(oprtr[tokType._for.ordinal()].toCharArray(), tokType._for, 0),
            new recrdKWD(oprtr[tokType._friend.ordinal()].toCharArray(), tokType._friend, 0),       new recrdKWD(oprtr[tokType._goto.ordinal()].toCharArray(), tokType._goto, 0),
            new recrdKWD(oprtr[tokType._if.ordinal()].toCharArray(), tokType._if, 0),               new recrdKWD(oprtr[tokType._int.ordinal()].toCharArray(), tokType._int, 0),
            new recrdKWD(oprtr[tokType._sint64.ordinal()].toCharArray(), tokType._sint64, 0),       new recrdKWD(oprtr[tokType._long.ordinal()].toCharArray(), tokType._long, 0),
            new recrdKWD(oprtr[tokType._new.ordinal()].toCharArray(), tokType._new, 0),             new recrdKWD(oprtr[tokType._private.ordinal()].toCharArray(), tokType._private, 0),
            new recrdKWD(oprtr[tokType._protected.ordinal()].toCharArray(), tokType._protected, 0), new recrdKWD(oprtr[tokType._public.ordinal()].toCharArray(), tokType._public, 0),
            new recrdKWD(oprtr[tokType._register.ordinal()].toCharArray(), tokType._register, 0),   new recrdKWD(oprtr[tokType._return.ordinal()].toCharArray(), tokType._return, 0),
            new recrdKWD(oprtr[tokType._short.ordinal()].toCharArray(), tokType._short, 0),         new recrdKWD(oprtr[tokType._signed.ordinal()].toCharArray(), tokType._signed, 0),
            new recrdKWD(oprtr[tokType._sizeof.ordinal()].toCharArray(), tokType._sizeof, 0),       new recrdKWD(oprtr[tokType._static.ordinal()].toCharArray(), tokType._static, 0),
            new recrdKWD(oprtr[tokType._struct.ordinal()].toCharArray(), tokType._struct, 0),       new recrdKWD(oprtr[tokType._case.ordinal()].toCharArray(), tokType._case, 1),
            new recrdKWD(oprtr[tokType._throw.ordinal()].toCharArray(), tokType._throw, 0),         new recrdKWD(oprtr[tokType._tryC.ordinal()].toCharArray(), tokType._tryC, 0),
            new recrdKWD(oprtr[tokType._typedef.ordinal()].toCharArray(), tokType._typedef, 0),     new recrdKWD(oprtr[tokType._union.ordinal()].toCharArray(), tokType._union, 0),
            new recrdKWD(oprtr[tokType._unsigned.ordinal()].toCharArray(), tokType._unsigned, 0),   new recrdKWD(oprtr[tokType._virtual.ordinal()].toCharArray(), tokType._virtual, 0),
            new recrdKWD(oprtr[tokType._void.ordinal()].toCharArray(), tokType._void, 0),           new recrdKWD(oprtr[tokType._volatile.ordinal()].toCharArray(), tokType._volatile, 0),
            new recrdKWD(oprtr[tokType._whileP.ordinal()].toCharArray(), tokType._whileP, 0),       new recrdKWD(oprtr[tokType._asOr.ordinal()].toCharArray(), tokType._asOr, 1),
            new recrdKWD(oprtr[tokType._or.ordinal()].toCharArray(), tokType._or, 1),               new recrdKWD(oprtr[tokType._string.ordinal()].toCharArray(), tokType._string, 0),
            new recrdKWD(oprtr[290].toCharArray(), tokType._bool, 0)
    };

    int cmpStr1(int start1,int start2){
        int count = 0;
        while (imgBuf[start1]!=imgBuf[start2]&&imgBuf[start1] != 0&&imgBuf[start2] != 0){
            count++;
            start1++;
            start2++;
        }
        return count;
    }

    // порівняння рядків
    // порівняння терміналів за відношенням порядку
    int cmpTrm(lxNode k0, lxNode kArg)//cmpKys
    {
        int start1 = k0.start;
        int start2 = kArg.start;
        int i = cmpStr1(start1, start2);
        if (i!=0)return i;
        return k0.stkLength - kArg.stkLength; // порівняння номерів модулів
    }

    // вибірка через пошук за двійковим деревом
    indStrUS selBTr(lxNode kArg, indStrUS rtTb)
    {
        int df;
        while ((df = cmpTrm(kArg, rtTb.pKyStr))!=0)
            if (df>0) {
                if (rtTb.pRtPtr!=null)rtTb = rtTb.pRtPtr;
                else break;
            }
            else {
                if (rtTb.pLtPtr!=null)rtTb = rtTb.pLtPtr;
                else break;
            }
        rtTb.dif = df;
        return rtTb;
    }

    int nNdxNds = 0;
    indStrUS[] ndxNds= makeArr(50);// = { { NULL,NULL,NULL,0 } },

    indStrUS[] makeArr(int size){
        indStrUS[] arr = new indStrUS[size];
        for (int i=0;i<size;i++){
            arr[i] = new indStrUS(null,null,null,0 );
        }
        return arr;
    }
           // *pRtNdx = ndxNds,
    indStrUS nilNds = new indStrUS(null,null,null,0 );

    indStrUS insBTr(lxNode pElm, indStrUS rtTb)
    {
        indStrUS pInsNod;//,*pNod;
        if (rtTb.pKyStr == null)
        {
            rtTb.pKyStr = pElm;
            return rtTb;
        }
        // if(rtTb->pKyStr->ndOp==_nil)rtTb->pKyStr=pElm;
        else {
            pInsNod = selBTr(pElm, rtTb);
            if (pInsNod.dif!=0)
            {
                ndxNds[++nNdxNds] = nilNds;
                if (pInsNod.dif<0)pInsNod = pInsNod.pLtPtr = ndxNds[nNdxNds];
                else pInsNod = pInsNod.pRtPtr = ndxNds[nNdxNds];
                ndxNds[nNdxNds].pKyStr = pElm;
            }
        }
        return pInsNod;
    }

    public String[] getOprtr() {
        return oprtr;
    }

    public char[] imgBuf = new char[1024]; // буфер вхідних образів
    int x = 0, y = 0, f = 0, cl;
    int nImBg = 0, nImCr = 0;
    public lxNode[] nodes = new lxNode[1024];

    char ReadLtr() {
        char c = (char) 0;
        cl = getnextchar();
        if (cl == 13 || cl == 10) {
            x = 0;
            y++;
            if (cl == 10) cl = getnextchar();
        }
        //while (cl == 9) {
        //    cl = getnextchar();
        //}
        x++;
        c = (char) cl;
        if (cl == -1) c = (char) 0; //c=13;
        imgBuf[nImCr++] = c;//cl;
        return c;
    }

    boolean flag = false;
    char[] text = new char[1024];
    int count = 0;
    public String path = "";

    private char getnextchar() {
        if (!flag) {
            System.out.print("Input file path: ");
            Scanner scan = new Scanner(System.in);

            path = scan.nextLine();
            //path = "d:/1.h";
            char[] text1 = {};
            File f = new File(path);
            if (f.exists() && !f.isDirectory()) {
                try {
                    FileReader fileReader = new FileReader(path);
                    String fileContents = "";

                    int i;

                    while ((i = fileReader.read()) != -1) {
                        char ch = (char) i;

                        fileContents = fileContents + ch;
                    }

                    System.out.println(fileContents);
                    text1 = fileContents.toCharArray();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("File not found!");
                System.out.println("Press Any Key To Continue...");
                //scan.nextLine();
                System.exit(0);
            }


            for (int i = 0; i < text1.length; i++) {
                text[i] = text1[i];
            }
            flag = true;
        }
        char c = text[count];
        count++;
        return c;
    }

    void lxInit(int lxNmb, ltrType cl) {
        nodes[lxNmb].x = x;
        nodes[lxNmb].y = y;
        nodes[lxNmb].f = f;
        nodes[lxNmb].ndOp = tokType._nil;
        nodes[lxNmb].prnNd = 0;
        //nodes[lxNmb].prvNd = (lxNode)(imgBuf[nImCr]);
        nodes[lxNmb].start = nImCr;
        if (cl.ordinal() < ltrType.nc.ordinal()/*&&nImCr!=0*/) nodes[lxNmb].start = nImCr - 1;
        nodes[lxNmb].pstNd = null;
        nodes[lxNmb].dataType = 0;
        nodes[lxNmb].resLength = 0;
        nodes[lxNmb].stkLength = 0;
    }

    int nNode = 0;
    int cntMdB = 0;
    int pRtNdx = 0;

    autStat s = autStat.S0, sP; // поточний та попередній стан лексеми
    ltrType c = ltrType.values()[0];  // клас чергової літери
    char l = (char) 1;      // чергова літера (початок фыктивний)
    recrdKWD pRt;

    public int LxAnlzr() {//static int lxNmb=0;
        autStat SP;
        int s1, c1;
        char l1, l0;        // чергова літера
        lxInit(nNode, c);
        do {
            sP = s;
            SP = sP;
            l1 = l; // запам'ятовування стану
            l = ReadLtr(); // читання літери
            l0 = l;
            c = ltCls[l];  // визначення класу літери
            c1 = (int) c.ordinal();
            if ((s == autStat.Scl) && (c != ltrType.dlmeorml)) continue;
            s = nxtSts[(int) s.ordinal()][(int) c.ordinal()];//[c<dlmaux?c:dlmaux];// стан лексеми
            s1 = (int) s.ordinal();
            if (s == autStat.Scr) continue;
            if (((sP == autStat.S2) || (sP == autStat.S3)) && ((c.ordinal() > ltrType.nc.ordinal()) && (c.ordinal() < ltrType.dlmeormr.ordinal()))) {// пошук в таблиці групових роздільників
                imgBuf[nImCr] = (char) 0;
                pRt = selBin(imgBuf, tablKWD, 67, (int) nImBg);
                if (pRt != null) {
                    nodes[nNode].ndOp = pRt.func;
                    if (pRt.func == tokType._remL) {
                        s = autStat.Scr;
                        nodes[nNode].start = nImCr;
                        continue;
                    }
                    if (pRt.func == tokType._remL) {
                        s = autStat.Scl;
                    }
                    continue;
                    //break;
                } else {
                    if (sP != autStat.S3) {
                        nodes[nNode].ndOp = dlCds[l1];
                        if ((nodes[nNode - 1].ndOp.ordinal() > tokType._cnst.ordinal()) && (nodes[nNode - 1].ndOp != tokType._bckt)
                                && (nodes[nNode - 1].ndOp != tokType._scbr))// перевірка унарності
                        {
                            if (nodes[nNode].ndOp == tokType._add) nodes[nNode].ndOp = tokType._addU;
                            if (nodes[nNode].ndOp == tokType._sub) nodes[nNode].ndOp = tokType._subU;
                            if (nodes[nNode].ndOp == tokType._mul) nodes[nNode].ndOp = tokType._refU;
                            if (nodes[nNode].ndOp == tokType._andB) nodes[nNode].ndOp = tokType._ptrU;
                        }
                        imgBuf[nImBg] = imgBuf[nImBg + 1];
                        nodes[nNode].prvNd = null;
                    } else {
                        imgBuf[nImBg] = imgBuf[nImBg + 1];
                        nImCr--;
                    }
                    nImCr--;
                    sP = autStat.S0;
                    s = nxtSts[(int) sP.ordinal()][(int) c.ordinal()];//[c<dlmaux?c:dlmaux];// стан лексеми
                    return nNode++;
                }
            }
            s1 = s.ordinal();
        }
        while ((s != autStat.S0) && (s != autStat.S2) && !(((sP == autStat.S0) || (sP == autStat.S2) || (sP == autStat.S3)) && (s.ordinal() < autStat.S2.ordinal())));

        s1 = sP.ordinal();
        switch (sP) {
            case Scr:
            case Scl:
                imgBuf[nImCr++] = (char) 0;
                //	((char*)(nodes[nNode].prvNd))--;
                nImBg = nImCr;
                break;
            case S2:
            case S0:
                    /*	if(s==S0)
                    //		dGroup(nNode);// аналіз групових роздільників
                    {//imgBuf[nImBg]=l1;
                    imgBuf[++nImCr]=0;//nImCr++;
                    }*/
                if (nodes[nNode].ndOp != tokType._nil) {
                    nImCr = nImBg;
                    l = ' ';
                    return nNode++;
                }
                //	if(sP!=S0)
                nodes[nNode].ndOp =/*(enum tokType)*/dlCds[l1];//dlCds[l1];
                if ((nNode - 1) >= 0) {
                    if (nodes[nNode - 1].ndOp.ordinal() > tokType._cnst.ordinal() && nodes[nNode - 1].ndOp != tokType._bckt
                            && nodes[nNode - 1].ndOp != tokType._scbr)// перевірка унарності
                    {
                        if (nodes[nNode].ndOp == tokType._add) nodes[nNode].ndOp = tokType._addU;
                        if (nodes[nNode].ndOp == tokType._sub) nodes[nNode].ndOp = tokType._subU;
                        if (nodes[nNode].ndOp == tokType._mul) nodes[nNode].ndOp = tokType._refU;
                        if (nodes[nNode].ndOp == tokType._andB) nodes[nNode].ndOp = tokType._ptrU;
                    }
                }
                if (nodes[nNode].ndOp == tokType._opbr && ((nodes[nNode - 1].ndOp == tokType._ass) || (nodes[nNode - 1].ndOp == tokType.values()[cntMdB]))) {
                    cntMdB++;
                    nodes[nNode].ndOp = tokType._tdbr;
                }
                if (nodes[nNode].ndOp == tokType._ocbr && nodes[nNode].ndOp == tokType.values()[cntMdB]) {
                    nodes[nNode].ndOp = tokType._tcbr;
                    cntMdB--;
                }
                if (nodes[nNode].ndOp != tokType._nil)
                //		&&imgBuf[nImBg]==)
                {
                    nodes[nNode].prvNd = null;
                    if (nImBg + 1 != nImCr)
                    //		 if(dlCdsC[l0]!=_nil||ltClsC[l0]==dlmaux||ltClsC[l0]==dlmeormr)
                    {
                        imgBuf[nImBg] = imgBuf[nImBg + 1];
                        if (s != autStat.S0) {
                            nImCr--;
                            imgBuf[nImBg] = imgBuf[nImCr];
                            nImCr = nImBg + 1;
                        }// 04.07.07
                        else nImCr = nImBg;
                    }
                    return nNode++;
                } else if (ltCls[imgBuf[nImBg]] == ltrType.dlmaux/*&&ltClsC[imgBuf[nImBg]]>nc*/) {
                    imgBuf[nImBg] = imgBuf[nImBg + 1];
                    nImCr--;
                }
                return nNode;
            case S1n:// пошук ключових слів та імен
                imgBuf[nImCr - 1] = (char) 0;
                // пошук у таблиці ключів;
                if (imgBuf[nImBg] == 13) nImBg++;
                pRt = selBin(imgBuf, tablKWD, 69, (int) nImBg);
                if (pRt != null) {
                    nodes[nNode].ndOp = pRt.func;
                    // якщо знайдено
                    nodes[nNode].prvNd = null;
                    nImCr = nImBg;
                    if (c != ltrType.dlmeormr && c != ltrType.dlmaux) imgBuf[nImCr++] = l;
                    return nNode++;
                }
                // якщо не знайдено
                nodes[nNode].ndOp = tokType._nam;
                //insBTr(nodes[nNode], ndxNds[pRtNdx]);
                nImBg = nImCr;
                if (c != ltrType.dlmeormr && c != ltrType.dlmaux) imgBuf[nImCr++] = l;
                break;
            default:    // не дійшли до класифікованих помилок
            case Eu:
            case Ec:
            case Ep:
            case Eq:
            case En:
            case Eo:// обробка помилок
            case S1c:
            case S2c:
            case S1p:
            case S2s:   // формування констант
            {
                imgBuf[nImCr - 1] = (char) 0;
                nodes[nNode].resLength = (int) sP.ordinal();//	frmCns(sP, nNode); break;
                if (sP != autStat.Eu && sP != autStat.Ec && sP != autStat.Ep && sP != autStat.Eq && sP != autStat.En && sP != autStat.Eo) {
                    nodes[nNode].ndOp = tokType._srcn;
                } else {
                    eNeut(nNode);
                }      // фіксація помилки
                nodes[nNode].dataType = (int) sP.ordinal();
                //insBTr(nodes[nNode], ndxNds[pRtNdx]);
                nImBg = nImCr;
                if (c != ltrType.dlmeormr && c != ltrType.dlmaux) imgBuf[nImCr++] = l;
                break;
            }
            case S3: {
                nImCr = nImBg;
                nodes[nNode].prvNd = null;
                imgBuf[nImBg] = imgBuf[nImBg + 2];
                s1 = s.ordinal();
                if (s != autStat.S0) nImCr = nImBg + 1;
                break;
            }//else nImCr--;
        }
        return nNode++;
    }

    void eNeut(int lxNmb) {
        nodes[lxNmb].ndOp = tokType._err;
    }

    // порівняння рядків
    int cmpStr(char[] s1, char[] s2, int start) {
        int n = 0;
        int k = start;
        while(s2[k]==13||s2[k]==9||s2[k]==10){
            k++;
        }
        while ((s1[n] == s2[k]) && (s1[n] != 0)) {
            n++;
            k++;
            if (n >= s1.length-1 || k >= s2.length-1) {
                break;
            }
        }
        return s1[n] - s2[k];
    }

    // порівняння за відношенням порядку
    int cmpKys(char[] k0, char[] kArg, int start) {
        int i = cmpStr(k0, kArg, start);
        //if(i)
        return i;
    }

    // вибірка за двійковим пошуком
    recrdKWD selBin(char[] kArg, recrdKWD[] tb, int ln, int start) {
        for (int i = 0; i < ln; i++) {
            if (cmpKys(tb[i].key, kArg, start) == 0) {
                return tb[i];
            }
        }
        return null;
        /*int i, nD = -1, nU = ln, n = (nD + nU) >> 1;
        i = cmpKys(tb[n].key, kArg, start);
        while (i != 0)
        {
            if (i>0)nU = n; else nD = n;
            n = (nD + nU) >> 1;
            if (n == nD) {
                return null; }
            i = cmpKys(tb[n].key, kArg, start);
        }
        return tb[n];
        */
    }

    public class recrdKWD  // структура рядка таблиці ключових слыв ы роздыльникыв
    {
        public char[] key;// примірник структури ключа
        public tokType func;// примірник функціональної частини
        public int versn;  // номер первинноъ версыъ застосування

        public recrdKWD(char[] key, tokType func, int versn) {
            this.key = key;
            this.func = func;
            this.versn = versn;
        }
    }

    public LexicalAnalyser() {
        for (int i = 0; i < 1024; i++) {
            nodes[i] = new lxNode(tokType._nil, null, null, 0, 0, 0, 0, 0, 0, 0, 0);
        }
    }
}