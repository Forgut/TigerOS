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
	//	System.out.println("vbase = " + virtualBase);
		int pagesRequired;
		
		if(processSize%FRAME_SIZE==0)
			pagesRequired=processSize/FRAME_SIZE;
		else
			pagesRequired=processSize/FRAME_SIZE+1;
		ArrayList<Character> tempFrame;
	//	for(int i=virtualBase;i<PageTable.pagesRequired;i++) { // TODO:PageTable obecnie wykonywanego procesu
		for(int i=0;i<pagesRequired;i++) { // TODO:PageTable obecnie wykonywanego procesu
			tempFrame = new ArrayList<Character>(FRAME_SIZE);
			if(i==pagesRequired-1) {
				for(int j=0;j<processSize-((pagesRequired-1)*FRAME_SIZE);j++)
					tempFrame.add(program[i*FRAME_SIZE+j]);
				for(int j=processSize-((pagesRequired-1)*FRAME_SIZE);j<FRAME_SIZE;j++)
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
	//	System.out.println("MEMORY: frameVirtual = " + frameVirtual);
	//	System.out.println("MEMORY: frameRAM = " + frameRAM);
		for(int i=0;i<FRAME_SIZE;i++) {
			RAM[frameRAM*FRAME_SIZE+i] = virtualMemory.get(frameVirtual).get(i);	
	//		System.out.println("MEMORY - zapisywane do RAM: " +  virtualMemory.get(frameVirtual).get(i) + " do " + (Integer)(frameRAM*FRAME_SIZE+i));
		}
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
	/*	System.out.println("----------------------rewriteFromRAMToVirtualMemory----------------------");
		System.out.println("frameVirtualToRewrite = " + frameVirtual);
		System.out.println("frameRAM = " + frameRAM);
*/
		// TODO?
//		frameVirtual = frameRAM;
		ArrayList<Character> temp = new ArrayList<Character>();
		for(int i=0;i<FRAME_SIZE;i++)
			temp.add(RAM[FRAME_SIZE*frameRAM+i]);
		
//		System.out.println("do VirtualMemory dodano na " + (Integer)(frameVirtual) + " miejsce");
		virtualMemory.set(frameVirtual, temp);
	}
	
	protected void rewriteFromRAMToVirtualMemoryOfOtherProcess(int frameRAM, int frameVirtual, int virtualBase) {
/*		System.out.println("----------------------rewriteFromRAMToVirtualMemory----------------------");
		System.out.println("frameVirtualToRewrite = " + frameVirtual);
		System.out.println("frameRAM = " + frameRAM);
*/	//	frameVirtual = frameRAM;
		ArrayList<Character> temp = new ArrayList<Character>();
		for(int i=0;i<FRAME_SIZE;i++)
			temp.add(RAM[FRAME_SIZE*frameRAM+i]);
		
//		System.out.println("do VirtualMemory dodano na " + (Integer)(frameVirtual-virtualBase) + " miejsce");
		virtualMemory.set(frameVirtual-virtualBase, temp);
	}
	
	protected void writeCharToRAM(int frame, int offset, char character) {
		int index = FRAME_SIZE*frame+offset;
		RAM[index] = character;
	}
	
	// Memory
	public void clearPageFromRAM(int frame) {
	        for(int i=0;i<FRAME_SIZE;i++)
	            RAM[frame*FRAME_SIZE+i] = ' ';
	        freeFrames[frame] = true;
	    }
	   
	    public void clearFrameFromVirtualMemory(int frame) {
	        ArrayList<Character> temp = new ArrayList<>();
	        for(int i=0;i<FRAME_SIZE;i++)
	            temp.add(' ');
	        for(int i=0;i<FRAME_SIZE;i++)
	            virtualMemory.set(frame, temp);
	    }
	
}