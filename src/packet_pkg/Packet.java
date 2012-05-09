// Package Name: msg_pkg
package packet_pkg;

import java.math.BigInteger;

public class Packet {

	// Static Variables
	static long lastMsgSeqNo = 131234;
	
	// Header
	public PacketType pktType;
	public int senderIp;
	public int destIp;				// 0 if multicast
	public long msgSeqNo;
	
	
	public long grpSeqNo;
	
	public BigInteger signedData=null;
	
	
	
	// public int TTL;   // not finalised
	
	
	// Payload
	BigInteger payload;
	
	
	
	
	public Packet(){
		
	}
	
	public Packet(PacketType pktType,int senderIp,int destIp,BigInteger signedData,BigInteger payload){
		this.msgSeqNo = Packet.lastMsgSeqNo++;
		this.pktType = pktType; 
		this.senderIp = senderIp;
		this.destIp = destIp;
		//this.TTL = TTL;
		this.signedData=signedData;
		
		this.payload = payload;
	}


	public BigInteger getPayload(){
		return this.payload;
	}
	
	
	public BigInteger getHash(){
		
		int shift_bits = 1028 * 1028;
		
		int intSum = 0;
		BigInteger bigIntSum = BigInteger.valueOf(0);
		
		Long longSum;

		longSum = shift_bits * this.msgSeqNo;
		intSum += shift_bits << this.senderIp;
		intSum += shift_bits<< this.destIp;
		
		bigIntSum = bigIntSum.add(BigInteger.valueOf(longSum));
		bigIntSum = bigIntSum.add(BigInteger.valueOf(intSum));
		
		return bigIntSum;
	}
	
	
}
