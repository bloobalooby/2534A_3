package com.example.lab_rest.remote;

import java.net.URL;

public class ApiUtils {

    //REST API server URL
    public static final String BASE_URL= "https://codelah.my/2534A_3/api/";

    // return UserService instance
    public static UserService getUserService() {
        return RetrofitClient.getClient(BASE_URL).create(UserService.class);
    }
}
