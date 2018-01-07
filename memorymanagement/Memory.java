package memorymanagement;

import java.util.ArrayList;

public class Memory {
	public final int FRAME_SIZE = 16;
	private final int RAM_SIZE = 128;
	private char[] RAM = new char[RAM_SIZE];
	protected Boolean[] freeFrames = new Boolean[RAM_SIZE/FRAME_SIZE];	
	protected ArrayList<ArrayList<Character>> virtualMemory = new  ArrayList<ArrayList<Character>>();
	protected ArrayList<FIFOFrame> FIFO = new ArrayList<FIFOFrame>();

	public Memory() {
		for(int i=0;i<(RAM_SIZE/FRAME_SIZE);i++) {
			freeFrames[i]=true;
		}
//		initVirtualMemory();
	}
	
	private void initVirtualMemory() {
		ArrayList<Character> temp = new ArrayList<Character>(FRAME_SIZE);
		for(int i=0;i<FRAME_SIZE;i++)
			temp.add(' ');		
		for(int i=0;i<2;i++) {			
			virtualMemory.add(temp);
		}		
	}
	
	public void print() {
		System.out.println("RAM:");
		printRAM();
		printVirtualMemory();
		printFIFO();
	}
	
	private void printRAM() {
		for(int i=0;i<(RAM_SIZE/FRAME_SIZE);i++) {
			for(int j=0;j<FRAME_SIZE;j++)
				System.out.print(RAM[FRAME_SIZE*i+j]);
			System.out.println("");
		}
		System.out.println();
		printRAMCharacteristics();
	}

	private void printRAMCharacteristics() {
		System.out.print("free frames:");
		for(Boolean x : freeFrames)
			System.out.print(x + " ");
		System.out.println("\n");
	}
	
	protected void printVirtualMemory() {
		System.out.println("Virtual memory:");
		for(ArrayList<Character> a : virtualMemory){
			for(Character b : a)
				System.out.print(b);
			System.out.println();
		}
		System.out.println();
	}
	
	protected void writeToVirualMemory(int virtualBase, char[] program, int processSize) {
		ArrayList<Character> tempFrame;
	//	for(int i=virtualBase;i<PageTable.pagesRequired;i++) { // TODO:PageTable obecnie wykonywanego procesu
		for(int i=0;i<PageTable.pagesRequired;i++) { // TODO:PageTable obecnie wykonywanego procesu
			tempFrame = new ArrayList<Character>(FRAME_SIZE);
			if(i==PageTable.pagesRequired-1) {
				for(int j=0;j<processSize-((PageTable.pagesRequired-1)*FRAME_SIZE);j++)
					tempFrame.add(program[i*FRAME_SIZE+j]);
				for(int j=processSize-((PageTable.pagesRequired-1)*FRAME_SIZE);j<FRAME_SIZE;j++)
					tempFrame.add(' ');
			}
			else 
				for(int j=0; j<FRAME_SIZE; j++)
					tempFrame.add(program[i*FRAME_SIZE+j]);			
			virtualMemory.add(tempFrame);	
		}			
	}
	
	protected char getCharFromRAM(int frame, int offset) {
		int index = FRAME_SIZE*frame+offset;
		return RAM[index];
	}
	
	protected boolean isFreeFrame() {
		for(Boolean x : freeFrames)
			if(x.equals(true))
				return true;
		return false;
	}
	
	protected int firstFreeFrame() {
		int frameNumber = -1;
		int index = 0;
		while(frameNumber == -1){
			if(freeFrames[index]==true)
				frameNumber = index;
			index++; 
		}
		return frameNumber;
	}
	
	protected void writeFrameToRAM(int frameVirtual, int frameRAM) {
		for(int i=0;i<FRAME_SIZE;i++)
			RAM[frameRAM*FRAME_SIZE+i] = virtualMemory.get(frameVirtual).get(i);		
		freeFrames[frameRAM] = false;
	}
	
	protected void printFIFO() {
		System.out.println("FIFO:");
		for(FIFOFrame e: FIFO) {
			System.out.println("ID: " + e.ID + "; RAM number:" + e.number + "; bit: " + bitToInt(e.bit));
		}
	}
	
	private int bitToInt(Boolean bit) {
		if(bit==false)
			return 0;
		else
			return 1;
	}	
	
	protected void rewriteFromRAMToVirtualMemory(int frameRAM, int frameVirtual) {
		// TODO?
		frameVirtual = frameRAM;
		ArrayList<Character> temp = new ArrayList<Character>();
		for(int i=0;i<FRAME_SIZE;i++)
			temp.add(RAM[FRAME_SIZE*frameRAM+i]);
		virtualMemory.set(frameVirtual, temp);
	}
	
	// TODO: przetestowac
	protected void rewriteFromRAMToVirtualMemoryOfOtherProcess(int frameRAM, int frameVirtual, int virtualBase) {
		frameVirtual = frameRAM;
		ArrayList<Character> temp = new ArrayList<Character>();
		for(int i=0;i<FRAME_SIZE;i++)
			temp.add(RAM[FRAME_SIZE*frameRAM+i]);
		virtualMemory.set(virtualBase + frameVirtual, temp);
	}
	
	protected void writeCharToRAM(int frame, int offset, char character) {
		int index = FRAME_SIZE*frame+offset;
		RAM[index] = character;
	}
}