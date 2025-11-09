package naveen.example.FoodDlvApp.servic;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import naveen.example.FoodDlvApp.io.FoodRequest;
import naveen.example.FoodDlvApp.io.FoodResponse;

public interface foodService {
     
	String uploadFile(MultipartFile file);
	
    FoodResponse addFood(FoodRequest request,MultipartFile file);
	List<FoodResponse>  readFoods();
	FoodResponse readFood(String id);
}
