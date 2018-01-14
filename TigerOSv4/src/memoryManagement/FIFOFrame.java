package memoryManagement;

public class FIFOFrame {
	protected int number;
	protected int ID;
	protected Boolean bit;
	
	public FIFOFrame(int number, int ID) {
		this.number = number;
		this.ID = ID;
		this.bit = true;
	}

}