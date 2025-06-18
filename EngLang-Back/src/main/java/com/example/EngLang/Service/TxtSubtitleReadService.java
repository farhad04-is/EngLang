package com.example.EngLang.Service;


import com.example.EngLang.Entity.TxtFile;
import com.example.EngLang.Repository.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TxtSubtitleReadService {

    private final FileRepository fileRepository;

    public byte[] pullFile(String path) {
        return (fileRepository.findBypath(path)
                .map(TxtFile:: getFileLength)
                .orElseThrow(() -> new RuntimeException("Veri bulunamadı.")));
    }

}
