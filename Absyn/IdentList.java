package Absyn;

import java.util.*;

public class IdentList extends Absyn 
{
    ArrayList<Ident> il;
    public IdentList() 
    {
        il = new ArrayList<Ident>();
    }

    public IdentList append(Ident id, int comma) 
    {
        il.add(id);
        return this;
    }
    public void printAST(){
        int list_size = il.size();
        int temp = 0;
        for(Ident id: il)
        {
            id.printAST();
            temp ++;
            if(temp != list_size)
                myPrint.astWriter.write(", ");
        }
    }
    public int printSYM(int n, int _ty, int isval, int scope_level){
        int list_size = il.size();
        int temp = 0;
        int start_num = n;
        int ty = _ty;
        for(Ident id: il)
        {
            //System.out.println("identlist before");
            start_num = id.printSYM(start_num, ty, isval, scope_level);

//            System.out.println("identlist after");
        }
        return start_num;
    }
}

