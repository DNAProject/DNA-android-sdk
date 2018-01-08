package com.dnawalletsdk.Cryptography;

import java.math.BigInteger;

public class Base58 {
    //base58 encoded alphabet
    public static final String ALPHABET = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz";
    private static final BigInteger BASE = BigInteger.valueOf(ALPHABET.length());

    public static byte[] decode(String input) {
        BigInteger bi = BigInteger.ZERO;
        for (int i = input.length() - 1; i >= 0; i--) {
            int index = ALPHABET.indexOf(input.charAt(i));
            if (index == -1) {
                throw new IllegalArgumentException();
            }
            bi = bi.add(BASE.pow(input.length() - 1 - i).multiply(BigInteger.valueOf(index)));
        }
        byte[] bytes = bi.toByteArray();
        boolean stripSignByte = bytes.length > 1 && bytes[0] == 0 && bytes[1] < 0;
        int leadingZeros = 0;
        for (; leadingZeros < input.length() && input.charAt(leadingZeros) == ALPHABET.charAt(0); leadingZeros++);
        byte[] tmp = new byte[bytes.length - (stripSignByte ? 1 : 0) + leadingZeros];
        System.arraycopy(bytes, stripSignByte ? 1 : 0, tmp, leadingZeros, tmp.length - leadingZeros);
        return tmp;
    }

    public static String encode(byte[] input) {
        BigInteger value = new BigInteger(1, input);
        StringBuilder sb = new StringBuilder();
        while (value.compareTo(BASE) >= 0) {
        	BigInteger[] r = value.divideAndRemainder(BASE);
            sb.insert(0, ALPHABET.charAt(r[1].intValue()));
            value = r[0];
        }
        sb.insert(0, ALPHABET.charAt(value.intValue()));
        for (byte b : input) {
            if (b == 0) {
                sb.insert(0, ALPHABET.charAt(0));
            } else {
                break;
            }
        }
        return sb.toString();
    }
}
