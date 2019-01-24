package org.yeffrey.cheesekake.web;

import graphql.ErrorType;
import graphql.GraphQLError;
import graphql.language.SourceLocation;
import org.yeffrey.core.error.ErrorDescription;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * See https://github.com/graphql-java/graphql-java/issues/1022
 */
public class ValidationError extends RuntimeException implements GraphQLError {
    private final List<ErrorDescription> errors;

    @Override
    public String getMessage() {
        return "Validation Errors";
    }

    public ValidationError(List<ErrorDescription> errors) {
        this.errors = errors;
    }

    @Override
    public List<SourceLocation> getLocations() {
        return null;
    }

    @Override
    public ErrorType getErrorType() {
        return ErrorType.ValidationError;
    }

    @Override
    public Map<String, Object> getExtensions() {
        Map<String, Object> validationErrors = new HashMap<>();
        validationErrors.put("validation_errors", this.errors);
        return validationErrors;
    }
}
