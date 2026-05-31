package com.tatardancespace.handler;

import com.tatardancespace.exception.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
@Order(2)
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ModelAndView handleNoResource(NoResourceFoundException ex, HttpServletRequest request) {
        String path = request.getRequestURI();

        if (path.matches(".*\\.(css|js|png|jpg|jpeg|gif|svg|ico|webp|woff|woff2|ttf)$")) {
            log.debug("Статический ресурс не найден: {}", path);
            return null;
        }

        log.warn("404 страница: {}", path);
        ModelAndView mav = new ModelAndView("error/404");
        mav.addObject("statusCode", 404);
        mav.addObject("errorMessage", "Страница не найдена");
        mav.addObject("path", path);
        return mav;
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ModelAndView handleNotFound(NoHandlerFoundException ex, HttpServletRequest request) {
        String path = request.getRequestURI();

        if (path.matches(".*\\.(css|js|png|jpg|jpeg|gif|svg|ico|webp|woff|woff2|ttf)$")) {
            return null;
        }

        log.warn("404 страница (NoHandlerFound): {}", path);
        ModelAndView mav = new ModelAndView("error/404");
        mav.addObject("statusCode", 404);
        mav.addObject("errorMessage", "Страница не найдена");
        mav.addObject("path", path);
        return mav;
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ModelAndView handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        log.warn("403 ошибка: {} для {}", request.getRequestURI(), request.getRemoteAddr());
        ModelAndView mav = new ModelAndView("error/403");
        mav.addObject("statusCode", 403);
        mav.addObject("errorMessage", "Доступ запрещён");
        mav.addObject("path", request.getRequestURI());
        return mav;
    }

    @ExceptionHandler(com.tatardancespace.exception.AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ModelAndView handleCustomAccessDenied(com.tatardancespace.exception.AccessDeniedException ex, HttpServletRequest request) {
        log.warn("403 кастомная ошибка: {} - {}", request.getRequestURI(), ex.getMessage());
        ModelAndView mav = new ModelAndView("error/403");
        mav.addObject("statusCode", 403);
        mav.addObject("errorMessage", ex.getMessage());
        mav.addObject("path", request.getRequestURI());
        return mav;
    }

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ModelAndView handleBusinessException(BusinessException ex, HttpServletRequest request) {
        log.warn("404 бизнес-ошибка: {} - {}", request.getRequestURI(), ex.getMessage());
        ModelAndView mav = new ModelAndView("error/404");
        mav.addObject("statusCode", 404);
        mav.addObject("errorMessage", ex.getMessage());
        mav.addObject("path", request.getRequestURI());
        return mav;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ModelAndView handleGenericException(Exception ex, HttpServletRequest request) {
        String path = request.getRequestURI();

        log.error("500 ошибка: {} - {}", path, ex.getMessage(), ex);
        ModelAndView mav = new ModelAndView("error/500");
        mav.addObject("statusCode", 500);
        mav.addObject("errorMessage", "Внутренняя ошибка сервера");
        mav.addObject("path", path);
        return mav;
    }
}