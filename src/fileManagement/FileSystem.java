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

	// Secondary method for checking value of file's last block index.
	private int indexOfLastBlock(String name) {
		int fileNumber = fileNumberSetter(name);
		int lastBlockIndex = mainCatalog.get(fileNumber).indexOfFirstBlock;
		int lastBlockValue = fileAllocationTable[lastBlockIndex];
		while (lastBlockValue != -1) {
			if (lastBlockValue == -2)
				break;
			lastBlockIndex = lastBlockValue;
			lastBlockValue = fileAllocationTable[lastBlockIndex];
		}
		return lastBlockIndex;
	}

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

	// Open file. If it finds file with exact name changes its open flag to "true";
	public void openFile(String name) {
		for (int i = 0; i < mainCatalog.size(); i++) {
			if (mainCatalog.get(i).name == name) {
				// TODO: Dodaæ mutexlock :/
				mainCatalog.get(i).open = true;
				System.out.println("FileSystem: File " + name + " opened.");
				break;
			}
		}
	}

	// Open file. If it finds file with exact name changes its open flag to "true";
	public void closeFile(String name) {
		for (int i = 0; i < mainCatalog.size(); i++) {
			if (mainCatalog.get(i).name == name) {
				// TODO: Dodaæ mutexlock :/
				mainCatalog.get(i).open = false;
				System.out.println("FileSystem: File " + name + " closed.");
				break;
			}
		}
	}

	// Return size of file.
	private int getFileSize(String name) {
		int size = 0;
		for (int i = 0; i < mainCatalog.size(); i++) {
			if (mainCatalog.get(i).name.equals(name)) {
				size = mainCatalog.get(i).size;
				break;
			}
		}
		return size;
	}

	// Return size of file.
	private boolean getFileFlagStatus(String name) {
		boolean open = false;
		for (int i = 0; i < mainCatalog.size(); i++) {
			if (mainCatalog.get(i).name.equals(name)) {
				open = mainCatalog.get(i).open;
				break;
			}
		}
		return open;
	}

	// Return firstIndex of file.
	private int getFileFirstIndex(String name) {
		int index = 0;
		for (int i = 0; i < mainCatalog.size(); i++) {
			if (mainCatalog.get(i).name.equals(name)) {
				index = mainCatalog.get(i).indexOfFirstBlock;
				break;
			}
		}
		return index;
	}

	// Check if file have space in last block.
	private boolean isFileHaveSpace(String name) {
		boolean have = false;
		if (getFileSize(name) % BLOCK_SIZE != 0 || getFileSize(name) == 0)
			have = true;
		return have;
	}

	// Return number of free blocks.
	public int numberOfFreeBlocks() {
		int freeBlocks = 0;
		for (int i = 0; i < NUMBER_OF_BLOCKS; i++)
			if (bitVector[i] == false)
				freeBlocks++;
		return freeBlocks;
	}

	// Check if new data will fit in memory.
	private boolean isEnoughMemory(String name, int neededSpace) {
		boolean isEnough = false;
		int freeMemory = BLOCK_SIZE * numberOfFreeBlocks();
		if (isFileHaveSpace(name)) {
			int size = getFileSize(name);
			int howMany = size % 32;
			freeMemory += howMany;
		}
		if (freeMemory >= neededSpace)
			isEnough = true;
		return isEnough;

	}

	// Return amount of free space in last block of file.
	private int getNumberOfFreeSpaceInLastBlock(String name) {
		int freeSpace = 0;
		freeSpace = BLOCK_SIZE - getFileSize(name) % BLOCK_SIZE;
		return freeSpace;
	}

	// Increase size of updated file.
	private void increaseFileSize(String name, int amount) {
		for (int i = 0; i < mainCatalog.size(); i++) {
			if (mainCatalog.get(i).name.equals(name)) {
				mainCatalog.get(i).size += amount;
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

	// Fill new block with data.
	private void fillNewBlock(int newBlock, String charTable) {
		int index = newBlock * BLOCK_SIZE;
		for (int i = 0; i < charTable.length(); i++, index++)
			dataArea[index] = charTable.charAt(i);
	}

	// Fill with data block which still has some free space to use.
	private void fillNotFullBlock(String name, String charTable) {
		int index = indexOfLastBlock(name) * BLOCK_SIZE + getFileSize(name) % BLOCK_SIZE;
		for (int i = 0; i < charTable.length(); i++) {
			dataArea[index] = charTable.charAt(i);
			index++;
		}
	}

	// Amplify file entry in FAT.
	private void amplifyEntry(String name, int newBlock) {
		fileAllocationTable[indexOfLastBlock(name)] = newBlock;
		fileAllocationTable[newBlock] = -1;

	}

	// Making amount of file blocks narrower by removing last FAT node.
	private void taperEntry(String name) {
		int lastBlock = indexOfLastBlock(name);
		for (int i = 0; i < NUMBER_OF_BLOCKS; i++) {
			if (fileAllocationTable[i] == lastBlock)
				fileAllocationTable[i] = -1;
		}
		fileAllocationTable[lastBlock] = -2;
	}

	// Remove any informations from catalog about file.
	private void removeDirectoryEntry(String name) {
		for (int i = 0; i < mainCatalog.size(); i++) {
			if (mainCatalog.get(i).name.equals(name)) {
				mainCatalog.remove(i);
			}
		}
	}

	// Creating a empty file.
	public int createEmptyFile(String name) {
		if (!checkNameAvailability(name)) {
			System.out.println("FileSystem: Name " + name + " is occupied");
			return 0;
		} else {
			int newBlock = findFreeBlock();
			if (newBlock != -1) {
				bitVector[newBlock] = true;
				File plik = new File(name, 0, newBlock);
				mainCatalog.add(plik);
				fileAllocationTable[newBlock] = -1;
				System.out.println("FileSystem: File " + name + " was created.");
				return 1;
			} else {
				System.out.println("FileSystem: Cannot find free block. File cannot be created.");
				return 0;
			}
		}
	}

	// Create file with data.
	public int createFile(String name, String content) {
		if (createEmptyFile(name) == 0)
			return 0;
		else {
			//openFile(name);
			appendToFile(name, content);
			for (int i = 0; i < mainCatalog.size(); i++) {
				if (mainCatalog.get(i).name.equals(name)) {
					 mainCatalog.get(i).open = false;
					break;
				}
			}
			//closeFile(name);
			return 1;
		}

	}

	// Add text to file.
	public int appendToFile(String name, String content) {
		if (!getFileFlagStatus(name)) {
			System.out.println("FileSystem: Cannot append to file. File is closed.");
			return 0;
		}
		int indexChar = 0;
		String charTable = "";
		if (!checkNameAvailability(name)) {
			if (isEnoughMemory(name, content.length())) {
				if (isFileHaveSpace(name)) {
					for (; indexChar < getNumberOfFreeSpaceInLastBlock(name)
							&& indexChar < content.length(); indexChar++)
						charTable += content.charAt(indexChar);
					fillNotFullBlock(name, charTable);
					increaseFileSize(name, charTable.length());

				}
				while (indexChar < content.length()) {
					charTable = "";
					for (int i = 0; i < BLOCK_SIZE && indexChar < content.length(); i++, indexChar++)
						charTable += content.charAt(indexChar);
					int index = findFreeBlock();
					amplifyEntry(name, index);
					fillNewBlock(index, charTable);
					bitVector[index] = true;
					increaseFileSize(name, charTable.length());
				}
				return 1;
			} else {
				System.out.println("FileSystem: Cannot append to file. Low memory.");
				return 0;
			}
		} else {
			System.out.println("FileSystem: File " + name + " doesn't exist.");
			return 0;
		}
	}

	// Delete file.
	public int deleteFile(String name) {
		if (!checkNameAvailability(name)) {
			int size = getFileSize(name);
			if (size > 0) {
				int blocks = size / BLOCK_SIZE;
				if (size % BLOCK_SIZE != 0) {
					blocks++;
				}
				while (blocks > 0) {
					int index = indexOfLastBlock(name);
					for (int i = index * BLOCK_SIZE; i < index * BLOCK_SIZE + BLOCK_SIZE; i++) {
						dataArea[i] = ' ';
					}
					bitVector[index] = false;
					taperEntry(name);
					blocks--;
				}
				removeDirectoryEntry(name);
				System.out.println("FileSystem: File " + name + " removed.");

			}
			return 1;
		} else {
			System.out.println("FileSystem: File " + name + " doesn't exist.");
			return 0;
		}
	}

	// Prints file's content.

	public void readFile(String name) {
		if (!getFileFlagStatus(name)) {
			System.out.println("FileSystem: Cannot append to file. File is closed.");
			return;
		}

		int size = getFileSize(name);
		if (size == 0) {
			System.out.println("FileSystem: File " + name + " is empty.");
			return;
		}
		int index = getFileFirstIndex(name);
		System.out.println("FileSystem: File " + name + " contains: \n");
		do {
			for (int i = index * BLOCK_SIZE; i < index * BLOCK_SIZE + 32; i++) {
				System.out.print(dataArea[i]);
			}
			int newIndex = fileAllocationTable[index];
			index = newIndex;
			if (index == -1)
				break;
		} while (true);

	}

	// Prints value of every memory cell.
	public void showData() {
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

	// Shows which blocks are used and which aren't.
	public void showBitVector() {
		System.out.println("bitVector:");
		int y;
		for (int i = 0; i < NUMBER_OF_BLOCKS; i++) {
			if (bitVector[i] == false)
				y = 0;
			else
				y = 1;
			System.out.print(y + " ");
			if ((i + 1) % 16 == 0)
				System.out.print("\n");
		}
		System.out.print("\n");
	}

	// Shows values of file allocation table.
	public void showFAT() {
		System.out.println("File Allocation Table:");
		for (int i = 1; i <= NUMBER_OF_BLOCKS; i++) {
			System.out.print("[");
			System.out.print(i + ": ");
			if (fileAllocationTable[i - 1] == -1)
				System.out.print(-1);
			else if (fileAllocationTable[i - 1] != -2)
				System.out.print(fileAllocationTable[i - 1] + 1);
			System.out.print("]");
			if ((i) % 8 == 0)
				System.out.print("\n");
		}
		System.out.print("\n");
	}

	// Prints every file from catalog and its informations.
	public void showMainCatalog() {
		System.out.println("mainCatalog:");
		if (mainCatalog.isEmpty()) {
			System.out.println("Main Catalog is Empty");
		} else {
			System.out.println("NR\tNAME\tFIRST BLOCK\tSIZE");
			for (int i = 0; i < mainCatalog.size(); i++) {
				System.out.print((i + 1) + "\t");
				System.out.print(mainCatalog.get(i).name + "\t");
				System.out.print(mainCatalog.get(i).indexOfFirstBlock + "\t\t");
				System.out.print(mainCatalog.get(i).size);
				System.out.print("\n");
			}
		}
		System.out.print("\n");
	}

	public static void main(String[] args) {
		
		
		/*FileSystem SystemTest = new FileSystem();
		SystemTest.createFile("plik1", "MV A 5,MV C A,SB C 1,ML A C,JX C 14,EX;");
		SystemTest.createFile("plik2", "MV A 2,MV B 3,AD A B,CF plik A,RF plik,EX;");

		SystemTest.createFile("plik3", "XC proces1 prog1.txt,XC proces2 prog2.txt,XP potok,EX;");
		SystemTest.createFile("plik4", "XS potok 5,EX;");
		SystemTest.createFile("plik5", "XR potok 1 500,MV A B,AD B 5,ML A B,XE potok,EX;");
		
		SystemTest.deleteFile("plik2");
		SystemTest.showBitVector();
		SystemTest.showData();
		SystemTest.showMainCatalog();
		SystemTest.showFAT();
*/
	}

}