package processManagement;

import java.util.ArrayList;

import memorymanagement.Memory;
import processManagement.process_control_block;

public class lista_procesow {
	private ArrayList<process_control_block> wszystkie_procesy;

	public lista_procesow(Memory memory) {
		wszystkie_procesy = new ArrayList<process_control_block>();
		wszystkie_procesy.add(new process_control_block(0, memory));
	}

	public void add_to_list(process_control_block pcb) {
		if (pcb.getStan() == 0)
			wszystkie_procesy.add(0, pcb);
		else
			wszystkie_procesy.add(pcb);
	}

	public int delete_from_list(int id) { // zwraca 0 jak sie udalo 1 jak nie
		boolean czy_usunieto = false;
		for (int i = 0; i < wszystkie_procesy.size(); i++) {
			if (wszystkie_procesy.get(i).getID() == id) {
				wszystkie_procesy.remove(i);
				czy_usunieto = true;
				return 0;
			}
			if (!czy_usunieto)
				return 1;
		}
		return 1;
	}

	public void delete_on_iter(int i) {
		wszystkie_procesy.remove(i);
	}

	public void gotowe_na_poczatek() {
		for (int i = wszystkie_procesy.size() - 1; i < wszystkie_procesy.size(); i--) {
			if (wszystkie_procesy.get(i).getStan() == 0) {
				wszystkie_procesy.set(0, wszystkie_procesy.get(i));
				wszystkie_procesy.remove(i);
			}
		}
	}

	ArrayList<process_control_block> getWszystkie_procesy() {
		return wszystkie_procesy;
	}

	public int size() {
		return wszystkie_procesy.size();
	}

	public process_control_block getPCB(int iter) {
		return wszystkie_procesy.get(iter);
	}

	public process_control_block getPCB_by_ID(int id) {
		for (process_control_block e : wszystkie_procesy) {
			if (e.getID() == id) {
				return e;
			}
		}
		return null;
	}
}
