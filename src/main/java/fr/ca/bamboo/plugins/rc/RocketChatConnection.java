package fr.ca.bamboo.plugins.rc;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Helper class to establish connection against an RocketChat server and perform operations on it
 *
 * @author jasa
 */
public class RocketChatConnection {

    private static final Logger log = Logger.getLogger(RocketChatConnection.class.getName());
    private static Map<String, String> roomsById = new HashMap<String, String>();

    private String server;
    private String user;
    private String password;

    private String authToken;
    private String userId;

    /**
     * The rest api object
     */
    WebResource restapi;

    public RocketChatConnection(String server, String user, String pass) {
        this.server = server;
        this.user = user;
        this.password = pass;

        configure();
        // Login
        login();
    }


    /**
     * Configures jersey client
     */
    private void configure() {
        ClientConfig config = new DefaultClientConfig();
        Client client = Client.create(config);
        restapi = client.resource(String.valueOf(server));
    }

    private void login() {
        WebResource resource = restapi.path("login");
        MultivaluedMap formData = new MultivaluedMapImpl();
        formData.add("user", user);
        formData.add("password", password);

        ClientResponse response = resource.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE).post(ClientResponse.class, formData);

        JSONObject responseObject = JSONObject.fromObject(response.getEntity(String.class));

        if (responseObject.getString("status").equals("success")) {
            JSONObject data = JSONObject.fromObject(responseObject.get("data"));
            this.userId = data.getString("userId");
            this.authToken = data.getString("authToken");
        }
        else{
            throw new RuntimeException("Failed to fetch public rooms with error: " + responseObject.getString("message"));
        }
        fetchPublicRooms();
    }

    private void fetchPublicRooms() {
        WebResource resource = restapi.path("publicRooms");

        ClientResponse response = addTokens(resource).get(ClientResponse.class);

        JSONObject responseObject = JSONObject.fromObject(response.getEntity(String.class));

        if (responseObject.getString("status").equals("success")) {
            JSONArray rooms = responseObject.getJSONArray("rooms");

            for (int i = 0; i < rooms.size(); i++) {
                {
                    JSONObject room = (JSONObject) rooms.get(i);
                    roomsById.put(room.getString("name"), room.getString("_id"));
                }
            }
        }
        else{
            throw new RuntimeException("Failed to fetch public rooms with error: " + responseObject.getString("message"));
        }
    }

    public void sendMessage(String roomName, String message) {
        // joinRoom(roomName);
        WebResource resource = restapi.path("rooms").path(roomsById.get(roomName)).path("send");

        JSONObject object = new JSONObject();
        object.put("msg", message);

        ClientResponse response = addTokens(resource).header("Content-Type", "application/json").post(ClientResponse.class, object.toString());
        JSONObject responseObject = JSONObject.fromObject(response.getEntity(String.class));

        if (!responseObject.getString("status").equals("success")) {
            throw new RuntimeException("Failed to send the message with error: " + responseObject.getString("message"));
        }
    }

    public void logout() {
        WebResource resource = restapi.path("logout");

        ClientResponse response = addTokens(resource).get(ClientResponse.class);

        JSONObject responseObject = JSONObject.fromObject(response.getEntity(String.class));

        if (!responseObject.getString("status").equals("success")) {
            throw new RuntimeException("Failed to logout with error: " + responseObject.getString("message"));
        }
    }

    private WebResource.Builder addTokens(WebResource original) {
        return original.header("X-Auth-Token", authToken).header("X-User-Id", userId);
    }

    public boolean isLoggedIn() {
        return authToken != null && userId != null;
    }
}
