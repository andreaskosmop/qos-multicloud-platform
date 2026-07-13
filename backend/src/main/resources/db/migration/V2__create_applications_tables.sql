CREATE TABLE applications (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title       VARCHAR(255) NOT NULL,
    description TEXT,
    version     VARCHAR(50),
    status      VARCHAR(30) NOT NULL DEFAULT 'INITIAL',  -- ENUM: INITIAL/DEPLOYING/DEPLOYED/UNDEPLOYING/TERMINATED/FAILED
    owner_id    UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP
);

CREATE TABLE application_tags (
    application_id VARCHAR(255) NOT NULL REFERENCES applications(id) ON DELETE CASCADE,
    tag            VARCHAR(50) NOT NULL
);

CREATE TABLE cloud_configs (
    id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    app_id         UUID NOT NULL REFERENCES applications(id) ON DELETE CASCADE,
    cloud_provider VARCHAR(20) NOT NULL,  -- ENUM: AWS/AZURE
    instance_type  VARCHAR(50) NOT NULL,
    min_nodes      INTEGER NOT NULL DEFAULT 1,
    max_nodes      INTEGER NOT NULL DEFAULT 10,
    desired_nodes  INTEGER NOT NULL DEFAULT 2,
    region         VARCHAR(50),
    port_mappings  JSONB
);

CREATE TABLE qos_rules (
    id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    app_id            UUID NOT NULL REFERENCES applications(id) ON DELETE CASCADE,
    metric_name       VARCHAR(100) NOT NULL,
    operator          VARCHAR(5) NOT NULL,
    threshold_value   DOUBLE PRECISION NOT NULL,
    duration_seconds  INTEGER DEFAULT 0,
    action_type       VARCHAR(30) NOT NULL,  -- ENUM: SCALE_PODS_UP/DOWN/SCALE_NODES_UP/DOWN/NOTIFY
    action_value      DOUBLE PRECISION,
    action_target     VARCHAR(20) DEFAULT 'all',
    cooldown_seconds  INTEGER DEFAULT 300,
    is_active         BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE deployments (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    app_id              UUID NOT NULL REFERENCES applications(id),
    cloud_provider      VARCHAR(20) NOT NULL,  -- ENUM: AWS/AZURE
    cluster_endpoint    VARCHAR(255),
    prometheus_endpoint VARCHAR(255),
    node_count          INTEGER,
    status              VARCHAR(30) NOT NULL DEFAULT 'PROVISIONING',  -- ENUM: PROVISIONING/ACTIVE/FAILED/TERMINATED
    terraform_workspace VARCHAR(500),
    deployed_at         TIMESTAMP,
    terminated_at       TIMESTAMP
);

CREATE TABLE scaling_events (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    app_id        UUID NOT NULL REFERENCES applications(id),
    qos_rule_id   UUID REFERENCES qos_rules(id),
    triggered_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    metric_value  DOUBLE PRECISION,
    action_type   VARCHAR(30) NOT NULL,
    action_status VARCHAR(20) NOT NULL DEFAULT 'IN_PROGRESS',
    error_message TEXT
);

CREATE TABLE notifications (
    id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id    UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    app_id     UUID REFERENCES applications(id) ON DELETE SET NULL,
    type       VARCHAR(50) NOT NULL,
    title      VARCHAR(255) NOT NULL,
    message    TEXT NOT NULL,
    is_read    BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
