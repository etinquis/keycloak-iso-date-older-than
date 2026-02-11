package com.synackdoche.keycloak.validators.isodateolderthan;

import org.keycloak.provider.ConfiguredProvider;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.validate.ValidationContext;
import org.keycloak.validate.ValidationError;
import org.keycloak.validate.ValidatorConfig;
import org.keycloak.validate.AbstractStringValidator;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;

public class IsoDateOlderThanValidator extends AbstractStringValidator implements ConfiguredProvider {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    public static final String ID = "iso-date-older-than";
    public static final String MESSAGE_INVALID_DATE = "error-invalid-date";
    public static final String MESSAGE_NOT_OLD_ENOUGH = "error-not-old-enough";
    private static final List<ProviderConfigProperty> configProperties = new ArrayList<>();

    public static final String KEY_YEARS = "years";

    static {
        ProviderConfigProperty property;
        property = new ProviderConfigProperty();
        property.setName(KEY_YEARS);
        property.setLabel("Minimum age");
        property.setHelpText("The minimum age");
        property.setType(ProviderConfigProperty.INTEGER_TYPE);
        configProperties.add(property);
    }

    public IsoDateOlderThanValidator() {

    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public String getHelpText() {
        return "Validates date in rfc3339/iso8601 format, as provided by the html5-date input, and checks if it is older than a certain amount of years";
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return configProperties;
    }
    
    @Override
    protected void doValidate(String value, String inputHint, ValidationContext context, ValidatorConfig config) {
        try {
            LocalDate date = LocalDate.from(FORMATTER.parse(value));
            int years = config.getInt(KEY_YEARS);
            LocalDate minDate = LocalDate.now().minusYears(years);
            if (date.isAfter(minDate)) {
                context.addError(new ValidationError(getId(), inputHint, MESSAGE_NOT_OLD_ENOUGH, value));
            }
        } catch (DateTimeParseException e) {
            context.addError(new ValidationError(getId(), inputHint, MESSAGE_INVALID_DATE, value));
        }
    }
}