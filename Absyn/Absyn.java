package Absyn;

import java.util.*;
import java.io.*;

class my_Symbol {
	int scope_level;
	Type ty;
	String name;
	boolean isParam;
	boolean isArray;
	int line;
	int pos;
	public my_Symbol(Type _ty, String _name, boolean _isParam,
	 int _scope_level, int _line, int _pos)
	{
		ty = _ty;
		name = _name;
		isParam = _isParam;
		scope_level = _scope_level;
		isArray = false;
		line = _line;
		pos = _pos;
	}
	
	public String getName()
	{
		return name;
	}
	public int getType()
	{
		return ty.typecheck();
	}
	public void setisArray(boolean _isArray)
	{
		isArray = _isArray;
	}
	public boolean getisArray()
	{
		return isArray;
	}
	public int getLine(){
		return line;
	}
	public int getPos(){
		return pos;
	}

}

class SymbolTable{

	private static SymbolTable whole_table;

	ArrayList<my_Symbol> s_table;
	ArrayList<Function> f_table;
	public SymbolTable(){
		s_table = new ArrayList<my_Symbol>();
		f_table = new ArrayList<Function>();
	}

	static public SymbolTable getTable(){
		return whole_table;
	}
	static public void setSymbolTable(){
		 whole_table = new SymbolTable();
	}

	public static void addSymbol (my_Symbol _symbol){
		SymbolTable.getTable().s_table.add(_symbol);
	}

	public static void addFunction (Function _func){
		SymbolTable.getTable().f_table.add(_func);
	}

	public static my_Symbol find(String name)
	{
		ArrayList<my_Symbol> sym_table = SymbolTable.getTable().s_table;
		for(my_Symbol my_s : sym_table ){
			if(my_s.getName().equals(name)){
				return my_s;
			}
		}
		return null;
	}
	public static Function find_func(String name){
		ArrayList<Function> func_table = SymbolTable.getTable().f_table;
		for(Function func : func_table ){
			if(func.getname().equals(name)){
				return func;
			}
		}
		return null;
	}
	static public void removeTABLE(int scope_level){
		ArrayList<my_Symbol> temp = SymbolTable.getTable().s_table;
		int table_size = temp.size();
		my_Symbol my_s;
		for(int i = table_size -1; i >=0; i --){
			my_s = temp.get(i);

//			System.out.println("remove Table :"+my_s.name+" scope_level :"+my_s.scope_level);
			//System.out.println("scope_level" +scope_level);

			if( my_s.scope_level == scope_level){
			//	System.out.println("remove Table remove");	
				temp.remove(i);
			}
			else{
			//	System.out.println("remove Table break");
				break;
			}
			
		}
	}
}

public class Absyn  {
	public int pos;
	ArrayList<String> test;

	public Absyn(){
	}
}
