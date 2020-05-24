package com.throne.seckilling.uitils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * 用于生成验证码的工具类
 * com.throne.seckilling.uitils
 * Created by throne on 2020/5/24
 */
public class VerifyCodeUtil {
    private static int width = 90;   // 图片宽度
    private static int height = 30;  // 图片高度
    private static int codeLength = 4; // 字符个数
    private static int vSpace = 15; // 字符水平间距
    private static int hSpace = 15; // 字符垂直间距
    private static int fontSize = 18; // 字体大小
    private static int lineCount = 15; // 干扰线数量
    private static int lineMaxLength = 12; // 干扰线最大长度
    private static char[] codeChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890".toCharArray();

    public static Map<String, Object> generateVerifyCode() {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics graphics = img.getGraphics();

        // 画背景
        graphics.setColor(Color.black);
        graphics.fillRect(0, 0, width, height);

        // 画边框
        graphics.setColor(Color.white);
        graphics.fillRect(0, 0, width - 1, height - 1);


        Font font = new Font("Fixedsys", Font.BOLD, fontSize);
        graphics.setFont(font);

        Random random = new Random();
        // 画干扰线
        graphics.setColor(Color.blue);
        for (int i = 0; i < lineCount; i++) {
            int x1 = random.nextInt(width);
            int y1 = random.nextInt(height);
            int x2 = random.nextInt(lineMaxLength);
            int y2 = random.nextInt(lineMaxLength);
            graphics.drawLine(x1, y1, x2, y2);
        }

        int red = 0, green = 0, blue = 0;
        // 生成随机验证码并绘制进图层
        StringBuffer codes = new StringBuffer();
        for (int i = 0; i < codeLength; i++) {
            char codeChar = codeChars[random.nextInt(36)];
            String code = String.valueOf(codeChar);

            red = random.nextInt(255);
            green = random.nextInt(255);
            blue = random.nextInt(255);
            graphics.setColor(new Color(red, green, blue));
            graphics.drawString(code, (i+1) * hSpace, vSpace);

            codes.append(code);
        }
        Map<String, Object> map = new HashMap<>();
        map.put("codeImg", img);
        map.put("codes", codes.toString());
        return map;

    }
}
