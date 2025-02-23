

package services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import models.Datasets;
import repositories.DatasetsRepository;
import utils.ResponseBuilder;
import utils.Validator;

import javax.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class DatasetsService{

    @PersistenceContext
    private EntityManager entityManager;

    private final DatasetsRepository dataRepository;
    private final ObjectMapper objectMapper;

    @Inject
    public DatasetsService(DatasetsRepository dataRepository, ObjectMapper objectMapper, EntityManager entityManager) {
        this.dataRepository = dataRepository;
        this.objectMapper = objectMapper;
        this.entityManager = entityManager;
    }

    public Map<String, Object> getAllDatasets() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Datasets> datasets = entityManager.createQuery("SELECT d FROM Datasets d", Datasets.class).getResultList();
//            response.put("status", 200);
//            response.put("message", "Datasets retrieved successfully");
//            response.put("totalRecords", datasets.size());
//            response.put("data", datasets);
//            response.put("id","Api.read");
//            response.put("ver","3.0.5");
//            response.put("time",LocalDateTime.now());
//            HashMap<String ,Object> p = new HashMap<>();
//            p.put("resmsgId","id");
//            p.put("status","success");
//            response.put("params",p);
//            response.put("status", 201);
//            response.put("message", "Dataset retrieved successfully");
//            Map<String,Object> result = new HashMap<>();
//            result.put("data",datasets);
//            response.put("result",result);
          return ResponseBuilder.buildResponseOnget("Success",201,"Data retrived successfully",datasets);

        } catch (PersistenceException e) {
   response.put("status", 503);
         response.put("message", "Database is currently unavailable. Please try again later.");

        }
        return response;
    }

    public Map<String, Object> getDatasetById(String id) {
        Map<String, Object> response = new HashMap<>();
        try {
            Datasets dataset = entityManager.find(Datasets.class, id);
            if (dataset != null) {
//                response.put("status", 200);
//                response.put("message", "Dataset retrieved successfully");
//                response.put("data", dataset);
                return ResponseBuilder.buildResponseOngetById("Success",200,"Dataset retrieved successfully",dataset);


            } else {
//                response.put("status", 404);
//                response.put("message", "Dataset not found for ID: " + id);
                return ResponseBuilder.buildResponse(id,"Failure",404,"Dataset not found for ID: " + id);
            }
        } catch (PersistenceException e) {
//            response.put("status", 503);
//            response.put("message", "Database is currently unavailable. Please try again later.");
            return ResponseBuilder.buildResponse(id,"Failure",404,"Database is currently unavailable. Please try again later.");

        }
//        return response;
    }

    @Transactional
    public Map<String, Object> createDataset(String json) {
        Map<String, Object> response = new HashMap<>();
        Datasets dataset = null;

        try {
           dataset = objectMapper.readValue(json, Datasets.class);

            Optional<String> validationError = Validator.validate(dataset);
            if (validationError.isPresent()) {
//                response.put("status", 400);
//                response.put("message", validationError.get());
//                return response;
             return ResponseBuilder.buildResponse(dataset.getId(),"Failure",400,validationError.get());
            }

            if (entityManager.find(Datasets.class, dataset.getId()) != null) {
//                response.put("status", 409);
//                response.put("message", "Dataset with the same ID already exists");
//                response.put("id","Api.read");
//                response.put("ver","3.0.5");
//                response.put("time",LocalDateTime.now());
//                HashMap<String ,Object> p = new HashMap<>();
//                p.put("resmsgId",dataset.getId());
//                p.put("status","failure");
//                response.put("params",p);
//                response.put("status", 409);
//                response.put("message", "Dataset with same Id already exists");
////                return response;
                return ResponseBuilder.buildResponse(dataset.getId(),"Failure",409,"Dataset with same Id already exists");
            }

            dataset.setUpdatedBy("admin");
            dataset.setCreatedAt(LocalDateTime.now());
            dataset.setUpdatedAt(LocalDateTime.now());
            //entityManager.persist(dataset);
            dataRepository.save(dataset);

//            response.put("id","Api.read");
//            response.put("ver","3.0.5");
//            response.put("time",LocalDateTime.now());
//            HashMap<String ,Object> p = new HashMap<>();
//            p.put("resmsgId",dataset.getId());
//            p.put("status","success");
//            response.put("params",p);
//            response.put("status", 201);
//            response.put("message", "Dataset created successfully");


        } catch (InvalidFormatException e) {
//            response.put("status", 400);
//            response.put("message", "Status should be Live, Draft, or RETIRED");

            return ResponseBuilder.buildResponse("id","Failure",400,"Status should be Live, Draft, or RETIRED");
        } catch (Exception e) {
//            response.put("status", 400);
//            response.put("message", "Check request body");
            return ResponseBuilder.buildResponse(dataset.getId(),"Failure",400,"Check Your Request Body");

        }
//        return response;
        String id = dataset.getId();
        return ResponseBuilder.buildResponse(id,"Success",201,"Data added Successfully");
    }

    @Transactional
    public Map<String, Object> updateDataset(String id, String json) {
        Map<String, Object> response = new HashMap<>();
        try {
            Datasets existingDataset = entityManager.find(Datasets.class, id);
            if (existingDataset == null) {
//                response.put("status", 404);
//                response.put("message", "Dataset not found for ID: " + id);
//                return response;
                return ResponseBuilder.buildResponse(id,"Failure",404,"Dataset not found for ID: " + id);


            }

            Datasets updates = objectMapper.readValue(json, Datasets.class);
            Optional<String> validationError = Validator.validateForUpdate(updates);
            if (validationError.isPresent()) {
//                response.put("status", 400);
//                response.put("message", validationError.get());
//                return response;
                return ResponseBuilder.buildResponse(id,"Failure",404,validationError.get());
            }

            if (updates.getDataSchema() != null) existingDataset.setDataSchema(updates.getDataSchema());
            if (updates.getRouteConfig() != null) existingDataset.setRouteConfig(updates.getRouteConfig());
            if (updates.getStatus() != null) existingDataset.setStatus(updates.getStatus());
            if (updates.getUpdatedBy() != null) existingDataset.setUpdatedBy(updates.getUpdatedBy());

            existingDataset.setUpdatedAt(LocalDateTime.now());
            if(!entityManager.getTransaction().isActive()){
                entityManager.getTransaction().begin();
            }
            entityManager.merge(existingDataset);
            entityManager.getTransaction().commit();
           // entityManager.merge(existingDataset);

//            response.put("id", id);
//            response.put("status", 200);
//            response.put("message", "Dataset updated successfully");
//            response.put("id","Api.read");
//            response.put("ver","3.0.5");
//            response.put("time",LocalDateTime.now());
//            HashMap<String ,Object> p = new HashMap<>();
//            p.put("resmsgId",id);
//            p.put("status","success");
//            response.put("params",p);
//            response.put("status", 200);
//            response.put("message", "Dataset updated successfully");
            return ResponseBuilder.buildResponse(id,"Success",200,"Dataset updated successfully");



        } catch (Exception e) {
//            response.put("status", 400);
//            response.put("message", "Check request body");
            return ResponseBuilder.buildResponse(id,"Failure",400,"Check Request Body");

        }
//        return response;
    }

    @Transactional
    public Map<String, Object> deleteDataset(String id) {
        Map<String, Object> response = new HashMap<>();
        try {
            Datasets dataset = entityManager.find(Datasets.class, id);
            if (dataset != null) {
                if(!entityManager.getTransaction().isActive()){
                    entityManager.getTransaction().begin();
                }entityManager.remove(dataset);
               // entityManager.persist(dataset);
                entityManager.getTransaction().commit();

//                response.put("id", id);
//                response.put("status", 200);
//                response.put("message", "Dataset with ID " + id + " deleted successfully");
//                response.put("id","Api.read");
//                response.put("ver","3.0.5");
//                response.put("time",LocalDateTime.now());
//                HashMap<String ,Object> p = new HashMap<>();
//                p.put("resmsgId",dataset.getId());
//                p.put("status","success");
//                response.put("params",p);
//                response.put("status", 200);
//                response.put("message", "Dataset deleted successfully");
                return ResponseBuilder.buildResponse(id,"Success",200,"Dataset deleted successfully");

            } else {
//                response.put("id", id);
//                response.put("status", 404);
//                response.put("message", "Dataset with ID " + id + " not found");
                return ResponseBuilder.buildResponse(id,"Failure",404,"Dataset with ID " + id + " not found");

            }
        } catch (Exception e) {
//            response.put("status", 400);
//            response.put("message", "Error deleting dataset");
            return ResponseBuilder.buildResponse(id,"Failure",404,"Error deleting record");

        }
//        return response;
    }
}
