import java.util.ArrayList;

public class Interpreter {

	private int Reg_A=0, Reg_B=0, Reg_C=0;
	private bool Flag_E = 0;		//Flaga do bledu wykonywania rozkazu
	private Processor processor;
	private Memory memory;
	private Communication communication;
	private ProcessManagment manager;
	private Filesystem filesystem;
	//private PCB PCB_b; 			//Zmienna do kopii PCB procesu
	private int CMDCounter; 	//Licznik rozkazu do czytania z pami�ci
	private int CCKCounter;		//licznik do sprawdzania czy program się skończył
	
//-------------------------------------------------------------------------------------------------------------------
	
	public Interpreter(Memory memory, ProcessManagment manager, Filesystem filesystem, Communication communication) {
		this.memory=memory;
		this.manager=manager;
		this.filesystem=filesystem;
		this.communication=communication;
		processor=new Processor();
	}
	
//-------------------------------------------------------------------------------------------------------------------
	
	public int RUN(Process Running) {
		//PCB_b=Running.GetPCB();		 //Wczytanie PCB procesu
		
		CCKCounter = 0;
		CMDCounter = Running.getLicznik_rozkazow(); //Pobieranie licznika rozkar�w
		
		this.Reg_A = Running.getR1(); //Pobieranie stanu rejestru A
		this.Reg_B = Running.getR2(); //Pobieranie stanu rejestru B
		this.Reg_C = Running.getR3(); //Pobieranie stanu rejestru C
		
		processor.Set_A(Reg_A);			 //Ustawianie wartosci rejestru A do pami�ci
		processor.Set_B(Reg_B);			 //Ustawianie wartosci rejestru B do pami�ci
		processor.Set_C(Reg_C);			 //Ustawianie wartosci rejestru C do pami�ci
		
		String Instruction = "";
		do {
			Instruction = GetInstruction();	 //Zmienna pomocnicza do �adowania instrukcji z pami�ci
			Execute(Instruction);
		}while(Instruction.charAt(CCKCounter) != ';' && Instruction.charAt(CCKCounter) != ',' && Running.Getstan()!=2 && Running.Getstan()!=1);
		
		ReturnToPCB();
		Running.SetPCB(PCB_b);
		return 0;
	}
	
//-------------------------------------------------------------------------------------------------------------------
	
	void Execute(String Instruction) {
		int x = 0;	//takie co� do sprawdzania czy by�a spacja
		int i = 1; 	//licznik do podzialu rozkazu na segmenty
		String CMD = "";
		String P1 = "";
		String P2 = "";
		
//-----------------------------------------------------------------------
		
		while(i < 4) {
			if(i == 1) {
				while(Instruction.charAt(x)!=' ' && Instruction.charAt(x)!=',' && Instruction.charAt(x)!=';') {
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
				CCKCounter++;
				i++;
			}
			else {
				break;
				}
			
		}
		CCKCounter++;
	
		Boolean What = CheckP2(P2);
		
//-----------------------------------------------------------------------	ARYTMETYKA
		
		switch (CMD) {
		case "AD": // Dodawanie warto�ci
			if (What) {
				processor.SetValue(P1, GetValue(P1) + GetValue(P2));
			} else {
				processor.SetValue(P1, GetValue(P1) + Integer.parseInt(P2));
			}
			break;

		case "SB": // Odejmowanie warto�ci
			if (What) {
				processor.SetValue(P1, GetValue(P1) - GetValue(P2));
			} else {
				processor.SetValue(P1, GetValue(P1) - Integer.parseInt(P2));
			}
			break;
			
		case "ML": // Mno�enie warto�ci
			if (What) {
				processor.SetValue(P1, GetValue(P1) * GetValue(P2));
			} else {
				processor.SetValue(P1, GetValue(P1) * Integer.parseInt(P2));
			}
			break;

		case "MV": // Umieszczenie warto�ci
			if (What) {
				processor.SetValue(P1, GetValue(P2));
			} else {
				processor.SetValue(P1, Integer.parseInt(P2));
			}
			break;

//-----------------------------------------------------------------------	PLIKI
		
		case "CE": // Tworzenie pliku
			filesystem.createEmptyFile(P1);
			break;
			
		case "CF": // Tworzenie pliku
			if (What) {
				filesystem.createFile(P1, GetValue(P2));
			} else {
				filesystem.createFile(P1, P2);
			}
			break;
			
		case "WF": // Dopisanie do pliku
			if (What) {
				filesystem.appendToFile(P1, GetValue(P2);
			} else {
				filesystem.appendToFile(P1, P2);
			}
			break;
			
		case "DF": // Usuwanie pliku
			filesystem.deleteFile(P1);
			break;
			
		case "RF": // Czytanie pliku														//TODO
			System.out.println(filesystem.getFileContent(P1));
			break;
			
		case "SK": // Wy�wietlanie plik�w													//TODO
			System.out.println(filesystem.showFiles());
			break;

//-----------------------------------------------------------------------	JUMPY I KONCZENIE
			
		case "JP": // Skok do rozkazu
			CMDCounter = Integer.parseInt(P1);
			break;
			
		case "JX": // Skok do rozkazu, je�li rejestr = 0
			if(GetValue(P1)==0) {
				CMDCounter = Integer.parseInt(P2);
			}
			break;

		case "EX": // Koniec programu
			Running.SetStan(2);
			break;	

//-----------------------------------------------------------------------	PROCESY
	
		case "XR": // czytanie komunikatu;													//TODO
			communication.readPipe(P1, P2, memory.freeLogicAdress(P2));
			break;
	
		case "XS": // -- Wys�anie komunikatu;
			communication.writePipe(P1, P2);
			break;
	
		case "XF": // -- znalezienie ID procesu (P1);
			processor.Set_A(manager.GetIDwithName(P1));
			break;
			
		case "XP": // -- Stworzenie potoku
			communication.createPipe(P1);
			break;
	
		case "XC": {// -- tworzenie procesu (P1,P2);
			manager.createprocess(P1,P2);
			break; }
	
		case "XZ": // -- wstrzymanie procesu
			Running.SetStan(1);
			break;
	
		}	
	}

//-------------------------------------------------------------------------------------------------------------------
	
	private Boolean CheckP2(String P2) {
		if(P2 == "A" || P2 == "B" || P2 == "C") {
			return 1;
		}
		else {
			return 0;
		}
	}
	
//-------------------------------------------------------------------------------------------------------------------
	
	private void ReturnToPCB() {
			PCB_b.A = processor.Get_A();
			PCB_b.B = processor.Get_B();
			PCB_b.C = processor.Get_C();
			
			PCB_b.commandCounter = CMDCounter;
	}
	
//-------------------------------------------------------------------------------------------------------------------
	
	private String GetInstruction() {
		String Instruction = "";
		int Counter=0;
		
		do{ 
			Instruction += memory.GetChar(CMDCounter, PCB_b.ID); //pobieranie z pami�ci znaku o danym numerze, oraz nale��cego do danego procesu

			CMDCounter++;
			Counter++;
		}while (Instruction.charAt(Counter)!=',' && Instruction.charAt(Counter)!=';');
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
