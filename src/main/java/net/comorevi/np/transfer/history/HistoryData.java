package net.comorevi.np.transfer.history;

public class HistoryData {
    private String name;
    private String address;
    private int port;

    public HistoryData(String name, String address, int port) {
        this.name = name;
        this.address = address;
        this.port = port;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }
}
