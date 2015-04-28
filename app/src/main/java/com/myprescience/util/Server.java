package com.myprescience.util;

import android.graphics.Bitmap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by dongjun on 15. 3. 27..
 */
public class Server {



    static public String ECHONEST_API_KEY = "ZZKPLNLJYHUVPSMXD";
    static public String ECHONEST_GENRE_SEARCH = "http://developer.echonest.com/api/v4/genre/search?api_key="+ECHONEST_API_KEY+"&format=json&results=500&name=";

    static public int MODE, RANDOM_MODE = 0, FIRST_MODE = 1, KPOP_MODE = 2, POP_MODE = 3, BILLBOARDHOT_MODE = 4, VALANCE_MODE = 5,
                    LOUDNESS_MODE = 6, DANCABILITY_MODE = 7, ENERGY_MODE = 8, LIVENESS_MODE = 9, SPEECHINESS_MODE = 10,
                    ACOUSTIC_MODE = 11, INSTRUMENTAL_MODE = 12, RANK_ORDER_MODE = 13;

//    static public String SERVER_ADDRESS = "http://218.37.209.129/MyPrescience/db";
//    static public String SERVER_ADDRESS = "http://172.200.152.155:8888/MyPrescience/db";
    static public String SERVER_ADDRESS = "http://166.104.245.89/MyPrescience/db";
    static public String SPOTIFY_API = "https://api.spotify.com/v1/";
    static public String WITH_USER = "&user_id=";

    static public String USER_API = "/User.php?query=";
    static public String INSERT_FACEBOOK_ID = "insertUser&facebookId=";
    static public String USER_ID_WITH_FACEBOOK_ID = "searchUser&facebookId=";

    static public String SONG_API = "/Song.php?query=";
    static public String SONG_WITH_ID = "selectAllWithId&id=";
    static public String RANDOM_SONGS = "selectRanSongs";
    static public String KOR_SONGS = "selectKorSongs";
    static public String VALENCE_SONGS = "selectValenceSongs";
    static public String LOUDNESS_SONGS = "selectLoudnessSongs";
    static public String DANCEABILITY_SONGS = "selectDanceabilitySongs";
    static public String ENERGY_SONGS = "selectEnergySongs";
    static public String LIVENESS_SONGS = "selectLivenessSongs";
    static public String SPEECHINCESS_SONGS = "selectSpeechinessSongs";
    static public String ACOUSTIC_SONGS = "selectAcousticnessSongs";
    static public String INSTRUMENTALNESS_SONGS = "selectInstrumentalnessSongs";
    static public String MYP_HOT_SONGS = "selectMyPHotSongs";
    static public String MYP_RANK_SONGS = "selectMypRankSongs";

    static public String BILLBOARD_API = "/Billboard.php?query=";
    static public String GENRE_TOP = "selectGenreTop&genres=";
    static public String HOT100 = "selectHot100";

    static public String RATING_API = "/Rating.php?query=";
    static public String INSERT_RATING = "insertRating&";
    static public String SELECT_MYSONGS = "selectSongs&user_id=";
    static public String SELECT_SONG_COUNT = "selectSongCount";
    static public String SELECT_SONG_AVG_RATING = "selectSongAvgRating&song_id=";
    static public String SELECT_SONG_RATING = "selectSongRating&song_id=";

    static public String RECOMMEND_API = "/Recommend.php?query=";
    static public String RECOMMEND_SONGS = "selectRecommendSongs";

    static public String GENRES_API = "/Genres.php?query=";
    static public String INSERT_GENRE_DETAIL = "InsertGenreDetail";
    static public String SELECT_GENRE_WITH_DETAIL = "SelectGenreWithDetail&detail=";

    static public String FACEBOOK_PROFILE = "https://graph.facebook.com/";
    static public String WIDTH_150 = "/picture?width=150";

    static public String YOUTUBE_DEVELOPMENT_KEY = "AIzaSyCPt2JtKVntVf5N1Uq-GFo6ilAkfPQyPDM";
    static public String YOUTUBE_API = "https://www.googleapis.com/youtube/v3/search?part=snippet&q=";
    static public String YOUTUBE_RESULT_FIVE = "&maxResults=5";
    static public String YOUTUBE_API_KEY = "&key=AIzaSyCPt2JtKVntVf5N1Uq-GFo6ilAkfPQyPDM";
    static public String YOUTUBE_EMBED= "http://www.youtube.com/embed/";
    static public String VIDEO_SMALL = "?vq=small";
    static public String VIDEO_MOST_VIEW = "&order=viewCount";

    // Url으로부터 Server의 JSON - return (String)
    static public String getStringFromUrl(String url) {
        DefaultHttpClient httpclient = new DefaultHttpClient();

        HttpGet post = new HttpGet(url);
        HttpResponse response;
        StringBuilder total = new StringBuilder();
        try {
            response = httpclient.execute(post);
            HttpEntity ht = response.getEntity();
            BufferedHttpEntity buf = new BufferedHttpEntity(ht);
            InputStream is = buf.getContent();

            BufferedReader r = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = r.readLine()) != null) {
                total.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return total.toString();
    }

    static public String getLevel(int songCount) {

        String[] LEVEL = {
                "마음의 양식이 필요합니다. 노래를 들으세요.",
                "이제 시작이군요! 얼쑤!",
                "음악으로 풍요로워 지셨나요?",
                "노래 들으며 리듬타시는 모습이 보이는 군요!",
                "Music is My Life!"
        };

        return LEVEL[songCount/50];
    }
}




// Insert Genre Detail
//        String genre = "R%26B";
//        new getGenreDetail().execute(ECHONEST_GENRE_SEARCH+genre, genre);
////        static public String ECHONEST_GENRE_SEARCH_RNB = "http://developer.echonest.com/api/v4/genre/search?api_key=ZZKPLNLJYHUVPSMXD&format=json&name=R%26B&results=500";
//        genre = "dance";
//        new getGenreDetail().execute(ECHONEST_GENRE_SEARCH+genre, genre);
////        static public String ECHONEST_GENRE_SEARCH_DANCE = "http://developer.echonest.com/api/v4/genre/search?api_key=ZZKPLNLJYHUVPSMXD&format=json&name=dance&results=500";
//        genre = "jazz";
//        new getGenreDetail().execute(ECHONEST_GENRE_SEARCH+genre, genre);
////        static public String ECHONEST_GENRE_SEARCH_JAZZ = "http://developer.echonest.com/api/v4/genre/search?api_key=ZZKPLNLJYHUVPSMXD&format=json&name=JAZZ&results=500";
//        genre = "country";
//        new getGenreDetail().execute(ECHONEST_GENRE_SEARCH+genre, genre);
////        static public String ECHONEST_GENRE_SEARCH_COUNTRY = "http://developer.echonest.com/api/v4/genre/search?api_key=ZZKPLNLJYHUVPSMXD&format=json&name=country&results=500";
//        genre = "electro";
//        new getGenreDetail().execute(ECHONEST_GENRE_SEARCH+genre, genre);
////        static public String ECHONEST_GENRE_SEARCH_ELECTRONIC = "http://developer.echonest.com/api/v4/genre/search?api_key=ZZKPLNLJYHUVPSMXD&format=json&name=electro&results=500";
//        genre = "metal";
//        new getGenreDetail().execute(ECHONEST_GENRE_SEARCH+genre, genre);
////        static public String ECHONEST_GENRE_SEARCH_METAL = "http://developer.echonest.com/api/v4/genre/search?api_key=ZZKPLNLJYHUVPSMXD&format=json&name=metal&results=500";
//        genre = "folk";
//        new getGenreDetail().execute(ECHONEST_GENRE_SEARCH+genre, genre);
////        static public String ECHONEST_GENRE_SEARCH_FOLK = "http://developer.echonest.com/api/v4/genre/search?api_key=ZZKPLNLJYHUVPSMXD&format=json&name=folk&results=500";
//        genre = "christmas";
//        new getGenreDetail().execute(ECHONEST_GENRE_SEARCH+genre, genre);
////        static public String ECHONEST_GENRE_SEARCH_CHRISTMAS = "http://developer.echonest.com/api/v4/genre/search?api_key=ZZKPLNLJYHUVPSMXD&format=json&name=christmas&results=500";
//        genre = "indie";
//        new getGenreDetail().execute(ECHONEST_GENRE_SEARCH+genre, genre);
////        static public String ECHONEST_GENRE_SEARCH_INDIE



//    class getGenreDetail extends AsyncTask<String, String, Void> {
//
//        @Override
//        protected Void doInBackground(String... url) {
//            String genreJSON = getStringFromUrl(url[0]);
//
//            JSONParser jsonParser = new JSONParser();
//            JSONObject responseJSON = null;
//            try {
//                responseJSON = (JSONObject) jsonParser.parse(genreJSON);
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//            JSONObject response = (JSONObject) responseJSON.get("response");
//            JSONArray genres = (JSONArray) response.get("genres");
//            for(int i = 0; i < genres.size(); i++) {
//                JSONObject genre = (JSONObject) genres.get(i);
//                String detail = (String) genre.get("name");
//                Log.e("detail", detail);
////                new insertGenreDetail().execute(SERVER_ADDRESS+GENRES_API+INSERT_GENRE_DETAIL+"&genre="+genreName+"&detail="+detail);
//                try {
//                    new insertGenreDetail().execute(SERVER_ADDRESS+GENRES_API+INSERT_GENRE_DETAIL+"&genre="+url[1]+"&detail="+ URLEncoder.encode(detail, "utf-8"));
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                }
//
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void result) {
//            super.onPostExecute(result);
//            Log.e("onPostExecute", "onPostExecute");
//        }
//    }
//
//    class insertGenreDetail extends AsyncTask<String, String, Void> {
//        @Override
//        protected Void doInBackground(String... url) {
//            String responseJSON = getStringFromUrl(url[0]);
//            Log.e("insertGenreDetail", responseJSON);
//            return null;
//        }
//    }