package Shell;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import Interpreter.Interpreter;
import processManagement.process_control_block;
import processManagement.lista_procesow;
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
		System.out.println(maka.getLicznik_rozkazow());
		ScriptFlag = false;		
		UserInput = new BufferedReader(new InputStreamReader(System.in));
		while(true)
		{
			//System.out.println("HOHOHOHA");
			
			READCOMM();
			
			if(ScriptFlag)
			{
				SCRIPTEXEC();
			}
			
			else COMMANDEXEC();
			
			
			ScriptFlag = false;
			//System.out.println("HOHOHOHO");
		}
		
	}
	
	
	void SCRIPTEXEC()
	{
		System.out.println("HAHAHAHAHAAHAHAHAHAHAHAHAHAHAHAHAHAHSDSAFAGFW");
	}
	
	
	void COMMANDEXEC()
	{
		System.out.println("COMMMAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAND");
		switch(User_Command.substring(0,4))
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
		case("regl"):
		{
			interpreter.Show_Regs();
			break;
		}
		default:
		
		{
			System.out.println("brak danego rozkazu");
			break;
		}
	}
		
		
	}
	
	void READCOMM() throws IOException
	{
		
		User_Command = UserInput.readLine().trim();
		String Scriptcheck = User_Command.substring(0, 2);
		if(Scriptcheck.contains("./"))
		{
			ScriptFlag = true;
			System.out.println(Scriptcheck);
		}
		else ScriptFlag = false;
		System.out.println(User_Command);
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

