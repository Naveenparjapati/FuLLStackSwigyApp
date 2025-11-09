package naveen.example.FoodDlvApp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import naveen.example.FoodDlvApp.io.FoodRequest;
import naveen.example.FoodDlvApp.io.FoodResponse;
import naveen.example.FoodDlvApp.servic.foodService;

@RestController
@RequestMapping("/api/foods")
@AllArgsConstructor
public class Foodcontraller {

	
	@Autowired
	private foodService foodservice;

	@PostMapping
	public FoodResponse addFood(@RequestPart("food") String foodString,
	                            @RequestPart("file") MultipartFile file) {
	    ObjectMapper objectMapper = new ObjectMapper();//for read value
	    FoodRequest request = null;
	    try {
	        request = objectMapper.readValue(foodString, FoodRequest.class);
	    } catch (JsonProcessingException ex) {
	        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid JSON format");
	    }
	    FoodResponse response = foodservice.addFood(request, file);
	    return response;
	}
	
	
	@GetMapping
	public List<FoodResponse> readFoods() {
	    return foodservice.readFoods();
	}
	
	@GetMapping("/{id}")
	public FoodResponse readFood(@PathVariable String id) {
	    return foodservice.readFood(id);
	}

	
}
