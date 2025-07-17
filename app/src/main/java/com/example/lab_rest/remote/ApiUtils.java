package com.example.lab_rest.remote;

/**
 * Utility class for accessing API services.
 * Provides centralized access to Retrofit service interfaces.
 */
public class ApiUtils {

    // Base URL of the REST API server
    public static final String BASE_URL = "https://codelah.my/2534A_3/api/"; // Our group database

    /**
     * Returns a UserService instance for performing user-related API calls.
     * @return UserService instance
     */
    public static UserService getUserService() {
        return RetrofitClient.getClient(BASE_URL).create(UserService.class);
    }

    /**
     * Returns an ItemService instance for performing item-related API calls.
     * @return ItemService instance
     */
    public static ItemService getItemService() {
        return RetrofitClient.getClient(BASE_URL).create(ItemService.class);
    }

    /**
     * Returns a ProfileService instance for performing profile-related API calls.
     * @return ProfileService instance
     */
    public static ProfileService getProfileService() {
        return RetrofitClient.getClient(BASE_URL).create(ProfileService.class);
    }

    /**
     * Returns a RequestService instance for performing request-related API calls.
     * @return RequestService instance
     */
    public static RequestService getRequestService() {
        return RetrofitClient.getClient(BASE_URL).create(RequestService.class);
    }
}
