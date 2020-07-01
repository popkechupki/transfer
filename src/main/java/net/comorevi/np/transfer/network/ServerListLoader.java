package net.comorevi.np.transfer.network;

import com.google.gson.Gson;
import net.comorevi.np.transfer.network.data.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.ExecutionException;

public class ServerListLoader {
    private static final ServerListLoader instance = new ServerListLoader();

    public LinkedList<OnlineServerEntry.Server> getOnlineServers() throws Exception {
        return ((OnlineServerEntry) getGsonEntry(EnumJsonDataType.ONLINE_LIST)).getServerList();
    }

    public CategoryEntry getCategoryData() throws Exception {
        return ((CategoryEntry) getGsonEntry(EnumJsonDataType.CATEGORY_LIST));
    }

    public String getCategoryLabels(OnlineServerEntry.Server server) throws Exception {
        CategoryEntry categoryData = getCategoryData();
        StringBuilder sb = new StringBuilder();
        Arrays.asList(server.getCategories().substring(1, server.getCategories().length() -1).split(",")).forEach(categoryId -> {
            sb.append(getCategoryLabelById(categoryData, Integer.parseInt(categoryId))).append(", ");
        });
        if (sb.length() != 0) sb.deleteCharAt(sb.lastIndexOf(","));
        return sb.toString();
    }

    public String getCategoryLabelById(CategoryEntry categoryData, int categoryId) {
        int i = 0;
        for (int j = 0; j < categoryData.getCategories().size(); j++) {
            if (categoryData.getCategories().get(j).getId() == categoryId) {
                i = j;
                break;
            }
        }
        return categoryData.getCategories().get(i).getName();
    }

    private GsonEntry getGsonEntry(EnumJsonDataType dataType) throws ExecutionException, InterruptedException {
        return getGsonEntry(dataType, 0);
    }

    private GsonEntry getGsonEntry(EnumJsonDataType dataType, int id) throws ExecutionException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder(URI.create(dataType.getBasicUrl()))
                .timeout(Duration.ofSeconds(10))
                .build();

        Class<? extends GsonEntry> entryClass = null;
        switch (dataType) {
            case ONLINE_LIST:
                entryClass = OnlineServerEntry.class;
                break;
            case CATEGORY_LIST:
                entryClass = CategoryEntry.class;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + dataType);
        }
        return new Gson().fromJson(httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString()).get().body(), entryClass);
    }

    private ServerListLoader() {}

    public static ServerListLoader getInstance() {
        return instance;
    }
}
