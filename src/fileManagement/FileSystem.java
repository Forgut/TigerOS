package fileManagement;

import java.util.ArrayList;
import java.util.List;

public class FileSystem {
	// Final variables - parameters of memory.
	private final int DISK_SIZE = 1024;
	private final int BLOCK_SIZE = 32;
	private final int NUMBER_OF_BLOCKS = DISK_SIZE / BLOCK_SIZE;

	// Data structures.
	private char[] dataArea = new char[DISK_SIZE];
	private int[] fileAllocationTable = new int[NUMBER_OF_BLOCKS];
	private boolean[] bitVector = new boolean[NUMBER_OF_BLOCKS];
	private List<File> mainCatalog = new ArrayList<File>();

	// Fill dataArea & bitVector with default values.
	public FileSystem() {
		for (int i = 0; i < DISK_SIZE; i++) {
			dataArea[i] = ' ';
		}
		for (int i = 0; i < NUMBER_OF_BLOCKS; i++) {
			bitVector[i] = false;
		}
		for (int i = 0; i < NUMBER_OF_BLOCKS; i++) {
			fileAllocationTable[i] = -2;
		}
	}

	static String response = "";

	// Secondary method for finding first free block for new file.
	private int findFreeBlock() {
		int freeBlock = -1;
		for (int i = 0; i < NUMBER_OF_BLOCKS; i++) {
			if (bitVector[i] == false) {
				freeBlock = i;
				break;
			}
		}
		return freeBlock;
	}

	// Secondary method for checking if name of the new file is occupied.
	private boolean checkNameAvailability(String searchedName) {
		boolean available = true;
		for (int i = 0; i < mainCatalog.size(); i++) {
			if (mainCatalog.get(i).name == searchedName) {
				available = false;
				break;
			}
		}
		return available;
	}

	// Creating a empty file.
	private boolean createEmptyFile(String name) {
		if (!checkNameAvailability(name)) {
			response = "Name " + name + " is occupied.";
			System.out.println(response);
			return false;
		} else {
			int newBlock = findFreeBlock();
			if (newBlock != -1) {
				bitVector[newBlock] = true;
				File plik = new File(name, 0, newBlock);
				mainCatalog.add(plik);
				fileAllocationTable[newBlock] = -1;
				response = "File named \"" + name + "\" was created.";
				System.out.println(response);
				return true;
			} else {
				response = "Cannot find free block.";
				System.out.println(response);
				return false;
			}
		}
	}

	// Open file. If it finds file with exact name changes its open flag to "true";
	private void openFile(String name) {
		for (int i = 0; i < mainCatalog.size(); i++) {
			if (mainCatalog.get(i).name == name) {
				mainCatalog.get(i).open = true;
				response = "File " + name + " opened.";
				System.out.println(response);
				// TODO: Add mutex lock
				break;
			}
		}
	}

	// Open file. If it finds file with exact name changes its open flag to "true";
	private void closeFile(String name) {
		for (int i = 0; i < mainCatalog.size(); i++) {
			if (mainCatalog.get(i).name == name) {
				mainCatalog.get(i).open = false;
				response = "File " + name + " closed.";
				System.out.println(response);
				// TODO: Add mutex lock
				break;
			}
		}
	}

	// Secondary method for setting file number in catalog.
	private int fileNumberSetter(String name) {
		int tempFileNumber = -1;
		for (int i = 0; i < mainCatalog.size(); i++) {
			if (mainCatalog.get(i).name == name) {
				tempFileNumber = i;
				break;
			}
		}
		return tempFileNumber;
	}

	// Secondary method for checking value of file's last block index.
	private int indexOfLastBlock(String name) {
		int fileNumber = fileNumberSetter(name);
		int lastBlockIndex = mainCatalog.get(fileNumber).indexOfFirstBlock;
		int lastBlockValue = fileAllocationTable[lastBlockIndex /*- 1*/];
		while (lastBlockValue != -1) {
			if(lastBlockValue==-2) break;
			lastBlockIndex = lastBlockValue;
			//System.out.println(lastBlockIndex+"    lastBlockIndex");
			lastBlockValue = fileAllocationTable[lastBlockIndex];
		}
		System.out.println(lastBlockIndex+"    returned lastBlockIndex");
		return lastBlockIndex; /* Return last block index number (from 1-32) */
	}

	/*
	 * Secondary method for checking value of index where method should start
	 * appending.
	 */
	/*private int indexOfWritingStart(int fileNumber) {
		int size = 0;
		if (mainCatalog.get(fileNumber).size > BLOCK_SIZE)
			size = mainCatalog.get(fileNumber).size % BLOCK_SIZE;
		else
			size = mainCatalog.get(fileNumber).size;
		return indexOfLastBlock(mainCatalog.get(fileNumber).name) + size - 1;
	}*/
	
	//Secondary method for checking value of index
	private int findIndex(int value) {
		int searchedIndex=-1;
		for(int i = 0 ; i<NUMBER_OF_BLOCKS;i++) {
			if(fileAllocationTable[i]==value)
			{
				searchedIndex = i;
				break;
			}
		}
		return searchedIndex;
	}
	
	// Delete file.
	private void deleteFile(String name) {
		if (checkNameAvailability(name)) {
			response = "File " + name + " doesn't exist.";
			System.out.println(response);
		} else {
			int fileNumber = fileNumberSetter(name);
			int charactersDeleted = 0;
			int i = mainCatalog.get(fileNumber).size;
			System.out.println(i +"           i");

			if (mainCatalog.get(fileNumber).size <= BLOCK_SIZE) {
				int j = 0;
				while (charactersDeleted != mainCatalog.get(fileNumber).size) {

					dataArea[indexOfLastBlock(name) * 32 + i-- -1] = ' ';
					charactersDeleted++;
				}
				fileAllocationTable[mainCatalog.get(fileNumber).indexOfFirstBlock]=-2;
				bitVector[mainCatalog.get(fileNumber).indexOfFirstBlock]=false;
				mainCatalog.remove(fileNumber);
				response = "File " + name + " has been deleted..";
				System.out.println(response);
			}
			else {
				while(mainCatalog.get(fileNumber).size>0){
				int tempSize;

				if(mainCatalog.get(fileNumber).size>0&&mainCatalog.get(fileNumber).size%32==0) tempSize = 32;
				else tempSize=mainCatalog.get(fileNumber).size%32;
				i = tempSize;
				int test = i;
				System.out.println(tempSize+"    tempSize");
				while (charactersDeleted != tempSize) {
					//System.out.println(indexOfLastBlock(name) * 32 + i-- -1 +"    indexOfLastBlock(name) * 32 + i-- -1");
					dataArea[(indexOfLastBlock(name) -1)* 32 + i-- -1] = ' ';
					charactersDeleted++;
				}

				bitVector[indexOfLastBlock(name)-1]=false;
				fileAllocationTable[findIndex(indexOfLastBlock(name))] = -1;
				fileAllocationTable[indexOfLastBlock(name)]=-2;

				mainCatalog.get(fileNumber).size-=charactersDeleted;
				charactersDeleted=0;
				}
				mainCatalog.remove(fileNumber);
				response = "File " + name + " has been deleted..";
				System.out.println(response);
			}

		}
	}

	// Add text to file.
	private void appendToFile(String name, String content) {
		char[] contentArray = content.toCharArray();
		int fileNumber = fileNumberSetter(name);
		int charactersAdded = 0;
		int sizeIncrease = 0;
		if (fileNumber == -1) {
			response = "File " + name + " doesn't exist.";
			System.out.println(response);
			return;
		}
		if (mainCatalog.get(fileNumber).size % 32 + content.length() <= BLOCK_SIZE) {
			int i = mainCatalog.get(fileNumber).size % 32, j = 0;
			while (charactersAdded != content.length()) {

				dataArea[indexOfLastBlock(name) * 32 + i++] = contentArray[j++];
				charactersAdded++;
				sizeIncrease++;
			}
			mainCatalog.get(fileNumber).size += content.length();
			response = "File " + name + " has been extended..";
			System.out.println(response);
		} else {
			int newBlockAmount;
			if (((mainCatalog.get(fileNumber).size % 32 + content.length()) % 32) == 0)
				newBlockAmount = content.length() / 32;
			else
				newBlockAmount = content.length() / 32 + 1;

			while (newBlockAmount != 0) {

				int newBlock = findFreeBlock();
				System.out.println(newBlock + "       newBlock");

				fileAllocationTable[indexOfLastBlock(name)] = newBlock;
				fileAllocationTable[newBlock] = -1;

				for (int i = mainCatalog.get(fileNumber).size % 32; i < 32; i++) {
					dataArea[((indexOfLastBlock(name) -1) * 32) + i] = contentArray[charactersAdded];
					charactersAdded++;
					sizeIncrease++;
					if (charactersAdded == content.length()) {
						mainCatalog.get(fileNumber).size += sizeIncrease;
						response = "File " + name + " has been extended..";
						System.out.println(response);
						return;
					}
				}
				bitVector[newBlock] = true;
				mainCatalog.get(fileNumber).size += sizeIncrease;
				newBlockAmount--;
				sizeIncrease = 0;

			}
			response = "File " + name + " has been extended..";
			System.out.println(response);
		}

	}

	private void createFile(String name, String content) {
		if (!createEmptyFile(name))
			return;
		openFile(name);
		appendToFile(name, content);
		closeFile(name);

	}

	private void showData() {
		int x = 1;
		for (int i = 0; i < DISK_SIZE; i++) {
			if (i % 32 == 0) {
				System.out.print(x + " ");
				if (x < 10)
					System.out.print(" ");
				x++;
			}
			System.out.print(dataArea[i]);
			if ((i + 1) % 8 == 0)
				System.out.print("\t");
			if ((i + 1) % 32 == 0)
				System.out.print("\n");
		}
		System.out.print("\n");
		
	}
	public  void showBitVector(){
        System.out.println("bitVector");
        int y;
        for(int i = 0; i < NUMBER_OF_BLOCKS; i++){
                if(bitVector[i] == false) y = 0;
                else y = 1;
                System.out.print(y+" ");
                if((i+1) % 4 == 0) System.out.print("\n");
        }
        System.out.print("\n");
}

	public static void main(String[] args) {
		System.out.println("START");
		FileSystem raz = new FileSystem();
		raz.createFile("plik1", "aaaaaaaaaaaaa");
		raz.createFile("plik2", "bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb");;
		raz.createFile("plik3", "ccc");
		raz.showData();
		raz.deleteFile("plik2");
		raz.createFile("plik2", "bbbbbbbbbbbbbbbbbbbbbbbbbbbbbb");
		raz.appendToFile("plik2", "1");
		raz.createFile("plik15", "cc2222c");

		raz.showBitVector();
		raz.showData();
		System.out.println("KONIEC");
	}

}
