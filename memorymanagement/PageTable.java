package memorymanagement;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import memorymanagement.Memory;
import processesmanagement.ControlBlock;

public class PageTable {
	protected int[] frameNumber;
	protected int pagesRequired;
	private Boolean[] inRAM;
	private int firstFreeLogicalAdress;
	private int processSize;
	private Memory memory;
	
	public PageTable(String fileName, int processSize, Memory memory) {
		this.memory = memory;
		if(processSize%memory.FRAME_SIZE==0)
			pagesRequired=processSize/memory.FRAME_SIZE;
		else
			pagesRequired=processSize/memory.FRAME_SIZE+1;
		System.out.println("Pages Required: " + pagesRequired);
		initInRAM();
		initFrameNumber();
		setVirtualBase();
		writeFromFileToVirtualMemory(fileName, processSize);
	}
	
	public void print() {
		System.out.println("\nPage Table ID:" + Runnning.ID);
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
		// TODO?
		return 0;
	}
	
	private void setVirtualBase() {
		Running.virtualBase = memory.virtualMemory.size();
	}
	
	public void writeToVirtualMemory(char[] program, int processSize) {
		memory.writeToVirualMemory(Running.virtualBase, program, processSize); 
	}	
	
	public void writeFromFileToVirtualMemory(String fileName, int processSize) {
		this.processSize = processSize;
		char[] program;
		StringBuilder sb = new StringBuilder();
		int logicalAdress = 0;
		File file = new File("src/" + fileName);
			if (!file.exists()) {
				System.out.println(fileName + " does not exist.");
				return;
			}
			if (!(file.isFile() && file.canRead())) {
				System.out.println(file.getName() + " cannot be read from.");
				return;
			}
			try {
				FileInputStream fis = new FileInputStream(file);
				if(processSize<fis.available()) {
					System.out.println("process size is too small");
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
			} catch (IOException e) {
				e.printStackTrace();
			}
		String everything = sb.toString();
		String result = everything.replaceAll("[\\t\\n\\r]+"," ");
		program = result.toCharArray();
		setFirstFreeLogicalAdress(program);
		System.out.println("first free: " + firstFreeLogicalAdress);
		Memory.writeToVirualMemory(Running.virtualBase, program, processSize);
	}
	
	private void setFirstFreeLogicalAdress(char[] program) {
		for(int i=0;i<program.length;i++) 
			if(program[i]==';')
				firstFreeLogicalAdress = i+1;
	}

	public  char readFromMemory(int logicalAdress) {
		if(isFrameInRAM(getVirtualFrame(logicalAdress))) {
			// TODO: set bit innego procesu		
			int ID = Running.ID;
			int frameRAM = frameNumber[getVirtualFrame(logicalAdress)];
			for(FIFOFrame e : memory.FIFO)
				if(e.ID == ID && e.number == frameRAM)
					e.bit=true;
			return getCharFromRAM(logicalAdress);
		}
		else {
			writeFrameToRAM(getVirtualFrame(logicalAdress));
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
	
	private void writeFrameToRAM(int frameVirtual) {
		if(memory.isFreeFrame()) {
			int frameRAM = memory.firstFreeFrame();
			memory.writeFrameToRAM(frameVirtual, frameRAM);
			frameNumber[frameVirtual] = frameRAM;
			inRAM[frameVirtual] = true;
	//		addFrameToFIFO(frameRAM, ControlBlock.ID); 
			addFrameToFIFO(frameRAM, Running.ID);
		}
		else {
			int victimFrameFIFOIndex = getVictimFrame();
			int frameRAM = memory.FIFO.get(victimFrameFIFOIndex).number;
			int ID = memory.FIFO.get(victimFrameFIFOIndex).ID;				
			if(isPageInCurrentProcess(ID)) 
				removeEverythingAboutPageOfCurrentProcessFromRAM(frameRAM);
			else
				// TODO: usun z pagetable innego procesu - nieprzetestowane
				removeEverythingAboutPageOfOtherProcessFromRAM(frameRAM, ID);
			memory.FIFO.remove(victimFrameFIFOIndex);
			
			memory.writeFrameToRAM(frameVirtual, frameRAM);
			addEverythingAboutPageOfCurrentProcessToRAM(frameVirtual, frameRAM, ID);
		}
	}
	
	private void removeEverythingAboutPageOfCurrentProcessFromRAM(int frameRAM) {
		// from RAM to virtual
		int frameVirtualToRewrite = frameNumber[frameRAM];
		if(frameVirtualToRewrite != -1)
			memory.rewriteFromRAMToVirtualMemory(frameVirtualToRewrite, getVirtualFrameOfOtherProcess());
		
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
	
	// TODO: przetestowac
	private void removeEverythingAboutPageOfOtherProcessFromRAM(int frameRAM, int ID) {
		// from RAM to virtual
		PCB tempPCB = getPCB(ID); 		// nie mam pojecia, skad mam wziac getPCB, kto mi to udostepni
		PageTable temp = tempPCB.pageTable;
		int frameVirtualToRewrite = temp.frameNumber[frameRAM];
		if(frameVirtualToRewrite != -1)
			memory.rewriteFromRAMToVirtualMemory(frameVirtualToRewrite, getVirtualFrameOfOtherProcess(), tempPCB.virtualBase);
		
		//frameNumber, inRAM
		int indexOfFrameToClear = 0;
		for(int i=0; i<temp.frameNumber.length; i++) {
			if(temp.frameNumber[indexOfFrameToClear]==frameRAM) 
				break;
			else
				indexOfFrameToClear++;
		}
		temp.frameNumber[indexOfFrameToClear] = -1;
		temp.inRAM[indexOfFrameToClear] = false;
	}
	
	private void addEverythingAboutPageOfCurrentProcessToRAM(int frameVirtual, int frameRAM, int ID) {
		frameNumber[frameVirtual] = frameRAM;
		inRAM[frameVirtual] = true;
		addFrameToFIFO(frameRAM, ID);
	}
	
	// TODO: przetestowac
	private Boolean isPageInCurrentProcess(int IDfromFIFO) {
		return IDfromFIFO == Running.ID;
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
	
	public void writeToMemory(int logicalAdress, char character) {
		if(isFrameInRAM(getVirtualFrame(logicalAdress))) {
			// TODO: set bit innego procesu?			
			int ID = Running.ID;
			int frameRAM = frameNumber[getVirtualFrame(logicalAdress)];
			for(FIFOFrame e : memory.FIFO)
				if(e.ID == ID && e.number == frameRAM)
					e.bit=true;
			writeCharToRAM(logicalAdress, character);
		}
		else {
			writeFrameToRAM(getVirtualFrame(logicalAdress));
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

}