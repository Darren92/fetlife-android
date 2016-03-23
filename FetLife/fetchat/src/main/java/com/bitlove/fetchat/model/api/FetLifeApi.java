package com.bitlove.fetchat.model.api;

import com.bitlove.fetchat.model.pojos.Conversation;
import com.bitlove.fetchat.model.pojos.Me;
import com.bitlove.fetchat.model.pojos.Message;
import com.bitlove.fetchat.model.pojos.Token;

import java.util.List;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

public interface FetLifeApi {

    @POST("/api/oauth/token")
    //@QueryParam(name="constantVariable", value="constantValue")
    Call<Token> login(@Query("client_id") String clientId, @Query("client_secret") String clientSecret, @Query("redirect_uri") String redirectUrl, @Query("grant_type") String grantType, @Query("username") String username, @Query("password") String password);

    @GET("/api/v2/me")
    Call<Me> getMe(@Header("Authorization") String authHeader);

    @GET("/api/v2/me/conversations")
    Call<List<Conversation>> getConversations(@Header("Authorization") String authHeader);

    @GET("/api/v2/me/conversations/{conversationId}/messages")
    Call<List<Message>> getMessages(@Header("Authorization") String authHeader, @Path("conversationId") String conversationId);

    @POST("/api/v2/me/conversations/{conversationId}/messages")
    Call<Message> postMessage(@Header("Authorization") String authHeader, @Path("conversationId") String conversationId, @Query("body") String body);

}
