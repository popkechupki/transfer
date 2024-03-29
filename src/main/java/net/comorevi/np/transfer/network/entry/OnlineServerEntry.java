package net.comorevi.np.transfer.network.entry;

import java.util.ArrayList;
import java.util.LinkedList;

public class OnlineServerEntry extends GsonEntry {
    private LinkedList<Server> servers;

    public LinkedList<Server> getServerList() {
        return servers;
    }

    public static class Server {
        private int id;
        private String name;
        private String address;
        private int port;
        private String description;
        private ArrayList<Integer> categories;
        private int is_verified;
        private int is_display_address;

        public int getId() {
            return id;
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

        public String getDescription() {
            return description;
        }

        public ArrayList<Integer> getCategories() {
            return categories;
        }

        public int getIs_verified() {
            return is_verified;
        }

        public int getIs_display_address() {
            return is_display_address;
        }
    }
}