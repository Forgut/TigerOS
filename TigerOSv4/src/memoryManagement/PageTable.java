package memoryManagement;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


import Shell.Shell;
import memoryManagement.Memory;
import processManagement.ProcessManagment;
import processManagement.process_control_block;

public class PageTable {
	protected int[] frameNumber;
	protected int pagesRequired;
	private Boolean[] inRAM;
	private int firstFreeLogicalAdress;
	private int processSize;
	private Memory memory;
	private int virtualBase;
	
	public PageTable(String fileName, int processSize, Memory memory) throws Exception {
		this.memory = memory;
		if(processSize%memory.FRAME_SIZE==0)
			pagesRequired=processSize/memory.FRAME_SIZE;
		else
			pagesRequired=processSize/memory.FRAME_SIZE+1;
		initInRAM();		
		initFrameNumber();		
		setVirtualBase();
		try {
			writeFromFileToVirtualMemory(fileName, processSize);
		} catch (Exception e) {
			System.out.println("PAGE TABLE: nie mozna dodac do pamieci");
			throw new Exception();
		}
	}
	
	public void print() {
		System.out.println("\nPage Table:");
		System.out.println("Frame number: ");
		for(int e : frameNumber)
			System.out.print(e + " ");
		System.out.println("\nIn RAM: ");
		for(Boolean e : inRAM)
			System.out.print(e + " ");
		System.out.println("\n");
	}
	
	private void initInRAM() {
		inRAM = new Boolean[pagesRequired];
		for(int i=0;i<pagesRequired;i++)
			inRAM[i] = false;
	}
	
	private void initFrameNumber() {
		frameNumber = new int[pagesRequired];
		for(int i=0;i<pagesRequired;i++)
			frameNumber[i] = -1;
	}
	
	public int getVirtualBase() {
		return virtualBase;
	}
	
	private void setVirtualBase() {
		virtualBase = memory.virtualMemory.size();; 	
	}
	
	public void writeToVirtualMemory(char[] program, int processSize) {
		memory.writeToVirualMemory(virtualBase , program, processSize); 
	}	
	
	public void writeFromFileToVirtualMemory(String fileName, int processSize)  throws Exception {
		this.processSize = processSize;
		char[] program;
		StringBuilder sb = new StringBuilder();
		int logicalAdress = 0;
		File file = new File("src/" + fileName);
		try {
			if (!file.exists()) {
				System.out.println(fileName + " does not exist.");
				throw new Exception();
		//		return;
			}
			if (!(file.isFile() && file.canRead())) {
				System.out.println(file.getName() + " cannot be read from.");
				throw new Exception();
		//		return;
			}
			try {
				FileInputStream fis = new FileInputStream(file);
				if(processSize<fis.available()) {
					System.out.println("process size is too small");
					throw new Exception();
			//		return;
				}
				char current;
				while (logicalAdress < processSize) {
					current = (char) fis.read();
					sb.append(current);
// trzeba uwzglednic, ze kazda nowa linia (zamieniona na ' ') ma swoj adres logiczny		        
					if(current != '\n')
						logicalAdress++;
				}
				fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			
		}
		String everything = sb.toString();		
		String result = everything.replaceAll("[\\t\\n\\r]+"," ");		
		program = result.toCharArray();		
		setFirstFreeLogicalAdress(program);
		memory.writeToVirualMemory(virtualBase, program, processSize);
		
	}
	
	private void setFirstFreeLogicalAdress(char[] program) {
		for(int i=0;i<program.length;i++) 
			if(program[i]==';')
				firstFreeLogicalAdress = i+1;
	}

	public  char readFromMemory(int logicalAdress, int ID) {
		if(isFrameInRAM(getVirtualFrame(logicalAdress))) {
			// TODO: set bit innego procesu		
			int frameRAM = frameNumber[getVirtualFrame(logicalAdress)];
			for(FIFOFrame e : memory.FIFO)
				if(e.ID == ID && e.number == frameRAM)
					e.bit=true;
			return getCharFromRAM(logicalAdress);
		}
		else {
//			System.out.println("PAGETABLE: readFromMemory: logicalAdress = " + logicalAdress + "; virtualBase = " + virtualBase + "; ID = " + ID);
			writeFrameToRAM(getVirtualFrame(logicalAdress)+virtualBase, ID);
			return getCharFromRAM(logicalAdress);
		}
	}
	
	private int getVirtualFrame(int logicalAdress) {
		return logicalAdress/memory.FRAME_SIZE;
	}
	
	private int getVirtualOffset(int logicalAdress) {
		return logicalAdress%memory.FRAME_SIZE;
	}
	
	private boolean isFrameInRAM(int virtualFrame) {
		return inRAM[virtualFrame]==true;
	}
	
	private char getCharFromRAM(int logicalAdress) {
		char character = 0;
		int frameInRAM = frameNumber[getVirtualFrame(logicalAdress)];
		int offset = getVirtualOffset(logicalAdress);
		character = memory.getCharFromRAM(frameInRAM, offset);
		return character;
	}
	
	private void writeFrameToRAM(int frameVirtual, int ID) {
	//	System.out.println("WRITEFRAMETORAM frameVirtual = " + frameVirtual);
		int frameLogical = frameVirtual - virtualBase;
		if(memory.isFreeFrame()) {
			int frameRAM = memory.firstFreeFrame();
			memory.writeFrameToRAM(frameVirtual, frameRAM);
			frameNumber[frameLogical] = frameRAM;
			inRAM[frameLogical] = true;
			addFrameToFIFO(frameRAM, ID); //????
		}
		else {
//			System.out.println("PAGETABLE: !isFreeFrame() - DRUGA SZANSA");
			int victimFrameFIFOIndex = getVictimFrame();
	//		System.out.println("victimFrameFIFOIndex = " + victimFrameFIFOIndex);
			int frameRAM = memory.FIFO.get(victimFrameFIFOIndex).number;
			int IDVictim = memory.FIFO.get(victimFrameFIFOIndex).ID;	
			int virtualBase = ProcessManagment.getPCB(IDVictim).pageTable.virtualBase;
/*			System.out.print("printMEMORY: ");
			memory.print();
			System.out.println("VICTIM: frameRAM = " + frameRAM + "; IDVictim = " + IDVictim);
			System.out.println("print page table victim:");
			ProcessManagment.getPCB(IDVictim).pageTable.print();
*/			if(isPageInCurrentProcess(IDVictim, ID))
				removeEverythingAboutPageOfCurrentProcessFromRAM(frameRAM, IDVictim);
			else
				// TODO: usun z pagetable innego procesu - nieprzetestowane
				removeEverythingAboutPageOfOtherProcessFromRAM(frameRAM, IDVictim);
			memory.FIFO.remove(victimFrameFIFOIndex);
//			System.out.println("VICTIM REMOVED");
//			memory.print();
			memory.writeFrameToRAM(frameVirtual, frameRAM);
			addEverythingAboutPageOfCurrentProcessToRAM(frameVirtual, frameRAM, ID);
//			System.out.println("END DRUGA SZANSA");
		}
	}
	
	private void removeEverythingAboutPageOfCurrentProcessFromRAM(int frameRAM, int ID) {
		// dodac virtualBase do framevirt to rewrite (chyba?)
//		System.out.println("----removeEverythingAboutPageOfOtherProcessFromRAM----");		
		// from RAM to virtual
	//	int frameVirtualToRewrite = frameNumber[frameRAM];
		int index = 0;
		Boolean done = false;
		while(!done) {
			if(ProcessManagment.getPCB(ID).pageTable.frameNumber[index]==frameRAM) {
//				System.out.println("INDEX w frameNumber = " + index);
				done = true;
			}
			else
				index++;
		}
		int frameVirtualToRewrite = index + ProcessManagment.getPCB(ID).pageTable.virtualBase;
//		System.out.println("frameVirtualToRewrite = " + frameVirtualToRewrite);
		if(frameVirtualToRewrite != -1) {
			frameVirtualToRewrite+=ProcessManagment.getPCB(ID).pageTable.virtualBase;
	//		memory.rewriteFromRAMToVirtualMemory(frameVirtualToRewrite, getVirtualFrameOfOtherProcess());
			memory.rewriteFromRAMToVirtualMemory(frameRAM, frameVirtualToRewrite);
		}
		//frameNumber, inRAM
		int indexOfFrameToClear = 0;
		for(int i=0; i<frameNumber.length; i++) {
			if(frameNumber[indexOfFrameToClear]==frameRAM) 
				break;
			else
				indexOfFrameToClear++;
		}
		frameNumber[indexOfFrameToClear] = -1;
		inRAM[indexOfFrameToClear] = false;
	}
	

	private void removeEverythingAboutPageOfOtherProcessFromRAM(int frameRAM, int ID) {
		// from RAM to virtual
//		System.out.println("----removeEverythingAboutPageOfOtherProcessFromRAM----");
		ProcessManagment.getPCB(ID).pageTable.print();
//		int frameVirtualToRewrite = ProcessManagment.getPCB(ID).pageTable.frameNumber[frameRAM];
		int index = 0;
		Boolean done = false;
		while(!done) {
			if(ProcessManagment.getPCB(ID).pageTable.frameNumber[index]==frameRAM) {
//				System.out.println("INDEX w frameNumber = " + index);
				done = true;
			}
			else
				index++;
		}
		int frameVirtualToRewrite = index + ProcessManagment.getPCB(ID).pageTable.virtualBase;
//		System.out.println("frameVirtualToRewrite = " + frameVirtualToRewrite);
		if(frameVirtualToRewrite != -1) {
	//		memory.rewriteFromRAMToVirtualMemoryOfOtherProcess(frameVirtualToRewrite, getVirtualFrameOfOtherProcess(), ProcessManagment.getPCB(ID).pageTable.virtualBase);
	//		frameVirtualToRewrite+=ProcessManagment.getPCB(ID).pageTable.virtualBase;
			memory.rewriteFromRAMToVirtualMemory(frameRAM, frameVirtualToRewrite);
		}
		//frameNumber, inRAM
		int indexOfFrameToClear = 0;
		for(int i=0; i<ProcessManagment.getPCB(ID).pageTable.frameNumber.length; i++) {
			if(ProcessManagment.getPCB(ID).pageTable.frameNumber[indexOfFrameToClear]==frameRAM) 
				break;
			else
				indexOfFrameToClear++;
		}
		ProcessManagment.getPCB(ID).pageTable.frameNumber[indexOfFrameToClear] = -1;
		ProcessManagment.getPCB(ID).pageTable.inRAM[indexOfFrameToClear] = false;
	//	ProcessManagment.getPCB(ID).pageTable.print();
	}
	
	private void addEverythingAboutPageOfCurrentProcessToRAM(int frameVirtual, int frameRAM, int ID) {
	//	System.out.println("addEverythingAboutPageOfCurrentProcessToRAM");
	//	System.out.println("ID = " + ID + "; frameVirtual = " + frameVirtual + "; frameRAM = "+frameRAM);
		frameNumber[frameVirtual-ProcessManagment.getPCB(ID).pageTable.getVirtualBase()] = frameRAM;
		inRAM[frameVirtual-ProcessManagment.getPCB(ID).pageTable.getVirtualBase()] = true;
		addFrameToFIFO(frameRAM, ID);
	}
	
	// TODO: przetestowac
	private Boolean isPageInCurrentProcess(int IDfromFIFO, int IDRunning) {
		return IDfromFIFO == IDRunning;
	//	return true;
	}

	private void addFrameToFIFO(int frameRAM, int ID) {
		FIFOFrame newFrame = new FIFOFrame(frameRAM, ID);
		memory.FIFO.add(newFrame);
	}
	
	private int getVictimFrame() {
		Boolean found = false;
		int victimIndex = 0;
		while(!found){
			if(isBitZero(victimIndex)) 			
				found = true; 
			else {
				memory.FIFO.get(victimIndex).bit = false;			
				victimIndex++;
				if(victimIndex >= memory.FIFO.size())
					victimIndex = 0;
			}
		}
		return victimIndex;
	}
	
	private Boolean isBitZero(int elementIndex) {
		return(!memory.FIFO.get(elementIndex).bit);
	}
	
	// TODO
	private int getVirtualFrameOfOtherProcess() {
		return 0;
	}
	
	public void writeToMemory(int logicalAdress, char character, int ID) {
		if(isFrameInRAM(getVirtualFrame(logicalAdress))) {
			// TODO: set bit innego procesu?			
			int frameRAM = frameNumber[getVirtualFrame(logicalAdress)];
			for(FIFOFrame e : memory.FIFO)
				if(e.ID == ID && e.number == frameRAM)
					e.bit=true;
			writeCharToRAM(logicalAdress, character);
		}
		else {
			writeFrameToRAM(getVirtualFrame(logicalAdress)+virtualBase, ID);
			writeCharToRAM(logicalAdress, character);
		}
		firstFreeLogicalAdress++;
	}
	
	private  void writeCharToRAM(int logicalAdress, char character) {
		int frameInRAM = frameNumber[getVirtualFrame(logicalAdress)];
		int offset = getVirtualOffset(logicalAdress);
		memory.writeCharToRAM(frameInRAM, offset, character);
		
	}
	
	public int getLogicalAdressOfMessageToWrite(int messageSize) {
		if((firstFreeLogicalAdress+messageSize) > processSize) {
			System.out.println("Memory: message is too long");
			return -1;
		}
		else
			return firstFreeLogicalAdress;
	}
	
	public void deleteFromMemory(int ID) {
        ArrayList<Integer> FIFOIndex = new ArrayList<Integer>();
        int virtualBase = ProcessManagment.getPCB(ID).pageTable.virtualBase;       
        for(int i=0;i<frameNumber.length;i++) {
            int frameRAMToDelete = frameNumber[i];
            if(frameRAMToDelete != -1)
                memory.clearPageFromRAM(frameRAMToDelete);
            memory.clearFrameFromVirtualMemory(virtualBase+i);
            inRAM[i] = false;
        }
        for(int i=memory.FIFO.size()-1;i>=0;i--){
            if(memory.FIFO.get(i).ID==ID) {
                FIFOIndex.add(i);
                memory.FIFO.remove(i);
            }
        }
    }
	
	

}