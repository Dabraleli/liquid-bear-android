package com.pillowapps.liqear.entities.lastfm.roots;

import com.google.gson.annotations.SerializedName;
import com.pillowapps.liqear.entities.LastfmResponse;
import com.pillowapps.liqear.entities.lastfm.LastfmArtist;

public class LastfmArtistRoot extends LastfmResponse {
    @SerializedName("artist")
    LastfmArtist artist;

    public LastfmArtistRoot() {
    }

    public LastfmArtist getArtist() {
        return artist;
    }

    @Override
    public String toString() {
        return "LastfmArtistRoot{" +
                "artist=" + artist +
                '}';
    }
}