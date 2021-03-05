/**
  * Main Driver that transfer process to the method that does all the work
  *  
  */

public class Main{
	static {
		  boolean assertsEnabled = false;
		  assert assertsEnabled = true; // Intentional side effect!!!
		  if (!assertsEnabled)
			  throw new RuntimeException("Asserts must be enabled!!!");
		 } 
	public static  void main(String[] clArgs) { 
        // Launch the main process
        new JUtils_Tester(clArgs);
	}	
	
}

