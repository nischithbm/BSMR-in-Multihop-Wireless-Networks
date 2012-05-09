package node_pkg;

import java.util.Hashtable;

public class NodeTable {

	public static Hashtable<Integer, Node> nodeTable = new Hashtable<Integer, Node>();
	
	// Mapping IpAddres with Node Obj
	public synchronized static void addEntry(Node nodeObj){
		nodeTable.put(nodeObj.ipAddress,nodeObj);
	}
	public synchronized static Node getNode(int ipAddr){
		return nodeTable.get(ipAddr);
	}
	
}
