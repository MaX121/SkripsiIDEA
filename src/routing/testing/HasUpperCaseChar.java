package routing.testing;

import java.util.ArrayList;

public class HasUpperCaseChar {
    public static void main(String[] args) {

        ArrayList <String> list = new ArrayList<>();

        list.add("One");
        list.add("two");
        list.add("three");
        list.add("Four");
        list.add("five");

        System.out.println(hasUpperCaseChar(list));

    }

    private static ArrayList <String> hasUpperCaseChar(ArrayList <String> list) {
        ArrayList <String>  returnedList = new ArrayList<>();
        for (String string : list) {
            // loop through each string in list and check for uppercase
            if (string.matches("[A-Z]*")) returnedList.add(string);
        } return returnedList;
    }


}
