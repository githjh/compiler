package Absyn;
import java.util.*;

public class Decl extends Absyn {

    Type t;
    IdentList il;
    Boolean isGlobal;
	public Decl(Type ty, IdentList id)
	{
		t = ty;
		il = id;
		isGlobal = false;
	}
	public void printAST()
	{
		t.printAST();
		il.printAST();
		myPrint.astWriter.write(";\r\n");
	}
	public int printSYM(int n, int scope_level)
	{
		int start_num = n;
		int ty = t.typecheck();
		int isval = 0;
		//t.printSYM(start_num);
		il.isGlobal = isGlobal;
		start_num = il.printSYM(start_num, ty , isval, scope_level);
		//myPrint.symWriter.write(";\r\n");
		return start_num;
	}
	
	
}
