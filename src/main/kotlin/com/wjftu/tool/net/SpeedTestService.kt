package com.wjftu.tool.net

import org.apache.hc.client5.http.classic.methods.HttpGet
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient
import org.apache.hc.client5.http.impl.classic.HttpClients
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager
import org.apache.hc.core5.http.io.entity.EntityUtils
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.ByteArrayOutputStream
import java.net.URI
import java.net.URL
import java.time.LocalDateTime
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong

@Service
class SpeedTestService {

    val log = LoggerFactory.getLogger(SpeedTestService::class.java)

    var testResult: SpeedTestResult? = null

    fun speedTest(url: String, threadCnt: Int, total: Int) {
        val connectionManager = PoolingHttpClientConnectionManager().apply {
            maxTotal = threadCnt
            defaultMaxPerRoute = threadCnt
        }

        val uri = URL(url).toURI()

        val httpClient: CloseableHttpClient = HttpClients.custom()
            .setConnectionManager(connectionManager)
            .build()

        val executorService = Executors.newFixedThreadPool(threadCnt)

        val totalTraffic = AtomicLong(0)
        val successCnt = AtomicInteger(0)
        val totalCnt = AtomicInteger(0)

        val result = SpeedTestResult(url, threadCnt, total, successCnt, totalTraffic, LocalDateTime.now(), null)

        testResult = result

        for(i in 1..threadCnt) {
            executorService.submit {
                while (true) {
                    val curCount = totalCnt.getAndIncrement()
                    if(curCount >= total) {
                        break
                    }
                    val traffic = sendRequest(uri, httpClient)
                    log.info("traffic = $traffic")
                    if(traffic != -1L) {
                        totalTraffic.addAndGet(traffic)
                        successCnt.incrementAndGet()
                    }
                }
            }
        }

        executorService.shutdown()

        try {
            if (!executorService.awaitTermination(2, TimeUnit.HOURS)) {
                log.info("Timeout reached, forcing shutdown of thread pool...")
                executorService.shutdownNow()
            }
        } catch (e: InterruptedException) {
            log.info("Thread pool was interrupted, forcing shutdown...")
            executorService.shutdownNow()
        }

        result.endTime = LocalDateTime.now()
        log.info("end speed test, result: $result")

        httpClient.close()

    }

    fun sendRequest(uri: URI, httpClient: CloseableHttpClient) : Long{
        var traffic = 0L
        try {
            // 创建 GET 请求
            val httpGet = HttpGet(uri)
            httpGet.addHeader("", "")

            httpClient.executeOpen(null, httpGet, null).use { response ->
                val entity = response.entity
                entity?.let {
                    val contentLength = entity.contentLength
                    if (contentLength >= 0) {
                        println("${Thread.currentThread().name} - Content Length: $contentLength bytes")
                    } else {
                        // If content length is unknown, calculate the actual size by reading the body
                        val byteArrayOutputStream = ByteArrayOutputStream()
                        entity.content.copyTo(byteArrayOutputStream)
                        val receivedBytes = byteArrayOutputStream.size()
                        traffic = receivedBytes.toLong()
                    }
                }
                EntityUtils.consume(response.entity)
            }
        } catch (e: Exception) {
            log.error("send request error", e)
            return -1L
        }
        return traffic
    }


    class SpeedTestResult(
        val url: String,
        val threadCnt: Int,
        val total: Int,
        val success: AtomicInteger,
        val traffic: AtomicLong,
        val startTime: LocalDateTime,
        var endTime: LocalDateTime?
    ) {
        override fun toString(): String {
            return "SpeedTestResult(url='$url', threadCnt=$threadCnt, total=$total, success=$success, traffic=$traffic, startTime=$startTime, endTime=$endTime)"
        }
    }
}