package packet_pkg;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;

import crypt_pkg.HashMd5;

import node_pkg.Node;
import node_pkg.NodeIpSign;
import node_pkg.SharedKey;

public class RReqPacket extends MultiHopPacket {

	
	
	public RReqPacket(){
		// Default Constructor
	}
	
	
	// Custom Constructors
	public RReqPacket(PacketType pktType,int senderIp,int destIp,int hops,BigInteger hopCountAuthenticator,HopCountAnchor hopCountAnchor,NodeIpSign nodePtr,BigInteger signedData,BigInteger payload,int TTL){
		super(pktType,senderIp,destIp,hops,hopCountAuthenticator,hopCountAnchor,nodePtr,signedData,payload,TTL);
	}
	
	public RReqPacket(PacketType pktType,int senderIp,int destIp,int hops,BigInteger hopCountAuthenticator,HopCountAnchor hopCountAnchor,ArrayList<NodeIpSign> nodePtr,BigInteger signedData,BigInteger payload,int TTL){
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
	
	
	
	public RReqPacket getClone(){
		RReqPacket newPkt = new RReqPacket();
		
		newPkt.msgSeqNo = this.msgSeqNo;
		
		newPkt.pktType = this.pktType;
		newPkt.senderIp = this.senderIp;
		newPkt.destIp = this.destIp;
		newPkt.hops = this.hops;
		
		
		for(Iterator<NodeIpSign> itr=this.nodePtr.iterator();itr.hasNext();){
			newPkt.nodePtr.add(itr.next().getClone());
         }
		//newPkt.nodePtr = this.nodePtr.clone();
		
		newPkt.hopCountAuthenticator =  this.hopCountAuthenticator;
		newPkt.hopCountAnchor = this.hopCountAnchor;
		
		newPkt.TTL = this.TTL;
		newPkt.signedData = this.signedData;
		
		newPkt.payload = this.payload;
		
		
		
		return newPkt;
	}
	
	
	/*
	public BigInteger getHash(){
		
		int shift_bits = 1028 * 1028;
		
		int intSum = 0;
		BigInteger bigIntSum = BigInteger.valueOf(0);
		
		Long longSum;
		
		
		longSum = shift_bits * this.msgSeqNo;
		intSum += shift_bits << this.senderIp;
		intSum += shift_bits<< this.destIp;
		
		for(Iterator<NodeIpSign> itr=this.nodePtr.iterator();itr.hasNext();){
			NodeIpSign tmp = itr.next();
			intSum += shift_bits << tmp.ipAddress;
			bigIntSum = bigIntSum.add(tmp.sign);
		}

		intSum += shift_bits << this.hops;
		
		bigIntSum = bigIntSum.add(this.hopCountAnchor.hcaValue);
		bigIntSum = bigIntSum.add(this.hopCountAnchor.hcaSign);
		bigIntSum = bigIntSum.add(this.hopCountAuthenticator);



		bigIntSum = bigIntSum.add(BigInteger.valueOf(longSum));
		bigIntSum = bigIntSum.add(BigInteger.valueOf(intSum));
		



		return bigIntSum;
		
		
		
	}
	
	
	*/
	
	
	


	
}
