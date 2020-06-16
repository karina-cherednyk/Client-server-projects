package handlers

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import kotlin.Exception

enum class Method { GET , POST, PUT, DELETE }
class UriBinder(val method: Method, pattern: String, val handler: UriHandler) {
    val regex = pattern.toRegex()
    fun matches(exchange: HttpExchange) = exchange.requestMethod == method.name && exchange.requestURI.toString().matches(regex)
    fun handle(exchange: HttpExchange) = handler.handle(exchange)
}

class ErrorResponse(val errorClass: String, val message: String, val role: String? = null)
open class ServerException(message: String, val code: Int): Exception(message)
class PublicPageException(message: String, code: Int = 404): ServerException(message,code)
class AnonymousPageException(message: String, code: Int = 404): ServerException(message,code)
class AdminPageException(message: String, code: Int = 404): ServerException(message,code)

abstract class UriHandler: HttpHandler {

    companion object {
        val mapper = jacksonObjectMapper()

        fun writeResponse(exchange: HttpExchange, statusCode: Int, response: Any?=null) {
            try {
                if( response == null){
                    exchange.sendResponseHeaders(statusCode,-1)
                    exchange.close()
                    return
                }
                val bytes = mapper.writeValueAsBytes(response)
                exchange.responseHeaders.add("Content-Type", "application/json")
                exchange.sendResponseHeaders(statusCode, bytes.size.toLong())
                exchange.responseBody.write(bytes)
                exchange.close()
            }catch (e : Exception){ println(e.message)}
        }
    }

    @Throws(Exception::class)
    abstract fun handleOrThrow(exchange: HttpExchange)

    override fun handle(exchange: HttpExchange?) {
        try {
            handleOrThrow(exchange!!)
        }catch (e: Exception ){
            val code = if( e is ServerException) e.code else 500
            return writeResponse(exchange!!, code, ErrorResponse(e::class.simpleName!!,e.message!!,exchange.principal.realm))
        }
    }
}

