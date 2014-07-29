package com.rometools.test;

import com.rometools.fetcher.impl.DiskFeedInfoCache;
import com.rometools.fetcher.impl.SyndFeedInfo;
import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import junit.framework.TestCase;

public class DiskFeedInfoCacheTest extends TestCase {

    private static final Path TEST_CACHE = Paths.get("test-cache").toAbsolutePath();
    public void testClear() throws Exception {
        final File cacheDir = TEST_CACHE.toFile();
        cacheDir.mkdir();
        cacheDir.deleteOnExit();

        final DiskFeedInfoCache cache = new DiskFeedInfoCache(TEST_CACHE);
        final SyndFeedInfo info = new SyndFeedInfo();
        final URL url = new URL("http://nowhere.com");
        cache.setFeedInfo(url, info);

        cache.clear();
        final Object returned = cache.getFeedInfo(url);
        assertTrue(returned == null);
    }

    public void testRemove() throws Exception {
        final File cacheDir = new File("test-cache");
        cacheDir.mkdir();
        cacheDir.deleteOnExit();

        final DiskFeedInfoCache cache = new DiskFeedInfoCache(TEST_CACHE);
        final SyndFeedInfo info = new SyndFeedInfo();
        final URL url = new URL("http://nowhere.com");
        cache.setFeedInfo(url, info);

        final SyndFeedInfo removedInfo = cache.remove(url);
        assertTrue(removedInfo.equals(info));
        final SyndFeedInfo shouldBeNull = cache.remove(url);
        assertTrue(null == shouldBeNull);
    }

}
