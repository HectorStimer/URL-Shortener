package com.hector.encurtadorlink.service;

import com.hector.encurtadorlink.dto.request.CreateUrlRequest;
import com.hector.encurtadorlink.dto.response.UrlResponse;
import com.hector.encurtadorlink.exception.UrlExpiredException;
import com.hector.encurtadorlink.exception.UrlNotFoundException;
import com.hector.encurtadorlink.model.Url;
import com.hector.encurtadorlink.repository.UrlRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Transactional
@Service
public class UrlService {
    private final static Logger logger =
            LoggerFactory.getLogger(UrlService.class);


    private final UrlRepository urlRepository;
    private final ShortCodeGenerator shortCodeGenerator;

    public UrlService(UrlRepository urlRepository, ShortCodeGenerator shortCodeGenerator){
        this.urlRepository=urlRepository;
        this.shortCodeGenerator=shortCodeGenerator;
    }


    public UrlResponse save(CreateUrlRequest dto) {
        String shortCode = shortCodeGenerator.generate();
        Url url = new Url(dto.originalUrl(), shortCode, dto.expiresAt());

        Url savedUrl = urlRepository.save(url);

        return new UrlResponse(
                savedUrl.getOriginalUrl(),
                savedUrl.getShortCode(),
                "https://dominio.com/" + savedUrl.getShortCode(),
                savedUrl.getCreatedAt(),
                savedUrl.getExpiresAt()
        );

    }

    public Url findByShortCode(String shortCode){
        Url url = urlRepository.findByShortCode(shortCode)
                .orElseThrow(()-> new UrlNotFoundException("url nao encontrada"));


        if (url.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new UrlExpiredException("url expirou");
        }

        return url;
    }

    public void delete(Long id){
        logger.info("deleting url");
        urlRepository.deleteById(id);
    }



}
