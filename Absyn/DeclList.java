package Absyn;

import java.util.*;

public class DeclList extends Absyn{

	ArrayList<Decl> dl;
	int num_local;
	public DeclList()
	{
		dl = new ArrayList<Decl>();
		num_local = 0;
	}
	public DeclList append(Decl d)
	{
		dl.add(d);
		return this;
	}
	public void printAST()
	{
		for(Decl d : dl){
			d.printAST();
		}
	}
	public int  printSYM(int n, int is_global, 
		ArrayList<String> names, ArrayList<Integer> depth, 
		int is_func_comp, int name_print, int scope_level)
	{
		String result;
		int start_num;
		if(is_global == 1)
		{
			start_num = 1;
			myPrint.symWriter.write("Function name: Global \r\n");
			myPrint.symWriter.write("count   Type   name   array       role\r\n");
		}
		else if(name_print == 1){
			start_num = n;
			myPrint.symWriter.write("Function name:");
			int list_size = names.size();
			int dep = 0;
			String name;
            for (int i = 0; i<list_size; i++){
                name = names.get(i);
                myPrint.symWriter.write(name);
                dep = depth.get(i);
                if(dep != 0)
                {
                    myPrint.symWriter.write("("+dep+")");
                }
                if(i != list_size -1)
                    myPrint.symWriter.write(" - ");
            }
            myPrint.symWriter.write("\r\n");
			myPrint.symWriter.write("count   Type   name   array       role\r\n");
		}
		else{
			start_num = n;
		}

		for(Decl d : dl){
			if(is_global == 1){
				d.isGlobal = true;
			}
			else{

			}
			start_num = d.printSYM(start_num, scope_level);
		}
		num_local = Reg_offset.my_offset.scope_var_num;
		
		myPrint.symWriter.write("\r\n");
		return start_num;
	}
	public void printCODE(){
		//code_write("//DeclList");
		code_write(String.format("  ADD %d SP@ SP",num_local));
	}
	
}
