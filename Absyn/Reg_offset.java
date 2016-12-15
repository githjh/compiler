package Absyn;

class Reg_offset{
	int reg_offset;
	int global_offset;
	int label_offset;
	static Reg_offset my_offset;

	public Reg_offset(){
		set_off(0);
		global_offset = 0;
		label_offset = 0;
	}
	public static void make_Reg_off(){
		my_offset = new Reg_offset();
	}
	public  void set_off(int _offset){
		reg_offset = _offset;
	}
	public  int get_off(){
		return reg_offset;
	}
	public  void add_off(){
		reg_offset = reg_offset +1;
	}
}