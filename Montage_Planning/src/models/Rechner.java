package models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public abstract class Rechner {
	private Integer seriennr;
	private Auftrag auftrag;
	private String status;
	private Monteur monteur;
	private List <Teile> teile ;
	private boolean montiert;
	private boolean installiert;
	private boolean getestet;
	private Fertigungsauftrag fa;
	
	
	

	public Rechner(int pSeriennr, Auftrag pAuftrag, String pStatus, Teile... pTeile)
	{
		setSeriennr(pSeriennr);
		setAuftrag(pAuftrag);
		status = pStatus;
		this.teile = new ArrayList<Teile>(Arrays.asList(pTeile));
	}
	//KOnstruktor für FA_REchner
	public Rechner(int pSeriennr, Fertigungsauftrag fa, String pStatus, Teile... pTeile)
	{
		setSeriennr(pSeriennr);
		this.fa = fa;
		status = pStatus;
		this.teile = new ArrayList<Teile>(Arrays.asList(pTeile));
	}
	
	
	public void disponieren()
	{
		status = "disponieren";
	}
	
	
	public void setUnvollstaendig()
	{
		status = "unvollstaendig";
	}
	public void setAngelegt()
	{
		status = "angelegt";
	}
	public void inBearbeitung()
	{
		status = "in Bearbeitung";
	}
	public void erledigt()
	{
		status = "erledigt";
	}
	public void imLager()
	{
		status = "im Lager";
	}
	public int getSeriennr() {
		return seriennr;
	}
	public void setSeriennr(int seriennr) {
		this.seriennr = seriennr;
	}
	public Auftrag getAuftrag() {
		return auftrag;
	}
	public void setAuftrag(Auftrag auftrag) {
		this.auftrag = auftrag;
	}
	public String getStatus()
	{
		return status;
	}
	public Monteur getMonteur() 
	{
		return monteur;
	}
	public void setMonteur(Monteur pMonteur) 
	{
		monteur = pMonteur;
	}
	public Teile getTeile() {
		return teile;
	}
	public void setTeile(Teile pTeile) {
		teile = pTeile;
	}
	public boolean isMontiert() 
	{
		return montiert;
	}
	public void setMontiert(boolean pMontiert) 
	{
		montiert = pMontiert;
	}
	public boolean isInstalliert() 
	{
		return installiert;
	}
	public void setInstalliert(boolean pInstalliert) 
	{
		installiert = pInstalliert;
	}
	public boolean isGetestet() 
	{
		return getestet;
	}
	public void setGetestet(boolean pGetestet) 
	{
		getestet = pGetestet;
	}
	
	
	
}
