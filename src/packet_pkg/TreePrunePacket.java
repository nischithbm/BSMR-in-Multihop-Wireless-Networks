package packet_pkg;

import java.math.BigInteger;

import node_pkg.Node;
import node_pkg.NodeIpSign;

public class TreePrunePacket extends Packet {

	
	// Header
	
	
	public TreePrunePacket(){
		// Default Constructor
	}
	
	
	// Custom Constructors
	public TreePrunePacket(PacketType pktType,int senderIp,int destIp,BigInteger signedData,BigInteger payload){
		super(pktType,senderIp,destIp,signedData,payload);
	}
	




	
	
	public String toString() {
	      String s = "";
	      s += "Packet Type:" + this.pktType;
	      s += " S:" + this.senderIp;
	      s += " D:" + this.destIp;
	     s += " MsgSeq:" + this.msgSeqNo;
		     
	      return s;
	}
	
	
	
	public TreePrunePacket getClone(){
		TreePrunePacket newPkt = new TreePrunePacket();
		
		newPkt.msgSeqNo = this.msgSeqNo;
		
		newPkt.pktType = this.pktType;
		newPkt.senderIp = this.senderIp;
		newPkt.destIp = this.destIp;
		


		newPkt.signedData = this.signedData;
		
		newPkt.payload = this.payload;
		
		
		
		return newPkt;
	}
	
	
	


	
	
}
