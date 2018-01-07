package Communication;

import java.util.LinkedList;
import java.util.Scanner;

import processManagement.process_control_block;
import memorymanagement.Memory;

public class Communication
{
	//variables
	private LinkedList<Pipe> pipes = new LinkedList<Pipe>();
	private process_control_block pcb; //process running
	private Memory memory; //process running
	
	public Communication(process_control_block pcb, Memory memory)
	{
		this.pcb = pcb;
		this.memory = memory;
	}
	
	//methods
	public int createPipe(String pipeName)
	{
		for(int i =0;i< pipes.size();i++)
		{
			if(pipes.get(i).getName().equals(pipeName) ) 
				{
					System.out.println("The pipe already exists.");
					return 0;
				}
		}
		pipes.add(new Pipe(pipeName));
		return 1;
	}
	public int deletePipe(String pipeName)
	{
		for(int i =0;i< pipes.size();i++) // przegląd łącz
		{	
			if(pipes.get(i).getName().equals(pipeName) ) 
			{
				pipes.remove(i);
				return 1;
			}
		}
		System.out.println("The pipe does not exist.");
		return 0;
	}
	public int readPipe(String pipeName, int numberOfSigns, int memoryAddress)
	{
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
				return 1;
			}
			else 
			{	
				System.out.println("The pipe does not exist."); // TODO: komunikat czy nie
				return 0;
			}
		}
		
	}
	public int writePipe(String pipeName, String message)
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
				return 1;
			}
			else 
			{	
				System.out.println("The pipe does not exist.");
				return 0;
			}
		}		
	}
	public void showAllPipes()
	{
		System.out.println("Pipes: ");
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
