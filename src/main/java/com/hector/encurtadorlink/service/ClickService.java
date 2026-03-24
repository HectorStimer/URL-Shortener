package com.hector.encurtadorlink.service;


import com.hector.encurtadorlink.dto.response.DailyClicksResponse;
import com.hector.encurtadorlink.dto.response.StatsResponse;
import com.hector.encurtadorlink.dto.response.UrlResponse;
import com.hector.encurtadorlink.model.Click;
import com.hector.encurtadorlink.model.Url;
import com.hector.encurtadorlink.repository.ClickReposirory;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
public class ClickService {
    private final static Logger logger =
            LoggerFactory.getLogger(ClickService.class);

    private final ClickReposirory reposirory;

    public ClickService(ClickReposirory reposirory){
        this.reposirory=reposirory;

    }

    @Async
    public void registerClick(Url url, String ipAddress, String userAgent, String referer) {
        logger.info("registering click for url: {}", url.getShortCode());
        Click click = new Click(url, ipAddress, userAgent, referer);
        reposirory.save(click);
    }




    public StatsResponse getStats(Url url) {
        List<Click> clicks = reposirory.findByUrl(url);

        int totalClicks = clicks.size();

        List<DailyClicksResponse> clicksPerDay = clicks.stream()
                .collect(Collectors.groupingBy(
                        click -> click.getClickedAt().toLocalDate(),
                        Collectors.collectingAndThen(Collectors.counting(), Long::intValue)
                ))
                .entrySet().stream()
                .map(entry -> new DailyClicksResponse(entry.getValue(), entry.getKey()))
                .toList();

        UrlResponse urlResponse = new UrlResponse(
                url.getOriginalUrl(),
                url.getShortCode(),
                "https://dominio.com/" + url.getShortCode(),
                url.getCreatedAt(),
                url.getExpiresAt()
        );

        return new StatsResponse(urlResponse, totalClicks, clicksPerDay);
    }
}
