package net.comorevi.np.transfer.utils;

import com.google.gson.Gson;
import net.comorevi.np.transfer.utils.data.OnlineServerData;
import net.comorevi.np.transfer.utils.data.CategoryData;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Arrays;

public class ServerListLoader {
    private static final ServerListLoader instance = new ServerListLoader();

    public OnlineServerData getOnlineServerData() throws Exception {
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder(URI.create("https://mcservers.jp/api/v1/server/list/online"))
                .timeout(Duration.ofSeconds(10))
                .build();

        return new Gson().fromJson(httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString()).get().body(), OnlineServerData.class);
    }

    public CategoryData getCategoryData() throws Exception {
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder(URI.create("https://mcservers.jp/api/v1/category/list"))
                .timeout(Duration.ofSeconds(10))
                .build();

        return new Gson().fromJson(httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString()).get().body(), CategoryData.class);
    }

    public String getCategoryLabels(OnlineServerData.Server server) throws Exception {
        CategoryData categoryData = getCategoryData();
        StringBuilder sb = new StringBuilder();
        Arrays.asList(server.getCategories().substring(1, server.getCategories().length() -1).split(",")).forEach(categoryId -> {
            sb.append(getCategoryLabelById(categoryData, Integer.parseInt(categoryId))).append(", ");
        });
        if (sb.length() != 0) sb.deleteCharAt(sb.lastIndexOf(","));
        return sb.toString();
    }

    public String getCategoryLabelById(CategoryData categoryData, int categoryId) {
        int i = 0;
        for (int j = 0; j < categoryData.getCategories().size(); j++) {
            if (categoryData.getCategories().get(j).getId() == categoryId) {
                i = j;
                break;
            }
        }
        return categoryData.getCategories().get(i).getName();
    }

    private ServerListLoader() {}

    public static ServerListLoader getInstance() {
        return instance;
    }
}
