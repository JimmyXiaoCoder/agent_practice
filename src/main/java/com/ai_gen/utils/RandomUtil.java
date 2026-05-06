package com.ai_gen.utils;

import java.security.SecureRandom;

/**
 * 随机数/随机字符串工具类
 */
public class RandomUtil {

    // 安全随机数生成器（相比 Random 更适合生成密钥等敏感场景）
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    // 字母表（大小写）
    private static final String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    // 数字表
    private static final String DIGITS = "0123456789";

    /**
     * 随机生成密钥：6位大小写字母 + 8位数字
     * 总长度：14位
     *
     * @return 生成的随机密钥字符串
     */
    public static String generateRandomKey() {
        StringBuilder sb = new StringBuilder(14);

        // 生成6位字母
        for (int i = 0; i < 6; i++) {
            sb.append(LETTERS.charAt(SECURE_RANDOM.nextInt(LETTERS.length())));
        }

        // 生成8位数字
        for (int i = 0; i < 8; i++) {
            sb.append(DIGITS.charAt(SECURE_RANDOM.nextInt(DIGITS.length())));
        }

        return sb.toString();
    }

    // ========== 测试入口 ==========
    public static void main(String[] args) {
        System.out.println("生成5个随机密钥示例：");
        for (int i = 0; i < 5; i++) {
            String key = generateRandomKey();
            System.out.println("Key " + (i + 1) + ": " + key + " (长度: " + key.length() + ")");
        }
    }
}