package org.baicaixiaozhan.simpleurlshortener.service;

import com.google.common.base.Strings;

import java.net.URI;
import java.util.Locale;

/**
 * DESC:
 *
 * @author baicaixiaozhan
 * @since 1.0.0
 */
public interface UrlShortenerService {

    default boolean isHttpOrHttps(String url) {
        if (Strings.isNullOrEmpty(url)) {
            return false;
        }
        return url.toLowerCase(Locale.ROOT).startsWith("http") ||
                url.toLowerCase(Locale.ROOT).startsWith("https");
    }

    /**
     * 根据 long url 构建 short url
     *
     * @param longUrl
     * @return
     */
    String buildShortUrl(String longUrl);

    /**
     * 根据 short code 获取 long url
     *
     * @param shortCode
     * @return
     */
    URI getLongUrlByShortCode(String shortCode);

}
