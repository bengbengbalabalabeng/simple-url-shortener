package org.baicaixiaozhan.simpleurlshortener.service;

import java.net.URI;

/**
 * DESC:
 *
 * @author baicaixiaozhan
 * @since 1.0.0
 */
public interface UrlShortenerService {

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
