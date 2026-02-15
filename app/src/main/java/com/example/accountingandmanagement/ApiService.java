package com.example.accountingandmanagement;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    // Matches your Netlify function filename (get-data.js)
    @POST(".netlify/functions/webhook")
    Call<DataResponse> SendAndGetData(@Body RequestBody credentials);
}