package Communication;

import java.util.LinkedList;
import mutexLock.MutexLock;
public class Pipe
{
	String name;
	MutexLock lock = new MutexLock();
	LinkedList<Character> data = new LinkedList<Character>();
	//protected static int numberOfPipes = 0;
	protected static LinkedList<Pipe> pipes = new LinkedList<Pipe>();
	public Pipe(String name)
	{
		this.name = name;
	}
	
	public String getName()
	{
		return name;
	}
	public LinkedList<Character> getData()
	{
		return data;
	}

}