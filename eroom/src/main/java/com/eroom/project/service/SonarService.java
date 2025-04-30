package com.eroom.project.service;

import com.eroom.project.dto.QualityMeasureDto;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONObject;

@Service
public class SonarService {

    private static final String COMPONENT_KEY = "choeunsung21_eroomfork";
    private static final String TOKEN = "4907d00f45379c983c1042df8a3e1d7a1b5e0b65";

    public List<QualityMeasureDto> getProjectQualityMeasures() {
        String metrics = String.join(",", List.of(
            "bugs", "vulnerabilities", "code_smells", "coverage", "duplicated_lines_density",
            "ncloc", "comment_lines_density", "complexity", "cognitive_complexity",
            "duplicated_blocks", "duplicated_lines", "lines", "functions", "classes", "files",
            "directories", "statements", "branch_coverage", "line_coverage", "uncovered_lines",
            "tests", "test_execution_time", "skipped_tests",
            "reliability_rating", "security_rating", "sqale_rating"
        ));

        String url = String.format(
            "https://sonarcloud.io/api/measures/component?component=%s&metricKeys=%s",
            COMPONENT_KEY, metrics
        );
        return fetchAndParseMeasures(url);
    }

    public List<QualityMeasureDto> getPrQualityMeasures(String prNumber) {
        String metrics = "bugs,vulnerabilities,code_smells,coverage,duplicated_lines_density";
        String url = String.format(
            "https://sonarcloud.io/api/measures/component?component=%s&pullRequest=%s&metricKeys=%s",
            COMPONENT_KEY, prNumber, metrics
        );
        return fetchAndParseMeasures(url);
    }

    private List<QualityMeasureDto> fetchAndParseMeasures(String apiUrl) {
        List<QualityMeasureDto> result = new ArrayList<>();

        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();
            conn.setRequestMethod("GET");
            String encodedAuth = Base64.getEncoder().encodeToString((TOKEN + ":").getBytes());
            conn.setRequestProperty("Authorization", "Basic " + encodedAuth);

            if (conn.getResponseCode() != 200) return result;

            StringBuilder response = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) response.append(line);
            }

            JSONObject json = new JSONObject(response.toString());
            JSONArray measures = json.getJSONObject("component").getJSONArray("measures");

            for (int i = 0; i < measures.length(); i++) {
                JSONObject obj = measures.getJSONObject(i);
                result.add(new QualityMeasureDto(obj.getString("metric"), obj.getString("value")));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}
