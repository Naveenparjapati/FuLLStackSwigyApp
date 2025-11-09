package naveen.example.FoodDlvApp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/test")
public class MongoTestController {

    @Autowired
    private MongoTemplate mongoTemplate;

    @GetMapping("/mongo-status")
    public Map<String, Object> checkMongoStatus() {
        try {
            // Get collection names
            Set<String> collections = mongoTemplate.getCollectionNames();
            
            // Get counts from both collections
            long fodiesCount = mongoTemplate.getCollection("fodies").countDocuments();
            long foodsCount = mongoTemplate.getCollection("foods").countDocuments();
            
            // Get documents from both collections
            List<Object> fodiesDocuments = mongoTemplate.findAll(Object.class, "fodies");
            List<Object> foodsDocuments = mongoTemplate.findAll(Object.class, "foods");
            
            Map<String, Object> result = new HashMap<>();
            result.put("status", "connected");
            result.put("database", mongoTemplate.getDb().getName());
            result.put("collections", collections);
            result.put("fodiesCount", fodiesCount);
            result.put("foodsCount", foodsCount);
            result.put("fodiesDocuments", fodiesDocuments.subList(0, Math.min(fodiesDocuments.size(), 5)));
            result.put("foodsDocuments", foodsDocuments.subList(0, Math.min(foodsDocuments.size(), 5)));
            return result;
        } catch (Exception e) {
            return Map.of(
                "status", "error",
                "error", e.getMessage(),
                "errorType", e.getClass().getName()
            );
        }
    }
}