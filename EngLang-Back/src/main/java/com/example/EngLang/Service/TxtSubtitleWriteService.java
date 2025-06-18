package com.example.EngLang.Service;

import com.example.EngLang.Mapper.FileMap;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TxtSubtitleWriteService {

    private static final Logger logger = LoggerFactory.getLogger(TxtSubtitleWriteService.class);

    private final FileMap fileMap;

    public String convertSubtitle(String videoUrl) throws IOException, InterruptedException {
        Path tempDir = null;
        String subtitleMinioFileName = null;

        try {
            tempDir = Files.createTempDirectory("yt_dlp_subtitles_");
            String outputTemplate = tempDir.toAbsolutePath().toString() + File.separator + "%(id)s.%(ext)s";
            logger.info("yt-dlp üçün müvəqqəti qovluq yaradıldı: {}", tempDir.toAbsolutePath());

            String[] command = {
                    "C:\\Users\\Farhad\\Desktop\\yt-dlp.exe", // Yolun düzgün olduğundan əmin olun
                    "--write-sub",
                    "--write-auto-sub",
                    "--sub-lang", "en",
                    "--sub-format", "vtt",
                    "--skip-download",
                    "--output", outputTemplate,
                    "--no-cache-dir",    // Önbellek istifadəsinin qarşısını alır
                    "--ignore-errors",   // Hataları görməzdən gəl, bəzi altyazıları tapa bilməzsə tamamilə çökməsin
                    videoUrl
            };

            ProcessBuilder pb = new ProcessBuilder(command);
            pb.redirectErrorStream(true);
            Process process = pb.start();
            logger.info("yt-dlp prosesi başlandı URL üçün: {}", videoUrl);

            // yt-dlp çıktısını okuyarak loglara yaz (daha ətraflı diaqnostika üçün)
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    logger.debug("yt-dlp output: {}", line); // Debug səviyyəsində ətraflı loglama
                }
            }

            int exitCode = process.waitFor();
            logger.info("yt-dlp prosesi tamamlandı. Çıxış kodu: {}", exitCode);

            if (exitCode != 0) {
                logger.warn("yt-dlp altyazı yükləməsi uğursuz oldu və ya xəta ilə başa çatdı. Çıxış kodu: {}. Logları yoxlayın. Video URL: {}", exitCode, videoUrl);
                // Burada xəta atmaq əvəzinə null qaytarmaq və ya boş bir fayl yaratmaq daha məqsədəuyğun ola bilər,
                // əgər altyazının olmaması tətbiqin tamamilə çökməsinə səbəb olmamalıdırsa.
                // Ancaq əgər altyazı vacibdirsə, xəta atmaq doğru yanaşmadır.
                throw new IOException("yt-dlp altyazı yükləməsi uğursuz oldu. Çıxış kodu: " + exitCode + ". Logları yoxlayın.");
            }

            File[] vttFiles = tempDir.toFile().listFiles((dir, name) -> name.endsWith(".en.vtt"));

            if (vttFiles != null && vttFiles.length > 0) {
                File vttFile = vttFiles[0];
                logger.info("Altyazı faylı tapıldı: {}", vttFile.getAbsolutePath());

                byte[] content = Files.readAllBytes(vttFile.toPath());
                String subtitleContentString = new String(content, java.nio.charset.StandardCharsets.UTF_8);

                // Yüklənmiş altyazının məzmununu yoxlamaq üçün loglama
                if (subtitleContentString.length() > 500) {
                    logger.debug("Yüklənmiş altyazı məzmunu (ilk 500 simvol): {}", subtitleContentString.substring(0, 500));
                } else {
                    logger.debug("Yüklənmiş altyazı məzmunu: {}", subtitleContentString);
                }

                // Altyazı məzmunu çox qısa və ya yalnız musiqi təsvirlərindən ibarətdirsə, xəbərdarlıq ver
                if (subtitleContentString.trim().length() < 50 || subtitleContentString.contains("[Music]")) {
                    logger.warn("Yüklənmiş altyazı məzmunu qısa və ya yalnız musiqi təsvirləri ehtiva edir. URL: {}", videoUrl);
                    // Burada qərar verin: boş altyazı qaytarın, yoxsa xəta atın?
                    // Məsələn, əgər boş altyazı MinIO-ya yazılmamalıdırsa:
                    // return null;
                }

                subtitleMinioFileName = "subtitles/" + UUID.randomUUID().toString() + ".vtt";
                saveFile(content, subtitleMinioFileName);
                logger.info("Altyazı MinIO-ya uğurla yazıldı. MinIO adı: {}", subtitleMinioFileName);

            } else {
                logger.warn("URL üçün İngilis VTT altyazı faylı tapılmadı: {}", videoUrl);
                throw new IOException("URL üçün İngilis VTT altyazı faylı tapılmadı: " + videoUrl);
            }
        } finally {
            if (tempDir != null && Files.exists(tempDir)) {
                try {
                    Files.walk(tempDir)
                            .sorted(Comparator.reverseOrder())
                            .map(Path::toFile)
                            .forEach(File::delete);
                    logger.info("Müvəqqəti qovluq uğurla təmizləndi: {}", tempDir);
                } catch (IOException e) {
                    logger.error("Müvəqqəti qovluğu təmizləyərkən xəta: {}. Hata: {}", tempDir, e.getMessage(), e);
                }
            }
        }
        return subtitleMinioFileName;
    }

    public void saveFile(byte[] txtFile, String minioFileName) {
        fileMap.toDto_File(txtFile, minioFileName);
    }
}