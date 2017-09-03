package kz.bonita.services.service;

import kz.bonita.services.util.RequestHelper;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BonitaServices {

    private final String BONITA_URI = "http://localhost:8080/bonita";
    private RequestHelper requestHelper = new RequestHelper(BONITA_URI);



    public void loginAs(String username, String password) {
        try {
            String loginURL = "/loginservice";

            System.out.println("Login url: " + loginURL);
            System.out.println("Username: " + username);

            // If you misspell a parameter you will get a HTTP 500 error
            List<NameValuePair> urlParameters = new ArrayList<>();
            urlParameters.add(new BasicNameValuePair("username", username));
            urlParameters.add(new BasicNameValuePair("password", password));
            urlParameters.add(new BasicNameValuePair("redirect", "false"));

            // UTF-8 is mandatory otherwise you get a NPE
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(urlParameters, "utf-8");
            requestHelper.executePostRequest(loginURL, entity);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public String getUserIdFromSession() {
        try {

            return extractUserIdFrom(requestHelper.executeGetRequest("/API/system/session/unusedid"));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public List<String> getCookies() {
        try {
            return requestHelper.getRequestCookies();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private String extractUserIdFrom(HttpResponse response) {
        try {
            String session = EntityUtils.toString(response.getEntity());
            String remain = session.substring(session.indexOf("user_id\":") + 10);
            String userid = remain.substring(0, remain.indexOf("\""));
            return userid;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
