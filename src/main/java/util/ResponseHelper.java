package util;

import org.apache.http.HttpResponse;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ResponseHelper {

    public int consumeResponse(HttpResponse response, boolean printResponse) {

        String responseAsString = consumeResponseIfNecessary(response);
        if(printResponse) {
            System.out.println(responseAsString);
        }

        return ensureStatusOk(response);
    }

    public String consumeResponseIfNecessary(HttpResponse response) {
        if (response.getEntity() != null) {
            BufferedReader rd;
            try {
                rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

                StringBuffer result = new StringBuffer();
                String line = "";
                while ((line = rd.readLine()) != null) {
                    result.append(line);
                }
                return result.toString();
            } catch (Exception e) {
                throw new RuntimeException("Failed to consume response.", e);
            }
        } else {
            return "";
        }
    }

    private int ensureStatusOk(HttpResponse response) {
        if (response.getStatusLine().getStatusCode() != 201 && response.getStatusLine().getStatusCode() != 200) {
            throw new RuntimeException("Failed : HTTP error code : " + response.getStatusLine().getStatusCode() + " : "
                    + response.getStatusLine().getReasonPhrase());
        }
        return response.getStatusLine().getStatusCode();
    }
}
