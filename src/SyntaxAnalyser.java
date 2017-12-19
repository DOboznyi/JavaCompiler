enum tokPrec // передування основних типів лексем
{
    nil,    // кінець файлу
    pend,//кінцевого символу програми-модуля - '.' або 'endmodule'
    pclf,// закритої операторної дужки - '}' або 'end' чи 'join'
    pclb,// закритої дужки - ')'
    peos,// кінця оператора - ';'
    pekw,// початкового ключового слова конструкції - 'enum', 'struct'
    pmkw,// проміжного ключового слова конструкції - 'else', ...
    pskw,// початкового ключового слова конструкції - 'if', 'for'
    prep,// початкового ключового слова конструкції - 'do', 'repeat'
    popf,// відкритої операторної дужки - '{' або 'begin' чи 'fork'
    pcld,// закритої дужки даних - '}'
    pbkw,// початкового ключового слова конструкції - 'int', 'float'
    pdol,// роздільника списка - ',' для параметрів ^ одну позицію
    pcls,// закритої дужки - ']'
    pass,// присвоювань - '=', '+=', ...
    psmc,// двокрапки - ':'
    pcnd,// умовної операції - '?'
    pacf,// доступу до полів - '.' та '->'
    porl,// логічної диз'юнкції - '||'
    panl,// логічної кон'юнкції - '&&'
    porw,// побітової диз'юнкції - '|' та '~|'
    pxrw,// додавання за модулем 2 - '^', '^~' та '~^'
    panw,// побітової кон'юнкції - '&' та '~&'
    pequ,//відношень рівності-нерівності - '==', '!=', '===','!=='
    prel,// відношень більше-менше  - '<', '<=', '>=' та '>'
    pshf,// зсувів - '<<', '>>', '<<<' та '>>>'
    padd,// додавання-віднімання - '+' та '-'
    pmul,// множення-ділення - '*', '/' та '%'
    ppwr,// піднесення до ступеню
    popd,// відкритої дужки даних - '{'
    pops,// відкритої дужки - '['
    popb,// відкритої дужки - '('
    puno,// унарної операції
    ptrm,// терму: константи та імені змінної, функції, тощо
    pprg//кінцевого символу програми-модуля- 'progam' або 'module'
};

public class SyntaxAnalyser {

    tokPrec[] opPrF = {
            tokPrec.nil, tokPrec.ptrm,//_nil,tokPrec. _nam,tokPrec.	//0 зовнішнє подання
            tokPrec.ptrm, tokPrec.ptrm,//_srcn,tokPrec.	_cnst,tokPrec.	//2 вхідне і внутрішнє кодування константи
            tokPrec.pskw, tokPrec.pmkw, tokPrec.pmkw, tokPrec.nil,//_if,tokPrec._then,tokPrec._else,tokPrec._elseif,tokPrec.	//4 if then else elseif
            tokPrec.pskw, tokPrec.pskw, tokPrec.pskw, tokPrec.nil,//_case,tokPrec. _switch,tokPrec. _default,tokPrec. _endcase,//8 case switch defualt endcase
            tokPrec.pbkw, tokPrec.pskw, tokPrec.pskw, tokPrec.pmkw,//_break,tokPrec. _return,tokPrec. _whileP,tokPrec. _whileN,tokPrec. //12 break return while do
            tokPrec.pbkw, tokPrec.prep, tokPrec.nil, tokPrec.nil,//_continue,tokPrec. _repeat,tokPrec. _untilN,tokPrec. _endloop,tokPrec. //16 continue repeat until
            tokPrec.pskw, tokPrec.pmkw, tokPrec.pmkw, tokPrec.pmkw,//_for,tokPrec. _to,tokPrec. _downto,tokPrec. _step,// for to downto step
            tokPrec.nil, tokPrec.nil, tokPrec.pbkw, tokPrec.nil,//_untilP,tokPrec. _loop,tokPrec. _with,tokPrec. _endif,tokPrec.
            tokPrec.pbkw, tokPrec.pbkw, tokPrec.nil, tokPrec.pbkw, tokPrec.pekw, tokPrec.pekw, tokPrec.pekw, tokPrec.pbkw,
            //_void,tokPrec._extern,tokPrec._var,tokPrec._const,tokPrec._enum,tokPrec._struct/*_record*/,tokPrec._union,tokPrec._register,//
            tokPrec.pbkw, tokPrec.pbkw, tokPrec.pbkw, tokPrec.pbkw, tokPrec.pbkw, tokPrec.pbkw, tokPrec.pbkw, tokPrec.pbkw,
            //_unsigned,tokPrec._signed,tokPrec._char,tokPrec._short,tokPrec._int,tokPrec._long,tokPrec._sint64,tokPrec._uint64,//
            tokPrec.pbkw, tokPrec.pbkw, tokPrec.nil, tokPrec.pbkw, tokPrec.pbkw, tokPrec.nil, tokPrec.pbkw, tokPrec.nil,
            //_float,tokPrec._double,tokPrec._label,tokPrec._auto,tokPrec._static,tokPrec._volatile,tokPrec._typedef,tokPrec._sizeof,//
            tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil,
            //_real,tokPrec._array,tokPrec._set,tokPrec._file,tokPrec._object,tokPrec. _string,tokPrec. _goto,tokPrec.
            tokPrec.nil, tokPrec.nil, tokPrec.nil,
            //_program,tokPrec._function,tokPrec._procedure /*task V*/,tokPrec.
            tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil,
            //_macromodule,tokPrec._primitive,tokPrec._specify,tokPrec._table,tokPrec. //Verilog
            tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil,
            //_generate,tokPrec._config,tokPrec._liblist,tokPrec._library,tokPrec.  //Verilog
            tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil,
            //_incdir,tokPrec._include,tokPrec._design,tokPrec._defaultS,tokPrec._instance,tokPrec._cell,tokPrec._use,tokPrec. //Verilog
            tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil,
            //_automatic,tokPrec._endmodule,tokPrec._endfunction,tokPrec._endtask,tokPrec.   //Verilog
            tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil,
            //_endprimitive,tokPrec._endspecify,tokPrec._endtable,tokPrec._endgenerate,tokPrec._endconfig,tokPrec.  //Verilog
            tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil,
            //_endcaseV,tokPrec._casex,tokPrec._casez,tokPrec._wait,tokPrec._forever,tokPrec._disable,tokPrec._ifnone,tokPrec. //Verilog
            tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil,
            //_pulsestyle_onevent,tokPrec._pulsestyle_ondetect,tokPrec._showcanceled,tokPrec._noshowcanceled,tokPrec. //Verilog
            tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil,
            //_vectored,tokPrec._scalared,tokPrec._small,tokPrec._medium,tokPrec._large,tokPrec. //Verilog
            tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil,
            //_genvar,tokPrec._parameter,tokPrec._localparam,tokPrec._defparam,tokPrec._specparam,tokPrec._PATHPULSE$,tokPrec.  //Verilog
            tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil,
            //_inlineF,tokPrec._forward,tokPrec._interrupt,tokPrec._exportF,tokPrec._extrn,tokPrec._asmb,tokPrec.
            tokPrec.nil, tokPrec.nil, tokPrec.nil,
            //_input,tokPrec._output,tokPrec._inout,tokPrec.  //Verilog|SQL+3
            tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil,
            //_objectP,tokPrec._constructor,tokPrec._desctructor,tokPrec._property,tokPrec._resP,tokPrec._abstract,tokPrec. //P++9
            tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil,
            //_class,tokPrec._public,tokPrec._private,tokPrec._protected,tokPrec._virtual,tokPrec._friend,tokPrec. //C++16
            tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil,
            //_new,tokPrec._delete,tokPrec._tryC,tokPrec._catch,tokPrec._throw/*raise*/,tokPrec. //C++20
            tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil,
            //_initial,tokPrec._always,tokPrec._assign,tokPrec._deassign,tokPrec._force,tokPrec._release,tokPrec. //Verilog+26
            tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil,
            //_reg,tokPrec._time,tokPrec._realtime,tokPrec._event,tokPrec._buf,tokPrec._not,tokPrec. //Verilog+32
            tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil,
            //_andG,tokPrec._orG,tokPrec._xorG,tokPrec._nandG,tokPrec._norG,tokPrec._xnorG,tokPrec.  //Verilog+38
            tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil,
            //_tran,tokPrec._tranif0,tokPrec._tranif1,tokPrec._rtran,tokPrec._rtranif0,tokPrec._rtranif1,tokPrec. //Verilog+44
            tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil,
            //_tri,tokPrec._trior,tokPrec._triand,tokPrec._trireg,tokPrec._tri0,tokPrec._tri1,//Verilog+50
            tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil,
            //_wire,tokPrec._wand,tokPrec._wor,tokPrec._wres,tokPrec.			//Verilog+54
            tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil,
            //_supply0,tokPrec._supply1,tokPrec._highz0,tokPrec._highz1,tokPrec. //Verilog+58
            tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil,
            //_strong0,tokPrec._strong1,tokPrec._pull0,tokPrec._pull1,tokPrec._weak0,tokPrec._weak1,tokPrec.  //Verilog+64
            tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil,
            //_pulldown,tokPrec._pullup,tokPrec._bufif0,tokPrec._bufif1,tokPrec._notif0,tokPrec._notif1,tokPrec.  //Verilog+70
            tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil,
            //_cmos,tokPrec._rcmos,tokPrec._nmos,tokPrec._pmos,tokPrec._rnmos,tokPrec._rpmos,tokPrec.  //Verilog+76
            tokPrec.popf, tokPrec.pclf,//_fork,tokPrec. _join,tokPrec.	// відкриті і закриті дужки паралельних операторів 2
            tokPrec.popf, tokPrec.pclf,//_opbr,tokPrec. _ocbr,tokPrec.	// відкриті і закриті дужки операторів 2
            tokPrec.nil, tokPrec.nil,//_ctbr,tokPrec.	_fcbr,tokPrec.	// відкриті і закриті дужки конкатенацій 3
            tokPrec.pops, tokPrec.pcls,//_ixbr,tokPrec. _scbr,tokPrec.	// відкриті і закриті дужки індексу 4
            tokPrec.popb, tokPrec.pclb,//_brkt,tokPrec. _bckt,tokPrec.	// відкриті і закриті дужки порядку і функцій 5
            tokPrec.popd, tokPrec.pcld,//_tdbr,tokPrec. _tcbr,tokPrec.	// відкриті і закриті дужки даних 6
            tokPrec.peos, tokPrec.peos,//_eosP,tokPrec. eosS,tokPrec.	// паралельні та послідовні
            tokPrec.peos, tokPrec.pdol, tokPrec.psmc, tokPrec.pcnd,//_EOS=begOprtr,tokPrec. _comma,tokPrec. _cln,tokPrec. _qmrk,// ; ,tokPrec. : ?
            tokPrec.pass, tokPrec.pass, tokPrec.pass, tokPrec.pass,//_asOr,tokPrec. _asAnd,tokPrec. _asXor,tokPrec. _asAdd,tokPrec.		//|= =& =^ =+
            tokPrec.pass, tokPrec.pass, tokPrec.pass, tokPrec.pass,//_asSub,tokPrec. _asMul,tokPrec. _asDiv,tokPrec. _asMod,tokPrec.	// -= *= /= %=
            tokPrec.pass, tokPrec.pass, tokPrec.pass, tokPrec.puno, tokPrec.puno,//_asShr,tokPrec._asShl,tokPrec. _ass,tokPrec. _dcr,tokPrec. _inr,tokPrec. 	// <<= >>= = -- ++
            tokPrec.puno, tokPrec.puno, tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil,//_dcrN,tokPrec._inrN,tokPrec._mcrs,tokPrec._dbcln,tokPrec._eoCm,tokPrec._EOF,tokPrec. //-- ++ //  #  ::  */
            tokPrec.prel, tokPrec.prel, tokPrec.pequ, tokPrec.pequ, tokPrec.prel, tokPrec.prel,//_lt,tokPrec._le,tokPrec. _eq,tokPrec. _ne,tokPrec. _ge,tokPrec._gt,tokPrec.		// < <= == != >= >
            tokPrec.padd, tokPrec.padd, tokPrec.pmul, tokPrec.pmul, tokPrec.pacf, tokPrec.pacf,//_add,tokPrec. _sub,tokPrec. _mul,tokPrec. _div,tokPrec. _fldDt,tokPrec. _fldPt,// + - * / . ->
            tokPrec.ppwr, tokPrec.pshf, tokPrec.pshf, tokPrec.pequ, tokPrec.pequ,//_pwr,tokPrec. _shLfa,tokPrec. _shRga,tokPrec. _eqB,tokPrec. _neB,tokPrec.	// ** <<< >>> === !==
            tokPrec.puno, tokPrec.puno, tokPrec.puno, tokPrec.puno,//_addU,tokPrec._subU,tokPrec._mulU,tokPrec. _andU,tokPrec.		// + - * & унарні
            tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil,//_lmts,tokPrec._eqar,tokPrec._astar,tokPrec._trasand,tokPrec.	// PV+4 ..  => *> &&&
            tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil,//_orR,tokPrec._andR,tokPrec._xorR,tokPrec._norR,tokPrec._nandR,tokPrec._nxorR,tokPrec._xornR,tokPrec. //V+11 & | ^ ~| ~& ~^
            tokPrec.nil, tokPrec.nil, tokPrec.nil,//_delay,tokPrec._eventV,tokPrec._events,tokPrec.		 //V+14 # @ @*
            tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.puno,//_norB,tokPrec._nandB,tokPrec._nxorB,tokPrec._xornB,tokPrec._addr,tokPrec. //~| ~& ~^ ^~  _ptr,tokPrec.
            tokPrec.peos, tokPrec.peos,//_rem,tokPrec._remL,tokPrec. //
            tokPrec.pmul, tokPrec.porw, tokPrec.panw, tokPrec.pxrw,//_mod,tokPrec. _orB,tokPrec. _andB,tokPrec. _xorB,tokPrec.		// %(mod) |(or) &(and) ^(xor)
            tokPrec.pshf, tokPrec.pshf, tokPrec.porl, tokPrec.panl,//_shLft,tokPrec._shRgt,tokPrec. _or,tokPrec. _and,tokPrec.	//<<(shl) >>(shr) ||(or) &&(and)
            tokPrec.puno, tokPrec.puno, tokPrec.nil, tokPrec.nil,//_xmrk,tokPrec._invB,tokPrec._divI,tokPrec._in,tokPrec. //_not,tokPrec. _notB,tokPrec. /(div)
            tokPrec.nil, tokPrec.nil, tokPrec.nil,//_posedge,tokPrec._negedge,tokPrec._orE  //Verilog+3
            tokPrec.popf,//_fork,tokPrec. _join,tokPrec.	// замкнені дужки паралельних операторів 2
            tokPrec.popf,//_opbr,tokPrec. _ocbr,tokPrec.	// замкнені дужки операторів 2
            tokPrec.nil,//_ctbr,tokPrec.	_fcbr,tokPrec.	// замкнені дужки конкатенацій 3
            tokPrec.pops,//_ixbr,tokPrec. _scbr,tokPrec.	// замкнені дужки індексу 4
            tokPrec.popb,//_brkt,tokPrec. _bckt,tokPrec.	// замкнені дужки порядку і функцій 5
            tokPrec.popd//_tdbr,tokPrec. _tcbr,tokPrec.	// замкнені дужки даних 6
            // _pnil
    };

    tokPrec[] opPrG = {
            tokPrec.nil, tokPrec.ptrm,//_nil,tokPrec. _nam,tokPrec.	//0 зовнішнє подання
            tokPrec.ptrm, tokPrec.ptrm,//_srcn,tokPrec.	_cnst,tokPrec.	//2 вхідне і внутрішнє кодування константи
            tokPrec.pskw, tokPrec.peos, tokPrec.peos, tokPrec.nil,//_if,tokPrec._then,tokPrec._else,tokPrec._elseif,tokPrec.	//4 if then else elseif
            tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil,//_case,tokPrec. _switch,tokPrec. _default,tokPrec. _endcase,//8 case switch defualt endcase
            tokPrec.pbkw, tokPrec.pskw, tokPrec.pskw, tokPrec.nil,//_break,tokPrec. _return,tokPrec. _whileP,tokPrec. _whileN,tokPrec. //12 break return while do
            tokPrec.pbkw, tokPrec.prep, tokPrec.nil, tokPrec.nil,//_continue,tokPrec.peos _repeat,tokPrec. _untilN,tokPrec. _endloop,tokPrec. //16 continue repeat until
            tokPrec.pskw, tokPrec.peos, tokPrec.nil, tokPrec.nil,//pclf_for,tokPrec. _to,tokPrec. _downto,tokPrec. _step,// for to downto step
            tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil,//_untilP,tokPrec. _loop,tokPrec. _with,tokPrec. _endif,tokPrec.
            tokPrec.nil, tokPrec.pbkw, tokPrec.nil, tokPrec.pbkw, tokPrec.pekw, tokPrec.pekw, tokPrec.pekw, tokPrec.nil,
            //_goto,tokPrec._extern,tokPrec._var,tokPrec._const,tokPrec._enum,tokPrec._struct/*_record*/,tokPrec._union,tokPrec._register,//
            tokPrec.pbkw, tokPrec.pbkw, tokPrec.pbkw, tokPrec.pbkw, tokPrec.pbkw, tokPrec.pbkw, tokPrec.pbkw, tokPrec.pbkw,
            //_unsigned,tokPrec._signed,tokPrec._char,tokPrec._short,tokPrec._int,tokPrec._long,tokPrec._sint64,tokPrec._uint64,//
            tokPrec.pbkw, tokPrec.pbkw, tokPrec.pbkw, tokPrec.pbkw, tokPrec.pbkw, tokPrec.nil, tokPrec.nil, tokPrec.nil,
            //_float,tokPrec._double,tokPrec._void,tokPrec._auto,tokPrec._static,tokPrec._volatile,tokPrec._typedef,tokPrec._sizeof,//
            tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil,
            //_real,tokPrec._array,tokPrec._set,tokPrec._file,tokPrec._object,tokPrec. _string,tokPrec. _label,tokPrec.
            tokPrec.nil, tokPrec.nil, tokPrec.nil,
            //_program,tokPrec._function,tokPrec._procedure /*task V*/,tokPrec.
            tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil,
            //_macromodule,tokPrec._primitive,tokPrec._specify,tokPrec._table,tokPrec. //Verilog
            tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil,
            //_generate,tokPrec._config,tokPrec._liblist,tokPrec._library,tokPrec.  //Verilog
            tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil,
            //_incdir,tokPrec._include,tokPrec._design,tokPrec._defaultS,tokPrec._instance,tokPrec._cell,tokPrec._use,tokPrec. //Verilog
            tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil,
            //_automatic,tokPrec._endmodule,tokPrec._endfunction,tokPrec._endtask,tokPrec.   //Verilog
            tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil,
            //_endprimitive,tokPrec._endspecify,tokPrec._endtable,tokPrec._endgenerate,tokPrec._endconfig,tokPrec.  //Verilog
            tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil,
            //_endcaseV,tokPrec._casex,tokPrec._casez,tokPrec._wait,tokPrec._forever,tokPrec._disable,tokPrec._ifnone,tokPrec. //Verilog
            tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil,
            //_pulsestyle_onevent,tokPrec._pulsestyle_ondetect,tokPrec._showcanceled,tokPrec._noshowcanceled,tokPrec. //Verilog
            tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil,
            //_vectored,tokPrec._scalared,tokPrec._small,tokPrec._medium,tokPrec._large,tokPrec. //Verilog
            tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil,
            //_genvar,tokPrec._parameter,tokPrec._localparam,tokPrec._defparam,tokPrec._specparam,tokPrec._PATHPULSE$,tokPrec.  //Verilog
            tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil,
            //_inlineF,tokPrec._forward,tokPrec._interrupt,tokPrec._exportF,tokPrec._extrn,tokPrec._asmb,tokPrec.
            tokPrec.nil, tokPrec.nil, tokPrec.nil,
            //_input,tokPrec._output,tokPrec._inout,tokPrec.  //Verilog|SQL+3
            tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil,
            //_objectP,tokPrec._constructor,tokPrec._desctructor,tokPrec._property,tokPrec._resP,tokPrec._abstract,tokPrec. //P++9
            tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil,
            //_class,tokPrec._public,tokPrec._private,tokPrec._protected,tokPrec._virtual,tokPrec._friend,tokPrec. //C++16
            tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil,
            //_new,tokPrec._delete,tokPrec._tryC,tokPrec._catch,tokPrec._throw/*raise*/,tokPrec. //C++20
            tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil,
            //_initial,tokPrec._always,tokPrec._assign,tokPrec._deassign,tokPrec._force,tokPrec._release,tokPrec. //Verilog+26
            tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil,
            //_reg,tokPrec._time,tokPrec._realtime,tokPrec._event,tokPrec._buf,tokPrec._not,tokPrec. //Verilog+32
            tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil,
            //_andG,tokPrec._orG,tokPrec._xorG,tokPrec._nandG,tokPrec._norG,tokPrec._xnorG,tokPrec.  //Verilog+38
            tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil,
            //_tran,tokPrec._tranif0,tokPrec._tranif1,tokPrec._rtran,tokPrec._rtranif0,tokPrec._rtranif1,tokPrec. //Verilog+44
            tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil,
            //_tri,tokPrec._trior,tokPrec._triand,tokPrec._trireg,tokPrec._tri0,tokPrec._tri1,//Verilog+50
            tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil,
            //_wire,tokPrec._wand,tokPrec._wor,tokPrec._wres,tokPrec.			//Verilog+54
            tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil,
            //_supply0,tokPrec._supply1,tokPrec._highz0,tokPrec._highz1,tokPrec. //Verilog+58
            tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil,
            //_strong0,tokPrec._strong1,tokPrec._pull0,tokPrec._pull1,tokPrec._weak0,tokPrec._weak1,tokPrec.  //Verilog+64
            tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil,
            //_pulldown,tokPrec._pullup,tokPrec._bufif0,tokPrec._bufif1,tokPrec._notif0,tokPrec._notif1,tokPrec.  //Verilog+70
            tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil,
            //_cmos,tokPrec._rcmos,tokPrec._nmos,tokPrec._pmos,tokPrec._rnmos,tokPrec._rpmos,tokPrec.  //Verilog+76
            tokPrec.pclf, tokPrec.pclf,//_fork,tokPrec. _join,tokPrec.	// відкриті і закриті дужки паралельних операторів 2
            tokPrec.pclf, tokPrec.pclf,//_opbr,tokPrec. _ocbr,tokPrec.	// відкриті і закриті дужки операторів 2
            tokPrec.nil, tokPrec.nil,//_ctbr,tokPrec.	_fcbr,tokPrec.	// відкриті і закриті дужки конкатенацій 3
            tokPrec.pcls, tokPrec.pcls,//_ixbr,tokPrec. _scbr,tokPrec.	// відкриті і закриті дужки індексу 4
            tokPrec.pclb, tokPrec.pclb,//_brkt,tokPrec. _bckt,tokPrec.	// відкриті і закриті дужки порядку і функцій 5
            tokPrec.pcld, tokPrec.pcld,//_tdbr,tokPrec. _tcbr,tokPrec.	// відкриті і закриті дужки даних 6
            tokPrec.peos, tokPrec.peos,//_eosP,tokPrec. eosS,tokPrec.	// паралельні та послідовні
            tokPrec.peos, tokPrec.pdol, tokPrec.psmc, tokPrec.pcnd,//_EOS=begOprtr,tokPrec. _comma,tokPrec. _cln,tokPrec. _qmrk,// ; ,tokPrec. : ?
            tokPrec.pass, tokPrec.pass, tokPrec.pass, tokPrec.pass,//_asOr,tokPrec. _asAnd,tokPrec. _asXor,tokPrec. _asAdd,tokPrec.		//|= =& =^ =+
            tokPrec.pass, tokPrec.pass, tokPrec.pass, tokPrec.pass,//_asSub,tokPrec. _asMul,tokPrec. _asDiv,tokPrec. _asMod,tokPrec.	// -= *= /= %=
            tokPrec.pass, tokPrec.pass, tokPrec.pass, tokPrec.puno, tokPrec.puno,//_asShr,tokPrec._asShl,tokPrec. _ass,tokPrec. _dcr,tokPrec. _inr,tokPrec. 	// <<= >>= = -- ++
            tokPrec.puno, tokPrec.puno, tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil,//_dcrN,tokPrec._inrN,tokPrec._mcrs,tokPrec._dbcln,tokPrec._eoCm,tokPrec._EOF,tokPrec. //-- ++ //  #  ::  */
            tokPrec.prel, tokPrec.prel, tokPrec.pequ, tokPrec.pequ, tokPrec.prel, tokPrec.prel,//_lt,tokPrec._le,tokPrec. _eq,tokPrec. _ne,tokPrec. _ge,tokPrec._gt,tokPrec.		// < <= == != >= >
            tokPrec.padd, tokPrec.padd, tokPrec.pmul, tokPrec.pmul, tokPrec.pacf, tokPrec.pacf,//_add,tokPrec. _sub,tokPrec. _mul,tokPrec. _div,tokPrec. _fldDt,tokPrec. _fldPt,// + - * / . ->
            tokPrec.ppwr, tokPrec.pshf, tokPrec.pshf, tokPrec.pequ, tokPrec.pequ,//_pwr,tokPrec. _shLfa,tokPrec. _shRga,tokPrec. _eqB,tokPrec. _neB,tokPrec.	// ** <<< >>> === !==
            tokPrec.puno, tokPrec.puno, tokPrec.puno, tokPrec.puno,//_addU,tokPrec._subU,tokPrec._mulU,tokPrec. _andU,tokPrec.		// + - * & унарні
            tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil,//_lmts,tokPrec._eqar,tokPrec._astar,tokPrec._trasand,tokPrec.	// PV+4 ..  => *> &&&
            tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil,//_orR,tokPrec._andR,tokPrec._xorR,tokPrec._norR,tokPrec._nandR,tokPrec._nxorR,tokPrec._xornR,tokPrec. //V+11 & | ^ ~| ~& ~^
            tokPrec.nil, tokPrec.nil, tokPrec.nil,//_delay,tokPrec._eventV,tokPrec._events,tokPrec.		 //V+14 # @ @*
            tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.nil, tokPrec.puno,//_norB,tokPrec._nandB,tokPrec._nxorB,tokPrec._xornB,tokPrec._addr,tokPrec. //~| ~& ~^ ^~  _ptr,tokPrec.
            tokPrec.peos, tokPrec.peos,//_rem,tokPrec._remL,tokPrec. //
            tokPrec.pmul, tokPrec.porw, tokPrec.panw, tokPrec.pxrw,//_mod,tokPrec. _orB,tokPrec. _andB,tokPrec. _xorB,tokPrec.		// %(mod) |(or) &(and) ^(xor)
            tokPrec.pshf, tokPrec.pshf, tokPrec.porl, tokPrec.panl,//_shLft,tokPrec._shRgt,tokPrec. _or,tokPrec. _and,tokPrec.	//<<(shl) >>(shr) ||(or) &&(and)
            tokPrec.puno, tokPrec.nil, tokPrec.nil, tokPrec.nil,//_xmrk,tokPrec._invB,tokPrec._divI,tokPrec._in,tokPrec. //_not,tokPrec. _notB,tokPrec. /(div)
            tokPrec.nil, tokPrec.nil, tokPrec.nil,//_posedge,tokPrec._negedge,tokPrec._orE  //Verilog+3
            tokPrec.popf,//_fork,tokPrec. _join,tokPrec.	// замкнені дужки паралельних операторів 2
            tokPrec.popf,//_opbr,tokPrec. _ocbr,tokPrec.	// замкнені дужки операторів 2
            tokPrec.nil,//_ctbr,tokPrec.	_fcbr,tokPrec.	// замкнені дужки конкатенацій 3
            tokPrec.pops,//_ixbr,tokPrec. _scbr,tokPrec.	// замкнені дужки індексу 4
            tokPrec.popb,//_brkt,tokPrec. _bckt,tokPrec.	// замкнені дужки порядку і функцій 5
            tokPrec.popd//_tdbr,tokPrec. _tcbr,tokPrec.	// замкнені дужки даних 6
            /* pclf,//_fork,tokPrec. _join,tokPrec.	// замкнені дужки паралельних операторів 2
            pclf,//_opbr,tokPrec. _ocbr,tokPrec.	// замкнені дужки операторів 2
            nil,//_ctbr,tokPrec.	_fcbr,tokPrec.	// замкнені дужки конкатенацій 3
            pcls,//_ixbr,tokPrec. _scbr,tokPrec.	// замкнені дужки індексу 4
            pclb,//_brkt,tokPrec. _bckt,tokPrec.	// замкнені дужки порядку і функцій 5
            pcld//_tdbr,tokPrec. _tcbr,tokPrec.	// замкнені дужки даних 6*/
            // _pnil
    };

    public void makeTree(lxNode[] nd,int start) {
        int nr = 0;
        int nc = 1;
        nd[0].prnNd = -1;
        do {
            nr = nxtProd(nd, nr, nc, start);
        }
        while (nd[++nc] != null);
    }

    public int nxtProd(lxNode[] nd,    // вказівник на початок масиву вузлів
                       int nR,   // номер кореневого вузла
                       int nC,
                       int start)   // номер поточного вузла
    {
        int n = nC - 1;     // номер попереднього вузла
        tokPrec pC = opPrF[nd[nC].ndOp.ordinal()];// передування поточного вузла
        tokPrec[] opPr = opPrG;//F;// nd[nC].prvNd = nd+n;
        while (n != -1) // цикл просування від попереднього вузла до кореню
        {
            if (opPr[nd[n].ndOp.ordinal()].ordinal() < pC.ordinal()//)// порівняння функцій передувань
                    && nd[n].ndOp.ordinal() </*_ctbz*/tokType._frkz.ordinal()) {
                if (n != nC - 1 && nd[n].pstNd != null)        // перевірка необхідності вставки
                {
                    nd[nC].prvNd = nd[n].pstNd;   // підготовка зв’язків
                    nd[nC].prvNd.prnNd =/*nd+*/nC+start;
                }   // для вставки вузла
                if (opPrF[(int) nd[n].ndOp.ordinal()] == tokPrec.pskw && nd[n].prvNd == null)
                    nd[n].prvNd = nd[nC];
                else
                    nd[n].pstNd = nd[nC];
                nd[nC].prnNd =/*nd+*/n+start; // додавання піддерева
                return nR;
            }
            if (opPrG[(int) nd[n].ndOp.ordinal()] == pC && (nd[n].ndOp == tokType._brkt || nd[n].ndOp == tokType._ixbr || nd[n].ndOp == tokType._opbr || nd[n].ndOp == tokType._tdbr)) {
                nd[n].ndOp = tokType.values()[((nd[n].ndOp.ordinal() - tokType._fork.ordinal()) / 2 + tokType._frkz.ordinal())];//09.04.07	  //замена на
                nd[nC] = nd[n];
                if (nd[nC].prnNd == -1) {
                    nR = nC;
                    nd[nR].prnNd = -1;
                } else if (opPrF[nd[nd[nC].prnNd-start].ndOp.ordinal()] == tokPrec.pskw && nd[nC].ndOp.ordinal() < tokType._frkz.ordinal())
                    nd[nd[nC].prnNd-start].prvNd = nd[nC];
                else if (opPrF[nd[nd[nC].prnNd-start].ndOp.ordinal()] == tokPrec.pekw && nd[nC].ndOp == tokType._opbz) {
                    nd[nd[nC].prnNd-start].prvNd = nd[nC];
                    nd[nd[nC].prnNd-start].pstNd = null;
                }
                return nR;
            }
                /* if(nd[n].ndOp==_brkt||nd[n].ndOp==_ixbr||nd[n].ndOp==_opbr||nd[n].ndOp==_tdbr)
                    {nd[nC].prnNd=n; nd[nC].prvNd=nd[n].pstNd;
                    nd[n].pstNd->prnNd=nC; nd[n].pstNd= nd+nC;
                    return nR;}*/
            if (nd[n].prnNd!=-1){
                n = nd[n].prnNd-start;
            }
            else
            {
                n = nd[n].prnNd;
            }
            opPr = opPrG; // просування до кореню
        }
        //  if(n<=)	else
        nd[nC].prvNd = nd[nR];
        nd[nR].prnNd =/*nd+*/nC+start;
        nR = nC;
        nd[nR].prnNd = -1;
        return nR;
    }

    boolean flag;
    lexData thread;

    //Содать терминальный узел (в дереве это узел без наследников)
    synNode trace_Terminal(lxNode tok) {
        synNode temp = new synNode(synNode.synType._terminal);
        temp.termin = tok;
        return temp;
    }

    ;

    //=#=#=#=#=#=#=#=# Обработчики нетерминалов (правил грамматики) =#=#=#=#
    // Принцип рекурсивного спуска в том, что если у нас есть грамматика в виде,
    // к примеру, Бекуса-Наура, то мы пишем код в строгом соответствии с правилами:
    // на каждое правило грамматики по обработчику.

    //Обработчик правила bool_expression      ::= expression
    //                        | expression "=" expression
    //                        | expression "<>" expression
    //                        | expression "<=" expression
    //                        | expression ">=" expression
    //                        | expression "<" expression
    //                        | expression ">" expression
    public synNode trace_Bool_Expression() {
        synNode temp = new synNode(synNode.synType._bool_expression);
        temp.addChild(trace_Expression());
        synNode t;
        switch (thread.lookNext()) {
            case _ne:
                flag = true;
                temp.addChild(trace_Terminal(thread.getNext()));
                temp.addChild(trace_Expression());
                break;
            case _le:
                flag = true;
                temp.addChild(trace_Terminal(thread.getNext()));
                temp.addChild(trace_Expression());
                break;
            case _ge:
                flag = true;
                temp.addChild(trace_Terminal(thread.getNext()));
                temp.addChild(trace_Expression());
                break;
            case _lt:
                flag = true;
                temp.addChild(trace_Terminal(thread.getNext()));
                temp.addChild(trace_Expression());
                break;
            case _gt:
                flag = true;
                temp.addChild(trace_Terminal(thread.getNext()));
                temp.addChild(trace_Expression());
                break;
            case _eq:
                flag = true;
                temp.addChild(trace_Terminal(thread.getNext()));
                temp.addChild(trace_Expression());
                break;
            default:
                if (!flag) {
                    thread.matchNext(tokType._eq);
                }
                break;
        }
        return temp;
    }

    //Обработчик правила unsigned_factor      ::= "(" expression ")"
    //                        | NUMBER
    //                        | IDENTIFIER
    public synNode trace_Unsigned_Factor() {
        synNode temp = new synNode(synNode.synType._unsigned_factor);
        switch (thread.lookNext()) {
            case _brkt:
                thread.matchNext(tokType._brkt);
                temp.addChild(trace_Bool_Expression());
                thread.matchNext(tokType._bckt);
                break;
            case _srcn:
                temp.addChild(trace_Terminal(thread.getNext()));
                break;
            case _nam:
                temp.addChild(trace_Terminal(thread.getNext()));
                break;

        }
        return temp;
    }

    //Обработчик правила signed_factor        ::= unsigned_factor
    //                        | "-" unsigned_factor
    public synNode trace_Signed_Factor() {
        synNode temp = new synNode(synNode.synType._signed_factor);
        if (thread.lookNext() == tokType._sub) {
            temp.addChild(trace_Terminal(thread.getNext()));
        }
        temp.addChild(trace_Unsigned_Factor());
        return temp;
    }

    //Обработка терминалов NUMBER ::= [0-9]+
    //					   IDENTIFIER ::= [a-zA-Z_][a-zA-Z0-9_]*
    public synNode trace_Term() {
        synNode temp = new synNode(synNode.synType._term);
        temp.addChild(trace_Signed_Factor());
        switch (thread.lookNext()) {
            case _mul:
                temp.addChild(trace_Terminal(thread.getNext()));
                temp.addChild(trace_Expression());
                break;
            case _div:
                temp.addChild(trace_Terminal(thread.getNext()));
                temp.addChild(trace_Expression());
                break;
        }
        return temp;
    }

    //Обработчик правила expression           ::= term
    //                        | term "+" expression
    //                        | term "-" expression
    public synNode trace_Expression() {
        synNode temp = new synNode(synNode.synType._expression);
        if (thread.lookNext() == tokType._nam && thread.lookNext2() == tokType._brkt) {
            temp.addChild(trace_Method());
        } else {
            temp.addChild(trace_Term());
        }
        switch (thread.lookNext()) {
            case _add:
                temp.addChild(trace_Terminal(thread.getNext()));
                temp.addChild(trace_Expression());
                break;
            case _sub:
                temp.addChild(trace_Terminal(thread.getNext()));
                temp.addChild(trace_Expression());
                break;
        }
        return temp;
    }

    //Обработчик правила for ::= "for" "(" statement ";" boolean_expression ";" statement ";" ")" "{" block "}"
    public synNode trace_For() {
        thread.matchNext(tokType._for);
        synNode temp = new synNode(synNode.synType._for_node);
        thread.matchNext(tokType._brkt);
        temp.addChild(trace_Statement());
        temp.addChild(trace_Bool_Expression());
        thread.matchNext(tokType._EOS);
        temp.addChild(trace_Statement_Body());
        thread.matchNext(tokType._bckt);
        temp.addChild(trace_Block(false));
        return temp;
    }

    //Обработчик правила while ::= "while" "(" boolean_expression ")" "{" block "}"
    public synNode trace_While() {
        thread.matchNext(tokType._whileP);
        synNode temp = new synNode(synNode.synType._do_node);
        thread.matchNext(tokType._brkt);
        temp.addChild(trace_Bool_Expression());
        thread.matchNext(tokType._bckt);
        temp.addChild(trace_Block(false));
        return temp;
    }

    //Обработчик правила do ::= "do" "{" block "}" while "(" boolean_expression ")"
    public synNode trace_Do() {
        thread.matchNext(tokType._repeat);
        synNode temp = new synNode(synNode.synType._do_node);
        temp.addChild(trace_Block(false));
        thread.matchNext(tokType._whileP);
        thread.matchNext(tokType._brkt);
        temp.addChild(trace_Bool_Expression());
        thread.matchNext(tokType._bckt);
        return temp;
    }


    //Обработчик правила assignment ::= IDENTIFIER "=" bool_expression ";"
    public synNode trace_Assignment() {
        thread.matchNext(tokType._nam);
        synNode temp = new synNode(synNode.synType._assignment);
        //temp.addChild(trace_Terminal(thread.getNext()));
        thread.matchNext(tokType._ass);
        //thread.matchNext(tokType._nam);
        //temp.addChild(trace_Terminal(thread.getNext()));
        temp.addChild(trace_Expression());
        return temp;
    }

    //Обработчик правила statement_body ::= empty | assignment | method
    public synNode trace_Statement_Body() {
        synNode temp = new synNode(synNode.synType._statement_body);
        boolean flag = false;
        if (thread.lookNext() == tokType._int && thread.lookNext2() == tokType._nam) {
            thread.matchNext(tokType._int);
            flag = true;
        }
        if (thread.lookNext() == tokType._EOS&&!flag) {
            return temp;}
        if (thread.lookNext() == tokType._nam&&thread.lookNext2() == tokType._EOS) {
            thread.matchNext(tokType._nam);
            return temp;
        } else {
            if (((thread.pos + 1) < (thread.pointer.length - 1)) && thread.lookNext2() == tokType._opbr) {
                temp.addChild(trace_Method());
            } else {
                temp.addChild(trace_Assignment());
            }
        }
        return temp;
    }

    //Обработчик правила method ::= IDENTIFIER "(" method | IDENTIFIER | empty
    //                              | "," method
    //                              | ")"
    public synNode trace_Method() {
        synNode temp = new synNode(synNode.synType._method_node);
        thread.matchNext(tokType._nam);
        //temp.addChild(trace_Terminal(thread.getNext()));
        thread.matchNext(tokType._brkt);
        while (thread.lookNext() != tokType._bckt) {
            if (((thread.pos + 1) < (thread.pointer.length - 1)) && thread.lookNext2() == tokType._opbr) {
                temp.addChild(trace_Method());
            } else {
                thread.matchNext(tokType._nam);
            }
            if (thread.lookNext() == tokType._bckt) {
                break;
            }
            thread.matchNext(tokType._comma);
        }
        thread.matchNext(tokType._bckt);
        return temp;
    }

    //Обработчик правила statement ::= statement_body ";"
    public synNode trace_Statement() {
        synNode temp = new synNode(synNode.synType._statement);
        temp.addChild(trace_Statement_Body());
        thread.matchNext(tokType._EOS);
        return temp;
    }

    //Обработчик правила compound_statement ::= for| while | do | statement
    public synNode trace_Compound() {
        synNode temp = new synNode(synNode.synType._compound_statement);
        switch (thread.lookNext()) {
            case _for:
                temp = trace_For();
                break;
            case _whileP:
                temp = trace_While();
                break;
            case _repeat:
                temp = trace_Do();
                break;
            default:
                temp = trace_Statement();
                break;
        }
        return temp;
    }

    //Обработчик правила block ::= "{" compound_statement "end" | "begin" "}"
    public synNode trace_Block(boolean type) {
        thread.matchNext(tokType._opbr); //opbr = begin
        synNode temp = new synNode(synNode.synType._block);
        if (thread.lookNext() == tokType._ocbr) {
            thread.matchNext(tokType._ocbr); //ocbr = end
        } else {
            while ((thread.lookNext() != tokType._ocbr) && (thread.lookNext() != tokType._return)) {
                temp.addChild(trace_Compound());
            }
            if (type) {
                thread.matchNext(tokType._return);
                if (thread.lookNext2() != tokType._EOS) {
                    thread.insTok(tokType._brkt);
                    thread.matchNext(tokType._brkt);
                    temp.addChild(trace_Expression());
                    thread.insTok(tokType._bckt);
                    thread.matchNext(tokType._bckt);
                }
                thread.matchNext(tokType._EOS);
            }
            thread.matchNext(tokType._ocbr); //ocbr = end
        }
        return temp;
    }

    public boolean trace_Type(boolean type) {
        switch (thread.lookNext()) {
            case _int:
                thread.matchNext(tokType._int);
                break;
            default:
                thread.matchNext(tokType._void);
                type = false;
                break;
        }
        return type;
    }

    boolean flag1 = false;

    //Обработчик правила funcions ::= модификатор_доступа static TYPE indentificator "(" type identificator
    public synNode trace_Function() {
        if (thread.lookNext() == tokType._public) {
            thread.matchNext(tokType._public);
            thread.matchNext(tokType._static);
            flag1 = true;
        }
        boolean type = true;
        type = trace_Type(type);
        thread.matchNext(tokType._nam); //opbr = begin
        synNode temp = new synNode(synNode.synType._function_node);
        thread.matchNext(tokType._brkt);
        if (flag1) {
            thread.matchNext(tokType._string);
            thread.matchNext(tokType._ixbr);
            thread.matchNext(tokType._scbr);
            thread.matchNext(tokType._nam);
            thread.matchNext(tokType._bckt);
        } else {
            while (thread.lookNext() != tokType._bckt) {
                if (thread.lookNext() == tokType._bool) {
                    thread.matchNext(tokType._bool);
                } else {
                    thread.matchNext(tokType._int);
                }
                thread.matchNext(tokType._nam);
                if (thread.lookNext() == tokType._bckt) {
                    break;
                }
                if (thread.lookNext() == tokType._comma) {
                    thread.pointer[thread.pos].ndOp = tokType._EOS;
                    thread.matchNext(tokType._EOS);
                } else {
                    thread.matchNext(tokType._comma);
                }
            }
            thread.matchNext(tokType._bckt);
        }
        temp.addChild(trace_Block(type));
        return temp;
    }

    //Обработчик правила program ::= "class" indentificator "{" functions "}"
    public synNode trace_Program() {
        thread.matchNext(tokType._class);
        synNode temp = new synNode(synNode.synType._class);
        thread.matchNext(tokType._nam);
        thread.matchNext(tokType._opbr); //opbr = begin
        while (thread.lookNext() != tokType._ocbr) {
            temp.addChild(trace_Function());
            thread.insTok(tokType._EOS);
            thread.matchNext(tokType._EOS);
        }
        thread.matchNext(tokType._ocbr); //ocbr = end
        return temp;
    }

    //Запуск синтаксического анализа
    public int synAnalysis(lxNode[] nodes) {
        thread = new lexData(nodes);
        trace_Program();
        int size = 0;
        while (thread.pointer[size].ndOp != tokType._nil) {
            size++;
        }
        size--;
        return size;
    }
}

class synNode {
    public enum synType {
        _class, _block, _compound_statement,
        _if_node, _if_without_else, _if_with_else, _for_node,
        _statement, _statement_body,
        _assignment,
        _bool_expression, _bool_factor,
        _expression, _term, _signed_factor, _unsigned_factor,
        _terminal, _while_node, _do_node, _method_node, _function_node
    }

    synType ndOp;                //код типа лексемы
    lxNode termin;            //терминальный символ данного узла
    synNode prvNd;                    //предшественник
    synNode[] child = new synNode[100];            //наследники
    private int currentSize;                //количество наследников

    // Строковые представления символов-результатов лексического разбора (используется при выводе ошибок)
    public static String[] tokTypeStr =
            {"_nil", "identificator",    //0 зовнішнє подання
                    "_srcn", "_cnst",    //2 вхідне і внутрішнє кодування константи
                    "if", "_then", "_else", "_elseif",    //4 if then else elseif
                    "_case", "_switch", "_default", "_endcase",//8 case switch defualt endcase
                    "_break", "_return", "_whileP", "_whileN", //12 break return while do
                    "_continue", "_repeat", "_untilN", "_endloop", //16 continue repeat until
                    "for", "to", "downto", "step",// for to downto step
                    "_untilP", "_loop", "_with", "_endif",
                    "_void", "_extern", "_var", "_const", "_enum", "_struct", "_union", "_register",//
                    "_unsigned", "_signed", "_char", "_short", "_int", "_long", "_sint64", "_uint64",//
                    "_float", "_double", "_label", "_auto", "_static", "_volatile", "_typedef", "_sizeof",//
                    "_real", "_array", "_set", "_file", "_object", "_string", "_goto",
                    "_program", "_function", "_procedure /*task V*/",
                    "_macromodule", "_primitive", "_specify", "_table", //Verilog
                    "_generate", "_config", "_liblist", "_library",  //Verilog
                    "_incdir", "_include", "_design", "_defaultS", "_instance", "_cell", "_use", //Verilog
                    "_automatic", "_endmodule", "_endfunction", "_endtask",   //Verilog
                    "_endprimitive", "_endspecify", "_endtable", "_endgenerate", "_endconfig",  //Verilog
                    "_endcaseV", "_casex", "_casez", "_wait", "_forever", "_disable", "_ifnone", //Verilog
                    "_pulsestyle", "_onevent", "_pulsestyle", "_ondetect", "_showcanceled", "_noshowcanceled", //Verilog
                    "_vectored", "_scalared", "_small", "_medium", "_large", //Verilog
                    "_genvar", "_parameter", "_localparam", "_defparam", "_specparam", "_PATHPULSE$",  //Verilog
                    "_inlineF", "_forward", "_interrupt", "_exportF", "_extrn", "_asmb",
                    "_input", "_output", "_inout",  //Verilog|SQL+3
                    "_objectP", "_constructor", "_desctructor", "_property", "_resP", "_abstract", //P++9
                    "_class", "_public", "_private", "_protected", "_virtual", "_friend", //C++16
                    "_new", "_delete", "_tryC", "_catch", "_throw/*raise*/", //C++20
                    "_initial", "_always", "_assign", "_deassign", "_force", "_release", //Verilog+26
                    "_reg", "_time", "_realtime", "_event", "_buf", "_not", //Verilog+32
                    "_andG", "_orG", "_xorG", "_nandG", "_norG", "_xnorG",  //Verilog+38
                    "_tran", "_tranif0", "_tranif1", "_rtran", "_rtranif0", "_rtranif1", //Verilog+44
                    "_tri", "_trior", "_triand", "_trireg", "_tri0", "_tri1",//Verilog+50
                    "_wire", "_wand", "_wor", "_wres",            //Verilog+54
                    "_supply0", "_supply1", "_highz0", "_highz1", //Verilog+58
                    "_strong0", "_strong1", "_pull0", "_pull1", "_weak0", "_weak1",  //Verilog+64
                    "_pulldown", "_pullup", "_bufif0", "_bufif1", "_notif0", "_notif1",  //Verilog+70
                    "_cmos", "_rcmos", "_nmos", "_pmos", "_rnmos", "_rpmos",  //Verilog+76
                    "_fork",    // відкриті і закриті дужки паралельних операторів 2
                    "begin", "end",    // відкриті і закриті дужки операторів 2
                    "_ctbr", "_fcbr",    // відкриті і закриті дужки конкатенацій 3
                    "[", "]",    // відкриті і закриті дужки індексу 4
                    "(", ")",    // відкриті і закриті дужки порядку і функцій 5
                    "{", "}",    // відкриті і закриті дужки даних 6
                    "_eosP", "eosS",    // паралельні та послідовні
                    ";", ",", "_cln", "_qmrk",// ; ", : ?
                    "_asOr", "_asAnd", "_asXor", "_asAdd",        //|= =& =^ =+
                    "_asSub", "_asMul", "_asDiv", "_asMod",    // -= *= /= %=
                    "_asShr", "_asShl", "_ass", "_dcr", "_inr",    // <<= >>= = -- ++
                    "_dcrN", "_inrN", "_mcrs", "_dbcln", "_eoCm", "_EOF", //-- ++ //  #  ::  */
                    "<", "<=", "=", "!=", ">=", ">",        // < <= == != >= >
                    "_add", "_sub", "_mul", "/", ".", "_fldPt",// + - * / . ->
                    "_pwr", "_shLfa", "_shRga", "_eqB", "_neB",    // ** <<< >>> === !==
                    "_addU", "_subU", "_refU", "_ptrU",        // + - * & унарні
                    "_lmts", "_eqar", "_astar", "_trasand",    // PV+4 ..  => *> &&&
                    "_orR", "_andR", "_xorR", "_norR", "_nandR", "_nxorR", "_xornR", //V+11 & | ^ ~| ~& ~^
                    "_delay", "_eventV", "_events",         //V+14 # @ @*
                    "_norB", "_nandB", "_nxorB", "_xornB", "_addr", //~| ~& ~^ ^~  "_ptr",
                    "_rem", "_remL", // ,
                    "_mod", "_orB", "_andB", "_xorB",        // %(mod) |(or) &(and) ^(xor)
                    "_shLft", "_shRgt", "_or", "_and",    //<<(shl) >>(shr) ||(or) &&(and)
                    "_xmrk", "_invB", "_divI", "_in", //"_not", "_notB", /(div)
                    "_posedge", "_negedge", "_orE",  //Verilog+3
                    "_frkz",    // відкриті і закриті дужки паралельних операторів 2
                    "_opbz",    // відкриті і закриті дужки операторів 2
                    "_ctbz",    // відкриті і закриті дужки конкатенацій 3
                    "_ixbz",    // відкриті і закриті дужки індексу 4
                    "_brkz",    // відкриті і закриті дужки порядку і функцій 5
                    "_tdbz"    // відкриті і закриті дужки даних 6
                    // "_pnil
            };

    //Все нетерминалы грамматики в строковом виде (нужно при выводе ошибок)
    private static String[] synTypeStr = {
            "_prgm", "_block", "_compound_statement",
            "_if_node", "_if_without_else", "_if_with_else", "_for",
            "_statement", "_statement_body",
            "_assignment",
            "_bool_expression", "_bool_factor",
            "_expression", "_term", "_signed_factor", "_unsigned_factor"
    };

    //Конструктор для пустого узла дерева разбора
    public synNode() {
        this.currentSize = 0;
    }

    //Конструктор для узла дерева разбора отдечающему за операцию с кодом code
    public synNode(synType code) {
        this.ndOp = code;
        this.currentSize = 0;
    }

    //Добавление к узлу "сыновей"
    void addChild(synNode child) {
        if (child != null) {
            this.child[currentSize] = child;
            currentSize++;
        }
    }

    //Вывод дерева в текстовом виде (рекурсивно вызывает метод от всех наследников)
    void toString(int space) {
        for (int i = 0; i < space; i++) {
            System.out.print(" ");
        }
        space += 3;
        if (this.ndOp == synType._terminal) {
            System.out.println(tokTypeStr[this.termin.ndOp.ordinal()]);
        } else {
            System.out.println(synTypeStr[this.ndOp.ordinal()]);
            for (int i = 0; i < this.currentSize; i++) {
                this.child[i].toString(space);
            }
        }
    }
}

//Адаптер на массив выдаваемый лексическим анализатором
//реализует методы:
//  lookNext() - помсмотреть на следующую лексему
//  matchNext(elem) - сравнить текущую лексему с elem, если они совпадают, то считать данную
//  лексему (вытолкнуть из стека), если не равны, то выдать ошибку.
class lexData {
    public lxNode[] pointer;
    public int pos;

    //Конструктор потока (pos - где мы сейчас находимся, pointer )
    public lexData(lxNode[] data) {
        pointer = data;
        pos = 0;
    }

    //"Подсмотреть" следующий символ (он остается в начале потока)
    public tokType lookNext() {
        return this.pointer[pos].ndOp;
    }

    //"Подсмотреть" символ за следующим(на 2 вперед)
    public tokType lookNext2() {
        return this.pointer[pos + 1].ndOp;
    }

    //Сравнить следующий символ потока с данным:
    // -- если совпадает, то вернуть true и извлечь из потока символ
    // -- если не совпадает, то выдать ошибку
    boolean matchNext(tokType elem) {
        boolean flag = true;
        if (this.pointer[pos].ndOp.ordinal() == elem.ordinal()) {
            pos++;
        } else {
            System.out.println("Error on " + pos + " lexem: \n have to be \"" + synNode.tokTypeStr[elem.ordinal() + 1] + "\" but we have \"" + synNode.tokTypeStr[this.pointer[pos].ndOp.ordinal() + 1] + "\"");
            flag = false;
            System.exit(0);
        }
        return flag;
    }

    public void insTok(tokType tok) {
        for (int i = pointer.length - 1; i > pos; i--) {
            pointer[i] = pointer[i - 1];
        }
        pointer[pos] = new lxNode(tok);
    }

    //Получить следующий символ из входного потока.
    public lxNode getNext() {
        pos++;
        return this.pointer[pos];
    }
}
