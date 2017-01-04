package com.zhangruofan.requery.util;

/**
 * Created by zhangruofan on 16-12-26.
 */

public interface Constants {

    interface Color {
        int[] colors = { android.graphics.Color.RED, android.graphics.Color.BLUE, android.graphics.Color.GREEN, android.graphics.Color.MAGENTA };
    }

    interface Name {
        String[] firstNames = new String[]{
                "Alice", "Bob", "Carol", "Chloe", "Dan", "Emily", "Emma", "Eric", "Eva",
                "Frank", "Gary", "Helen", "Jack", "James", "Jane",
                "Kevin", "Laura", "Leon", "Lilly", "Mary", "Maria",
                "Mia", "Nick", "Oliver", "Olivia", "Patrick", "Robert",
                "Stan", "Vivian", "Wesley", "Zoe"};
        String[] lastNames = new String[]{
                "Hall", "Hill", "Smith", "Lee", "Jones", "Taylor", "Williams", "Jackson",
                "Stone", "Brown", "Thomas", "Clark", "Lewis", "Miller", "Walker", "Fox",
                "Robinson", "Wilson", "Cook", "Carter", "Cooper", "Martin" };
    }

    interface Extra {
        String EXTRA_PERSON_ID = "personId";
    }
}
