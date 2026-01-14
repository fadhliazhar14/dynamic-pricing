package telco.dynamic_pricing.service;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import telco.dynamic_pricing.PricingRepository;
import telco.dynamic_pricing.dto.PricingResponseDTO;
import telco.dynamic_pricing.entity.TelcoProductPricing;

import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PricingService {
    private final PricingRepository repository;


    @Transactional
    public void processExcelUpload(MultipartFile file) throws Exception {
        List<TelcoProductPricing> products = parseExcelFile(file);
        repository.saveAll(products);
    }

    private List<TelcoProductPricing> parseExcelFile(MultipartFile file) throws Exception {
        List<TelcoProductPricing> productList = new ArrayList<>();

        try (InputStream is = file.getInputStream();
             Workbook workbook = WorkbookFactory.create(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            DataFormatter formatter = new DataFormatter();

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);

                // Empty cell check
                if (row == null || isRowEmpty(row, formatter)) {
                    continue;
                }

                // Mapping
                TelcoProductPricing product = TelcoProductPricing.builder()
                        .biller(formatter.formatCellValue(row.getCell(0)))
                        .productId(formatter.formatCellValue(row.getCell(1)))
                        .denomId(formatter.formatCellValue(row.getCell(2)))
                        .groupCode(formatter.formatCellValue(row.getCell(3)))
                        .merchantCode(formatter.formatCellValue(row.getCell(4)))
                        .title(formatter.formatCellValue(row.getCell(5)))
                        .description(formatter.formatCellValue(row.getCell(6)))
                        .shortcode(formatter.formatCellValue(row.getCell(7)))
                        .amount(getDecimalValue(row.getCell(8)))
                        .originalPrice(getDecimalValue(row.getCell(9)))
                        .priceEup(getDecimalValue(row.getCell(10)))
                        .priceAggregator(getDecimalValue(row.getCell(11)))
                        .sortOrder(getIntegerValue(row.getCell(13)))
                        .isVisible("Yes".equalsIgnoreCase(formatter.formatCellValue(row.getCell(14))))
                        .isActive("Yes".equalsIgnoreCase(formatter.formatCellValue(row.getCell(15))))
                        .effectiveDate(getLocalDateTime(row.getCell(20)))
                        .build();

                product.setFee(calculateFee(product.getPriceEup(), product.getPriceAggregator()));
                productList.add(product);
            }
        }

        return productList;
    }

    private boolean isRowEmpty(Row row, DataFormatter formatter) {
        String biller = formatter.formatCellValue(row.getCell(0));
        String productId = formatter.formatCellValue(row.getCell(1));
        String shortcode = formatter.formatCellValue(row.getCell(7));

        return (biller == null || biller.trim().isEmpty()) &&
                (productId == null || productId.trim().isEmpty()) &&
                (shortcode == null || shortcode.trim().isEmpty());
    }

    private BigDecimal calculateFee(BigDecimal eup, BigDecimal aggregator) {
        if (eup == null || aggregator == null) return BigDecimal.ZERO;
        // Rumus: Fee = EUP - COGS
        return eup.subtract(aggregator);
    }

    private BigDecimal getDecimalValue(Cell cell) {
        if (cell == null || cell.getCellType() == CellType.BLANK) return BigDecimal.ZERO;
        return BigDecimal.valueOf(cell.getNumericCellValue());
    }

    private Integer getIntegerValue(Cell cell) {
        if (cell == null || cell.getCellType() == CellType.BLANK) return 0;
        return (int) cell.getNumericCellValue();
    }

    private LocalDateTime getLocalDateTime(Cell cell) {
        if (!DateUtil.isCellDateFormatted(cell)) return null;
        return cell.getLocalDateTimeCellValue();
    }

    public List<PricingResponseDTO> getAllPricingData() {
        List<TelcoProductPricing> entities = repository.findAll();

        return entities.stream().map(entity -> PricingResponseDTO.builder()
                .biller(entity.getBiller())
                .productId(entity.getProductId())
                .shortcode(entity.getShortcode())
                .productName(entity.getTitle())
                .priceEup(entity.getPriceEup())
                .priceAggregator(entity.getPriceAggregator())
                .fee(entity.getFee())
                .isActive(entity.getIsActive())
                .lastUpdated(entity.getUpdatedAt().toString())
                .build()
        ).collect(Collectors.toList());
    }

    @Transactional
    public void cleansePricingData() {
        repository.deleteAllInBatch();
    }
}
