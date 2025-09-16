package com.example.demo.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(CustomerNotFoundException ex, HttpServletRequest req) {
        return err(HttpStatus.NOT_FOUND , ex.getMessage(), req);
    }

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(AccountNotFoundException ex, HttpServletRequest req) {
        return err(HttpStatus.NOT_FOUND , ex.getMessage(), req);
    }

    @ExceptionHandler(NotEnoughBalanceException.class)
    public ResponseEntity<ApiError> handleBalanceProblem(NotEnoughBalanceException ex, HttpServletRequest req) {
        return err(HttpStatus.BAD_REQUEST , ex.getMessage(), req);
    }

    @ExceptionHandler(SameAccountTransferException.class)
    public ResponseEntity<ApiError> handleAccountTransferProblem(SameAccountTransferException ex, HttpServletRequest req) {
        return err(HttpStatus.BAD_REQUEST , ex.getMessage(), req);
    }

    @ExceptionHandler(AmountNotEnoughError.class)
    public ResponseEntity<ApiError> handleAmountNotEnoughError(AmountNotEnoughError ex, HttpServletRequest req) {
       return err(HttpStatus.BAD_REQUEST , ex.getMessage(), req);
    }

    @ExceptionHandler(AccountClosedException.class)
    public ResponseEntity<ApiError> handleBusiness(AccountClosedException exception , HttpServletRequest req){

        return err(HttpStatus.BAD_REQUEST , exception.getMessage(), req);
    }

    @ExceptionHandler(BalanceNotZeroException.class)
    public ResponseEntity<ApiError> handleBalanceNotZeroException(BalanceNotZeroException ex, HttpServletRequest req){
        return err(HttpStatus.BAD_REQUEST , ex.getMessage(), req);
    }

    @ExceptionHandler(AccountAlreadyClosedException.class)
    public ResponseEntity<ApiError> handleAccountAlreadyClosed(AccountAlreadyClosedException ex, HttpServletRequest req){
        return err(HttpStatus.BAD_REQUEST , ex.getMessage(), req);
    }

    @ExceptionHandler(CustomerHasActiveBalanceException.class)
    public ResponseEntity<ApiError> handleCustomerHasActiveBalanceException(CustomerHasActiveBalanceException ex,
                                                                            HttpServletRequest req){
        return err(HttpStatus.BAD_REQUEST , ex.getMessage(), req);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req){
        return err(HttpStatus.BAD_REQUEST , ex.getMessage(), req);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> exception(Exception exception, HttpServletRequest req) {
        var status = HttpStatus.INTERNAL_SERVER_ERROR;
        return err(status, exception.getMessage(), req);
    }

    private ResponseEntity<ApiError> err(HttpStatus status,
                                         String message,
                                         HttpServletRequest req) {
        return ResponseEntity.status(status)
                .body(ApiError.of(status, message, req.getRequestURI()));
    }



}
