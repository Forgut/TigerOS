package Communication;

import java.util.LinkedList;
import java.util.Scanner;

import processManagement.process_control_block;

public class Communication
{
	//variables
	private process_control_block pcb; //process running
	public Communication(process_control_block pcb)
	{
		this.pcb = pcb;
	}
	
	//methods
	public int createPipe(String pipeName)
	{
		for(int i =0;i< Pipe.pipes.size();i++)
		{
			if(Pipe.pipes.get(i).getName().equals(pipeName) ) 
				{
					System.out.println("Communication: The pipe already exists.");
					return 0;
				}
		}
		Pipe.pipes.add(new Pipe(pipeName));
		System.out.println("Communication: Pipe created");
		return 1;
	}
	public int deletePipe(String pipeName)
	{
		for(int i =0;i< Pipe.pipes.size();i++) // przegląd łącz
		{	
			if(Pipe.pipes.get(i).getName().equals(pipeName) ) 
			{
				Pipe.pipes.remove(i);
				System.out.println("Communication: Pipe deleted.");
				return 1;
			}
		}
		System.out.println("Communication: The pipe does not exist.");
		return 0;
	}
	public int readPipe(String pipeName, int numberOfSigns, int memoryAddress)
	{
		for(int i =0;i< Pipe.pipes.size();i++) // przegad lacz
		{
			if(Pipe.pipes.get(i).getName().equals(pipeName)) // szukanie lacza o danej nazwie
			{
				Pipe.pipes.get(i).lock.lock(pcb);
				
				for(int j = 0; numberOfSigns - Pipe.pipes.get(i).offset > 0; j++) // czytanie po jednym znaku
				{
					if(Pipe.pipes.get(i).data.size() == 0) 
						{	
							Pipe.pipes.get(i).lock.unlock();	// jezeli pusty to unlock		
							Pipe.pipes.get(i).lock.addToQueue(pcb);
							break;
						}
					else 
						{									
							pcb.pageTable.writeToMemory(memoryAddress + Pipe.pipes.get(i).offset, Pipe.pipes.get(i).data.removeFirst(), pcb.getID() ); // zapisywanie do pameci modul: NATALIA 
							Pipe.pipes.get(i).offset++;
							Pipe.pipes.get(i).lock.unlock();
						}
					if(j == numberOfSigns-1) 
						{
							Pipe.pipes.get(i).offset = 0;
							return 1;
						}
				}
				//Pipe.pipes.get(i).lock.unlock();
				
				return 2;
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
		for(int i =0;i< Pipe.pipes.size();i++) // przegląd łącz
		{	
			if(Pipe.pipes.get(i).getName().equals(pipeName)) // szukanie łącza o danej nazwie
			{
					for(int j = 0; j < message.length(); j++) //dzielenie komunikatu na pojedyncze znaki i zapisywanie do łącza
					{
						Pipe.pipes.get(i).lock.lock(pcb);
						Pipe.pipes.get(i).data.add((Character)message.charAt(j));
						if(Pipe.pipes.get(i).data.size() == 0) isEmpty = true; 
						else isEmpty = false;
						Pipe.pipes.get(i).lock.unlock();
					}
					System.out.println("Communication: Saved in pipe.");
					showAllPipes();
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
		for(int i = 0;i< Pipe.pipes.size();i++)
		{	
			System.out.print(Pipe.pipes.get(i).getName() + ": " + Pipe.pipes.get(i).getData().toString() + '\n');
		}
	}
	public void showOnePipe() //raczej niepotrzebne
	{
		System.out.println("Choose: ");
		for(int i = 0;i< Pipe.pipes.size();i++) // wypisanie dostępnych łączy
		{	
			System.out.println(Pipe.pipes.get(i).getName());
		}
		Scanner in = new Scanner(System.in);
		System.out.println();
		String inS = in.next(); // wczytanie dolecowej nazwy łącza

		for(int i = 0;i< Pipe.pipes.size();i++) // znalezienie docelowego łącza
		{	
			if(inS.equals(Pipe.pipes.get(i).getName().toString())) 
				{
					System.out.println(Pipe.pipes.get(i).getName() + ": " + Pipe.pipes.get(i).getData().toString());
					return;
				}
		}
		System.out.println("Communication: Does not exist!");
	}

}