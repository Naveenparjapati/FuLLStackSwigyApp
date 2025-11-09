package naveen.example.FoodDlvApp.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import naveen.example.FoodDlvApp.entity.FoodEntity;
@Repository
public interface FoodRepository extends MongoRepository<FoodEntity, String> {

}
