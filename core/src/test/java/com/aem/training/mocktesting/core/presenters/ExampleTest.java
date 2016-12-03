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
package com.aem.training.mocktesting.core.presenters;

import com.aem.training.mocktesting.core.services.AdminCasinoService;
import com.aem.training.mocktesting.core.services.GenericServiceInterface;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.Template;
import com.day.cq.wcm.api.WCMException;
import com.day.cq.wcm.api.WCMMode;
import com.google.common.collect.ImmutableMap;
import io.wcm.testing.mock.aem.junit.AemContext;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.osgi.framework.ServiceReference;

import javax.inject.Inject;
import java.util.Iterator;

public class ExampleTest {

    @Rule
    public final AemContext context = new AemContext();

    @Before
    public void setUp() {
        // register models from package
        context.addModelsForPackage("com.app1.models");

        // set runmode for unit test
        context.runMode("author");

        // load json data
        loadJson();
    }

    public void loadJson() {
        context.load().json("/sample-data.json", "/content/sample/en");
    }

    public void buildPage() {
        // create page
        context.create().page("/content/sample/en", "/apps/sample/template/homepage");
    }

    public void buildResource() {
        // create resource
        context.create().resource("/content/test1", ImmutableMap.<String, Object>builder()
                .put("prop1", "value1")
                .put("prop2", "value2")
                .build());
    }

    @Test
    public void testModel() {
        RequestAttributeModel model = context.request().adaptTo(RequestAttributeModel.class);
        // further testing
    }

    @Model(adaptables = SlingHttpServletRequest.class)
    interface RequestAttributeModel {
        @Inject
        String getProp1();
    }

    @Test
    public void testPage() {
        Resource resource = context.resourceResolver().getResource("/content/sample/en");
        Page page = resource.adaptTo(Page.class);
        // further testing
    }

    @Test
    public void testPageChildren() {
        Page page = context.pageManager().getPage("/content/sample/en");
        Template template = page.getTemplate();
        Iterator<Page> childPages = page.listChildren();
        // further testing
    }

    @Test
    public void testPageManagerOperations() throws WCMException {
        Page page = context.pageManager().create("/content/sample/en", "test1",
                "/apps/sample/templates/homepage", "title1");
        // further testing
        context.pageManager().delete(page, false);
    }

    public void simulateSlingRequest() {
        // prepare sling request
        context.request().setQueryString("param1=aaa&param2=bbb");

        context.requestPathInfo().setSelectorString("selector1.selector2");
        context.requestPathInfo().setExtension("html");

        // set current page
        context.currentPage("/content/sample/en");

        // set WCM Mode
        WCMMode.EDIT.toRequest(context.request());

    }

    @Mock
    private AdminCasinoService myService;

    public void registerOSGiService() {
        // register OSGi service
        context.registerService(AdminCasinoService.class, myService);

        // or alternatively: inject dependencies, activate and register OSGi service
        context.registerInjectActivateService(myService);

        // get OSGi service
        AdminCasinoService service = context.getService(AdminCasinoService.class);

        // or alternatively: get OSGi service via bundle context
        ServiceReference ref = context.bundleContext().getServiceReference(AdminCasinoService.class.getName());
        AdminCasinoService service2 = (AdminCasinoService) context.bundleContext().getService(ref);
    }

    public void registerAdapterFactory() {
        // register adapter factory
        context.registerService(myAdapterFactory);

        // test adaption
        MyClass object = resource.adaptTo(MyClass.class);
    }


}