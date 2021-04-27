package com.lnstow.jungle0;

public class BaseJungle {
    public static final byte MOVIE_LIST = 1;
    public static final byte MOVIE_DETAIL = 2;
    public static final byte MOVIE_HOME = 3;
    public static final byte MOVIE_FAVORITE = 4;
    public static final byte MOVIE_SETTING = 5;
    public static final byte MOVIE_READ = 6;
    public static final String JUNGLE_URL = "https://jg0.net/av_info/zh/";
    public static final String JUNGLE_URL_SEARCH = JUNGLE_URL + "?search_type=id&search=";
    public static final String JUNGLE_URL_MOE = JUNGLE_URL + "?ty=kawaii";
    public static final String JUNGLE_URL_HISTORY = JUNGLE_URL + "?ty=h";
    public static final int JUNGLE_LENGTH = JUNGLE_URL.length();
    public static final byte VIEW_SIZE_SMALL = 1;
    public static final byte VIEW_SIZE_BIG = 2;
    public static final byte VIEW_SIZE_SAFE = 3;
    public static final byte VIEW_SIZE_MEDIUM = 4;
    public static final String[] PEOPLE_NUM = new String[]{"All", "1", "2", "3", "over4", "0"};
    public static final String DATABASE_NAME = "movie_record.db";
    public static byte DEFAULT_VIEW_SIZE;
    public static int DEFAULT_PEOPLE;


    public static String addQueryParam(String src, int peopleNumIndex, int page) {
        if (src.charAt(src.length() - 1) == '/') {
            return src +
                    "?hm=" + PEOPLE_NUM[peopleNumIndex] +
                    "&s=" + page;
        } else {
            return src +
                    "&hm=" + PEOPLE_NUM[peopleNumIndex] +
                    "&s=" + page;
        }
    }
}
