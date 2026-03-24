package com.hector.encurtadorlink.dto.response;

import java.time.LocalDate;

public record DailyClicksResponse (Integer clicksPerDay, LocalDate dayOfClick){
}
