package com.geekq.miaosha.utils;


import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.gson.*;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;
/**
 * @author zzh
 * @version 1.0.0
 * @ClassName TokenUtils.java
 * @Description TODO
 * @createTime 2022年03月22日 15:25:00
 */
public class TokenUtils {

    private static Key aesKey = getAESKey();
    private static String hmacKey = "wSpJSBADgHgGIjdA9jvLzQ==";

    private static class Status {
        private int code;
        private String message;

        public Status(int code, String message) {
            this.code = code;
            this.message = message;
        }

        @Override
        public String toString() {
            return "Status{" +
                    "code=" + code +
                    ", message='" + message + '\'' +
                    '}';
        }
    }

    /**
     * 校验token，主要是校验两个方面
     * 1、校验签名是否合法
     * 2、校验token是否过期
     * @param token
     * @return
     */
    public static Status verify(String token) {
        List<String> list = Splitter.on('.').splitToList(token);
        if (list.size() != 3) {
            return new Status(5000, "token length is not valid");
        }
        String encryptJson = list.get(1);
        String sign = list.get(2);
        String headerJson = list.get(0);
        try {
            if (!verifySign(encryptJson, sign)) {
                return new Status(5001, "invalid token");
            }

            if (!verifyExpire(headerJson)) {
                return new Status(5002, "token has time out");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Status(200, "success");
    }

    /**
     * 校验token是否过期
     * @param headerJson  请求头的base64编码
     * @return
     */
    private static boolean verifyExpire(String headerJson) {
        byte[] headerBytes = base64Decoder(headerJson);
        String json = new String(headerBytes);
        JsonObject jsonObject = (JsonObject)new JsonParser().parse(json);
        JsonElement element = jsonObject.get("expire");
        long expireTime = element.getAsLong();
        return expireTime > System.currentTimeMillis() / 1000;
    }

    /**
     *校验签名  使用base64解码后的字节数组来生成签名，并与传递来的签名进行比较得出合法性
     * @param encryptJson
     * @param sign
     * @return
     * @throws Exception
     */
    private static boolean verifySign(String encryptJson, String sign) throws Exception{
        byte[] encryptBody = base64Decoder(encryptJson);
        byte[] bytes = generateHmacSign(encryptBody);
        return StringUtils.equals(sign, base64Encoder(bytes));
    }

    private static byte[] AESDecryptBody(byte[] encryptBody) throws Exception{
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, aesKey);
        return cipher.doFinal(encryptBody);
    }

    /**
     *
     * @param tokenBody 实例对象，通常为bean
     * @param minute 过期时间   单位:min
     * @param <T>
     * @return
     */
    public static <T> String createToken(T tokenBody, int minute) {
        long now = System.currentTimeMillis() / 1000;
        Gson gson = new Gson();
        JsonObject jsonBody = new JsonObject();
        jsonBody.addProperty("body", gson.toJson(tokenBody));

        String randomAlphabetic = RandomStringUtils.randomAlphabetic(3);
        JsonObject jsonHeader = new JsonObject();
        jsonHeader.addProperty("now", now);
        jsonHeader.addProperty("rand_num", randomAlphabetic);
        jsonHeader.addProperty("expire", (now + minute * 60));

        String token = null;
        try {
            byte[] encryptContent = generateEncryptBody(jsonHeader.toString(), jsonHeader.toString());
            byte[] signWithEncrypt = generateSignWithEncrypt(encryptContent);
            token = Joiner.on(".").join(new String[]{base64Encoder(
                    jsonHeader.toString().getBytes("utf-8")),
                    base64Encoder(encryptContent),
                    base64Encoder(signWithEncrypt)}
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        return token;
    }

    private static byte[] generateEncryptBody(String header, String body) throws Exception{
        byte[] headerBytes = header.getBytes("utf-8");
        byte[] bodyBytes = body.getBytes("utf-8");
        byte[] content = xor(headerBytes, bodyBytes);
        byte[] encryptContent = AESEncrypt(content);
        return encryptContent;
    }

    private static byte[] generateSignWithEncrypt(byte[] encryptContent) throws Exception{
        byte[] result = generateHmacSign(encryptContent);
        return result;
    }

    private static byte[] generateHmacSign(byte[] content) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(hmacKey.getBytes("utf-8"), "HmacMD5");
        Mac mac = Mac.getInstance("HmacMD5");
        mac.init(secretKey);
        return mac.doFinal(content);
    }

    private static String base64Encoder(byte[] data) {
        return Base64.encodeBase64String(data);
    }

    private static byte[] base64Decoder(String content) {
        return Base64.decodeBase64(content);
    }

    private static byte[] xor(byte[] header, byte[] body) {
        byte[] xorData = new byte[body.length];
        for (int i = 0; i < body.length; i ++) {
            int idx = i % header.length;
            xorData[i] = (byte)(body[i] ^ header[idx]);
        }
        return xorData;
    }

    private static Key getAESKey() {
        Key key = null;
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(new SecureRandom());
            byte[] encodedKey = keyGenerator.generateKey().getEncoded();
            key = new SecretKeySpec(encodedKey, "AES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return key;
    }

    private static byte[] AESEncrypt(byte[] content) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, aesKey);
        return cipher.doFinal(content);
    }

    private static byte[] AESDecrypt(byte[] content) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, aesKey);
        return cipher.doFinal(content);
    }
    static class Student {
        int id;
        String name;

        public Student(int id, String name) {
            this.id = id;
            this.name = name;
        }
    }
//
//    public static void main(String[] args) throws InterruptedException {
//        String token = createToken(new Student(1, "tom"), 1);
//        System.out.println(token);
//        Status status = verify(token);
//        System.out.println(status);
//        System.out.println(verify(token + "ab"));
//        Thread.sleep(70000);
//        System.out.println(verify(token));
//    }
}

