package net.sytes.kashey.consist.softwareversionmanager.dto.department;

public record DepartmentDto(
        String departmentName,
        Long parentDepartmentId,
        String headName,
        String headPhone,
        String headEmail,
        String note) {
}
