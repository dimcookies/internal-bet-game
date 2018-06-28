package bet.service.utils;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.Status;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.CommonsLogWriter;

import java.io.PrintWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Utility for EhCache
 *
 * @author n.kazarian
 *
 */
public abstract class EhCacheUtils {

	private static final Log log = LogFactory.getLog(EhCacheUtils.class);

	private EhCacheUtils() {
	}

	public static void clearCache() {
		for (CacheManager cm : CacheManager.ALL_CACHE_MANAGERS) {
			if (cm.getStatus().equals(Status.STATUS_ALIVE)) {
				for (String name : cm.getCacheNames()) {
					Ehcache cache = cm.getEhcache(name);
					if (cache.getStatus().equals(Status.STATUS_ALIVE)) {
						log.info("Cache " + name + " contains " + cache.getSize() + " objects.");
						cache.removeAll();
						log.info("Clearing cache " + name + ". Now contains " + cache.getSize() + " objects.");
					}
				}
			}
		}
	}

}
