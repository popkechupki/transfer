package net.comorevi.np.transfer.utils.data;

import java.util.LinkedList;

public class CategoryData {
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
