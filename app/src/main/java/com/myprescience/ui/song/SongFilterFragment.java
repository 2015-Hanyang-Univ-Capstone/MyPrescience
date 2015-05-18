package com.myprescience.ui.song;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.myprescience.R;

import java.util.ArrayList;

import static com.myprescience.util.Server.ACOUSTIC_MODE;
import static com.myprescience.util.Server.BILLBOARDHOT_MODE;
import static com.myprescience.util.Server.DANCABILITY_MODE;
import static com.myprescience.util.Server.ENERGY_MODE;
import static com.myprescience.util.Server.Genre_CLUB_MODE;
import static com.myprescience.util.Server.Genre_COUNTRY_MODE;
import static com.myprescience.util.Server.Genre_ELECTRONIC_MODE;
import static com.myprescience.util.Server.Genre_HIPHOP_MODE;
import static com.myprescience.util.Server.Genre_JAZZ_MODE;
import static com.myprescience.util.Server.Genre_POP_MODE;
import static com.myprescience.util.Server.Genre_ROCK_MODE;
import static com.myprescience.util.Server.Genre_RnB_MODE;
import static com.myprescience.util.Server.INSTRUMENTAL_MODE;
import static com.myprescience.util.Server.KPOP_MODE;
import static com.myprescience.util.Server.LIVENESS_MODE;
import static com.myprescience.util.Server.LOUDNESS_MODE;
import static com.myprescience.util.Server.MODE;
import static com.myprescience.util.Server.POP_MODE;
import static com.myprescience.util.Server.RANK_ORDER_MODE;
import static com.myprescience.util.Server.SPEECHINESS_MODE;
import static com.myprescience.util.Server.TODAY_SONG_MODE;
import static com.myprescience.util.Server.VALANCE_MODE;

/**
 * Created by paveld on 4/17/14.
 */
public class SongFilterFragment extends Fragment {

    private Button mRandomSongFilterButton, mKpopSongFilterButton, mPopSongFilterButton, mBillboardHot100SongFilterButton,
                    mGenrePopFilterButton, mGenreHiphopFilterButton, mGenreRnBFilterButton, mGenreRockFilterButton,
                    mGenreCountryFilterButton, mGenreElectronicFilterButton, mGenreJazzFilterButton, mGenreClubFilterButton,
                    mValenceSongFilterButton, mLoudnessSongFilterButton, mDancabilitySongFilterButton, mEnergySongFilterButton,
                    mLivenessSongFilterButton, mSpeechinessSongFilterButton, mAcousticSongFilterButton, minstrumentalnessSongFilterButton,
                    mMypRankOrder_SongFilter, mClickedButton;
    private OnFilterSelectedListener mCallback;
    private int Clicked;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View filter_view = inflater.inflate(R.layout.fragment_song_fliter, container, false);

        mRandomSongFilterButton = (Button) filter_view.findViewById(R.id.random_SongFilter);
        mKpopSongFilterButton = (Button) filter_view.findViewById(R.id.kpop_SongFilter);
        mPopSongFilterButton = (Button) filter_view.findViewById(R.id.pop_SongFilter);
        mBillboardHot100SongFilterButton = (Button) filter_view.findViewById(R.id.billboardHot100_SongFilter);

        mGenrePopFilterButton = (Button) filter_view.findViewById(R.id.genrePop_SongFilter);
        mGenreHiphopFilterButton = (Button) filter_view.findViewById(R.id.genreHiphop_SongFilter);
        mGenreRnBFilterButton = (Button) filter_view.findViewById(R.id.genreRnB_SongFilter);
        mGenreRockFilterButton = (Button) filter_view.findViewById(R.id.genreRock_SongFilter);
        mGenreCountryFilterButton = (Button) filter_view.findViewById(R.id.genreCountry_SongFilter);
        mGenreElectronicFilterButton = (Button) filter_view.findViewById(R.id.genreElectronic_SongFilter);
        mGenreJazzFilterButton = (Button) filter_view.findViewById(R.id.genreJazz_SongFilter);
        mGenreClubFilterButton = (Button) filter_view.findViewById(R.id.genreClub_SongFilter);

        mValenceSongFilterButton = (Button) filter_view.findViewById(R.id.valance_SongFilter);
        mLoudnessSongFilterButton = (Button) filter_view.findViewById(R.id.loudness_SongFilter);
        mDancabilitySongFilterButton = (Button) filter_view.findViewById(R.id.danceablility_SongFilter);
        mEnergySongFilterButton = (Button) filter_view.findViewById(R.id.energy_SongFilter);
        mLivenessSongFilterButton = (Button) filter_view.findViewById(R.id.live_SongFilter);
        mSpeechinessSongFilterButton = (Button) filter_view.findViewById(R.id.speechiness_SongFilter);
        mAcousticSongFilterButton = (Button) filter_view.findViewById(R.id.acousticness_SongFilter);
        minstrumentalnessSongFilterButton = (Button) filter_view.findViewById(R.id.instrumentalness_SongFilter);
        mMypRankOrder_SongFilter = (Button) filter_view.findViewById(R.id.MypRankOrder_SongFilter);

        ArrayList<Button> button_list = new ArrayList<Button>(30);
        ArrayList<Integer> button_num = new ArrayList<Integer>(30);
        button_list.add(mRandomSongFilterButton);               button_num.add(TODAY_SONG_MODE);
        button_list.add(mKpopSongFilterButton);                 button_num.add(KPOP_MODE);
        button_list.add(mPopSongFilterButton);                  button_num.add(POP_MODE);
        button_list.add(mBillboardHot100SongFilterButton);      button_num.add(BILLBOARDHOT_MODE);
        button_list.add(mGenrePopFilterButton);                 button_num.add(Genre_POP_MODE);
        button_list.add(mGenreHiphopFilterButton);              button_num.add(Genre_HIPHOP_MODE);
        button_list.add(mGenreRnBFilterButton);                 button_num.add(Genre_RnB_MODE);
        button_list.add(mGenreRockFilterButton);                button_num.add(Genre_ROCK_MODE);
        button_list.add(mGenreCountryFilterButton);             button_num.add(Genre_COUNTRY_MODE);
        button_list.add(mGenreElectronicFilterButton);          button_num.add(Genre_ELECTRONIC_MODE);
        button_list.add(mGenreJazzFilterButton);                button_num.add(Genre_JAZZ_MODE);
        button_list.add(mGenreClubFilterButton);                button_num.add(Genre_CLUB_MODE);
        button_list.add(mValenceSongFilterButton);              button_num.add(VALANCE_MODE);
        button_list.add(mLoudnessSongFilterButton);             button_num.add(LOUDNESS_MODE);
        button_list.add(mDancabilitySongFilterButton);          button_num.add(DANCABILITY_MODE);
        button_list.add(mEnergySongFilterButton);               button_num.add(ENERGY_MODE);
        button_list.add(mLivenessSongFilterButton);             button_num.add(LIVENESS_MODE);
        button_list.add(mSpeechinessSongFilterButton);          button_num.add(SPEECHINESS_MODE);
        button_list.add(mAcousticSongFilterButton);             button_num.add(ACOUSTIC_MODE);
        button_list.add(minstrumentalnessSongFilterButton);     button_num.add(INSTRUMENTAL_MODE);
        button_list.add(mMypRankOrder_SongFilter);              button_num.add(RANK_ORDER_MODE);

        for(int i = 0; i < button_list.size(); i++) {
            final int buttonMode = button_num.get(i);
            Button button = button_list.get(i);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Clicked != buttonMode) {
                        nonAtiveButton(mClickedButton);
                        Clicked = buttonMode;
                        activeButton((Button) v);
                    }
                }
            });
        }
        initButtonUI();

        return filter_view;
    }

    public void initButtonUI() {
        switch(MODE) {
            case 0 : activeButtonUI(mRandomSongFilterButton);
                Clicked = TODAY_SONG_MODE;
                break;
            case 2 : activeButtonUI(mKpopSongFilterButton);
                Clicked = KPOP_MODE;
                break;
            case 3 : activeButtonUI(mPopSongFilterButton);
                Clicked = POP_MODE;
                break;
            case 4 : activeButtonUI(mBillboardHot100SongFilterButton);
                Clicked = BILLBOARDHOT_MODE;
                break;


            case 41 : activeButtonUI(mGenrePopFilterButton);
                Clicked = Genre_POP_MODE;
                break;
            case 42 : activeButtonUI(mGenreHiphopFilterButton);
                Clicked = Genre_HIPHOP_MODE;
                break;
            case 43 : activeButtonUI(mGenreRnBFilterButton);
                Clicked = Genre_RnB_MODE;
                break;
            case 44 : activeButtonUI(mGenreRockFilterButton);
                Clicked = Genre_ROCK_MODE;
                break;
            case 45 : activeButtonUI(mGenreCountryFilterButton);
                Clicked = Genre_COUNTRY_MODE;
                break;
            case 46 : activeButtonUI(mGenreElectronicFilterButton);
                Clicked = Genre_ELECTRONIC_MODE;
                break;
            case 47 : activeButtonUI(mGenreJazzFilterButton);
                Clicked = Genre_JAZZ_MODE;
                break;
            case 48 : activeButtonUI(mGenreClubFilterButton);
                Clicked = Genre_CLUB_MODE;
                break;


            case 101 : activeButtonUI(mValenceSongFilterButton);
                Clicked = VALANCE_MODE;
                break;
            case 102 : activeButtonUI(mLoudnessSongFilterButton);
                Clicked = LOUDNESS_MODE;
                break;
            case 103 : activeButtonUI(mDancabilitySongFilterButton);
                Clicked = DANCABILITY_MODE;
                break;
            case 104 : activeButtonUI(mEnergySongFilterButton);
                Clicked = ENERGY_MODE;
                break;
            case 105 : activeButtonUI(mLivenessSongFilterButton);
                Clicked = LIVENESS_MODE;
                break;
            case 106 : activeButtonUI(mSpeechinessSongFilterButton);
                Clicked = SPEECHINESS_MODE;
                break;
            case 107 : activeButtonUI(mAcousticSongFilterButton);
                Clicked = ACOUSTIC_MODE;
                break;
            case 108 : activeButtonUI(minstrumentalnessSongFilterButton);
                Clicked = INSTRUMENTAL_MODE;
                break;
            case 109 : activeButtonUI(mMypRankOrder_SongFilter);
                Clicked = RANK_ORDER_MODE;
                break;
        }
    }

    public void activeButtonUI(Button clickedButton) {
        clickedButton.setTextColor(getResources().getColor(R.color.WhiteSmoke));
        clickedButton.setBackgroundResource(R.drawable.button_active_background);
        mClickedButton = clickedButton;
    }

    public void activeButton(Button clickedButton) {
        activeButtonUI(clickedButton);
        mCallback.onFilterSelected(Clicked);
    }

    public void nonAtiveButton(Button clickedButton) {
        clickedButton.setBackgroundResource(R.drawable.button_background);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public interface OnFilterSelectedListener {
        public void onFilterSelected(int position);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mCallback = (OnFilterSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()+ " must implement OnHeadlineSelectedListener");
        }
    }
}
