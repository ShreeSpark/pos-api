package com.shreespark.pos_api.barcode.service;

import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.shreespark.pos_api.common.enums.BarcodeFormat;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
public class BarcodeGeneratorService {

    private static final int BARCODE_WIDTH  = 300;
    private static final int BARCODE_HEIGHT = 150;
    private static final int QR_SIZE        = 300;
    private static final int PADDING        = 20;
    private static final int COLS           = 3;

    public String generate(String value, BarcodeFormat format) {
        byte[] png = generatePng(value, format);
        return Base64.getEncoder().encodeToString(png);
    }

    public byte[] generatePng(String value, BarcodeFormat format) {
        try {
            com.google.zxing.BarcodeFormat zxingFormat = switch (format) {
                case QR_CODE  -> com.google.zxing.BarcodeFormat.QR_CODE;
                case CODE_128 -> com.google.zxing.BarcodeFormat.CODE_128;
                case EAN_13   -> com.google.zxing.BarcodeFormat.EAN_13;
            };

            int width  = format == BarcodeFormat.QR_CODE ? QR_SIZE : BARCODE_WIDTH;
            int height = format == BarcodeFormat.QR_CODE ? QR_SIZE : BARCODE_HEIGHT;

            Map<EncodeHintType, Object> hints = Map.of(EncodeHintType.MARGIN, 1);
            BitMatrix matrix = new MultiFormatWriter().encode(value, zxingFormat, width, height, hints);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(matrix, "PNG", out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Barcode generation failed: " + e.getMessage());
        }
    }

    // Composes multiple base64 barcode images into a single printable PNG sheet
    public byte[] generateSheet(List<String> base64Images, List<String> labels) {
        try {
            int count  = base64Images.size();
            int rows   = (int) Math.ceil((double) count / COLS);
            int cellW  = BARCODE_WIDTH  + PADDING * 2;
            int cellH  = BARCODE_HEIGHT + PADDING * 2 + 20; // 20px for label text
            int sheetW = cellW * COLS;
            int sheetH = cellH * rows;

            BufferedImage sheet = new BufferedImage(sheetW, sheetH, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = sheet.createGraphics();
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, sheetW, sheetH);
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.PLAIN, 11));

            for (int i = 0; i < count; i++) {
                int col = i % COLS;
                int row = i / COLS;
                int x   = col * cellW + PADDING;
                int y   = row * cellH + PADDING;

                byte[] imgBytes = Base64.getDecoder().decode(base64Images.get(i));
                BufferedImage barcodeImg = ImageIO.read(new ByteArrayInputStream(imgBytes));
                g.drawImage(barcodeImg, x, y, BARCODE_WIDTH, BARCODE_HEIGHT, null);

                if (labels != null && i < labels.size()) {
                    g.drawString(labels.get(i), x, y + BARCODE_HEIGHT + 14);
                }
            }

            g.dispose();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(sheet, "PNG", out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Sheet generation failed: " + e.getMessage());
        }
    }
}
