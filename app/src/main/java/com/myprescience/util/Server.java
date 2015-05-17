package com.myprescience.util;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Created by dongjun on 15. 3. 27..
 */
public class Server {



    static public String ECHONEST_API_KEY = "ZZKPLNLJYHUVPSMXD";
    static public String ECHONEST_GENRE_SEARCH = "http://developer.echonest.com/api/v4/genre/search?api_key="+ECHONEST_API_KEY+"&format=json&results=500&name=";

    static public int MODE, RANDOM_MODE = 0, FIRST_MODE = 1, KPOP_MODE = 2, POP_MODE = 3, BILLBOARDHOT_MODE = 4,
                    Genre_POP_MODE = 41, Genre_HIPHOP_MODE = 42, Genre_RnB_MODE = 43, Genre_ROCK_MODE = 44,
                    Genre_COUNTRY_MODE = 45, Genre_ELECTRONIC_MODE = 46, Genre_JAZZ_MODE = 47, Genre_CLUB_MODE = 48,
                    VALANCE_MODE = 101, LOUDNESS_MODE = 102, DANCABILITY_MODE = 103, ENERGY_MODE = 104,
                    LIVENESS_MODE = 105, SPEECHINESS_MODE = 106, ACOUSTIC_MODE = 107, INSTRUMENTAL_MODE = 108,
                    RANK_ORDER_MODE = 109;

//    static public String SERVER_ADDRESS = "http://218.37.209.129/MyPrescience/db";
//    static public String SERVER_ADDRESS = "http://172.200.152.173:8888/MyPrescience/db";
    static public String SERVER_ADDRESS = "http://166.104.245.89/MyPrescience/db";
    static public String SPOTIFY_API = "https://api.spotify.com/v1/";
    static public String WITH_USER = "&user_id=";

    static public String USER_API = "/User.php?query=";
    static public String INSERT_FACEBOOK_ID = "insertUser&facebookId=";
    static public String USER_ID_WITH_FACEBOOK_ID = "searchUser&facebookId=";

    static public String SONG_API = "/Song.php?query=";
    static public String SONG_WITH_ID = "selectAllWithId&id=";
    static public String RANDOM_SONGS = "selectRanSongs";
    static public String SONG_WTIH_CLAUSE = "selectSongsWithClause&clause=";
    static public String SONG_WTIH_GENRE_CLAUSE = "selectSongsWithGenreClause&clause=";
    static public String MYP_HOT_SONGS = "selectMyPHotSongs";
    static public String MYP_RANK_SONGS = "selectMypRankSongs";
    static public String MYP_TOP100_SONGS = "selectMyPTop100Songs";
    static public String SELECT_SONG_WITH_TRACKID = "selectSongWithTrackId&track_id=";

    static public String BILLBOARD_API = "/Billboard.php?query=";
    static public String GENRE_TOP = "selectGenreTop&genres=";
    static public String HOT100 = "selectHot100";

    static public String RATING_API = "/Rating.php?query=";
    static public String INSERT_RATING = "insertRating&";
    static public String INSERT_LOCAL_FILE_RATING = "insertLocalFileRating";
    static public String SELECT_MYSONGS = "selectSongs&user_id=";
    static public String SELECT_SONG_COUNT = "selectSongCount";
    static public String SELECT_SONG_AVG_RATING = "selectSongAvgRating&song_id=";
    static public String SELECT_SONG_RATING = "selectSongRating&song_id=";


    static public String RECOMMEND_API = "/Recommend.php?query=";
    static public String RECOMMEND_SONGS = "selectRecommendSongs";
    static public String RECOMMEND_SEARCH_SONGS = "selectRecommendSearchSongs";
    static public String CLAUSE = "&clause=AND%20";
    static public String RECOMMEND_SEARCH_SONGS_WITH_GENRE = "selectRecommendSearchSongsWithGenre&genre=";
    static public String EXEC_RECOMMEND_ALGORITHM = "execRecommendAlgorithm";

    static public String GENRES_API = "/Genres.php?query=";
    static public String INSERT_GENRE_DETAIL = "InsertGenreDetail";
    static public String SELECT_GENRE_WITH_DETAIL = "SelectGenreWithDetail&detail=";

    static public String ALBUM_API = "/Album.php?query=";
    static public String SELECT_LATEST_ALBUMS = "selectLatestAlbums";
    static public String SELECT_MAIN_LATEST_ALBUMS = "selectMainLatestAlbums";
    static public String SELECT_MY_ALBUMS = "selectMyAlbums";

    static public String ARTIST_API = "/Artist.php?query=";
    static public String SELECT_MY_ARTISTS = "selectMyArtists";

    static public String FACEBOOK_PROFILE = "https://graph.facebook.com/";
    static public String WIDTH_150 = "/picture?width=150";

    static public String YOUTUBE_DEVELOPMENT_KEY = "AIzaSyCPt2JtKVntVf5N1Uq-GFo6ilAkfPQyPDM";
    static public String YOUTUBE_API = "https://www.googleapis.com/youtube/v3/search?part=snippet&q=";
    static public String YOUTUBE_RESULT_FIVE = "&maxResults=5";
    static public String YOUTUBE_RESULT_THREE = "&maxResults=3";
    static public String YOUTUBE_RESULT_ONE = "&maxResults=1";
    static public String YOUTUBE_API_KEY = "&key=AIzaSyCPt2JtKVntVf5N1Uq-GFo6ilAkfPQyPDM";
    static public String YOUTUBE_EMBED= "http://www.youtube.com/embed/";
    static public String VIDEO_SMALL = "?vq=small";
    static public String VIDEO_MOST_VIEW = "&order=viewCount";

    static public String LUCENE_API = "/Lucene.php?query=";
    static public String SEARCH_SONGS = "searchSongs&q=";
    static public String SEARCH_SONGID = "searchSongId";

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

        return LEVEL[songCount/100];
    }

    public static String callByArrayParameters(String url, List<NameValuePair> parameters) {
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(url);

        String line = "";
        StringBuilder stringBuilder = new StringBuilder();
        try {
            post.setEntity(new UrlEncodedFormEntity(parameters, "utf-8"));

            HttpResponse response = client.execute(post);
            if (response.getStatusLine().getStatusCode() != 200)
            {
                System.out.println("DB: Error executing script !");
            }
            else {
                BufferedReader rd = new BufferedReader(new InputStreamReader(
                        response.getEntity().getContent()));
                line = "";
                while ((line = rd.readLine()) != null) {
                    stringBuilder.append(line);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
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