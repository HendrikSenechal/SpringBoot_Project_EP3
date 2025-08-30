package com.springBoot_Opdracht_EP3;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import exception.DuplicateEntityException;
import exception.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class ApiExceptionHandler {

	private ProblemDetail base(HttpStatus status, String title, String detail, HttpServletRequest req) {
		ProblemDetail pd = ProblemDetail.forStatusAndDetail(status, detail);
		pd.setTitle(title);
		pd.setProperty("timestamp", Instant.now().toString());
		pd.setProperty("path", req.getRequestURI());
		return pd;
	}

	@ExceptionHandler(EntityNotFoundException.class)
	public ProblemDetail notFound(EntityNotFoundException ex, HttpServletRequest req) {
		return base(HttpStatus.NOT_FOUND, "Festival not found", ex.getMessage(), req);
	}

	@ExceptionHandler(DuplicateEntityException.class)
	public ProblemDetail duplicate(DuplicateEntityException ex, HttpServletRequest req) {
		return base(HttpStatus.CONFLICT, "Duplicate festival", ex.getMessage(), req);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ProblemDetail invalidBody(MethodArgumentNotValidException ex, HttpServletRequest req) {
		Map<String, String> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
				.collect(Collectors.toMap(fe -> fe.getField(), DefaultMessageSourceResolvable::getDefaultMessage,
						(a, b) -> b, LinkedHashMap::new));
		ProblemDetail pd = base(HttpStatus.BAD_REQUEST, "Validation failed", "One or more fields are invalid.", req);
		pd.setProperty("errors", fieldErrors);
		return pd;
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ProblemDetail invalidParams(ConstraintViolationException ex, HttpServletRequest req) {
		Map<String, String> errors = ex.getConstraintViolations().stream().collect(Collectors
				.toMap(v -> v.getPropertyPath().toString(), v -> v.getMessage(), (a, b) -> b, LinkedHashMap::new));
		ProblemDetail pd = base(HttpStatus.BAD_REQUEST, "Constraint violation", "Invalid request parameters.", req);
		pd.setProperty("errors", errors);
		return pd;
	}

	@ExceptionHandler({ MethodArgumentTypeMismatchException.class, MissingServletRequestParameterException.class })
	public ProblemDetail badRequest(Exception ex, HttpServletRequest req) {
		return base(HttpStatus.BAD_REQUEST, "Bad request", ex.getMessage(), req);
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ProblemDetail dbConflict(DataIntegrityViolationException ex, HttpServletRequest req) {
		return base(HttpStatus.CONFLICT, "Data integrity violation", "The operation conflicts with existing data.",
				req);
	}

	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ProblemDetail methodNotAllowed(HttpRequestMethodNotSupportedException ex, HttpServletRequest req) {
		return base(HttpStatus.METHOD_NOT_ALLOWED, "Method not allowed", ex.getMessage(), req);
	}

	@ExceptionHandler(HttpMediaTypeNotSupportedException.class)
	public ProblemDetail mediaType(HttpMediaTypeNotSupportedException ex, HttpServletRequest req) {
		ProblemDetail pd = base(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Unsupported media type", ex.getMessage(), req);
		pd.setProperty("supported", ex.getSupportedMediaTypes().stream().map(MediaType::toString).toList());
		return pd;
	}

	@ExceptionHandler(Exception.class)
	public ProblemDetail fallback(Exception ex, HttpServletRequest req) {
		return base(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", "An unexpected error occurred.", req);
	}
}
