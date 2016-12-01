package Absyn;

import java.util.*;

public class ParamList extends Absyn 
{
    ArrayList<Type> tl;
    ArrayList<Ident> il;

    public ParamList(Type t, Ident id) {
        tl = new ArrayList<Type>();
        il = new ArrayList<Ident>();
    }

    public void append(Type t, Ident id) {
        tl.add(t);
        il.add(id);
    }
    public void printAST()
    {
        int list_size = tl.size();
        int i = 0;
        Type ty;
        Ident id;
        for (i = 0; i<list_size; i++){
            ty = tl.get(i);
            ty.printAST();
            id = il.get(i);
            id.printAST();
            if(i != list_size-1)
                myPrint.astWriter.write(", ");
        }
    }
    public int printSYM(int n,int scope_level)
    {
        int list_size = tl.size();
        int start_num = n;
        int i = 0;
        Type ty;
        int ty_i = 0;
        Ident id;
        int isval = 1;
        for (i = 0; i<list_size; i++){
            ty = tl.get(i);
         //   ty.printSYM(1);
            ty_i = ty.typecheck();
            id = il.get(i);
            start_num = id.printSYM(start_num,ty_i,isval, scope_level);
        //    if(i != list_size-1)
        //        myPrint.symWriter.write(", ");
        }
        return start_num;
    }
}

