package node_pkg;

import java.math.BigInteger;

public class NodeIpSign {
	public int ipAddress;
	public int hop_dist;		// from the source
	public BigInteger sign;
	
	NodeIpSign(){
		// Default Constructor
	}
	
	NodeIpSign(int ipAddress,BigInteger sign){		// Custom Constructor
		this.ipAddress = ipAddress;
		this.sign = sign;
	}
	
	public NodeIpSign getClone(){
		NodeIpSign newObj = new NodeIpSign();
		newObj.ipAddress = this.ipAddress;
		newObj.sign = this.sign;
		return newObj;
	}
}
