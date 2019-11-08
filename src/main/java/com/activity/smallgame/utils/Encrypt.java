package com.activity.smallgame.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Encrypt {

    /**
     * SHA-256 加密
     * @param strText
     * @return
     */
    public static String getSHA256(final String strText) {
        return SHA(strText, "SHA-256");
    }

    /**
     * SHA-512 加密
     * @param strText
     * @return
     */
    public static String getSHA512(final String strText) {
        return SHA(strText, "SHA-512");
    }

    /**
     * MD5 加密
     * @param strText
     * @return
     */
    public static String getMD5(final String strText) {
        return SHA(strText, "MD5");
    }

    /**
     * 字符串 SHA 加密
     * @param strText
     * @param strType
     * @return
     */
    private static String SHA(final String strText, final String strType) {
        // 返回值
        String strResult = null;
        if (strText != null && strText.length() > 0) {
            try {
                // SHA 加密开始
                // 创建加密对象，传入加密类型
                MessageDigest messageDigest = MessageDigest.getInstance(strType);
                // 传入要加密的字符串
                messageDigest.update(strText.getBytes());
                // 得到 byte 数组
                byte[] byteBuffer = messageDigest.digest();
                // 将 byte 数组转成 String 类型
                StringBuffer strHexString = new StringBuffer();
                // 遍历 byte 数组
                for (byte b : byteBuffer) {
                    // 转成16进制并存储到字符串变量中
                    String hex = Integer.toHexString(0xff & b);
                    if (hex.length() == 1) {
                        strHexString.append('0');
                    }
                    strHexString.append(hex);
                }
                // 得到返回结果
                strResult = strHexString.toString();
            }catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
        return strResult;
    }
}
