package com.hector.encurtadorlink.dto.response;

import java.util.List;

public record StatsResponse(UrlResponse url,
                            int totalClicks,
                            List<DailyClicksResponse> clicksPerDay){
}
