package Absyn;

import java.util.*;

public class Stmt extends Absyn
{
    int stmt_val;
    public  void printAST(){};
    public  void printSYM(int n, ArrayList<String> names, ArrayList<Integer> depth, 
        int is_func_comp, int name_print, int scope_level)
    {}
    public  void addSYM(ArrayList<String> names, ArrayList<Integer> depth){};
    public  void removeSYM(ArrayList<String> names, ArrayList<Integer> depth){};
}

class StmtList extends Absyn 
{
    ArrayList<Stmt> sl;
    ArrayList<Integer>  typeList;
    public StmtList() 
    {
        sl = new ArrayList<Stmt>();
    }

    public StmtList append(Stmt s) 
    {
        sl.add(s);
        return this;
    }
    public void printAST()
    {
        for(Stmt st: sl){
            st.printAST();
        }
    }
    public void printSYM(int n, ArrayList<String> names, ArrayList<Integer> depth, 
        int is_func_comp, int name_print, int scope_level)
    {
        
        for(Stmt st: sl){
            st.printSYM(n, names, depth, is_func_comp, name_print, scope_level+1);
        }
    }
}
class Semi extends Stmt
{
    String sm;
    public Semi(String n)
    {
        sm = n;
        stmt_val = 8;
    }
    public void printAST(){
        myPrint.astWriter.write(sm);
    }
    public void printSYM(int n, ArrayList<String> names, ArrayList<Integer> depth, 
        int is_func_comp, int name_print){
        myPrint.symWriter.write(sm);
    }
    public void addSYM(ArrayList<String> names, ArrayList<Integer> depth)
    {
    }
    public void removeSYM(ArrayList<String> names, ArrayList<Integer> depth)
    {
    }

}
class AssignStmt extends Stmt 
{
    Assign assign;
    public AssignStmt(Assign a) 
    {
        assign = a;
        stmt_val = 0;
    }
    public void printAST()
    {
        assign.printAST();
        myPrint.astWriter.write(";\r\n");
    }
    public void printSYM(int n, ArrayList<String> names, ArrayList<Integer> depth, 
        int is_func_comp, int name_print, int scope_level)
    {
       // System.out.println("AssignStmt");
        assign.printSYM(n,names, depth, is_func_comp, name_print, scope_level);
    }
    public void addSYM(ArrayList<String> names, ArrayList<Integer> depth)
    {
        
    }
    public void removeSYM(ArrayList<String> names, ArrayList<Integer> depth)
    {
    }
}

class Assign extends Absyn 
{
    String name;
    Expr in_expr;
    Expr expr;
    int line;
    int pos;
    public Assign(String n, Expr idx, Expr ex, int _line, int _pos) 
    {
        name = n;
        in_expr = idx;
        expr = ex;
        line = _line;
        pos = _pos;
    }
    public void printAST(){
        myPrint.astWriter.write(name);
        if(in_expr != null){
            myPrint.astWriter.write("[");
            in_expr.printAST();
            myPrint.astWriter.write("]");
        }
        myPrint.astWriter.write("=");
        if (expr != null)
            expr.printAST();
    }
    public void printSYM(int n, ArrayList<String> names, ArrayList<Integer> depth, 
        int is_func_comp, int name_print, int scope_level){
        my_Symbol my_s  = SymbolTable.find(name);
        if(my_s == null)
        {
             System.out.println("SYMENTIC ERROR "+line+":"+pos
                +" note: "+ name+" is not declared");
        }
        else{
            int as_type = my_s.getType();
            int ex_type = expr.getExprType();
            String as_str = typetoString(as_type);
            String ex_str = typetoString(ex_type);

            expr.printSYM(n, names, depth, is_func_comp, name_print);

            if(as_type != ex_type){
                System.out.println("Warning : "+line+":"+pos
                    +" note: "+name+" : "+ex_str +" value is assigned to an "
                    +as_str +" variable");
            }
            if(in_expr != null && my_s.getisArray() == false){
                System.out.println("Warning "+in_expr.getLine()+":"+in_expr.getPos()
                    +" note: "+name+" is not array");
            }
            if(in_expr != null && my_s.getisArray() == true && in_expr.getExprType() == 1)
            {
                System.out.println("SYMENTIC ERROR "+in_expr.getLine()+":"+in_expr.getPos()
                    +" note: "+ "array subscript is not an interger");   
            }
        }

    }
    public void addSYM(ArrayList<String> names, ArrayList<Integer> depth)
    {
    }
}

class CallStmt extends Stmt 
{
    Call call;

    public CallStmt(Call c)
    {
        call = c;
        stmt_val = 1;

    }
    public void printAST()
    {
        call.printAST();
        myPrint.astWriter.write(";\r\n");
    }
    public void printSYM(int n, ArrayList<String> names, ArrayList<Integer> depth, 
        int is_func_comp, int name_print){}
    public void addSYM(ArrayList<String> names, ArrayList<Integer> depth){    }
    public void removeSYM(ArrayList<String> names, ArrayList<Integer> depth){ }
}

class Call extends Absyn 
{
    String name;
    ArgList args;
    int line;
    int pos;
    public Call(String n, ArgList al,int _line, int _pos)
    {
        name = n;
        args = al;
        line = _line;
        pos = _pos;
    }
    public void printAST()
    {
         myPrint.astWriter.write(name);
         myPrint.astWriter.write("(");
         if (args != null)
             args.printAST();
         myPrint.astWriter.write(")");

    }
    public void printSYM(int n, ArrayList<String> names, ArrayList<Integer> depth, 
        int is_func_comp, int name_print)
    {
        Function func = SymbolTable.find_func(name);
        if(func == null)
        {
            System.out.println("SYMENTIC ERROR "+line+":"+pos +" function '"
             +name+"' is not declared");
        }
         // myPrint.symWriter.write(name);
         // myPrint.symWriter.write("(");
         // if (args != null)
         //     args.printSYM();
         // myPrint.symWriter.write(")");

    }
    public void addSYM(ArrayList<String> names, ArrayList<Integer> depth)
    {
        
    }
    public int getType(){
        int ty;
        Function func = SymbolTable.find_func(name);
        if(func == null){
            ty = -1;
        }
        else{
            ty = func.getType();
        }
        return ty;
    }
}

class RetStmt extends Stmt 
{
    Expr expr;
    public RetStmt(Expr ex) 
    {
        expr = ex;
        stmt_val = 2;
    }
    public void printAST(){
         myPrint.astWriter.write("return");
        
        if(expr != null)
        {
            myPrint.astWriter.write(" ");
            expr.printAST();
        }
         myPrint.astWriter.write(";\r\n");
     }
    public void printSYM(int n, ArrayList<String> names, ArrayList<Integer> depth, 
        int is_func_comp, int name_print){}
    public void addSYM(ArrayList<String> names, ArrayList<Integer> depth){}
    public void removeSYM(ArrayList<String> names, ArrayList<Integer> depth){}
}

class WhileStmt extends Stmt 
{
    Expr expr;
    Stmt stmt;
    Boolean is_do;

    public WhileStmt(Expr ex, Stmt st, Boolean d) 
    {
        expr = ex;
        stmt = st;
        is_do = d;
        stmt_val = 3;
    }
    public void printAST()
    {
        if(is_do == false)
        {
            myPrint.astWriter.write("while(");
            expr.printAST();
            myPrint.astWriter.write(")");
            stmt.printAST();
        }
        else{
            myPrint.astWriter.write("do");
            stmt.printAST();
            myPrint.astWriter.write("while(");
            if (expr != null)
                expr.printAST();
            myPrint.astWriter.write(")\r\n");
               
        }

    }
    public void printSYM(int n, ArrayList<String> names, ArrayList<Integer> depth, 
        int is_func_comp, int name_print, int scope_level)
    {
        addSYM(names,depth);
        expr.ExprValidCheck();
        stmt.printSYM(1, names, depth, 1, 1, scope_level +1);
        removeSYM(names,depth);
    }
    public void addSYM(ArrayList<String> names, ArrayList<Integer> depth)
    {
        int dep = 1;
        for(String s:names){
            if( s.equals("while"))
                dep++;
        }
        names.add("while");
        depth.add(dep);
        
    }
    public void removeSYM(ArrayList<String> names, ArrayList<Integer> depth)
    {
       // myPrint.symWriter.write("remove WhileStmt call\r\n");
        names.remove(names.size()-1);
        depth.remove(depth.size()-1);
    }
}

class ForStmt extends Stmt 
{
    Assign init;
    Expr condition;
    Assign inc;
    Stmt stmt;

    public ForStmt(Assign _init, Expr _cond, Assign _inc, Stmt _stmt) 
    {
        init = _init;
        condition = _cond;
        inc = _inc;
        stmt = _stmt;
        stmt_val = 4;
    }
    public void printAST()
    {
        myPrint.astWriter.write("for(");
        init.printAST();
        myPrint.astWriter.write(";");
        condition.printAST();
        myPrint.astWriter.write(";");
        inc.printAST();
        myPrint.astWriter.write(")\r\n");
        stmt.printAST();
    }
    public void printSYM(int n, ArrayList<String> names, ArrayList<Integer> depth, 
        int is_func_comp, int name_print, int scope_level)
    {
        addSYM(names,depth);
        stmt.printSYM(1, names, depth, 1, 1, scope_level +1 );
        removeSYM(names,depth);

    }
    public void addSYM(ArrayList<String> names, ArrayList<Integer> depth)
    {
        int dep = 1;
        for(String s:names){
            if( s.equals("for"))
                dep++;
        }
        names.add("for");
        depth.add(dep);
    }
    public void removeSYM(ArrayList<String> names, ArrayList<Integer> depth)
    {
        //myPrint.symWriter.write("remove for call\r\n");
        names.remove(names.size()-1);
        depth.remove(depth.size()-1);
    }
}

class IfStmt extends Stmt 
{
    Expr condition;
    Stmt then_stmt;
    Stmt else_stmt;

    public IfStmt(Expr cond, Stmt th, Stmt el) 
    {
        condition = cond;
        then_stmt = th;
        else_stmt = el;
        stmt_val = 5;
    }
    public void printAST(){
        myPrint.astWriter.write("if(");
        if(condition != null)
            condition.printAST();
        myPrint.astWriter.write(")\r\n");
        then_stmt.printAST();
        if(else_stmt !=null){
        myPrint.astWriter.write("else\r\n");
            else_stmt.printAST();
        }
    }
    public void printSYM(int n, ArrayList<String> names, ArrayList<Integer> depth, 
        int is_func_comp, int name_print, int scope_level)
    {
        addSYM(names,depth);

        condition.ExprValidCheck();
        
        then_stmt.printSYM(1, names, depth, 1,1, scope_level+1);
        removeSYM(names,depth);
        SymbolTable.removeTABLE(scope_level+1);

        if(else_stmt !=null){
            addSYM_else(names, depth);
            else_stmt.printSYM(1, names, depth, 1, 1, scope_level+1);
            removeSYM(names, depth);
            SymbolTable.removeTABLE(scope_level+1);
        }
    }
    public void addSYM(ArrayList<String> names, ArrayList<Integer> depth)
    {
        int dep = 1;
        for(String s:names){
            if( s.equals("if"))
                dep++;
        }
        names.add("if");
        depth.add(dep);
        
    }
    public void addSYM_else(ArrayList<String> names, ArrayList<Integer> depth)
    {
        int dep = 1;
        for(String s:names){
            if( s.equals("if"))
                dep++;
        }
        names.add("else");
        depth.add(dep);
        
    }
    public void removeSYM(ArrayList<String> names, ArrayList<Integer> depth)
    {
        names.remove(names.size()-1);
        depth.remove(depth.size()-1);
    }
}

class SwitchStmt extends Stmt 
{
    Ident ident;
    CaseList caselist;
    StmtList default_stmt;
    Boolean df_hs_bk;

    public SwitchStmt(Ident id, CaseList cl, StmtList ds, Boolean dhb) 
    {
        ident = id;
        caselist = cl;
        default_stmt = ds;
        df_hs_bk = dhb;
        stmt_val = 6;
    }
    public void printAST()
    {
        myPrint.astWriter.write("switch (");
        ident.printAST();
        myPrint.astWriter.write(")\r\n");
        myPrint.astWriter.write("{\r\n");
        caselist.printAST();
        if(default_stmt != null)
        {
            myPrint.astWriter.write("default:\r\n");
            default_stmt.printAST();
            if(df_hs_bk)
                myPrint.astWriter.write("break;\r\n");
        }
        myPrint.astWriter.write("}\r\n");
        
    }
    public void printSYM(int n, ArrayList<String> names, ArrayList<Integer> depth, 
        int is_func_comp, int name_print, int scope_level)
    {
        addSYM(names,depth);
        caselist.printSYM(1, names, depth, 1, 1, scope_level);
        if (default_stmt != null)
        {
            default_stmt.printSYM(1, names, depth, 1, 1, scope_level+1);
        }
        SymbolTable.removeTABLE(scope_level +1);
        removeSYM(names, depth);
    }
    public void addSYM(ArrayList<String> names, ArrayList<Integer> depth)
    {
        int dep = 1;
        for(String s:names){
            if( s.equals("switch_case"))
                dep++;
        }
        names.add("switch_case");
        depth.add(dep);
        
    }
    public void removeSYM(ArrayList<String> names, ArrayList<Integer> depth)
    {
        names.remove(names.size()-1);
        depth.remove(depth.size()-1);
    }
}

class CaseList extends Absyn 
{
    ArrayList<String> il;
    ArrayList<StmtList> sll;
    ArrayList<Boolean> bl;

    public CaseList() 
    {
        il = new ArrayList<String>();
        sll = new ArrayList<StmtList>();
        bl = new ArrayList<Boolean>();
    }

    public CaseList append(String k, StmtList sl, Boolean hb)
    {
        il.add(k);
        sll.add(sl);
        bl.add(hb);
        return this;
    }
    public void printAST()
    {
        int list_size = il.size();
        String str;
        StmtList stmtl;
        boolean ble;
        for (int i = 0; i< list_size; i++){
            myPrint.astWriter.write("case ");
            str = il.get(i);
            myPrint.astWriter.write(str);
            myPrint.astWriter.write(":\r\n");
            stmtl = sll.get(i);
            stmtl.printAST();
            ble = bl.get(i);
            if(ble)
                myPrint.astWriter.write("break;\r\n");
        }
    }
    public void printSYM(int n, ArrayList<String> names, ArrayList<Integer> depth, 
        int is_func_comp, int name_print, int scope_level)
    {
        for (StmtList sll_e : sll)  
            sll_e.printSYM(1, names,depth, 1, 1, scope_level+1);

    }
    public void addSYM(ArrayList<String> names, ArrayList<Integer> depth)
    {
        
    }
    public void removeSYM(ArrayList<String> names, ArrayList<Integer> depth)
    {
    }
}

class Expr extends Absyn 
{
    int line;
    int pos;
    int ty;
    public void printAST(){};
    public void printSYM(int n, ArrayList<String> names, ArrayList<Integer> depth, 
        int is_func_comp, int name_print){};
    public int getExprType(){
    //    System.out.println("getExprType");
    //    System.out.println(pos);
        return ty;
    };
    public void ExprValidCheck(){};
    public int getLine(){
        return line;
    }
    public int getPos(){
        return pos;
    }
}
class Num extends Expr{
    String num;
    public Num(String n, int _ty, int _line, int _pos)
    {
        num = n;
        ty = _ty;
        line = _line;
        pos = _pos;
    }
    public void printAST()
    {
        myPrint.astWriter.write(num);
    }

}
class IDExpr extends Expr{
    String name;
    Expr expr;
    boolean isArray;
    public IDExpr(String n, Expr e, boolean isA, int _line, int _pos)
    {
        name = n;
        expr = e;
        isArray = isA;
        line = _line;
        pos = _pos;
    }
    public void printAST()
    {
      myPrint.astWriter.write(name);
      if(isArray)
      {
        myPrint.astWriter.write("[");
        expr.printAST();
        myPrint.astWriter.write("]");
      }  
    }
    public void printSYM(int n, ArrayList<String> names, ArrayList<Integer> depth, 
        int is_func_comp, int name_print)
    {

    }
    public int getExprType(){
        my_Symbol my_s = SymbolTable.find(name);
        if(my_s == null)
        {
            System.out.println("SYMENTIC ERROR "+line+":"+pos
                +" note: "+ name + " is not declared");
            return -1;
        }
        else{
            boolean my_s_isArr = my_s.getisArray();

            if(isArray == true && my_s_isArr == false)
            {
                System.out.println("SYMENTIC ERROR "+line+":"+pos
                +" note: "+ name + " is not an array");
            }
            else if(isArray == false && my_s_isArr == true)
            {
                System.out.println("SYMENTIC ERROR "+line+":"+pos
                +" note: "+ name + " is an array");
            }
            return my_s.getType();
        }
    }
}
class UnOpExpr extends Expr{
    String op;
    Expr e1;
    public UnOpExpr(String _op, Expr _e1, int _line, int _pos){
        e1 = _e1;
        op = _op;
        line = _line;
        pos = _pos;
    }
    public void printAST()
    {
        myPrint.astWriter.write(op);
        e1.printAST();
    }
    public void printSYM(int n, ArrayList<String> names, ArrayList<Integer> depth, 
        int is_func_comp, int name_print)
    {

    }
    public int getExprType(){
        return e1.getExprType();
    }
}
class CallExpr extends Expr{
    Call cl;
    public CallExpr(Call _cl, int _pos, int _line)
    {
        cl = _cl;
        line = _line;
        pos = _pos;
    }
    public void printAST()
    {
        cl.printAST();
    }
    public void printSYM(int n, ArrayList<String> names, ArrayList<Integer> depth, 
        int is_func_comp, int name_print)
    {
        cl.printSYM(n,names, depth, is_func_comp, name_print);
    }
    public int getExprType(){
        return 999;
    }

}
class BinOpExpr extends Expr{
    String op;
    Expr e1;
    Expr e2;
    
    public BinOpExpr(Expr _e1, String _op, Expr _e2, int _line, int _pos){
        e1 = _e1;
        op = _op;
        e2 = _e2;
        line = _line;
        pos = _pos;
    }
    public void printAST()
    {
        e1.printAST();
        myPrint.astWriter.write(op);
        e2.printAST();
    }
    public void printSYM(int n, ArrayList<String> names, ArrayList<Integer> depth, 
        int is_func_comp, int name_print)
    {

    }
    public int getExprType(){
        return e1.getExprType();
    }
    public void ExprValidCheck(){
        int e1_type = e1.getExprType();
        int e2_type = e2.getExprType();
        if ( (e1_type != -1) && (e1_type != -1)
            && (e1_type != e2_type))
        {
            System.out.println("Warning : "+line+":"+pos
                    +" note: " +typetoString(e1_type) +" " + op + " "
                    + typetoString(e2_type) 
                    +" expr type miss match");
        }
    }
}

class ArgList extends Absyn {
    ArrayList<Expr> al;

    public ArgList() {
        al = new ArrayList<Expr>();
    }

    public ArgList append(Expr e) {
        al.add(e);
        return this;
    }
    public void printAST(){
        int list_size = al.size();
        Expr tmp;
        for (int i = 0; i< list_size; i++)
        {
            tmp = al.get(i);
            tmp.printAST();
            if(i != list_size-1)
                myPrint.astWriter.write(", ");
        }
    }
    public void printSYM(int n, ArrayList<String> names, ArrayList<Integer> depth, 
        int is_func_comp, int name_print)
    {

    }
}

class CompStmt extends Stmt
{
    DeclList decls;
    StmtList stmts;

    public CompStmt(DeclList dl, StmtList sl) 
    {
        decls = dl;
        stmts = sl;
        stmt_val = 7;
    }
    public void printAST()
    {
        myPrint.astWriter.write("{\r\n");
        if(decls != null)
            decls.printAST();
        if(stmts != null)
            stmts.printAST();
        myPrint.astWriter.write("}\r\n");
    }
    public void printSYM(int n, ArrayList<String> names, ArrayList<Integer> depth, 
        int is_func_comp, int name_print, int scope_level)
    {
        if (is_func_comp == 0)
        {
            addSYM(names, depth);
        }
        int is_global = 0;
        if(decls != null){
            decls.printSYM(n,is_global, names, depth,is_func_comp, name_print, scope_level+1);
        }
        if(stmts != null){
            stmts.printSYM(n, names, depth, 0, 1, scope_level+1);
        }
        if (is_func_comp == 0)
            removeSYM(names, depth);
        SymbolTable.removeTABLE(scope_level +1);

    }
    public void addSYM(ArrayList<String> names, ArrayList<Integer> depth)
    {
        int dep = 1;
        for(String s:names){
            if( s.equals("compound"))
                dep++;
        }
        names.add("compound");
        depth.add(dep);
        
    }
    public void removeSYM(ArrayList<String> names, ArrayList<Integer> depth)
    {
        names.remove(names.size()-1);
        depth.remove(depth.size()-1);
    }

}

class EmptyStmt extends Stmt
{
    public EmptyStmt() {}
    public void printAST(){}
    public void printSYM(int n, ArrayList<String> names, ArrayList<Integer> depth, 
        int is_func_comp, int name_print){}
    public void addSYM(ArrayList<String> names, ArrayList<Integer> depth){}
    public void removeSYM(ArrayList<String> names, ArrayList<Integer> depth){}
}

