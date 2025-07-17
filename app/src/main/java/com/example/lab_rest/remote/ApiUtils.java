package com.example.lab_rest.remote;

import java.net.URL;

public class ApiUtils {

    //REST API server URL
    public static final String BASE_URL= "https://codelah.my/2534A_3/api/"; // our group database

    // return UserService instance
    public static UserService getUserService() {
        return RetrofitClient.getClient(BASE_URL).create(UserService.class);
    }

    // return ItemService instance
    public static ItemService getItemService() {
        return RetrofitClient.getClient(BASE_URL).create(ItemService.class);
    }

    public static ProfileService getProfileService() {
        return RetrofitClient.getClient(BASE_URL).create(ProfileService.class);
    }

    public static RequestService getRequestService() {
        return RetrofitClient.getClient(BASE_URL).create(RequestService.class);
    }



}
