package org.misja.stockfighter;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.http.json.JsonHttpContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;

import java.io.IOException;
import java.io.InputStreamReader;

public class Api {
    private static final String API_KEY = "0e7762dcf8e13561339b0ab42b68163b2a5b69e1";

    private static final String STOCKFIGTHER_URL = "https://api.stockfighter.io/ob/api";

    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    private static final JsonFactory JSON_FACTORY = new JacksonFactory();

    private static final JsonObjectParser PARSER = new JsonObjectParser(JSON_FACTORY);

    public boolean healthCheck() throws IOException {
        HttpResponse response = executeGetRequest(STOCKFIGTHER_URL + "/venues/TESTEX/stocks/FOOBAR");
        return "OK".equalsIgnoreCase(response.getStatusMessage());
    }

    public boolean healthCheckForVenue(String venue) throws IOException {
        HttpResponse response = executeGetRequest(STOCKFIGTHER_URL + "/venues/" + venue + "/heartbeat");
        return "OK".equalsIgnoreCase(response.getStatusMessage());
    }

    public OrderBook getOrderBook(String venue, String stock) throws IOException {
        HttpResponse response = executeGetRequest(STOCKFIGTHER_URL + "/venues/" + venue + "/stocks/" + stock);
        return response.parseAs(OrderBook.class);
    }

    public OrderStatus placeOrder(Order order) throws IOException {
        HttpResponse response = executePostRequest(STOCKFIGTHER_URL + "/venues/" + order.venue + "/stocks/" + order.stock + "/orders", order);
        return PARSER.parseAndClose(new InputStreamReader(response.getContent()), OrderStatus.class);
    }

    public OrderStatus cancelOrder(String venue, String stock, int orderId) throws IOException {
        HttpResponse response = executeDeleteRequest(STOCKFIGTHER_URL + "/venues/" + venue + "/stocks/" + stock + "/orders/" + orderId);
        return response.parseAs(OrderStatus.class);
    }

    public OrderStatus getOrderStatus(String venue, String stock, int orderId) throws IOException {
        HttpResponse response = executeGetRequest(STOCKFIGTHER_URL + "/venues/" + venue + "/stocks/" + stock + "/orders/" + orderId);
        return response.parseAs(OrderStatus.class);
    }

    private HttpResponse executePostRequest(String url, Object object) throws IOException {
        HttpRequestFactory requestFactory = getHttpRequestFactory();
        JsonHttpContent jsonHttpContent = new JsonHttpContent(new JacksonFactory(), object);
        HttpRequest request = requestFactory.buildPostRequest(new GenericUrl(url), jsonHttpContent);
        return request.execute();
    }

    private HttpResponse executeGetRequest(String url) throws IOException {
        HttpRequestFactory requestFactory = getHttpRequestFactory();
        HttpRequest request = requestFactory.buildGetRequest(new GenericUrl(url));
        return request.execute();
    }

    private HttpResponse executeDeleteRequest(String url) throws IOException {
        HttpRequestFactory requestFactory = getHttpRequestFactory();
        HttpRequest request = requestFactory.buildDeleteRequest(new GenericUrl(url));
        return request.execute();
    }

    private HttpRequestFactory getHttpRequestFactory() {
        return HTTP_TRANSPORT.createRequestFactory(request -> {
            request.setParser(new JsonObjectParser(JSON_FACTORY));
            request.getHeaders().set("X-Starfighter-Authorization", API_KEY);
        });
    }
}
