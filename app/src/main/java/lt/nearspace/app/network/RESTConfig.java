package lt.nearspace.app.network;

import java.text.SimpleDateFormat;

public class RESTConfig {

    public static final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
    public static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat(DATE_FORMAT_NOW);

    public static final int REQUEST_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 10000;

    public static final String DEFAULT_HOST = "";
    public static final String DEFAULT_METHOD = "GET";
    public static final String DEFAULT_CONTENT_TYPE = "application/json; charset=utf-8";
    public static final String DEFAULT_ACCEPT = "*/*";

    //public static final String DOMAIN = "http://192.168.88.147";
    //public static final String DOMAIN = "http://192.168.0.103";
    //public static final String DOMAIN = "http://172.16.174.163";
    public static final String DOMAIN = "http://79.98.29.149";
    public static final String GET_ARTICLE_LIST = "/api/news/list.json";
    public static final String GET_SINGLE_ARTICLE = "/api/news/%d/article.json";
    public static final String GET_ARTICLES = "/api/news/%d/articles/later/then.json";
    public static final String POST_ARTICLE = "/api/authorizeds/news/articles.json";
    public static final String POST_VOTES = "/api/authorizeds/votes/lists.json";
    public static final String WSSE_AUTH = "/api/check/user.json";
    public static final String GET_SALT = "/api/users/%s/public/data.json";

    //public static final String DOMAIN = "http://bim.int.bite.lt";

}
