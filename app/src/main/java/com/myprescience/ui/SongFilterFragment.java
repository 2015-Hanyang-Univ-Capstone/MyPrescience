package com.myprescience.ui;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.myprescience.R;

import static com.myprescience.util.Server.ACOUSTIC_MODE;
import static com.myprescience.util.Server.BILLBOARDHOT_MODE;
import static com.myprescience.util.Server.DANCABILITY_MODE;
import static com.myprescience.util.Server.ENERGY_MODE;
import static com.myprescience.util.Server.INSTRUMENTAL_MODE;
import static com.myprescience.util.Server.KPOP_MODE;
import static com.myprescience.util.Server.LIVENESS_MODE;
import static com.myprescience.util.Server.LOUDNESS_MODE;
import static com.myprescience.util.Server.MYP_RANK_SONGS;
import static com.myprescience.util.Server.POP_MODE;
import static com.myprescience.util.Server.RANDOM_MODE;
import static com.myprescience.util.Server.MODE;
import static com.myprescience.util.Server.RANK_ORDER_MODE;
import static com.myprescience.util.Server.SPEECHINESS_MODE;
import static com.myprescience.util.Server.VALANCE_MODE;

/**
 * Created by paveld on 4/17/14.
 */
public class SongFilterFragment extends Fragment {

    private Button mRandomSongFilterButton, mKpopSongFilterButton, mPopSongFilterButton, mBillboardHot100SongFilterButton, mClickedButton,
                    mValenceSongFilterButton, mLoudnessSongFilterButton, mDancabilitySongFilterButton, mEnergySongFilterButton,
                    mLivenessSongFilterButton, mSpeechinessSongFilterButton, mAcousticSongFilterButton, minstrumentalnessSongFilterButton,
                    mMypRankOrder_SongFilter;
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
        mValenceSongFilterButton = (Button) filter_view.findViewById(R.id.valance_SongFilter);
        mLoudnessSongFilterButton = (Button) filter_view.findViewById(R.id.loudness_SongFilter);
        mDancabilitySongFilterButton = (Button) filter_view.findViewById(R.id.danceablility_SongFilter);
        mEnergySongFilterButton = (Button) filter_view.findViewById(R.id.energy_SongFilter);
        mLivenessSongFilterButton = (Button) filter_view.findViewById(R.id.live_SongFilter);
        mSpeechinessSongFilterButton = (Button) filter_view.findViewById(R.id.speechiness_SongFilter);
        mAcousticSongFilterButton = (Button) filter_view.findViewById(R.id.acousticness_SongFilter);
        minstrumentalnessSongFilterButton = (Button) filter_view.findViewById(R.id.instrumentalness_SongFilter);
        mMypRankOrder_SongFilter = (Button) filter_view.findViewById(R.id.MypRankOrder_SongFilter);

        mClickedButton = mRandomSongFilterButton;

        switch(MODE) {
            case 0 : activeButtonUI(mRandomSongFilterButton);
                     Clicked = RANDOM_MODE;
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
            case 5 : activeButtonUI(mValenceSongFilterButton);
                     Clicked = VALANCE_MODE;
                     break;
            case 6 : activeButtonUI(mLoudnessSongFilterButton);
                     Clicked = LOUDNESS_MODE;
                     break;
            case 7 : activeButtonUI(mDancabilitySongFilterButton);
                     Clicked = DANCABILITY_MODE;
                     break;
            case 8 : activeButtonUI(mEnergySongFilterButton);
                     Clicked = ENERGY_MODE;
                     break;
            case 9 : activeButtonUI(mLivenessSongFilterButton);
                     Clicked = LIVENESS_MODE;
                     break;
            case 10 : activeButtonUI(mSpeechinessSongFilterButton);
                     Clicked = SPEECHINESS_MODE;
                     break;
            case 11 : activeButtonUI(mAcousticSongFilterButton);
                     Clicked = ACOUSTIC_MODE;
                     break;
            case 12 : activeButtonUI(minstrumentalnessSongFilterButton);
                     Clicked = INSTRUMENTAL_MODE;
                     break;
            case 13 : activeButtonUI(mMypRankOrder_SongFilter);
                     Clicked = RANK_ORDER_MODE;
                     break;
        }

        mRandomSongFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Clicked != RANDOM_MODE) {
                    nonAtiveButton(mClickedButton);
                    Clicked = RANDOM_MODE;
                    activeButton((Button) v);
                }
            }
        });

        mKpopSongFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Clicked != KPOP_MODE) {
                    nonAtiveButton(mClickedButton);
                    Clicked = KPOP_MODE;
                    activeButton((Button) v);
                }
            }
        });

        mPopSongFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Clicked != POP_MODE) {
                    nonAtiveButton(mClickedButton);
                    Clicked = POP_MODE;
                    activeButton((Button) v);
                }
            }
        });

        mBillboardHot100SongFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Clicked != BILLBOARDHOT_MODE) {
                    nonAtiveButton(mClickedButton);
                    Clicked = BILLBOARDHOT_MODE;
                    activeButton((Button) v);
                }
            }
        });

        mValenceSongFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Clicked != VALANCE_MODE) {
                    nonAtiveButton(mClickedButton);
                    Clicked = VALANCE_MODE;
                    activeButton((Button) v);
                }
            }
        });

        mLoudnessSongFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Clicked != LOUDNESS_MODE) {
                    nonAtiveButton(mClickedButton);
                    Clicked = LOUDNESS_MODE;
                    activeButton((Button) v);
                }
            }
        });

        mDancabilitySongFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Clicked != DANCABILITY_MODE) {
                    nonAtiveButton(mClickedButton);
                    Clicked = DANCABILITY_MODE;
                    activeButton((Button) v);
                }
            }
        });

        mEnergySongFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Clicked != ENERGY_MODE) {
                    nonAtiveButton(mClickedButton);
                    Clicked = ENERGY_MODE;
                    activeButton((Button) v);
                }
            }
        });

        mLivenessSongFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Clicked != LIVENESS_MODE) {
                    nonAtiveButton(mClickedButton);
                    Clicked = LIVENESS_MODE;
                    activeButton((Button) v);
                }
            }
        });

        mSpeechinessSongFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Clicked != SPEECHINESS_MODE) {
                    nonAtiveButton(mClickedButton);
                    Clicked = SPEECHINESS_MODE;
                    activeButton((Button) v);
                }
            }
        });

        mAcousticSongFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Clicked != ACOUSTIC_MODE) {
                    nonAtiveButton(mClickedButton);
                    Clicked = ACOUSTIC_MODE;
                    activeButton((Button) v);
                }
            }
        });
        minstrumentalnessSongFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Clicked != INSTRUMENTAL_MODE) {
                    nonAtiveButton(mClickedButton);
                    Clicked = INSTRUMENTAL_MODE;
                    activeButton((Button) v);
                }
            }
        });

        mMypRankOrder_SongFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Clicked != RANK_ORDER_MODE) {
                    nonAtiveButton(mClickedButton);
                    Clicked = RANK_ORDER_MODE;
                    activeButton((Button) v);
                }
            }
        });

        return filter_view;
    }

    public void activeButtonUI(Button clickedButton) {
        clickedButton.setPadding(25, 25, 25, 25);
        clickedButton.setBackgroundResource(R.drawable.active_background);
        clickedButton.setTextColor(Color.WHITE);
    }

    public void activeButton(Button clickedButton) {
        activeButtonUI(clickedButton);
        mClickedButton = clickedButton;
        mCallback.onFilterSelected(Clicked);
    }

    public void nonAtiveButton(Button clickedButton) {
        clickedButton.setBackgroundResource(R.drawable.background);
        clickedButton.setTextColor(getResources().getColor(R.color.color_base_light_blue));
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
