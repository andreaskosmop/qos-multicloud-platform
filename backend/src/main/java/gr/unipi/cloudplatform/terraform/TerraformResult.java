package gr.unipi.cloudplatform.terraform;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public class TerraformResult {

    private final boolean success;
    private final Map<String, Object> outputs;
    private final String errorMessage;

    public static TerraformResult success(Map<String, Object> outputs) {
        return new TerraformResult(true, outputs, null);
    }

    public static TerraformResult failure(String errorMessage) {
        return new TerraformResult(false, Map.of(), errorMessage);
    }
}
