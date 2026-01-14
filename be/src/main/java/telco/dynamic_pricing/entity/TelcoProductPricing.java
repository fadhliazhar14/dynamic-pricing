package telco.dynamic_pricing.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "telco_product_pricing")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TelcoProductPricing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String biller;
    private String productId;
    private String denomId;
    private String groupCode;
    private String merchantCode;
    private String title;
    private String description;
    private String shortcode;
    private BigDecimal amount;
    private BigDecimal originalPrice;
    private BigDecimal priceEup;
    private BigDecimal priceAggregator;
    private BigDecimal fee;
    private Integer sortOrder;
    private Boolean isVisible;
    private Boolean isActive;
    private LocalDateTime effectiveDate;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
