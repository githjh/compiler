package Absyn;

import java.util.*;

public class Program extends Absyn {
  public int pos;
  DeclList dl;
  FuncList fl;
  public Program(DeclList _dl, FuncList _fl){
  	dl = _dl;
  	fl = _fl;
    SymbolTable.setSymbolTable();

    int is_global = 1;
    if(dl != null){
      dl.printAST();

            
      dl.printSYM(0,is_global, null, null, 0, 0, 0);

           
    }
    if(fl != null){

      fl.printAST();
     // System.out.println("program before");
      fl.printSYM();
      // System.out.println("program after");
    }
  //  System.out.println("test");
  //  for(my_Symbol my_s : SymbolTable.getTable().s_table){
  //    System.out.println(my_s.name);
  //  }
	}
}
