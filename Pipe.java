package InterprocessCommunication;

import java.util.LinkedList;

public class Pipe
{
	String name;
	private Boolean available = true;
	LinkedList<Character> data = new LinkedList<Character>();
	
	public Pipe(String name)
	{
		this.name = name;
	}
	
	
	public String getName()
	{
		return name;
	}
	public void setName(String name)
	{
		this.name = name;
	}
	public Boolean getAvailable()
	{
		return available;
	}
	public void setAvailable(Boolean available)
	{
		this.available = available;
	}
	public LinkedList<Character> getData()
	{
		return data;
	}
	public void setData(LinkedList<Character> data)
	{
		this.data = data;
	}
	
	
	
}
