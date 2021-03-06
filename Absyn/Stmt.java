package Absyn;

import java.util.*;

public class Stmt extends Absyn
{
    int stmt_val;
    public  void printAST(){};
    public  void printSYM(int n, ArrayList<String> names, ArrayList<Integer> depth, 
        int is_func_comp, int name_print, int scope_level)
    {
        System.out.println("not overrided");
    }
    public  void addSYM(ArrayList<String> names, ArrayList<Integer> depth){};
    public  void removeSYM(ArrayList<String> names, ArrayList<Integer> depth){};
    public  void printCODE(){};
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
    public  void printCODE()
    {
        for(Stmt st: sl){
            st.printCODE();
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
    public void printCODE(){
        System.out.println("semi");
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
    public void printCODE(){
        assign.printCODE();
        //System.out.println("assignstmt");
    }
}

class Assign extends Absyn 
{
    String name;
    Expr in_expr;
    Expr expr;
    int line;
    int pos;
    int type_convert;
    my_Symbol save_symbol;
    public Assign(String n, Expr idx, Expr ex, int _line, int _pos) 
    {
        name = n;
        in_expr = idx;
        expr = ex;
        line = _line;
        pos = _pos;
        type_convert =0;
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
        save_symbol = my_s;
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
            expr.ExprValidCheck();
            if (in_expr != null){
                in_expr.printSYM(n, names, depth, is_func_comp, name_print);
            }
            if(as_type != ex_type){
                System.out.println("Warning : "+line+":"+pos
                    +" note: "+name+" : "+ex_str +" value is assigned to an "
                    +as_str +" variable");
                if(ex_str.equals("float")){
                    type_convert = 1;// f->I;
                }
                else if(ex_str.equals("integer")){
                    type_convert = 2; // I->F;
                }
            }
            if(in_expr != null && my_s.getisArray() == false){
                System.out.println("Warning "+in_expr.getLine()+":"+in_expr.getPos()
                    +" note: "+name+" is not array");
            }
            if(in_expr != null && my_s.getisArray() == true && in_expr.getExprType() == 1)
            {
                System.out.println("SYMENTIC ERROR "+in_expr.getLine()+":"+in_expr.getPos()
                    +" note: "+ "array subscript is not an integer");   
            }
        }

    }
    public void addSYM(ArrayList<String> names, ArrayList<Integer> depth)
    {
    }
    public void printCODE(){
       // System.out.println(save_symbol.offset);
        if(expr != null){
            expr.printCODE();
            //F->I
            if(type_convert == 1){
                code_write(String.format("  F2I VR(%d)@ VR(%d)",
                                expr.reg_num, Reg_offset.my_offset.reg_offset));
                expr.reg_num = Reg_offset.my_offset.reg_offset;
                Reg_offset.my_offset.add_off(); 
            }
            //I->F
            else if(type_convert == 2){
                code_write(String.format("  I2F VR(%d)@ VR(%d)",
                                expr.reg_num, Reg_offset.my_offset.reg_offset));
                expr.reg_num = Reg_offset.my_offset.reg_offset;
                Reg_offset.my_offset.add_off();
            }
        }
        if(save_symbol != null){
            //code_write("// Assign");
            if(save_symbol.isGlobal){
                if(in_expr != null){
                    //code_write("// Assign arr");
                    int arr_num;
                    in_expr.printCODE();
                    code_write(String.format("  ADD %d VR(%d)@ VR(%d)",
                    save_symbol.offset,in_expr.reg_num,Reg_offset.my_offset.reg_offset));
                    arr_num = Reg_offset.my_offset.reg_offset;
                    Reg_offset.my_offset.add_off();
                    code_write(String.format("  MOVE VR(%d)@ MEM(VR(%d)@)",
                        expr.reg_num,arr_num));
                }
                else{
                    code_write(String.format("  MOVE VR(%d)@ MEM(%d)",
                        expr.reg_num,save_symbol.offset));
                }
            }
            //local variable 
            else{
                if(in_expr != null){
                    //code_write("// Assign arr");
                    int arr_num;
                    in_expr.printCODE();
                    code_write(String.format("  ADD %d VR(%d)@ VR(%d)",
                    save_symbol.offset,in_expr.reg_num,Reg_offset.my_offset.reg_offset));
                    arr_num = Reg_offset.my_offset.reg_offset;
                    Reg_offset.my_offset.add_off();
                    code_write(String.format("  MOVE VR(%d)@ MEM(FP@(VR(%d)@))",
                        expr.reg_num,arr_num));
                }
                else{

                    code_write(String.format("  MOVE VR(%d)@ MEM(FP@(%d))",
                        expr.reg_num, save_symbol.offset));
                }
            }
        }
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
    public  void printSYM(int n, ArrayList<String> names, ArrayList<Integer> depth, 
        int is_func_comp, int name_print, int scope_level){
       // System.out.println("callstmt");
        call.printSYM(n,names, depth, is_func_comp, name_print);
    }
    public void addSYM(ArrayList<String> names, ArrayList<Integer> depth){    }
    public void removeSYM(ArrayList<String> names, ArrayList<Integer> depth){ }
    public void printCODE(){
        call.printCODE();
    }
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
        if(name.equals("printf")){
            Expr tmp;
            int arg_ty;
            for(int i =0; i < args.al.size(); i ++){
                //temp = args.al.get(i);
                //arg_ty.getType();
            }
            args.printSYM(n,names,depth,is_func_comp,name_print);
        }
        else if(name.equals("scanf")){
            args.printSYM(n, names, depth, is_func_comp, name_print); 
            
            
        }
        else{
            Function func = SymbolTable.find_func(name);
            if(func == null)
            {
                System.out.println("SYMENTIC ERROR "+line+":"+pos +" function '"
                 +name+"' is not declared");
            }
            else{
             //   System.out.println("call analysis");
                args.save_function = func;
                args.printSYM(n, names, depth, is_func_comp, name_print);
            }
            //myPrint.symWriter.write(name);
         // myPrint.symWriter.write("(");
         // if (args != null)
         //     args.printSYM();
         // myPrint.symWriter.write(")");
        }
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
            //System.out.println(" function : "+ name + " type : "+ ty);
        }
        return ty;
    }
    public void printCODE(){
        //System.out.println("call");
        Expr arg_expr;
        String label_return = "pos_"+Reg_offset.my_offset.label_offset;
        Reg_offset.my_offset.label_offset += 1;
        //code_write("//call");
        //call by reference
        if (name.equals("scanf")){
            arg_expr = args.al.get(0);
            //System.out.println(arg_expr.name + " !!!!!!!!!");
            arg_expr.get_MEM_addr();
            code_write(String.format(" MOVE VR(%d)@ MEM(SP@)", arg_expr.reg_num));
            code_write("  ADD 1 SP@ SP");
            //System.out.println("scanf type " + arg_expr.ty);
            if(arg_expr.ty == 1){
               // System.out.println("float scanf ");
                name = "scanf_f";
            }
        }
        //call by value;
        else{
            if (args != null){
                int i;
                
                for (i = args.al.size() -1; i >= 0; i--){
                    arg_expr = args.al.get(i);
                    arg_expr.printCODE();
                    code_write(String.format("  MOVE VR(%d)@ MEM(SP@)",arg_expr.reg_num));
                    code_write("  ADD 1 SP@ SP");
                }
            }
        }
        code_write(String.format("  MOVE %s MEM(SP@)",label_return));
        code_write("  ADD 1 SP@ SP");
        code_write("  MOVE FP@ MEM(SP@)");
        code_write("  ADD 1 SP@ SP");
        code_write("  MOVE SP@ FP");
        //code_write("  ADD 1 SP@ SP");
        
        code_write("  JMP "+name);
        code_write("LAB "+ label_return);
        code_write(String.format("  SUB SP@ %d SP",2+args.al.size()));

       // System.out.println("call type_convert");
    }
}

class RetStmt extends Stmt 
{
    Expr expr;
    Function save_function;
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
        int is_func_comp, int name_print,  int scope_level){
        //System.out.println("RetStmt print sym");
        expr.printSYM(n, names, depth, is_func_comp, name_print);
        
        save_function = SymbolTable.find_last_func();
        int expr_type = expr.ty;
        int func_type = save_function.t.typecheck();
        if(expr_type != func_type){
            System.out.println("Warning : "+expr.line+":"+expr.pos
                    +" note: return type miss match");
        }
        //System.out.println("RetStmt save_function + " + save_function.name);
    }
    public void addSYM(ArrayList<String> names, ArrayList<Integer> depth){}
    public void removeSYM(ArrayList<String> names, ArrayList<Integer> depth){}
    public void printCODE(){
        //System.out.println("RetStmt");
        expr.printCODE();
        int expr_type = expr.ty;
        int func_type = save_function.t.typecheck();
        int type_convert = 0;
        //System.out.println("\nfunction : " + save_function.name + " expr_type " + expr_type + " function type " + func_type);
        if(expr_type == 1 && func_type == 0){
            type_convert = 1;
        }
        else if (expr_type == 0 && func_type == 1){
            type_convert = 2;
        }

        if(type_convert == 1){
            //System.out.println(" F2I");
            code_write(String.format("  F2I VR(%d)@ VR(%d)",
                            expr.reg_num, Reg_offset.my_offset.reg_offset));
            expr.reg_num = Reg_offset.my_offset.reg_offset;
            Reg_offset.my_offset.add_off(); 
        }
        //I->F
        else if(type_convert == 2){
            code_write(String.format("  I2F VR(%d)@ VR(%d)",
                            expr.reg_num, Reg_offset.my_offset.reg_offset));
            expr.reg_num = Reg_offset.my_offset.reg_offset;
            Reg_offset.my_offset.add_off();
        }
        code_write(String.format("  MOVE VR(%d)@ VR(0)", 
                expr.reg_num));
        //Reg_offset.my_offset.add_off();
        code_write("  MOVE FP@ SP");
        code_write("  MOVE MEM(SP@(-1))@ FP");
        code_write("  JMP  MEM(SP@(-2))@");
    }
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
    public void printCODE(){
        //System.out.println("WhileStmt");
        String while_cond = "pos_"+Reg_offset.my_offset.label_offset;
        Reg_offset.my_offset.label_offset += 1;
        String while_start = "pos_"+Reg_offset.my_offset.label_offset;
        Reg_offset.my_offset.label_offset += 1;
        String while_end = "pos_"+Reg_offset.my_offset.label_offset;
        Reg_offset.my_offset.label_offset += 1;
        if(is_do){
            code_write(String.format("  JMP %s",while_start));
        }
        code_write(String.format("LAB %s", while_cond));
        expr.printCODE();
        code_write(String.format("  JMPZ VR(%d)@ %s",
            expr.reg_num, while_end));
        code_write(String.format("LAB %s",
            while_start));
        stmt.printCODE();
        code_write(String.format("  JMP %s",
            while_cond));
        code_write(String.format("LAB %s",
            while_end));
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
        init.printSYM(1, names, depth, 1, 1, scope_level +1);
        condition.printSYM(1, names, depth, 1,1);
        inc.printSYM(1, names, depth, 1,1, scope_level +1);
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
    public void printCODE(){
        //System.out.println("for stmt");
        String for_start = "pos_"+Reg_offset.my_offset.label_offset;
        Reg_offset.my_offset.label_offset += 1;
        String for_end = "pos_"+Reg_offset.my_offset.label_offset;
        Reg_offset.my_offset.label_offset += 1;
       
        init.printCODE();
        code_write(String.format("LAB %s",
            for_start));
        condition.printCODE();
        code_write(String.format("JMPZ VR(%d)@ %s",
            condition.reg_num, for_end));
        stmt.printCODE();
        inc.printCODE();
        code_write(String.format("JMP %s",
            for_start));
        code_write(String.format("LAB %s",
            for_end));
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
        condition.printSYM(1, names, depth, 1,1);
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
    public void printCODE(){
        //System.out.println("IfStmt");
        String label_next = "pos_"+Reg_offset.my_offset.label_offset;
        Reg_offset.my_offset.label_offset += 1;
        String label_else = "pos_"+Reg_offset.my_offset.label_offset;
        Reg_offset.my_offset.label_offset += 1;
        
        condition.printCODE();
        
        if(else_stmt != null){
            //System.out.println("IfStmt1");
            code_write(String.format("  JMPZ VR(%d)@ %s",
                condition.reg_num, label_else));
        }
        else{
            //System.out.println("IfStmt2");
            code_write(String.format("  JMPZ VR(%d)@ %s",
                condition.reg_num, label_next));
        }
        then_stmt.printCODE();
        if(else_stmt != null){
            //System.out.println("IfStmt3");
            code_write(String.format("  JMP %s", label_next));
            code_write(String.format("LAB %s",label_else));
            else_stmt.printCODE();
        }
        code_write(String.format("LAB %s",label_next));
        
        
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
        ident.printSYM(0,100, 0, 0);
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
    public void printCODE(){
        //System.out.println("SwitchStmt");
        String switch_default = "pos_" +Reg_offset.my_offset.label_offset;
        Reg_offset.my_offset.label_offset += 1;
        
        String switch_next = "pos_" +Reg_offset.my_offset.label_offset;
        Reg_offset.my_offset.label_offset += 1;
        
        int reg_num = Reg_offset.my_offset.reg_offset;

        my_Symbol save_symbol = ident.save_symbol;
        if(save_symbol != null){
            if(save_symbol.isGlobal){
                if(save_symbol.isArray){
                    code_write(String.format("  MOVE MEM(%d)@ VR(%d)",
                        save_symbol.offset +  Integer.parseInt(ident.ar_num), reg_num));
                }
                else{
                    code_write(String.format("  MOVE MEM(%d)@ VR(%d)",
                        save_symbol.offset, reg_num));
                }
            }
            //parameters
            else if(save_symbol.isParam){
                code_write(String.format("  MOVE MEM(FP@(%d))@ VR(%d)",
                    -3 -save_symbol.offset, reg_num));
            }
            //local variable
            else{
                if(save_symbol.isArray ){
                     code_write(String.format("  MOVE MEM(FP@(%d))@ VR(%d)",
                        save_symbol.offset + Integer.parseInt(ident.ar_num), reg_num));   
                }
                else{
                    code_write(String.format("  MOVE MEM(FP@(%d))@ VR(%d)",
                        save_symbol.offset, reg_num));
                }
            }

            Reg_offset.my_offset.add_off();

            if(save_symbol.ty.typecheck() == 1){
                code_write(String.format("  F2I VR(%d)@ VR(%d)",
                                reg_num, Reg_offset.my_offset.reg_offset));
                reg_num = Reg_offset.my_offset.reg_offset;
                Reg_offset.my_offset.add_off(); 
            }
            caselist.printCODE(reg_num);
            code_write(String.format("LAB %s",switch_default));
            if(default_stmt != null){
                default_stmt.printCODE();
            }
            code_write(String.format("LAB %s",switch_next));
        }
        else{
            System.out.println("SwitchStmt Error");   
        }
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
    public void printCODE(int reg_num){
        //System.out.println("caselist");
        int list_size = il.size();
        String str;
        StmtList stmtl;
        boolean ble;
        ArrayList<String> label_cases = new ArrayList<String>();
        int case_reg_num =Reg_offset.my_offset.reg_offset;
        Reg_offset.my_offset.add_off();
        String label_break = "pos_"+(Reg_offset.my_offset.label_offset -1);
        String label_default = "pos_"+(Reg_offset.my_offset.label_offset -2);
        for(int i = 0; i< list_size; i++){
            label_cases.add( "pos_"+Reg_offset.my_offset.label_offset);
            Reg_offset.my_offset.label_offset += 1;
            str = il.get(i);
            code_write(String.format("  SUB VR(%d)@ %s VR(%d)",
                        reg_num,str,case_reg_num));
            code_write(String.format("  JMPZ VR(%d)@ %s",
                case_reg_num,label_cases.get(i)));

        }
        code_write(String.format(" JMP %s",label_default));
        for(int i = 0; i < list_size; i++){
            code_write(String.format("LAB %s",label_cases.get(i)));
            stmtl = sll.get(i);
            stmtl.printCODE();
            ble = bl.get(i);
            if(ble){
                code_write(String.format("JMP %s",label_break));
            }
        }
    }
}

class Expr extends Absyn 
{
    int line;
    int pos;
    int ty;
    int reg_num;
    String name;
    Expr expr;
    int type_convert = 0;;
    // 1 :  F->I;
    // 2 :  I->F;
    public void printAST(){};
    public void printSYM(int n, ArrayList<String> names, ArrayList<Integer> depth, 
        int is_func_comp, int name_print){
        System.out.println("not overrided EXPR");
    };
    public int getExprType(){
        //System.out.println("getExprType : " + ty);
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
    public void printCODE(){
        System.out.println("EXPR printCODE is not overrided");

    }
    public void get_MEM_addr(){
        System.out.println("EXPR get mem addr is not overrided");
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
        reg_num = 0;
    }
    public void printAST()
    {
        myPrint.astWriter.write(num);
    }
    public void printSYM(int n, ArrayList<String> names, ArrayList<Integer> depth, 
    int is_func_comp, int name_print)
    {
        //System.out.println("NUM expr  sym");

    }
    public void printCODE(){
        //System.out.println("NUM printcode");
        reg_num = Reg_offset.my_offset.reg_offset;
        code_write(String.format("  MOVE %s VR(%d)",num, reg_num));
        Reg_offset.my_offset.add_off();
        //F->I
        if(type_convert == 1){
            code_write(String.format("  F2I VR(%d)@ VR(%d)",
                            reg_num, Reg_offset.my_offset.reg_offset));
            reg_num = Reg_offset.my_offset.reg_offset;
            Reg_offset.my_offset.add_off(); 
        }
        //I->F
        else if(type_convert == 2){
            code_write(String.format("  I2F VR(%d)@ VR(%d)",
                            reg_num, Reg_offset.my_offset.reg_offset));
            reg_num = Reg_offset.my_offset.reg_offset;
            Reg_offset.my_offset.add_off();
        }

        
    }

}
class IDExpr extends Expr{
    
    boolean isArray;
    my_Symbol save_symbol;
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
        if (expr != null){
            expr.printSYM(n, names, depth, is_func_comp, name_print);
        }
        
        my_Symbol my_s = SymbolTable.find(name);
        save_symbol = my_s;
        ty = my_s.ty.typecheck();
        //System.out.println("printsysm expr : " + name);
        if(my_s == null)
        {
            System.out.println("SYMENTIC ERROR "+line+":"+pos
                +" note: "+ name + " is not declared");
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
        }

    }
    public int getExprType(){
        
        my_Symbol my_s = SymbolTable.find(name);
        save_symbol = my_s;
        //System.out.println("getExprType");
        if(my_s == null)
        {
    
            return -1;
        }
        else{

            //System.out.println("my_s.gettype(): "+my_s.getType());
            return my_s.getType();
        }
        
    }
    public void printCODE(){
        
        
        //System.out.println("idexpr1 : "+ save_symbol.name);
        int arr_offset = 0;
        
        if(save_symbol == null){
            System.out.println("NULL");
        }
        reg_num = Reg_offset.my_offset.reg_offset;
        if(save_symbol.isGlobal){
            if(isArray){
                int arr_num;
                //code_write("//IDexpr arr");
                expr.printCODE();
                code_write(String.format("  ADD VR(%d)@ %d VR(%d)",
                    expr.reg_num,save_symbol.offset,Reg_offset.my_offset.reg_offset));
                arr_num = Reg_offset.my_offset.reg_offset;
                Reg_offset.my_offset.add_off();
                code_write(String.format("  MOVE MEM(VR(%d)@)@ VR(%d)",
                    arr_num, Reg_offset.my_offset.reg_offset));
                reg_num = Reg_offset.my_offset.reg_offset;
            }
            else{
                code_write(String.format("  MOVE MEM(%d)@ VR(%d)",
                    save_symbol.offset, reg_num));
            }
        }
        //parameters
        else if(save_symbol.isParam){
            code_write(String.format("  MOVE MEM(FP@(%d))@ VR(%d)",
                -3 -save_symbol.offset, reg_num));
        }
        //local variable
        else{
            if(isArray){
                int arr_num;
                //code_write("//IDexpr arr");
                expr.printCODE();
                code_write(String.format("  ADD VR(%d)@ %d VR(%d)",
                    expr.reg_num,save_symbol.offset,Reg_offset.my_offset.reg_offset));
                arr_num = Reg_offset.my_offset.reg_offset;
                Reg_offset.my_offset.add_off();
                code_write(String.format("  MOVE MEM(FP@(VR(%d)@))@ VR(%d)",
                    arr_num, Reg_offset.my_offset.reg_offset));
                reg_num = Reg_offset.my_offset.reg_offset;
            }
            else{
            code_write(String.format("  MOVE MEM(FP@(%d))@ VR(%d)",
                save_symbol.offset, reg_num));
            }
        }
        Reg_offset.my_offset.add_off();
        //F->I
        if(type_convert == 1){
            code_write(String.format("  F2I VR(%d)@ VR(%d)",
                            reg_num, Reg_offset.my_offset.reg_offset));
            reg_num = Reg_offset.my_offset.reg_offset;
            Reg_offset.my_offset.add_off(); 
        }
        //I->F
        else if(type_convert == 2){
            code_write(String.format("  I2F VR(%d)@ VR(%d)",
                            reg_num, Reg_offset.my_offset.reg_offset));
            reg_num = Reg_offset.my_offset.reg_offset;
            Reg_offset.my_offset.add_off();
        }
    }
    public void get_MEM_addr(){
        if(save_symbol.isGlobal){
            if(isArray){
                code_write("//get_mem addr");
                expr.printCODE();
                code_write(String.format("  ADD VR(%d)@ %d VR(%d)",
                    expr.reg_num,save_symbol.offset,Reg_offset.my_offset.reg_offset));
                reg_num = Reg_offset.my_offset.reg_offset;
                Reg_offset.my_offset.add_off();
            }
            else{
                code_write(String.format("  MOVE %d VR(%d)",
                    save_symbol.offset,Reg_offset.my_offset.reg_offset));
                reg_num = Reg_offset.my_offset.reg_offset;
                Reg_offset.my_offset.add_off();
            }
        }
        //parameters
        else if(save_symbol.isParam){
            System.out.println("parameter get_mem_addr");
            //return (-3 - save_symbol.offset);
        }
        //local variable
        else{
            if(isArray){
                int arr_num;
                code_write("//get_mem arr");
                expr.printCODE();
                code_write(String.format("  ADD VR(%d)@ %d VR(%d)",
                    expr.reg_num,save_symbol.offset,Reg_offset.my_offset.reg_offset));
                arr_num = Reg_offset.my_offset.reg_offset;
                Reg_offset.my_offset.add_off();
                code_write(String.format("  MOVE FP@(VR(%d)@) VR(%d)",
                            arr_num, Reg_offset.my_offset.reg_offset));
                reg_num = Reg_offset.my_offset.reg_offset;
                Reg_offset.my_offset.add_off();
            }
            else{
                code_write(String.format("  MOVE FP@(%d) VR(%d)",
                            save_symbol.offset, Reg_offset.my_offset.reg_offset));
                reg_num = Reg_offset.my_offset.reg_offset;
                Reg_offset.my_offset.add_off();
            }

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
        //System.out.println("UnOpExpr printsym");
        e1.printSYM(n, names, depth, is_func_comp, name_print);
        ty = e1.ty;
    }
    public int getExprType(){
        return e1.getExprType();
    }
    public void printCODE(){
        e1.printCODE();
        
        code_write(String.format("  SUB 0 VR(%d)@ VR(%d)",
                e1.reg_num,Reg_offset.my_offset.reg_offset));
        reg_num = Reg_offset.my_offset.reg_offset;
        Reg_offset.my_offset.add_off();
    
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
        //System.out.println("callexpr");
        cl.printSYM(n,names, depth, is_func_comp, name_print);
        ty = cl.getType();
    }
    public int getExprType(){
        return cl.getType();
    }
    public void printCODE(){
        cl.printCODE();
        //System.out.println("call type_convert : " + type_convert);
        //F->I
        if(type_convert == 1){
            code_write(String.format("  F2I VR(0)@ VR(%d)",
                            Reg_offset.my_offset.reg_offset));
            code_write(String.format("  MOVE VR(%d)@ VR(0)",
                            Reg_offset.my_offset.reg_offset));
            Reg_offset.my_offset.add_off(); 
        }
        //I->F
        else if(type_convert == 2){
            code_write(String.format("  I2F VR(0)@ VR(%d)",
                            Reg_offset.my_offset.reg_offset));
            code_write(String.format("  MOVE VR(%d)@ VR(0)",
                            Reg_offset.my_offset.reg_offset));
            Reg_offset.my_offset.add_off();
        }
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
        e1.printSYM(n, names, depth, is_func_comp, name_print);
        e2.printSYM(n, names, depth, is_func_comp, name_print);
        int e1_type = e1.ty;
        int e2_type = e2.ty;
        //System.out.println(e1_type + " "+ op + " " +e2_type);
        if(e1_type == 0 && e2_type == 0)
        {
            ty = 0;
        }
        else{
            if(e1_type == 0)
            {
                e1.type_convert = 2;
            }
            else if(e2_type == 0){
                e2.type_convert = 2;
            }
            ty = 1;
        }
    }
    public int getExprType(){
        int e1_type = e1.getExprType();
        int e2_type = e2.getExprType();
       // System.out.println(e1_type + " "+ op + " " +e2_type);
        if(e1_type == 0 && e2_type == 0)
        {
            ty = 0;
            return 0;
        }
        else{
            if(e1_type == 0)
            {
                e1.type_convert = 2;
            }
            else if(e2_type == 0){
                e2.type_convert = 2;
            }
            ty = 1;
            return 1;
        }

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

    public void printCODE(){
        //System.out.println("BinOpExpr");
        e1.printCODE();
        code_write(String.format("  MOVE VR(%d)@ MEM(SP@)",e1.reg_num));
        code_write(String.format("  ADD 1 SP@ SP"));
        e2.printCODE();
        code_write(String.format("  MOVE VR(%d)@ MEM(SP@)",e2.reg_num));
        code_write(String.format("  ADD 1 SP@ SP"));
        String label_true = null;
        String label_false = null;
        String label_next = null;

        if(op.equals("==") || op.equals(">=") || op.equals("<=") || op.equals("!=") ||
            op.equals("<") || op.equals(">")){
            label_true = "pos_"+Reg_offset.my_offset.label_offset;
            Reg_offset.my_offset.label_offset += 1;
            label_false = "pos_"+Reg_offset.my_offset.label_offset;
            Reg_offset.my_offset.label_offset += 1;
            label_next = "pos_"+Reg_offset.my_offset.label_offset;
            Reg_offset.my_offset.label_offset += 1;
        }
        String prefix = "";
        if(ty == 1){
            prefix = "F";
        }
        if(op.equals("+")){
            code_write(String.format("  %sADD MEM(SP@(-2))@ MEM(SP@(-1))@ VR(%d)", 
                 prefix,Reg_offset.my_offset.reg_offset));
            reg_num = Reg_offset.my_offset.reg_offset;
            Reg_offset.my_offset.add_off();
        }
        else if(op.equals("-")){
            code_write(String.format("  %sSUB MEM(SP@(-2))@ MEM(SP@(-1))@ VR(%d)", 
                 prefix,Reg_offset.my_offset.reg_offset));
            reg_num = Reg_offset.my_offset.reg_offset;
            Reg_offset.my_offset.add_off(); 
        }
        else if(op.equals("/")){
            code_write(String.format("  %sDIV MEM(SP@(-2))@ MEM(SP@(-1))@ VR(%d)", 
                 prefix,Reg_offset.my_offset.reg_offset));
            reg_num = Reg_offset.my_offset.reg_offset;
            Reg_offset.my_offset.add_off();
        }
        else if(op.equals("*")){
            code_write(String.format("  %sMUL MEM(SP@(-2))@ MEM(SP@(-1))@ VR(%d)", 
                 prefix,Reg_offset.my_offset.reg_offset));
            reg_num = Reg_offset.my_offset.reg_offset;
            Reg_offset.my_offset.add_off();
        }
        else if(op.equals("!=") || op.equals("==")){
            reg_num = Reg_offset.my_offset.reg_offset;
            code_write(String.format("  %sSUB MEM(SP@(-2))@ MEM(SP@(-1))@ VR(%d)", 
                 prefix,Reg_offset.my_offset.reg_offset));
            code_write(String.format("  JMPZ VR(%d)@ %s",
                    reg_num,label_true));
            code_write(String.format("LAB %s",label_false));
            if(op.equals("!=")){
                code_write(String.format("  MOVE 1 VR(%d)",
                Reg_offset.my_offset.reg_offset));
            }
            else{
                code_write(String.format("  MOVE 0 VR(%d)",
                Reg_offset.my_offset.reg_offset));
            }
            code_write(String.format("  JMP %s",label_next));
            code_write(String.format("LAB %s",label_true));
            if(op.equals("!=")){
                code_write(String.format("  MOVE 0 VR(%d)",
                Reg_offset.my_offset.reg_offset));
            }
            else{
                code_write(String.format("  MOVE 1 VR(%d)",
                Reg_offset.my_offset.reg_offset));   
            }
            reg_num = Reg_offset.my_offset.reg_offset;
            Reg_offset.my_offset.add_off();
            code_write(String.format("LAB %s",label_next));     
        }
        else if(op.equals("<") || op.equals(">") || 
                op.equals("<=") || op.equals(">=")){
            if(op.equals("<")){
                code_write(String.format("  %sSUB MEM(SP@(-2))@ MEM(SP@(-1))@ VR(%d)", 
                 prefix,Reg_offset.my_offset.reg_offset));
            }
            else if(op.equals(">")){
                code_write(String.format("  %sSUB MEM(SP@(-1))@ MEM(SP@(-2))@ VR(%d)", 
                 prefix,Reg_offset.my_offset.reg_offset));
            }
            else if(op.equals("<=")){
                code_write(String.format("  %sSUB MEM(SP@(-1))@ MEM(SP@(-2))@ VR(%d)", 
                 prefix,Reg_offset.my_offset.reg_offset));
            }
            else if(op.equals(">=")){
                code_write(String.format("  %sSUB MEM(SP@(-2))@ MEM(SP@(-1))@ VR(%d)", 
                 prefix,Reg_offset.my_offset.reg_offset));
            }
            reg_num = Reg_offset.my_offset.reg_offset;
            Reg_offset.my_offset.add_off();

            if(op.equals("<") || op.equals(">")){
                code_write(String.format("  JMPN VR(%d)@ %s",
                    reg_num,label_true));
            }
            else if(op.equals("<=") || op.equals(">=")){
                code_write(String.format("  JMPN VR(%d)@ %s",
                    reg_num,label_false));   
            }
            if(op.equals("<") || op.equals(">")){
                code_write(String.format("LAB %s",label_false));
                code_write(String.format("  MOVE 0 VR(%d)",
                    Reg_offset.my_offset.reg_offset));
                reg_num = Reg_offset.my_offset.reg_offset;

                code_write(String.format("  JMP %s",label_next));

                code_write(String.format("LAB %s",label_true));
                code_write(String.format("  MOVE 1 VR(%d)",
                    Reg_offset.my_offset.reg_offset));
                reg_num = Reg_offset.my_offset.reg_offset;
                Reg_offset.my_offset.add_off();

                code_write(String.format("LAB %s",label_next));
            }
            else{
                code_write(String.format("LAB %s",label_true));
                code_write(String.format("  MOVE 1 VR(%d)",
                    Reg_offset.my_offset.reg_offset));
                reg_num = Reg_offset.my_offset.reg_offset;

                code_write(String.format("  JMP %s",label_next));

                code_write(String.format("LAB %s",label_false));
                code_write(String.format("  MOVE 0 VR(%d)",
                    Reg_offset.my_offset.reg_offset));
                reg_num = Reg_offset.my_offset.reg_offset;
                Reg_offset.my_offset.add_off();

                code_write(String.format("LAB %s",label_next));
            }
        }

        code_write(String.format("  SUB SP@ 2 SP"));
    }
}

class ArgList extends Absyn {
    ArrayList<Expr> al;
    Function save_function;

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
        int list_size = al.size();
        Expr tmp;
        int ty;
        //System.out.println("arglist");
        ParamList temp_list;
        ArrayList<Type> tl = null;
        int param_type;
        Type temp_ty;   
        if (save_function != null){
            temp_list = save_function.params;
            tl = temp_list.tl;
            
        }
        for(int i = 0; i< list_size; i ++){
            tmp = al.get(i);
            tmp.printSYM(n, names,depth,is_func_comp,name_print);
            if(save_function != null){
                temp_ty = tl.get(i);
                ty = tmp.getExprType();
                param_type = temp_ty.typecheck();
                if(ty == 1 && param_type == 0){
                    tmp.type_convert = 1;
                }
                else if(ty == 0 && param_type == 1){
                    tmp.type_convert = 2;
                }
                if(ty != param_type){
                    System.out.println("Warning : " + tmp.line +":" + tmp.pos
                                 +" parameter type miss match");
                }
                //System.out.println("input: "+ty + " ori : " + param_type);
            }
         //   System.out.println("type"+ty);
        }

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
        int save_scope_var_num = Reg_offset.my_offset.scope_var_num;
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
        Reg_offset.my_offset.scope_var_num = save_scope_var_num;

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
    public void printCODE(){
        //System.out.println("decls print");
        if (decls != null){
            decls.printCODE();
        }

        //System.out.println("stmts print");
        stmts.printCODE();
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

