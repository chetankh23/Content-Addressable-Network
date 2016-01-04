package com.can.ClientPack;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.SocketAddress;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.can.ClientPack.ClientInterface;
import com.can.DAO.Point;
import com.can.DAO.Zone;
import com.can.util.Config;
import com.can.util.ZoneUtils;

/**
 * The class Client includes implementation of peer join and leave protocol,
 * displaying peer information, file insert and retrieve algorithm as well as
 * the routing mechanism for routing request in a CAN.
 * 
 * @author chetan
 *
 */
public class Client extends UnicastRemoteObject implements ClientInterface {
	
	private static final long serialVersionUID = 1L;
	private String nodeIdentifier;
	private String ipAddress;
	private HashMap<String, File> files;
	private Zone myZone;
	private HashMap<String, Zone> myNeighbours;
	static boolean isNodePresent = false;
	static boolean ready = true;

	protected Client() throws RemoteException {
		
		super();
		
		nodeIdentifier="Peer1";
		
		ipAddress = Config.getIPV4Address();
		
		System.out.println("Ip address is : " + ipAddress);
		
		files = new HashMap<String, File>();
		
		myZone = new Zone();
		
		myNeighbours = new HashMap<String, Zone>();
	}

	/**
	 * Validates if the RMI Registry already created.
	 * 
	 * @return True, if registry exists, false otherwise.
	 */
	public boolean isRegistryCreated() {
       
        Socket socket = new Socket();
        
        SocketAddress sockAddr = new InetSocketAddress(Config.SERVER_PORT);
        
        try {
                socket.connect(sockAddr);
                // try to connect, if it works then don't start the registry again
                return true;
        } catch (IOException e) {
                // if we get an exception then create a new registry
        
        } finally {
                	try {
                        socket.close();
                	} catch (IOException e) {
                        // ignored
                }
        }
        return false;
	}
		
	public static void main(String[] args) {
		
		Client client = null;
		
		try {	
				client = new Client();
				
				System.setProperty("java.rmi.server.hostname", client.ipAddress);
				
				System.setProperty("java.security.policy", "file:./security.policy");
					
				Registry registry=LocateRegistry.createRegistry(Config.SERVER_PORT);
				
				registry.rebind(Config.SERVER_ID, client);

		} catch (Exception e) {
			
			System.out.println("Exception: Failed to create registry " + e.getMessage());
			
			try {
				
				Registry registry=LocateRegistry.getRegistry(Config.SERVER_PORT);
				
				registry.rebind(Config.SERVER_ID, client);
			
			} catch (RemoteException e1) {
				
				e1.printStackTrace();
			}
		}
		
		if(client!=null)
			client.displayOptions(client);
	}
	
	public void displayOptions(Client client) {
		
		while(ready) {
			
			System.out.println("Please choose one of the following options :- ");
			
			System.out.println("1. Join");
			
			System.out.println("2. Leave");
			
			System.out.println("3. Insert a File");
			
			System.out.println("4. Search");
			
			System.out.println("5. View Peer Info ");
			
			System.out.println("6. Exit");
			 
			BufferedReader buffer = new BufferedReader(new InputStreamReader(
					System.in));
			
			try {
				
				int action = Integer.parseInt(buffer.readLine());
				
				if(action==1) {
					performJoin(client);
				}
				
				else if(action == 2) {
					performNodeDeletion(client);
				}
				
				else if(action==3) {
					performInsert(client);
				}
				
				else if(action==4) {
					performSearch(client);
				}
				
				else if(action ==5) {
					
					System.out.println("Please Select one of the following options ");
					
					System.out.println("1. View All");
					
					System.out.println("2. View Peer");
					
					int selection=Integer.parseInt(buffer.readLine());
					
					if(selection==1)
						viewPeers(client);
					
					else if(selection==2) {
						
							System.out.println("Please enter IP address of Peer in order to view details");
						
							String peerIp=buffer.readLine();
							
							try {
									ClientInterface viewPeer = (ClientInterface) Naming
												.lookup("rmi://" + peerIp + Config.POST_IP_URL);
						
									viewPeer.viewPeerInfo();

							} catch (Exception e) {
						
								System.out.println("Failure !!");
						
								e.printStackTrace();
							}
						
					}
					else
						System.out.println("Invalid Choice !!");
				}
				
				else if(action==6) {
					System.exit(1);
					break;
				}
				else
					System.out.println("Invalid Choice !!");
			
			} catch (NumberFormatException e) {
				
				e.printStackTrace();
			
			} catch (IOException e) {
				
				e.printStackTrace();
			}	
		}	
	}
	
	
	/**
	 *  Initiate the Node Join process
	 * @param client Instance of Peer
	 */
	private void performJoin(Client client) {
		
		if(isNodePresent) {
			
			System.out.println("The Node you have entered is already part of CAN network !!");	
		
		} else {
			
			try {
				BufferedReader buffer = new BufferedReader(new InputStreamReader(System.in));

				System.out.println("Please enter Unique Identifier for Node :- ");

				nodeIdentifier = buffer.readLine();

				System.out.println("Please enter the bootstrap node IP address :- ");

				String bootStrapIP = buffer.readLine();

				double xValue = ZoneUtils.genRandomCoordinate(10.0, 0.0);

				double yValue = ZoneUtils.genRandomCoordinate(10.0, 0.0);

				System.out.println("IP address of server " + bootStrapIP);

				System.out.println("Random x co-ordinate " + xValue);

				System.out.println("Random y co-ordinate " + yValue);

				System.out.println(
						"Server URL string is " + "rmi://" + bootStrapIP + Config.POST_IP_URL);

				ClientInterface newPeer = (ClientInterface) Naming
						.lookup("rmi://" + bootStrapIP + Config.POST_IP_URL);

				boolean result = newPeer.addPeerToNetwork(xValue, yValue, client.ipAddress);

				if (result) {

					System.out.println("Success !!");
					
					client.viewPeerInfo();

					isNodePresent = true;
					
				} else {

					System.out.println("Failed to add New Node in CAN network !!");

					isNodePresent = false;
				}

			} catch (Exception e) {
			
					System.out.println("Failure !!");
			
					isNodePresent = false;
			
					e.printStackTrace();
				}
			}
	}
	
	
	/**
	 * This function validates if the random point is current zone or not. If
	 * yes, the zone is split and neighbours are updated accordingly, otherwise,
	 * the request to routed to nearest neighbour.
	 */
	public boolean addPeerToNetwork(double xValue, double yValue, String ip) throws RemoteException {

		if (ZoneUtils.checkPointInZone(xValue, yValue, myZone)) {

			System.out.println("Point is in zone");

			Zone newOccupantZone = ZoneUtils.splitZone(myZone);

			System.out.println("Zone split");

			if (newOccupantZone != null) {

				myNeighbours.put(ip, newOccupantZone);

				updateNewNode(ip, newOccupantZone);

				updateNeighbours();
				
			} else
				return false;

		} else {

			if (routeToNeighbour(xValue, yValue))
				return true;

			return false;
		}

		return true;
	}
	
	
	/**
	 * Updates the newly added with neighbours.
	 * 
	 * @param ip
	 *            IP address of the newly added node.
	 * @param zone
	 *            Object of Zone class including zone coordinates of newly added
	 *            node.
	 */
	void updateNewNode(String ip, Zone zone) {
		
		try {	
				ClientInterface newPeer = (ClientInterface) Naming
					.lookup("rmi://" + ip + Config.POST_IP_URL);
			
				newPeer.setZone(zone);
			
				newPeer.addNeighbour(this.ipAddress, this.myZone);
				
				Iterator<String> keySetIterator = myNeighbours.keySet().iterator();
				
				while (keySetIterator.hasNext()) {
					
					String key = keySetIterator.next();

					if (!key.equalsIgnoreCase(ip)
							&& ZoneUtils.hasCommonEdge(zone, myNeighbours.get(key))) {

						try {
							
							ClientInterface neighbourPeer = (ClientInterface) Naming
									.lookup("rmi://" + key + Config.POST_IP_URL);
							
							neighbourPeer.addNeighbour(ip, zone);
							
							newPeer.addNeighbour(key, myNeighbours.get(key));
							
						} catch (Exception e) {

							e.printStackTrace();
						}
					} 
				}

		} catch (Exception e) {

			e.printStackTrace();
		}
	}
	
		
	
	/**
	 * This function includes routing logic to find the nearest neighbour to
	 * route. The nearest neighbour is found by calculating the distance of
	 * point from zone co-ordinates of neighbour.
	 * 
	 * @param xValue
	 *            x co-ordinate of random point
	 * @param yValue
	 *            y co-ordinate of random point
	 * @return True if routing to nearest neighbour is successful, false
	 *         otherwise.
	 */
	private boolean routeToNeighbour(double xValue, double yValue) {

		String peerIp = null;

		Iterator<String> iterator = myNeighbours.keySet().iterator();

		while (iterator.hasNext()) {

			String ip = iterator.next();

			Zone zone = myNeighbours.get(ip);

			if (!ZoneUtils.checkPointInZone(xValue, yValue, zone)) {

				double min = 0;

				Point randomPoint = new Point(xValue, yValue);

				boolean minFlag = true;

				if (minFlag) {
					peerIp = ip;

					min = ZoneUtils.findMinDistance(randomPoint, zone);

					minFlag = false;

				} else {

					double result = ZoneUtils.findMinDistance(randomPoint, zone);

					if (result < min) {

						min = result;

						peerIp = ip;
					}
				}

			} else {

				double min = 0;

				Point randomPoint = new Point(xValue, yValue);

				boolean minFlag = true;

				if (minFlag) {
					peerIp = ip;

					min = ZoneUtils.findMinDistance(randomPoint, zone);

					minFlag = false;

				} else {

					double result = ZoneUtils.findMinDistance(randomPoint, zone);

					if (result < min) {

						min = result;

						peerIp = ip;
					}
				}
			}
		}

		try {

			ClientInterface newPeer = (ClientInterface) Naming.lookup("rmi://" + peerIp + Config.POST_IP_URL);

			newPeer.addPeerToNetwork(xValue, yValue, peerIp);

		} catch (RemoteException e) {

			e.printStackTrace();

			return false;

		} catch (NotBoundException e) {

			e.printStackTrace();

			return false;

		} catch (MalformedURLException e) {

			e.printStackTrace();

			return false;
		}

		return true;
	}	
	
	
	/**
	 * This function initiates Node Deletion process by finding the best
	 * available neighbour to merge. The data items are handed over to neighbour
	 * and deletion is performed thereafter. After departure, subsequent
	 * neighbours are updated accordingly.
	 * 
	 * @param client
	 *            Instance of Peer which is to be deleted.
	 */
	public void performNodeDeletion(Client client) {

		String ip = client.selectMergeNode();

		try {
			ClientInterface peer= (ClientInterface) Naming.lookup("rmi://" + ip + Config.POST_IP_URL);

			peer.leave(client.ipAddress);

			for (String key : client.files.keySet()) {

				peer.addFile(key, client.files.get(key));
			}

			for (String key : myNeighbours.keySet())
			{
				try {
					
					ClientInterface neighbourPeer = (ClientInterface) Naming
							.lookup("rmi://" + key + Config.POST_IP_URL);
					
					neighbourPeer.removeNeighbour(this.ipAddress);
				
				} catch (Exception e) {
					
					e.printStackTrace();
				}
			}
			
			System.out.println("Success !!");

			ready = false;

		} catch (Exception e) {

			System.out.println("Failure !!");

			e.printStackTrace();
		}
	}
	
	
	/**
	 * This function determines the best available neighbour to merge.
	 * @return Ip address of peer to merge.
	 */
	private String selectMergeNode() {
		
		String selectedPeer = "";
		
		double min = 100;
		
		for (String ip : myNeighbours.keySet()) {
			
			Zone z = myNeighbours.get(ip);
			
			double area = (z.lowerRight.x - z.lowerLeft.x) * (z.upperLeft.y - z.lowerLeft.y);
			
			if (ZoneUtils.isSquareOrRect(myZone, z) && area < min) {
				
				min = area;
				
				selectedPeer = ip;
			}

		}
		return selectedPeer;
	}

	
	
	@Override
	public void leave(String ip) throws RemoteException {
		
		mergeZones(myZone, myNeighbours.get(ip));
		
		updateNeighbours();	
	}
	
	
	/**
	 * This function performs merging of two zones when peer is about to depart.
	 * 
	 * @param zone1
	 *            Zone of departing node.
	 * @param zone2
	 *            Zone of neighbour node.
	 */
	public void mergeZones(Zone zone1, Zone zone2) {
		
		double minX, maxX, minY, maxY;
		
		if (zone2.lowerLeft.x > zone1.lowerLeft.x)
			minX = zone1.lowerLeft.x;
		
		else
			minX = zone2.lowerLeft.x;
		
		if (zone2.lowerRight.x > zone1.lowerRight.x)
			maxX = zone2.lowerRight.x;
		
		else
			maxX = zone1.lowerRight.x;
		
		if (zone2.lowerLeft.y > zone1.lowerLeft.y)
			minY = zone1.lowerLeft.y;
		
		else
			minY = zone2.lowerLeft.y;
		
		if (zone2.upperLeft.y > zone1.upperLeft.y)
			maxY = zone2.upperLeft.y;
		
		else
			maxY = zone1.upperLeft.y;
		
		myZone.lowerLeft.x = minX;
		myZone.lowerLeft.y = minY;
		myZone.lowerRight.x = maxX;
		myZone.lowerRight.y = minY;
		myZone.upperLeft.x = minX;
		myZone.upperLeft.y = maxY;
		myZone.upperRight.x = maxX;
		myZone.upperRight.y = maxY;
	}
	
	
	/**
	 * Perfoms insert of file with filename keyword. After successful insertion,
	 * displays which peer stores the file and the route at the IP layer from
	 * peer to the destination peer. If the insertion fails, Failure is
	 * displayed.
	 * 
	 * @param client
	 *            Bootstapping Peer.
	 */
   private void performInsert(Client client) {
	   
	   try {
		   BufferedReader buffer = new BufferedReader(new InputStreamReader(System.in));

		   System.out.println("Please enter keyword for file ");
		
		   String filekey = buffer.readLine();
		   
		   System.out.println("Please enter file name ");
		   
		   String fileName=buffer.readLine();
		   
		   File fileToInsert=new File(fileName);
		   
		   String result = client.insert(filekey, fileToInsert);
			
		   if (result.contains("Failure")) {
				
			   System.out.println("Insertion of file:" + fileName
						+ " fails !!");
		   }
			
		   else {
				
			   System.out.println("The file:"
						+ fileName
						+ " is sucessfully inserted into the peer "
						+ (result.lastIndexOf('>') > -1 ? result
								.substring(result.lastIndexOf('>'))
								: result));
			   
			   System.out.println("Route:" + result);
		   }
		
	   } catch (IOException e) {
		   // TODO Auto-generated catch block
		   e.printStackTrace();
	   }
   }
	
   
	/**
	 * Displays list of peers and their details included in the network.
	 * 
	 * @param client
	 *            Bootstrapping peer.
	 */
	   private void viewPeers(Client client) {
		
		Set<String> totalPeers=new HashSet<String>();
		
		Set<String> tempNeighbours=new HashSet<String>();
		
		try {
				client.viewPeerInfo();
			
				totalPeers.addAll(client.getNeighboursList());
			
				totalPeers.remove(client.ipAddress);
			
				do {
				
					if(tempNeighbours.size()>0) {
					
						totalPeers.addAll(tempNeighbours);
					
						totalPeers.remove(client.ipAddress);
					}
				
					for(String ip : totalPeers) {
					
						try {

								ClientInterface neighbour = (ClientInterface) Naming
											.lookup("rmi://" + ip + Config.POST_IP_URL);
						
								tempNeighbours.addAll(neighbour.getNeighboursList());
						
								tempNeighbours.remove(client.ipAddress);
					
						} catch (Exception e) {

							e.printStackTrace();
						}
					}
		
				}while(!totalPeers.containsAll(tempNeighbours) && !tempNeighbours.isEmpty());
		
		} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}
		
		for (String ip : totalPeers) {
			
			try {
					ClientInterface requestedPeer = (ClientInterface) Naming
								.lookup("rmi://" + ip + Config.POST_IP_URL);
				
					requestedPeer.viewPeerInfo();

			} catch (Exception e) {
				
				System.out.println("Failure !!");
				
				e.printStackTrace();
			}
		}
	}

	

	@Override
	public void viewPeerInfo() throws RemoteException {
		
		System.out.println("Node identifier : "+ this.nodeIdentifier);
		
		System.out.println("\nIP address : "+this.ipAddress);
		
		System.out.println("\nZone is : "+this.myZone.lowerLeft+","+this.myZone.lowerRight+","+this.myZone.upperLeft+","+this.myZone.upperRight);
		
		System.out.println("\nNeighbours : \n IP address \t Zone Value");
		
		for (String ip : this.myNeighbours.keySet())
			System.out.println(ip+"\t"+this.myNeighbours.get(ip));
	}


	@Override
	public void removeNeighbour(String ip) throws RemoteException {
		
		this.myNeighbours.remove(ip);
	}


	@Override
	public void setZone(Zone zone) throws RemoteException {
		
		this.myZone = zone;
	}


	@Override
	public void addNeighbour(String ip, Zone zone) throws RemoteException {

		this.myNeighbours.put(ip, zone);
	}


	@Override
	public void addFile(String key, File file) throws RemoteException {
		
		this.files.put(key, file);
	}


	@Override
	public void deleteFile(String key) throws RemoteException {
		
		this.files.remove(key);
	}


	@Override
	public Set<String> getNeighboursList() throws RemoteException {
		
		return new HashSet<String>(this.myNeighbours.keySet());
	}

	@Override
	public Zone getZone() throws RemoteException {
		
		return this.myZone;
	}

	
	/**
	 * Updates list of neighbours when any peer joins or leave the network.
	 */
	@Override
	public void updateNeighbours() throws RemoteException {
		
		Iterator<String> keySetIterator = myNeighbours.keySet().iterator();
		
		Zone z1, z2;
		
		while (keySetIterator.hasNext()) {
			
			String key = keySetIterator.next();

			if (myNeighbours.get(key).lowerLeft.x > myZone.lowerLeft.x) {
				
				z1 = myZone;
				
				z2 = myNeighbours.get(key);
				
			} else {
				
				z1 = myNeighbours.get(key);
				
				z2 = myZone;
			}

			if (ZoneUtils.hasCommonEdge(z1, z2)) {
	
				try {
					
					ClientInterface neighbourPeer = (ClientInterface) Naming
							.lookup("rmi://" + key + Config.POST_IP_URL);
					
					neighbourPeer.addNeighbour(this.ipAddress, this.myZone);
				} catch (Exception e) {
					
					e.printStackTrace();
				}
			} else {
				
				keySetIterator.remove();
				
				try {
					ClientInterface neighbourPeer = (ClientInterface) Naming
							.lookup("rmi://" + key + Config.POST_IP_URL);
					
					neighbourPeer.removeNeighbour(this.ipAddress);
				} catch (Exception e) {
					
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Given keyword for a File and File Content, a mapping point is found and
	 * each peer verifies if the point lies in its zone. If it does, file is
	 * inserted at that peer and Hash Table is updated. else request is routed
	 * to next closest neighbour matching the destination point.
	 */
	@Override
	public String insert(String keyword, File file) throws RemoteException {
		
		Point P = findMappingPoint(keyword);
		
		if (ZoneUtils.checkPointInZone(P,this.myZone)) {
			
			this.files.put(keyword, file);

			return "(" + this.nodeIdentifier + ")";

		} else {
			
			String ip = selectClosestPeer(P);
			
			try {
				
				ClientInterface requestedPeer = (ClientInterface) Naming
						.lookup("rmi://" + ip + Config.POST_IP_URL);
				
				return "(" + this.nodeIdentifier + ")" + ","
						+ requestedPeer.insert(keyword, file);

			} catch (Exception e) {
				
				System.out.println("Failure !!");
				
				e.printStackTrace();
			}
		}
		
		return "Failure!";
	}


	Point findMappingPoint(String keyword) {
		
		double x = 0, y = 0;
		
		if (keyword.length() > 4) {
			
			for (int i = 1; i < keyword.length(); i = i + 2)
				x = x + keyword.charAt(i);
			
			for (int i = 0; i < keyword.length(); i = i + 2)
				y = y + keyword.charAt(i);

			return new Point(x % 10, y % 10);
		}
		
		return new Point();
	}
	
	
	/**
	 * Selects closest peer from the network matching the given coordinates.
	 * 
	 * @param randomP
	 *            Coordinates whose closest peer is to be found.
	 * @return IP address of selected Peer.
	 */
	String selectClosestPeer(Point randomP) {
		
		Iterator<String> iterator = myNeighbours.keySet().iterator();
		
		String selectedPeer = "";
		
		boolean flag = true;
		
		double min = 0;
		
		while (iterator.hasNext()) {
			
			String Ip = iterator.next();
			
			Zone zone = myNeighbours.get(Ip);
			
			if (flag) {
				selectedPeer = Ip;
				
				min = ZoneUtils.findMinDistance(randomP, zone);
				
				flag = false;
			} else {
					
					if (ZoneUtils.findMinDistance(randomP, zone) < min) {
						
						min = ZoneUtils.findMinDistance(randomP, zone);
						
						selectedPeer = Ip;
					}
				}
		}
		return selectedPeer;
	}
	
	
	/**
	 * Search for a file with keyword. After a successful search, it displays
	 * which peer stores the file and the route at the IP layer from peer to the
	 * destination peer. If the search fails, Failure is displayed.
	 * 
	 * @param client
	 *            Bootstrapping peer where search request is given.
	 */
	private void performSearch(Client client) {
		 
		try {
				
				BufferedReader buffer = new BufferedReader(new InputStreamReader(System.in));

				System.out.println("Please enter keyword for file ");
				
				String keyword = buffer.readLine();
					
				String result = client.search(keyword);
				
				if (result.contains("Failure"))
					System.out.println("Search for a file with keyword:"
							+ keyword + " fails !!");
				
				else {
					
					System.out.println((result.lastIndexOf(',') > -1 ? result
							.substring(result.lastIndexOf(',')) : "")
							+ " stores the file with the keyword:" + keyword);
					
					System.out.println("Route:" + result);
				}
		
		} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}		 		   
	}

	
	/**
	 * Given keyword for a file, a mapping point is found and each peer verifies
	 * if the point lies in its zone. If it does, corresponding file entry is
	 * checked in the Hash Table. On success, file and peer information is
	 * displayed, else Failure is displayed. If the point lies outside peer
	 * zone, request for search is routed to next closest neighbour matching the
	 * destination point.
	 */
	@Override
	public String search(String keyword) throws RemoteException {
		
		Point P = findMappingPoint(keyword);
		
		System.out.println("Point:" + P);
		
		if (ZoneUtils.checkPointInZone(P, this.myZone)) {
			
			if (files.containsKey(keyword)) {

				return "(" + this.nodeIdentifier + ")";
			} else
				
				return "No file with keyword " + keyword + " exists !!";

		} else {
			
			String ip = selectClosestPeer(P);
			
			try {
				
				ClientInterface requestedPeer = (ClientInterface) Naming
						.lookup("rmi://" + ip + Config.POST_IP_URL);
				
				return "(" + this.nodeIdentifier + ")" + ","
						+ requestedPeer.search(keyword);

			} catch (Exception e) {
				
				System.out.println("Failure !!");
				
				e.printStackTrace();
			}
		}
		return "Failure !!";
	}
}
