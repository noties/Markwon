package io.noties.markwon.ext.latex;

import android.graphics.drawable.Drawable;
import android.util.LruCache;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import ru.noties.jlatexmath.JLatexMathDrawable;

/**
 * Cache for JLatexMathDrawable instances to improve performance
 * by avoiding redundant rendering of the same LaTeX formulas.
 * 
 */
public class JLatexMathDrawableCache {

    private static final int DEFAULT_CACHE_SIZE = 32;
    
    // Singleton instance
    private static volatile JLatexMathDrawableCache instance;
    
    // LRU cache to store rendered LaTeX drawables
    private final LruCache<String, Object> cache;
    
    // Whether to enable the cache
    private final boolean enabled;
    
    private JLatexMathDrawableCache(int maxSize, boolean enabled) {
        this.cache = new LruCache<>(maxSize);
        this.enabled = enabled;
    }
    
    /**
     * Get the singleton instance of the cache
     */
    @NonNull
    public static JLatexMathDrawableCache getInstance() {
        if (instance == null) {
            synchronized (JLatexMathDrawableCache.class) {
                if (instance == null) {
                    instance = new JLatexMathDrawableCache(DEFAULT_CACHE_SIZE, true);
                }
            }
        }
        return instance;
    }
    
    /**
     * Create a new instance with custom configuration
     * 
     * @param maxSize maximum number of entries in the cache
     * @param enabled whether the cache is enabled
     * @return a new cache instance
     */
    @NonNull
    public static JLatexMathDrawableCache create(int maxSize, boolean enabled) {
        return new JLatexMathDrawableCache(maxSize, enabled);
    }
    
    /**
     * Get a drawable from the cache
     * 
     * @param key the LaTeX formula string
     * @return the cached drawable or null if not found
     */
    /**
     * Cache entry containing both the drawable and its configuration
     */
    private static class CacheEntry {
        final Drawable drawable;
        final JLatexMathDrawable.Builder builder;
        
        CacheEntry(Drawable drawable, JLatexMathDrawable.Builder builder) {
            this.drawable = drawable;
            this.builder = builder;
        }
    }
    
    @Nullable
    public Drawable get(@NonNull String key) {
        if (!enabled) {
            return null;
        }
        
        synchronized (cache) {
            final CacheEntry entry = (CacheEntry) cache.get(key);
            if (entry != null) {
                if (entry.drawable instanceof JLatexMathDrawable && entry.builder != null) {
                    // Recreate the drawable with the same configuration
                    return entry.builder.build();
                }
                return entry.drawable;
            }
            return null;
        }
    }
    
    /**
     * Store a drawable in the cache
     * 
     * @param key the LaTeX formula string
     * @param drawable the drawable to cache
     */
    public void put(@NonNull String key, @NonNull Drawable drawable) {
        put(key, drawable, null);
    }
    
    /**
     * Store a drawable in the cache with its builder for recreation
     * 
     * @param key the LaTeX formula string
     * @param drawable the drawable to cache
     * @param builder the builder used to create the drawable
     */
    public void put(@NonNull String key, @NonNull Drawable drawable, @Nullable JLatexMathDrawable.Builder builder) {
        if (!enabled) {
            return;
        }
        
        synchronized (cache) {
            cache.put(key, new CacheEntry(drawable, builder));
        }
    }
    
    /**
     * Clear the cache
     */
    public void clear() {
        synchronized (cache) {
            cache.evictAll();
        }
    }
    
    /**
     * Get the current size of the cache
     */
    public int size() {
        synchronized (cache) {
            return cache.size();
        }
    }
}
