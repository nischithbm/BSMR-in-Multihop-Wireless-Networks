package packet_pkg;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;

import node_pkg.Node;
import node_pkg.NodeIpSign;

public class MultiHopPacket extends Packet {

	
	// Header
	public ArrayList<NodeIpSign> nodePtr = new ArrayList<NodeIpSign>();
	
	//public NodeIpSign nodePtr[] = new NodeIpSign[Node.maxHops];
	
	public int hops;
	public int TTL;			// maximum no of hops a packet can travel
	
	public HopCountAnchor hopCountAnchor;
	public BigInteger hopCountAuthenticator;
	
	
	
	public MultiHopPacket(){
		// Default Constructor
	}
	
	
	// Custom Constructors
	public MultiHopPacket(PacketType pktType,int senderIp,int destIp,int hops,BigInteger hopCountAuthenticator,HopCountAnchor hopCountAnchor,NodeIpSign nodePtr,BigInteger signedData,BigInteger payload,int tt){
		super(pktType,senderIp,destIp,signedData,payload);
		this.hops = hops;
		this.TTL = tt;
		this.hopCountAuthenticator = hopCountAuthenticator;
		this.hopCountAnchor = hopCountAnchor;
		this.nodePtr.add(nodePtr);
	}
	
	public MultiHopPacket(PacketType pktType,int senderIp,int destIp,int hops,BigInteger hopCountAuthenticator,HopCountAnchor hopCountAnchor,ArrayList<NodeIpSign> nodePtr,BigInteger signedData,BigInteger payload,int tt){
		super(pktType,senderIp,destIp,signedData,payload);
		this.hops = hops;
		this.TTL = tt;
		this.hopCountAuthenticator = hopCountAuthenticator;
		this.hopCountAnchor = hopCountAnchor;
		this.nodePtr = nodePtr;
	}
	

	
	
	
	
	
	
	
	public String toString() {
	      String s = "";
	      s += "Packet Type:" + this.pktType;
	      s += " S:" + this.senderIp;
	      s += " D:" + this.destIp;
	      s += " Hops:" + this.hops;
	      s += " MsgSeq:" + this.msgSeqNo;
		     
	      return s;
	}
	
	
	public MultiHopPacket getClone(){
		MultiHopPacket newPkt = new MultiHopPacket();
		
		newPkt.msgSeqNo = this.msgSeqNo;
		
		newPkt.pktType = this.pktType;
		newPkt.senderIp = this.senderIp;
		newPkt.destIp = this.destIp;
		newPkt.hops = this.hops;
		
		
		for (Iterator<NodeIpSign> itr = this.nodePtr.iterator(); itr.hasNext();) {
			newPkt.nodePtr.add(itr.next().getClone());
		}
		
		
		newPkt.hopCountAuthenticator =  this.hopCountAuthenticator;
		newPkt.hopCountAnchor = this.hopCountAnchor;
		
		newPkt.TTL = this.TTL;
		newPkt.signedData = this.signedData;
		
		newPkt.payload = this.payload;
		
		
		
		return newPkt;
	}
	
	
	
	
	
	
	
	
	
	
	// getters
	
	
}
