package com.blackmesaresearch.hytrac.service;

import com.blackmesaresearch.hytrac.parser.DocumentParserFactory;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.Base64;
import java.util.EnumMap;
import java.util.Map;

@Service
public class ScanService {

    private final DocumentParserFactory parserFactory;

    // Injecting the Factory via constructor
    public ScanService(DocumentParserFactory parserFactory) {
        this.parserFactory = parserFactory;
    }

    public String decodePdf417(String base64Image) throws Exception {
        if (base64Image.contains(",")) {
            base64Image = base64Image.split(",")[1];
        }

        byte[] imageBytes = Base64.getDecoder().decode(base64Image);
        BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(imageBytes));
        
        if (originalImage == null) {
            throw new IllegalArgumentException("Invalid image data.");
        }

        // Image processing optimizations
        int newWidth = originalImage.getWidth() * 2;
        int newHeight = originalImage.getHeight() * 2;
        BufferedImage processedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g2d = processedImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
        g2d.dispose();

        Map<DecodeHintType, Object> hints = new EnumMap<>(DecodeHintType.class);
        hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
        hints.put(DecodeHintType.POSSIBLE_FORMATS, java.util.Collections.singletonList(BarcodeFormat.PDF_417));

        try {
            LuminanceSource source = new BufferedImageLuminanceSource(processedImage);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
            Result result = new MultiFormatReader().decode(bitmap, hints);
            return result.getText();
        } catch (NotFoundException e) {
            throw new Exception("Barcode could not be read or detected from the source.");
        }
    }

    public Map<String, Object> parsePayload(String rawText, String documentType) {
        // Automatically dynamically handles routing to the correct parser instance
        return parserFactory.getParser(documentType).parse(rawText);
    }
}
