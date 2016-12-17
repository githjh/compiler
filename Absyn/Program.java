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

    Reg_offset.make_Reg_off();

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
    // code writer

    code_write("AREA SP");
    code_write("AREA FP");
    code_write("AREA MEM");

    code_write("AREA VR");
    code_write("LAB START");

    


    //System.out.println(Reg_offset.my_offset.global_offset);
    code_write(String.format("MOVE %d SP", Reg_offset.my_offset.global_offset));
    code_write(String.format("MOVE %d FP", Reg_offset.my_offset.global_offset));
    
    code_write("JMP main");
    
    String print_function = ("LAB printf \n"
                            +"  WRITE MEM(SP@(-3))@\n"
                            +"  MOVE  MEM(SP@(-1))@ FP\n"
                            +"  JMP   MEM(SP@(-2))@\n");
    String scanf_function = ("LAB scanf\n"
                            +"  READI MEM(MEM(SP@(-3))@)\n"
                            +"  MOVE MEM(SP@(-1))@ FP\n"
                            +"  JMP MEM(SP@(-2))@\n");
    code_write(print_function);
    code_write(scanf_function);
    //System.out.println(Reg_offset.my_offset.reg_offset);
    Reg_offset.my_offset.add_off();
    if(fl != null){
      fl.printCODE();
    }

    code_write("LAB EXIT");
    code_write("LAB END");


    //System.out.println("test2");
    
  //  System.out.println("test");
  //  for(my_Symbol my_s : SymbolTable.getTable().s_table){
  //    System.out.println(my_s.name);
  //  }
	}
}
