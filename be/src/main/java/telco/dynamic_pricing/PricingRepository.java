package telco.dynamic_pricing;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import telco.dynamic_pricing.entity.TelcoProductPricing;

@Repository
public interface PricingRepository extends JpaRepository<TelcoProductPricing, Long> {

}
