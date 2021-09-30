package com.restapi;


public class EncryptDecrypt {
	  
	 public String encrypt(String plainText) throws Exception {
		 	return plainText+"123123";
	        
	    }

	    public String decrypt(String encryptedText)throws Exception {
	    	
	    	return encryptedText.substring(0,encryptedText.length()-6);
	        
	    }
	
}
