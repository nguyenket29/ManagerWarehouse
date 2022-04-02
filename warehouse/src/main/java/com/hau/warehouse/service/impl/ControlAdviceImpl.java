package com.hau.warehouse.service.impl;

import com.hau.warehouse.exception.ApiException;
import com.hau.warehouse.exception.CustomExistException;
import com.hau.warehouse.exception.CustomNotFoundException;
import com.hau.warehouse.exception.CustomTokenRefreshException;
import com.hau.warehouse.payload.response.APIResponse;
import com.hau.warehouse.service.IControllAdvice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.PrintWriter;
import java.io.StringWriter;

@RestControllerAdvice
public class ControlAdviceImpl implements IControllAdvice {

    @ExceptionHandler(CustomNotFoundException.class)
    @Override
    public ResponseEntity<APIResponse<CustomNotFoundException>> ErrorNotFound(CustomNotFoundException custom, ApiException api){
        return new ResponseEntity<>(new APIResponse(api.getStatus(), custom.getMessage(),
                convertStackTraceToString(custom)),
                api.getStatus());
    }

    @ExceptionHandler(CustomExistException.class)
    @Override
    public ResponseEntity<APIResponse> ErrorExist(CustomExistException e, ApiException api) {
        return new ResponseEntity<>(new APIResponse(api.getStatus(), e.getMessage(),
                convertStackTraceToString(e)),
                api.getStatus());
    }

    @ExceptionHandler(CustomTokenRefreshException.class)
    @Override
    public ResponseEntity<APIResponse> ErrorRefreshToken(CustomTokenRefreshException e, ApiException api) {
        return new ResponseEntity<>(new APIResponse(api.getStatus(), e.getMessage(),
                convertStackTraceToString(e)),
                api.getStatus());
    }

    @ExceptionHandler(Exception.class)
    @Override
    public ResponseEntity<APIResponse> ErrorInternalServer(Exception e, ApiException api) {
        return new ResponseEntity<>(new APIResponse(api.getStatus(), e.getMessage(),
                convertStackTraceToString(e)),
                api.getStatus());
    }

    private String convertStackTraceToString(Exception e){
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        e.printStackTrace(printWriter);
        String stackTrace = stringWriter.toString();
        return stackTrace;
    }
}
