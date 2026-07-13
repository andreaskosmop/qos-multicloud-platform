CREATE INDEX idx_applications_owner    ON applications(owner_id);
CREATE INDEX idx_applications_status   ON applications(status);
CREATE INDEX idx_cloud_configs_app     ON cloud_configs(app_id);
CREATE INDEX idx_qos_rules_app         ON qos_rules(app_id);
CREATE INDEX idx_deployments_app       ON deployments(app_id);
CREATE INDEX idx_deployments_status    ON deployments(status);
CREATE INDEX idx_scaling_events_app    ON scaling_events(app_id);
CREATE INDEX idx_scaling_events_time   ON scaling_events(triggered_at DESC);
CREATE INDEX idx_notifications_user    ON notifications(user_id, is_read);
