package net.comorevi.np.transfer.network.entry;

import java.util.LinkedList;

public class CategoryEntry extends GsonEntry {
    private LinkedList<Category> categories;

    public LinkedList<Category> getCategories() {
        return categories;
    }

    public static class Category {
        private int id;
        private String name;

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }
}
