package naveen.example.FoodDlvApp.servic;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;


import naveen.example.FoodDlvApp.entity.FoodEntity;
import naveen.example.FoodDlvApp.io.FoodRequest;
import naveen.example.FoodDlvApp.io.FoodResponse;
import naveen.example.FoodDlvApp.repository.FoodRepository;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;


@Service
public class Awss3Service implements foodService{
	@Autowired
	FoodRepository foodRepository;
	
	@Autowired
	private S3Client s3Client;
	
	@Autowired
	private org.springframework.data.mongodb.core.MongoTemplate mongoTemplate;
	
	// S3 bucket name is injected from application.properties using Spring's @Value.
	// Set this property in src/main/resources/application.properties, for example:
	// aws.s3.bucketname=your-bucket-name
	// If you need to temporarily disable injection for local testing, you can
	// replace usages of 'bucketName' with a hardcoded string (e.g. "my-test-bucket").
	@Value("${aws.s3.bucketname}")
	private String bucketName;
	
	@Override
	public String uploadFile(MultipartFile file) {
	    // Commented out S3 upload functionality
//	    String filenameExtension = file.getOriginalFilename()
//	            .substring(file.getOriginalFilename().lastIndexOf(".") + 1);
//	    String key = UUID.randomUUID().toString() + "." + filenameExtension;
//
//	try {
//	    // Build the PutObjectRequest using the configured bucket name.
//	    // Note: this uses the injected 'bucketName' from application.properties.
//	    // To test with a different bucket temporarily, replace 'bucketName'
//	    // below with a literal string, e.g. "my-test-bucket".
//	    PutObjectRequest putObjectRequest = PutObjectRequest.builder()
//		    .bucket(bucketName)
//		    .key(key)
//		    .acl("public-read")
//		    .contentType(file.getContentType())
//		    .build();
//
//	        PutObjectResponse response = s3Client.putObject(
//	                putObjectRequest,
//	                RequestBody.fromBytes(file.getBytes())
//	        );
//
//			if (response.sdkHttpResponse().isSuccessful()) {
//				// Construct the public URL for the uploaded object. This assumes
//				// the bucket is public and located in the standard S3 namespace.
//				// For private buckets or region-specific endpoints, adjust as needed.
//				return "https://" + bucketName + ".s3.amazonaws.com/" + key;
//			} else {
//	            throw new ResponseStatusException(
//	                    HttpStatus.INTERNAL_SERVER_ERROR,
//	                    "File upload failed"
//	            );
//	        }
//	    } catch (IOException ex) {
//	        throw new ResponseStatusException(
//	                HttpStatus.INTERNAL_SERVER_ERROR,
//	                "An error occurred while uploading the file"
//	        );
//	    }
		
		// Return a placeholder URL for testing
		return "https://placeholder-url.com/" + file.getOriginalFilename();
	}

//	@Override
//	public FoodResponse addFood(FoodRequest request, MultipartFile file) {
//		// TODO Auto-generated method stub
//		return null;
//	}
	@Override
	public FoodResponse addFood(FoodRequest request, MultipartFile file) {
	    System.out.println("Starting addFood method with request: " + request);
	    FoodEntity newFoodEntity = convertToEntity(request);
	    System.out.println("Converted to entity: " + newFoodEntity);
	    String imageUrl = uploadFile(file);
	    System.out.println("Got image URL: " + imageUrl);
	    newFoodEntity.setImageUrl(imageUrl);
	    System.out.println("Attempting to save entity to database...");
	    try {
	        System.out.println("Entity before save: " + newFoodEntity);
	        newFoodEntity = foodRepository.save(newFoodEntity);
	        System.out.println("Save operation completed");
	        System.out.println("Entity after save: " + newFoodEntity);
	        if (newFoodEntity.getId() != null) {
	            System.out.println("Successfully saved entity with ID: " + newFoodEntity.getId());
	        } else {
	            System.out.println("Warning: Saved entity has null ID");
	        }
	    } catch (Exception e) {
	        System.err.println("Error saving to database: " + e.getMessage());
	        System.err.println("Error type: " + e.getClass().getName());
	        e.printStackTrace();
	        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to save to database: " + e.getMessage());
	    }
	    FoodResponse response = convertToResponse(newFoodEntity);
	    System.out.println("Returning response: " + response);
	    return response;
	}

	private FoodEntity convertToEntity(FoodRequest request) {
	    return FoodEntity.builder()
	            .name(request.getName())
	            .description(request.getDescription())
	            .category(request.getCategory())
	            .price(request.getPrice())
	            .build();
	}

	private FoodResponse convertToResponse(FoodEntity entity) {
	    return FoodResponse.builder()
	            .id(entity.getId())
	            .name(entity.getName())
	            .description(entity.getDescription())
	            .category(entity.getCategory())
	            .price(entity.getPrice())
	            .imageUrl(entity.getImageUrl())
	            .build();
	}

	@Override
	public List<FoodResponse> readFoods() {
	    List<FoodEntity> databaseEntries = foodRepository.findAll();
	    return databaseEntries.stream()
	            .map(object -> convertToResponse(object))
	            .collect(Collectors.toList());
	}

	@Override
	public FoodResponse readFood(String id) {
	FoodEntity extngfood=	foodRepository.findById(id).orElseThrow(()-> new RuntimeException("Food not found "+id));
	return convertToResponse(extngfood)	;
	
	}

	@Override
	public boolean deleteFile(String filename) {
		// TODO Auto-generated method stub
		DeleteObjectRequest deleteObjectRequest=DeleteObjectRequest.builder()
				.bucket(bucketName)
				.key(filename)
				.build();
		s3Client.deleteObject(deleteObjectRequest);
				
		return true;
	}

	@Override
	public void deleteFood(String id) {
		// TODO Auto-generated method stub
	 FoodResponse  response	=readFood(id);
	 String imageUrl=response.getImageUrl();
	String filename= imageUrl.substring(imageUrl.lastIndexOf("/")+1);
	boolean isFileDelete=deleteFile(filename);
	if(isFileDelete)
	{
		foodRepository.deleteById(response.getId());
	}
}



}
