package telco.dynamic_pricing.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import telco.dynamic_pricing.dto.PricingResponseDTO;
import telco.dynamic_pricing.entity.TelcoProductPricing;
import telco.dynamic_pricing.service.PricingService;
import telco.dynamic_pricing.util.ApiResponse;

import java.util.List;

@RestController
@RequestMapping("/api/dynamic-pricing")
@RequiredArgsConstructor
public class PricingController {

    private final PricingService pricingService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadPricing(@RequestParam("file") MultipartFile file) {
        try {
            pricingService.processExcelUpload(file);

            return ResponseEntity.ok("File processed and pricing updated successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing file: " + e.getMessage());
        }
    }

    @GetMapping("/data")
    public ResponseEntity<ApiResponse<List<PricingResponseDTO>>> getAllData() {
        List<PricingResponseDTO> data = pricingService.getAllPricingData();

        ApiResponse<List<PricingResponseDTO>> response =
                ApiResponse.success("Success", data);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/truncate")
    public ResponseEntity<String> cleanseData() {
        pricingService.cleansePricingData();
        return ResponseEntity.ok("All pricing data has been cleansed.");
    }
}
