package com.eroom.project.controller;

import com.eroom.project.dto.QualityMeasureDto;
import com.eroom.project.service.SonarService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sonar/quality")
public class SonarController {

    private final SonarService sonarService;

    public SonarController(SonarService sonarService) {
        this.sonarService = sonarService;
    }

    @GetMapping
    public List<QualityMeasureDto> getProjectQuality() {
        return sonarService.getProjectQualityMeasures();
    }

    @GetMapping("/pr/{prNumber}")
    public List<QualityMeasureDto> getPrQuality(@PathVariable("prNumber") String prNumber) {
        return sonarService.getPrQualityMeasures(prNumber);
    }
}
