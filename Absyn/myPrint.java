package Absyn;

import java.io.*;


public class myPrint {
  public int pos;
  private Writer writer;
  static myPrint astWriter;
  static myPrint symWriter;
  public myPrint(Writer writer){
  	this.writer = writer;
  }
  
  public static void setASTWriter(Writer writer){
  	astWriter = new myPrint(writer);
  }
  public static void setSYMWriter(Writer writer){
    symWriter = new myPrint(writer);
  }

  public void write(String s)
  {
	  try{
		  writer.write(s);
	  }
	  catch(IOException e){
		  System.out.println("ioerror");  
	  }
  }
}

