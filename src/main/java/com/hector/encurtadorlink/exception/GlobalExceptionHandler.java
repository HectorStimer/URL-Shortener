package com.hector.encurtadorlink.exception;

import com.hector.encurtadorlink.model.Url;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(UrlNotFoundException.class)
  public ResponseEntity<String> urlNotFound(UrlNotFoundException ex){
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());

  }


  @ExceptionHandler(UrlExpiredException.class)
  public ResponseEntity<String> urlExpired(UrlExpiredException ex){
    return ResponseEntity.status(HttpStatus.GONE).body(ex.getMessage());
  }


}
