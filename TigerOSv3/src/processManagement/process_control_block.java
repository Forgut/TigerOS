package processManagement;
import java.util.Random;
import java.util.Scanner;
import java.util.ArrayList;

import memorymanagement.Memory;
import memorymanagement.PageTable;



//WORKIN IMPORTS THIS SHOULD NOT EXIST
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class process_control_block {
public static int nowe_id=0;
private String Name;
private boolean czasu_rzeczywistego;
private int ID;
private int licznik_rozkazow;
private int priorytet_bazowy;
private int priorytet_dynamiczny;
private int licznik_wykonanych_rozkazow;
/*NATALIA*/
public PageTable pageTable;
public int virtualBase;
private int rozmiar_tablicy_stronic, start_kodu_procesu /*w pliku wymiany*/;
/*Koniec*/
/*Kaper*/
private ArrayList<Integer> numery_rozkazow;
/*Koniec*/
private String file_name; //nazwa pliku w ktorym sie znajduje kod procesu
/*ADAM ???*/
//private klasa_adama rejestry_procesora;
/*koniec*/
private int R1,R2,R3; //rejsetry dla interpretera w ktorych moze sobie zapamietywac rzeczy
						  //Bartoszek tak powiedzial Mili
private int stan;
public void setName(String a) {
	Name=a;
}
private int setPriorytet_Bazowy(int a) {
	if (a<=0 || a>15) return 1; else { //zwraca 1 w przypadku bledu
		priorytet_bazowy=a;
		priorytet_dynamiczny=a;
		return 0; //zwraca zero jezeli wszystko ok
	}
}
public int INCPriorytet_Dynamiczny() {
	if (priorytet_dynamiczny<15) { priorytet_dynamiczny++; return 0; //mozna zwiekszyc priorytet -zwraca 0
	}
	else {
		return 1; //nie udalo sie zwiekszyc priorytetu - zwraca 1
	}
}
public int DECPriorytet_Dynamiczny() {
if (priorytet_dynamiczny > 1 && priorytet_dynamiczny <8 ) {
	priorytet_dynamiczny--;
	return 0;
} else {
	return 1;
}
}
public int INCPriorytet_Dynamiczny(int a) {
	if (priorytet_dynamiczny>0 && priorytet_dynamiczny<7 && !czasu_rzeczywistego) {
		priorytet_dynamiczny++;
		return 0;
	}
	else {
		return 1; //nie udalo sie zwiekszyc priorytetu - zwraca 1
	}
}
public void INCLicznik_wykonanych_rozkazow() {
	licznik_wykonanych_rozkazow++;
}
public void SetLicznik_wykonanych_rozkazow(int a) {
	licznik_wykonanych_rozkazow=a;
}
public int SetLicznik_rozkazow(int a) {
	if (a>=0) {licznik_rozkazow=a;
	return 0;
	}
	else {
		return 1;
	}
}
public void INCLicznik_rozkazow() {
	licznik_rozkazow++;
}
public int Setrozmiar_tablicy_stronic(int a) {
	if (a>=0) {rozmiar_tablicy_stronic=a;
	return 0;
	}
	else {
		return 1;
	}
}
public int SetStart_kodu_procesu(int a) {
	if (a>=0) {
	start_kodu_procesu=a;
	return 0;
	}
	else {
		return 1;
	}
}
public void SetFile_name(String a) {
	file_name=a;
}
public int SetR1(int a){
	if (a>=0) {R1=a; return 0;}else {return 1;}
}
public int SetR2(int a){
	if (a>=0) {R2=a; return 0;}else {return 1;}
}
public int SetR3(int a){
	if (a>=0) {R3=a; return 0;}else {return 1;}
}
public void Setstan(int a) {//0 -gotowy 1 - oczekujacy 2-zakonczony
	stan=a;
}
private int Losuj_priorytet(boolean rzeczywisty) {//losowanie z innego zakresu dla procesu czasu rzeczywistego
	Random generator=new Random();
	if (rzeczywisty) return generator.nextInt(8)+8;
	else return generator.nextInt(7)+1;
}
public process_control_block(int a, Memory memory) {//osobny konstruktor dla procesu bezczynnosci - powinien byc utworzony jako pierwszy
	setName("Proces Bezczynnosci");
	setPriorytet_Bazowy(0);
	ID=nowe_id;
	nowe_id++;
	this.pageTable=new PageTable("bezczynnosc",16,memory); 
	licznik_rozkazow=licznik_wykonanych_rozkazow=rozmiar_tablicy_stronic=start_kodu_procesu=R1=R2=R3=0;
	numery_rozkazow=new ArrayList<Integer>();
	SetFile_name("bezczynnosc");
	Setstan(0);	
}
public process_control_block() {	
	ID=nowe_id; 
	numery_rozkazow=new ArrayList<Integer>();
	licznik_rozkazow=priorytet_bazowy=priorytet_dynamiczny=licznik_wykonanych_rozkazow=virtualBase=0;
}
public process_control_block(String nname) {
	setName(nname);
	ID=nowe_id; 
	numery_rozkazow=new ArrayList<Integer>();
	licznik_rozkazow=priorytet_bazowy=priorytet_dynamiczny=licznik_wykonanych_rozkazow=0;
}
public process_control_block(String nname,int size, String file_nam, Memory memory) {
	Name=nname;
	this.czasu_rzeczywistego=false;
	ID=nowe_id;
	nowe_id++;
	licznik_rozkazow=0;
	setPriorytet_Bazowy(Losuj_priorytet(czasu_rzeczywistego));
	this.pageTable=new PageTable(file_nam,size, memory);
	licznik_wykonanych_rozkazow=0;
	numery_rozkazow=new ArrayList<Integer>();
	start_kodu_procesu=0;
	SetFile_name(file_nam);
	SetR1(0);
	SetR2(0);
	SetR3(0);
	Setstan(0);
}
public void add_numer(int addr) {
	numery_rozkazow.add(addr);
}
public int getnumery_rozkazow(int iter) {
	return numery_rozkazow.get(iter);
}
public ArrayList<Integer> getnumery_rozkazow() {
	return numery_rozkazow;
}
public int get_ostatni_numery_rozkazow(){
	return numery_rozkazow.get(numery_rozkazow.size()-1);
}
public int getID() {
	return ID;
}
public String getName() {
	return Name;
}
public boolean Czy_czasu_rzeczywistego() {
	return czasu_rzeczywistego;
}
public int getLicznik_rozkazow() {
	return licznik_rozkazow;
}
public int getPriorytet_bazowy() {
	return priorytet_bazowy;
}
public int getPriorytet_dynamiczny() {
	return priorytet_dynamiczny;
}
public int getLicznik_wykonanych_rozkazow() {
	return licznik_wykonanych_rozkazow;
}
public PageTable getInformacja_o_zarzadzaniu_pamiecia() {
	return pageTable;
}
public int getRozmiar_tablicy_stronic() {
	return rozmiar_tablicy_stronic;
}
public int getStart_kodu_procesu() {
	return start_kodu_procesu;
}
public String getFile_name() {
	return file_name;
}
public int getR1() {
	return R1;
}
public int getR2() {
	return R2;
}
public int getR3() {
	return R3;
}
public int getStan() {
	return stan;
}
public void print() {
	System.out.println("Proces: " + this.Name + " ID: " + this.ID);
	System.out.println("Priorytet: " + this.priorytet_dynamiczny + " Licznik wykonanych: " + this.licznik_rozkazow + " stan: " + this.stan);
	System.out.println("R1: " + R1 + " R2: " + R2 + " R3: " +R3 );
	System.out.println("Plik: " + file_name);
	
}
}
