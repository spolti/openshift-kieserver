/**
 *  Copyright 2016 Red Hat, Inc.
 *
 *  Red Hat licenses this file to you under the Apache License, version
 *  2.0 (the "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *  implied.  See the License for the specific language governing
 *  permissions and limitations under the License.
 */
package org.openshift.kieserver.web.redirect;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class RedirectServletRequestWrapper extends HttpServletRequestWrapper {

    private final HttpServletRequest request;
    private final Map<String, String[]> parameterOverrides;

    public RedirectServletRequestWrapper(HttpServletRequest request, Map<String, String[]> parameterOverrides) {
        super(request);
        this.request = request;
        this.parameterOverrides = parameterOverrides;
    }

    @Override
    public String getParameter(String name) {
        String[] values = getParameterValues(name);
        return values != null && values.length > 0 ? values[0] : null;
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        Map<String, String[]> parameterMap = new HashMap<String, String[]>();
        parameterMap.putAll(request.getParameterMap());
        parameterMap.putAll(parameterOverrides);
        return parameterMap;
    }

    @Override
    public Enumeration<String> getParameterNames() {
        return Collections.enumeration(getParameterMap().keySet());
    }

    @Override
    public String[] getParameterValues(String name) {
        return getParameterMap().get(name);
    }

    @Override
    public String getQueryString() {
        // TODO: fix regex so we don't need to add the preceding question mark
        String queryString = request.getQueryString();
        if (!queryString.startsWith("?")) {
            queryString = "?" + queryString;
        }
        for (String name : parameterOverrides.keySet()) {
            String value = getParameter(name);
            queryString = queryString.replaceAll("(?<=[?&;])" + name + "=[^&;]*", name + "=" + value);
        }
        return queryString.startsWith("?") ? queryString.substring(1, queryString.length()) : queryString;
    }

}
