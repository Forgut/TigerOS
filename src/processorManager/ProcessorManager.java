package processorManager;

import java.util.ArrayList;
import java.util.Arrays;


import Interpreter.Interpreter;
import processManagement.lista_procesow;
import processManagement.process_control_block;

public class ProcessorManager {

	private process_control_block processesManagement;
    
    private Interpreter interpreter;
    
    boolean arr[] = new boolean[16];
    		
    public static  process_control_block idleProcess;//proces bezczynnosci;
    lista_procesow listaS;
    public   process_control_block Running;//aktualnie dzialajacy proces
    public process_control_block NextRunningProcess;//kolejny proces do uruchomienia
    
    public ArrayList<ArrayList<process_control_block>> lista;//stworzenie kolejki priorytetowej

        
    
    
	public ProcessorManager(Interpreter interpreter,lista_procesow listaS)
	{
		this.listaS=listaS;
		this.interpreter=interpreter;
		lista = new ArrayList<ArrayList<process_control_block>>();
		for (int i=0;i<16;i++) {
			lista.add(new ArrayList<process_control_block>());
		}
		idleProcess=listaS.getPCB(0);
		Running=idleProcess;
		
	}	

	public void FindReadyThread()//zrzekanie sie albo uplyniecie kwantu czasu
	{
		CheckBiggest();
		changerunningProcess();
	}
	
	public void ReadyThread(process_control_block Temp)//decyduje o przyszłosci procesu w momencie zwiekszenia sie jego kwantu czasu 
	{
		lista.get(Temp.getPriorytet_dynamiczny()).remove(Temp);
		if(lista.get(Temp.getPriorytet_dynamiczny()).size()==0)
		{
			arr[Temp.getPriorytet_dynamiczny()]=false;
		}
		if(Temp.getPriorytet_dynamiczny()<6)
		{
			Temp.INCPriorytet_Dynamiczny();
		}
		Temp.INCPriorytet_Dynamiczny();
		
		AddProcess(Temp);
		
		System.out.println("Podnioslo Priorytet");
	}
    	
	public void Starving()//zwiekszanie priorytetow w po uplynieciu kwantu czasu
	{
		
		for(int i=6;i>0;i--)//sprawdzanie tylko dla priorytetow 1-6
		{
			
			if(arr[i]==true)
			{
				for(int b=lista.get(i).size()-1;b>0;b--)
				{
					
						
						
						if(lista.get(i).get(b).getLicznik_wykonanych_rozkazow()==3)//decydowanie co ile rozkazow wykonanych ma zmienic sie priorytet
						{
						
							lista.get(i).get(b).SetLicznik_wykonanych_rozkazow(0);//resetowanie licznika
						
							process_control_block temp;//zmienna pomocnicza do przelozenia procesu w liscie na nowe należne jej miejsce
							temp=lista.get(i).get(b);
							
							ReadyThread(temp);
						}
					
				}
					

			}
		}
	}
	
	public boolean CheckBiggest()//sprawdza czy znajduje sie proces wiekszy od aktualnie wykonywanego, jesli tak ustawia go na jako NextRunningProcess
	{
		if(Running.getStan()==2)
		{
			Running=idleProcess;
		}
		
		int temp=Running.getPriorytet_dynamiczny();		
		
		if(temp>7)
		{
			for(int i=15;i>7;i--)//sprawdzanie jesli Running jest czasu rzeczywistego
			{
				if(arr[i]==true)
				{
					NextRunningProcess=lista.get(i).get(0);
					
					
					if(lista.get(i).isEmpty()==true)
						
					{
						arr[i]=false;
					}
					
					return true;
				}
				
			}
			NextRunningProcess=idleProcess;
			return false;
			
		}
		else
		{			
			for(int i=15;i>temp;i--)//sprawdzanie czy sa wieksze ktore powinny wywlaszczyc
			{
				if(lista.get(i).size()>0)
				{
					
					NextRunningProcess=lista.get(i).get(0);//sprawdzanie tylko 1 poniwaz reszta nie ma sensu
					
					
					if(lista.get(i).isEmpty()==true)//kontrolowanie arr
						
					{
						arr[i]=false;
					}
					Running=NextRunningProcess;
					return true;
				}
				
			}
			for(int i=7;i>0;i--)//znalezienie innego zamiennika na przyszlosc
			{
				if(lista.get(i).size()>0)
				{
					NextRunningProcess=lista.get(i).get(0);
					
					
					if(lista.get(i).isEmpty()==true)
						
					{
						arr[i]=false;
					}
					return false;
				}
				
			}			
			NextRunningProcess=idleProcess;
			return false;
		}
		
	}	
	
	public void Clear()
	{
		
		for(int i=8;i>=0;i--)//sprawdzanie tylko dla priorytetow 1-7
		{
			
			if(arr[i]==true)
			{
				for(int b=lista.get(i).size()-1;b>=0;b--)
				{
					
						if(lista.get(i).get(b).getStan()==2)
						{
							
							lista.get(i).remove(b);
							if(lista.get(i).size()==0)
							{
								arr[Running.getPriorytet_dynamiczny()]=false;
							}
						}
																	
				}
					

			}
		}
	}
	
	public void IncreaseCounter()
	{
		for(int i=7;i>=0;i--)//sprawdzanie tylko dla priorytetow 1-7
		{
			
			if(lista.get(i).size()>0)
			{
				for(int b=0;b>lista.get(i).size();b++)
				{
					
						lista.get(i).get(b).INCLicznik_wykonanych_rozkazow();
						System.out.println("Podniesiono licznik dla "+ lista.get(i).get(b).getID());
																	
				}
					

			}
		}
	}
	
	public void AddProcess(process_control_block Temp)//dodawanie procesu do kolejki priorytetowej
	{
		lista.get(Temp.getPriorytet_dynamiczny()).add(Temp);
		if(lista.get(Temp.getPriorytet_dynamiczny()).size()>0)
		{
			arr[Temp.getPriorytet_dynamiczny()]=true;
		}
		

		if(CheckBiggest()==true)
		{
			changerunningProcess();
		}

	}
	
	public void GetReady()// dodawanie procesow gotowych do kolejki priorytetowej
	{
		int i=0;
		do
		{
			process_control_block temp1;
			temp1=listaS.getPCB(i);
			
			if(temp1==Running)
			{
				break;
			}
			if(temp1.getStan()==2)
			{
				break;
			}
			
			if(arr[temp1.getPriorytet_dynamiczny()]==false)
			{
				if(temp1.getPriorytet_bazowy()==0)
				{
					AddProcess(temp1);
					break;
				}
				
				AddProcess(temp1);
				i++;
			}
			else
			{
			if(temp1.getPriorytet_bazowy()==0)
			{
				if(lista.get(temp1.getPriorytet_dynamiczny()).contains(temp1)==true)//sprawdzenie czy znajduje sie w liscie oraz dodac i 
				{
					break;
				}
				else
				{
					
					AddProcess(temp1);
					
				}
				
				break;
			}
			
			if(lista.get(temp1.getPriorytet_dynamiczny()).contains(temp1)==true)//sprawdzenie czy znajduje sie w liscie oraz dodac i 
			{
				break;
			}
			else
			{
				
				AddProcess(temp1);
				i++;
			}
			}
		}while(true);
	}
	
	public void changerunningProcess()//bierze aktualnie dzialajacy proces, umieszcza go na poczatku kolejki oraz zmienia aktualnie wykonywany proces
	{
		if(Running.getStan()==0)
		{
			if(Running.getPriorytet_bazowy()!=0) {
				lista.get(Running.getPriorytet_dynamiczny()).add(0,Running);
			}
		}
		
		Running=NextRunningProcess;
		if(Running.getPriorytet_bazowy()!=0)
		{
			lista.get(Running.getPriorytet_dynamiczny()).remove(Running);
			
		}
		
		if(lista.get(Running.getPriorytet_dynamiczny()).size()==0)
		{
			arr[Running.getPriorytet_dynamiczny()]=false;
		}
		
		NextRunningProcess=idleProcess;
	}

	public void Scheduler()//tu sie wszystko dzieje, z tego miejsca wszystko jest wywoływane 
	{
		
		GetReady();
		
		Starving();
		
		if(Running.getStan()==1)//sprawdzanie czy aktualnie dzialajacy procesor nie jest w stanie oczekujacym
		{
			FindReadyThread();
			
			interpreter.RUN(Running);//odpalanie interpretera			
		}
		else 
		{
			if(CheckBiggest()) {
				NextRunningProcess.getPriorytet_bazowy();
				changerunningProcess();
			}
				
		interpreter.RUN(Running);//odpalanie interpretera
		if(Running.getStan()==2)
		{
			Clear();
			Running=idleProcess;
			NextRunningProcess=idleProcess;
		}
		}
		
		IncreaseCounter();		
		if(Running.getPriorytet_dynamiczny()>Running.getPriorytet_bazowy()) {
			Running.DECPriorytet_Dynamiczny();
		}
		
		
		
		
	}

	public void showQueue()
	{
		for(int i=15;i>=0;i--)//sprawdzanie tylko dla priorytetow 1-7
		{
			
			if(arr[i]==true)
			{
				for(int b=lista.get(i).size()-1;b>=0;b--)
				{
					
						lista.get(i).get(b).print();

																	
				}
					

			}
		}
	}
	
	public void showRunning() //pokazywanie aktualnie wykonywanego procesu
	{
		if(Running!=idleProcess)
		{
			 System.out.println("\n\nAktualnie jest wykonywany proces, jego informacje to:  \n\n");
	         Running.print();
		}
		else
		{
			System.out.println("\n\nAktualnie nie ma procesu wykonywanego \n\n");
		}
	}
	
}