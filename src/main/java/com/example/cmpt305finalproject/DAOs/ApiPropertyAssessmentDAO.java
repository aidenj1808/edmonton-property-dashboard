package com.example.cmpt305finalproject.DAOs;

import com.example.cmpt305finalproject.cmpt305.PropertyAssessments;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ApiPropertyAssessmentDAO implements PropertyAssessmentDAO{
    private String currentYearEndpoint; // https://data.edmonton.ca/resource/q7d6-ambg.csv
    private String historicalEndpoint; // https://data.edmonton.ca/api/odata/v4/qi6a-xuwt.csv

    public ApiPropertyAssessmentDAO(String currentYearEndpoint, String historicalEndpoint) {
        this.currentYearEndpoint = currentYearEndpoint;
        this.historicalEndpoint = historicalEndpoint;
    }

    @Override
    public List<String> getNeighbourhoods() {
        String query = "select distinct Neighbourhood";

        String url = currentYearEndpoint + "?$query=" + URLEncoder.encode(query, StandardCharsets.UTF_8);
//        String url = currentYearEndpoint + "?$query=select+distinct+Neighbourhood";

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            return Arrays.stream(response.body().split("\n"))
                    .map(x -> x.replace("\"", ""))
                    .skip(1)
                    .sorted()
                    .collect(Collectors.toList());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public List<String> getAssessmentClasses() {
        return null;
    }

    @Override
    public List<PropertyAssessments> getNeighbourhoodHistoricalData(String neighbourhood) {
        return null;
    }
}
