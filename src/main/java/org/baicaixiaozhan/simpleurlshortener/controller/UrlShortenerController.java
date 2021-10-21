package org.baicaixiaozhan.simpleurlshortener.controller;

import org.baicaixiaozhan.simpleurlshortener.service.UrlShortenerService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.MOVED_PERMANENTLY;

/**
 * DESC:
 *
 * @author baicaixiaozhan
 * @since 1.0.0
 */
@RequestMapping("/api/shortUrl")
@RestController
public class UrlShortenerController {

    private final UrlShortenerService hashUrlShortener;
    private final UrlShortenerService base62UrlShortener;

    public UrlShortenerController(@Qualifier("hashUrlShortener") UrlShortenerService hashUrlShortener,
                                  @Qualifier("base62UrlShortener") UrlShortenerService base62UrlShortener) {
        this.hashUrlShortener = hashUrlShortener;
        this.base62UrlShortener = base62UrlShortener;
    }

    /**
     * use hash
     * build short url by long url
     *
     * @param url
     * @return
     */
    @PostMapping("/hash/build")
    public ResponseEntity<?> getShortUrl(String url) {
        return ResponseEntity.ok(hashUrlShortener.buildShortUrl(url));
    }

    /**
     * use hash
     * redirect url by short code
     *
     * @param shortCode
     * @return
     */
    @GetMapping("/hash/{shortCode}")
    public ResponseEntity<?> redirectByShortCode(@PathVariable String shortCode) {
        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(hashUrlShortener.getLongUrlByShortCode(shortCode));
        return new ResponseEntity<>(httpHeaders, MOVED_PERMANENTLY);
    }

    /**
     * use decimal to base62
     * build short url by long url
     *
     * @param url
     * @return
     */
    @PostMapping("/decimal2Base62/build")
    public ResponseEntity<?> getShortUrl2(String url) {
        return ResponseEntity.ok(base62UrlShortener.buildShortUrl(url));
    }

    /**
     * use decimal to base62
     * redirect url by short code
     *
     * @param shortCode
     * @return
     */
    @GetMapping("/decimal2Base62/{shortCode}")
    public ResponseEntity<?> redirectByShortCode2(@PathVariable String shortCode) {
        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(base62UrlShortener.getLongUrlByShortCode(shortCode));
        return new ResponseEntity<>(httpHeaders, MOVED_PERMANENTLY);
    }

}
