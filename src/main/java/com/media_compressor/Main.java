package com.media_compressor;

import static spark.Spark.post;
import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.internalServerError;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.UUID;

import javax.servlet.MultipartConfigElement;
import javax.servlet.http.Part;

public class Main {
    static final String TARGET_DIRECTORY = "/home/karthik/Desktop/compressed_files/";
    static final String[] SUPPORTED_IMAGE_FORMATS = { "image/jpeg", "image/png" };

    public static void main(String[] args) {

        port(8080);

        get("/", (request, response) -> {
            response.type("text/html");
            return "<h1>It works!</h1>";
        });

        post("/compress/image", (request, response) -> {
            request.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp"));

            final Part multipart = request.raw().getPart("file");
            final Boolean isSupportedImageFormat = Arrays.stream(SUPPORTED_IMAGE_FORMATS)
                    .anyMatch(multipart.getContentType()::equals);

            if (!isSupportedImageFormat) {
                response.type("application/json");
                response.status(415);
                return "{\"message\":\"Unsupported media type!\"}";
            }

            UUID uuid = UUID.randomUUID();

            try (InputStream inputStream = multipart.getInputStream()) {
                String filePath = TARGET_DIRECTORY + uuid, compressedFilePath = TARGET_DIRECTORY + uuid + "_compressed";
                File file = new File(filePath);
                Files.copy(inputStream, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
                ImageCompressor.compressImage(filePath, compressedFilePath, multipart.getContentType());
            } catch (Exception ex) {
                response.type("application/json");
                internalServerError("{\"message\":\"Unable to create a file!\"}");
            }

            return "{\"fileId\":" + "\"" + uuid + "\"" + "}";
        });
    }
}