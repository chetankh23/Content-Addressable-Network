package com.can.ClientPack;

import java.io.File;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Set;

import com.can.DAO.Zone;

/**
 * The ClientInterface includes method declarations required for the Client
 * Class.
 * 
 * @author chetan
 *
 */
public interface ClientInterface extends Remote{

	public Set<String> getNeighboursList() throws RemoteException;
	
	public void viewPeerInfo() throws RemoteException;
	
	public boolean addPeerToNetwork(double xValue, double yValue,String ip) throws RemoteException;
	
	public void leave(String ip) throws RemoteException;
	
	public void addFile(String key,File file) throws RemoteException;
	
	public void deleteFile(String key)throws RemoteException;
	
	public void removeNeighbour(String ip) throws RemoteException;

	public void setZone(Zone zone) throws RemoteException;
	
	public Zone getZone() throws RemoteException;
	
	public void addNeighbour(String ip, Zone zone) throws RemoteException;
	
	public void updateNeighbours() throws RemoteException;
	
	public String insert(String keyword, File file)throws RemoteException;
	
	public String search(String keyword) throws RemoteException;
}
