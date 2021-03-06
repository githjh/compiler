package Absyn;

import java.util.*;

public class FuncList extends Absyn 
{
    ArrayList<Function> fl;

    public FuncList() 
    {
        fl = new ArrayList<Function>();
    }

    public FuncList append(Function f) 
    {
        fl.add(f);
        return this;
    }
    public void printAST()
    {
        for(Function f: fl)
        {
            f.printAST();
        }
    }
    public void printSYM()
    {
        for(Function f: fl)
        {
            String f_name = f.getname();
            myPrint.symWriter.write("Function name :");
            myPrint.symWriter.write(f_name);
            myPrint.symWriter.write("\r\n");
            myPrint.symWriter.write("count   Type   name   array       role\r\n");
            SymbolTable.addFunction(f);
            f.printSYM();
            
            Reg_offset.my_offset.param_num = 0;
            
        }
        //ArrayList<Function> func_table = SymbolTable.getTable().f_table;
        //for(Function func : func_table ){
        //  System.out.println(func.getname());
        //  
        //}
    }
    public void printCODE(){
        for(Function f: fl){
            f.printCODE();
        }
    }
}

