package com.media_compressor;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

public class ImageCompressor {

    public static void compressImage(String inputPath, String outputPath, String MIMEType) throws IOException {
        File inputFile = new File(inputPath);
        BufferedImage inputImage = ImageIO.read(inputFile);

        Iterator<ImageWriter> writers = ImageIO.getImageWritersByMIMEType(MIMEType);
        ImageWriter writer = writers.next();

        File outputFile = new File(outputPath);
        ImageOutputStream outputStream = ImageIO.createImageOutputStream(outputFile);
        writer.setOutput(outputStream);

        ImageWriteParam params = writer.getDefaultWriteParam();
        params.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        params.setCompressionQuality(0.6f);

        writer.write(null, new javax.imageio.IIOImage(inputImage, null, null), params);

        outputStream.close();
        writer.dispose();
    }

}