package com.pillowapps.liqear.models;

import com.pillowapps.liqear.entities.lastfm.LastfmArtist;
import com.pillowapps.liqear.entities.lastfm.LastfmTrack;
import com.pillowapps.liqear.entities.lastfm.roots.LastfmArtistsRoot;
import com.pillowapps.liqear.entities.lastfm.roots.LastfmTracksRoot;
import com.pillowapps.liqear.network.callbacks.LastfmCallback;
import com.pillowapps.liqear.network.callbacks.LastfmSimpleCallback;
import com.pillowapps.liqear.network.ServiceHelper;
import com.pillowapps.liqear.network.service.LastfmApiService;

import java.util.List;

public class LastfmChartModel {
    private LastfmApiService lastfmService = ServiceHelper.getLastfmService();

    public void getHypedArtists(int limit, int page, final LastfmSimpleCallback<List<LastfmArtist>> callback) {
        lastfmService.getChartHypedArtists(limit, page, new LastfmCallback<LastfmArtistsRoot>() {
            @Override
            public void success(LastfmArtistsRoot data) {
                callback.success(data.getArtists().getArtists());
            }

            @Override
            public void failure(String error) {
                callback.failure(error);
            }
        });
    }

    public void getTopArtists(int limit, int page, final LastfmSimpleCallback<List<LastfmArtist>> callback) {
        lastfmService.getChartTopArtists(limit, page, new LastfmCallback<LastfmArtistsRoot>() {
            @Override
            public void success(LastfmArtistsRoot data) {
                callback.success(data.getArtists().getArtists());
            }

            @Override
            public void failure(String error) {
                callback.failure(error);
            }
        });
    }

    public void getLovedTracksChart(int limit, int page,
                                    final LastfmSimpleCallback<List<LastfmTrack>> callback) {
        lastfmService.getChartLovedTracks(limit, page, new LastfmCallback<LastfmTracksRoot>() {
            @Override
            public void success(LastfmTracksRoot data) {
                callback.success(data.getTracks().getTracks());
            }

            @Override
            public void failure(String error) {
                callback.failure(error);
            }
        });
    }

    public void getTopTracksChart(int limit, int page, final LastfmSimpleCallback<List<LastfmTrack>> callback) {
        lastfmService.getChartTopTracks(limit, page, new LastfmCallback<LastfmTracksRoot>() {
            @Override
            public void success(LastfmTracksRoot data) {
                callback.success(data.getTracks().getTracks());
            }

            @Override
            public void failure(String error) {
                callback.failure(error);
            }
        });
    }

    public void getHypedTracks(int limit, int page, final LastfmSimpleCallback<List<LastfmTrack>> callback) {
        lastfmService.getChartHypedTracks(limit, page, new LastfmCallback<LastfmTracksRoot>() {
            @Override
            public void success(LastfmTracksRoot data) {
                callback.success(data.getTracks().getTracks());

            }

            @Override
            public void failure(String error) {
                callback.failure(error);

            }
        });
    }
}
