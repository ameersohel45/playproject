package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import models.Datasets;
import play.libs.Json;
import play.mvc.*;
import services.DatasetsService;
import utils.Validator;

import javax.inject.Inject;
import java.util.Map;

public class DatasetsController extends Controller {
    private final DatasetsService datasetsService;
    //private final ObjectMapper objectMapper;
    private final Validator validator;

    @Inject
    public DatasetsController(DatasetsService datasetsService, Validator validator) {
        this.datasetsService = datasetsService;
        //this.objectMapper = objectMapper;
        this.validator = validator;
    }

    public Result getAllDatasets() {
        Map<String, Object> response = datasetsService.getAllDatasets();
        return ok(Json.toJson(response));
    }

    public Result getDatasetById(String id) {
        Map<String, Object> response = datasetsService.getDatasetById(id);
        return status((int) response.get("status"), Json.toJson(response));
    }

    public Result createDataset(Http.Request request) {


        JsonNode json = request.body().asJson();
        Map<String, Object> response = datasetsService.createDataset(String.valueOf(json));
        return status((int) response.get("status"), Json.toJson(response));

    }

  public Result updateDataset(String id, Http.Request request) {
        JsonNode json = request.body().asJson();
        Map<String, Object> response = datasetsService.updateDataset(id, String.valueOf(json));
        return status((int) response.get("status"), Json.toJson(response));
    }

    public Result deleteDataset(String id) {
        Map<String, Object> response = datasetsService.deleteDataset(id);
        return status((int) response.get("status"), Json.toJson(response));
   }
}
