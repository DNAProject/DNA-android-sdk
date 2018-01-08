package com.dnawalletsdk.Cryptography;

import android.annotation.SuppressLint;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.spec.InvalidParameterSpecException;
import java.util.Arrays;

import javax.crypto.*;
import javax.crypto.spec.*;
import javax.security.auth.DestroyFailedException;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

@SuppressLint("NewApi")
public class AES {
	private static final String KEY_ALGORITHM = "AES";
	private static final String CIPHER_ALGORITHM = "AES/CBC/NoPadding";
	
	static {
		Security.addProvider(new BouncyCastleProvider());
	}
	
	public static byte[] decrypt(byte[] encryptedData, byte[] key, byte[] iv) throws IllegalBlockSizeException, BadPaddingException {
		if (key.length != 32 || iv.length != 16) {
			throw new IllegalArgumentException();
		}
		try {
			SecretKey secretKey = new SecretKeySpec(key, KEY_ALGORITHM);
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
			IvParameterSpec IV = new IvParameterSpec(iv);
			cipher.init(Cipher.DECRYPT_MODE, secretKey , IV);
			return cipher.doFinal(encryptedData);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException ex) {
			throw new RuntimeException(ex);
		}
	}
	
	public static byte[] encrypt(byte[] data, byte[] key, byte[] iv) {
		if (key.length != 32 || iv.length != 16) {
			throw new IllegalArgumentException();
		}
		try {
			SecretKey secretKey = new SecretKeySpec(key, KEY_ALGORITHM);
			Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
			IvParameterSpec IV = new IvParameterSpec(iv);
			cipher.init(Cipher.ENCRYPT_MODE, secretKey , IV);
			return cipher.doFinal(data);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException ex) {
			throw new RuntimeException(ex);
		}
	}
	
	public static byte[] generateIV() {
		byte[] iv = new byte[16];
		SecureRandom rng = new SecureRandom();
		rng.nextBytes(iv);
		return iv;
	}
	
	public static byte[] generateKey() {
		SecretKey key = null;
		try {
			KeyGenerator keyGenerator = KeyGenerator.getInstance(KEY_ALGORITHM);
			keyGenerator.init(256);
			key = keyGenerator.generateKey();
			return key.getEncoded();
		} catch (NoSuchAlgorithmException ex) {
			throw new RuntimeException(ex);
		} finally {
			if (key != null) {
				key = null;
			}
		}
	}
	
	public static byte[] generateKey(String password) {
		byte[] passwordBytes = null, passwordHash = null;
		try {
			passwordBytes = password.getBytes("UTF-8");
			passwordHash = Digest.sha256(passwordBytes);
			return Digest.sha256(passwordHash); 
		} catch (UnsupportedEncodingException ex) {
			throw new RuntimeException(ex);
		} finally {
			if (passwordBytes != null) {
				Arrays.fill(passwordBytes, (byte)0);
			}
			if (passwordHash != null) {
				Arrays.fill(passwordHash, (byte)0);
			}
		}
	}
}
