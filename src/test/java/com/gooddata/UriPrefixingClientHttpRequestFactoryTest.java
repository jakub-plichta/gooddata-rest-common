/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata;

import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.AsyncClientHttpRequest;
import org.springframework.http.client.AsyncClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URI;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class UriPrefixingClientHttpRequestFactoryTest {

    @DataProvider(name = "factory")
    public Object[][] createFactories() {
        final SimpleClientHttpRequestFactory wrapped = new SimpleClientHttpRequestFactory();
        wrapped.setTaskExecutor(new SimpleAsyncTaskExecutor());

        return new Object[][] {
                new Object[] {
                        new UriPrefixingClientHttpRequestFactory(wrapped, "http", "localhost", 1234)
                },
                new Object[] {
                        new UriPrefixingClientHttpRequestFactory(wrapped, "http://localhost:1234")
                }
        };
    }

    @Test(dataProvider = "factory")
    public void createRequest(final ClientHttpRequestFactory requestFactory) throws IOException {
        final ClientHttpRequest request = requestFactory.createRequest(URI.create("/gdc/resource"), HttpMethod.GET);
        assertThat(request.getURI().toString(), is("http://localhost:1234/gdc/resource"));
    }

    @Test(dataProvider = "factory")
    public void createAsyncRequest(final AsyncClientHttpRequestFactory requestFactory) throws IOException {
        final AsyncClientHttpRequest request = requestFactory.createAsyncRequest(URI.create("/gdc/resource"), HttpMethod.GET);
        assertThat(request.getURI().toString(), is("http://localhost:1234/gdc/resource"));
    }

    @Test(expectedExceptions = UnsupportedOperationException.class)
    public void createAsyncRequest_notAsyncFactory() throws IOException {
        final ClientHttpRequestFactory wrapped = (uri, httpMethod) -> null;
        final UriPrefixingClientHttpRequestFactory requestFactory = new UriPrefixingClientHttpRequestFactory(wrapped, "http", "localhost", 1234);

        requestFactory.createAsyncRequest(URI.create("/gdc/resource"), HttpMethod.GET);
    }
}
