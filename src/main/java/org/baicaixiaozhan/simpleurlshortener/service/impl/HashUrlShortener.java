package org.baicaixiaozhan.simpleurlshortener.service.impl;

import com.google.common.base.Strings;
import com.google.common.hash.Hashing;
import org.baicaixiaozhan.simpleurlshortener.exception.UserPerceivableException;
import org.baicaixiaozhan.simpleurlshortener.service.UrlShortenerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Objects;

/**
 * DESC:
 *
 * @author baicaixiaozhan
 * @since 1.0.0
 */
@Service
public class HashUrlShortener implements UrlShortenerService {

    private static final Logger log = LoggerFactory.getLogger(HashUrlShortener.class);

    @Value("${server-prefix}")
    private String serverPrefix;
    private Cache cache;
    private final CacheManager cacheManager;

    public HashUrlShortener(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
        this.cache = this.cacheManager.getCache("simpleCache");
    }

    /**
     * 根据 long url 构建 short url
     *
     * @param longUrl
     * @return
     */
    @Override
    public String buildShortUrl(String longUrl) {
        if (isHttpOrHttps(longUrl)) {
            String shortCode = Hashing.murmur3_32_fixed()
                    .hashString(longUrl, StandardCharsets.UTF_8).toString();
            cache.put(shortCode, longUrl);
            return serverPrefix + "/api/shortUrl/" + shortCode;
        }
        throw new UserPerceivableException("Illegal URL.");
    }

    private boolean isHttpOrHttps(String url) {
        if (Strings.isNullOrEmpty(url)) {
            return false;
        }
        return url.toLowerCase(Locale.ROOT).startsWith("http") ||
                url.toLowerCase(Locale.ROOT).startsWith("https");
    }

    /**
     * 根据 short code 获取 long url
     *
     * @param shortCode
     * @return
     */
    @Override
    public URI getLongUrlByShortCode(String shortCode) {
        try {
            return new URI(Objects.requireNonNull(cache.get(shortCode).get()).toString());
        } catch (Exception e) {
            log.error("get long url exception with code {}", shortCode, e);
        }
        throw new UserPerceivableException("get long url exception with short code");
    }
}
