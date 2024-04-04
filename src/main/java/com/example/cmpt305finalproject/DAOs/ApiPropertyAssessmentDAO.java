package com.example.cmpt305finalproject.DAOs;

import com.example.cmpt305finalproject.cmpt305.PropertyAssessments;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Long.parseLong;

public class ApiPropertyAssessmentDAO implements PropertyAssessmentDAO {
    private String nbh;
    private String endpoint; // https://data.edmonton.ca/resource/q7d6-ambg.csv

    public ApiPropertyAssessmentDAO(String endpoint) {
        this.endpoint = endpoint;
        nbh = getNeighbourhoodHeaderType();
    }

    private String formatQuery(String query) {
        if (nbh != null) {
            query = query.replace("neighbourhood_name", nbh);
        }
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

    private List<String> getHeader() {
        try {
            HttpResponse<String> response = fetchData("Select* limit 1");

            return Arrays.stream(Arrays.stream(response.body().split("\n"))
                            .map(x -> x.replace("\"", ""))
                            .toList()
                            .getFirst()
                            .split(","))
                    .toList();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return new ArrayList<String>();
        }
    }

    /**
     * check how the neighbourhood column is represented in the data
     * helps to automatically differentiate between current year and historical datasets, which have inconsistent column names
     *
     * @return the label of the neighbourhood column, or an empty string if no such column is present
     */
    private String getNeighbourhoodHeaderType() {
        List<String> header = getHeader();
        for (String column : header) {
            if (column.equals("neighbourhood_name")) {
                return "neighbourhood_name";
            }
            if (column.equals("neighbourhood")) {
                return "neighbourhood";
            }
        }
        return "";
    }

    private List<String> getFormattedDataColumn(String query) {
        try {
            HttpResponse<String> response = fetchData(query);

            return Arrays.stream(response.body().split("\n"))
                    .map(x -> x.replace("\"", ""))
                    .skip(1)
                    .sorted()
                    .filter(x -> !x.isEmpty())
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

    private Map<String, String> getYearNumericalSummary(String neighbourhood, String assessmentClass, String func) {
        String query = "select assessment_year, " + func + "(assessed_value) " +
                "where neighbourhood_name = '" + neighbourhood + "' " +
                "and mill_class_1 = '" + assessmentClass + "' " +
                "and tax_class_pct_1 = '100' " +
                "and assessed_value != 0 " +
                "group by assessment_year";
        List<String> data = getFormattedDataColumn(query);

        try {
            HttpResponse<String> response = fetchData(query);

            return Arrays.stream(response.body().split("\n"))
                    .map(x -> x.replace("\"", "").split(","))
                    .skip(1)
                    .collect(Collectors.toMap(x -> x[0],
                            x -> x[1]));

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return new HashMap<String, String>();
        }
    }

    public Map<String, Long> getYearAssessedValueSum(String neighbourhood, String assessmentClass) {
        Map<String, String> data = getYearNumericalSummary(neighbourhood, assessmentClass, "sum");
        Map<String, Long> out = new HashMap<>();

        data.forEach((key, value) -> {
            out.put(key, parseLong(value));
        });
        return out;
    }

    public Map<String, Long> getYearAssessedValueAvg(String neighbourhood, String assessmentClass) {
        Map<String, String> data = getYearNumericalSummary(neighbourhood, assessmentClass, "avg");
        Map<String, Long> out = new HashMap<>();

        data.forEach((key, value) -> {
            out.put(key, (long) Double.parseDouble(value));
        });
        return out;
    }

    public List<Long> getValuesByYear(String neighbourhood, String assessmentClass, int year) {
        int limit = 1000;
        int offset = 0;
        String query = "select assessed_value " +
                "where neighbourhood_name = '" + neighbourhood + "' " +
                "and mill_class_1 = '" + assessmentClass + "' " +
                "and tax_class_pct_1 = '100' " +
                "and assessed_value != 0 " +
                "and assessment_year = " + year +
                "order by assessed_value ASC ";

        List<Long> allData = new ArrayList<>();
        List<String> newData;


        do {
            newData = getFormattedDataColumn(query + "limit " + limit + " offset " + offset);
            newData.forEach(value -> allData.add(Long.parseLong(value)));

            offset += limit;
        } while (!newData.isEmpty());

        return allData.stream().sorted().collect(Collectors.toList());
    }

    public List<Long> getValuesByYearNoOutliers(String neighbourhood, String assessmentClass, int year) {
        return removeOutliers(getValuesByYear(neighbourhood, assessmentClass, year));
    }

    private List<Long> removeOutliers(List<Long> data) {
        long Q3 = data.get(Math.floorDiv(data.size() * 3, 4)) + data.get(Math.ceilDiv(data.size() * 3, 4)) / 2;
        long Q1 = data.get(Math.floorDiv(data.size(), 4)) + data.get(Math.ceilDiv(data.size(), 4)) / 2;
        long IQR = Q3 - Q1;

        long upperBound = (long) (Q3 + (IQR * 1.5));
        long lowerBound = (long) (Q1 - (IQR * 1.5));

        return data.stream()
                .filter(x -> x > lowerBound && x < upperBound)
                .collect(Collectors.toList());
    }

    private long getMedian(List<Long> assessmentValues) {
        int size = assessmentValues.size();
        // even
        if ((size % 2) == 0) {
            return ((assessmentValues.get((size / 2) - 1)) + assessmentValues.get(size / 2) / 2);
        }
        // odd
        else {
            return (assessmentValues.get(size / 2));
        }
    }

    public Map<String, Long> getYearAssessedValueMedian(String neighbourhood, String assessmentClass) {
        Map<String, Long> out = new HashMap<>();
        String query = "select distinct assessment_year";
        List<String> years = getFormattedDataColumn(query);

        for (String year : years) {
            List<Long> values = getValuesByYear(neighbourhood, assessmentClass, Integer.parseInt(year));
            long median = getMedian(values);
            out.put(year, median);
        }
        return out;
    }
}
