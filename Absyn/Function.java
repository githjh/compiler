package Absyn;

import java.util.*;

public class Function extends Absyn 
{
    Type t;
    String name;
    ParamList params;
    CompStmt cs;
    ArrayList<String>  stmt_names;
    ArrayList<Integer> depth;
    public Function(Type _t, String fn, ParamList pl, CompStmt _cs) 
    {
        t = _t;
        name = fn;
        params = pl;
        cs = _cs;
    }
    public void printAST()
    {
        t.printAST();
        myPrint.astWriter.write(name);
        myPrint.astWriter.write("(");
        if(params != null){
            params.printAST();
        }
        myPrint.astWriter.write(")");
        cs.printAST();
    }
    public void printSYM()
    {
        int start_num = 1;
        stmt_names = new ArrayList<String>();
        depth = new ArrayList<Integer>();
        //myPrint.symWriter.write("Function name :");
        //t.printSYM(1);
        //myPrint.symWriter.write(name);
        //myPrint.symWriter.write("\r\n");
        //System.out.println("function before");
        if(params != null){
            start_num = params.printSYM(start_num, 99);
        }

        //System.out.println("function after");
        stmt_names.add(name);
        depth.add(0);
  //      myPrint.symWriter.write(")");
        // fuction scope level start from 1
        cs.printSYM(start_num, stmt_names, depth, 1, 0, 0);
    }
    public String getname()
    {
        return name;
    }
}

