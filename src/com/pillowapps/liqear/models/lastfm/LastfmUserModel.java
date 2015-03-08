package com.pillowapps.liqear.models.lastfm;

import com.pillowapps.liqear.entities.lastfm.LastfmArtist;
import com.pillowapps.liqear.entities.lastfm.LastfmTrack;
import com.pillowapps.liqear.entities.lastfm.LastfmUser;
import com.pillowapps.liqear.entities.lastfm.roots.LastfmFriendsRoot;
import com.pillowapps.liqear.entities.lastfm.roots.LastfmLovedTracksRoot;
import com.pillowapps.liqear.entities.lastfm.roots.LastfmNeighboursRoot;
import com.pillowapps.liqear.entities.lastfm.roots.LastfmRecentTracksRoot;
import com.pillowapps.liqear.entities.lastfm.roots.LastfmRecommendationsArtistRoot;
import com.pillowapps.liqear.entities.lastfm.roots.LastfmTopArtistsRoot;
import com.pillowapps.liqear.entities.lastfm.roots.LastfmTopTracksRoot;
import com.pillowapps.liqear.helpers.AuthorizationInfoManager;
import com.pillowapps.liqear.helpers.Converter;
import com.pillowapps.liqear.helpers.LastfmApiHelper;
import com.pillowapps.liqear.network.ServiceHelper;
import com.pillowapps.liqear.network.callbacks.LastfmCallback;
import com.pillowapps.liqear.network.callbacks.LastfmSimpleCallback;
import com.pillowapps.liqear.network.service.LastfmApiService;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class LastfmUserModel {
    private LastfmApiService lastfmService = ServiceHelper.getLastfmService();
    private LastfmApiHelper apiHelper = new LastfmApiHelper();

    public void getUserTopArtists(String userName, String period, int limit, int page,
                                  final LastfmSimpleCallback<List<LastfmArtist>> callback) {
        lastfmService.getUserTopArtists(userName,
                period,
                limit,
                page,
                new LastfmCallback<LastfmTopArtistsRoot>() {
                    @Override
                    public void success(LastfmTopArtistsRoot data) {
                        callback.success(data.getArtists().getArtists());
                    }

                    @Override
                    public void failure(String error) {
                        callback.failure(error);
                    }
                });
    }

    public void getUserRecommendedArtists(int limit, int page,
                                          final LastfmSimpleCallback<List<LastfmArtist>> callback) {
        String sessionKey = AuthorizationInfoManager.getLastfmKey();

        Map<String, String> params = new TreeMap<>();
        params.put("limit", String.valueOf(limit));
        params.put("page", String.valueOf(page));
        params.put("method", "user.getRecommendedArtists");
        params.put("sk", sessionKey);
        lastfmService.getRecommendedArtists(
                limit,
                page,
                apiHelper.generateApiSig(params),
                sessionKey,
                new LastfmCallback<LastfmRecommendationsArtistRoot>() {
                    @Override
                    public void success(LastfmRecommendationsArtistRoot data) {
                        callback.success(data.getRecommendations().getArtists());
                    }

                    @Override
                    public void failure(String error) {
                        callback.failure(error);
                    }
                });
    }

    public void getUserRecentTracks(String userName, int limit, int page,
                                    final LastfmSimpleCallback<List<LastfmTrack>> callback) {
        lastfmService.getRecentTracks(
                userName,
                limit,
                page,
                new LastfmCallback<LastfmRecentTracksRoot>() {
                    @Override
                    public void success(LastfmRecentTracksRoot data) {
                        callback.success(Converter.convertLastfmTracksArtistStruct(data.getTracks().getTracks()));
                    }

                    @Override
                    public void failure(String error) {
                        callback.failure(error);
                    }
                }
        );
    }

    public void getUserTopTracks(String userName, String period, int limit, int page,
                                 final LastfmSimpleCallback<List<LastfmTrack>> callback) {

        lastfmService.getUserTopTracks(
                userName,
                period,
                limit,
                page,
                new LastfmCallback<LastfmTopTracksRoot>() {
                    @Override
                    public void success(LastfmTopTracksRoot data) {
                        callback.success(data.getTracks().getTracks());

                    }

                    @Override
                    public void failure(String error) {
                        callback.failure(error);
                    }
                }
        );
    }


    public void getLovedTracks(String userName, int limit, int page,
                               final LastfmSimpleCallback<List<LastfmTrack>> callback) {
        lastfmService.getLovedTracks(
                userName,
                limit,
                page,
                new LastfmCallback<LastfmLovedTracksRoot>() {
                    @Override
                    public void success(LastfmLovedTracksRoot data) {
                        callback.success(data.getTracks().getTracks());
                    }

                    @Override
                    public void failure(String error) {
                        callback.failure(error);
                    }
                }
        );
    }

    public void getLastfmFriends(String username, int limit, int page,
                                 final LastfmSimpleCallback<List<LastfmUser>> callback) {
        lastfmService.getFriends(username, limit, page, new LastfmCallback<LastfmFriendsRoot>() {
            @Override
            public void success(LastfmFriendsRoot data) {
                callback.success(data.getUsers().getUsers());
            }

            @Override
            public void failure(String error) {
                callback.failure(error);
            }
        });
    }

    public void getNeighbours(String username, int limit,
                              final LastfmSimpleCallback<List<LastfmUser>> callback) {
        lastfmService.getNeighbours(username, limit, new LastfmCallback<LastfmNeighboursRoot>() {
            @Override
            public void success(LastfmNeighboursRoot data) {
                callback.success(data.getUsers().getUsers());
            }

            @Override
            public void failure(String error) {
                callback.failure(error);
            }
        });
    }

    public void getWeeklyTracksChart(String user, int count, final LastfmSimpleCallback<List<LastfmTrack>> callback) {
        lastfmService.getWeeklyTracksChart(user, count);
    }
}
