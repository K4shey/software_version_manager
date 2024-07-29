INSERT INTO departments (department_name, head_name, head_phone, head_email, note)
VALUES ('Финансовая дирекция', 'Паниковский М.С.', '+7(925)223-322-223', 'vector@mail.com', 'Just another note');

INSERT INTO departments (department_name, parent_department_id, head_name, head_phone, head_email, note)
VALUES ('Бухгалтерия', 1, 'Бендер О.И.', '+7(495)777-33-11', 'bender_i@ya.ru', 'Just another note');

INSERT INTO departments (department_name, parent_department_id, head_name, head_phone, head_email, note)
VALUES ('Отдел расчета ЗП', 1, 'Балаганов А.А.', '+7(495)888-91-11', 'bez@yahoo.com', 'Just another note');


INSERT INTO configurations (configuration_name, configuration_alias, current_version, latest_version, department_id,
                            status)
VALUES ('ARAutomation', 'Комплексная автоматизация', '2.4.13.275', '1.1.1.1', 1, 'NEW');

INSERT INTO configurations (configuration_name, configuration_alias, current_version, latest_version, department_id,
                            status)
VALUES ('Accounting', 'Бухгалтерия предприятия', '3.0.152.28', '1.1.1.1', 2, 'NEW');

INSERT INTO configurations (configuration_name, configuration_alias, current_version, latest_version, department_id,
                            status)
VALUES ('HRM', 'Зарплата и управление персоналом', '3.1.29.78', '1.1.1.1', 3, 'NEW');