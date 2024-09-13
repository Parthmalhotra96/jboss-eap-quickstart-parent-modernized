package org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class LoggingConstants {

    public static final String VALIDATION_FAILED_CODE = "APP101";
    public static final String RETRIEVAL_ERROR_CODE = "APP102";
    public static final String EXISTING_EMAIL_ERROR_CODE = "APP103";
    public static final String DELETING_ERROR_CODE = "APP104";
    public static final String UNEXPECTED_ERROR_CODE = "APP901";

    public static final String VALIDATION_FAILED = "Validation failed : {}";
    public static final String RETRIEVAL_ERROR= "Error retrieving data : {}";
    public static final String EXISTING_EMAIL_ERROR= "Email already exists: {}";
    public static final String DELETING_ERROR= "Error deleting data : {}";
    public static final String UNEXPECTED_ERROR = "Unexpected error occurred : {}";
}
