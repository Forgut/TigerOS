import java.util.ArrayList;

public class Interpreter {

	private int Reg_A=0, Reg_B=0, Reg_C=0;
	private Memory memory;
	private ProcessManager manager;
	private filesystem filesystem;
	private PCB PCB_b; 			//Zmienna do kopii PCB procesu
	private int CMDCounter; 	//Licznik rozkazu do czytania z pamiêci
	
//-------------------------------------------------------------------------------------------------------------------
	
	public Interpreter(Memory memory, ProcessManager manager) {
		this.memory=memory;
		this.manager=manager;
	}
	
//-------------------------------------------------------------------------------------------------------------------
	
	public int RUN(Process Running) {
		PCB_b=Running.GetPCB();		 //Wczytanie PCB procesu
		
		CMDCounter = PCB_b.GetCMDCounter(); //Pobieranie licznika rozkarów
		
		this.Reg_A = PCB_b.Get_A()); //Pobieranie stanu rejestru A
		this.Reg_B = PCB_b.Get_B()); //Pobieranie stanu rejestru B
		this.Reg_C = PCB_b.Get_C()); //Pobieranie stanu rejestru C
		
		memory.A = Reg_A;			 //Ustawianie wartosci rejestru A do pamiêci
		memory.B = Reg_B;			 //Ustawianie wartosci rejestru B do pamiêci
		memory.C = Reg_c;			 //Ustawianie wartosci rejestru C do pamiêci
		
		do {
		String Instruction = GetInstruction();	 //Zmienna pomocnicza do ³adowania instrukcji z pamiêci
		Execute(Instruction);
		}while(Instuction.substring(6) == ',');
		
		ReturnToPCB();
		Running.SetPCB(PCB_b);
		return 0;
	}
	
//-------------------------------------------------------------------------------------------------------------------
	
	void Execute(String Instruction) {
		int x = 0;	//takie coœ do sprawdzania czy by³a spacja
		int i = 1; 	//licznik do podzialu rozkazu na segmenty
		String CMD = "";
		String P1 = "";
		String P2 = "";
		
//-----------------------------------------------------------------------
		
		while(i < 4) {
			if(i == 1) {
				while(Instruction[x]!=' ' || Instruction[x]!=',' || Instruction[x]!=';') {
					CMD += Instruction[x];
					x++;
				}
				if(Instruction[x]!=',' || Instruction[x]!=';'){
					i++;
				}
				else {
					break;
				}
			}
			else if(i == 2) {
				while(Instruction[x]!=' ' || Instruction[x]!=',' || Instruction[x]!=';') {
					P1 += Instruction[x];
					x++;
				}
				if(Instruction[x]!=',' || Instruction[x]!=';'){
					i++;
				}
				else {
					break;
				}
			}
			else if(i == 3) {
				while(Instruction[x]!=' ' || Instruction[x]!=',' || Instruction[x]!=';') {
					P2 += Instruction[x];
					x++;
				}
				break;
			}
		}

		bool What = CheckP2(P2);
		
//-----------------------------------------------------------------------		
		
		switch (CMD) {
		case "AD": // Dodawanie wartoœci
			if (What) {
				setValue(P1, getValue(P1) + getValue(P2));
			} else {
				setValue(P1, getValue(P1) + Integer.parseInt(P2));
			}
			break;

		case "SB": // Odejmowanie wartoœci
			if (What) {
				setValue(P1, getValue(P1) - getValue(P2));
			} else {
				setValue(P1, getValue(P1) - Integer.parseInt(P2));
			}
			break;
			
		case "ML": // Mno¿enie wartoœci
			if (What) {
				setValue(P1, getValue(P1) * getValue(P2));
			} else {
				setValue(P1, getValue(P1) * Integer.parseInt(P2));
			}
			break;

		case "MV": // Umieszczenie wartoœci
			if (What) {
				setValue(P1, getValue(P2));
			} else {
				setValue(P1, Integer.parseInt(P2));
			}
			break;

//-----------------------------------------------------------------------
			
		case "CF": // Tworzenie pliku
			if (What) {
				filesystem.createFileWithContent(P1, GetValue(P2);
			} else {
				filesystem.createFileWithContent(P1, P2);
			}
			break;
			
		case "WF": // Dopisanie do pliku
			if (What) {
				filesystem.writeToFile(P1, GetValue(P2);
			} else {
				filesystem.writeToFile(P1, P2);
			}
			break;
			
		case "DF": // Usuwanie pliku
			filesystem.deleteFile(P1);
			break;
			
		case "RF": // Czytanie pliku
			System.out.println(filesystem.getFileContent(P1));
			break;
			
		case "SK": // Wyœwietlanie plików
			System.out.println(filesystem.showFiles());
			break;

//-----------------------------------------------------------------------
			
		case "JP": // Skok do rozkazu
			CMDCounter = Integer.parseInt(P1);
			break;
			
		case "JX": // Skok do rozkazu, jeœli rejestr = 0
			if(getValue(P1)!=0) {
				CMDCounter = Integer.parseInt(P2);
			}
			break;

		case "EX": // Koniec programu
			ProcessorManager.RUNNING.SetState(4);
			break;	

//-----------------------------------------------------------------------		
	/*	
		case "XR": // czytanie komunikatu;
			String received  = Communication.read(ProcessorManager.RUNNING.GetName());
			ProcessorManager.RUNNING.pcb.receivedMsg = received;
			filesystem.createEmptyFile(ProcessorManager.RUNNING.GetName());
			filesystem.appendToFile(ProcessorManager.RUNNING.GetName(), received);
			break;
		case "XS": // -- Wys³anie komunikatu;
			Communication.write(P1, P2);
			break;
		case "XN": // -- znalezienie PCB (P1);
			//setValue("A", processesManagment.FindProcessWithName(P1));
			core.Processor.A=processesManagment.GetIDwithName(P1);
			break;

		case "XC": {// -- tworzenie procesu (P1);
			File file = new File(P2);
			if(!file.exists())  {
				try {
					FileWriter fw = new FileWriter(P2, true);
					BufferedWriter out = new BufferedWriter(fw);
					out.write("HLT");
					out.close();
					fw.close();
				} catch(FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(processesManagment.NewProcess_XC(P2, P1)==-1){
				ProcessorManager.RUNNING.SetState(4);
				return;
			}
			processesManagment.getProcess(P1).SetState(3);
			break; }
		case "XY": // -- Uruchomienie procesu
			processesManagment.getProcess(P1).SetState(1);
			processesManagment.getProcess(P1).SetCurrentPriority(ProcessorManager.MAX_PRIORITY+1);
			break;
		case "XD": // -- usuwanie procesu (P1);
			processesManagment.getProcess(P1).SetState(4);
			processesManagment.DeleteProcessWithName_XD(P1);
			break;
		case "XZ": // -- Zatrzymanie procesu
			processesManagment.getProcess(P1).SetState(3);
			break;
	*/
		}	
	}

//-------------------------------------------------------------------------------------------------------------------
	
	private bool CheckP2(String P2) {
		if(P2 == " A" || P2 == " B" || P2 == " C") {
			return 1;
		}
		else {
			return 0;
		}
	}
	
//-------------------------------------------------------------------------------------------------------------------
	
	private void ReturnToPCB() {
			PCB_b.A = Get_A();
			PCB_b.B = Get_B();
			PCB_b.C = Get_C();
			
			PCB_b.commandCounter = CMDCounter;
	}
	
//-------------------------------------------------------------------------------------------------------------------
	
	private String GetInstruction() {
		String Instruction = "";
		int Counter=0;
		
		while (Instruction[Counter]!=',' || Instruction[Counter]!=';') { 
			Instruction += memory.GetChar(CMDCounter, PCB_b.ID); //pobieranie z pamiêci znaku o danym numerze, oraz nale¿¹cego do danego procesu

			CMDCounter++;
			Counter++;
		}
		return Instruction;
	}
	
//-------------------------------------------------------------------------------------------------------------------
	
	private int GetValue(String P) {
		switch (P) {
		case "A":
			return memory.A;
		case "B":
			return memory.B;
		case "C":
			return memory.C;
		}
		return 0;
	}
	
//-------------------------------------------------------------------------------------------------------------------
	
	private void SetValue(String P1, int P2) {
		switch (P1) {
		case "A":
			memory.A=P2;
			break;
		case "B":
			memory.B=P2;
			break;
		case "C":
			memory.C=P2;
			break;
		}
	}

//-------------------------------------------------------------------------------------------------------------------
	
 	private int Get_A() {
		return memory.A;
	}
	
//-------------------------------------------------------------------------------------------------------------------
 	
	private int Get_B() {
		return memory.B;
	}
	
//-------------------------------------------------------------------------------------------------------------------
	
	private int Get_C() {
		return memory.C;
	}
	
//-------------------------------------------------------------------------------------------------------------------
	
	public void Show_Regs() {
		System.out.println("Register A: " + memory.A);
		System.out.println("Register B: " + memory.B);
		System.out.println("Register C: " + memory.C);
		System.out.println("Command counter: " + this.CMDCounter);
	}

}