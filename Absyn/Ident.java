package Absyn;

import java.util.*;

class Ident extends Absyn 
{
    String name;
    int isArray;
    String ar_num;
    public Ident(int isA, String n, String array_num) 
    {
        name = n;
        isArray = isA;
        ar_num = array_num;
    }
    public void printAST()
    {
        if(isArray == 0)
            myPrint.astWriter.write(name);
        else if(isArray == 1){
            myPrint.astWriter.write(name+"["+ar_num+"]");
        }

    }
    public int printSYM(int n, int ty, int isval, int scope_level)
    {
        String result;
        String s_name;
        String count;
        String i_type;

        Type this_type;
        if(ty == 0)
        {
            this_type = new Type(0);
        }
        else{
            this_type = new Type(1);
        }
        my_Symbol my_s = new my_Symbol(this_type, name, false, scope_level);
        if(SymbolTable.find(name) != null)
            System.out.println("previous declaration :"+name);
        if(isArray == 1){
            my_s.setisArray(true);
        }
        SymbolTable.addSymbol(my_s);

        //System.out.println(name);
        
        if(ty == 0)
            i_type = String.format("%7s","int");
        else if (ty ==  1)
            i_type = String.format("%7s","float");
        else
            i_type = "     ";

        count = String.format("%5d",n);
        s_name = String.format("%7s",name);
        
        myPrint.symWriter.write(count);
        myPrint.symWriter.write(i_type);
        myPrint.symWriter.write(s_name);
        if(isArray == 0){
            myPrint.symWriter.write("        ");
        }
        else if(isArray == 1){
            result =  String.format("%8s", ar_num);
            myPrint.symWriter.write(result);
        }
        if(isval == 0){
            myPrint.symWriter.write("   variable\r\n");
        }
        else if(isval == 1)
        {
            myPrint.symWriter.write("  parameter\r\n");
        }
        return n+1;
    }

}