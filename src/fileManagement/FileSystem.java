package fileManagement;

import java.util.ArrayList;
import java.util.List;

public class FileSystem {
	// Final variables - parameters of memory.
	private final int DISK_SIZE = 1024;
	private final int BLOCK_SIZE = 32;
	private final int NUMBER_OF_BLOCKS = DISK_SIZE/BLOCK_SIZE;
	
	// Data structures.
	private char[] dataArea = new char[DISK_SIZE];
	private int[] fileAllocationTable = new int[NUMBER_OF_BLOCKS];
	private boolean[] bitVector = new boolean[NUMBER_OF_BLOCKS];
	private List<File> mainCatalog = new ArrayList<File>();
	
	// Fill dataArea & bitVector with default values.
	public FileSystem() {
		for(int i=0; i<DISK_SIZE; i++) {dataArea[i]='#';}
		for(int i=0; i<NUMBER_OF_BLOCKS; i++) {bitVector[i]=false; }
	}
	
	// Secondary method for finding first free block for new file.
	private int findFreeBlock() {
		int freeBlock = -1;
		for(int i=0; i<NUMBER_OF_BLOCKS; i++) {
			if(bitVector[i]=false) {
				freeBlock = i;
				break;}
			}
		return freeBlock;
	}
	
	// Secondary method for checking if name of the new file is occupied.
	/*private boolean checkNameAvailability(String name) {
		for (Fi : mainCatalog) {
            System.out.println(value);
        }
	}*/
	
	// Creating a empty file.
	private void createEmptyFile(String name) {
		int newBlock = findFreeBlock();
		if(newBlock!=-1) {
			bitVector[newBlock] = true;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static void main(String[] args) {
		
	}

}
