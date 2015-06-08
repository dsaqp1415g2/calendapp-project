package edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Properties;

/**
 * Created by Jordi on 29/05/2015.
 */
public class CalendappAPI {

    private final static String TAG = CalendappAPI.class.getName();
    private static CalendappAPI instance = null;
    private CalendappRootAPI rootAPI = null;
    private URL url;

    private CalendappAPI(Context context) throws IOException, AppException {
        super();

        AssetManager assetManager = context.getAssets();
        Properties config = new Properties();
        config.load(assetManager.open("config.properties")); //Carga el asset creado "config.properties"
        String urlHome = config.getProperty("calendapp.home"); //Obtener la propiedad por su nombre
        url = new URL(urlHome);

        Log.d("LINKS", url.toString());
        getRootAPI();
    }

    public final static CalendappAPI getInstance(Context context) throws AppException {
        if (instance == null)
            try {
                instance = new CalendappAPI(context);
            } catch (IOException e) {
                throw new AppException(
                        "Can't load configuration file");
            }
        return instance;
    }

    private void getRootAPI() throws AppException {
        Log.d(TAG, "getRootAPI()");
        rootAPI = new CalendappRootAPI(); //Instancia el model que pide la respuesta a "/"
        HttpURLConnection urlConnection = null; //cnx HTTP
        try {
            urlConnection = (HttpURLConnection) url.openConnection(); //Abrir cnx
            urlConnection.setRequestMethod("GET"); //Indicar método a usar
            urlConnection.setDoInput(true); //Declaras que vas a leer la respuesta
            urlConnection.connect(); //Hacer la petición
        } catch (IOException e) {
            throw new AppException(
                    "Can't connect to Calendapp API Web Service");
        }

        BufferedReader reader; //Leer la respuesta
        try {
            reader = new BufferedReader(new InputStreamReader(
                    urlConnection.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line); //Guardar la respuesta en un StringBuilder (es un JSON)
            }

            JSONObject jsonObject = new JSONObject(sb.toString()); //Se procesa el JSON de respuesta
            JSONArray jsonLinks = jsonObject.getJSONArray("links");
            parseLinks(jsonLinks, rootAPI.getLinks());
        } catch (IOException e) {
            throw new AppException(
                    "Can't get response from Calendapp API Web Service");
        } catch (JSONException e) {
            throw new AppException("Error parsing Calendapp Root API");
        }

    }

    public User checkLogin(String username, String password) throws AppException {
        Log.d(TAG, "checkLogin()");
        User user = new User();
        user.setUsername(username);
        user.setUserpass(password);
        String as = user.getUsername();
        String pass = user.getUserpass();
        Log.e(as, "user: "+as);
        Log.e(pass, "pass: " + pass);
        HttpURLConnection urlConnection = null;
       
        try {
            JSONObject jsonUser = createJsonUser(user);
            URL urlPostUsers = new URL(rootAPI.getLinks().get("login").getTarget());
            urlConnection = (HttpURLConnection) urlPostUsers.openConnection();
            String mediaType = rootAPI.getLinks().get("login").getParameters().get("type");
            urlConnection.setRequestProperty("Accept",
                    mediaType);
            urlConnection.setRequestProperty("Content-Type",
                    mediaType);
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.connect();
            PrintWriter writer = new PrintWriter(
                    urlConnection.getOutputStream());
            writer.println(jsonUser.toString());
            writer.close();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    urlConnection.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            jsonUser = new JSONObject(sb.toString());
            user.setLoginSuccesfull(jsonUser.getBoolean("loginSuccessfull"));

            JSONArray jsonLinks = jsonUser.getJSONArray("links");
            parseLinks(jsonLinks, user.getLinks());
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
            throw new AppException("Error parsing response");
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
            throw new AppException("Error getting response");
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
        }

        return user;
    }

    private void parseLinks(JSONArray jsonLinks, Map<String, Link> map)
            throws AppException, JSONException {
        for (int i = 0; i < jsonLinks.length(); i++) {
            Link link = null;
            try {
                link = SimpleLinkHeaderParser
                        .parseLink(jsonLinks.getString(i));
            } catch (Exception e) {
                throw new AppException(e.getMessage());
            }
            String rel = link.getParameters().get("rel");
            String rels[] = rel.split("\\s");
            for (String s : rels)
                map.put(s, link);
        }
    }

    private JSONObject createJsonUser(User user) throws JSONException {
        JSONObject jsonUser = new JSONObject();
        jsonUser.put("username", user.getUsername());
        jsonUser.put("password", user.getUserpass());
        jsonUser.put("name", user.getName());
        jsonUser.put("email", user.getEmail());
        jsonUser.put("age", user.getAge());
        return jsonUser;
    }


}
