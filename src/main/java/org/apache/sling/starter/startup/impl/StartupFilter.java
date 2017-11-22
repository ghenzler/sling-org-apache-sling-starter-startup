/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.sling.starter.startup.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.stream.Collectors;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StartupFilter implements Filter {

    private static final Logger LOG = LoggerFactory.getLogger(StartupFilter.class);

    private String content;

    StartupFilter() {
        if (content == null) {
            InputStream is = StartupFilter.class.getClassLoader().getResourceAsStream("index.html");
            if (is != null) {
                BufferedReader buffer = null;
                try {
                    buffer = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                    content = buffer.lines().collect(Collectors.joining(System.lineSeparator()));
                } catch (UnsupportedEncodingException e) {
                    LOG.error("Cannot read embedded HTML page.", e);
                } finally {
                    if (buffer != null) {
                        try {
                            buffer.close();
                        } catch (IOException e) {
                            LOG.error("Unable to release resource.", e);
                        }
                    }
                }
            }

        }
    }

    @Override
    public void init(final FilterConfig filterConfig) {
        // nothing to do
    }

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException {

        HttpServletResponse httpResponse = (HttpServletResponse) response;
        httpResponse.setContentType("text/html");
        httpResponse.setCharacterEncoding("utf-8");
        httpResponse.setHeader("Cache-Control", "no-store");
        httpResponse.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
        if (!"".equals(content) && content != null) {
            final PrintWriter pw = response.getWriter();
            pw.append(content);
            pw.flush();
        }
    }

    @Override
    public void destroy() {
        // nothing to do
    }
}
