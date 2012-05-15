package packet_pkg;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;

import node_pkg.Node;
import node_pkg.NodeIpSign;

public class MRatePacket extends MultiHopPacket {

	
	public ArrayList<Integer> percRate = new ArrayList<Integer>();
	
	
	
	public MRatePacket(){
		// Default Constructor
	}
	
	
	// Custom Constructors
	public MRatePacket(PacketType pktType,int senderIp,int destIp,int hops,BigInteger hopCountAuthenticator,HopCountAnchor hopCountAnchor,NodeIpSign nodePtr,int percRate,BigInteger signedData,BigInteger payload,int TTL){
		super(pktType,senderIp,destIp,hops,hopCountAuthenticator,hopCountAnchor,nodePtr,signedData,payload,TTL);
		this.percRate.add(percRate);
	}
	
	/*
	public MRatePacket(PacketType pktType,int senderIp,int destIp,int hops,BigInteger hopCountAuthenticator,HopCountAnchor hopCountAnchor,ArrayList<NodeIpSign> nodePtr,ArrayList<Integer> percRate,BigInteger signedData,BigInteger payload,int TTL){
		super(pktType,senderIp,destIp,hops,hopCountAuthenticator,hopCountAnchor,nodePtr,signedData,payload,TTL);
		this.percRate = percRate;
	}
	*/
	
	


	
	
	
	public String toString() {
	      String s = "";
	      s += "Packet Type:" + this.pktType;
	      s += " S:" + this.senderIp;
	      s += " D:" + this.destIp;
	      s += " Hops:" + this.hops;
	      s += " MsgSeq:" + this.msgSeqNo;
		     
	      return s;
	}
	
	
	
	public MRatePacket getClone(){
		MRatePacket newPkt = new MRatePacket();
		
		newPkt.msgSeqNo = this.msgSeqNo;
		
		newPkt.pktType = this.pktType;
		newPkt.senderIp = this.senderIp;
		newPkt.destIp = this.destIp;
		newPkt.hops = this.hops;
		
		

		for(Iterator<NodeIpSign> itr=this.nodePtr.iterator();itr.hasNext();){
			newPkt.nodePtr.add(itr.next().getClone());
         }
		for(int k=0;k<this.percRate.size();k++){
			newPkt.percRate.add(this.percRate.get(k));
         }
		
		newPkt.hopCountAuthenticator =  this.hopCountAuthenticator;
		newPkt.hopCountAnchor = this.hopCountAnchor;


		
		newPkt.TTL = this.TTL;
		newPkt.signedData = this.signedData;
		
		newPkt.payload = this.payload;
		
		return newPkt;
	}
	
	
}
