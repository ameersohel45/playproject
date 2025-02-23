package utils;

import models.Datasets;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class ResponseBuilder {

    public static Map<String, Object> buildResponse(String resmsgId) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", "Api.read");
        response.put("ver", "3.0.5");
        response.put("time", LocalDateTime.now());

        HashMap<String, Object> params = new HashMap<>();
        params.put("resmsgId", resmsgId);
        params.put("status", "Success");

        response.put("params", params);
        response.put("status", 201);
        response.put("message", "Data added successfully");

        return response;
    }
}

