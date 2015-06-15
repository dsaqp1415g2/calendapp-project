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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
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

    public Comment createComment(String content, String username) throws AppException {
        Log.d(TAG, "createComment()");
        Comment comment = new Comment();
        comment.setContent(content);
        if (username != null)
            comment.setUsername(username);
        HttpURLConnection urlConnection = null;
        try {
            JSONObject jsonComment = createJsonComment(comment);
            URL urlPostComments = new URL(rootAPI.getLinks().get("post-comment")
                    .getTarget());
            urlConnection = (HttpURLConnection) urlPostComments.openConnection();
            String mediaType = rootAPI.getLinks().get("post-comment").getParameters().get("type"); //Esta línea no estaba en el gist
            urlConnection.setRequestProperty("Accept",
                    mediaType); //Esto estaba mal en los gists
            urlConnection.setRequestProperty("Content-Type",
                    mediaType);
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.connect();
            PrintWriter writer = new PrintWriter(
                    urlConnection.getOutputStream());
            writer.println(jsonComment.toString());
            writer.close();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    urlConnection.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            jsonComment = new JSONObject(sb.toString());

            comment.setCommentid(jsonComment.getInt("commentid"));
            comment.setContent(jsonComment.getString("content"));
            comment.setDislikes(jsonComment.getInt("dislikes"));
            comment.setLikes(jsonComment.getInt("likes"));
            comment.setUsername(jsonComment.getString("username"));

            JSONArray jsonLinks = jsonComment.getJSONArray("links");
            parseLinks(jsonLinks, comment.getLinks());
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

        return comment;
    }

    private JSONObject createJsonComment(Comment comment) throws JSONException {
        JSONObject jsonComment = new JSONObject();
        jsonComment.put("content", comment.getContent());
        if (comment.getUsername() != null)
            jsonComment.put("username", comment.getUsername());

        return jsonComment;
    }

    public CommentCollection getComments(String urlComments) throws AppException {
        Log.d(TAG, "getComments()");
        CommentCollection comments = new CommentCollection(); //Modelo de la colección

        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) new URL(urlComments).openConnection(); //Cnx URL Contra el ROOT + el target del atributo "comments"
            urlConnection.setRequestMethod("GET");

            urlConnection.setRequestProperty("Accept",
                    MediaType.CALENDAPP_API_COMMENT_COLLECTION); //Esto estaba mal en los gists
            urlConnection.setDoInput(true);
            urlConnection.connect();
        } catch (IOException e) {
            throw new AppException(
                    "Can't connect to Calendapp API Web Service");
        }

        BufferedReader reader;
        try {
            reader = new BufferedReader(new InputStreamReader(
                    urlConnection.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            JSONObject jsonObject = new JSONObject(sb.toString());
            JSONArray jsonLinks = jsonObject.getJSONArray("links");
            parseLinks(jsonLinks, comments.getLinks());

            JSONArray jsonComments = jsonObject.getJSONArray("comments");
            for (int i = 0; i < jsonComments.length(); i++) {
                Comment comment = new Comment();
                JSONObject jsonComment = jsonComments.getJSONObject(i);

                comment.setCommentid(jsonComment.getInt("commentid"));
                comment.setContent(jsonComment.getString("content"));
                comment.setDislikes(jsonComment.getInt("dislikes"));
                comment.setLastModified(jsonComment.getLong("lastModified"));
                comment.setLikes(jsonComment.getInt("likes"));
                comment.setUsername(jsonComment.getString("username"));

                jsonLinks = jsonComment.getJSONArray("links");
                parseLinks(jsonLinks, comment.getLinks());
                comments.getComments().add(comment);
            }
        } catch (IOException e) {
            throw new AppException(
                    "Can't get response from Calendapp API Web Service");
        } catch (JSONException e) {
            throw new AppException("Error parsing Calendapp Root API");
        }

        return comments;
    }




    public User updateUser(String name, int age, String email) throws AppException {
        Log.d(TAG, "updateUser()");
        User user = new User();
        user.setName(name);
        user.setAge(age);
        user.setEmail(email);

        HttpURLConnection urlConnection = null;
        try {
            JSONObject jsonUser = createJsonUser(user);
            URL urlPostUsers = new URL(rootAPI.getLinks().get("create-user")
                    .getTarget());
            urlConnection = (HttpURLConnection) urlPostUsers.openConnection();
            String mediaType = rootAPI.getLinks().get("update-user").getParameters().get("type"); //Esta línea no estaba en el gist
            urlConnection.setRequestProperty("Accept",
                    mediaType); //Esto estaba mal en los gists
            urlConnection.setRequestProperty("Content-Type",
                    mediaType);
            urlConnection.setRequestMethod("PUT");
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

            user.setUsername(jsonUser.getString("username"));
            user.setEmail(jsonUser.getString("email"));
            user.setName(jsonUser.getString("name"));
            user.setAge(jsonUser.getInt("age"));

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

    public User createUser(String username, String name, int ages, String email, String userpass) throws AppException {
        Log.d(TAG, "createUser()");
        User user = new User();
        user.setUsername(username);
        user.setName(name);
        user.setEmail(email);
        user.setUserpass(userpass);
        user.setAge(ages);

        HttpURLConnection urlConnection = null;
        try {
            JSONObject jsonUser = createJsonUser(user);
            URL urlPostUsers = new URL(rootAPI.getLinks().get("create-user")
                    .getTarget());
            urlConnection = (HttpURLConnection) urlPostUsers.openConnection();
            String mediaType = rootAPI.getLinks().get("create-user").getParameters().get("type"); //Esta línea no estaba en el gist
            urlConnection.setRequestProperty("Accept",
                    mediaType); //Esto estaba mal en los gists
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

            user.setUsername(jsonUser.getString("username"));

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
            String data = jsonUser.toString();
            writer.println(data);
            writer.close();
            int rc = urlConnection.getResponseCode();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    urlConnection.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            jsonUser = new JSONObject(sb.toString());
            user.setLoginSuccesfull(jsonUser.getBoolean("loginSuccessful"));

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
        jsonUser.put("userpass", user.getUserpass());
        jsonUser.put("name", user.getName());
        jsonUser.put("userid", user.getUserid());
        jsonUser.put("age", user.getAge());
        jsonUser.put("email", user.getEmail());
        return jsonUser;
    }

    public User getUser(String urlUser) throws AppException{
        User user = new User();
        HttpURLConnection urlConnection = null;
        Log.d("TAG", urlUser);
        try {
            URL url = new URL(urlUser);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoInput(true);
            urlConnection.connect();


            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    urlConnection.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            JSONObject jsonUser = new JSONObject(sb.toString());

            user.setUserid(jsonUser.getInt("userid"));
            user.setUsername(jsonUser.getString("username"));
            user.setName(jsonUser.getString("name"));
            user.setAge(jsonUser.getInt("age"));
            user.setEmail(jsonUser.getString("email"));
            JSONArray jsonLinks = jsonUser.getJSONArray("links");
            parseLinks(jsonLinks, user.getLinks());
        } catch (MalformedURLException e) {
            Log.e(TAG, e.getMessage(), e);
            throw new AppException("Bad sting url");
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
            throw new AppException("Exception when getting the sting");
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
            throw new AppException("Exception parsing response");
        }
        return user;
    }

    public EventCollection getEvents(String urlEvents) throws AppException {
        //Log.d(TAG, "getEvents(String urlEvents)");
        EventCollection events = new EventCollection();

        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL (urlEvents);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("Accept",
                    MediaType.CALENDAPP_API_EVENT_COLLECTION);
            urlConnection.setDoInput(true);
            urlConnection.connect();
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND) {
                Log.d(TAG, "CACHE");
                return null;
            }
        } catch (IOException e) {
            throw new AppException(
                    "Can't connect to Calendapp API Web Service");
        }


        BufferedReader reader;
        try {
            reader = new BufferedReader(new InputStreamReader(
                    urlConnection.getInputStream()));

            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            JSONObject jsonObject = new JSONObject(sb.toString());
            JSONArray jsonLinks = jsonObject.getJSONArray("links");
            parseLinks(jsonLinks, events.getLinks());

            events.setFirstTimestamp(jsonObject.getLong("firstTimestamp"));
            events.setLastTimestamp(jsonObject.getLong("lastTimestamp"));
            JSONArray jsonEvents = jsonObject.getJSONArray("events");
            for (int i = 0; i < jsonEvents.length(); i++){
                Event event = new Event();
                JSONObject jsonEvent = jsonEvents.getJSONObject(i);
                event.setEventid(jsonEvent.getInt("eventid"));
                event.setUserid(jsonEvent.getInt("userid"));
                event.setGroupid(jsonEvent.getInt("groupid"));
                event.setName(jsonEvent.getString("name"));
                event.setDateInitial(jsonEvent.getLong("dateInitial"));
                event.setDateFinish(jsonEvent.getLong("dateFinish"));
                event.setLastModified(jsonEvent.getLong("lastModified"));
                jsonLinks = jsonEvent.getJSONArray("links");
                parseLinks(jsonLinks, event.getLinks());
                events.getEvents().add(event);
            }
        }catch (MalformedURLException e) {
            Log.e(TAG, e.getMessage(), e);
            throw new AppException("Bad sting url");
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
            throw new AppException("Exception when getting the event");
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
            throw new AppException("Exception parsing response");
        }

        return events;
    }
    private Map<String, Event> eventsCache = new HashMap<String, Event>();

    public Event getEvent(String urlEvent) throws AppException{
        Event event = new Event ();
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(urlEvent);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("Accept",
                    MediaType.CALENDAPP_API_EVENT);
            urlConnection.setDoInput(true);

            event = eventsCache.get(urlEvent);
            String eTag = (event == null) ? null : event.geteTag();
            if(eTag != null)
                urlConnection.setRequestProperty("If-None-Match", eTag);
            urlConnection.connect();
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_NOT_MODIFIED){
                Log.d(TAG, "CACHE");
                return eventsCache.get(urlEvent);
            }
            Log.d(TAG, "NOT IN CACHE");
            event = new Event();
            eTag = urlConnection.getHeaderField("ETag");
            event.seteTag(eTag);
            eventsCache.put(urlEvent, event);

            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    urlConnection.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            JSONObject jsonEvent = new JSONObject(sb.toString());
            event.setEventid(jsonEvent.getInt("eventid"));
            event.setUserid(jsonEvent.getInt("userid"));
            event.setGroupid(jsonEvent.getInt("groupid"));
            event.setName(jsonEvent.getString("name"));
            event.setDateInitial(jsonEvent.getLong("dateInitial"));
            event.setDateFinish(jsonEvent.getLong("dateFinish"));
            event.setLastModified(jsonEvent.getLong("lastModified"));
            JSONArray jsonLinks = jsonEvent.getJSONArray("links");
            parseLinks(jsonLinks, event.getLinks());

        }catch (MalformedURLException e) {
            Log.e(TAG, e.getMessage(), e);
            throw new AppException("Bad sting url");
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
            throw new AppException("Exception when getting the sting");
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
            throw new AppException("Exception parsing response");
        } catch (AppException e) {
            e.printStackTrace();
        }
        return event;
    }

    private Map<String, Group> groupsCache = new HashMap<>();

    public Group getGroup(String urlGroup) throws AppException{
        Group group = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(urlGroup);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("Accept",
                    MediaType.CALENDAPP_API_GROUP);
            urlConnection.setDoInput(true);

            group = groupsCache.get(urlGroup);
            String eTag = (group == null) ? null : group.geteTag();
            if(eTag != null){
                urlConnection.setRequestProperty("If-None-Match", eTag);
            }
            urlConnection.connect();
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_NOT_MODIFIED)
            {
                Log.d(TAG, "CACHE");
                return groupsCache.get(urlGroup);
            }
            Log.d(TAG, "NOT IN CACHE");
            group = new Group();
            eTag = urlConnection.getHeaderField("ETag");
            group.seteTag(eTag);
            groupsCache.put(urlGroup, group);

            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    urlConnection.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            JSONObject jsonGroup = new JSONObject(sb.toString());
            group.setAdmin(jsonGroup.getString("admin"));
            group.setCreationTimestamp(jsonGroup.getLong("creationTimestamp"));
            group.setDescription(jsonGroup.getString("description"));
            group.setGroupid(jsonGroup.getInt("groupid"));
            group.setLastModified(jsonGroup.getLong("lastModified"));
            group.setName(jsonGroup.getString("name"));
            group.setShared(jsonGroup.getBoolean("shared"));
            JSONArray jsonLinks = jsonGroup.getJSONArray("links");
            parseLinks(jsonLinks, group.getLinks());

        }catch (MalformedURLException e) {
            Log.e(TAG, e.getMessage(), e);
            throw new AppException("Bad sting url");
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
            throw new AppException("Exception when getting the sting");
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
            throw new AppException("Exception parsing response");
        }
        return group;
    }

    public GroupCollection getGroups(String urlMyGroups) throws AppException {
        Log.d(TAG, "getGroups()");
        GroupCollection groups = new GroupCollection();

        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) new URL(urlMyGroups).openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("Accept",
                    MediaType.CALENDAPP_API_GROUP_COLLECTION);
            urlConnection.setDoInput(true);
            urlConnection.connect();
        } catch (IOException e) {
            throw new AppException(
                    "Can't connect to Calendapp API Web Service");
        }
        BufferedReader reader;
        try{
            reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            JSONObject jsonObject = new JSONObject(sb.toString());
            JSONArray jsonLinks = jsonObject.getJSONArray("links");
            parseLinks(jsonLinks, groups.getLinks());

            groups.setNewestTimestamp(jsonObject.getLong("newestTimestamp"));
            groups.setOldestTimestamp(jsonObject.getLong("oldestTimestamp"));
            JSONArray jsonGroups = jsonObject.getJSONArray("groups");
            for (int i = 0; i < jsonGroups.length(); i++){
                Group group = new Group();
                JSONObject jsonGroup = jsonGroups.getJSONObject(i);
                group.setAdmin(jsonGroup.getString("admin"));
                group.setCreationTimestamp(jsonGroup.getLong("creationTimestamp"));
                group.setDescription(jsonGroup.getString("description"));
                group.setGroupid(jsonGroup.getInt("groupid"));
                group.setLastModified(jsonGroup.getLong("lastModified"));
                group.setName(jsonGroup.getString("name"));
                group.setShared(jsonGroup.getBoolean("shared"));
                jsonLinks = jsonGroup.getJSONArray("links");
                parseLinks(jsonLinks, group.getLinks());
                groups.getGroups().add(group);
            }

        }catch (IOException e) {
            throw new AppException(
                    "Can't get response from Beeter API Web Service");
        } catch (JSONException e) {
            throw new AppException("Error parsing Beeter Root API");
        }
        return groups;
    }



}
