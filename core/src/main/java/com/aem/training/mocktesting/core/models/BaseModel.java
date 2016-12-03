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

import com.day.cq.wcm.api.Page;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;

import javax.inject.Inject;

@SuppressWarnings("squid:S2095")
@Model(adaptables = Resource.class)
public abstract class BaseModel {

    @Inject
    @Optional
    private String id;

    @Inject
    @Optional
    private ResourceResolver resourceResolver;

    @JsonIgnore
    private String path;

    @JsonIgnore
    private Page page;

    public BaseModel() {
        super();
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(final String path) {
        this.path = path;
    }

    protected ResourceResolver getResourceResolver() {
        return resourceResolver;
    }

    public Page getPage() {
        return page;
    }

    public void setPage(final Page page) {
        this.page = page;
    }
}
