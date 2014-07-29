/*
 * Copyright 2005 Sun Microsystems, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.rometools.fetcher.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.nio.file.Path;

/**
 * Disk based cache.
 */
public class DiskFeedInfoCache implements FeedFetcherCache {

    protected final Path cachePath;

    public DiskFeedInfoCache(final Path cachePath) {
        this.cachePath = cachePath;
    }

    @Override
    public SyndFeedInfo getFeedInfo(final URL url) {
        SyndFeedInfo info = null;
        final Path fileName = cachePath.resolve("feed_" + replaceNonAlphanumeric(url.toString(), '_').trim());
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            fis = new FileInputStream(fileName.toFile());
            ois = new ObjectInputStream(fis);
            info = (SyndFeedInfo) ois.readObject();
        } catch (final FileNotFoundException e) {
            // That's OK, we'l return null
        } catch (final ClassNotFoundException e) {
            // Error writing to cache is fatal
            throw new RuntimeException("Attempting to read from cache", e);
        } catch (final IOException e) {
            // Error writing to cache is fatal
            throw new RuntimeException("Attempting to read from cache", e);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (final IOException e) {
                }
            }
            if (ois != null) {
                try {
                    ois.close();
                } catch (final IOException e) {
                }
            }
        }
        return info;
    }

    @Override
    public void setFeedInfo(final URL url, final SyndFeedInfo feedInfo) {
        final Path fileName = cachePath.resolve("feed_" + replaceNonAlphanumeric(url.toString(), '_').trim());
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(fileName.toFile());
            final ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(feedInfo);
            fos.flush();
            fos.close();
        } catch (final Exception e) {
            // Error writing to cache is fatal
            throw new RuntimeException("Attempting to write to cache", e);
        }
    }

    public static String replaceNonAlphanumeric(final String str, final char subst) {
        final StringBuffer ret = new StringBuffer(str.length());
        final char[] testChars = str.toCharArray();
        for (final char testChar : testChars) {
            if (Character.isLetterOrDigit(testChar)) {
                ret.append(testChar);
            } else {
                ret.append(subst);
            }
        }
        return ret.toString();
    }

    /**
     * Clear the cache.
     */
    @Override
    public synchronized void clear() {
        final File file = cachePath.toFile();
        // only do the delete if the directory exists
        if (file.exists() && file.canWrite()) {
            // make the directory empty
            for (File deleteMe : file.listFiles())
                deleteMe.delete();

            // don't delete the cache directory
        }
    }

    @Override
    public SyndFeedInfo remove(final URL url) {

        SyndFeedInfo info = null;
        final Path fileName = cachePath.resolve("feed_" + replaceNonAlphanumeric(url.toString(), '_').trim());
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        boolean consumed = false;

        try {

            fis = new FileInputStream(fileName.toFile());
            ois = new ObjectInputStream(fis);
            info = (SyndFeedInfo) ois.readObject();
            consumed = true;

        } catch (final FileNotFoundException e) {
            // That's OK, we'l return null
        } catch (final ClassNotFoundException e) {
            // Error writing to cache is fatal
            throw new RuntimeException("Attempting to read from cache", e);
        } catch (final IOException e) {
            // Error writing to cache is fatal
            throw new RuntimeException("Attempting to read from cache", e);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (final IOException e) {
                }
            }
            if (ois != null) {
                try {
                    ois.close();
                } catch (final IOException e) {
                }
            }
            if (consumed) {
                final File file = fileName.toFile();
                if (file.exists()) {
                    file.delete();
                }
            }
        }

        return info;
    }
}
