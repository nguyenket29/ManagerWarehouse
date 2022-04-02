package com.hau.warehouse.service;

import com.hau.warehouse.exception.ApiException;
import com.hau.warehouse.exception.CustomNotFoundException;
import com.hau.warehouse.exception.CustomExistException;
import com.hau.warehouse.exception.CustomTokenRefreshException;
import com.hau.warehouse.payload.response.APIResponse;
import org.springframework.http.ResponseEntity;

public interface IControllAdvice {
    //Error Not Found
    ResponseEntity<APIResponse<CustomNotFoundException>> ErrorNotFound(CustomNotFoundException e, ApiException api);

    ResponseEntity<APIResponse> ErrorExist(CustomExistException e, ApiException api);

    ResponseEntity<APIResponse> ErrorRefreshToken(CustomTokenRefreshException e, ApiException api);

    //Error Internal Server - 500
    ResponseEntity<APIResponse> ErrorInternalServer(Exception e, ApiException api);
}
