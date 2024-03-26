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
    private String endpoint; // https://data.edmonton.ca/resource/q7d6-ambg.csv
//    private String historicalEndpoint; // https://data.edmonton.ca/resource/qi6a-xuwt.csv

    public ApiPropertyAssessmentDAO(String endpoint) {
        this.endpoint = endpoint;
    }

    private String formatQuery(String query) {
        return endpoint + "?$query=" + URLEncoder.encode(query, StandardCharsets.UTF_8);
    }

    private HttpResponse<String> fetchData(String query) throws IOException, InterruptedException {
        String url = formatQuery(query);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private List<String> getFormattedDataColumn(String query) {
        try {
            HttpResponse<String> response = fetchData(query);

            return Arrays.stream(response.body().split("\n"))
                    .map(x -> x.replace("\"", ""))
                    .skip(1)
                    .sorted()
                    .collect(Collectors.toList());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return List.of("Error: unable to fetch data");
        }
    }

    @Override
    public List<String> getNeighbourhoods() {
        return getFormattedDataColumn("select distinct neighbourhood_name");
    }

    @Override
    public List<String> getAssessmentClasses() {
        return getFormattedDataColumn("select distinct mill_class_1");
    }

    @Override
    public List<PropertyAssessments> getNeighbourhoodData(String neighbourhood) {
        return null;
    }

    public List<String> getYearAssessedValueSum(String neighbourhood, String assessmentClass) {
        return getFormattedDataColumn("select assessment_year, sum(assessed_value) " +
                //"limit = 50000 " +
                "where neighbourhood_name = '" + neighbourhood + "' " +
                "and mill_class_1 = '" + assessmentClass + "' " +
                "and tax_class_pct_1 = '100' " +
                "and assessed_value != 0 " +
                "group by assessment_year");
    }

    public List<String> getYearAssessedValueAvg(String neighbourhood, String assessmentClass) {
        return getFormattedDataColumn("select assessment_year, avg(assessed_value) " +
                //"limit = 50000 " +
                "where neighbourhood_name = '" + neighbourhood + "' " +
                "and mill_class_1 = '" + assessmentClass + "' " +
                "and tax_class_pct_1 = '100' " +
                "and assessed_value != 0 " +
                "group by assessment_year");
    }
}
