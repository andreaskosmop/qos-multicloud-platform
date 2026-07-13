package gr.unipi.cloudplatform.service;

import gr.unipi.cloudplatform.model.entity.Deployment;
import gr.unipi.cloudplatform.model.enums.DeploymentStatus;
import gr.unipi.cloudplatform.repository.DeploymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Ο PrometheusService χρησιμοποιεί RestTemplate για HTTP calls στο Prometheus API.
 * Ένα σημαντικό feature είναι η υποστήριξη multi-cloud: αν η εφαρμογή τρέχει
 * σε AWS + Azure, queries γίνονται στα 2 Prometheus instances και επιστρέφεται
 * ο μέσος όρος.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class PrometheusService {

    private final RestTemplate restTemplate;
    private final DeploymentRepository deploymentRepository;

    public Double queryMetric(String appId, String metricName) {
        List<Deployment> active = deploymentRepository
                .findByApplicationIdAndStatus(appId, DeploymentStatus.ACTIVE);
        if (active.isEmpty()) return null;

        List<Double> values = active.stream()
                .map(d -> queryFromInstance(d.getPrometheusEndpoint(),
                        buildPromQL(metricName, appId)))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        // Μέσος όρος από πολλαπλά clouds
        return values.isEmpty() ? null
                : values.stream().mapToDouble(Double::doubleValue).average().orElse(0);
    }

    private Double queryFromInstance(String url, String promql) {
        try {
            String endpoint = url + "/api/v1/query?query="
                    + URLEncoder.encode(promql, StandardCharsets.UTF_8);
            // Σημείωση: εδώ θα γινόταν το πραγματικό RestTemplate.getForEntity(...)
            // με parsing του PrometheusResponse — παραλείπεται εδώ για συντομία.
            log.debug("Querying Prometheus: {}", endpoint);
            return null; // TODO: υλοποίηση πλήρους response parsing
        } catch (Exception e) {
            log.warn("Prometheus query failed: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Οι μετρικές response_time και error_rate υποστηρίζονται υπό την προϋπόθεση
     * ότι η εφαρμογή εκθέτει τυποποιημένες HTTP μετρικές (http_request_duration_seconds,
     * http_requests_total) — τυπική σύμβαση ονοματολογίας Prometheus/OpenMetrics που
     * παρέχεται αυτόματα από βιβλιοθήκες instrumentation όπως το Micrometer
     * (Spring Boot Actuator) ή αντίστοιχες σε άλλες πλατφόρμες.
     */
    private String buildPromQL(String metric, String appId) {
        return switch (metric) {
            case "cpu_usage" ->
                    "avg(rate(container_cpu_usage_seconds_total{namespace=\"" + appId + "\"}[5m]))*100";
            case "memory_usage" ->
                    "avg(container_memory_usage_bytes{namespace=\"" + appId + "\"})/1048576";
            case "response_time" ->
                    "avg(http_request_duration_seconds{namespace=\"" + appId + "\"})*1000";
            case "error_rate" ->
                    "rate(http_requests_total{namespace=\"" + appId + "\",status=~\"5..\"}[5m])";
            default ->
                    metric + "{namespace=\"" + appId + "\"}";  // Custom metric ορισμένη από τον χρήστη
        };
    }

    public List<Double> queryRange(String appId, String metric,
                                    Instant start, Instant end, String step) {
        if (deploymentRepository.findByApplicationIdAndStatus(appId,
                DeploymentStatus.ACTIVE).isEmpty()) return List.of();

        String primaryUrl = deploymentRepository
                .findByApplicationIdAndStatus(appId, DeploymentStatus.ACTIVE)
                .get(0).getPrometheusEndpoint();

        String url = primaryUrl + "/api/v1/query_range"
                + "?query=" + URLEncoder.encode(buildPromQL(metric, appId), StandardCharsets.UTF_8)
                + "&start=" + start.getEpochSecond()
                + "&end=" + end.getEpochSecond()
                + "&step=" + step;

        log.debug("Range query: {}", url);
        // TODO: υλοποίηση πλήρους parsing του PrometheusRangeResponse
        return List.of();
    }
}
