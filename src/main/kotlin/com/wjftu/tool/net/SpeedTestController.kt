package com.wjftu.tool.net

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.ModelAndView

@Controller
class SpeedTestController(
    val speedTestService: SpeedTestService
) {

    @GetMapping("/speedTest")
    fun speedTest() : ModelAndView {
        val mav = ModelAndView("speedtest")
        mav.addObject("result", speedTestService.testResult)
        return mav
    }

    @PostMapping("/speedTest")
    fun speedTest(
        @RequestParam("url")
        url: String,
        @RequestParam("threadCnt")
        threadCnt: Int,
        @RequestParam("total")
        total: Int) : ModelAndView {
        speedTestService.speedTest(url, threadCnt, total)
        val mav = speedTest()
        mav.addObject("url", url)
        mav.addObject("threadCnt", threadCnt)
        mav.addObject("total", total)
        return mav
    }



}