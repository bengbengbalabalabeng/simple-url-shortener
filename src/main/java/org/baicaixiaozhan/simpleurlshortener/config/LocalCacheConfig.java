package org.baicaixiaozhan.simpleurlshortener.config;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.cache.support.SimpleValueWrapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

/**
 * DESC: use guava`s cache implement simple local cache config
 *
 * @author baicaixiaozhan
 * @since 1.0.0
 */
@Configuration
public class LocalCacheConfig {

    private static final Logger log = LoggerFactory.getLogger(LocalCacheConfig.class);

    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        Cache<Object, Object> cache = CacheBuilder.newBuilder()
                .initialCapacity(18)
                .maximumSize(Long.MAX_VALUE)
                .recordStats()
                .build();

        cacheManager.setCaches(Collections.singleton(new GuavaCache("simpleCache", cache)));
        cacheManager.afterPropertiesSet();
        return cacheManager;
    }

    /**
     * cache with guava implement
     */
    public static class GuavaCache implements org.springframework.cache.Cache {

        final String name;
        final Cache<Object, Object> cache;

        GuavaCache(String name, Cache<Object, Object> cache) {
            this.name = name;
            this.cache = cache;
        }

        /**
         * Return the cache name.
         */
        @Override
        public String getName() {
            return this.name;
        }

        /**
         * Return the underlying native cache provider.
         */
        @Override
        public Object getNativeCache() {
            return this.cache;
        }

        /**
         * Return the value to which this cache maps the specified key.
         * <p>Returns {@code null} if the cache contains no mapping for this key;
         * otherwise, the cached value (which may be {@code null} itself) will
         * be returned in a {@link ValueWrapper}.
         *
         * @param key the key whose associated value is to be returned
         * @return the value to which this cache maps the specified key,
         * contained within a {@link ValueWrapper} which may also hold
         * a cached {@code null} value. A straight {@code null} being
         * returned means that the cache contains no mapping for this key.
         * @see #get(Object, Class)
         * @see #get(Object, Callable)
         */
        @Override
        public ValueWrapper get(Object key) {
            return new SimpleValueWrapper(cache.getIfPresent(key));
        }

        /**
         * Return the value to which this cache maps the specified key,
         * generically specifying a type that return value will be cast to.
         * <p>Note: This variant of {@code get} does not allow for differentiating
         * between a cached {@code null} value and no cache entry found at all.
         * Use the standard {@link #get(Object)} variant for that purpose instead.
         *
         * @param key  the key whose associated value is to be returned
         * @param type the required type of the returned value (may be
         *             {@code null} to bypass a type check; in case of a {@code null}
         *             value found in the cache, the specified type is irrelevant)
         * @return the value to which this cache maps the specified key
         * (which may be {@code null} itself), or also {@code null} if
         * the cache contains no mapping for this key
         * @throws IllegalStateException if a cache entry has been found
         *                               but failed to match the specified type
         * @see #get(Object)
         * @since 4.0
         */
        @Override
        public <T> T get(Object key, Class<T> type) {
            final Object value = cache.getIfPresent(key);
            if (value != null && type != null && !type.isInstance(value)) {
                throw new IllegalStateException(
                        "Cached value is not of required type [" + type.getName() + "]: " + value);
            }
            return (T) value;
        }

        /**
         * Return the value to which this cache maps the specified key, obtaining
         * that value from {@code valueLoader} if necessary. This method provides
         * a simple substitute for the conventional "if cached, return; otherwise
         * create, cache and return" pattern.
         * <p>If possible, implementations should ensure that the loading operation
         * is synchronized so that the specified {@code valueLoader} is only called
         * once in case of concurrent access on the same key.
         * <p>If the {@code valueLoader} throws an exception, it is wrapped in
         * a {@link ValueRetrievalException}
         *
         * @param key         the key whose associated value is to be returned
         * @param valueLoader
         * @return the value to which this cache maps the specified key
         * @throws ValueRetrievalException if the {@code valueLoader} throws an exception
         * @see #get(Object)
         * @since 4.3
         */
        @Override
        public <T> T get(Object key, Callable<T> valueLoader) {
            T result = null;
            try {
                result = (T) cache.get(key, valueLoader);
            } catch (ExecutionException e) {
                log.error("get cache data exception.", e);
            }
            return result;
        }

        /**
         * Associate the specified value with the specified key in this cache.
         * <p>If the cache previously contained a mapping for this key, the old
         * value is replaced by the specified value.
         * <p>Actual registration may be performed in an asynchronous or deferred
         * fashion, with subsequent lookups possibly not seeing the entry yet.
         * This may for example be the case with transactional cache decorators.
         * Use {@link #putIfAbsent} for guaranteed immediate registration.
         *
         * @param key   the key with which the specified value is to be associated
         * @param value the value to be associated with the specified key
         * @see #putIfAbsent(Object, Object)
         */
        @Override
        public void put(Object key, Object value) {
            cache.put(key, value);
        }

        /**
         * Evict the mapping for this key from this cache if it is present.
         * <p>Actual eviction may be performed in an asynchronous or deferred
         * fashion, with subsequent lookups possibly still seeing the entry.
         * This may for example be the case with transactional cache decorators.
         * Use {@link #evictIfPresent} for guaranteed immediate removal.
         *
         * @param key the key whose mapping is to be removed from the cache
         * @see #evictIfPresent(Object)
         */
        @Override
        public void evict(Object key) {
            cache.invalidate(key);
        }

        /**
         * Clear the cache through removing all mappings.
         * <p>Actual clearing may be performed in an asynchronous or deferred
         * fashion, with subsequent lookups possibly still seeing the entries.
         * This may for example be the case with transactional cache decorators.
         * Use {@link #invalidate()} for guaranteed immediate removal of entries.
         *
         * @see #invalidate()
         */
        @Override
        public void clear() {
            cache.invalidateAll();
        }
    }

}
