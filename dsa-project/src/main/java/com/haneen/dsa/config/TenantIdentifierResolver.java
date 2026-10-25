package com.haneen.dsa.config;

import com.haneen.dsa.security.TenantContext;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

@Component
public class TenantIdentifierResolver implements CurrentTenantIdentifierResolver<Long>,HibernatePropertiesCustomizer {

    private static final Long ROOT = -1L;
    @Override
    public Long resolveCurrentTenantIdentifier(){
        Long tenantId = TenantContext.getTenantId();
        System.out.println("RESOLVER called, context=" + tenantId);
        return tenantId!=null? tenantId: ROOT;
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }

    @Override
    public void customize(Map<String, Object> hibernateProperties) {
        // Registers this resolver with Hibernate automatically
        hibernateProperties.put(AvailableSettings.MULTI_TENANT_IDENTIFIER_RESOLVER, this);
    }

    @Override
    public boolean isRoot(Long id){
        return Objects.equals(id, ROOT);
    }
}
