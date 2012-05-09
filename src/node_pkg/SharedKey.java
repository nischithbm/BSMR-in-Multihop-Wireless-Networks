package node_pkg;

import java.util.Hashtable;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;

public class SharedKey {
	
	public static Hashtable<Integer, RSAPublicKeySpec> IpRSAPubKeyTable = new Hashtable<Integer, RSAPublicKeySpec>();
	
	public static Hashtable<Integer, PublicKey> IpPubKeyTable = new Hashtable<Integer, PublicKey>();
	
	
	
	// Mapping IpAddres with RSAPublic Key - used for encryption
	public static void addEntry(int ipAddr,RSAPublicKeySpec rsaPubKey){
		
		IpRSAPubKeyTable.put(ipAddr,rsaPubKey);
	}
	public static RSAPublicKeySpec getRSAPubKey(int ipAddr){
		return IpRSAPubKeyTable.get(ipAddr);
	}
	
	
	
	// Mapping IpAddres with Public Key - used for digital signature
	public static void addEntry(int ipAddr,PublicKey pubKey){
		IpPubKeyTable.put(ipAddr,pubKey);
	}
	public static PublicKey getPubKey(int ipAddr){
		return IpPubKeyTable.get(ipAddr);
	}
}
