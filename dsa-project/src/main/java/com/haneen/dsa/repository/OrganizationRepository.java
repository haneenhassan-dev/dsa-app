package com.haneen.dsa.repository;
import com.haneen.dsa.model.Organization;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationRepository extends JpaRepository<Organization,Long> {
    boolean existsByOrgName(String orgName);
}
