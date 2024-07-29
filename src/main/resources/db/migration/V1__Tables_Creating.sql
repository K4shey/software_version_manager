DROP TABLE IF EXISTS configurations;
DROP TABLE IF EXISTS departments;

-- Создание таблицы departments
CREATE TABLE departments
(
    department_id        BIGSERIAL PRIMARY KEY,
    parent_department_id BIGINT
        CONSTRAINT foreign_key_parent_department REFERENCES departments,
    department_name      VARCHAR(255) NOT NULL,
    head_email           VARCHAR(255),
    head_name            VARCHAR(255),
    head_phone           VARCHAR(255),
    note                 VARCHAR(255)
);

-- Создание таблицы configurations
CREATE TABLE configurations
(
    configuration_id    BIGSERIAL PRIMARY KEY,
    department_id       BIGINT       NOT NULL
        CONSTRAINT foreign_key_department
            REFERENCES departments ON DELETE CASCADE,
    configuration_name  VARCHAR(255) NOT NULL,
    configuration_alias VARCHAR(255) NOT NULL,
    current_version     VARCHAR(255),
    latest_version      VARCHAR(255),
    status              VARCHAR(20)  NOT NULL
);

-- Создание индекса на поле status таблицы configurations
CREATE INDEX idx_configurations_status ON configurations (status);

-- Создание составного индекса на поля department_id и status таблицы configurations
CREATE INDEX idx_configurations_department_id_status ON configurations (department_id, status);


ALTER TABLE departments
    OWNER TO "user";
ALTER TABLE configurations
    OWNER TO "user";

-- Добавление комментариев к таблице departments
COMMENT ON TABLE departments IS 'Таблица, содержащая информацию о подразделениях';
COMMENT ON COLUMN departments.department_id IS 'Уникальный идентификатор подразделения';
COMMENT ON COLUMN departments.parent_department_id IS 'Ссылка на родительское подразделение';
COMMENT ON COLUMN departments.department_name IS 'Название подразделения';
COMMENT ON COLUMN departments.head_email IS 'Email руководителя подразделения';
COMMENT ON COLUMN departments.head_name IS 'Имя руководителя подразделения';
COMMENT ON COLUMN departments.head_phone IS 'Телефон руководителя подразделения';
COMMENT ON COLUMN departments.note IS 'Заметки о подразделении';

-- Добавление комментариев к таблице configurations
COMMENT ON TABLE configurations IS 'Таблица, содержащая информацию о конфигурациях';
COMMENT ON COLUMN configurations.configuration_id IS 'Уникальный идентификатор конфигурации';
COMMENT ON COLUMN configurations.department_id IS 'Ссылка на подразделение, к которому относится конфигурация';
COMMENT ON COLUMN configurations.configuration_name IS 'Название конфигурации';
COMMENT ON COLUMN configurations.configuration_alias IS 'Псевдоним конфигурации';
COMMENT ON COLUMN configurations.current_version IS 'Текущая версия конфигурации';
COMMENT ON COLUMN configurations.latest_version IS 'Последняя версия конфигурации';
COMMENT ON COLUMN configurations.status IS 'Статус конфигурации (новая, в актуальном состоянии, требуется обновление, ошибка)';
