package Shell;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

import Interpreter.Interpreter;
import processManagement.process_control_block;
import processManagement.ProcessManagment;
import processorManager.ProcessorManager;
import Communication.Communication;
import fileManagement.FileSystem;
import memoryManagement.Memory;
public class Shell 
{
	private BufferedReader UserInput;
	private String User_Command;
	boolean ScriptFlag;
	boolean WrongCFlag;
	
	private Interpreter interpreter;
	private Memory memory;
	//public static process_control_block runningProcess = new process_control_block();
	private ProcessorManager processormanager;
	private FileSystem filesystem;
	public process_control_block  maka = new process_control_block();
	private ProcessManagment processManagment;
	
	
	
	Shell() throws IOException
	{		
		memory = new Memory();
		filesystem = new FileSystem();
		processManagment = new ProcessManagment(memory);
		interpreter = new Interpreter(memory,processManagment, filesystem);
		processormanager = new ProcessorManager(interpreter, processManagment);
		ScriptFlag = false;
		WrongCFlag = false;
		UserInput = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Welcome to the TigerOS");
		while(true)
		{
			//System.out.println("HOHOHOHA");
			
			READCOMM();
			if(!(User_Command.isEmpty()) && User_Command.length() == 4 && User_Command.charAt(0) == 'e' && User_Command.charAt(1) == 'x' && User_Command.charAt(2) == 'i' && User_Command.charAt(3) == 't')
			{
				break;
			}
			if(!(WrongCFlag))
			{
			if(ScriptFlag)
			{
				SCRIPTEXEC();
			}
			
			else COMMANDEXEC();
			//System.out.println(User_Command.length());
			}
			
			ScriptFlag = false;
			WrongCFlag = false;
			//System.out.println("HOHOHOHO");
		}
		
	}
	
	
	void SCRIPTEXEC() throws IOException
	{
		
		try(BufferedReader br = new BufferedReader(new FileReader(User_Command.substring(2) + ".sch"))) 
		{
            User_Command = br.readLine();
            while(User_Command != null)
            {
                COMMANDEXEC();
                User_Command = br.readLine();
                if(!(User_Command.isEmpty()) && User_Command.length() == 4 && User_Command.charAt(0) == 'e' && User_Command.charAt(1) == 'x' && User_Command.charAt(2) == 'i' && User_Command.charAt(3) == 't')
    			{
    				break;
    			}
                
            }
		}
		catch(IOException no_f)
		{
			System.out.println("No file found");
		}
	
	}
	
	void COMMANDEXEC() throws IOException
	{
		if(User_Command.length()==2)
		{
		switch(User_Command)
		{
		    //load program
			case("p1"):
			{
				processManagment.create_process("P1", 60,  "Prog1", memory);
				break;
			}
			case("p2"):
			{
				processManagment.create_process("P2", 60, "Prog2", memory);
				break;
			}
			case("p3"):
			{
				processManagment.create_process("P3", 50, "Prog3", memory);
				break;
			}
			/*case("p4"):
			{
				processManagment.create_process("P4", 30, "Prog4", memory);
				break;
			}
			case("p5"):
			{
				processManagment.create_process("P5", 30, "Prog5", memory);
				break;
			}*/
			case("cp"):
			{
				System.out.println("Please enter the name of the process: ");
				String S1 = UserInput.readLine().trim();
				System.out.println("Please enter the size of the process: ");
				int size = Integer.parseInt(UserInput.readLine().trim());
				System.out.println("Please enter the name of the file with program: ");
				String S2 = UserInput.readLine().trim();
				processManagment.create_process(S1, size, S2, memory);
				break;
			}
			default:
			{
				System.out.println("Wrong Command");
			}	
		}
		}
		
		if(User_Command.length()==3)
		{
		switch(User_Command)
		{
			case("lsf"):
			{
				filesystem.showMainCatalog();
				break;
			}
			default:
			{
				System.out.println("Wrong Command");
			}
		}
		}
		
		if(User_Command.length() > 3)
		{
			if(User_Command.charAt(0)=='r' && User_Command.charAt(1)=='m'  && User_Command.length() > 4 && User_Command.charAt(2)=='f')
			{
				//delete file
				filesystem.deleteFile(User_Command.substring(4));
				return;
			}
			if(User_Command.charAt(0)=='c' && User_Command.charAt(1)=='f' )
			{
				//create file
				if(User_Command.charAt(3) == '-' && User_Command.length()>6)
				{
					if(User_Command.charAt(4) == 'e') filesystem.createEmptyFile(User_Command.substring(6));
					return;
				}
				else
				{
					System.out.println("Enter the content of file:");
					String content = UserInput.readLine();
					filesystem.createFile(User_Command.substring(3), content);
					return;
				}	
			}
			if(User_Command.charAt(0)=='l' && User_Command.charAt(1)=='s'  && User_Command.length() > 4 && User_Command.charAt(2)=='f')
			{
				if(User_Command.charAt(5) == 'r')
				filesystem.showRootEntry(User_Command.substring(7));
			}
		}
		
		if(User_Command.length()==4)
		{
		switch(User_Command)
		{
			case("help"):
			{
				System.out.println("Commands:");
				
				System.out.println("p1   load Prog1 as P1 with size of 60");
				System.out.println("p2   load Prog2 as P2 with size of 60");
				System.out.println("p3   load Prog3 as P3 with size of 50\n");
				
				System.out.println("cp   load program and fill in variables");
				System.out.println("cp x y z    load z as x with size of y\n");
				System.out.println("lsf   display root");
				System.out.println("lsf -r   displays specific root entry");
				
				System.out.println("\ncf  x   create file x");
				System.out.println("cf -e x   create empty file x\n");
				System.out.println("rmf x   remove file x\n");
				
				
				System.out.println("memo   display memory status");
				System.out.println("regs   display registers");
				System.out.println("step   execute one command");
				
				System.out.println("wait   execute commands until one program ends");
				System.out.println("pipe   displays all pipes");
				System.out.println("\nlsp -a   lists all processes");
				System.out.println("lsp -c   lists current process");
				System.out.println("lsp -r   lists ready processes");
				System.out.println("lsp -w   lists waiting processes\n");
				
				System.out.println("\ndisk -a   displays FAT table, bit vector, data currently on disk and free and taken memory");
				System.out.println("disk -f   displays FAT table");
				System.out.println("disk -d   displays data currently on disk");
				System.out.println("disk -b   displays bit vector");
				System.out.println("disk -m   displays the usage of the memory");
				System.out.println("disk -s   displays the content of one block\n");
				break;
			}
			//print memory
			case("memo"):
			{
				memory.print();
				break;
			}
			//show registers
			case("regs"):
			{
				interpreter.Show_Regs();
				break;
			}
			//1 step on processor
			case("step"):
			{
				processormanager.Scheduler();
				break;
			}
			case("wait"):
			{
				while(!interpreter.Flag_End)
				{
					processormanager.Scheduler();
				}
				interpreter.Flag_End = false;
				break;
			}
			case("pipe"):
			{
				Communication.showAllPipes();
				break;
			}
			default:
			{
				System.out.println("Wrong Command");
			}
		}
		}
		
		if(User_Command.length() == 6)
		{
		switch(User_Command.substring(0, 3))
		{
		//list processes
			case("lsp"):
			{
			switch(User_Command.charAt(5))
			{
				case('c'):
				{
					processormanager.showRunning();
					break;
				}
				case('r'):
				{
					processManagment.print_procesy_gotowe();
					break;
				}
				case('w'):
				{
					processManagment.print_procesy_oczekujace();
					break;
				}
				case('a'):
				{
					processManagment.print_procesy();
					break;
				}
				default:
				{
					System.out.println("Wrong Command");
					break;
				}
			}
			}	
		}
		}
		
		
		
			
		if(User_Command.length() > 7 && User_Command.charAt(0)=='d' && User_Command.charAt(1)!='p')
		{
		switch(User_Command.substring(0, 4))
		{
			case("disk"):
			{
				switch(User_Command.charAt(6))
				{
				case('f'):
				{
					//print FAT table
					filesystem.showFAT();
					break;
				}
				case('d'):
				{
					//print data on disk
					filesystem.showData();
					break;
				}
				case('b'):
				{
					//show bitVector
					filesystem.showBitVector();
					break;
				}
				case('m'):
				{
					//show memory usage
					System.out.println("Free memory: " + 32*filesystem.numberOfFreeBlocks());
					System.out.println("Memory used: " + 32*(32 - filesystem.numberOfFreeBlocks()));
					break;	
				}
				/*case('r'):
				{
					filesystem.showRootEntry(User_Command.substring(8));
					break;
				}*/
				case('s'):
				{
					int x = Integer.parseInt(User_Command.substring(8));
					filesystem.showBlockContent(x);
					break;
				}
				case('a'):
				{
					System.out.println("Free memory: " + 32*filesystem.numberOfFreeBlocks());
					System.out.println("Memory used: " + 32*(32 - filesystem.numberOfFreeBlocks()));
					filesystem.showFAT();
					filesystem.showData();
					filesystem.showBitVector();
					break;
				}
				default:
				{
					System.out.println("Wrong Command");
				}
				}
			}
		}
		}
		
		if(User_Command.length() > 7 && User_Command.charAt(0)=='c' && User_Command.charAt(1)=='p')
		{
			int index = 3;
			String S1 = " ";
			String S2 = " ";
			String S3 = " ";
			while(true)
			{										//cp ania 120 prog7
				S1 += User_Command.charAt(index);
				index++;
				if(User_Command.charAt(index) == ' ')
				{
					index++;
					break;
				}
			}
			while(true)
			{
				S2 += User_Command.charAt(index);
				index++;
				if(User_Command.charAt(index) == ' ')
				{
					index++;
					break;
				}
			}
			S3 = User_Command.substring(index);
			S1.trim();
			S2.trim();
			S3.trim();
			//System.out.println(S1.substring(1));
			int size = Integer.parseInt(S2.substring(1));
			//System.out.println(size);
			//System.out.println(S3);
			processManagment.create_process(S1.substring(1), size, S3, memory);
			
			
		}
		
		
		
	}
	
	void READCOMM() throws IOException
	{
		
		User_Command = UserInput.readLine().trim();
		if(User_Command.length() < 2)
		{
			WrongCFlag = true;
			System.out.println("Wrong");
			
		}
		else
		{String Scriptcheck = User_Command.substring(0, 2);
		if(Scriptcheck.contains("./"))
		{
			ScriptFlag = true;
		}
		else ScriptFlag = false;
		}
		
	}
	
	public static void main(String[] args)
	{
		try {
			new Shell();
		} catch(Exception excep) {
			System.out.println("Error: " + excep.getMessage() + excep);
		}
		
		
	}
	
	public static void Set_Running()
	{
		
	}
	void print()
	{
		Set_Running();
	}
}