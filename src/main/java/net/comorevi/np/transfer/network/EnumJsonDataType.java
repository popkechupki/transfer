package net.comorevi.np.transfer.network;

public enum EnumJsonDataType {
    ONLINE_LIST("https://mcservers.jp/api/v1/server/list/online"),
    CATEGORY_LIST("https://mcservers.jp/api/v1/category/list");

    private String url;

    EnumJsonDataType(String url) {
        this.url = url;
    }

    public String getBasicUrl() {
        return url;
    }
}
