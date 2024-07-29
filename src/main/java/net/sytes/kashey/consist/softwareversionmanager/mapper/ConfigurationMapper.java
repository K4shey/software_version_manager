package net.sytes.kashey.consist.softwareversionmanager.mapper;

import net.sytes.kashey.consist.softwareversionmanager.dto.configuration.onec.OneCConfigurationDto;
import net.sytes.kashey.consist.softwareversionmanager.model.Configuration;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ConfigurationMapper {

    OneCConfigurationDto toDto(Configuration model);
}
