// Package Name: crypt_pkg
package crypt_pkg;

//Importing libraries
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashMd5{
	
	// Returns a 32-bit Hash string of the input message 
	public static String getMd5Hash(String msg){
		MessageDigest md = null;
        byte[] hashMsg = null;
        
        BigInteger bigInt = null;
        String hashText = null;
        
		try {
			md = MessageDigest.getInstance("MD5");
			md.update(msg.getBytes());
			
			hashMsg = md.digest();					// Hash the msg using MD5
	        
	        bigInt = new BigInteger(1,hashMsg);		// convert into bigInt
	        hashText = bigInt.toString(16);			// convert into string
	        
	        // Now we need to zero pad it if you actually want the full 32 chars.
	        while(hashText.length() < 32 ){
	        	hashText = "0"+hashText;
	        }
		} catch (NoSuchAlgorithmException e) {
		}
		
		return hashText;
	}
	
	
	
	// Returns BigInteger-Hash string of the input BigInteger message 
	public static BigInteger getMd5Hash(BigInteger msg){
		MessageDigest md = null;
        byte[] hashMsg = null;
        
        BigInteger bigIntHash = null;
        
		try {
			md = MessageDigest.getInstance("MD5");
			md.update(msg.toByteArray());
			
			hashMsg = md.digest();					// Hash the msg using MD5
	        
			bigIntHash = new BigInteger(1,hashMsg);		// convert into bigInt

		} catch (NoSuchAlgorithmException e) {
		}
		
		return bigIntHash;
	}
	
	
	// Returns BigInteger-Hash string of the input int message 
	public static BigInteger getMd5Hash(int intMsg){
		String strMsg = Integer.toString(intMsg);
		BigInteger msg = new BigInteger(strMsg);
		
		MessageDigest md = null;
        byte[] hashMsg = null;
        
        BigInteger bigIntHash = null;
        
		try {
			md = MessageDigest.getInstance("MD5");
			md.update(msg.toByteArray());
			
			hashMsg = md.digest();					// Hash the msg using MD5
	        
			bigIntHash = new BigInteger(1,hashMsg);		// convert into bigInt

		} catch (NoSuchAlgorithmException e) {
		}
		
		return bigIntHash;
	}
	
	
	public static BigInteger getHashChain(int num,BigInteger input){
		if(num==0)
			return input;
		if(num==1)
			return getMd5Hash(input);
		return getHashChain(num-1,getMd5Hash(input));
		
	}
}