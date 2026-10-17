package com.example.cmpt305finalproject.DAOs;

import com.example.cmpt305finalproject.cmpt305.TableViewEntry;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.io.IOException;
import java.io.UncheckedIOException;
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

    /**
     * Sends a SoQL query to this DAO's dataset.
     *
     * @param query the SoQL query to run
     * @return the API's response, with the results as CSV in its body
     * @throws UncheckedIOException if the API can't be reached
     * @throws IllegalStateException if the request is interrupted or the API returns an error status
     */
    private HttpResponse<String> fetchData(String query) {
        String url = formatQuery(query);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new UncheckedIOException("Couldn't reach the Edmonton Open Data API at " + endpoint, e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while waiting for the Edmonton Open Data API", e);
        }

        if (response.statusCode() != 200) {
            throw new IllegalStateException("The Edmonton Open Data API returned HTTP "
                    + response.statusCode() + " for the query: " + query);
        }
        return response;
    }

    /**
     * Fetches the dataset's column names.
     *
     * @return the column names, in the order the API returns them
     */
    private List<String> getHeader() {
        HttpResponse<String> response = fetchData("Select* limit 1");

        return Arrays.stream(Arrays.stream(response.body().split("\n"))
                        .map(x -> x.replace("\"", ""))
                        .toList()
                        .getFirst()
                        .split(","))
                .toList();
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

    /**
     * Runs a query and returns its result rows as sorted CSV lines, without the header or quotes.
     *
     * @param query the SoQL query to run
     * @return one string per non-empty result row
     */
    private List<String> getFormattedDataColumn(String query) {
        HttpResponse<String> response = fetchData(query);

        return Arrays.stream(response.body().split("\n"))
                .map(x -> x.replace("\"", ""))
                .skip(1)
                .sorted()
                .filter(x -> !x.isEmpty())
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getNeighbourhoods() {
        return getFormattedDataColumn("select distinct neighbourhood_name");
    }

    @Override
    public List<String> getAssessmentClasses() {
        return getFormattedDataColumn("select distinct mill_class_1");
    }

    /**
     * Has the API aggregate assessed values by year for one neighbourhood and assessment class.
     *
     * @param neighbourhood the neighbourhood to filter on
     * @param assessmentClass the assessment class to filter on
     * @param func the SoQL aggregate function to apply, such as "sum" or "avg"
     * @return the aggregated value for each assessment year, both as strings
     */
    private Map<String, String> getYearNumericalSummary(String neighbourhood, String assessmentClass, String func) {
        String query = "select assessment_year, " + func + "(assessed_value) " +
                "where neighbourhood_name = '" + neighbourhood + "' " +
                "and mill_class_1 = '" + assessmentClass + "' " +
                "and tax_class_pct_1 = '100' " +
                "and assessed_value != 0 " +
                "group by assessment_year";
        HttpResponse<String> response = fetchData(query);

        return Arrays.stream(response.body().split("\n"))
                .map(x -> x.replace("\"", "").split(","))
                .skip(1)
                .collect(Collectors.toMap(x -> x[0],
                        x -> x[1]));
    }

    public Map<String, Long> getYearAssessedValueSum(String neighbourhood, String assessmentClass) {
        Map<String, String> data = getYearNumericalSummary(neighbourhood, assessmentClass, "sum");
        // Return Empty Map if dataset is empty, ideally would handle by providing the user with a pop-up alert.
        if (data.isEmpty()) {
            return new HashMap<>();
        }
        Map<String, Long> out = new HashMap<>();

        data.forEach((key, value) -> {
            out.put(key, parseLong(value));
        });
        return out;
    }

    public Map<String, Long> getYearAssessedValueAvg(String neighbourhood, String assessmentClass) {
        Map<String, String> data = getYearNumericalSummary(neighbourhood, assessmentClass, "avg");
        // Return Empty Map if dataset is empty, ideally would handle by providing the user with a pop-up alert.
        if (data.isEmpty()) {
            return new HashMap<>();
        }
        Map<String, Long> out = new HashMap<>();

        data.forEach((key, value) -> {
            out.put(key, (long) Double.parseDouble(value));
        });
        return out;
    }

    public Map<String, Long> getYearAvgNoOutliers(String neighbourhood, String assessmentClass) {
        List<String> years = getFormattedDataColumn("select distinct assessment_year");
        Map<String, Long> out = new HashMap<>();

        for (String year : years) {
            List<Long> values = getValuesByYearNoOutliers(neighbourhood, assessmentClass, Integer.parseInt(year));
            out.put(year, average(values));
        }

        return out;
    }

    /**
     * Averages a list of assessed values, rounding down to the nearest dollar.
     *
     * @param values the assessed values to average
     * @return the average, or 0 if the list is empty
     */
    static long average(List<Long> values) {
        if (values.isEmpty()) {
            return 0;
        }
        long sum = 0;
        for (Long value : values) {
            sum += value;
        }
        return sum / values.size();
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

    /**
     * Removes values more than 1.5 times the interquartile range below Q1 or above Q3.
     *
     * @param data the values to filter, sorted in ascending order
     * @return the values within the fences, or an empty list if there are fewer than four values
     */
    public List<Long> removeOutliers(List<Long> data) {
        // Return Empty List if five number summary can't be calculated,
        // ideally would handle by providing the user with a pop-up alert.
        if (data.size() < 4) {
            return new ArrayList<>();
        }
        long Q3 = (data.get(Math.floorDiv(data.size() * 3, 4)) + data.get(Math.ceilDiv(data.size() * 3, 4))) / 2;
        long Q1 = (data.get(Math.floorDiv(data.size(), 4)) + data.get(Math.ceilDiv(data.size(), 4))) / 2;
        long IQR = Q3 - Q1;

        long upperBound = (long) (Q3 + (IQR * 1.5));
        long lowerBound = (long) (Q1 - (IQR * 1.5));

        return data.stream()
                .filter(x -> x >= lowerBound && x <= upperBound)
                .collect(Collectors.toList());
    }

    private long getMedian(List<Long> assessmentValues) {
        // Return zero if no assessments found, ideally would handle by providing the user with a pop-up alert.
        if (assessmentValues.isEmpty()) {
            return 0;
        }
        int size = assessmentValues.size();
        // even
        if ((size % 2) == 0) {
            return ( (assessmentValues.get((size / 2) - 1) + assessmentValues.get(size / 2) ) / 2);
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

    public ObservableList<TableViewEntry> getTablePage(String neighbourhood, String assessmentClass, int page, int pageSize) {
        int offset = pageSize * page;
        String query = "select account_number, assessment_year, assessed_value, suite, house_number, street_name, year_built, lot_size, garage " +
                "where neighbourhood_name = '" + neighbourhood + "' " +
                "and mill_class_1 = '" + assessmentClass + "' " +
                "and tax_class_pct_1 = '100' " +
                "and assessed_value != 0 " +
                "order by assessed_value ASC " +
                "limit " + pageSize +
                "offset " + offset;

        List<String> data = getFormattedDataColumn(query);
        ObservableList<TableViewEntry> out = FXCollections.observableArrayList();

        for (String line : data) {
            String[] values = line.split(",");
            int account = Integer.parseInt(values[0]);
            int assessmentYear = Integer.parseInt(values[1]);
            long assessedValue = Long.parseLong(values[2]);
            String suite = values[3];
            String houseNum = values[4];
            String streetName = values[5];

            int yearBuilt;
            try {
                yearBuilt = Integer.parseInt(values[6]);
            } catch (NumberFormatException e) {
                yearBuilt = -1;
            }

            String lotSize = values[7];
            String garage = values[8];

            out.add(new TableViewEntry(account, assessmentYear, assessedValue, suite, houseNum, streetName, yearBuilt, lotSize, garage));
        }

        return out;
    }
}
