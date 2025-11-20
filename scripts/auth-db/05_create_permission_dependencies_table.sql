CREATE TABLE permission_dependencies (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    permission_id BIGINT NOT NULL,
    depends_on_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted BOOLEAN DEFAULT FALSE,
    CONSTRAINT fk_permission_dep_permission FOREIGN KEY (permission_id) REFERENCES permissions(id),
    CONSTRAINT fk_permission_dep_depends_on FOREIGN KEY (depends_on_id) REFERENCES permissions(id),
    UNIQUE KEY uniq_permission_dependency (permission_id, depends_on_id)
);

-- Indexes
CREATE INDEX idx_permission_dependencies_permission_id ON permission_dependencies(permission_id);
CREATE INDEX idx_permission_dependencies_depends_on_id ON permission_dependencies(depends_on_id);
CREATE INDEX idx_permission_dependencies_is_deleted ON permission_dependencies(is_deleted);
