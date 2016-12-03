package com.aem.training.mocktesting.core.constants;

import com.day.cq.commons.jcr.JcrConstants;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Locale;
import java.util.regex.Pattern;

public final class Constants {

    private Constants() {
    }

    public static final String SLING_RESOURCE_TYPE = "sling:resourceType";
    public static final String NT_UNSTRUCTURED = JcrConstants.NT_UNSTRUCTURED;
    public static final String CQ_PAGECONTENT = "cq:PageContent";
    public static final String CQ_PAGE = "cq:Page";
    public static final String CQ_TEMPLATE = "cq:template";
    public static final String IS_ACTIVE_PROPERTY = "isActive";

    // Format link
    public static final Pattern EXTENSION_PATTERN_LINK = Pattern.compile(".*\\.\\w{2,4}$");
    public static final String PREFIX_HTTP = "http://";
    public static final String PREFIX_HTTPS = "https://";
    public static final String QUESTION_MARK = "?";
    public static final String HASH = "#";
    public static final String HTML_SUFFIX = ".html";

    // Youtube video id
    public static final Pattern YOUTUBE_VIDEO_ID_PATTERN = Pattern.compile("v=[^&/]+");

    // Object Mapper
    public static final ObjectMapper MAPPER = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    static {
        MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    /**
     * The application default {@link Locale}. By default the default {@link Locale} is {@link Locale#FRANCE}.
     */
    public static final Locale DEFAULT_LOCALE = Locale.FRANCE;
    /**
     * The default {@link Locale} is used in admin pages. In admin pages, we use locale {@link Locale#ENGLISH}.
     */
    public static final Locale DEFAULT_ADMIN_LOCALE = Locale.UK;

    // admin service user
    public static final String ADMIN_SERVICE_NAME = "admin-service";
}
