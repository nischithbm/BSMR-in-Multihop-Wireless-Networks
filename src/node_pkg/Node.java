//Package Name
package node_pkg;

//Importing custom packages

import crypt_pkg.HashMd5;

import packet_pkg.*;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;

import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;

// external library - guava project
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;


public class Node {
	// Simulation Parameters
	public static int nwSize = 45;  // Total no of nodes in the network
	public static int grpSize;		// Total no of member nodes in the group

	
	// Simulation Results
	public static int pSent;		// Total no of packets sent by the source node
	public int pRecvd;				// Total no of packets received by each member
	
	
	public static int maxNodes = 500; // Maximum number of nodes permitted
	public static int maxHops = 30; // Maximum number of hops permitted
	public static int tRange = 50; // Transmission Range - same for all nodes
	public static boolean showRange = false; // show/hide nodes' transmission range
	public static int nodeFillRadious = 15; // width of node used in paint method

	public static int totalNodes = 0; // Total number of nodes in the group
	static int lastIpAddress = 0;

	public static int maxAttempts = 5;		// max no of attempts for route discovery for one instance
	public int attempts;
	
	
	public static int readRate = 1000;
	public static int beaconRate = 5000;
	public static int stickToMeRate = 5000;

	public static int RA_Time = 5000; // Route Accumulation - Timer
	public int BO_Time = 800; // Back Off - Timer - default 1000 - increased depending on no. of attempts
	public int WTC_Time; // Wait To Connect - Timer - proportional to no of hops in the route
	
	// Threads used
	public Timer bufferThread = null;
	public Timer beaconThread = null;
	public Timer stickToMeThread = null;
	public Timer beaconChkThread = null;
	public Timer connThread = null;


	public Timer RA_Timer = null;
	public Timer StickToMe_Timer = null;

	
	int ipAddress;
	int groupSeqNo; // Route Freshness ---> higher the Group Sequence No - fresher the route

	//public int treeLevel=999;
	NodeType type; // Type of the Node
	AttackType attackType;

	int posX;
	int posY;

	public int no_of_reachableNodes; // each node has variable no of reachable nodes
	public int reachableNodes[] = new int[Node.maxNodes];

	public Queue<Packet> buffer = new LinkedList<Packet>();
	ArrayList<Packet> rrepTmpList = new ArrayList<Packet>();

	Hashtable<Long, Integer> rcvdMsgs = new Hashtable<Long, Integer>();

	// One Hop Neighbors - state maintenance in each node
	int upStreamNode; // tree upstream Node
	public ArrayList<Integer> downStreamNodes = new ArrayList<Integer>(); // List of downstream connected nodes

	// Reliability of the links - weight lists
	Multimap<Integer, Timestamp> linkHistory = ArrayListMultimap.create();

	// to get beacon count in last few mins..
	Multimap<Integer, Timestamp> beaconHistory = ArrayListMultimap.create();
	// public ArrayList<Integer> weightList = new ArrayList<Integer>();

	
	/** ** Cryptography **** */
	static int CRYPT_BIT_SIZE = 512; // can use --> 512 bits or 1024 bits or 2048 bits

	// Used for encryption
	public RSAPublicKeySpec rsaPub = null; // Public Key is shared
	private RSAPrivateKeySpec rsaPriv = null;

	// Used for digital signatures
	public PublicKey pubKey = null; // Public Key is shared
	private PrivateKey privKey = null;

	public NodeIpSign nodeIpSign = new NodeIpSign(); // Node Ip Sign

	int TF; // TF -> Trust Factor
	int distributeTF[] = new int[100];

	public boolean Connected; // Node is connected or disconnected to the tree

	// Node status - used for animation in Topology Class
	public int receiving = 0;
	public int dropping = 0;
	public int sending = 0;

	// public static int TTKN = 231;

	/** ***** Node Monitoring Frame ******* */
	JFrame nodeMonitorFrame = null;
	JScrollPane nodeMonitorScrollPane = null;
	JTree nodeMonitorTree = null;

	public Node() {
		// default constructor
	}

	public Node(NodeType nType, int X, int Y) {
		this.ipAddress = ++lastIpAddress;
		this.totalNodes++;

		this.groupSeqNo = 0;
		this.type = nType;
		if (this.type == NodeType.SOURCE) {
			this.Connected = true; // Source is always connected and available
		}
		this.posX = X;
		this.posY = Y;

		this.attackType = AttackType.NONE;

		this.initialize();

		NodeTable.addEntry(this);

		RA_Time = RA_Time + 100;
	}

	private void initialize() {

		switch (this.attackType) {
		case NONE:
			TF = 100;
			break; // Ideal condition - 100%
		case BLACK_HOLE:
			TF = 0;
			break;
		case GRAY_HOLE:
			TF = 50;
			break;
		default:
			break;
		}

		this.genCryptKeys();
		SharedKey.addEntry(this.ipAddress, this.rsaPub);
		SharedKey.addEntry(this.ipAddress, this.pubKey);

		this.createNodeIpSign();
		this.createNodeMonitor();
		this.initThreads();

	}

	public void genTF() {
		Random randGen = new Random();
		int k = TF;
		int l = 100 - TF;
		for (int i = 0; i < 100; i++) {

			if (randGen.nextBoolean() && k > 0) {
				distributeTF[i] = 1;
				k--;
			} else {
				if (l > 0) {
					distributeTF[i] = 0;
					l--;
				} else {
					distributeTF[i] = 1;
				}
			}
		}
	}

	public void createNodeIpSign() {
		this.nodeIpSign.ipAddress = this.getIpAddress();
		this.nodeIpSign.sign = digSign(HashMd5.getMd5Hash(this.getIpAddress()));
	}

	public void createNodeMonitor() {

		nodeMonitorFrame = new JFrame("Node Monitor: Node " + this.ipAddress);
		nodeMonitorFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		nodeMonitorFrame.setBounds(200, 50, 500, 320);
		nodeMonitorFrame.setLayout(null);

		DefaultMutableTreeNode root = new DefaultMutableTreeNode("Node "+ this.getIpAddress());

		DefaultMutableTreeNode tmpTreeNode = new DefaultMutableTreeNode("Node Initializing its parameters");
		root.add(tmpTreeNode);


		nodeMonitorTree = new JTree(new DefaultTreeModel(root));
		DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
		renderer.setOpenIcon(null);
		renderer.setClosedIcon(null);
		renderer.setLeafIcon(null);
		renderer.setAutoscrolls(true);
		renderer.setText("sd");
		nodeMonitorTree.setCellRenderer(renderer);

		
		
		nodeMonitorScrollPane = new JScrollPane(nodeMonitorTree);
		nodeMonitorScrollPane.setBounds(10, 10, 450, 200);

		JButton jbtn = new JButton("Close Node Monitor");
		jbtn.setBounds(150, 220, 160, 30);
		jbtn.setBackground(Color.red);
		jbtn.setForeground(Color.white);
		jbtn.setFont(new Font(null, Font.BOLD, 12));

		jbtn.setVisible(true);
		jbtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				nodeMonitorFrame.setVisible(false);
			}
		});

		nodeMonitorFrame.add(nodeMonitorScrollPane);
		nodeMonitorFrame.add(jbtn);

		nodeMonitorFrame.setResizable(false);
		nodeMonitorFrame.setVisible(false); // By default hidden
	}

	public void initThreads() {
		
		
		bufferThread = new Timer("bufferThread");
		startThread(bufferThread, Node.readRate);
		// bufferThread = new Thread(this,"bufferThread");
		// bufferThread.start();

		if (this.type == NodeType.SOURCE ) {
			//stickToMeThread = new TimerB("stickToMeThread");
			//startThread(stickToMeThread, Node.stickToMeRate);
		}
	}


	// Set Node Position
	public void setPosX(int x) {
		posX = x;
	}

	public void setPosY(int y) {
		posY = y;
	}

	public void setPosXY(int x, int y) {
		posX = x;
		posY = y;
	}

	// Get Node Position
	public int getPosX() {
		return posX;
	}

	public int getPosY() {
		return posY;
	}

	public int getIpAddress() {
		return this.ipAddress;
	}

	public void setNodeType(NodeType nt) {
		this.type = nt;
	}

	public NodeType getNodeType() {
		return this.type;
	}

	// Node Monitor Visibility
	public void setNodeMonitorVisibility(boolean bln) {
		nodeMonitorFrame.setVisible(bln);
	}

	public boolean getNodeMonitorVisibility() {
		return nodeMonitorFrame.isVisible();
	}

	
	public String toString(){
		String str="";
		str += "Ip Adress: "+this.getIpAddress();
		str += "\nNode Type: "+this.type;
		
		str += "\nConnection Status: ";
		if(this.Connected)	str += "Connected";
		else	str += "Disconnected";
		
		
		
		return str;
	}
	
	
	public static void reset(){
		Node.lastIpAddress = 0;
		Node.totalNodes = 0;
		Node.pSent = 0;
	}
	
	
	
	
	
	
	public void findReachableNodes() {
		Node tmpNode;
		this.no_of_reachableNodes = 0;
		for (int i = 0; i < Node.totalNodes; i++) {
			tmpNode = NodeTable.getNode(i + 1);
			if (this.getIpAddress() != tmpNode.getIpAddress()) {
				// sqrt( (x1-x2)^2 + (y1-y2)^2 )
				if (Math.sqrt(Math.pow(this.getPosX() - tmpNode.getPosX(), 2)
						+ Math.pow(this.getPosY() - tmpNode.getPosY(), 2)) < (2 * tRange)) {
					this.reachableNodes[this.no_of_reachableNodes] = tmpNode
							.getIpAddress();
					this.no_of_reachableNodes++;
				}
			}
		}
	}

	public boolean isReachable(int ipAddr) {
		// Generic function - to find out whether Node tmp is reachable from this.node
		Node tmpNode = NodeTable.getNode(ipAddr);
		if (this.ipAddress != tmpNode.getIpAddress()) {
			if (Math.sqrt(Math.pow(this.getPosX() - tmpNode.getPosX(), 2)
					+ Math.pow(this.getPosY() - tmpNode.getPosY(), 2)) < (2 * tRange)) {
				return true;
			}
		}
		return false;
	}

	public void getReachableNodes() {
		// System.out.println("Node "+ipAddress+"- Reachable Nodes are");
		for (int i = 0; i < no_of_reachableNodes; i++) {
			// this.dumpNodeStatus(""+reachableNodes[i]);
		}
		// May not be usefull
	}

	


	public void createSignSend(PacketType pType){
		
		if (pType == PacketType.DATA_PKT) {
			
			 DataPacket pktObj;
				
				byte[] data = null;
				BigInteger bigIntMsg = null;
				BigInteger signedBigIntData = null;
				
				String strMsg = "Hello this is a very big message which i ve generated and sending as a part of" +
						"data in the packet. and definitely the data maintains its authenticity and integrity by using" +
						"RSA and MD5 algorithms. We also use digital signatures.";
				
				//Node srcNode = NodeTable.getNode(1);
				
					data = strMsg.getBytes();
					//System.out.println("len:"+data.length);
					bigIntMsg = new BigInteger(data);
					
					signedBigIntData = this.digSign(HashMd5.getMd5Hash(bigIntMsg));
				
					BigInteger hcaVal = HashMd5.getHashChain(Node.maxHops, new BigInteger("435"));
					BigInteger hcaSign = this.digSign(HashMd5.getMd5Hash(hcaVal));
					HopCountAnchor hca = new HopCountAnchor(hcaVal,hcaSign);
					
					BigInteger hcAuthen = new BigInteger("435");
					
				
					
					int ttl = 10;
						pktObj = new DataPacket(PacketType.DATA_PKT,1,0,0,hcAuthen,hca,this.nodeIpSign,signedBigIntData,bigIntMsg,ttl);
						
						for(Iterator<Integer> itr=this.downStreamNodes.iterator();itr.hasNext();){
				    		this.sending = 10;
				    		this.sendPacket(itr.next(), pktObj);
						}
			
		}
		
		else if(pType == PacketType.MRATE){
			MRatePacket pktObj;
			
			byte[] data = null;
			BigInteger bigIntMsg = null;
			BigInteger signedBigIntData = null;
			
			String strMsg = "sample";
			
			
				data = strMsg.getBytes();
				
				bigIntMsg = new BigInteger(data);
				
				signedBigIntData = this.digSign(HashMd5.getMd5Hash(bigIntMsg));
			
				BigInteger hcaVal = HashMd5.getHashChain(Node.maxHops, new BigInteger("435"));
				BigInteger hcaSign = this.digSign(HashMd5.getMd5Hash(hcaVal));
				HopCountAnchor hca = new HopCountAnchor(hcaVal,hcaSign);
				
				BigInteger hcAuthen = new BigInteger("435");
				
			
				int mRate = 30;
				int ttl = 10;
					pktObj = new MRatePacket(PacketType.MRATE,1,0,0,hcAuthen,hca,this.nodeIpSign,mRate, signedBigIntData,bigIntMsg,ttl);
					
					
					for(Iterator<Integer> itr=this.downStreamNodes.iterator();itr.hasNext();){
						this.sending = 10;
						this.sendPacket(itr.next(), pktObj);
					}
					
		}
		
		
		
		else if (pType == PacketType.RREQ) {
			byte[] data = null;
			BigInteger bigIntMsg = null;

			String strMsg = "sample";
			data = strMsg.getBytes();
			bigIntMsg = new BigInteger(data);

			BigInteger hcaVal = HashMd5.getHashChain(Node.maxHops,
					new BigInteger("435"));
			BigInteger hcaSign = digSign(HashMd5.getMd5Hash(hcaVal));
			HopCountAnchor hca = new HopCountAnchor(hcaVal, hcaSign);

			BigInteger hcAuthen = new BigInteger("435");

			
				RReqPacket pktObj;
				int ttl = 5;
				pktObj = new RReqPacket(pType, this.getIpAddress(), 0, 0,
						hcAuthen, hca, this.nodeIpSign, null, bigIntMsg, ttl);
				pktObj.signedData = digSign(pktObj.getHash());

				
				this.sending = 10;
				boolean flag=false;
				for (int i = 0; i < no_of_reachableNodes; i++) {
					
					flag=isSuccessor(reachableNodes[i],pktObj.senderIp);
					
					if(!flag){
						this.sendPacket(reachableNodes[i], pktObj);
					}
					
				}
				

		}
		else if (pType == PacketType.BEACON) {
			BeaconPacket pktObj;

			byte[] data = null;
			BigInteger bigIntMsg = null;
			BigInteger signedBigIntData = null;

			String strMsg = "sample";

			data = strMsg.getBytes();
			bigIntMsg = new BigInteger(data);

			signedBigIntData = this.digSign(HashMd5.getMd5Hash(bigIntMsg));

			pktObj = new BeaconPacket(PacketType.BEACON, this.getIpAddress(), this.upStreamNode, signedBigIntData, bigIntMsg);

			this.sendPacket(this.upStreamNode,pktObj);
		}
		else if (pType == PacketType.STICK_TO_ME) {
			//this.dumpNodeStatus("sending stick..", 1);
			StickToMePacket pktObj;

			byte[] data = null;
			BigInteger bigIntMsg = null;
			BigInteger signedBigIntData = null;

			String strMsg = "sample";

			data = strMsg.getBytes();
			bigIntMsg = new BigInteger(data);

			signedBigIntData = this.digSign(HashMd5.getMd5Hash(bigIntMsg));

			pktObj = new StickToMePacket(PacketType.STICK_TO_ME, this.getIpAddress(), 0, signedBigIntData, bigIntMsg);


			for(Iterator<Integer> itr=this.downStreamNodes.iterator();itr.hasNext();){
				this.sending = 10;
				this.sendPacket(itr.next(), pktObj);
			}
			
		}
		else if (pType == PacketType.TREE_PRUNE) {
			
			TreePrunePacket pktObj;

			byte[] data = null;
			BigInteger bigIntMsg = null;
			BigInteger signedBigIntData = null;

			String strMsg = "sample";

			data = strMsg.getBytes();
			bigIntMsg = new BigInteger(data);

			signedBigIntData = this.digSign(HashMd5.getMd5Hash(bigIntMsg));

			pktObj = new TreePrunePacket(PacketType.TREE_PRUNE, this.getIpAddress(), this.upStreamNode, signedBigIntData, bigIntMsg);

			this.sendPacket(this.upStreamNode,pktObj);
			this.upStreamNode=0;
		}
		
	}
	
	public void createSignSend(PacketType pktType, int destIp, int ttl, ArrayList<NodeIpSign> nodeRevPtr) {

		if (pktType == PacketType.RREP) {
			byte[] data = null;
			BigInteger bigIntMsg = null;
			BigInteger signedBigIntData = null;

			String strMsg = "sample";
			data = strMsg.getBytes();
			bigIntMsg = new BigInteger(data);
			signedBigIntData = digSign(HashMd5.getMd5Hash(bigIntMsg));

			this.sending = 10;

			RRepPacket pktObj = new RRepPacket(pktType, this.getIpAddress(),
					destIp, 0, null, null, this.nodeIpSign, nodeRevPtr,
					signedBigIntData, bigIntMsg, ttl);

			this.sendPacket(nodeRevPtr.get(nodeRevPtr.size() - 1).ipAddress,
					pktObj);
		} else if (pktType == PacketType.MACT) {
			byte[] data = null;
			BigInteger bigIntMsg = null;
			BigInteger signedBigIntData = null;

			String strMsg = "sample";
			data = strMsg.getBytes();
			bigIntMsg = new BigInteger(data);
			signedBigIntData = digSign(HashMd5.getMd5Hash(bigIntMsg));

			this.sending = 10;

			MActPacket pktObj = new MActPacket(pktType, this.getIpAddress(),
					destIp, 0, null, null, this.nodeIpSign, nodeRevPtr,
					signedBigIntData, bigIntMsg, ttl);

			this.upStreamNode=0;
			this.upStreamNode = nodeRevPtr.get(nodeRevPtr.size() - 1).ipAddress;
			
			this.sendPacket(this.upStreamNode,pktObj);
			
			System.out.println(this.getIpAddress()+":Sending MACT to "+this.upStreamNode);
			
		}
	}

	
	
	
	
	public void floodPacket(Packet pktObj) {

		if (pktObj.pktType == PacketType.RREQ) {
			RReqPacket pkt = (RReqPacket) pktObj;

			int d = pkt.TTL - 1;
			if (d >= 0) {

				pkt.TTL--;
				pkt.hops++;
				pkt.hopCountAuthenticator = HashMd5.getMd5Hash(pkt.hopCountAuthenticator);

				this.sending = 10;

				pkt.nodePtr.add(this.nodeIpSign);

				// this.dumpNodeStatus("ip sign"+this.nodeIpSign.ipAddress);

				/*for (int i = 0; i < no_of_reachableNodes; i++) {
					if (pkt.nodePtr.get(pkt.nodePtr.size() - 1).ipAddress != reachableNodes[i]) {
						this.sendPacket(reachableNodes[i], pkt);
					}
				}*/
				boolean flag=false;
				for (int i = 0; i < no_of_reachableNodes; i++) {
					
					flag=isSuccessor(reachableNodes[i],pktObj.senderIp);
					
					if(!flag){
						this.sendPacket(reachableNodes[i], pktObj);
					}
					
				}
				
				
			} else {
				return;
			}
		}

	}

	
	public boolean isSuccessor(int node1,int node2){
		// checks whether node1 is a successor of node2 and returns true or false
		boolean flag=false;
		
		for (Iterator<Integer> itr = NodeTable.getNode(node2).downStreamNodes.iterator(); itr.hasNext();) {
			int tmpNode=itr.next();
			if(tmpNode==node1){
				flag=true;
				break;
			}
			else if(NodeTable.getNode(node1).downStreamNodes.size()>=0){
				flag = isSuccessor(node1,tmpNode);
				if(flag){
					break;
				}
			}
		}
		
		return flag;
	}
	
	
	public void multicastPacket(Packet pktObj) {
		if (pktObj.pktType == PacketType.DATA_PKT) {
			DataPacket pkt = (DataPacket) pktObj;
			
			int d = pkt.TTL - 1;
			if (d >= 0) {
				pkt.TTL--;
				pkt.hops++;
				pkt.hopCountAuthenticator = HashMd5.getMd5Hash(pkt.hopCountAuthenticator);

				pkt.nodePtr.add(this.nodeIpSign);

				// this.dumpNodeStatus("ip sign"+this.nodeIpSign.ipAddress);
				boolean flag=false;
				for (Iterator<Integer> itr = this.downStreamNodes.iterator(); itr.hasNext();) {
					this.sending = 10;
					this.sendPacket(itr.next(), pkt);
					flag=true;
				}
				if(flag)	this.dumpNodeStatus("Multicasting "+PacketType.DATA_PKT, 1);
			} else {
				return;
			}
		} else if (pktObj.pktType == PacketType.MRATE) {
			MRatePacket pkt = (MRatePacket) pktObj;

			//System.out.println(this.getIpAddress() + "=>Data Rate: " + calcPercvdDataRate());

			int d = pkt.TTL - 1;
			if (d >= 0) {
				pkt.TTL--;
				pkt.hops++;
				pkt.hopCountAuthenticator = HashMd5.getMd5Hash(pkt.hopCountAuthenticator);

				pkt.nodePtr.add(this.nodeIpSign);

				// this.dumpNodeStatus("ip sign"+this.nodeIpSign.ipAddress);

				for (Iterator<Integer> itr = this.downStreamNodes.iterator(); itr.hasNext();) {
					this.sending = 10;
					this.sendPacket(itr.next(), pkt);
				}

			} else {
				return;
			}
		}

	}

	
	
	public void unicastPacket(Packet pktObj) {

		if (pktObj.pktType == PacketType.RREP) {
			RRepPacket pkt = (RRepPacket) pktObj;

			int d = pkt.TTL - 1;
			if (d >= 0) {
				pkt.TTL--;

				this.sending = 10;
				pkt.hops++;

				// pkt.hopCountAuthenticator =
				// HashMd5.getMd5Hash(pkt.hopCountAuthenticator);

				this.sending = 10;
				pkt.nodePtr.add(this.nodeIpSign);
				// this.dumpNodeStatus("ip sign"+this.nodeIpSign.ipAddress);

				pkt.nodeRevPtr.remove(pkt.nodeRevPtr.size() - 1);

				
				this.sendPacket(pkt.nodeRevPtr.get(pkt.nodeRevPtr.size() - 1).ipAddress, pkt);
			} else {
				return;
			}
		}

		else if (pktObj.pktType == PacketType.MACT) {
			MActPacket pkt = (MActPacket) pktObj;

			int d = pkt.TTL - 1;
			if (d >= 0) {
				pkt.TTL--;

				this.sending = 10;
				pkt.hops++;
				
				this.sending = 10;
				pkt.nodePtr.add(this.nodeIpSign);
				// this.dumpNodeStatus("ip sign"+this.nodeIpSign.ipAddress);

				pkt.nodeRevPtr.remove(pkt.nodeRevPtr.size() - 1);

				this.upStreamNode = pkt.nodeRevPtr.get(pkt.nodeRevPtr.size() - 1).ipAddress;
				
				this.sendPacket(pkt.nodeRevPtr.get(pkt.nodeRevPtr.size() - 1).ipAddress, pkt);
			} else {
				return;
			}
		}
	}

	public void sendPacket(int sendToIpAddress, Packet pktObj) {
		// Generic Function to send a packet to another node(reachable node)
		Packet cpyPkt = null;
		if(sendToIpAddress==0){
			return;
		}
		if (this.isReachable(sendToIpAddress)) {
			if (pktObj.pktType == PacketType.DATA_PKT) {
				cpyPkt = ((DataPacket) pktObj).getClone();
				NodeTable.getNode(sendToIpAddress).linkHistory.put(this.getIpAddress(), Node.getTimeStamp());
			} else if (pktObj.pktType == PacketType.RREQ) {
				cpyPkt = ((RReqPacket) pktObj).getClone();
			} else if (pktObj.pktType == PacketType.RREP) {
				cpyPkt = ((RRepPacket) pktObj).getClone();
			} else if (pktObj.pktType == PacketType.MACT) {
				cpyPkt = ((MActPacket) pktObj).getClone();
			} else if (pktObj.pktType == PacketType.MRATE) {
				cpyPkt = ((MRatePacket) pktObj).getClone();
			} else if (pktObj.pktType == PacketType.BEACON) {
				cpyPkt = ((BeaconPacket) pktObj).getClone();
				NodeTable.getNode(sendToIpAddress).beaconHistory.put(this.getIpAddress(), Node.getTimeStamp());
			} else if (pktObj.pktType == PacketType.STICK_TO_ME) {
				cpyPkt = ((StickToMePacket) pktObj).getClone();
			} else if (pktObj.pktType == PacketType.TREE_PRUNE) {
				cpyPkt = ((TreePrunePacket) pktObj).getClone();
			}

			// this.dumpNodeStatus("Sending from "+this.getIpAddress()+"->Sending to "+sendToIpAddress);
			NodeTable.getNode(sendToIpAddress).buffer.add(cpyPkt);
		}
	}

	public boolean verifyPacket(Packet pktObj) {
		boolean valid = false;

		// this.dumpNodeStatus("Verifying the packet",true);

		if (pktObj.pktType == PacketType.DATA_PKT) {
			return true;
		}
		if (pktObj.pktType == PacketType.RREQ) {
			// RReqPacket pkt = (RReqPacket) pktObj;

			valid = this.verifyPacketSign(pktObj);
			if (valid) {
				valid = this.verifyHopCount(pktObj);
			}
			// if(valid){
			// valid = this.verifyNodePtr(pktObj);
			// }
			if (valid) {
				valid = this.verifyHopCountAnchor(pktObj);
			}
			if (valid) {
				valid = this.verifyHopCount(pktObj);
			}
		}
		if (pktObj.pktType == PacketType.RREP) {
			return true;
		}
		if (pktObj.pktType == PacketType.MACT) {
			return true;
		}
		if (pktObj.pktType == PacketType.MRATE) {
			return true;
		}
		if (pktObj.pktType == PacketType.BEACON) {
			return true;
		}
		if (pktObj.pktType == PacketType.STICK_TO_ME) {
			return true;
		}
		if (pktObj.pktType == PacketType.TREE_PRUNE) {
			return true;
		}
		return valid;
	}

	public boolean verifyPacketSign(Packet pktObj) {
		boolean valid = false;
		int senderIp;
		PublicKey pkey = null;
		BigInteger signedData = null, hashedPayload = null;

		// this.dumpNodeStatus("Verifying signature",true);

		if (pktObj.pktType == PacketType.RREQ) {
			RReqPacket pkt = (RReqPacket) pktObj;

			senderIp = pkt.senderIp; // .nodePtr.get(pkt.nodePtr.size()-1).ipAddress;
			// Extract sender IpAddress from Packet Header
			pkey = SharedKey.getPubKey(senderIp); // Get public key of senderIp from the Public Key Hash Table
			signedData = pkt.signedData; // Extract signedData from Packet Header
			hashedPayload = pkt.getHash(); // Hash the pkt payload
		} else if (pktObj.pktType == PacketType.RREP) {
			RRepPacket pkt = (RRepPacket) pktObj;

			senderIp = pkt.senderIp; // .nodePtr.get(pkt.nodePtr.size()-1).ipAddress;
			// Extract sender IpAddress from Packet Header
			pkey = SharedKey.getPubKey(senderIp); // Get public key of senderIp from the Public Key Hash Table
			signedData = pkt.signedData; // Extract signedData from Packet Header
			hashedPayload = pkt.getHash(); // Hash the pkt payload
		}

		valid = this.verifySign(pkey, signedData, hashedPayload); // verify the signature
		if (valid) {
			// this.dumpNodeStatus("Valid packet signature");
		} else {
			// this.dumpNodeStatus("Invalid packet signature");
		}
		return valid;
	}

	public boolean verifyHopCountAnchor(Packet pktObj) {
		boolean valid = false;
		int senderIp;
		PublicKey pkey = null;
		BigInteger signedData = null;
		BigInteger hashedPayload = null;

		// this.dumpNodeStatus("Verifying Hop Count anchor",true);

		if (pktObj.pktType == PacketType.RREQ) {
			RReqPacket pkt = (RReqPacket) pktObj;

			senderIp = pkt.senderIp; // Extract sender IpAddress from Packet
			// Header
			pkey = SharedKey.getPubKey(senderIp); // Get public key of
			// senderIp from the Public
			// Key Hash Table

			signedData = pkt.hopCountAnchor.hcaSign; // Extract signedData
			// from Packet Header
			hashedPayload = HashMd5.getMd5Hash(pkt.hopCountAnchor.hcaValue); // Hash
			// the
			// pkt
			// payload
		} else if (pktObj.pktType == PacketType.RREP) {
			RRepPacket pkt = (RRepPacket) pktObj;

			senderIp = pkt.senderIp; // Extract sender IpAddress from Packet
			// Header
			pkey = SharedKey.getPubKey(senderIp); // Get public key of
			// senderIp from the Public
			// Key Hash Table

			signedData = pkt.hopCountAnchor.hcaSign; // Extract signedData
			// from Packet Header
			hashedPayload = HashMd5.getMd5Hash(pkt.hopCountAnchor.hcaValue); // Hash
			// the
			// pkt
			// payload
		}

		valid = this.verifySign(pkey, signedData, hashedPayload); // verify
		// the
		// signature
		if (valid) {
			// this.dumpNodeStatus("Valid hop count anchor");
		} else {
			// this.dumpNodeStatus("Invalid hop count anchor");
		}

		return valid;
	}

	public boolean verifyHopCount(Packet pktObj) {
		boolean valid = false;

		// this.dumpNodeStatus("Verifying Hop Count",true);

		if (pktObj.pktType == PacketType.RREQ) {
			RReqPacket pkt = (RReqPacket) pktObj;

			BigInteger hcAuth = pkt.hopCountAuthenticator;
			int d = pkt.hops;
			// this.dumpNodeStatus("HCAuth "+hcAuth);
			// this.dumpNodeStatus("hash HCAuth
			// "+HashMd5.getHashChain(Node.maxHops - d, hcAuth)+" HCanchor
			// "+pkt.hopCountAnchor.hcaValue);

			if (HashMd5.getHashChain(Node.maxHops - d, hcAuth).equals(
					pkt.hopCountAnchor.hcaValue)) {
				valid = true;
			}
		} else if (pktObj.pktType == PacketType.RREP) {
			RRepPacket pkt = (RRepPacket) pktObj;

			BigInteger hcAuth = pkt.hopCountAuthenticator;
			int d = pkt.hops;
			// this.dumpNodeStatus("HCAuth "+hcAuth);
			// this.dumpNodeStatus("hash HCAuth
			// "+HashMd5.getHashChain(Node.maxHops - d, hcAuth)+" HCanchor
			// "+pkt.hopCountAnchor.hcaValue);

			if (HashMd5.getHashChain(Node.maxHops - d, hcAuth).equals(
					pkt.hopCountAnchor.hcaValue)) {
				valid = true;
			}
		}
		if (valid) {
			// this.dumpNodeStatus("Valid hop count");
		} else {
			// this.dumpNodeStatus("Invalid hop count");
		}

		return valid;
	}

	public boolean verifyNodePtr(Packet pktObj) {
		boolean valid = false;

		int nodeIp;
		PublicKey pkey;
		BigInteger signedIp, hashedIp;

		// this.dumpNodeStatus("Verifying Ptr Nodes signature",true);

		if (pktObj.pktType == PacketType.RREQ) {
			RReqPacket pkt = (RReqPacket) pktObj;

			NodeIpSign tmpNodeIpSign = null;
			int d = pkt.hops;
			int k = d;

			for (Iterator<NodeIpSign> itr = pkt.nodePtr.iterator(); itr.hasNext();) {
				tmpNodeIpSign = itr.next();
				// this.dumpNodeStatus(tmpNodeIpSign.ipAddress+"");

				nodeIp = tmpNodeIpSign.ipAddress; // Extract sender IpAddress
				// from Packet Header
				pkey = SharedKey.getPubKey(nodeIp); // Get public key of
				// senderIp from the Public
				// Key Hash Table
				signedIp = tmpNodeIpSign.sign; // Extract signedData from
				// Packet Header
				hashedIp = HashMd5.getMd5Hash(nodeIp); // Hash the pkt payload

				valid = this.verifySign(pkey, signedIp, hashedIp); // verify
				// the
				// signature

				if (!valid) {
					// this.dumpNodeStatus("Node:"+nodeIp+" Invalid node ip
					// signature");
					break;
				}
			}
			if (valid) {
				// this.dumpNodeStatus("Valid node ip signature");
			}
		}

		if (pktObj.pktType == PacketType.RREP) {
			RRepPacket pkt = (RRepPacket) pktObj;

			NodeIpSign tmpNodeIpSign = null;
			int d = pkt.hops;
			int k = d;

			for (Iterator<NodeIpSign> itr = pkt.nodePtr.iterator(); itr.hasNext();) {
				tmpNodeIpSign = itr.next();
				// this.dumpNodeStatus(tmpNodeIpSign.ipAddress+"");

				nodeIp = tmpNodeIpSign.ipAddress; // Extract sender IpAddress from Packet Header
				pkey = SharedKey.getPubKey(nodeIp); // Get public key of senderIp from the Public Key Hash Table
				signedIp = tmpNodeIpSign.sign; // Extract signedData from Packet Header
				hashedIp = HashMd5.getMd5Hash(nodeIp); // Hash the pkt payload

				valid = this.verifySign(pkey, signedIp, hashedIp); // verify the signature

				if (!valid) {
					// this.dumpNodeStatus("Node:"+nodeIp+" Invalid node ip signature");
					break;
				}
			}
			if (valid) {
				// this.dumpNodeStatus("Valid node ip signature");
			}
		}

		return valid;
	}

	public void processPacket(Packet pktObj) {
		this.dumpNodeStatus("Processing Packet", 3);
		
		if (pktObj.pktType == PacketType.DATA_PKT) {
			String rcvdMsg = new String(pktObj.getPayload().toByteArray());
			this.dumpNodeStatus("Received Data: "+rcvdMsg, 4);
			
			/*System.out.println("Introducing processing delay");
			try {
				Thread.sleep(2500);
			} catch (InterruptedException e) {	}
			System.out.println("processing delay finished");*/
			
			
			/*if(connThread==null){
				connThread=new TimerB("connThread");
				startThread(connThread,20000,6000);
			}*/
			
			
			// this.dumpNodeStatus("Received message by "+this.ipAddress+" : \n" +rcvdMsg);
			DataPacket pkt = ((DataPacket) pktObj).getClone();
			multicastPacket(pkt);
		}
		if (pktObj.pktType == PacketType.MRATE) {
			// check drop in data rate
			// update mrate message

			// this.dumpNodeStatus("Received message by "+this.ipAddress+" : \n"
			// +rcvdMsg);
			MRatePacket pkt = ((MRatePacket) pktObj).getClone();
			
			
			if(pkt.percRate.size()>1){
				if(Math.abs(pkt.percRate.get(0) - this.calcPercvdDataRate())>14){
					this.createSignSend(PacketType.TREE_PRUNE);
					this.initiateRouteDiscovery();
				}
			}
			pkt.percRate.add(this.calcPercvdDataRate());
			System.out.println("current Mrate values");
			for (Iterator<Integer> itr = pkt.percRate.iterator(); itr.hasNext();) {
				System.out.println("mrate packet received by "+this.getIpAddress()+"=>"+itr.next());
			}
			
			multicastPacket(pkt);
		}
		
		
		
		else if (pktObj.pktType == PacketType.RREQ) {
			RReqPacket pkt = ((RReqPacket) pktObj).getClone();

			if (!isHopLessThanPrev(pkt.msgSeqNo, pkt.hops)) {
				this.dumpNodeStatus("Duplicate found - Packet dropped", 4);
				this.dropPacket(pkt);
			}
			else if (this.getIpAddress() == pkt.senderIp) {
				this.dumpNodeStatus("Packet dropped - to avoid cycles", 4);
				this.dropPacket(pkt);
			}
			else if (this.Connected) {
				// this.dumpNodeStatus("src "+pkt.senderIp);
				if (this.getNodeType() == NodeType.SOURCE
						|| this.getNodeType() == NodeType.MEMBER_NODE
						|| this.getNodeType() == NodeType.TREE_NODE) {
					
						// this.dumpNodeStatus("Sending to "+pkt.senderIp);

						// generate and unicast RREP
						this.createSignSend(PacketType.RREP, pkt.senderIp, pkt.hops, pkt.nodePtr);
				}
			} 
			else if (this.getNodeType() == NodeType.MEMBER_NODE
						|| this.getNodeType() == NodeType.NON_MEMBER_NODE) {
				this.floodPacket(pkt);			// forward the packet
			}
			
		}

		else if (pktObj.pktType == PacketType.RREP) {
			RRepPacket pkt = ((RRepPacket) pktObj).getClone();
			// this.dumpNodeStatus("destin "+pkt.destIp);

			if (!isHopLessThanPrev(pkt.msgSeqNo, pkt.hops)) {
				this.dumpNodeStatus("Duplicate found - Packet dropped", 4);
				this.dropPacket(pkt);
			}
			else if (this.getIpAddress() == pkt.destIp) {
				if(!this.Connected){
					this.rrepTmpList.add(pkt);
					// this.dumpNodeStatus("Stored the RREP into temp buffer");
				}
			}
			else{
				this.unicastPacket(pkt);		// forward
			}
		}


		else if (pktObj.pktType == PacketType.MACT) {
			// this.dumpNodeStatus("Got MACT Message",true);
			MActPacket pkt = ((MActPacket) pktObj).getClone();
			// this.dumpNodeStatus("src "+pkt.senderIp);

			
			// check whether it is already a downstream node - add if not
			
			// update its downstream nodes
			this.downStreamNodes.add(pkt.nodePtr.get(pkt.nodePtr.size() - 1).ipAddress);
			
			if(this.downStreamNodes.size()==1){
				// start threads
				this.stopThread(this.beaconChkThread);
				beaconChkThread = new Timer("beaconChkThread");
				startThread(beaconChkThread,2*Node.beaconRate);
				
				this.stopThread(this.stickToMeThread);
				stickToMeThread = new Timer("stickToMeThread");
				startThread(stickToMeThread, Node.stickToMeRate);
			}
			
			
			if(this.Connected){
				if (this.getNodeType() == NodeType.SOURCE
						|| this.getNodeType() == NodeType.MEMBER_NODE
						|| this.getNodeType() == NodeType.TREE_NODE) {
						
					/*if(beacon_Timer!=null){
						beacon_Timer.cancel();
					}
					beacon_Timer = new TimerB("beacon_Timer");
					startTimer(beacon_Timer,4*Node.beaconRate);*/
	
					//System.out.println("am already source od tree or member");
				
					// generate MRATE
				}
			}
			else if(this.getNodeType() == NodeType.MEMBER_NODE || this.getNodeType() == NodeType.NON_MEMBER_NODE){
				if (this.getNodeType() == NodeType.NON_MEMBER_NODE) {
					// Make itself as a tree node
					this.setNodeType(NodeType.TREE_NODE);
					this.Connected=true;
				}

				
				this.unicastPacket(pkt);
				
				
				//if(beaconThread!=null){
				//	beaconThread.cancel();
				//}
				this.stopThread(beaconThread);
				beaconThread = new Timer("beaconThread");
				startThread(beaconThread,Node.beaconRate);
				
			}
			
			
		}

		
		
		else if (pktObj.pktType == PacketType.BEACON) {
			//reset the beacon timer..
			
			/*if(beacon_Timer!=null){
				beacon_Timer.cancel();
			}
			beacon_Timer = new TimerB("beacon_Timer");
			startTimer(beacon_Timer,4*Node.beaconRate);*/
		}

		else if (pktObj.pktType == PacketType.STICK_TO_ME) {
			//reset the stick to me timer..
			
			this.stopThread(StickToMe_Timer);
			StickToMe_Timer = new Timer("StickToMe_Timer");
			startTimer(StickToMe_Timer,4*Node.stickToMeRate);
		}

		else if (pktObj.pktType == PacketType.TREE_PRUNE) {
			// Update its list of downstream nodes
			for (int m=0;m<this.downStreamNodes.size();m++) {
				if(this.downStreamNodes.get(m)==pktObj.senderIp){
					this.downStreamNodes.remove(m);
				}
			}
			
			// Check if it can prune itself
			if(this.type==NodeType.SOURCE || this.type==NodeType.MEMBER_NODE){
				//can not prune itself
				if(this.downStreamNodes.size()==0){
					this.stopThread(beaconChkThread);
					this.stopThread(stickToMeThread);
				}
			}
			if(this.type==NodeType.TREE_NODE){
				if(this.downStreamNodes.size()==0){
					// prunes itself
					
					this.stopThread(StickToMe_Timer);
					this.stopThread(stickToMeThread);
					
					
					this.setNodeType(NodeType.NON_MEMBER_NODE);
					this.Connected=false;
					this.createSignSend(PacketType.TREE_PRUNE);		// to its upstream node
					
					this.stopThread(beaconThread);
					this.stopThread(beaconChkThread);
					
					this.upStreamNode=0;
				}
				else{
					// else continues to act as tree node
				}
			}
		}
		
		
	}

	public void dropPacket(Packet pkt) {
		this.dropping = 5;
	}

	public void initiateRouteDiscovery() {
		
		if(!this.Connected){
			this.upStreamNode=0;
			System.out.println(this.getIpAddress()+":initiating route discovery");
			
			this.createSignSend(PacketType.RREQ);
			
			this.stopThread(RA_Timer);
			
			RA_Timer = new Timer("RA_Timer");
			startTimer(RA_Timer, Node.RA_Time);
		}
	}

	
	public void printLinkHistory() {
		System.out.println("\nLink history");

		Timestamp ts = getTimeStamp(Calendar.MINUTE, -1);
		int cnt = 0;
		Set keySet = this.linkHistory.keySet();
		Iterator keyIterator = keySet.iterator();
		while (keyIterator.hasNext()) {
			cnt = 0;
			Object key = keyIterator.next();
			System.out.print("Key: " + key + ", ");
			Collection values = (Collection) this.linkHistory
					.get((Integer) key);
			Iterator valuesIterator = values.iterator();
			while (valuesIterator.hasNext()) {
				Timestamp tsn = (Timestamp) valuesIterator.next();
				if (tsn.after(ts)) {
					cnt++;
				}
				System.out.print("Value: " + tsn + ". ");
			}
			System.out.print("Count :" + cnt);
			System.out.print("\n");
		}
		System.out.println(getTimeStamp());
		System.out.println(getTimeStamp(Calendar.MINUTE, -1));

	}

	public int calcPercvdDataRate() {
		// packets per minute
		Timestamp ts = getTimeStamp(Calendar.SECOND, -30);
		int cnt = 0;
		Set<Integer> keySet = this.linkHistory.keySet();
		Iterator<Integer> keyIterator = keySet.iterator();

		while (keyIterator.hasNext()) {
			cnt = 0;
			Object key = keyIterator.next();
			Collection values = (Collection) this.linkHistory
					.get((Integer) key);
			Iterator valuesIterator = values.iterator();
			while (valuesIterator.hasNext()) {
				Timestamp tsn = (Timestamp) valuesIterator.next();
				if (tsn.after(ts)) {
					cnt++;
				}
			}
		}
		return cnt*2;
	}

	public void startTimer(Timer t, int milSec) {
		// Runs task after expiration of timer
		
		
		
		t.schedule(new TimerTask() {
			@Override
			public void run() {
				String curThreadName = Thread.currentThread().getName();

				// System.out.println("\n "+getIpAddress()+" inside Timer...");
				if (curThreadName.equals("RA_Timer")) {
					
					System.out.println("Timer Expired");
					if (rrepTmpList.size() != 0) {

						System.out.println("selecting shortest route among RREPs");

						int min = 999;
						RRepPacket dpkt = null;
						Object dpt[] = rrepTmpList.toArray();
						for (int i = 0; i < dpt.length; i++) {
							int prevNodeIp = ((RRepPacket) dpt[i]).nodePtr.get(((RRepPacket) dpt[i]).nodePtr.size() - 1).ipAddress;
							if (NodeTable.getNode(prevNodeIp).type == NodeType.SOURCE) {
								dpkt = ((RRepPacket) dpt[i]).getClone();
								break;
							}
							if (min > ((RRepPacket) dpt[i]).hops) {
								min = ((RRepPacket) dpt[i]).hops;
								dpkt = ((RRepPacket) dpt[i]).getClone();
							}
						}

						System.out.println("selected shortest route");
						// dumpNodeStatus("Shortest packet is" + dpkt);
						// dumpNodeStatus("Unicasting to "+ dpkt.senderIp);

						
						if(type==NodeType.NON_MEMBER_NODE){
							setNodeType(NodeType.MEMBER_NODE);
						}
						createSignSend(PacketType.MACT, dpkt.senderIp,dpkt.hops, dpkt.nodePtr);
						
						rrepTmpList.clear();
						
						
						stopThread(beaconThread);
						beaconThread = new Timer("beaconThread");
						startThread(beaconThread,Node.beaconRate);
						
						/*if(beacon_Timer!=null){
							beacon_Timer.cancel();
						}
						beacon_Timer = new Timer("beacon_Timer");
						startTimer(beacon_Timer,4*Node.beaconRate);*/

						
						Connected = true;
						attempts = 0;
						// RA_Timer.cancel();
					}

					else {
						System.out.println("Didn't get any RREP");

						try {
							// Back off for some time before starting again
							if (attempts < 5) {
								Thread.sleep(BO_Time);
								attempts++;

								BO_Time = (int) (1.15 * BO_Time); // 15 % increase in BO_Timer
								rrepTmpList.clear();

								System.out.println(getIpAddress() + "--Attempts--" + attempts);
								
								
								stopThread(beaconThread);
								Connected = false;
								
								initiateRouteDiscovery();
							}

						} catch (InterruptedException e) {
						}
					}
				}
				
				else if (curThreadName.equals("StickToMe_Timer")) {
					
					System.out.println(getIpAddress()+" StickToMe_Timer expired");
					
					System.out.println("sending tree prune");
					//if(upStreamNode!=0){
					//	createSignSend(PacketType.TREE_PRUNE);
					//}
					
					
					stopThread(beaconThread);
					
					if(Connected){
						Connected = false;
						initiateRouteDiscovery();
						//this.cancel();
					}
				}

			}
		}, milSec);

		t.purge();
		
	}
	
	public void stopThread(Timer t) {
		if(t!=null){
			t.cancel();
			t.purge();
			t=null;
		}
	}
	public void startThread(Timer t, int milSec) {
		startThread(t,0,milSec);
	}

	public void startThread(Timer t,int delay, int milSec) {
		/*
		 *  Runs task periodically at fixed rate
		 */
		
		t.scheduleAtFixedRate(new TimerTask() {
			
			@Override
			public void run() {
				
				String curThreadName = Thread.currentThread().getName();
				if (curThreadName.equals("bufferThread")) {
					
						Packet pktObj;
						boolean validPacket = true;
	
						// Read and process packets from the buffer
	
						if (buffer.peek() != null) {
							receiving = 3;
	
							pktObj = buffer.poll();
							dumpNodeStatus("Read new packet - " + pktObj.pktType, 1);
							dumpNodeStatus("Sender - " + pktObj.senderIp, 2);
	
							dumpNodeStatus("verifying packet", 2);
							validPacket = verifyPacket(pktObj); // Verify the packet
	
							if (validPacket) {
								dumpNodeStatus("Valid Packet", 3);
								processPacket(pktObj); // Process the packet if verification successful
							} else {
								dumpNodeStatus("Invalid Packet - Packet Dropped", 3);
								dropPacket(pktObj); // Drop the packet if verification is unsuccessful
							}
	
						}
					
				}

				else if(curThreadName.equals("stickToMeThread")){
					
						dumpNodeStatus("Sending StickToMe Message to downstream nodes", 1);
						createSignSend(PacketType.STICK_TO_ME);
					
				}
				else if(curThreadName.equals("beaconThread")){
					
						dumpNodeStatus("Sending Beacon to " + upStreamNode, 1);
						createSignSend(PacketType.BEACON);
					
					
				}
				else if(curThreadName.equals("beaconChkThread")){
					//System.out.println(getIpAddress()+":Checking for beacons..");
					
					Timestamp ts = getTimeStamp(Calendar.SECOND, -10);
					int cnt = 0;
					Set<Integer> keySet = beaconHistory.keySet();
					Iterator<Integer> keyIterator = keySet.iterator();
					while (keyIterator.hasNext()) {
						
						cnt = 0;
						Object key = keyIterator.next();
						//System.out.println(getIpAddress()+":Checking for beacons..from:"+key);
						
						Collection<Timestamp> values = beaconHistory.get((Integer) key);
						Iterator<Timestamp> valuesIterator = values.iterator();
						while (valuesIterator.hasNext()) {
							Timestamp tsn = (Timestamp) valuesIterator.next();
							if (tsn.after(ts)) {
								cnt++;
								break;
							}
						}
						
						if(cnt==0){
							//System.out.println(getIpAddress()+":didnt get beacons..from:"+key);
							
							for (int m=0;m<downStreamNodes.size();m++) {
								if(downStreamNodes.get(m)==key){
									downStreamNodes.remove(key);
								}
							}
							
							//Connected = false;
						}
					}
					
					if(downStreamNodes.size()==0){
						// Check if it can prune itself
						if(type==NodeType.SOURCE || type==NodeType.MEMBER_NODE){
							//can not prune itself
						}
						if(type==NodeType.TREE_NODE && Connected){		// also check for connected
							
								// prunes itself
								System.out.println(getIpAddress()+":pruning itself..");
								
								setNodeType(NodeType.NON_MEMBER_NODE);
								Connected=false;
								beaconThread.cancel();
								beaconThread=null;
								createSignSend(PacketType.TREE_PRUNE);		// to its upstream node
								upStreamNode=0;
								beaconChkThread=null;
								System.out.println("Cancelling this");
								//this.cancel();
								
						}
						stickToMeThread.cancel();
					}
					
				}
				else if(curThreadName.equals("connThread")){
					
					/*if(Math.abs(30 - calcPercvdDataRate())>14){
						sendTreePrune();
						Connected = false;
						initiateRouteDiscovery();
					}
					sendBeacon();*/
				}
				

			}
		}, delay, milSec);
		
		t.purge();
	}

	public void addMsgEntry(long msgSeq, int hopCount) {
		rcvdMsgs.put(msgSeq, hopCount);
	}

	public int getMsgEntry(long msgSeq, int hopCount) {
		// this.dumpNodeStatus("msgSeq->"+msgSeq+"\nnewHopCount->"+hopCount+"  OldHopCount->"+rcvdMsgs.get(msgSeq));
		return rcvdMsgs.get(msgSeq);
	}

	public void updateMsgEntry(long msgSeq, int hopCount) {
		rcvdMsgs.remove(msgSeq);
		rcvdMsgs.put(msgSeq, hopCount);
	}

	public boolean isHopLessThanPrev(long msgSeq, int hopCount) {
		int oldHopCount;

		// this.dumpNodeStatus("msgSeq->"+msgSeq+"\nnewHopCount->"+hopCount+" OldHopCount->"+rcvdMsgs.get(msgSeq));

		if (rcvdMsgs.get(msgSeq) != null) {
			oldHopCount = rcvdMsgs.get(msgSeq);

			if (hopCount < oldHopCount) {
				this.updateMsgEntry(msgSeq, hopCount);
				// this.dumpNodeStatus("Updated:  msgSeq->"+msgSeq+"newHopCount->"+rcvdMsgs.get(msgSeq));
				return true;
			} else {
				return false;
			}
		} else {
			this.addMsgEntry(msgSeq, hopCount);
			// this.dumpNodeStatus("Updated: msgSeq->"+msgSeq+"newHopCount->"+rcvdMsgs.get(msgSeq));
			return true;
		}
	}

	
	
	public boolean canSend() {
		Random randGen = new Random();

		int randIndex = randGen.nextInt(100);
		if (this.distributeTF[randIndex] == 1) {
			return true;
		} else if (this.distributeTF[randIndex] == 0) {
			return false;
		}

		return false;
	}

	
	
	
	
	
	
	private void genCryptKeys() {
		KeyPairGenerator kpg = null;
		KeyPair kp = null;
		KeyFactory fact = null;

		try {
			kpg = KeyPairGenerator.getInstance("RSA");

			kpg.initialize(CRYPT_BIT_SIZE); // 512 bits default
			kp = kpg.genKeyPair();

			fact = KeyFactory.getInstance("RSA");
			this.rsaPub = fact.getKeySpec(kp.getPublic(),
					RSAPublicKeySpec.class);
			this.rsaPriv = fact.getKeySpec(kp.getPrivate(),
					RSAPrivateKeySpec.class);

			this.pubKey = kp.getPublic();
			this.privKey = kp.getPrivate();

		} catch (NoSuchAlgorithmException e) {
		} catch (InvalidKeySpecException e) {
		}
	}

	public BigInteger encrypt(RSAPublicKeySpec publicKeyObj, BigInteger message) {
		// Encrypted using public key
		return message.modPow(publicKeyObj.getPublicExponent(), publicKeyObj.getModulus());
	}

	public BigInteger decrypt(BigInteger encrypted) {
		// Decrypted using private key
		return encrypted.modPow(this.rsaPriv.getPrivateExponent(), this.rsaPriv.getModulus());
	}

	
	public BigInteger digSign(int data) {
		// Sign integer data - using private key
		Signature sig = null;
		try {
			sig = Signature.getInstance("MD5WithRSA");
			sig.initSign(privKey);
			sig.update((byte) data);
			return (new BigInteger(sig.sign()));
		} catch (NoSuchAlgorithmException e) {
		} catch (InvalidKeyException e) {
		} catch (SignatureException e) {
		}
		return null;
	}

	public boolean verifySign(PublicKey nodPubKey, BigInteger signedData, int data) {
		// verify signature of BigInteger data - using public key
		Signature sig = null;
		try {
			sig = Signature.getInstance("MD5WithRSA");

			sig.initVerify(nodPubKey);
			sig.update((byte) data);

			return (sig.verify(signedData.toByteArray()));

		} catch (NoSuchAlgorithmException e) {
		} catch (InvalidKeyException e) {
		} catch (SignatureException e) {
		}
		return false;
	}

	public BigInteger digSign(BigInteger data) {
		// Sign BigInteger data - using private key
		Signature sig = null;
		try {
			sig = Signature.getInstance("MD5WithRSA");
			sig.initSign(privKey);
			sig.update(data.toByteArray());
			return (new BigInteger(sig.sign()));

		} catch (NoSuchAlgorithmException e) {
		} catch (InvalidKeyException e) {
		} catch (SignatureException e) {
		}
		return null;
	}

	public boolean verifySign(PublicKey nodPubKey, BigInteger signedData, BigInteger data) {
		// verify signature of BigInteger data - using public key
		Signature sig = null;

		try {
			sig = Signature.getInstance("MD5WithRSA");

			sig.initVerify(nodPubKey);
			sig.update(data.toByteArray());

			return (sig.verify(signedData.toByteArray()));

		} catch (NoSuchAlgorithmException e) {
		} catch (InvalidKeyException e) {
		} catch (SignatureException e) {
		}
		return false;
	}

	public static String getCurTime(String dateFormat) {
		// Generic - function To get time with specified format
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		return sdf.format(cal.getTime());
	}

	public static Timestamp getTimeStamp(int calType, int offset) {
		// Generic - function to get cur timestamp - offset
		Calendar cal = Calendar.getInstance();

		cal.add(calType, offset);

		Date curDate = cal.getTime();
		return (new Timestamp(curDate.getTime()));
	}

	public static Timestamp getTimeStamp() {
		// Generic - function to get cur timestamp
		Calendar cal = Calendar.getInstance();
		Date curDate = cal.getTime();
		return (new Timestamp(curDate.getTime()));
	}

	public synchronized void dumpNodeStatus(String data, int level) {
		// Tree Level is to be given carefully..
		DefaultTreeModel treeModel = (DefaultTreeModel) nodeMonitorTree.getModel();
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();
		DefaultMutableTreeNode tmpTreeNode = new DefaultMutableTreeNode(Node.getCurTime("hh:mm:ss:SSS")	+ "   " + data);

		DefaultMutableTreeNode tmpParentNode = root; // default parent is root new CheckBoxNode("Accessibility", true)
		try {
			for (int i = 1; i < level; i++) {
				tmpParentNode = (DefaultMutableTreeNode) treeModel.getChild(
					tmpParentNode, tmpParentNode.getChildCount() - 1);
			}

			treeModel.insertNodeInto((DefaultMutableTreeNode) tmpTreeNode,tmpParentNode, tmpParentNode.getChildCount());
			
			nodeMonitorTree.scrollRowToVisible(root.getChildCount());
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println(this.getIpAddress()+":Tree Index out of bound");
			//if (level > 2) {
			//	dumpNodeStatus(data, level - 1);
			//}
		}
	}
}
