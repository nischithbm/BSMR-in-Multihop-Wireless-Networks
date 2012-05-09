package packet_pkg;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;

import node_pkg.Node;
import node_pkg.NodeIpSign;

public class MActPacket extends MultiHopPacket {

	
	public ArrayList<NodeIpSign> nodeRevPtr = new ArrayList<NodeIpSign>();
	
	
	
	public MActPacket(){
		// Default Constructor
	}
	
	
	// Custom Constructors
	public MActPacket(PacketType pktType,int senderIp,int destIp,int hops,BigInteger hopCountAuthenticator,HopCountAnchor hopCountAnchor,NodeIpSign nodePtr,ArrayList<NodeIpSign> nodeRevPtr,BigInteger signedData,BigInteger payload,int TTL){
		super(pktType,senderIp,destIp,hops,hopCountAuthenticator,hopCountAnchor,nodePtr,signedData,payload,TTL);
		this.nodeRevPtr = nodeRevPtr;
	}
	
	public MActPacket(PacketType pktType,int senderIp,int destIp,int hops,BigInteger hopCountAuthenticator,HopCountAnchor hopCountAnchor,ArrayList<NodeIpSign> nodePtr,ArrayList<NodeIpSign> nodeRevPtr,BigInteger signedData,BigInteger payload,int TTL){
		super(pktType,senderIp,destIp,hops,hopCountAuthenticator,hopCountAnchor,nodePtr,signedData,payload,TTL);
		this.nodeRevPtr = nodeRevPtr;
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
	
	
	
	public MActPacket getClone(){
		MActPacket newPkt = new MActPacket();
		
		newPkt.msgSeqNo = this.msgSeqNo;
		
		newPkt.pktType = this.pktType;
		newPkt.senderIp = this.senderIp;
		newPkt.destIp = this.destIp;
		newPkt.hops = this.hops;
		
		

		for(Iterator<NodeIpSign> itr=this.nodePtr.iterator();itr.hasNext();){
			newPkt.nodePtr.add(itr.next().getClone());
         }
		
		for(Iterator<NodeIpSign> itr=this.nodeRevPtr.iterator();itr.hasNext();){
			newPkt.nodeRevPtr.add(itr.next().getClone());
         }
		
		
		newPkt.hopCountAuthenticator =  this.hopCountAuthenticator;
		newPkt.hopCountAnchor = this.hopCountAnchor;
		
		
		
		// should clone this too..
		newPkt.nodeRevPtr = this.nodeRevPtr;
		
		
		newPkt.TTL = this.TTL;
		newPkt.signedData = this.signedData;
		
		newPkt.payload = this.payload;
		
		return newPkt;
	}
	
	
}
