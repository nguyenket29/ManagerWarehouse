package com.hau.warehouse.advice;

import com.hau.warehouse.exception.ApiException;
import com.hau.warehouse.exception.CustomExistException;
import com.hau.warehouse.exception.CustomNotFoundException;
import com.hau.warehouse.exception.CustomTokenRefreshException;
import com.hau.warehouse.payload.response.APIResponse;
import org.springframework.http.ResponseEntity;

public interface IControllAdvice {
    ResponseEntity<APIResponse<CustomNotFoundException>> ErrorNotFound(
            CustomNotFoundException custom, ApiException api);
    ResponseEntity<APIResponse> ErrorExist(CustomExistException e, ApiException api);
    ResponseEntity<APIResponse> ErrorRefreshToken(CustomTokenRefreshException e, ApiException api);
    ResponseEntity<APIResponse> ErrorInternalServer(Exception e, ApiException api);
}
