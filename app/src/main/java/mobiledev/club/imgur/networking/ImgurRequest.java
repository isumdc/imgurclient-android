package mobiledev.club.imgur.networking;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Wrapper around JsonObjectRequest which adds HTTP headers
 */
public class ImgurRequest extends JsonObjectRequest {
    private String clientId;

    public ImgurRequest(int method, String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(method, url, jsonRequest, listener, errorListener);
    }

    public ImgurRequest(String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(url, jsonRequest, listener, errorListener);
    }

    /**
     * Returns a list of extra HTTP headers to go along with this request. Can
     * throw {@link com.android.volley.AuthFailureError} as authentication may be required to
     * provide these values.
     * @throws com.android.volley.AuthFailureError In the event of auth failure
     */
    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = new HashMap<String,String>();
        headers.put("Authorization", "Client-ID " + clientId);
        return headers;
    }

    public void setImgurClientId(String clientId){
        this.clientId = clientId;
    }
}