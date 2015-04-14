package com.myprescience.util;

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

    static public int USER_ID = 0;
    static public int MODE, RANDOM_MODE = 0, FIRST_MODE = 1, KPOP_MODE = 2, POP_MODE = 3, BILLBOARDHOT_MODE = 4;

//    static public String SERVER_ADDRESS = "http://218.37.215.185/MyPrescience/db";
//    static public String SERVER_ADDRESS = "http://172.200.152.155:8888/MyPrescience/db";
    static public String SERVER_ADDRESS = "http://166.104.245.89/MyPrescience/db";
    static public String SPOTIFY_API = "https://api.spotify.com/v1/";

    static public String USER_API = "/User.php?query=";
    static public String INSERT_FACEBOOK_ID = "insertUser&facebookId=";
    static public String USER_ID_WITH_FACEBOOK_ID = "searchUser&facebookId=";

    static public String SONG_API = "/Song.php?query=";
    static public String SONG_WITH_ID = "selectAllWithId&id=";
    static public String RANDOM_SONGS = "selectRanSongs&user_id=";

    static public String BILLBOARDTOP_API = "/BillboardTop.php?query=";
    static public String BBT_WITH_GENRE = "selectGenreTop&genres=";

    static public String RATING_API = "/Rating.php?query=";
    static public String INSERT_RATING = "insertRating&";
    static public String SELECT_MYSONGS = "selectSongs&user_id=";
    static public String SELECT_SONG_COUNT = "selectSongCount&user_id=";

    static public String FACEBOOK_PROFILE = "https://graph.facebook.com/";
    static public String WIDTH_150 = "/picture?width=150";

    static public String YOUTUBE_API = "https://www.googleapis.com/youtube/v3/search?part=snippet&q=";
    static public String YOUTUBE_RESULT_ONE = "&maxResults=1";
    static public String YOUTUBE_API_KEY = "&key=AIzaSyCPt2JtKVntVf5N1Uq-GFo6ilAkfPQyPDM";
    static public String YOUTUBE_EMBED= "http://www.youtube.com/embed/";
    static public String VIDEO_SMALL = "?vq=small";

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
