package InterprocessCommunication;

import java.util.LinkedList;
import java.util.Scanner;

public class Communication
{
	//variables
	private LinkedList<Pipe> pipes = new LinkedList<Pipe>();
	//private static ProcessesManagement processesManagement; //process which is running right now
	
	
	//methods
	public void createPipe(String pipeName)
	{
		for(int i =0;i< pipes.size();i++)
		{
			if(pipes.get(i).getName().equals(pipeName) ) 
				{
					System.out.println("The pipe exists.."); // OMIN¥C CHYBA
					return;
				}
		}
		pipes.add(new Pipe(pipeName));
	}
	public void deletePipe(String pipeName)
	{
		//System.out.println("Podaj nazwe: ");
		//Scanner reader = new Scanner(System.in);
		//name = reader.next();
		for(int i =0;i< pipes.size();i++) // przegl¹d ³¹cz
		{	
			if(pipes.get(i).getName().equals(pipeName) ) pipes.remove(i);
		}
	}
	/*public int openPipe(String name, char sign) // 'r' - read, 'w' - write
	{
		if(sign == 'w') 
		{

			for(int i =0;i< pipes.size();i++) // przegl¹d ³¹cz
			{	
				if(pipes.get(i).getName().equals(name) && pipes.get(i).getWriteOpen() == false && pipes.get(i).getReadOpen() == false ) // szukanie ³¹cza o danej nazwie
				{
					pipes.get(i).setWriteOpen(true); // 
				}
				else
				{
					//synch czekamyy na ustawienie read=false i write=false 
				}
			}
		}
		else if(sign == 'r') 
		{

			for(int i =0;i< pipes.size();i++) // przegl¹d ³¹cz
			{	
				if(pipes.get(i).getName().equals(name) && pipes.get(i).getWriteOpen() == false && pipes.get(i).getReadOpen() == false ) // szukanie ³¹cza o danej nazwie
				{
					pipes.get(i).setReadOpen(true); // 
				}
				else
				{
					//synch //synch czekemay na ustawienie read=false i write=false 
				}
			}
		}
		return 0;
	}
	public int closePipe(String name)
	{

			for(int i =0;i< pipes.size();i++) // przegl¹d ³¹cz
			{	
				if(pipes.get(i).getName().equals(name)) // szukanie ³¹cza o danej nazwie
				{
					pipes.get(i).setWriteOpen(false);
					pipes.get(i).setReadOpen(false);
				}
			}

		return 0;
	}*/
	public void readPipe(String pipeName, int numberOfSigns, int memoryAddress)
	{
		String result = null;
		for(int i =0;i< pipes.size();i++) // przegl¹d ³¹cz
		{	
			if(pipes.get(i).getName().equals(pipeName)) // szukanie ³¹cza o danej nazwie
			{
				pipes.get(i).setAvailable(false); // zajmowanie ³¹cza
				for(int j = 0; j < numberOfSigns; j++)
				{
					if(pipes.get(i).data.size() > 0)  // dodac do kolejki procesów oczekujacych, a¿ coœ pojawi siê w ³¹czu
					{
						// lock(); ??? DAWID, tylko zapis lub odczyt w jednym czasie
						result += pipes.get(i).data.removeFirst(); // 	USUNAC
						//PageTableObiekt.writeToMemory(memoryAddress + j, pipes.get(i).data.removeFirst() ); // zapisywanie do pamiêci, modu³ NATALIA
						// unlock(); ???
					}
					else System.out.println("Nie ma komunikatow."); // USUNAC
				}
				pipes.get(i).setAvailable(true); // oddanie ³¹cza
				return;
			}
		}
		System.out.println("Nie ma takiego lacza."); // synch?
		System.out.println(result); // USUNAC
	}
	public void writePipe(String pipeName, String message)
	{
		for(int i =0;i< pipes.size();i++) // przegl¹d ³¹cz
		{	
			if(pipes.get(i).getName().equals(pipeName)) // szukanie ³¹cza o danej nazwie
			{
				pipes.get(i).setAvailable(false); // zajmowanie ³¹cza

					//dzielenie komunikatu na pojedyncze znaki i zapisywanie do ³¹cza
					for(int j = 0; j < message.length(); j++) 
					{
						// lock(); ?? DAWID, tylko zapis lub odczyt w jednym czasie
						pipes.get(i).data.add((Character)message.charAt(j));
						// unlock(); ??
					}

				pipes.get(i).setAvailable(true); // oddanie ³¹cza
				return;
			}
			
		}
		System.out.println("Nie ma takiego ³¹cza."); // synch?
		
	}
	public void showAllPipes()
	{
		for(int i = 0;i< pipes.size();i++)
		{	
			System.out.print(pipes.get(i).getName() + ": " + pipes.get(i).getData().toString() + '\n');

		}
	}
	public void showOnePipe()
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
		System.out.println("Does not exist!");
	}
	
	
	
	
	
	public static void main(String[] args)
	{
		Communication com = new Communication();
		com.createPipe("p1");
		com.createPipe("p2");
		com.createPipe("p1");
		
		com.writePipe("p1", "hejka1");
		com.writePipe("p1", "hejka2");
		com.writePipe("p1", "hejka3");
		com.writePipe("p1", "hejka4");
		com.writePipe("p1", "hejka5");
		com.writePipe("p2", "qqqq");
		com.writePipe("p2", "wwww");
		com.readPipe("p1", 3, 10);
		com.writePipe("p1", "hejka5.2");
		com.writePipe("p1", "hejka6");
		com.createPipe("p2");
		com.showAllPipes();
		com.deletePipe("p2");
		com.showAllPipes();

	}

}
