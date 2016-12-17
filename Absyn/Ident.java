package Absyn;

import java.util.*;

class Ident extends Absyn 
{
    String name;
    int isArray;
    String ar_num;
    int line;
    int pos;
    Boolean isGlobal;
    Boolean isParam;
 
    my_Symbol save_symbol;
    public Ident(int isA, String n, String array_num, int _line, int _pos) 
    {
        name = n;
        isArray = isA;
        ar_num = array_num;
        line = _line;
        pos = _pos;
        isGlobal = false;
        isParam = false;
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

        Type this_type = null;
         my_Symbol find_s;
        if(ty == 0)
        {
            this_type = new Type(0);
        }
        else if(ty == 1){
            this_type = new Type(1);
        }
        else if(ty == 100){
            //this is for switch case
            find_s = SymbolTable.find(name);
            save_symbol = find_s;
            if(find_s == null)
            System.out.println("SYMENTIC ERROR "+line+":"+pos
                +" note: "+name + " is not declared");
            return 1;
        }
        my_Symbol my_s = new my_Symbol(this_type, name, false, scope_level, line, pos);
        find_s = SymbolTable.find(name);
        if(find_s != null)
            System.out.println("SYMENTIC ERROR "+line+":"+pos
                +" note: previous declaration of "+name
                +" : " + find_s.getLine()+":" +find_s.getPos());
        if(isArray == 1){
            my_s.setisArray(true);
        }
        my_s.isGlobal = isGlobal;
        if(isGlobal){
            my_s.offset = Reg_offset.my_offset.global_offset;
            if(isArray == 1){
                //System.out.println("reg global_offset : " + my_s.offset);
                Reg_offset.my_offset.global_offset += Integer.parseInt(ar_num);
            }
            else{
                Reg_offset.my_offset.global_offset += 1;   
            }
            //System.out.println(my_s.offset);
        }
        //local variable 
        else if(isval == 0){
            my_s.offset = Reg_offset.my_offset.scope_var_num;
            //System.out.println(my_s.offset);
            if(isArray == 1){
                Reg_offset.my_offset.scope_var_num += Integer.parseInt(ar_num);
            }
            else{
                Reg_offset.my_offset.scope_var_num += 1;
            }
            
        }
        else{
            isParam = true;
            my_s.offset = Reg_offset.my_offset.param_num;
            Reg_offset.my_offset.param_num += 1;
            my_s.isParam = isParam;
            //System.out.println(my_s.name);
        }

        SymbolTable.addSymbol(my_s);
        save_symbol = my_s; 


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
