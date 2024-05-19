package ptit.oop.assetmanagement.controllers;

import ptit.oop.assetmanagement.dtos.ResponseObject;
import ptit.oop.assetmanagement.exceptions.BadRequestException;
import ptit.oop.assetmanagement.exceptions.GlobalException;
import ptit.oop.assetmanagement.exceptions.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.stream.Collectors;

@ControllerAdvice
public class AppExceptionHandler {
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ResponseObject> handleNotFoundException(
            NotFoundException ex, WebRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        ResponseObject object = ResponseObject.builder()
                .status(status)
                .message(ex.getMessage())
                .build();
        return new ResponseEntity<>(object, status);
    }

    @ExceptionHandler(GlobalException.class)
    public ResponseEntity<ResponseObject> handleDefaultException(GlobalException ex) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        ResponseObject object = ResponseObject.builder()
                .status(status)
                .message("Internal server error")
                .build();
        return new ResponseEntity<>(object, status);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ResponseObject> handleNotFoundException(
        BadRequestException ex, WebRequest request) {
        return new ResponseEntity<>(ResponseObject
            .builder()
            .status(HttpStatus.BAD_REQUEST)
            .message(ex.getMessage())
            .build(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseObject> handleNotFoundException(
        MethodArgumentNotValidException ex, WebRequest request) {
        return new ResponseEntity<>(ResponseObject.builder()
            .status(HttpStatus.BAD_REQUEST)
            .message(ex.getAllErrors().stream().map(ObjectError::getDefaultMessage).collect(Collectors.joining(" ")))
            .build(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ResponseObject> handleAccessDeniedException(
        AccessDeniedException ex, WebRequest request) {
        return new ResponseEntity<>(ResponseObject.builder()
            .status(HttpStatus.FORBIDDEN)
            .message(ex.getMessage())
            .build(), HttpStatus.FORBIDDEN);
    }

}
