package Shell;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

import Interpreter.Interpreter;
import processManagement.process_control_block;
import processManagement.ProcessManagment;
import memorymanagement.Memory;
import processorManager.ProcessorManager;
import Communication.Communication;
import fileManagement.FileSystem;
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
	private Communication communication;
	private FileSystem filesystem;
	public process_control_block  maka = new process_control_block();
	private ProcessManagment processManagment;
	
	
	
	Shell() throws IOException
	{		
		memory = new Memory();
		filesystem = new FileSystem();
		processManagment = new ProcessManagment(memory);
		interpreter = new Interpreter(memory,processManagment, filesystem);
		processormanager = new ProcessorManager(interpreter, processManagment.getIstniejaceProcesy());
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
			System.out.println(User_Command.length());
			}
			
			ScriptFlag = false;
			WrongCFlag = false;
			//System.out.println("HOHOHOHO");
		}
		
	}
	
	
	void SCRIPTEXEC() throws IOException
	{
		
		try(BufferedReader br = new BufferedReader(new FileReader(User_Command.substring(2) + ".txt"))) 
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
				processManagment.create_process("P1", "p1", memory);
				break;
			}
			case("p2"):
			{
				processManagment.create_process("P2", "p2", memory);
				break;
			}
			case("p3"):
			{
				processManagment.create_process("P3", "p3", memory);
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
		}
		
		if(User_Command.length()==4)
		{
		switch(User_Command)
		{
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
		
		
		
		
		
			
		if(User_Command.length() == 7)
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
		
		
		
		/*switch(User_Command.substring(0,4))
		{
		case("free"): //free (show free memory)
		{
			System.out.println("Free memory: " + 32*filesystem.numberOfFreeBlocks());
			System.out.println("Memory used: " + 32*(32 - filesystem.numberOfFreeBlocks()));
			break;
		}
		case("shom"):
		{
			break;	
		}
		case("wait"): //run the prog. from beg to end
		{
			
			break;
		}
		case("load"):
		{
			processManagment.create_process("1p", "Prog", memory);
			break;
		}
		case("step"): //make one step on proc
		{
			System.out.println("SCH");
			processormanager.Scheduler();
			break;
		}
		case("list"):
		{
			processManagment.print_procesy();
		}
		case("curr"):
		{
			processormanager.showRunning();
			break;
		}
		case("memo"):  
		{
			memory.print();
			break;
		}
		case("root"):
		{
			filesystem.showMainCatalog();
			break;
		}
		case("fatp"):
		{
			filesystem.showFAT();
			break;
		}
		case("disk"):
		{
			filesystem.showData();
			break;
		}
		case("newf"):
		{
			filesystem.createEmptyFile("PUSTY");
			break;
		}
		case("wrtf"):
		{
			filesystem.openFile("PUSTY");
			filesystem.readFile("PUSTY");
			filesystem.appendToFile("PUSTY", "EAEAEAEAEAEEAEAEAEAE");
			filesystem.readFile("PUSTY");
			filesystem.closeFile("PUSTY");
			break;
		}
		default:
		{
			
		}
	}*/	
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
		} catch(Exception io) {
			System.out.println(io.getMessage());
			System.out.println("im overloaded!");
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

