package bkv.colligendis.utils.numista.parser;

import bkv.colligendis.database.service.features.YearService;
import bkv.colligendis.database.service.numista.CalendarService;
import bkv.colligendis.database.service.numista.CatalogueReferenceService;
import bkv.colligendis.database.service.numista.CatalogueService;
import bkv.colligendis.database.service.numista.CollectibleTypeService;
import bkv.colligendis.database.service.numista.CommemoratedEventService;
import bkv.colligendis.database.service.numista.CurrencyService;
import bkv.colligendis.database.service.numista.DenominationService;
import bkv.colligendis.database.service.numista.IssuerService;
import bkv.colligendis.database.service.numista.IssuingEntityService;
import bkv.colligendis.database.service.numista.MarkService;
import bkv.colligendis.database.service.numista.MintService;
import bkv.colligendis.database.service.numista.MintmarkService;
import bkv.colligendis.database.service.numista.NTypeService;
import bkv.colligendis.database.service.numista.RulerGroupService;
import bkv.colligendis.database.service.numista.RulerService;
import bkv.colligendis.database.service.numista.SeriesService;
import bkv.colligendis.database.service.numista.SpecifiedMintService;
import bkv.colligendis.database.service.numista.VariantService;
import bkv.colligendis.utils.N4JUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public abstract class PartParser {

    private static final Logger logger = LogManager.getLogger(PartParser.class);

    public static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/18.3 Safari/605.1.15";
    public static final String COOKIE = "_pk_ses.10.0242=1; carte=type; access_token=P%3EbLPZEY%24%21t.%3F9jIWgGg%28sG%5DrBF1S%28b%21%231.x7g%21%3E; pseudo=kbobryakov; test_cookies=1; _pk_ref.10.0242=%5B%22%22%2C%22%22%2C1752798026%2C%22https%3A%2F%2Fwww.google.com%2F%22%5D; PHPSESSID=s9ng8rblh3hmheem82fi9fivfo; pieces_par_page=50; cf_clearance=4BVyMVqdzJhLEIVyIb83oHWw28Wjvi7d_7rRX7tbtxY-1752013302-1.2.1.1-ae26F5fbR4rGUn63S4W8E4iTI2HqteIPIge5GuxlRp1ieVDnuFLroUz38JPU6CO8MB3tIMDTDZMs.pm9RlfTwMblAxKG9q2z1lBK5CMUySi4xP30uRtkr3Ktrc7oRgIIj7zL7qiVESrNpwfpqwTTZkga8Y2DxQZp44mz_kalvbSw7BUIwV7NCq.tviEZD1V0UfM3vMzoTyQPZlilLYDuFXo1zW8ZBOwmvLnrjkQoi3Q; _pk_id.10.0242=a36509097c0e55fd.1751222019.; search_order=v; issuer_sort=d; saisie_rapide=n; search_subtypes=148; tb=y; tc=y; tn=y; tp=y; tt=y; tbb=y; tbc=y; tbl=y; tbt=y";
    private static final Boolean showPageAfterLoad = false;

    protected static final NTypeService nTypeService = N4JUtil.getInstance().numistaService.nTypeService;
    protected static final CollectibleTypeService collectibleTypeService = N4JUtil
            .getInstance().numistaService.collectibleTypeService;
    protected static final RulerService rulerService = N4JUtil.getInstance().numistaService.rulerService;
    protected static final RulerGroupService rulerGroupService = N4JUtil.getInstance().numistaService.rulerGroupService;
    protected static final IssuingEntityService issuingEntityService = N4JUtil
            .getInstance().numistaService.issuingEntityService;
    protected static final IssuerService issuerService = N4JUtil.getInstance().numistaService.issuerService;
    protected static final CurrencyService currencyService = N4JUtil.getInstance().numistaService.currencyService;
    protected static final DenominationService denominationService = N4JUtil
            .getInstance().numistaService.denominationService;
    protected static final CommemoratedEventService commemoratedEventService = N4JUtil
            .getInstance().numistaService.commemoratedEventService;
    protected static final SeriesService seriesService = N4JUtil.getInstance().numistaService.seriesService;
    protected static final CatalogueReferenceService catalogueReferenceService = N4JUtil
            .getInstance().numistaService.catalogueReferenceService;
    protected static final CatalogueService catalogueService = N4JUtil.getInstance().numistaService.catalogueService;
    protected static final VariantService variantService = N4JUtil.getInstance().numistaService.variantService;
    protected static final MarkService markService = N4JUtil.getInstance().numistaService.markService;
    protected static final SpecifiedMintService specifiedMintService = N4JUtil
            .getInstance().numistaService.specifiedMintService;
    protected static final MintmarkService mintmarkService = N4JUtil.getInstance().numistaService.mintmarkService;
    protected static final MintService mintService = N4JUtil.getInstance().numistaService.mintService;

    protected static final YearService yearService = N4JUtil.getInstance().numistaService.yearService;
    protected static final CalendarService calendarService = N4JUtil.getInstance().numistaService.calendarService;

    public String partName;

    protected final Parser parser;

    public PartParser(Parser parser) {
        this.parser = parser;
    }

    public ParsingResult parse(PageParser pageParser) {
        return parser.parse(pageParser);
    }

    public ParsingResult parseWithMetric(PageParser pageParser) {
        long time = System.currentTimeMillis();
        ParsingResult result = parser.parse(pageParser);
        logger.info("{} parsing takes: {}", partName, (System.currentTimeMillis() - time) / 1000 + " sec "
                + (System.currentTimeMillis() - time) % 1000 + " millis");
        return result;
    }

    public static Document loadPageByURL(String urlString, boolean useCookies) {
        Document document;
        try {
            URL url = URI.create(urlString).toURL();
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            con.setRequestMethod("GET");
            con.setRequestProperty("Accept", "application/json"); // Indicate we expect
            // JSON

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

    public static String getAttribute(Element element, String key) {
        if (element != null && !element.attributes().get(key).isEmpty()) {
            return element.attributes().get(key);
        }
        return null;
    }

    public static Map<String, String> getAttributeWithTextSingleOption(Document page, String searchQuery, String key) {
        Element element = page.selectFirst(searchQuery);

        if (element == null) {
            logger.info("Can't find " + searchQuery + " on the page");
            return null;
        }

        Element option = element.selectFirst("option");

        if (option == null) {
            logger.info("Can't find <option> tag in " + searchQuery + " on the page");
            return null;
        }

        if (option.text().isEmpty()) {
            logger.info("The " + searchQuery + " name is empty on the page");
            return null;
        }

        if (option.attributes().get(key).isEmpty()) {
            logger.info("The " + searchQuery + " " + key + " is empty on the page");
            return null;
        }

        return Map.of(key, option.attributes().get(key), "text", option.text());
    }

    /**
     * Parse string {@code fullName} to find year periods.
     * Example: (1887-1918), (1887), (1990-date)
     *
     * @return Pair with left = "fromYears UUIDs" and right = "tillYears UUIDs"
     */
    public static Pair<List<UUID>, List<UUID>> parseYearPeriods(String fullName) {

        Pair<List<UUID>, List<UUID>> result = MutablePair.of(new ArrayList<>(), new ArrayList<>());

        Pattern pattern = Pattern.compile("[(]\\S+[)]");
        Matcher matcher = pattern.matcher(fullName);

        while (matcher.find()) {
            String periodStr = matcher.group(0);
            UUID yearFromUuid = null;
            UUID yearTillUuid = null;

            String[] years = periodStr.replace("(", "").replace(")", "").split("-");

            // Years can be (1887-1918), (1936), (1990-date)
            // After splitting by "-", we can get array of 2 strings or 1 string

            if (years.length == 0 || years.length > 2) {
                logger.error("Can't parse PHP request (years for = {} with length != 1 or 2).", fullName);
                return null;
            } else if (years.length == 1) { // we have a period during one year, example "(1936)"
                if (StringUtils.isNumeric(years[0])) {

                    yearFromUuid = yearService.findGregorianYearUuidByValue(Integer.parseInt(years[0]));
                    assert yearFromUuid != null;

                    yearTillUuid = yearFromUuid;
                } else { // Try to catch another variants for ruler's period with one year which is not
                         // numeric
                    logger.error("Can't parse PHP request (period for = {} with one year which is not Numeric)",
                            fullName);
                    return null;
                }
            } else { // Ruler's Period has two years (1887-1918) or (1990-date)

                if (StringUtils.isNumeric(years[0])) { // Now I only know that the start year is only number

                    yearFromUuid = yearService.findGregorianYearUuidByValue(Integer.parseInt(years[0]));
                    assert yearFromUuid != null;

                } else { // Try to catch another variants for ruler's period with two year which start
                         // year is not numeric
                    logger.error("Can't parse PHP request (start year = {} is not Numeric).", fullName);
                    return null;
                }

                if (years[1].equals("date")) { // End year can be Numeric or "date". The "date" means that the ruling is
                                               // not finished.
                    yearTillUuid = null;
                } else if (StringUtils.isNumeric(years[1])) {
                    yearTillUuid = yearService.findGregorianYearUuidByValue(Integer.parseInt(years[1]));
                    assert yearTillUuid != null;

                } else { // Try to catch another variants for ruler's period with two year which end year
                         // is not numeric and not "date"
                    logger.error("Can't parse PHP request (end year = {} is not Numeric and not 'date').", fullName);
                    return null;
                }
            }

            if (!result.getLeft().contains(yearFromUuid)) {
                result.getLeft().add(yearFromUuid);
            }

            if (yearTillUuid != null && !result.getRight().contains(yearTillUuid)) {
                result.getRight().add(yearTillUuid);
            }
        }

        return result;

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
}
