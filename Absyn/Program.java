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

    code_write(String.format("MOVE %d SP", Reg_offset.my_offset.global_offset));
    code_write(String.format("MOVE %d FP", Reg_offset.my_offset.global_offset));
    
    String label_exit = "EXIT";
    code_write(String.format("  MOVE %s MEM(SP@)",label_exit));
    
    code_write("  ADD 1 SP@ SP");
    code_write("  MOVE FP@ MEM(SP@)");
    code_write("  ADD 1 SP@ SP");
    code_write("  MOVE SP@ FP");
    //code_write("  ADD 1 SP@ SP");
    
    
    code_write("JMP main");
    
    String print_function = ("LAB printf \n"
                            +"  MOVE  FP@ SP\n"
                            +"  WRITE MEM(SP@(-3))@\n"
                            +"  MOVE  MEM(SP@(-1))@ FP\n"
                            +"  JMP   MEM(SP@(-2))@\n");
    String scanf_function = ("LAB scanf\n"
                            +"  MOVE  FP@ SP\n"
                            +"  READI MEM(MEM(SP@(-3))@)\n"
                            +"  MOVE MEM(SP@(-1))@ FP\n"
                            +"  JMP MEM(SP@(-2))@\n");
    String scanf_function_f = ("LAB scanf_f\n"
                            +"  MOVE  FP@ SP\n"
                            +"  READF MEM(MEM(SP@(-3))@)\n"
                            +"  MOVE MEM(SP@(-1))@ FP\n"
                            +"  JMP MEM(SP@(-2))@\n");
    
    //System.out.println(Reg_offset.my_offset.reg_offset);
    Reg_offset.my_offset.add_off();
    if(fl != null){
      fl.printCODE();
    }
    code_write(String.format("  SUB SP@ %d SP",2));
    code_write(print_function);
    code_write(scanf_function);
    code_write(scanf_function_f);
    code_write("LAB EXIT");
    
    code_write("LAB END");

	}
}
