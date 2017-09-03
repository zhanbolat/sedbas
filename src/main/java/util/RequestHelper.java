package util;

import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.impl.conn.SchemeRegistryFactory;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import java.util.ArrayList;
import java.util.List;

public class RequestHelper {


    private String bonitaURI;
    private final HttpClient httpClient;
    private HttpContext httpContext;
    private ResponseHelper responseHelper;

    public RequestHelper(String bonitaURI) {
        PoolingClientConnectionManager conMan = getConnectionManager();
        httpClient = new DefaultHttpClient(conMan);

        CookieStore cookieStore = new BasicCookieStore();
        this.httpContext = new BasicHttpContext();
        this.httpContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);

        this.bonitaURI = bonitaURI;
        responseHelper = new ResponseHelper();
    }

    private PoolingClientConnectionManager getConnectionManager() {
        PoolingClientConnectionManager conMan = new PoolingClientConnectionManager(SchemeRegistryFactory.createDefault());
        conMan.setMaxTotal(200);
        conMan.setDefaultMaxPerRoute(200);
        return conMan;
    }

    public List<String> getRequestCookies() {
        try {
            System.out.println("saveRequestCookies method call.");
            System.out.println("Current cookie-store: " + httpContext.getAttribute(ClientContext.COOKIE_STORE));

            List<String> cookiesList = new ArrayList<>();

            if (httpContext != null && httpContext.getAttribute(ClientContext.COOKIE_STORE) != null) {
                CookieStore cookieStore = (BasicCookieStore) httpContext.getAttribute(ClientContext.COOKIE_STORE);

                StringBuilder cookies;
                for (Cookie cookie : cookieStore.getCookies()) {
                    cookies = new StringBuilder();
                    cookies.append(cookie.getName()).append("=").append(cookie.getValue());
                    if (cookie.getExpiryDate() != null) cookies.append("; Expires=").append(cookie.getExpiryDate());
                    if (cookie.getPath() != null) cookies.append("; Path=").append("/bonita");
//                    if (cookie.getDomain() != null) cookies.append("; domain=").append(cookie.getDomain());
                    if ("JSESSIONID".equalsIgnoreCase(cookie.getName())) cookies.append("; HttpOnly");
                    cookiesList.add(cookies.toString());
                }
            }

            return cookiesList;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public int executePostRequest(String apiURI, UrlEncodedFormEntity entity) {
        try {
            HttpPost postRequest = new HttpPost(bonitaURI + apiURI);

            postRequest.setEntity(entity);

            HttpResponse response = httpClient.execute(postRequest, httpContext);

            return responseHelper.consumeResponse(response, true);
        } catch (HttpHostConnectException e) {
            throw new RuntimeException("Bonita bundle may not have been started, or the URL is invalid. Please verify hostname and port number. URL used is: " + bonitaURI,e);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public HttpResponse executePostRequest(String apiURI, String payloadAsString) {
        try {
            HttpPost postRequest = new HttpPost(bonitaURI + apiURI);

            StringEntity input = new StringEntity(payloadAsString);
            input.setContentType("application/json");

            postRequest.setEntity(input);

            if (httpContext != null && httpContext.getAttribute("http.cookie-store") != null) {
//				System.out.println("http.cookie-store=" + httpContext.getAttribute("http.cookie-store"));

                CookieStore cookieStore = (BasicCookieStore) httpContext.getAttribute(ClientContext.COOKIE_STORE);
//				System.out.println("Cookies (" + cookieStore.getCookies().size() + "): " + cookieStore.getCookies());
//				System.out.println("X-Bonita-API-Token value: " + cookieStore.getCookies().get(1).getValue());

                postRequest.setHeader("JSESSIONID", cookieStore.getCookies().get(0).getValue());
                postRequest.setHeader("X-Bonita-API-Token", cookieStore.getCookies().get(1).getValue());
                postRequest.setHeader("bonita.tenant", cookieStore.getCookies().get(2).getValue());
            }

            HttpResponse response = httpClient.execute(postRequest, httpContext);

            return response;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public HttpResponse executeGetRequest(String apiURI) {
        try {
            HttpGet getRequest = new HttpGet(bonitaURI + apiURI);

            HttpResponse response = httpClient.execute(getRequest, httpContext);

            return response;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }

    public HttpResponse executePutRequest(String apiURI, String payloadAsString) {
        try {
            HttpPut putRequest = new HttpPut(bonitaURI + apiURI);
            putRequest.addHeader("Content-Type", "application/json");

            StringEntity input = new StringEntity(payloadAsString);
            input.setContentType("application/json");
            putRequest.setEntity(input);

            if (httpContext != null && httpContext.getAttribute("http.cookie-store") != null) {
                CookieStore cookieStore = (BasicCookieStore) httpContext.getAttribute(ClientContext.COOKIE_STORE);

//				putRequest.setHeader("JSESSIONID", cookieStore.getCookies().get(0).getValue());
                putRequest.setHeader("X-Bonita-API-Token", cookieStore.getCookies().get(1).getValue());
//				putRequest.setHeader("bonita.tenant", cookieStore.getCookies().get(2).getValue());
            }

            return httpClient.execute(putRequest, httpContext);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public HttpResponse executeDeleteRequest(String deleteURI) {
        try {

            HttpDelete deleteRequest = new HttpDelete(bonitaURI + deleteURI);
            if (httpContext != null && httpContext.getAttribute("http.cookie-store") != null) {
                CookieStore cookieStore = (BasicCookieStore) httpContext.getAttribute(ClientContext.COOKIE_STORE);

//				deleteRequest.setHeader("JSESSIONID", cookieStore.getCookies().get(0).getValue());
                deleteRequest.setHeader("X-Bonita-API-Token", cookieStore.getCookies().get(1).getValue());
//				deleteRequest.setHeader("bonita.tenant", cookieStore.getCookies().get(2).getValue());
            }

            HttpResponse response = httpClient.execute(deleteRequest, httpContext);

            return response;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }
}
