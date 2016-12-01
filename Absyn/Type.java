package Absyn;

import java.util.*;

class Type extends Absyn 
{
    int ty;
    public Type(int ty_info) 
    {
    	ty = ty_info;
    }
    public void printAST()
    {
    	if(ty == 0)
	    	myPrint.astWriter.write("int ");
	    else if (ty == 1)
	    	myPrint.astWriter.write("float ");
    }
    public int printSYM(int n)
    {
        if(ty == 0){
            myPrint.symWriter.write("int ");
        }
        else if (ty == 1){
            myPrint.symWriter.write("float ");
        }
        return n;
    }
    public int typecheck(){
        return ty;
    }
}

