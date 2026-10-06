package ai.fin.convertor;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.YearMonth;

/**
 * JPA attribute converter for persisting {@link YearMonth}.
 * Converts {@link YearMonth} to its ISO-8601 string representation (yyyy-MM)
 * when storing in the database and parses it back when reading.
 * Applied automatically to all {@link YearMonth} entity attributes.
 */
@Converter(autoApply = true)
public class YearMonthConverter implements AttributeConverter<YearMonth, String> {

    // Converts YearMonth to its ISO-8601 string representation (yyyy-MM)
    @Override
    public String convertToDatabaseColumn(YearMonth attribute) {
        return attribute != null ? attribute.toString() : null;
    }

    // Converts database value back to YearMonth.
    @Override
    public YearMonth convertToEntityAttribute(String dbData) {
        return dbData != null ? YearMonth.parse(dbData) : null;
    }
}