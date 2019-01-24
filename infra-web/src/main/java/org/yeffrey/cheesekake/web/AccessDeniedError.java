package org.yeffrey.cheesekake.web;

import graphql.ErrorType;
import graphql.GraphQLError;
import graphql.language.SourceLocation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AccessDeniedError extends RuntimeException implements GraphQLError {

    @Override
    public String getMessage() {
        return "Access Denied Error";
    }

    @Override
    public List<SourceLocation> getLocations() {
        return null;
    }

    @Override
    public ErrorType getErrorType() {
        return ErrorType.ExecutionAborted;
    }

    @Override
    public Map<String, Object> getExtensions() {
        Map<String, Object> validationErrors = new HashMap<>();
        validationErrors.put("access_denied", "You are not allowed to perform this operation");
        return validationErrors;
    }
}
