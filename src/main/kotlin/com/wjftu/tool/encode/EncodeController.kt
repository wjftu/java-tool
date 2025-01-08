package com.wjftu.tool.encode

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.ModelAndView

@Controller
class EncodeController(
    @Autowired
    val encodeService: EncodeService
) {

    @GetMapping("base64")
    fun base64() : String {
        return "base64"
    }

    @RequestMapping("base64encode")
    fun base64encode(
        @RequestParam("plain")
        plain: String,
    ) : ModelAndView {
        val mav = ModelAndView("base64")
        val base64 = encodeService.base64Encode(plain)
        mav.addObject("plain", plain)
        mav.addObject("base64", base64)
        return mav
    }

    @RequestMapping("base64decode")
    fun base64decode(
        @RequestParam("base64")
        base64: String,
    ) : ModelAndView {
        val mav = ModelAndView("base64")
        val plain = encodeService.base64Decode(base64)
        mav.addObject("plain", plain)
        mav.addObject("base64", base64)
        return mav
    }
}