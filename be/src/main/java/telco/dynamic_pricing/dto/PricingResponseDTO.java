package telco.dynamic_pricing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PricingResponseDTO {
    private String biller;
    private String productId;
    private String shortcode;
    private String productName;
    private BigDecimal priceEup;
    private BigDecimal priceAggregator;
    private BigDecimal fee;
    private Boolean isActive;
    private String lastUpdated;
}
