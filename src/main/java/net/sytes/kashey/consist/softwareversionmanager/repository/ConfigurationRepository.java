package net.sytes.kashey.consist.softwareversionmanager.repository;


import net.sytes.kashey.consist.softwareversionmanager.model.Configuration;
import net.sytes.kashey.consist.softwareversionmanager.specification.configuration.ConfigurationSpecification;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConfigurationRepository {
    List<Configuration> findAll();

    Optional<Configuration> findById(Long id);

    Configuration save(Configuration configuration);

    void delete(Configuration configuration);

    List<Configuration> findAllWithSpecification(ConfigurationSpecification specification, int page, int size);
}