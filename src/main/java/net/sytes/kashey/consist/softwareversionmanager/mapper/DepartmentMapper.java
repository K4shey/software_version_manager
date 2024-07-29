package net.sytes.kashey.consist.softwareversionmanager.mapper;


import net.sytes.kashey.consist.softwareversionmanager.dto.department.DepartmentDto;
import net.sytes.kashey.consist.softwareversionmanager.model.Department;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DepartmentMapper {

    DepartmentDto toDto(Department model);
}
