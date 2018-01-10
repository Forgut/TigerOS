package Interpreter;


import java.util.ArrayList;
import memorymanagement.Memory;
import fileManagement.FileSystem;
import Communication.Communication;
import processManagement.ProcessManagment;
import processManagement.process_control_block;
import memorymanagement.Memory;
public class Interpreter {

	private int Reg_A=0, Reg_B=0, Reg_C=0;
	//private bool Flag_E = 0;		//Flaga do bledu wykonywania rozkazu
	private Processor processor;
	private Memory memory;
	private Communication communication;
	private ProcessManagment manager;
	private FileSystem filesystem;
	//private PCB PCB_b; 			//Zmienna do kopii PCB procesu
	private int CMDCounter; 	//Licznik rozkazu do czytania z pami ci
	private int CCKCounter;		//licznik do sprawdzania czy program siê skoñczy³
	
//-------------------------------------------------------------------------------------------------------------------
	
	public Interpreter(Memory memory, ProcessManagment manager, FileSystem filesystem) {					//Memory memory, bez tego
		this.memory=memory;
		this.manager=manager;
		this.filesystem=filesystem;
		processor=new Processor();
	}
	
//-------------------------------------------------------------------------------------------------------------------
	
	public int RUN(process_control_block Running) {
		//PCB_b=Running.GetPCB();		 //Wczytanie PCB procesu
		
		communication = new Communication(Running); //nie u¿ywna zmienna komunikacji
		
		
		
		CCKCounter = 0;
		CMDCounter = Running.getLicznik_rozkazow(); //Pobieranie licznika rozkar w
		//System.out.println(CMDCounter + " stan licznika");
		this.Reg_A = Running.getR1(); //Pobieranie stanu rejestru A
		this.Reg_B = Running.getR2(); //Pobieranie stanu rejestru B
		this.Reg_C = Running.getR3(); //Pobieranie stanu rejestru C
		
		processor.Set_A(Reg_A);			 //Ustawianie wartosci rejestru A do pami ci
		processor.Set_B(Reg_B);			 //Ustawianie wartosci rejestru B do pami ci
		processor.Set_C(Reg_C);			 //Ustawianie wartosci rejestru C do pami ci
		
		String Instruction = "";
		
		Instruction = GetInstruction(Running);	 //Zmienna pomocnicza do  adowania instrukcji z pami ci
		//System.out.println(Instruction.length());
		Running.SetLicznik_rozkazow(Running.getLicznik_rozkazow()+Instruction.length());
		Execute(Instruction,Running);
		System.out.println(CMDCounter + " stan licznika");
		ReturnToPCB(Running);
		//Running.SetPCB();
		return 0;
	}
	
//-------------------------------------------------------------------------------------------------------------------
	
	void Execute(String Instruction,process_control_block Running) {
		int x = 0;	//takie co  do sprawdzania czy by a spacja
		int i = 1; 	//licznik do podzialu rozkazu na segmenty
		String CMD = "";
		String P1 = "";
		String P2 = "";
		String P3 = "";
		
//-----------------------------------------------------------------------
		
		while(i < 5) {
			if(i == 1) {
				while(Instruction.charAt(x)!=' ' && Instruction.charAt(x)!=',' && Instruction.charAt(x)!=';') {
					System.out.println("komenda");
					CMD += Instruction.charAt(x);
					CCKCounter++;
					x++;
				
				}
				if(Instruction.charAt(x)==' '){
					i++;
					x++;
				}
				else if(Instruction.charAt(x)==','){
					break;
				}
				else if(Instruction.charAt(x)==';'){
					System.out.println("srednik");
					break;
				}
			}
			else if(i == 2) {
				while(Instruction.charAt(x)!=' ' && Instruction.charAt(x)!=',' && Instruction.charAt(x)!=';') {
					P1 += Instruction.charAt(x);
					CCKCounter++;
					x++;
				}
				if(Instruction.charAt(x)==' '){
					i++;
					x++;
				}
				else if(Instruction.charAt(x)==','){
					break;
				}
				else if(Instruction.charAt(x)==';'){
					break;
				}
			}
			else if(i == 3) {
				while(Instruction.charAt(x)!=' ' && Instruction.charAt(x)!=',' && Instruction.charAt(x)!=';') {
					P2 += Instruction.charAt(x);
					CCKCounter++;
					x++;
				}
				if(Instruction.charAt(x)==' '){
					i++;
					x++;
				}
				else if(Instruction.charAt(x)==','){
					break;
				}
				else if(Instruction.charAt(x)==';'){
					break;
				}
			}
			else if(i == 4) {
				while(Instruction.charAt(x)!=' ' && Instruction.charAt(x)!=',' && Instruction.charAt(x)!=';') {
					P3 += Instruction.charAt(x);
					CCKCounter++;
					x++;
				}
				CCKCounter++;
				i++;
			}
			else {
				break;
				}
			
		}
		Boolean What = false;
		CCKCounter++;
		if( !P2.isEmpty()) {What = CheckP2(P2);}		
//-----------------------------------------------------------------------	ARYTMETYKA
		
		switch (CMD) {
		case "AD": // Dodawanie warto ci
			if (What) {
				processor.SetValue(P1, GetValue(P1) + GetValue(P2));
			} else {
				processor.SetValue(P1, GetValue(P1) + Integer.parseInt(P2));
			}
			break;

		case "SB": // Odejmowanie warto ci
			if (What) {
				processor.SetValue(P1, GetValue(P1) - GetValue(P2));
			} else {
				processor.SetValue(P1, GetValue(P1) - Integer.parseInt(P2));
			}
			break;
			
		case "ML": // Mno enie warto ci
			if (What) {
				processor.SetValue(P1, GetValue(P1) * GetValue(P2));
			} else {
				processor.SetValue(P1, GetValue(P1) * Integer.parseInt(P2));
			}
			break;

		case "MV": // Umieszczenie warto ci
			if (What) {
				processor.SetValue(P1, GetValue(P2));
			} else {
				processor.SetValue(P1, Integer.parseInt(P2));
			}
			break;

//-----------------------------------------------------------------------	PLIKI
		
		case "CE": // Tworzenie pliku
			if(filesystem.createEmptyFile(P1)==1) {
				//filesystem.createEmptyFile(P1);
			}
			else {
				Running.Setstan(2);
			}
			break;
			
		case "CF": // Tworzenie pliku z zawartoscia
			if (What) {
				if(filesystem.createFile(P1,Integer.toString(GetValue(P2)))==1) {
					//filesystem.createFile(P1,Integer.toString(GetValue(P2)));
				}
				else{
					Running.Setstan(2);
				}
			} else {
				if(filesystem.createFile(P1, P2)==1) {
					//filesystem.createFile(P1, P2);
				}
				else {
					Running.Setstan(2);
				}
			}
			break;
			
		case "WF": // Dopisanie do pliku
			//filesystem.openFile(P1,Running);
			if (What) {
				if(filesystem.appendToFile(P1,Integer.toString(GetValue(P2)))==1){
					//filesystem.appendToFile(P1,Integer.toString(GetValue(P2)));
				}
				else {
					Running.Setstan(2);
				}
			} 
			else {
				if(filesystem.appendToFile(P1,P2)==1) {
					//filesystem.appendToFile(P1, P2);
				}
				else {
					Running.Setstan(2);
				}
			}
			filesystem.closeFile(P1);
			break;
			
		case "DF": // Usuwanie pliku
			filesystem.openFile(P1,Running);
			if((filesystem.deleteFile(P1))==1) {
				//filesystem.deleteFile(P1);
			}
			else {
				Running.Setstan(2);
			}
			break;
		
		case "RF": // Czytanie pliku
			filesystem.openFile(P1,Running);
			filesystem.readFile(P1);
			break;
			
		case "RN": // Czytanie n znakow z pliku
			filesystem.openFile(P1,Running);
			filesystem.readFile(P1,GetValue(P2));
			break;
			
//-----------------------------------------------------------------------	JUMPY I KONCZENIE
			
		case "JP": // Skok do rozkazu
			CMDCounter = Running.getnumery_rozkazow(P1);
			break;
			
		case "JX": // Skok do rozkazu, je li rejestr != 0
			if(GetValue(P1)!=0) {
				CMDCounter = Running.getnumery_rozkazow(P2);// + Running.pageTable.getVirtualBase();
			}
			break;

		case "EX":
			System.out.println("exitentah");// Koniec programu
			Running.Setstan(2);
			break;	

//-----------------------------------------------------------------------	PROCESY
	
		case "XR": // czytanie komunikatu;		
			int l=communication.readPipe(P1, Integer.parseInt(P2), Integer.parseInt(P3));
			if(l==1){
				//communication.readPipe(P1, Integer.parseInt(P2), Integer.parseInt(P3));
				int od= Integer.parseInt(P3);
				int ile = Integer.parseInt(P2);
				String pom = "";
				do {
					pom+=Running.pageTable.readFromMemory(od,Running.getID());
					od++;
				}while (pom.length()!=ile);
				processor.SetValue("B", Integer.parseInt(pom));
			}
			else if(l==2){
				CMDCounter=12;
			}
			else {
				Running.Setstan(2);
				}
			break;
	
		case "XS": // -- Wys anie komunikatu;
			if(communication.writePipe(P1, P2)==1) {
				//communication.writePipe(P1, P2);
			}
			else {
				Running.Setstan(2);
			}
			break;
	/*
		case "XF": // -- znalezienie ID procesu (P1);
			processor.Set_A(manager.GetIDwithName(P1));
			break;
	*/		
		case "XP": // -- Stworzenie potoku
			if(communication.createPipe(P1)==1) {
				//communication.createPipe(P1);
			}
			else {
				Running.Setstan(2);
			}
			break;
			
		case "XE": // -- Usuwanie potoku
			if(communication.deletePipe(P1)==1) {
				//communication.deletePipe(P1);
			}
			else {
				Running.Setstan(2);
			}
			break;
	
		case "XC": // -- tworzenie procesu (P1,P2);
			if(manager.create_process(P1,P2,memory)==1) 
			{
				System.out.println("Process has been created");  //
			}
			else {
				Running.Setstan(2);
			}
			break; 
		
		case "XZ": // -- wstrzymanie procesu
			Running.Setstan(1);
			break;
		}
	
		}	


//-------------------------------------------------------------------------------------------------------------------
	
	private boolean CheckP2(String P2) {
		if(P2.charAt(0) == 'A' || P2.charAt(0) == 'B' || P2.charAt(0) == 'C') {
			return true;
		}
		else {
			return false;
		}
	}
	
//-------------------------------------------------------------------------------------------------------------------
	
	private void ReturnToPCB(process_control_block Running) {
			Running.SetR1(processor.Get_A());
			Running.SetR2(processor.Get_B());
			Running.SetR3(processor.Get_C());
			
			Running.SetLicznik_rozkazow(CMDCounter);
	}
	
//-------------------------------------------------------------------------------------------------------------------
	
	private String GetInstruction(process_control_block Running) {
		String Instruction = " ";
		int Counter=0;
		Running.add_numer(CMDCounter);
		do{ 
			Instruction += Running.pageTable.readFromMemory(CMDCounter,Running.getID()); //pobieranie z pami ci znaku o danym numerze, oraz nale  cego do danego procesu
			
			CMDCounter++;
			Counter++;
		}while (Instruction.charAt(Counter)!=',' && Instruction.charAt(Counter)!=';');
		Instruction = Instruction.trim();
		System.out.println(Instruction);
		return Instruction;
	}
	
//-------------------------------------------------------------------------------------------------------------------
	
	private int GetValue(String P1) {
		switch (P1) {
		case "A":
			return processor.Get_A();
		case "B":
			return processor.Get_B();
		case "C":
			return processor.Get_C();
		}
		return 0;
	}
	
//-------------------------------------------------------------------------------------------------------------------
	
	public void Show_Regs() {
		System.out.println("Register A: " + processor.Get_A());
		System.out.println("Register B: " + processor.Get_B());
		System.out.println("Register C: " + processor.Get_C());
		System.out.println("Command counter: " + this.CMDCounter);
	}

}