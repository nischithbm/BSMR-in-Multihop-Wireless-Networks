package packet_pkg;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;

import node_pkg.Node;
import node_pkg.NodeIpSign;

public class DataPacket extends MultiHopPacket {

	
	
	
	
	public DataPacket(){
		// Default Constructor
	}
	
	
	// Custom Constructors
	public DataPacket(PacketType pktType,int senderIp,int destIp,int hops,BigInteger hopCountAuthenticator,HopCountAnchor hopCountAnchor,NodeIpSign nodePtr,BigInteger signedData,BigInteger payload,int TTL){
		super(pktType,senderIp,destIp,hops,hopCountAuthenticator,hopCountAnchor,nodePtr,signedData,payload,TTL);
	}
	
	public DataPacket(PacketType pktType,int senderIp,int destIp,int hops,BigInteger hopCountAuthenticator,HopCountAnchor hopCountAnchor,ArrayList<NodeIpSign> nodePtr,BigInteger signedData,BigInteger payload,int TTL){
		super(pktType,senderIp,destIp,hops,hopCountAuthenticator,hopCountAnchor,nodePtr,signedData,payload,TTL);
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
	
	
	
	public DataPacket getClone(){
		DataPacket newPkt = new DataPacket();
		
		newPkt.msgSeqNo = this.msgSeqNo;
		
		newPkt.pktType = this.pktType;
		newPkt.senderIp = this.senderIp;
		newPkt.destIp = this.destIp;
		newPkt.hops = this.hops;
		
		

		for(Iterator<NodeIpSign> itr=this.nodePtr.iterator();itr.hasNext();){
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
