package com.p9.riskservice.web;

import com.p9.riskservice.dto.RiskResponse;
import com.p9.riskservice.service.RiskService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/risk")
public class RiskController {
    private final RiskService riskService;
    public RiskController(RiskService riskService) { this.riskService = riskService; }

    @GetMapping(value = "/{patientId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<RiskResponse> getRisk(@PathVariable Long patientId) {
        return riskService.evaluate(patientId);
    }
}
