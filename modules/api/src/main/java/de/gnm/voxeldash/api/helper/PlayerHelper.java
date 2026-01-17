package de.gnm.voxeldash.api.helper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.util.UUID;

public class PlayerHelper {

    private static final OkHttpClient client = new OkHttpClient();
    private static final ObjectMapper mapper = new ObjectMapper();

    public static UUID getUUID(String username) {
        try {
            Request request = new Request.Builder()
                    .url("https://api.mojang.com/users/profiles/minecraft/" + username)
                    .build();

            Response response = client.newCall(request).execute();

            if (response.isSuccessful()) {
                JsonNode jsonNode = mapper.readTree(response.body().string());
                response.close();

                return UUID.fromString(jsonNode.get("id").asText().replaceFirst(
                        "(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5"
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}
