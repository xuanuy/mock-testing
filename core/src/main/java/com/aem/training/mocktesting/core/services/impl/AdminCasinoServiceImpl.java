package com.aem.training.mocktesting.core.services.impl;

import com.aem.training.mocktesting.core.models.BaseModel;
import com.aem.training.mocktesting.core.services.AdminCasinoService;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;

import java.util.Collections;
import java.util.List;

@Component(immediate = true, name = "com.aem.training.mocktesting.core.services.impl.AdminCasinoServiceImpl")
@Service
public class AdminCasinoServiceImpl extends AbstractService<BaseModel> implements AdminCasinoService {

    @Override
    public List<String> getResourceTypes() {
        return Collections.singletonList("casinos/components/page/admin/casinoEntityPage");
    }

    @Override
    public boolean hasIsActiveProperty() {
        return false;
    }

    @Override
    public boolean isAdmin() {
        return true;
    }

    @Override
    protected String getAdminPath() {
        return "/content/mocktesting/admin";
    }

    @Override
    protected String getContentPath() {
        return "/content/mocktesting";
    }
}
