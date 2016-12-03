/*
 *  Copyright 2015 Adobe Systems Incorporated
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.aem.training.mocktesting.core.models;

import junitx.util.PrivateAccessor;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.jcr.JsonItemWriter;
import org.apache.sling.settings.SlingSettingsService;
import org.junit.Before;
import org.junit.Test;

import javax.jcr.*;
import java.io.StringWriter;
import java.util.UUID;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Simple JUnit test verifying the HelloWorldModel
 */
public class TestHelloWorldModel {

    //@Inject
    private HelloWorldModel hello;

    private String slingId;

    @Before
    public void setup() throws Exception {
        SlingSettingsService settings = mock(SlingSettingsService.class);
        slingId = UUID.randomUUID().toString();
        when(settings.getSlingId()).thenReturn(slingId);

        hello = new HelloWorldModel();
        PrivateAccessor.setField(hello, "settings", settings);
        hello.init();
    }

    @Test
    public void testGetMessage() throws Exception {
        // some very basic junit tests
        String msg = hello.getMessage();
        assertNotNull(msg);
        assertTrue(msg.length() > 0);
    }

    @Test
    public void testJson() throws RepositoryException, JSONException {
        Repository repository = JcrUtils.getRepository("http://localhost:4502/crx/server");
        Session session = repository.login(new SimpleCredentials("admin", "admin".toCharArray()));

        // Getting a particular node
        Node root = session.getRootNode();
        Node subContent = root.getNode("apps/geometrixx-gov/components/logo");


        // Iterating over the nodes and printing their names
        StringWriter stringWriter = new StringWriter();
        JsonItemWriter jsonWriter = new JsonItemWriter(null);
        jsonWriter.dump(subContent, stringWriter, -1, true);
        String json = stringWriter.toString();


        System.out.println(json);
    }
}
