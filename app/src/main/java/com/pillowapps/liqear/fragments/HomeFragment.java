package com.pillowapps.liqear.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.michaelnovakjr.numberpicker.NumberPicker;
import com.pillowapps.liqear.LBApplication;
import com.pillowapps.liqear.R;
import com.pillowapps.liqear.activities.EqualizerActivity;
import com.pillowapps.liqear.activities.HomeActivity;
import com.pillowapps.liqear.activities.ImagePagerActivity;
import com.pillowapps.liqear.activities.MusicServiceManager;
import com.pillowapps.liqear.activities.PlaylistsActivity;
import com.pillowapps.liqear.activities.PreferencesActivity;
import com.pillowapps.liqear.activities.modes.VkAudioSearchActivity;
import com.pillowapps.liqear.activities.viewers.LastfmArtistViewerActivity;
import com.pillowapps.liqear.adapters.PlaylistItemsAdapter;
import com.pillowapps.liqear.audio.Timeline;
import com.pillowapps.liqear.callbacks.SimpleCallback;
import com.pillowapps.liqear.entities.MainActivityStartEnum;
import com.pillowapps.liqear.entities.Track;
import com.pillowapps.liqear.entities.events.ExitEvent;
import com.pillowapps.liqear.entities.events.ShowProgressEvent;
import com.pillowapps.liqear.helpers.AuthorizationInfoManager;
import com.pillowapps.liqear.helpers.Constants;
import com.pillowapps.liqear.helpers.ErrorNotifier;
import com.pillowapps.liqear.helpers.NetworkUtils;
import com.pillowapps.liqear.helpers.SharedPreferencesManager;
import com.pillowapps.liqear.helpers.StateManager;
import com.pillowapps.liqear.helpers.TrackUtils;
import com.pillowapps.liqear.helpers.home.HomePresenter;
import com.pillowapps.liqear.helpers.home.HomeView;
import com.pillowapps.liqear.models.LyricsModel;
import com.pillowapps.liqear.models.PlaylistModel;
import com.pillowapps.liqear.models.ShareModel;
import com.pillowapps.liqear.models.TrackModel;
import com.pillowapps.liqear.models.VideoModel;
import com.pillowapps.liqear.models.lastfm.LastfmLibraryModel;
import com.pillowapps.liqear.models.lastfm.LastfmTrackModel;
import com.pillowapps.liqear.models.vk.VkAudioModel;
import com.squareup.otto.Subscribe;

import java.util.List;

import javax.inject.Inject;

import dagger.Module;
import dagger.Provides;
import dagger.Subcomponent;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public abstract class HomeFragment extends BaseFragment implements HomeView {

    @Inject
    HomePresenter presenter;

    protected HomeActivity activity;

    protected ProgressBar progressBar;

    protected PlaylistItemsAdapter playlistItemsAdapter;

    protected Menu mainMenu;

    @Inject
    MusicServiceManager musicServiceManager;

    @Inject
    VkAudioModel vkAudioModel;
    @Inject
    LastfmTrackModel lastfmTrackModel;
    @Inject
    PlaylistModel playlistModel;
    @Inject
    StateManager stateManager;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LBApplication.get(getActivity()).applicationComponent().plus(new HomeFragmentModule()).inject(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        activity = (HomeActivity) getActivity();

        playlistItemsAdapter = new PlaylistItemsAdapter(activity);

        presenter.bindView(this);
        musicServiceManager.startService(activity, () -> {
            presenter.setMusicServiceConnected();
            activity.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        });

        LBApplication.BUS.register(this);
    }

    @Override
    public void onDestroy() {
        LBApplication.BUS.unregister(this);
        stateManager.savePlaylistState(musicServiceManager.getService());

        super.onDestroy();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Track currentTrack = Timeline.getInstance().getCurrentTrack();
        int itemId = item.getItemId();
        switch (itemId) {
            case android.R.id.home: {
//                activity.setHomeClicked(); todo in tablet version
//                if (toggle != null) toggle.onOptionsItemSelected(item);
            }
            return true;
            case R.id.photo_artist_button: {
                if (currentTrack == null || currentTrack.getArtist() == null) return true;
                if (!NetworkUtils.isOnline()) {
                    toast(R.string.no_internet);
                    return true;
                }
                Intent intent = new Intent(getActivity(), ImagePagerActivity.class);
                intent.putExtra(ImagePagerActivity.ARTIST, currentTrack.getArtist());
                startActivity(intent);
            }
            return true;
            case R.id.show_artist_button: {
                if (currentTrack == null || currentTrack.getArtist() == null) return true;
                if (!NetworkUtils.isOnline()) {
                    toast(R.string.no_internet);
                    return true;
                }
                Intent intent = new Intent(getActivity(), LastfmArtistViewerActivity.class);
                intent.putExtra(LastfmArtistViewerActivity.ARTIST, currentTrack.getArtist());
                startActivityForResult(intent, Constants.MAIN_REQUEST_CODE);
            }
            return true;
            case R.id.share_track_button: {
                if (currentTrack == null) return true;
                new ShareModel().showShareCurrentTrackDialog(getActivity());
            }
            return true;
            case R.id.next_url_button: {
                if (currentTrack == null) return true;
                if (Timeline.getInstance().getCurrentTrack() != null
                        && Timeline.getInstance().getCurrentTrack().isLocal()) {
                    toast(R.string.track_local);
                    return true;
                }
                if (!AuthorizationInfoManager.isAuthorizedOnVk()) {
                    toast(R.string.vk_not_authorized);
                    return true;
                }
                if (!NetworkUtils.isOnline()) {
                    toast(R.string.no_internet);
                    return true;
                }
                Intent intent = new Intent(getActivity(), VkAudioSearchActivity.class);
                intent.putExtra(Constants.TARGET, TrackUtils.getNotation(currentTrack));
                intent.putExtra(Constants.TYPE, 2);
                startActivityForResult(intent, Constants.VK_ADD_REQUEST_CODE);
            }
            return true;
            case R.id.lyrics_button: {
                if (currentTrack == null) return true;
                if (!AuthorizationInfoManager.isAuthorizedOnVk()) {
                    toast(R.string.vk_not_authorized);
                    return true;
                }
                if (!NetworkUtils.isOnline()) {
                    toast(R.string.no_internet);
                    return true;
                }
                new LyricsModel().openLyrics(getActivity(), currentTrack);
            }
            return true;
            case R.id.youtube_video_button: {
                if (currentTrack == null) return true;
                musicServiceManager.pause();
                new VideoModel().openVideo(getActivity(), currentTrack);
            }
            return true;
            case R.id.add_to_vk_button: {
                if (currentTrack == null) return true;
                if (!AuthorizationInfoManager.isAuthorizedOnVk()) {
                    toast(R.string.vk_not_authorized);
                    return true;
                }
                if (!NetworkUtils.isOnline()) {
                    toast(R.string.no_internet);
                    return true;
                }
                vkAudioModel.addToVk(getActivity(), currentTrack);
            }
            return true;
            case R.id.settings: {
                Intent preferencesIntent = new Intent(getActivity(), PreferencesActivity.class);
                startActivity(preferencesIntent);
            }
            return true;
            case R.id.equalizer: {
                Intent intent = new Intent(getActivity(), EqualizerActivity.class);
                startActivity(intent);
            }
            return true;
            case R.id.timer_button: {
                LayoutInflater inflater = (LayoutInflater) getActivity()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View layout = inflater.inflate(R.layout.seekbar_layout, null);
                final NumberPicker sb = (NumberPicker) layout.findViewById(R.id.minutes_picker);
                sb.setRange(1, 1440);
                int timerDefault = SharedPreferencesManager.getPreferences()
                        .getInt(Constants.TIMER_DEFAULT, 10);
                sb.setCurrent(timerDefault);

                final MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                        .title(R.string.timer)
                        .customView(layout, true)
                        .positiveText(android.R.string.ok)
                        .negativeText(android.R.string.cancel)
                        .build();

                dialog.getActionButton(DialogAction.POSITIVE).setOnClickListener(v -> {
                    musicServiceManager.setTimer(sb.getCurrent() * 60);
                    SharedPreferences.Editor editor =
                            SharedPreferencesManager.getPreferences().edit();
                    editor.putInt(Constants.TIMER_DEFAULT, sb.getCurrent());
                    editor.apply();
                });
                dialog.getActionButton(DialogAction.NEGATIVE).setOnClickListener(v -> {
                    musicServiceManager.setTimer(0);
                    dialog.dismiss();
                });
                dialog.show();
            }
            return true;
            case R.id.exit_button: {
                exit();
            }
            return true;
            case R.id.playlists_button: {
                Intent intent = new Intent(getActivity(), PlaylistsActivity.class);
                intent.putExtra(PlaylistsActivity.AIM, PlaylistsActivity.Aim.SHOW_PLAYLISTS);
                startActivityForResult(intent, Constants.MAIN_REQUEST_CODE);
            }
            return true;
            case R.id.clean_titles: {
                List<Track> playlistTracks = Timeline.getInstance().getPlaylistTracks();
                new TrackModel().clearTitles(playlistTracks);
                updateAdapter();
            }
            return true;
//            case R.id.shuffle_button: {
//                shufflePlaylist();
//            }
//            return true;
//            case R.id.playlist_edit_mode_button: {
//                boolean editMode = !playlistItemsAdapter.isEditMode();
//                playlistItemsAdapter.setEditMode(editMode);
//            }
//            return true;
//            case R.id.fixate_search_result_button: {
//                fixateSearchResult();
//            }
//            return true;
//            case R.id.find_current_button: {
//                findCurrentTrack();
//            }
//            return true;
//            case R.id.sort_by_artist_button: {
//                sortByArtist();
//            }
//            return true;
//            case R.id.edit_modes_button: {
//                ModeItemsHelper.setEditMode(!ModeItemsHelper.isEditMode());
//                if (!isTablet()) {
//                    modeAdapter.notifyChanges();
//                } else {
////                    if (!landscapeTablet) showMenu();todo
//                    modeListFragment.getAdapter().notifyChanges();
//                }
//            }
//            return true;
//            case R.id.menu_search: {
//                SharedPreferences savePreferences = SharedPreferencesManager.getSavePreferences();
//                boolean visibility = !savePreferences.getBoolean(
//                        Constants.SEARCH_PLAYLIST_VISIBILITY, false);
//                savePreferences.edit().putBoolean(
//                        Constants.SEARCH_PLAYLIST_VISIBILITY, visibility).apply();
//                updateSearchVisibility();
//            }
//            return true; todo
            default:
                return false;
        }
    }

    private void exit() {
        musicServiceManager.stopService(activity);
        activity.finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) return;

        if (requestCode == Constants.MAIN_REQUEST_CODE) {
            MainActivityStartEnum mainActivityStartEnum = (MainActivityStartEnum) data.getSerializableExtra(Constants.ACTION_ENUM);
            if (mainActivityStartEnum != null) {
                switch (mainActivityStartEnum) {
                    case PLAY_TRACKS: {
                        if (playlistItemsAdapter.isEditMode())
                            playlistItemsAdapter.setEditMode(false);
                        int positionToPlay = data.getIntExtra(Constants.POSITION_TO_PLAY, 0);
                        changeViewPagerItem(0);
                        updateAdapter();
                        changePlaylist(positionToPlay, true);
                    }
                    break;
                    case UPDATE_ADAPTER: {
                        updateAdapter();
                    }
                    break;
                    default:
                        break;
                }
            }

        } else if (requestCode == Constants.VK_ADD_REQUEST_CODE) { //todo somewhere else
            long aid = data.getLongExtra("aid", -1);
            long oid = data.getLongExtra("oid", -1);
            if (aid == -1 || oid == -1) {
//                musicService.changeUrl(data.getIntExtra("position", 0));
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void showLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
    }

    @Override
    public void updateAdapter() {
        List<Track> playlist = Timeline.getInstance().getPlaylistTracks();
        playlistItemsAdapter.setValues(playlist);
        updateEmptyPlaylistTextView();
    }

    @Override
    public void changePlaylist(int index, boolean autoPlay) {
        Timeline.getInstance().clearQueue();
        Timeline.getInstance().updateRealTrackPositions();
        Timeline.getInstance().clearPreviousIndexes();
        musicServiceManager.pause();
        List<Track> tracks = Timeline.getInstance().getPlaylistTracks();
        playlistItemsAdapter.setValues(tracks);
        if (tracks.size() > 0) {
            if (autoPlay) {
                presenter.playTrack(index);
            }
            playlistModel.saveMainPlaylist(getActivity(), Timeline.getInstance().getPlaylist())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Subscriber<Long>() {
                        @Override
                        public void onCompleted() {
                            Timber.d("saving completed");
                        }

                        @Override
                        public void onError(Throwable e) {
                            Timber.d("saving error " + e.toString());
                            e.printStackTrace();
                        }

                        @Override
                        public void onNext(Long aLong) {
                            Timber.d("saving onNext " + aLong);
                        }
                    });
        }
        updateEmptyPlaylistTextView();
    }

    @Override
    public void showError(String errorMessage) {
        ErrorNotifier.showError(activity, errorMessage);
    }

    @Override
    public abstract void updateEmptyPlaylistTextView();

    public void openRadiomix() {
        presenter.openRadiomix();
    }

    public void openLibrary() {
        presenter.openLibrary();
    }

    @Subscribe
    public void exitEvent(ExitEvent event) {
        exit();
    }

    @Subscribe
    public void showProgressEvent(ShowProgressEvent event) {
    }

    public void openDropButton() {
        if (mainMenu == null) return;
        mainMenu.performIdentifierAction(R.id.track_button, 0);
    }

    public void toggleLoveCurrentTrack() {
        if (!AuthorizationInfoManager.isAuthorizedOnLastfm()) {
            toast(R.string.last_fm_not_authorized);
            return;
        }
        if (!NetworkUtils.isOnline()) {
            toast(R.string.no_internet);
            return;
        }
        showLoading(true);
        final Track track = Timeline.getInstance().getCurrentTrack();
        if (!track.isLoved()) {
            lastfmTrackModel.love(track, new SimpleCallback<Object>() {
                @Override
                public void success(Object data) {
                    track.setLoved(true);
                    updateLoveButton();
                    showLoading(false);
                }

                @Override
                public void failure(String errorMessage) {
                    showLoading(false);

                }
            });
        } else {
            lastfmTrackModel.unlove(track, new SimpleCallback<Object>() {
                @Override
                public void success(Object o) {
                    showLoading(false);
                    track.setLoved(false);
                    updateLoveButton();
                }

                @Override
                public void failure(String error) {
                    showLoading(false);
                }
            });
        }
    }

    public void updateLoveButton() {
        // No operations.
    }

    @Subcomponent(modules = HomeFragmentModule.class)
    public interface HomeFragmentComponent {
        void inject(@NonNull HomeFragment itemsFragment);
    }

    @Module
    public static class HomeFragmentModule {

        @Provides
        @NonNull
        public HomePresenter provideHomePresenter(@NonNull LastfmLibraryModel libraryModel) {
            return new HomePresenter(libraryModel);
        }
    }
}
