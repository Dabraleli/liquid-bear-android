package com.pillowapps.liqear.connection;

import com.pillowapps.liqear.models.lastfm.LastfmAlbum;
import com.pillowapps.liqear.models.vk.VkAlbumsResponseRoot;
import com.pillowapps.liqear.models.vk.VkGroupsResponseRoot;
import com.pillowapps.liqear.models.vk.VkResponse;
import com.pillowapps.liqear.models.vk.VkTrack;
import com.pillowapps.liqear.models.vk.VkTracksResponseRoot;
import com.pillowapps.liqear.models.vk.VkUser;
import com.pillowapps.liqear.models.vk.VkWallMessagesResponseRoot;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

public interface VkApiService {

    @GET("/wall.get")
    public void getWallMessages(@Query("owner_id") long ownerId,
                                @Query("offset") int offset,
                                @Query("count") int count,
                                Callback<VkWallMessagesResponseRoot> callback);

    @GET("/fave.getPosts")
    public void getFavoriteWallMessages(@Query("offset") int offset,
                                        @Query("count") int count,
                                        Callback<VkWallMessagesResponseRoot> callback);

    @GET("/newsfeed.get")
    public void getNewsfeedWallMessages(@Query("offset") int offset,
                                        @Query("count") int count,
                                        Callback<VkWallMessagesResponseRoot> callback);

    @POST("/wall.post")
    public void postWallMessages(@Query("message") String message,
                                 @Query("attachment") String attachment,
                                 Callback<VkResponse> callback);

    @GET("/audio.get")
    public void getAudio(@Query("owner_id") long ownerId,
                         @Query("count") int count,
                         @Query("offset") int offset,
                         Callback<VkTracksResponseRoot> callback);

    @GET("/audio.get")
    public void getGroupAudio(@Query("gid") long groupId,
                              @Query("count") int count,
                              @Query("offset") int offset,
                              Callback<VkTracksResponseRoot> callback);

    @GET("/audio.get")
    public void getAudio(@Query("owner_id") String ownerId,
                         @Query("album_id") String albumId,
                         @Query("count") int count,
                         @Query("offset") int offset,
                         Callback<VkTracksResponseRoot> callback);

    @GET("/audio.search")
    public void searchAudio(@Query("q") String q,
                            @Query("offset") int offset,
                            @Query("count") int count,
                            Callback<VkTracksResponseRoot> callback);

    @GET("/users.get")
    public void getUser(@Query("user_ids") long uid,
                        @Query("fields") String fields,
                        Callback<List<VkUser>> callback);

    @POST("/status.set")
    public void setAudioStatus(@Query("audio") String audio,
                               Callback<VkResponse> callback);

    @POST("/execute.saps")
    public void setAudioStatusWithSearch(@Query("q") String searchQuery,
                                         Callback<VkResponse> callback);

    @POST("/execute.ta")
    public void addAudioWithSearch(@Query("q") String searchQuery,
                                   Callback<VkResponse> callback);

    @GET("/groups.get")
    public void getGroups(@Query("extended") int extended,
                          @Query("offset") int offset,
                          @Query("count") int count,
                          Callback<VkGroupsResponseRoot> callback);

    @GET("/audio.getAlbums")
    public void getAlbums(@Query("owner_id") long ownerId,
                          @Query("offset") int offset,
                          @Query("count") int count,
                          Callback<VkAlbumsResponseRoot> callback);

    @GET("/audio.getRecommendations")
    public void getRecommendations(@Query("offset") int offset,
                                   @Query("count") int count,
                                   Callback<List<VkTrack>> callback);

    @GET("/friends.get")
    public void getFriends(@Query("user_id") long userId,
                           @Query("fields") String fields,
                           @Query("offset") int offset,
                           @Query("count") int count,
                           Callback<List<LastfmAlbum>> callback);

    @GET("/execute.u")
    public void getTrackUrl(@Query("q") String trackNotation,
                            @Query("n") String offset,
                            Callback<String> callback);

    @GET("/execute.getLyrics")
    public void getLyrics(@Query("q") String trackNotation,
                          @Query("aim") String offset,
                          Callback<String> callback);

    @POST("/photos.saveWallPhoto")
    public void saveWallPhoto(@Query("server") String server,
                              @Query("photo") String photo,
                              @Query("hash") String hash,
                              Callback<VkResponse> callback);

    @GET("/photos.getWallUploadServer")
    public void getWallUploadServer(Callback<VkResponse> callback);

    @POST("/status.set")
    public void updateStatus(@Query("audio") String audioString,
                             Callback<VkResponse> callback);

    @POST("/audio.add")
    void addAudio(@Query("audio_id") long audioId,
                  @Query("owner_id") long ownerId,
                  Callback<VkResponse> callback);

    @POST("/execute.ta")
    void addAudioFast(@Query("q") String q,
                      Callback<VkResponse> callback);
}
