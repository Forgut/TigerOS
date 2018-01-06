import java.util.LinkedList;
import java.util.Scanner;

public class Communication
{
	//variables
	private LinkedList<Pipe> pipes = new LinkedList<Pipe>();
	//private process_control_block pcb; //process which is running right now
	
	//methods
	public void createPipe(String pipeName)
	{
		for(int i =0;i< pipes.size();i++)
		{
			if(pipes.get(i).getName().equals(pipeName) ) 
				{
					// System.out.println("The pipe exists.."); // TODO: komunikat czy nie?
					return;
				}
		}
		pipes.add(new Pipe(pipeName));
	}
	public void deletePipe(String pipeName)
	{
		for(int i =0;i< pipes.size();i++) // przegląd łącz
		{	
			if(pipes.get(i).getName().equals(pipeName) ) pipes.remove(i);
		}
	}
	public void readPipe(String pipeName, int numberOfSigns, int memoryAddress, process_control_block pcb)
	{
		//this.pcb = pcb;
		for(int i =0;i< pipes.size();i++) // przegląd łącz
		{	
			if(pipes.get(i).getName().equals(pipeName)) // szukanie łącza o danej nazwie
			{
				for(int j = 0; j < numberOfSigns; j++)
				{
					boolean isEmpty = false;
					if(pipes.get(i).data.size() == 0) 
						{
							isEmpty = true; // jeśli pusty
							pipes.get(i).lock.lock(pcb,isEmpty);
						}
					else 
						{
							isEmpty = false; // jeśli coś zawiera
							pipes.get(i).lock.lock(pcb);
						}
					
					PageTableObiekt.writeToMemory(memoryAddress + j, pipes.get(i).data.removeFirst() ); // zapisywanie do pamięci, moduł NATALIA 
					if(pipes.get(i).data.size() == 0) isEmpty = true; 
					else isEmpty = true;
					pipes.get(i).lock.unlock();
				}
				return;
			}
		}
		// System.out.println("Nie ma takiego lacza."); // TODO: komunikat czy nie
	}
	public void writePipe(String pipeName, String message, process_control_block pcb)
	{
		//this.pcb = pcb;
		boolean isEmpty = false;
		for(int i =0;i< pipes.size();i++) // przegląd łącz
		{	
			if(pipes.get(i).getName().equals(pipeName)) // szukanie łącza o danej nazwie
			{
					//dzielenie komunikatu na pojedyncze znaki i zapisywanie do łącza
					for(int j = 0; j < message.length(); j++) 
					{
						pipes.get(i).lock.lock(pcb);
						pipes.get(i).data.add((Character)message.charAt(j));
						if(pipes.get(i).data.size() == 0) isEmpty = true; 
						else isEmpty = false;
						pipes.get(i).lock.unlock();
					}
				return;
			}
		}
		// System.out.println("Nie ma takiego łącza."); // TODO: komunikat czy nie?
		
	}
	public void showAllPipes()
	{
		for(int i = 0;i< pipes.size();i++)
		{	
			System.out.print(pipes.get(i).getName() + ": " + pipes.get(i).getData().toString() + '\n');
		}
	}
	public void showOnePipe() //raczej niepotrzebne
	{
		System.out.println("Choose: ");
		for(int i = 0;i< pipes.size();i++) // wypisanie dostępnych łączy
		{	
			System.out.println(pipes.get(i).getName());
		}
		Scanner in = new Scanner(System.in);
		System.out.println();
		String inS = in.next(); // wczytanie dolecowej nazwy łącza

		for(int i = 0;i< pipes.size();i++) // znalezienie docelowego łącza
		{	
			if(inS.equals(pipes.get(i).getName().toString())) 
				{
					System.out.println(pipes.get(i).getName() + ": " + pipes.get(i).getData().toString());
					return;
				}
		}
		System.out.println("Does not exist!");
	}

}
