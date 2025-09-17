package bkv.colligendis.utils.meshok;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import bkv.colligendis.database.entity.meshok.MeshokCategory;
import bkv.colligendis.database.entity.meshok.MeshokLot;
import bkv.colligendis.database.entity.meshok.MeshokSeller;
import bkv.colligendis.utils.N4JUtil;
import bkv.colligendis.utils.meshok.data.CategoriesRequest;
import bkv.colligendis.utils.meshok.data.Category;
import bkv.colligendis.utils.meshok.data.CategoryResponse;
import bkv.colligendis.utils.meshok.data.GetItemsRequest;
import bkv.colligendis.utils.meshok.data.GetItemsResponse;
import bkv.colligendis.utils.meshok.data.Location;
import bkv.colligendis.utils.meshok.data.Lot;
import bkv.colligendis.utils.meshok.data.Picture;

public class MeshokUtil {
    public static final String MESHOK_URL = "https://meshok.net/";
    public static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.2 Safari/605.1.15";
    public static final String COOKIE = "sup_ses=d885585e500a4b6c87623c4ff9b71cf2; current-visit=1748117655; last-visit=1748078110; lla=1748130320; st-client-side-device=desktop; _ga_8VTXKHQH42=GS2.1.s1748117656$o152$g1$t1748130309$j0$l0$h0; _ga=GA1.1.914315030.1735085196; auth=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOjE3MzE3NTksImV4cCI6MTc0ODEzMDU4OCwiaWF0IjoxNzQ4MTMwMjU4LCJyb2xlcyI6WyJ1c2VyIl0sImp0aSI6IlkyZW5iY21iL0UzZDJVcUR5YUdYaXAvVHZ2bmZodGk3OFpSN1czVW92V0k9In0.qIhr1pOzbEL5x1AdazfAWjSgUi-FeYXwMhOiVbjcayE; lang=ru; sup_ses=d885585e500a4b6c87623c4ff9b71cf2; tz_offset=-180; preferences={\"$6@7s@8\":{\"$b\":{\"$d\":20,\"$c\":{\"$9\":\"$n\",\"$a\":0}},\"$e\":{\"$d\":20,\"$c\":{\"$9\":\"$n\",\"$a\":0}},\"$6s-on-$g\":{\"$d\":20,\"$c\":{\"$9\":\"watchersCount\",\"$a\":1}},\"$gs-$h\":{\"$d\":20,\"$c\":{\"$9\":\"$n\",\"$a\":0}}},\"$i@d\":500,\"$i@c\":{\"$9\":\"orderDate\",\"$a\":1},\"$7@fs\":{\"search\":false,\"showOnly@k\":true,\"categories@k\":true,\"$l-959@k\":true},\"items@m\":\"list\"}; _ym_isad=2; st-rec/cats=3w~2ajbdb.9jy~2aen0s.a1s~2aatgu.b6e~2aat80.4g~2aa7kb.1n4~2a9797.1dw~2a978h.17r~2a96z8; _9fa7d=5ce50a9ea7e0ab4f; cf_clearance=kvAqKO5yw0qRL0rd2Tu2clw9r2HvyhW_oWQDGQuQ2Vg-1747653064-1.2.1.1-0t.R.KRklawjN608UT3wG2XUPXChudnrvIMlpL19nCatjd0iRRLJUywd7wkJggQUnkDZ.CbRKKCAf83qD6mwkUYzYLyidtlOKeCqNDHxytKczjDTNygsMqHRe0p3P92Nja.5JJVwezwfJGxebsrPhIewfC1yVCwjnlyMt6UFDEXvVrS2QzwgXazJlj4dVgRWX3Ta8IC3SCx_OjPgC6ZzrKmi6M24bMnFh9ej1Gr7hOTkNi5S7I3fvMmcEmvUDiwml3ZyYlo7r06knzwLrOkdzMwPVS.eTozemmVO5TK8CKsdrRg2e90OKjnMoIfqVtyWG6RPiGvSsZa4NlPrb3pPMaiFOzhMzm0_xJ3jRbfPxWbbq4jEgcCKwqkudPtInUtz; ss=1731759%7C8e5c32fae6d123ec53664d3be4b6529fc5cd68fffd56552b60e8502bb434bf1e; st-lots-filter-shown=true; st-delivery=w,all; st-lot-in-sale-view-settings={}; st-tids=m7yzkz4tg1y7di; st-bgrm-used=true; st-create-lot-is-search-category-active=false; st-rec/lots=true; L=1; la=1735480808; st-privacy-alert=false; _ym_d=1735085196; _ym_uid=1735085196884061274; st-hidden-menu-sections=[]; st-messages-settings={\"filter\":{\"readState\":null,\"chatTypes\":null}}; st-color-theme=white";

    public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    static Gson gson = new GsonBuilder().serializeSpecialFloatingPointValues().serializeNulls().create();

    public static final String CATEGORIES_GET_ITEMS_URL = "api/command/categories/get-items";
    public static final String LOTS_GET_ITEMS_URL = "api/command/lots/get-items";

    public static void saveLotsToDatabase(int categoryMid) {
        List<Lot> lots = getLots(categoryMid);

        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

        for (Lot lot : lots) {
            MeshokLot meshokLot = N4JUtil.getInstance().meshokService.meshokLotService.findByMid(lot.getId());
            if (meshokLot == null)
                meshokLot = new MeshokLot();

            meshokLot.setMid(lot.getId());
            meshokLot.setTitle(lot.getTitle());

            try {
                if (lot.getBeginDate() != null)
                    meshokLot.setBeginDate(sdf.parse(lot.getBeginDate()));
                if (lot.getEndDate() != null)
                    meshokLot.setEndDate(sdf.parse(lot.getEndDate()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            meshokLot.setBidsCount(lot.getBidsCount());

            MeshokCategory meshokCategory = N4JUtil.getInstance().meshokService.meshokCategoryService
                    .findByMid(lot.getCategoryId());
            if (meshokCategory == null)
                meshokCategory = new MeshokCategory();

            meshokLot.setCategory(meshokCategory);

            meshokLot.setCurrency(lot.getCurrency());

            meshokLot.setWatchCount(lot.getWatchCount());
            meshokLot.setHitsCount(lot.getHitsCount());

            meshokLot.setPictures(lot.getPictures().stream().map(Picture::getUrl).collect(Collectors.toList()));

            meshokLot.setAntisniperEnabled(lot.isAntisniperEnabled());
            meshokLot.setEndDateExtended(lot.isEndDateExtended());

            meshokLot.setBlocked(lot.isBlocked());
            meshokLot.setBanned(lot.isBanned());
            meshokLot.setBargainAvailable(lot.isBargainAvailable());
            meshokLot.setFeatured(lot.isFeatured());

            meshokLot.setPrice(lot.getPrice());
            meshokLot.setQuantity(lot.getQuantity());
            meshokLot.setSoldQuantity(lot.getSoldQuantity());

            meshokLot.setStatus(lot.getStatus());

            MeshokSeller meshokSeller = N4JUtil.getInstance().meshokService.meshokSellerService
                    .findByMid(lot.getSeller().getId());
            if (meshokSeller == null) {
                meshokSeller = new MeshokSeller();
                meshokSeller.setMid(lot.getSeller().getId());
                meshokSeller.setDisplayName(lot.getSeller().getDisplayName());
                meshokSeller.setTrusted(lot.getSeller().isTrusted());
                meshokSeller.setHasEconomyDelivery(lot.getSeller().isHasEconomyDelivery());
                meshokSeller.setHasDiscounts(lot.getSeller().isHasDiscounts());
                meshokSeller.setAvatarURL(lot.getSeller().getAvatarURL());
                meshokSeller.setBanned(lot.getSeller().isBanned());
                meshokSeller.setOnHold(lot.getSeller().isOnHold());
            }
            meshokLot.setSeller(meshokSeller);

            meshokLot.setStartPrice(lot.getStartPrice());
            meshokLot.setStrikePrice(lot.getStrikePrice());

            meshokLot.setType(lot.getType());

            meshokLot.setTemporarilyBlocked(lot.isTemporarilyBlocked());

            N4JUtil.getInstance().meshokService.meshokLotService.save(meshokLot);
        }

    }

    public static int getCountLotsByCategory(int categoryMid) {
        GetItemsRequest request = new GetItemsRequest();
        request.getFilter().setCategoryId(categoryMid);

        request.getFilter().setLocation(new Location(32, "all", true));
        request.getFilter().getLocation().setCityId(32);
        request.getFilter().getLocation().setEconomyDelivery(true);
        request.getFilter().setPage(1);
        request.getFilter().setPageSize(200);

        request.getIncludes().setLots(false);
        request.getIncludes().setStats(true);
        GetItemsResponse response = fetchAndParseJson(LOTS_GET_ITEMS_URL, true,
                GetItemsResponse.class, request);

        int count = response.getResult().getStats().getCount().getOverall();

        return count;
    }

    public static List<Lot> getLots(int categoryMid) {

        GetItemsRequest request = new GetItemsRequest();
        request.getFilter().setCategoryId(categoryMid);

        request.getFilter().setLocation(new Location(32, "all", true));
        request.getFilter().getLocation().setCityId(32);
        request.getFilter().getLocation().setEconomyDelivery(true);
        request.getFilter().setPage(1);
        request.getFilter().setPageSize(200);

        request.getIncludes().setLots(false);
        request.getIncludes().setStats(true);
        GetItemsResponse response = fetchAndParseJson(LOTS_GET_ITEMS_URL, true,
                GetItemsResponse.class, request);

        int count = response.getResult().getStats().getCount().getOverall();
        System.out.println("Total lots: " + count);

        request.getIncludes().setLots(true);

        int maxpage = count / 200 + 1;
        List<Lot> lots = new ArrayList<>();

        for (int i = 1; i <= maxpage; i++) {

            request.getFilter().setPage(i);

            System.out.println(request.toJson());

            response = fetchAndParseJson(LOTS_GET_ITEMS_URL, true, GetItemsResponse.class,
                    request);

            lots.addAll(Arrays.asList(response.getResult().getLots()));
        }

        return lots;
    }

    public static List<Category> getCategories() {
        CategoriesRequest request = new CategoriesRequest();
        // 252 - coins, 786 - banknotes and bonds
        request.setIdentifiers(List.of(786, 252));
        request.setChildsLevel(5);
        request.setIncludeParents(true);

        CategoryResponse response = fetchAndParseJsonWithCookies(CATEGORIES_GET_ITEMS_URL, CategoryResponse.class,
                request);

        return response.getResult();
    }

    public static void saveToDatabase(List<Category> categories) {
        // Сохранение категорий должно быть по уровням, начиная с 5 категории и ниже
        categories.stream().filter(category -> category.getLevel() == 1).forEach(category -> {
            MeshokCategory meshokCategory = N4JUtil.getInstance().meshokService.meshokCategoryService
                    .findByMid(category.getId());
            if (meshokCategory == null)
                meshokCategory = new MeshokCategory(category.getName());

            meshokCategory.setExtraName(category.getExtraName());
            meshokCategory.setMid(category.getId());
            meshokCategory.setLevel(category.getLevel());
            if (category.getParentId() != 0) {
                meshokCategory.setParent(
                        N4JUtil.getInstance().meshokService.meshokCategoryService.findByMid(category.getParentId()));
            }
            if (meshokCategory.getParent() != null) {
                System.out.println(
                        "Parent: " + meshokCategory.getParent().getName() + " Category " + meshokCategory.getName()
                                + " saved");
            }
            N4JUtil.getInstance().meshokService.meshokCategoryService.save(meshokCategory);
        });
    }

    public static <T> T fetchAndParseJsonWithCookies(String urlString, Class<T> clazz, Object requestBody) {
        return fetchAndParseJson(urlString, true, clazz, requestBody);
    }

    public static <T> T fetchAndParseJson(String urlString, boolean useCookies, Class<T> clazz, Object requestBody) {
        try {
            URL url = URI.create(MeshokUtil.MESHOK_URL + urlString).toURL();
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true);

            if (useCookies) {
                con.setRequestProperty("User-Agent", USER_AGENT);
                con.setRequestProperty("Cookie", MeshokUtil.COOKIE);
            }
            // System.out.println("-----------Request---------------");
            // System.out.println(gson.toJson(requestBody));

            // Write request body
            if (requestBody != null) {
                try (OutputStream os = con.getOutputStream()) {
                    byte[] input = gson.toJson(requestBody).getBytes("utf-8");
                    os.write(input, 0, input.length);
                }
            }

            int responseCode = con.getResponseCode();
            if (responseCode >= 200 && responseCode < 300) {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                    String inputLine;
                    StringBuilder responseContent = new StringBuilder();
                    while ((inputLine = in.readLine()) != null) {
                        responseContent.append(inputLine);
                    }
                    // System.out.println("-----------Response---------------");
                    System.out.println(responseContent.toString());
                    return gson.fromJson(responseContent.toString(), clazz);
                }
            } else {
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
            e.printStackTrace();
            return null;
        }
    }

}
