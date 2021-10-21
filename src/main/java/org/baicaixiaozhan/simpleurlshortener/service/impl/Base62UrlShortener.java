package org.baicaixiaozhan.simpleurlshortener.service.impl;

import org.baicaixiaozhan.simpleurlshortener.exception.UserPerceivableException;
import org.baicaixiaozhan.simpleurlshortener.service.UrlShortenerService;
import org.baicaixiaozhan.simpleurlshortener.util.UniqueNumberBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.Objects;

/**
 * DESC:
 *
 * @author baicaixiaozhan
 * @since 1.0.0
 */
@Service
public class Base62UrlShortener implements UrlShortenerService {

    private static final Logger log = LoggerFactory.getLogger(Base62UrlShortener.class);

    @Value("${server-prefix}")
    private String serverPrefix;
    private Cache cache;
    private final CacheManager cacheManager;
    @Qualifier("simpleUniqueNumberBuilder")
    private final UniqueNumberBuilder<String> uniqueNumberBuilder;

    public Base62UrlShortener(CacheManager cacheManager, UniqueNumberBuilder<String> uniqueNumberBuilder) {
        this.cacheManager = cacheManager;
        this.uniqueNumberBuilder = uniqueNumberBuilder;
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
            String shortCode = uniqueNumberBuilder.build();
            cache.put(shortCode, longUrl);
            return serverPrefix + "/api/shortUrl/decimal2Base62/" + shortCode;
        }
        throw new UserPerceivableException("Illegal URL.");
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
