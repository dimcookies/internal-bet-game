package bet.service.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ClearCacheTask {

    @Autowired
    private CacheManager cacheManager;

    @Scheduled(fixedRateString = "3600000")
    public void clearCaches() {
        cacheManager.getCacheNames().parallelStream().forEach(name -> cacheManager.getCache(name).clear());
    }

    public void clearCache(String cacheName) {
        cacheManager.getCacheNames().parallelStream()
                .filter(s -> s.equals(cacheName))
                .forEach(name -> cacheManager.getCache(name).clear());
    }
}