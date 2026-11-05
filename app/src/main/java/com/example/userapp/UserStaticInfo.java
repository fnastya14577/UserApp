package com.example.userapp;

import java.util.ArrayList;
import java.util.List;

public class UserStaticInfo {
    public static String profileId;
    public static final String USERS_SIGN_IN_INFO = "UsersSignInInfo";
    public static final String USERS_PROFILE_INFO = "UsersProfileInfo";
    public static final String PASSWORD = "password";
    public static final String PROFILE_ID = "profileId";
    public static final String AGE = "age";
    public static final String NAME = "name";
    public static final String STATE = "state";
    public static List<User> users = new ArrayList<>();
    public static final String POSITION = "position";

    public UserStaticInfo() {
        if (users.size() == 0) {
            AddUsersInList();
        }
    }

    private void AddUsersInList() {
        users.add(new User("Соня", "В сети", 19, 0));
        users.add(new User("Соня", "В сети", 19, 1));
        users.add(new User("Соня", "Отошла", 19, 2));
        users.add(new User("Соня", "Не беспокоить", 19, 0));
    }
}