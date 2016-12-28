package com.ekb.akka.parse;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class that parsers the router log.
 * Created by EdwinBrown on 12/23/2016.
 */
public class RouterParser {
    /**
     * Pattern
     */
    public static final Pattern pattern = Pattern.compile("(\\w{3}\\s+\\d{1,2} \\d{2}:\\d{2}:\\d{2} \\d{4}) (\\w+)\\" +
            ".(\\w+)<\\d+> ([\\w\\[\\]]+): (.*)");

    /** date */
    private String date;
    /** host or something */
    private String host;
    /** log level */
    private String level;
    /** protocol */
    private String protocol;
    /** message */
    private String message;
    /** bulk indicator */
    private boolean bulk;

    /**
     * Default
     */
    public RouterParser() {
    }

    /**
     * Constructor
     * @param record Router record
     */
    public RouterParser(String record) {
        parse(record);
    }

    /**
     * Parse the record
     * @param record Router record
     */
    public boolean parse(String record) {
        boolean parsed;

        Matcher matcher = pattern.matcher(record);
        if ((parsed = matcher.matches())) {
            date = matcher.group(1);
            host = matcher.group(2);
            level = matcher.group(3);
            protocol = matcher.group(4);
            message = matcher.group(5);
        }

        return parsed;
    }

    public String getDate() {
        return date;
    }

    public String getHost() {
        return host;
    }

    public String getLevel() {
        return level;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getMessage() {
        return message;
    }

    public void setBulk(boolean bulk) {
        this.bulk = bulk;
    }

    public boolean isBulk() {
        return bulk;
    }

    public Map<String, String> toMap() {
        HashMap<String, String> map = new HashMap<String, String>();

        map.put("date", date);
        map.put("host", host);
        map.put("level", level);
        map.put("protocol", protocol);
        map.put("message", message);

        return map;
    }

    public String toJson() {
        String json = null;

        json = "{\"date\": \"" + date + "\",\n" +
                 "\"host\": \"" + host + "\",\n" +
                 "\"level\": \"" + level + "\",\n" +
                 "\"protocol\": \"" + protocol + "\",\n" +
                 "\"message\": \"" + message + "\"}";

        return json;
    }

    @Override
    public String toString() {
        return "RouterParser{" +
                "date='" + date + '\'' +
                ", host='" + host + '\'' +
                ", level='" + level + '\'' +
                ", protocol='" + protocol + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
