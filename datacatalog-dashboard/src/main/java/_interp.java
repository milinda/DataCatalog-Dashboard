import java.util.Formatter;

public class _interp {
//labels
static final int _apply$_k = 0;
static final int _value$_of = 1;
static final int _apply$_env = 2;
static final int _apply$_proc = 3;

//registers
Object _k, _v, _expr, _env, _num, _c, _a;

//program counter
int _pc;

//union types
static class _exp_const {
Object _v;

_exp_const(Object _v) {
this._v = _v;
}
}

static class _exp_var {
Object _v;

_exp_var(Object _v) {
this._v = _v;
}
}

static class _exp_if {
Object _test, _conseq, _alt;

_exp_if(Object _test, Object _conseq, Object _alt) {
this._test = _test;
this._conseq = _conseq;
this._alt = _alt;
}
}

static class _exp_mult {
Object _rand1, _rand2;

_exp_mult(Object _rand1, Object _rand2) {
this._rand1 = _rand1;
this._rand2 = _rand2;
}
}

static class _exp_sub1 {
Object _rand;

_exp_sub1(Object _rand) {
this._rand = _rand;
}
}

static class _exp_zero {
Object _rand;

_exp_zero(Object _rand) {
this._rand = _rand;
}
}

static class _exp_letcc {
Object _body;

_exp_letcc(Object _body) {
this._body = _body;
}
}

static class _exp_throw {
Object _vexp, _kexp;

_exp_throw(Object _vexp, Object _kexp) {
this._vexp = _vexp;
this._kexp = _kexp;
}
}

static class _exp_let {
Object _vexp, _body;

_exp_let(Object _vexp, Object _body) {
this._vexp = _vexp;
this._body = _body;
}
}

static class _exp_lambda {
Object _body;

_exp_lambda(Object _body) {
this._body = _body;
}
}

static class _exp_app {
Object _rator, _rand;

_exp_app(Object _rator, Object _rand) {
this._rator = _rator;
this._rand = _rand;
}
}

static class _kt_empty$_k {
Object _dismount;

_kt_empty$_k(Object _dismount) {
this._dismount = _dismount;
}
}

static class _kt_if$_k {
Object _conseq, _alt, _env, _k;

_kt_if$_k(Object _conseq, Object _alt, Object _env, Object _k) {
this._conseq = _conseq;
this._alt = _alt;
this._env = _env;
this._k = _k;
}
}

static class _kt_mult$_inner$_k {
Object _v$ex, _k;

_kt_mult$_inner$_k(Object _v$ex, Object _k) {
this._v$ex = _v$ex;
this._k = _k;
}
}

static class _kt_mult$_outer$_k {
Object _rand2, _env, _k;

_kt_mult$_outer$_k(Object _rand2, Object _env, Object _k) {
this._rand2 = _rand2;
this._env = _env;
this._k = _k;
}
}

static class _kt_sub1$_k {
Object _k;

_kt_sub1$_k(Object _k) {
this._k = _k;
}
}

static class _kt_zero$_k {
Object _k;

_kt_zero$_k(Object _k) {
this._k = _k;
}
}

static class _kt_throw$_k {
Object _vexp, _env;

_kt_throw$_k(Object _vexp, Object _env) {
this._vexp = _vexp;
this._env = _env;
}
}

static class _kt_let$_k {
Object _body, _env, _k;

_kt_let$_k(Object _body, Object _env, Object _k) {
this._body = _body;
this._env = _env;
this._k = _k;
}
}

static class _kt_arg$_k {
Object _proc, _k;

_kt_arg$_k(Object _proc, Object _k) {
this._proc = _proc;
this._k = _k;
}
}

static class _kt_proc$_k {
Object _rand, _env, _k;

_kt_proc$_k(Object _rand, Object _env, Object _k) {
this._rand = _rand;
this._env = _env;
this._k = _k;
}
}

static class _clos_closure {
Object _code, _env;

_clos_closure(Object _code, Object _env) {
this._code = _code;
this._env = _env;
}
}

static class _envr_empty {


}

static class _envr_extend {
Object _arg, _env;

_envr_extend(Object _arg, Object _env) {
this._arg = _arg;
this._env = _env;
}
}


//trampoline
void trampoline() throws Exception {
tramp: while(true) {
switch (_pc) {
case _apply$_k:
if (_k instanceof _kt_empty$_k) {
Object _dismount = ((_kt_empty$_k) _k)._dismount;
break tramp;

} else if (_k instanceof _kt_if$_k) {
Object _conseq = ((_kt_if$_k) _k)._conseq;
Object _alt = ((_kt_if$_k) _k)._alt;
Object _env$ex = ((_kt_if$_k) _k)._env;
Object _k$ex = ((_kt_if$_k) _k)._k;
_k = _k$ex;
_env = _env$ex;
if ((Boolean)_v) {
_expr = _conseq;
 } else {
_expr = _alt;
 }
_pc = _value$_of;

} else if (_k instanceof _kt_mult$_inner$_k) {
Object _v$ex = ((_kt_mult$_inner$_k) _k)._v$ex;
Object _k$ex = ((_kt_mult$_inner$_k) _k)._k;
_k = _k$ex;
_v = ((Integer)_v$ex * (Integer)_v);
_pc = _apply$_k;

} else if (_k instanceof _kt_mult$_outer$_k) {
Object _rand2 = ((_kt_mult$_outer$_k) _k)._rand2;
Object _env$ex = ((_kt_mult$_outer$_k) _k)._env;
Object _k$ex = ((_kt_mult$_outer$_k) _k)._k;
_k = new _kt_mult$_inner$_k(_v, _k$ex);
_env = _env$ex;
_expr = _rand2;
_pc = _value$_of;

} else if (_k instanceof _kt_sub1$_k) {
Object _k$ex = ((_kt_sub1$_k) _k)._k;
_k = _k$ex;
_v = ((Integer)_v - (Integer)Integer.valueOf(1));
_pc = _apply$_k;

} else if (_k instanceof _kt_zero$_k) {
Object _k$ex = ((_kt_zero$_k) _k)._k;
_k = _k$ex;
_v = ((Integer)_v == 0);
_pc = _apply$_k;

} else if (_k instanceof _kt_throw$_k) {
Object _vexp = ((_kt_throw$_k) _k)._vexp;
Object _env$ex = ((_kt_throw$_k) _k)._env;
_k = _v;
_env = _env$ex;
_expr = _vexp;
_pc = _value$_of;

} else if (_k instanceof _kt_let$_k) {
Object _body = ((_kt_let$_k) _k)._body;
Object _env$ex = ((_kt_let$_k) _k)._env;
Object _k$ex = ((_kt_let$_k) _k)._k;
_k = _k$ex;
_env = new _envr_extend(_v, _env$ex);
_expr = _body;
_pc = _value$_of;

} else if (_k instanceof _kt_arg$_k) {
Object _proc = ((_kt_arg$_k) _k)._proc;
Object _k$ex = ((_kt_arg$_k) _k)._k;
_k = _k$ex;
_a = _v;
_c = _proc;
_pc = _apply$_proc;

} else if (_k instanceof _kt_proc$_k) {
Object _rand = ((_kt_proc$_k) _k)._rand;
Object _env$ex = ((_kt_proc$_k) _k)._env;
Object _k$ex = ((_kt_proc$_k) _k)._k;
_k = new _kt_arg$_k(_v, _k$ex);
_env = _env$ex;
_expr = _rand;
_pc = _value$_of;

} else { throw new Exception((new Formatter()).format("Error in union-case: could not match %s against type _kt", _k.getClass().getName()).out().toString());
}

break;
case _value$_of:
if (_expr instanceof _exp_const) {
Object _n = ((_exp_const) _expr)._v;
_k = _k;
_v = _n;
_pc = _apply$_k;

} else if (_expr instanceof _exp_var) {
Object _v = ((_exp_var) _expr)._v;
_k = _k;
_env = _env;
_num = _v;
_pc = _apply$_env;

} else if (_expr instanceof _exp_if) {
Object _test = ((_exp_if) _expr)._test;
Object _conseq = ((_exp_if) _expr)._conseq;
Object _alt = ((_exp_if) _expr)._alt;
_k = new _kt_if$_k(_conseq, _alt, _env, _k);
_env = _env;
_expr = _test;
_pc = _value$_of;

} else if (_expr instanceof _exp_mult) {
Object _rand1 = ((_exp_mult) _expr)._rand1;
Object _rand2 = ((_exp_mult) _expr)._rand2;
_k = new _kt_mult$_outer$_k(_rand2, _env, _k);
_env = _env;
_expr = _rand1;
_pc = _value$_of;

} else if (_expr instanceof _exp_sub1) {
Object _rand = ((_exp_sub1) _expr)._rand;
_k = new _kt_sub1$_k(_k);
_env = _env;
_expr = _rand;
_pc = _value$_of;

} else if (_expr instanceof _exp_zero) {
Object _rand = ((_exp_zero) _expr)._rand;
_k = new _kt_zero$_k(_k);
_env = _env;
_expr = _rand;
_pc = _value$_of;

} else if (_expr instanceof _exp_letcc) {
Object _body = ((_exp_letcc) _expr)._body;
_k = _k;
_env = new _envr_extend(_k, _env);
_expr = _body;
_pc = _value$_of;

} else if (_expr instanceof _exp_throw) {
Object _vexp = ((_exp_throw) _expr)._vexp;
Object _kexp = ((_exp_throw) _expr)._kexp;
_k = new _kt_throw$_k(_vexp, _env);
_env = _env;
_expr = _kexp;
_pc = _value$_of;

} else if (_expr instanceof _exp_let) {
Object _vexp = ((_exp_let) _expr)._vexp;
Object _body = ((_exp_let) _expr)._body;
_k = new _kt_let$_k(_body, _env, _k);
_env = _env;
_expr = _vexp;
_pc = _value$_of;

} else if (_expr instanceof _exp_lambda) {
Object _body = ((_exp_lambda) _expr)._body;
_k = _k;
_v = new _clos_closure(_body, _env);
_pc = _apply$_k;

} else if (_expr instanceof _exp_app) {
Object _rator = ((_exp_app) _expr)._rator;
Object _rand = ((_exp_app) _expr)._rand;
_k = new _kt_proc$_k(_rand, _env, _k);
_env = _env;
_expr = _rator;
_pc = _value$_of;

} else { throw new Exception((new Formatter()).format("Error in union-case: could not match %s against type _exp", _expr.getClass().getName()).out().toString());
}

break;
case _apply$_env:
if (_env instanceof _envr_empty) {
throw new Exception((new Formatter()).format("Error in _env: unbound variable").out().toString());

} else if (_env instanceof _envr_extend) {
Object _arg = ((_envr_extend) _env)._arg;
Object _env$ex = ((_envr_extend) _env)._env;
if ((Boolean)((Integer)_num == 0)) {
_k = _k;
_v = _arg;
_pc = _apply$_k;
 } else {
_k = _k;
_env = _env$ex;
_num = ((Integer)_num - 1);
_pc = _apply$_env;
 }

} else { throw new Exception((new Formatter()).format("Error in union-case: could not match %s against type _envr", _env.getClass().getName()).out().toString());
}

break;
case _apply$_proc:
if (_c instanceof _clos_closure) {
Object _code = ((_clos_closure) _c)._code;
Object _env$ex = ((_clos_closure) _c)._env;
_k = _k;
_env = new _envr_extend(_a, _env$ex);
_expr = _code;
_pc = _value$_of;

} else { throw new Exception((new Formatter()).format("Error in union-case: could not match %s against type _clos", _c.getClass().getName()).out().toString());
}

break;

default: throw new Exception("Invalid label " + _pc);
}
}
}
//run corresponds to main label from ParentheC
public void run() throws Exception {
_env = new _envr_empty();
_expr = new _exp_let(new _exp_lambda(new _exp_lambda(new _exp_if(new _exp_zero(new _exp_var(Integer.valueOf(0))), new _exp_const(Integer.valueOf(1)), new _exp_mult(new _exp_var(Integer.valueOf(0)), new _exp_app(new _exp_app(new _exp_var(Integer.valueOf(1)), new _exp_var(Integer.valueOf(1))), new _exp_sub1(new _exp_var(Integer.valueOf(0)))))))), new _exp_app(new _exp_app(new _exp_var(Integer.valueOf(0)), new _exp_var(Integer.valueOf(0))), new _exp_const(Integer.valueOf(5))));
_pc = _value$_of;
_k = new _kt_empty$_k(null);

trampoline();
System.out.printf("Factorial of 5: %s\n", _v);
}

public static void main(String[] args) throws Exception {
new _interp().run();
}
}
