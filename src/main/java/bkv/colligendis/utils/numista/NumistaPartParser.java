package bkv.colligendis.utils.numista;

import bkv.colligendis.database.entity.features.Year;
import bkv.colligendis.database.entity.numista.NType;
import bkv.colligendis.utils.DebugUtil;
import bkv.colligendis.utils.N4JUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public abstract class NumistaPartParser {

    public String partName;

    protected final PartParser parser;

    public static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/18.3 Safari/605.1.15";
    public static final String COOKIE = "_pk_ses.10.0242=1; carte=type; access_token=P%3EbLPZEY%24%21t.%3F9jIWgGg%28sG%5DrBF1S%28b%21%231.x7g%21%3E; pseudo=kbobryakov; test_cookies=1; _pk_ref.10.0242=%5B%22%22%2C%22%22%2C1752798026%2C%22https%3A%2F%2Fwww.google.com%2F%22%5D; PHPSESSID=s9ng8rblh3hmheem82fi9fivfo; pieces_par_page=50; cf_clearance=4BVyMVqdzJhLEIVyIb83oHWw28Wjvi7d_7rRX7tbtxY-1752013302-1.2.1.1-ae26F5fbR4rGUn63S4W8E4iTI2HqteIPIge5GuxlRp1ieVDnuFLroUz38JPU6CO8MB3tIMDTDZMs.pm9RlfTwMblAxKG9q2z1lBK5CMUySi4xP30uRtkr3Ktrc7oRgIIj7zL7qiVESrNpwfpqwTTZkga8Y2DxQZp44mz_kalvbSw7BUIwV7NCq.tviEZD1V0UfM3vMzoTyQPZlilLYDuFXo1zW8ZBOwmvLnrjkQoi3Q; _pk_id.10.0242=a36509097c0e55fd.1751222019.; search_order=v; issuer_sort=d; saisie_rapide=n; search_subtypes=148; tb=y; tc=y; tn=y; tp=y; tt=y; tbb=y; tbc=y; tbl=y; tbt=y";
    private static final Boolean showPageAfterLoad = false;

    public static final String TYPE_PAGE_PREFIX = "https://en.numista.com/catalogue/contributions/modifier.php?id=";

    public NumistaPartParser(PartParser parser) {
        this.parser = parser;
    }

    public ParseEvent parse(Document document, NType nType) {
        return parser.parse(document, nType);
    }

    public ParseEvent parseWithMetric(Document document, NType nType) {
        long time = System.currentTimeMillis();
        ParseEvent result = parser.parse(document, nType);
        DebugUtil.showInfo(this, composeTimeInfo("parsing: ", time));
        return result;
    }

    ;

    private String composeTimeInfo(String message, long time) {
        return message + " takes " + (System.currentTimeMillis() - time) / 1000 + " sec "
                + (System.currentTimeMillis() - time) % 1000 + " millis";
    }

    public static String getAttribute(Element element, String key) {
        if (element != null && !element.attributes().get(key).isEmpty()) {
            return element.attributes().get(key);
        }
        return null;
    }

    public static Map<String, String> getAttributeWithTextSingleOption(Document page, String searchQuery, String key) {
        Element element = page.selectFirst(searchQuery);

        if (element == null) {
            DebugUtil.showWarning(NumistaPartParser.class, "Can't find " + searchQuery + " on the page");
            return null;
        }

        Element option = element.selectFirst("option");

        if (option == null) {
            // DebugUtil.showInfo(NumistaEditPageUtil.class, "Can't find <option> tag in " +
            // searchQuery + " on the page");
            return null;
        }

        if (option.text().isEmpty()) {
            DebugUtil.showWarning(NumistaPartParser.class, "The " + searchQuery + " name is empty on the page");
            return null;
        }

        if (option.attributes().get(key).isEmpty()) {
            DebugUtil.showWarning(NumistaPartParser.class,
                    "The " + searchQuery + " " + key + " is empty on the page");
            return null;
        }

        return Map.of(key, option.attributes().get(key), "text", option.text());
    }

    public static HashMap<String, String> getAttributeWithTextSelectedOption(Object source, String searchQuery) {
        Element element = null;
        if (source instanceof Document) {
            element = ((Document) source).selectFirst(searchQuery);
        } else if (source instanceof Element) {
            element = ((Element) source).selectFirst(searchQuery);
        }

        if (element == null)
            return null;

        return element.select("option").stream().filter(option -> option.attributes().hasKey("selected")).findFirst()
                .map(option -> {
                    HashMap<String, String> r = new HashMap<>();
                    r.put("value", option.attributes().get("value"));
                    r.put("text", option.text());
                    return r;
                }).orElse(null);

    }

    /**
     * Parse string {@code fullName} to find year periods.
     * Example: (1887-1918), (1887), (1990-date)
     *
     * @return Pair with left = "fromYears" and right = "tillYears"
     */
    protected static Pair<List<Year>, List<Year>> parseYearPeriods(String fullName) {

        Pair<List<Year>, List<Year>> result = MutablePair.of(new ArrayList<>(), new ArrayList<>());

        Pattern pattern = Pattern.compile("[(]\\S+[)]");
        Matcher matcher = pattern.matcher(fullName);

        while (matcher.find()) {
            String periodStr = matcher.group(0);
            final Year yearFrom;
            final Year yearTill;

            String[] years = periodStr.replace("(", "").replace(")", "").split("-");

            // Years can be (1887-1918), (1936), (1990-date)
            // After splitting by "-", we can get array of 2 strings or 1 string

            if (years.length == 0 || years.length > 2) {
                DebugUtil.showError(NumistaPartParser.class,
                        "Can't parse PHP request (years for = " + fullName + " with length != 1 or 2).");
                return null;
            } else if (years.length == 1) { // we have a period during one year, example "(1936)"
                if (StringUtils.isNumeric(years[0])) {
                    yearFrom = N4JUtil.getInstance().numistaService.calendarService
                            .findGregorianYearByValueOrCreate(Integer.parseInt(years[0]));
                    yearTill = yearFrom;
                } else { // Try to catch another variants for ruler's period with one year which is not
                         // numeric
                    DebugUtil.showError(NumistaPartParser.class, "Can't parse PHP request (period for = " + fullName
                            + " with one year which is not Numeric)");
                    return null;
                }
            } else { // Ruler's Period has two years (1887-1918) or (1990-date)

                if (StringUtils.isNumeric(years[0])) { // Now I only know that the start year is only number
                    yearFrom = N4JUtil.getInstance().numistaService.calendarService
                            .findGregorianYearByValueOrCreate(Integer.parseInt(years[0]));
                } else { // Try to catch another variants for ruler's period with two year which start
                         // year is not numeric
                    DebugUtil.showError(NumistaPartParser.class,
                            "Can't parse PHP request (start year = " + fullName + " is not Numeric).");
                    return null;
                }

                assert yearFrom != null;

                if (years[1].equals("date")) { // End year can be Numeric or "date". The "date" means that the ruling is
                                               // not finished.
                    yearTill = null;
                } else if (StringUtils.isNumeric(years[1])) {
                    yearTill = N4JUtil.getInstance().numistaService.calendarService
                            .findGregorianYearByValueOrCreate(Integer.parseInt(years[1]));
                } else { // Try to catch another variants for ruler's period with two year which end year
                         // is not numeric and not "date"
                    DebugUtil.showError(NumistaPartParser.class,
                            "Can't parse PHP request (end year = " + fullName + " is not Numeric and not 'date').");
                    return null;
                }
            }

            assert yearFrom != null;

            if (!result.getLeft().contains(yearFrom)) {
                result.getLeft().add(yearFrom);
            }

            if (yearTill != null && !result.getRight().contains(yearTill)) {
                result.getRight().add(yearTill);
            }
        }

        return result;
    }

    /**
     * Find in Map {@code hashMap} values with keys ("value", "text") and check on
     * {@code null} and {@code empty}
     *
     * @param hashMap Map with elements with "value" and "text" keys
     * @return {@code true} if values by "value" and "text" keys in Map is not null
     *         and is not empty, else - {@code false}.
     */
    public static boolean isValueAndTextNotNullAndNotEmpty(HashMap<String, String> hashMap) {
        return hashMap.get("value") != null && !hashMap.get("value").isEmpty() && hashMap.get("text") != null
                && !hashMap.get("text").isEmpty();
    }

    public static List<HashMap<String, String>> getAttributesWithTextSelectedOptions(Element element) {
        if (element != null) {
            return element.select("option").stream().filter(option -> option.attributes().hasKey("selected"))
                    .findFirst().map(option -> {
                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put("value", option.attributes().get("value"));
                        hashMap.put("text", option.text());
                        return hashMap;
                    }).stream().collect(Collectors.toList());
        }
        return null;
    }

    public static HashMap<String, String> getAttributeWithTextSingleOption(Element element, String key) {
        if (element != null) {
            Element option = element.selectFirst("option");
            if (option != null) {
                HashMap<String, String> result = new HashMap<>();
                result.put(key, option.attributes().get(key));
                result.put("text", option.text());
                return result;
            }
        }
        return null;
    }

    public static List<String> getTextsSelectedOptions(Element element) {
        if (element != null) {
            return element.select("option").stream().filter(option -> option.attributes().hasKey("selected"))
                    .map(Element::text).collect(Collectors.toList());
        }
        return null;
    }

    public static String getTagText(Element element) {
        if (element != null && !element.text().isEmpty()) {
            return element.text();
        }
        return null;
    }

    public static Document loadPageByURL(String urlString, boolean useCookies) {
        Document document;
        try {
            URL url = URI.create(urlString).toURL();
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            con.setRequestMethod("GET");
            con.setRequestProperty("Accept", "application/json"); // Indicate we expect JSON

            if (useCookies) {
                con.setRequestProperty("User-Agent", USER_AGENT);
                con.setRequestProperty("Cookie", COOKIE); // Use with caution if the JSON source is not numista
            }

            int responseCode = con.getResponseCode();
            // System.out.println("\nSending 'GET' request to URL : " + url);
            // System.out.println("Response Code : " + responseCode);

            if (responseCode == 404)
                return null;

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
                response.append(System.lineSeparator());
            }

            in.close();

            // Send the request to the server
            document = Jsoup.parse(response.toString());

            if (showPageAfterLoad)
                System.out.println(document);

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return document;
    }

    /**
     * Fetches content from the given URL and parses it as a JSON object.
     *
     * @param urlString  The URL to fetch JSON data from.
     * @param useCookies Whether to include the predefined COOKIE and USER_AGENT
     *                   (useful for numista.com APIs).
     * @return A JsonObject if parsing is successful, otherwise null.
     */
    public static <T> T fetchAndParseJson(String urlString, boolean useCookies, Class<T> clazz) {
        try {
            URL url = URI.create(urlString).toURL();
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            con.setRequestMethod("GET");
            con.setRequestProperty("Accept", "application/json"); // Indicate we expect JSON

            if (useCookies) {
                con.setRequestProperty("User-Agent", USER_AGENT);
                con.setRequestProperty("Cookie", COOKIE); // Use with caution if the JSON source is not numista
            }

            int responseCode = con.getResponseCode();
            if (responseCode >= 200 && responseCode < 300) { // Check for successful response
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuilder responseContent = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    responseContent.append(inputLine);
                }
                in.close();

                // Parse the JSON string
                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.readValue(responseContent.toString(), clazz);

            } else {
                DebugUtil.showError(NumistaPartParser.class,
                        "HTTP GET request failed with response code: " + responseCode + " for URL: " + urlString);
                // Log error response body if any
                try (BufferedReader errorStream = new BufferedReader(new InputStreamReader(con.getErrorStream()))) {
                    String errorLine;
                    StringBuilder errorResponse = new StringBuilder();
                    while ((errorLine = errorStream.readLine()) != null) {
                        errorResponse.append(errorLine);
                    }
                    System.err.println("Error response: " + errorResponse.toString());
                } catch (Exception e) {
                    // Ignore if error stream cannot be read
                }
                return null;
            }
        } catch (IOException e) {
            DebugUtil.showError(NumistaPartParser.class,
                    "IOException during fetching/parsing JSON from URL: " + urlString + " - " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Fetches content from the given URL and parses it as a JSON object.
     *
     * @param urlString  The URL to fetch JSON data from.
     * @param useCookies Whether to include the predefined COOKIE and USER_AGENT
     *                   (useful for numista.com APIs).
     * @return A JsonObject if parsing is successful, otherwise null.
     */
    public static String fetchJson(String urlString, boolean useCookies) {
        try {
            URL url = URI.create(urlString).toURL();
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            con.setRequestMethod("GET");
            con.setRequestProperty("Accept", "application/json"); // Indicate we expect JSON

            if (useCookies) {
                con.setRequestProperty("User-Agent", USER_AGENT);
                con.setRequestProperty("Cookie", COOKIE); // Use with caution if the JSON source is not numista
            }

            int responseCode = con.getResponseCode();
            if (responseCode >= 200 && responseCode < 300) { // Check for successful response
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuilder responseContent = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    responseContent.append(inputLine);
                }
                in.close();

                return responseContent.toString();
            } else {
                DebugUtil.showError(NumistaPartParser.class,
                        "HTTP GET request failed with response code: " + responseCode + " for URL: " + urlString);
                // Log error response body if any
                try (BufferedReader errorStream = new BufferedReader(new InputStreamReader(con.getErrorStream()))) {
                    String errorLine;
                    StringBuilder errorResponse = new StringBuilder();
                    while ((errorLine = errorStream.readLine()) != null) {
                        errorResponse.append(errorLine);
                    }
                    System.err.println("Error response: " + errorResponse.toString());
                } catch (Exception e) {
                    // Ignore if error stream cannot be read
                }
                return null;
            }
        } catch (IOException e) {
            DebugUtil.showError(NumistaPartParser.class,
                    "IOException during fetching/parsing JSON from URL: " + urlString + " - " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

}