import java.util.LinkedList;

public class Pipe
{
	String name;
	MutexLock lock = new MutexLock();
	LinkedList<Character> data = new LinkedList<Character>();
	
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
