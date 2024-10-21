package kassandrafalsitta.e_commerce_back.exceptions;

import kassandrafalsitta.e_commerce_back.payloads.ErrorDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class ExceptionsHandler {
    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) //400
    public ErrorDTO handleBadRequest(BadRequestException ex){
        return new ErrorDTO(ex.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND) // 404
    public ErrorDTO handleNotFound(NotFoundException ex){
        return new ErrorDTO(ex.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) // 500
    public ErrorDTO handleGenericErrors(Exception ex){
        ex.printStackTrace();
        return new ErrorDTO("Problema lato server, giuro che lo risolveremo presto!", LocalDateTime.now());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) //400
    public ErrorDTO handleMissingBody(HttpMessageNotReadableException ex, WebRequest request) {
        return new ErrorDTO("devi inserire un body", LocalDateTime.now());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) // 400
    public ErrorDTO handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest request) {
        List<String> errors = new ArrayList<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.add( error.getDefaultMessage());
            break;
        }
        return new ErrorDTO(errors.getFirst().toString(),LocalDateTime.now());
    }
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) //400
    public ErrorDTO handleUncorrectPath(HttpRequestMethodNotSupportedException ex, WebRequest request) {
        return new ErrorDTO("devi inserire un path corretto", LocalDateTime.now());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) //400
    public ErrorDTO handleUncorrectPath(NoResourceFoundException ex, WebRequest request) {
        return new ErrorDTO("devi inserire un path corretto", LocalDateTime.now());
    }

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED) // 401
    public ErrorDTO handleUnauthorized(UnauthorizedException ex) {
        return new ErrorDTO(ex.getMessage(), LocalDateTime.now());
    }


    @ExceptionHandler(AuthorizationDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN) // 403
    public ErrorDTO handleForbidden(AuthorizationDeniedException ex) {
        return new ErrorDTO("Non hai i permessi per accedere", LocalDateTime.now());
    }
}
