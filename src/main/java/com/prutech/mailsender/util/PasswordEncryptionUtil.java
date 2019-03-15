/**
 * 
 */
package com.prutech.mailsender.util;

import java.security.MessageDigest;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.thoughtworks.xstream.core.util.Base64Encoder;


/**
 * @author venkat.sai
 *
 */
public class PasswordEncryptionUtil {

	public static String encrypt(String password, String organizationId) {
		Cipher cipher;
		String ivalue = password;
		if (password != null && password.trim().length() > 0) {
			try {
				cipher = initCipher(organizationId, Cipher.ENCRYPT_MODE);
				String encvalue = null;
				for (int i = 0; i < 2; i++) {
					encvalue = organizationId + ivalue;
					byte[] text1 = cipher.doFinal(encvalue.getBytes());
					ivalue = new Base64Encoder().encode(text1);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return ivalue;
	}

	public byte[] digest(String text, String _pki) {
		return encrypt(text, _pki).getBytes();
	}

	public static String decrypt(String password, String organizationId) {
		String ivalue = password;
		if (password != null && password.trim().length() > 0) {
			try {
				Cipher cipher = initCipher(organizationId, Cipher.DECRYPT_MODE);
				for (int i = 0; i < 2; i++) {
					byte[] dvalue = new Base64Encoder().decode(ivalue);
					byte[] idvalue;
					idvalue = cipher.doFinal(dvalue);
					ivalue = new String(idvalue).substring(organizationId.length());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return ivalue;
	}

	public static byte[] unabridged(byte[] text, byte[] _pki) {
		return decrypt(new String(text), new String(_pki)).getBytes();
	}

	public static Cipher initCipher(String organizationId, int mode) throws Exception {
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		byte[] skey = digest.digest(organizationId.getBytes());
		byte[] keyBytes = Arrays.copyOf(skey, 24);
		SecretKey key = new SecretKeySpec(keyBytes, "DESede");
		IvParameterSpec iv = new IvParameterSpec(new byte[8]);
		Cipher cipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
		cipher.init(mode, key, iv);
		return cipher;
	}

	/*
	 * public static void main(String[] args) { String password = "P1"; String
	 * organizationId = "organizationid2"; String encryptedPassword =
	 * encrypt(password, organizationId);
	 * System.out.println("PasswordEncryptionUtil.main()...encryptedPassword:" +
	 * encryptedPassword);
	 * 
	 * String decryptedPassword = decrypt(encryptedPassword, organizationId);
	 * System.out.println("PasswordEncryptionUtil.main()...decryptedPassword:" +
	 * decryptedPassword); }
	 */

}
