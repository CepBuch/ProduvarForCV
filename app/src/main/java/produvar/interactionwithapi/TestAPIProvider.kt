package produvar.interactionwithapi

import android.content.res.Resources
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.core.Method
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.fuel.httpUpload
import com.github.kittinunf.result.Result
import com.google.gson.Gson
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import produvar.interactionwithapi.enums.ErrorType
import produvar.interactionwithapi.enums.TagType
import produvar.interactionwithapi.helpers.TagChecker
import produvar.interactionwithapi.interfaces.ApiProvider
import produvar.interactionwithapi.interfaces.AsyncApiProvider
import produvar.interactionwithapi.models.*

class TestAPIProvider : AsyncApiProvider {

    companion object {
        const val BASE_URL = "https://prodapp.000webhostapp.com"
        const val AUTHENTICATE_REQUEST = "/authenticate"
        const val LOGIN_REQUEST = "/login"
        const val SEARCH_BY_SCAN_REQUEST = "/searchbyscan"
        const val ORDERS_REQUEST = "/orders"
        const val UPDATE_STATUS_REQUEST = "/orderstatusupdate"
        const val LOGOUT_REQUEST = "/logout"
    }

    init {
        FuelManager.instance.basePath = BASE_URL
    }

    override fun authenticate(authenticationTag: String): Deferred<Pair<UserDTO?, ErrorType?>> {
        val params = listOf(
                "code" to authenticationTag
        )

        return async(CommonPool) {
            val (_, response, result) = AUTHENTICATE_REQUEST.httpUpload(Method.POST, params)
                    .dataParts { _, _ -> listOf() }
                    .responseObject(UserDTO.Deserializer())

            when (result) {
                is Result.Success -> Pair(result.get(), null)
                else -> Pair(null, when (response.statusCode) {
                    403 -> ErrorType.NOT_FOUND
                    else -> ErrorType.UNDEFINED
                })
            }
        }
    }


    override fun login(username: String, password: String): Deferred<Pair<UserDTO?, ErrorType?>> {
        val params = listOf(
                "username" to username,
                "password" to password)

        return async(CommonPool) {
            val (_, response, result) = LOGIN_REQUEST.httpUpload(Method.POST, params)
                    .dataParts { _, _ -> listOf() }
                    .responseObject(UserDTO.Deserializer())

            when (result) {
                is Result.Success -> Pair(result.get(), null)
                else -> Pair(null, when (response.statusCode) {
                    403 -> ErrorType.NOT_FOUND
                    else -> ErrorType.UNDEFINED
                })
            }
        }
    }

    override fun logout(user: User): Deferred<ErrorType?> {
        return async(CommonPool)
        {
            val (_, _, result) = LOGOUT_REQUEST.httpPost()
                    .header("Authorization" to "Bearer ${user.bearer}")
                    .response()

            when (result) {
                is Result.Success -> null
                else -> ErrorType.UNDEFINED
            }
        }
    }

    override fun searchByScan(tagContent: String): Deferred<Pair<BasicOrderViewDTO?, ErrorType?>> {
        val params = listOf(
                when (TagChecker.classify(tagContent)) {
                    TagType.URL -> "url"
                    else -> "code"
                } to tagContent
        )

        return async(CommonPool) {
            val (_, response, result) = SEARCH_BY_SCAN_REQUEST.httpGet(params)
                    .responseObject(BasicOrderViewDTO.Deserializer())

            when (result) {
                is Result.Success -> Pair(result.get(), null)
                else -> Pair(null, when (response.statusCode) {
                    400 -> ErrorType.NOT_FOUND
                    else -> ErrorType.UNDEFINED
                })
            }
        }
    }


    override fun orderInfo(user: User, orderCode: String): Deferred<Pair<OrderDTO?, ErrorType?>> {
        val params = listOf(
                "code" to orderCode,
                "skip" to 0,
                "limit" to 1
        )

        return async(CommonPool) {
            val (_, response, result) = ORDERS_REQUEST.httpGet(params)
                    .header("Authorization" to "Bearer ${user.bearer}")
                    .responseObject(OrderDTO.Deserializer())

            when (result) {
                is Result.Success -> Pair(result.get(), null)
                else -> Pair(null, when (response.statusCode) {
                    400 -> ErrorType.NOT_FOUND
                    else -> ErrorType.UNDEFINED
                })
            }
        }
    }

    override fun orderStatusUpdate(user: User, orderCode: String, currentStatus: String,
                                   newStatus: String, description: String, location: String): Deferred<ErrorType?> {
        val params = mapOf(
                "who" to user.username,
                "code" to orderCode,
                "description" to description,
                "location" to location,
                "currentStatus" to currentStatus,
                "newStatus" to newStatus
        )

        return async(CommonPool) {
            val (_, response, result) = UPDATE_STATUS_REQUEST.httpPost()
                    .authenticate(user.username!!, user.bearer)
                    .header(
                            "Authorization" to "Bearer ${user.bearer}",
                            "Content-Type" to "application/json")
                    .body(Gson().toJson(params))
                    .responseString()

            when (result) {
                is Result.Success -> null
                else -> when (response.statusCode) {
                    400 -> ErrorType.FORBIDDEN
                    409 -> ErrorType.NOT_FOUND
                    else -> ErrorType.UNDEFINED
                }
            }
        }
    }
}