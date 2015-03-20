/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.crankycoder.demostumbler;

import org.mozilla.mozstumbler.service.core.http.HTTPResponse;
import org.mozilla.mozstumbler.service.core.http.IHttpUtil;
import org.mozilla.mozstumbler.service.core.http.IResponse;
import org.mozilla.mozstumbler.service.utils.Zipper;
import org.mozilla.mozstumbler.svclocator.ServiceLocator;
import org.mozilla.mozstumbler.svclocator.services.log.ILogger;
import org.mozilla.mozstumbler.svclocator.services.log.LoggerUtil;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.net.URL;
import java.util.Map;

@SuppressWarnings("unused")
public class DebugHttpUtil implements IHttpUtil {
    private static String LOG_TAG = LoggerUtil.makeLogTag(DebugHttpUtil.class);
    private static ILogger Log = (ILogger) ServiceLocator.getInstance().getService(ILogger.class);

    public String getUrlAsString(URL url) throws IOException {
        return "abc";
    }

    @Override
    public String getUrlAsString(String url) throws IOException {
        return "abc";
    }

    @Override
    public InputStream getUrlAsStream(String url) throws IOException {
        return new ByteArrayInputStream("abc".getBytes());
    }

    @Override
    public File getUrlAsFile(URL url, File file) throws IOException {
        Writer writer = new FileWriter(file);
        writer.write(getUrlAsString(url));
        return file;
    }

    @Override
    public IResponse get(String urlString, Map<String, String> headers) {
        Log.i(LOG_TAG, "GET " + urlString + "|" + headers.toString());
        return new HTTPResponse(200, 0);
    }

    @Override
    public IResponse head(String latestUrl, Map<String, String> headers) {
        Log.i(LOG_TAG, "HEAD " + latestUrl+ "|" + headers.toString());
        return new HTTPResponse(200, 0);
    }

    @Override
    public IResponse post(String urlString, byte[] data, Map<String, String> headers, boolean precompressed) {
        if (precompressed) {
            try {
                String newData = Zipper.unzipData(data);
                Log.i(LOG_TAG, "POST " + urlString+ "|" + headers.toString() + "|" + newData);
            } catch (IOException e) {
                Log.i(LOG_TAG, "POST " + urlString+ "|" + headers.toString() + "|" + data.toString());
                Log.w(LOG_TAG, "Error unzipping: " + e.toString());
            }
         }

        return new HTTPResponse(200, 0);
    }
}
