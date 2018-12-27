package com.mywebsite.configuration.beans;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.*;
import java.util.Arrays;

/**
 * Helper bean for encrypting and decrypting sensitive Strings.
 *
 * Based on: https://stackoverflow.com/a/22445878/696372
 */
public class EncryptionHelper {

    private final String password;

    public EncryptionHelper(String password) {
        this.password = password;
    }

    /**
     * Encrypts the given text.
     *
     * Result is a string where the first 16 characters is the IV used to encrypt it, and the rest is the encrypted String.
     *
     * @param text
     * @return
     */
    public String encrypt(String text) {
        if(text == null) {
            return null;
        }
        try {
            //Generate 16-character IV
            SecureRandom random = new SecureRandom();
            String iv = new BigInteger(130, random).toString(32).substring(0, 16);

            //initalise Cipher
            Cipher cipher = getCipher(iv, this.password, true);

            //encrypt
            byte[] encrypted = cipher.doFinal(text.getBytes());
            String result = Base64.encodeBase64String(encrypted);
            return iv+result;
        } catch (Exception e) {
            throw new RuntimeException("Should never happen", e);
        }

    }

    /**
     * Decrypts strings that have been encrypted with the encrypt method of this class
     *
     */
    public String decrypt(String input) {
        if(input == null) {
            return null;
        }
        try {
            //extract IV
            String iv = input.substring(0, 16);
            String encrypted = input.substring(16);

            //initalise Cipher
            Cipher cipher = getCipher(iv, this.password, false);

            //decrypt
            byte[] original = cipher.doFinal(Base64.decodeBase64(encrypted));
            return new String(original);
        } catch(Exception e) {
            throw new RuntimeException("Should never happen", e);
        }
    }

    private static Cipher getCipher(String iv, String password, boolean encrypt) throws UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeyException {
        byte[] key = passwordTo128BitKey(password);
        SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
        //NOTE: cipher is not thread-safe! So creating new instance every time
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        IvParameterSpec ivForEncrypt = new IvParameterSpec(iv.getBytes("UTF-8"));
        cipher.init(encrypt ? Cipher.ENCRYPT_MODE : Cipher.DECRYPT_MODE, skeySpec, ivForEncrypt);
        return cipher;
    }

    private static byte[] passwordTo128BitKey(String password) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest digester = MessageDigest.getInstance("SHA-256");
        digester.update(String.valueOf(password).getBytes("UTF-8"));
        byte[] result = digester.digest();
        return Arrays.copyOf(result, 16);
    }
}