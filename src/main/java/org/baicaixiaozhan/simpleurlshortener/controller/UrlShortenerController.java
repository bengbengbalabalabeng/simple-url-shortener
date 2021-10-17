package org.baicaixiaozhan.simpleurlshortener.controller;

import org.baicaixiaozhan.simpleurlshortener.service.UrlShortenerService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;

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

    private final UrlShortenerService urlShortenerService;

    public UrlShortenerController(@Qualifier("hashUrlShortener") UrlShortenerService urlShortenerService) {
        this.urlShortenerService = urlShortenerService;
    }

    /**
     * build short url by long url
     *
     * @param url
     * @return
     */
    @PostMapping("/build")
    public ResponseEntity<?> getShortUrl(String url) {
        return ResponseEntity.ok(urlShortenerService.buildShortUrl(url));
    }

    /**
     * redirect url by short code
     *
     * @param shortCode
     * @return
     */
    @GetMapping("/{shortCode}")
    public ResponseEntity<?> redirectByShortCode(@PathVariable String shortCode) {
        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(urlShortenerService.getLongUrlByShortCode(shortCode));
        return new ResponseEntity<>(httpHeaders, MOVED_PERMANENTLY);
    }

}
