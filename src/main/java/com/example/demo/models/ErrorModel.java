/**
 * Created By shoh@n
 * Date: 8/13/2022
 */

package com.example.demo.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ErrorModel {

    private Integer status;
    private String message;
    private List<Map<String, Object>> errors;

    public Map<String, Object> response() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("status", this.status);
        map.put("message", this.message);
        return Map.of("error", map);
    }

    public Map<String, Object> errorsResponse() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("status", this.status);
        map.put("message", "Validation error occur");
        map.put("errors", this.errors);
        return Map.of("error", map);
    }
}
