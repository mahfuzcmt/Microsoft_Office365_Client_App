package hello;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class QRCodeGenerator {

    public static void generateQRCode(String text, String filePath, int width, int height) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();

        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height, hints);

        Path path = FileSystems.getDefault().getPath(filePath);
        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);
        System.out.println("✅ QR Code generated at: " + filePath);
    }

    public static void main(String[] args) {
        String websiteURL = "https://www.facebook.com/sodaionline247";
        String outputFilePath = "sodaionline_fb_qr.png";
        int width = 300;
        int height = 300;

        try {
            generateQRCode(websiteURL, outputFilePath, width, height);
        } catch (WriterException | IOException e) {
            System.err.println("❌ Could not generate QR Code: " + e.getMessage());
        }
    }
}

