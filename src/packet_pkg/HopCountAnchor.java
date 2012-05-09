package packet_pkg;

import java.math.BigInteger;

public class HopCountAnchor {
	public BigInteger hcaValue;
	public BigInteger hcaSign;
	
	public HopCountAnchor(){
		// default constructor
	}
	
	public HopCountAnchor(BigInteger hcaValue,BigInteger hcaSign){
		this.hcaValue = hcaValue;
		this.hcaSign = hcaSign;		
	}
}
