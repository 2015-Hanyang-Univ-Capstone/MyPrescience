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
public class JSON {

    static public String SERVER_ADDRESS = "http://172.200.152.155:8888/MyPrescience/db";
    static public String SPOTIFY_API = "https://api.spotify.com/v1/";

    static public String USER = "/User.php?query=";
    static public String INSERT_FACEBOOK_ID = "insertUser&facebookId=";

    static public String SONG = "/Song.php?query=";
    static public String SONG_WITH_ID = "selectAllWithId&id=";

    static public String BILLBOARDTOP = "/BillboardTop.php?query=";
    static public String BBT_WITH_GENRE = "selectGenreTop&genres=";



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

}
