package com.eroom.project.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.eroom.project.dto.QualityMeasureDto;

@RestController
public class SonarController {

	private static final String SONAR_API_URL =
		    "https://sonarcloud.io/api/measures/component?component=choeunsung21_eroomfork"
		    + "&metricKeys=bugs,vulnerabilities,code_smells,coverage,duplicated_lines_density,"
		    + "ncloc,comment_lines_density,complexity,cognitive_complexity,"
		    + "duplicated_blocks,duplicated_lines,lines,functions,classes,files,"
		    + "directories,statements,branch_coverage,line_coverage,uncovered_lines,"
		    + "tests,test_execution_time,skipped_tests,reliability_rating,security_rating,sqale_rating";
    private final String TOKEN = "4907d00f45379c983c1042df8a3e1d7a1b5e0b65";

    
    // 프로젝트 전체 품질 검사
    @GetMapping("/api/sonar/quality")
    public List<QualityMeasureDto> getSonarQuality() {
        List<QualityMeasureDto> result = new ArrayList<>();

        try {
            URL url = new URL(SONAR_API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "Basic " + java.util.Base64.getEncoder().encodeToString((TOKEN + ":").getBytes()));

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            in.close();

            JSONObject jsonObject = new JSONObject(response.toString());
            JSONArray measures = jsonObject.getJSONObject("component").getJSONArray("measures");

            for (int i = 0; i < measures.length(); i++) {
                JSONObject measure = measures.getJSONObject(i);
                String metric = measure.getString("metric");
                String value = measure.getString("value");
                result.add(new QualityMeasureDto(metric, value));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
    
    
    // PR별 품질 검사
    @GetMapping("/api/sonar/quality/pr/{prNumber}")
    public String getSonarQualityForPr(@PathVariable String prNumber) {
        try {
            String apiUrl = "https://sonarcloud.io/api/project_analyses/search?project=choeunsung21_eroomfork&pullRequest=" + prNumber;

            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            String encodedAuth = Base64.getEncoder().encodeToString((TOKEN + ":").getBytes());
            conn.setRequestProperty("Authorization", "Basic " + encodedAuth);

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            return response.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\":\"Failed to fetch SonarCloud PR data\"}";
        }
    }

}
