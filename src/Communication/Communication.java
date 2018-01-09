package Communication;

import java.util.LinkedList;
import java.util.Scanner;

import processManagement.process_control_block;

public class Communication
{
	//variables
	private LinkedList<Pipe> pipes = new LinkedList<Pipe>();
	private process_control_block pcb; //process running
	
	public Communication(process_control_block pcb)
	{
		this.pcb = pcb;
	}
	
	//methods
	public int createPipe(String pipeName)
	{
		for(int i =0;i< pipes.size();i++)
		{
			if(pipes.get(i).getName().equals(pipeName) ) 
				{
					System.out.println("Communication: The pipe already exists.");
					return 0;
				}
		}
		pipes.add(new Pipe(pipeName));
		System.out.println("Communication: Pipe created.");
		return 1;
	}
	public int deletePipe(String pipeName)
	{
		for(int i =0;i< pipes.size();i++) // przegl¹d ³¹cz
		{	
			if(pipes.get(i).getName().equals(pipeName) ) 
			{
				pipes.remove(i);
				System.out.println("Communication: Pipe deleted.");
				return 1;
			}
		}
		System.out.println("Communication: The pipe does not exist.");
		return 0;
	}
	public int readPipe(String pipeName, int numberOfSigns, int memoryAddress)
	{
		for(int i =0;i< pipes.size();i++) // przegl¹d ³¹cz
		{	
			if(pipes.get(i).getName().equals(pipeName)) // szukanie ³¹cza o danej nazwie
			{
				for(int j = 0; j < numberOfSigns; j++) // czytanie po jednym znaku
				{
					pipes.get(i).lock.lock(pcb);					
					if(pipes.get(i).data.size() == 0) pipes.get(i).lock.unlock();	// jezeli pusty to unlock		
					else 
						{						
							int ID = pcb.getID();
							pcb.pageTable.writeToMemory(memoryAddress + j, pipes.get(i).data.removeFirst(), ID ); // zapisywanie do pamiêci, modu³ NATALIA 
							pipes.get(i).lock.unlock();
						}
				}
				return 1;
			}
			else 
			{	
				System.out.println("Communication: The pipe does not exist."); // TODO: komunikat czy nie
				return 0;
			}
		}
		System.out.println("Communication: The pipe does not exist."); // TODO: komunikat czy nie
		return 0;
		
	}
	public int writePipe(String pipeName, String message)
	{
		//this.pcb = pcb;
		boolean isEmpty = false;
		for(int i =0;i< pipes.size();i++) // przegl¹d ³¹cz
		{	
			if(pipes.get(i).getName().equals(pipeName)) // szukanie ³¹cza o danej nazwie
			{
					//dzielenie komunikatu na pojedyncze znaki i zapisywanie do ³¹cza
					for(int j = 0; j < message.length(); j++) 
					{
						pipes.get(i).lock.lock(pcb);
						pipes.get(i).data.add((Character)message.charAt(j));
						if(pipes.get(i).data.size() == 0) isEmpty = true; 
						else isEmpty = false;
						pipes.get(i).lock.unlock();
					}
				return 1;
			}
			else 
			{	
				System.out.println("Communication: The pipe does not exist.");
				return 0;
			}
		}	
		System.out.println("Communication: The pipe does not exist.");
		return 0;
	}
	public void showAllPipes()
	{
		System.out.println("Communication: Pipes: ");
		for(int i = 0;i< pipes.size();i++)
		{	
			System.out.print(pipes.get(i).getName() + ": " + pipes.get(i).getData().toString() + '\n');
		}
	}
	public void showOnePipe() //raczej niepotrzebne
	{
		System.out.println("Choose: ");
		for(int i = 0;i< pipes.size();i++) // wypisanie dostêpnych ³¹czy
		{	
			System.out.println(pipes.get(i).getName());
		}
		Scanner in = new Scanner(System.in);
		System.out.println();
		String inS = in.next(); // wczytanie dolecowej nazwy ³¹cza

		for(int i = 0;i< pipes.size();i++) // znalezienie docelowego ³¹cza
		{	
			if(inS.equals(pipes.get(i).getName().toString())) 
				{
					System.out.println(pipes.get(i).getName() + ": " + pipes.get(i).getData().toString());
					return;
				}
		}
		System.out.println("Communication: Does not exist!");
	}

}