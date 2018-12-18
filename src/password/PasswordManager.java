package password;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class PasswordManager {
	public String strongPasswordHash(String password) throws NoSuchAlgorithmException, InvalidKeySpecException{
		int iterations = 9999;
		char[] passwordArray = password.toCharArray();
		byte[] salt = getSalt();
		byte[] salt1 = new byte[8];
		byte[] salt2 = new byte[8];
		for(int i = 0; i<salt.length/2; i++){
			salt1[i] = salt[i];
		}
		for(int i = 8; i<salt.length; i++){
			salt2[i-8] = salt[i];
		}
		String salt1String = toHex(salt1);
		String salt2String = toHex(salt2);
		PBEKeySpec spec = new PBEKeySpec(passwordArray, salt, iterations, 64*8);
		SecretKeyFactory secret = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
		byte[] hash = secret.generateSecret(spec).getEncoded();
		return iterations+salt1String+toHex(hash)+salt2String;
	}
	private byte[] getSalt(){
		SecureRandom secure = null;
		try {
			secure = SecureRandom.getInstance("SHA1PRNG");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		byte[] salt = new byte[16];
		secure.nextBytes(salt);
		return salt;
	}
	private String toHex(byte[] array) throws NoSuchAlgorithmException{
		BigInteger bi = new BigInteger(1, array);
		String hex = bi.toString(16);
		int paddingLength = (array.length*2)-hex.length();
		if(paddingLength>0){
			return String.format("%0"+paddingLength+"d", 0)+hex;
		} else {
			return hex;
		}
	}
	public boolean validatePassword(String originalPassword, String storedPassword) throws NoSuchAlgorithmException, InvalidKeySpecException{
		String iteration = storedPassword.substring(0, 4);
		int iterations = Integer.parseInt(iteration);
		String salt1 = storedPassword.substring(4, 20);
		String salt2 = storedPassword.substring(storedPassword.length()-16, storedPassword.length());
		String hashString = storedPassword.substring(20, storedPassword.length()-16);
		byte[] salt = fromHex(salt1+salt2);
		byte[] hash = fromHex(hashString);
		
		PBEKeySpec spec = new PBEKeySpec(originalPassword.toCharArray(), salt, iterations, hash.length*8);
		SecretKeyFactory secret = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
		byte[] testHash = secret.generateSecret(spec).getEncoded();
		
		int diff = hash.length^testHash.length;
		for(int i = 0; i<hash.length && i<testHash.length; i++){
			diff|=hash[i]^testHash[i];
		}
		return diff==0;
	}
	private byte[] fromHex(String hex) throws NoSuchAlgorithmException{
		byte[] bytes = new byte[hex.length()/2];
		for(int i = 0; i<bytes.length; i++){
			bytes[i]= (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
		}
		return bytes;
	}
}
