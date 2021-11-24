package snake.app.utils;

import java.math.BigInteger;
import java.util.Arrays;

public class ByteUtil {
    /**
     * Convert an integer to byte array
     * @param val
     * @return
     */
    public static byte[] intToByteArr(int val) {
        return BigInteger.valueOf(val).toByteArray();
    }

    /**
     * Convert an integer to byte array - limit to only 2 bytes
     * @param val
     * @return
     */
    public static byte[] intToByteArr2(int val)  {
        return new byte[] {
                (byte) (val >> 8),
                (byte) val
        };
    }

    /**
     * Convert byte array to integer
     * @param bytes
     * @return
     */
    public static int byteArrToInt(byte[] bytes) {
        return new BigInteger(bytes).intValue();
    }

    /**
     * Concatenate 2 byte arrays into 1 byte array
     * @param a
     * @param b
     * @return
     */
    public static byte[] concatenateByteArr(byte[] a, byte[] b) {
        byte[] combined = new byte[a.length + b.length];
        for (int i = 0; i < combined.length; ++i) {
            combined[i] = i < a.length ? a[i] : b[i - a.length];
        }
        return combined;
    }

    public static byte[] concatenateByteArr(byte a, byte[] b) {
        byte[] aArr = new byte[] {a};
        return concatenateByteArr(aArr, b);
    }

    public static byte[] concatenateByteArr(byte[] a, byte b) {
        byte[] bArr = new byte[] {b};
        return concatenateByteArr(a, bArr);
    }

    public static byte[] ipTo4Bytes(String ip) {
        if (ip.equals("localhost")) ip = "127.0.0.1";
        final int standardLength = 4;
        String[] s = ip.split("\\.");
        if (s.length != standardLength) throw new IllegalArgumentException("Invalid IP address: " + ip);
        byte[] res = new byte[standardLength];
        for (int i = 0; i < standardLength; i++) {
            int num = Integer.valueOf(s[i]);
            res[i] = (byte) num;
        }
        return res;
    }

    public static String ipBytesToString(byte[] ipBytes) {
        final int standardLength = 4;
        if (ipBytes.length != standardLength) throw new IllegalArgumentException("Invalid IP address: " + Arrays.toString(ipBytes));
        String res = "";
        for (byte b: ipBytes) {
            res += Byte.toString(b);
            res += ".";
        }
        return res.substring(0, res.length() - 1);
    }

    public static String bitsInByteStr(byte b) {
        return String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
    }

    public static String bitsInByteStr(int num) {
        return bitsInByteStr((byte) num);
    }

    public static int[] byteToIntArr(byte b) {
        char[] charArr = bitsInByteStr(b).toCharArray();
        int[] res = new int[charArr.length];
        for (int i = 0; i < charArr.length; i++) {
            res[i] = Integer.parseInt(String.valueOf(charArr[i]));
        }
        return res;
    }

    public static void main(String[] args) {
        String ip = "127.0.0.1";
        byte[] ipInB = ipTo4Bytes(ip);
        System.out.println(Arrays.toString(ipInB));

        String decodedIp = ipBytesToString(ipInB);
        System.out.println(decodedIp);

        byte[] portB = new byte[] {31, -112};
        System.out.println(byteArrToInt(portB));

        int val = 0;
        for (int shiftVal = 7; shiftVal >= 0; shiftVal--) {
            int mask = 1 << shiftVal;
            int res = val | mask;
            System.out.println(ByteUtil.bitsInByteStr(mask));
            System.out.println(ByteUtil.bitsInByteStr(res));
        }

        for (int i = -128; i <= 127; i++) {
            System.out.println(i + ": " + ByteUtil.bitsInByteStr(i));
        }

        int neg = -128;
        int[] negArr = byteToIntArr((byte) neg);
        System.out.println(Arrays.toString(negArr));

    }

}
