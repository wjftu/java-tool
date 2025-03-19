package com.wjftu.tool.error

import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.servlet.ModelAndView

@ControllerAdvice
class ErrorHandler {

    @ExceptionHandler(Exception::class)
    fun handleError(e: Exception) : ModelAndView {
        val mav = ModelAndView("error")
        mav.addObject("error", e)
        mav.addObject("message", e.message)
        return mav
    }
}