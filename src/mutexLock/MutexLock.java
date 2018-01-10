package mutexLock;

import java.util.LinkedList;
import java.util.Queue;

import processManagement.process_control_block;

public class MutexLock {
	private Queue<process_control_block> queue;
	private boolean isLocked;
	private process_control_block currentProcess;
	
	public MutexLock() {
		isLocked=false;
		queue = new LinkedList<process_control_block>();
	}
	
	public boolean isLocked() {
		return isLocked;
	}
	
	public void lock(process_control_block pcb) {
		//Jesli zamek jest odblokowany, ustaw aktualny proces w zamku na pcb i zamknij zamek
		if(isLocked == false) {
			isLocked = true;
			currentProcess = pcb;
			System.out.println("Locked lock with pcb: " + pcb.getID());
		}
		//Jesli zamek jest zablokowany, zmien stan procesu na oczekujacy i dodaj go do kolejki
		else {
			System.out.println("Lock locked! Adding " + pcb.getID() + " to queue.");
			//Dodac zmiane state procesu na waiting
			pcb.Setstan(1);
			queue.add(pcb);
		}
	}
	
	public void lock(process_control_block pcb, boolean isEmpty) {
		//Jesli potok jest pusty, dodaj do kolejki
		System.out.println("Lock locked! Adding " + pcb.getID() + " to queue.");
		//Dodac zmiane state procesu na waiting
		pcb.Setstan(1);
		queue.add(pcb);
	}
	
	public void unlock() {
		//Jesli kolejka jest pusta, otworz zamek
		if(queue.isEmpty()) {
			System.out.println("CurrentProcess value before checking queue: " + currentProcess.getID() + ". Queue is empty. Unlocking lock.");
			isLocked = false;
			currentProcess = null;
		}
		//Jesli kolejka nie jest pusta, wez kolejny proces z kolejki i zmien jego stan na gotowy
		else {
			System.out.print("Changing currentProcess from : " +  currentProcess.getID());
			currentProcess = queue.poll();
			currentProcess.Setstan(0);
			System.out.println(" to: " +  currentProcess.getID());
		}
	}
	
	public void addToQueue(process_control_block pcb)
	{
		queue.add(pcb);
		pcb.Setstan(1);
	}
	//Todo later
	/*public void unlock(boolean isEmpty) {
		//Jesli kolejka jest pusta, otworz zamek
		if(queue.isEmpty()) {
			System.out.println("CurrentProcess value before checking queue: " + currentProcess.getID() + ". Queue is empty. Unlocking lock.");
			isLocked = false;
			currentProcess = null;
		}
	}*/
	
	public void trylock(process_control_block pcb) {
		if(isLocked == false) {
			isLocked = true;
			currentProcess = pcb;
		}
	}
}