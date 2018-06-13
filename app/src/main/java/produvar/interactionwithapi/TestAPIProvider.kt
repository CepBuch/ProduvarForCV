package produvar.interactionwithapi

import android.content.res.Resources
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.core.Method
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.fuel.httpUpload
import com.github.kittinunf.result.Result
import com.google.gson.Gson
import produvar.interactionwithapi.enums.ErrorType
import produvar.interactionwithapi.enums.TagType
import produvar.interactionwithapi.interfaces.ApiProvider
import produvar.interactionwithapi.models.*

class TestAPIProvider : ApiProvider {

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

    override fun authenticate(authenticationTag: String,
                              success: (UserDTO) -> Unit, failure: (ErrorType) -> Unit) {
        val params = listOf(
                "code" to authenticationTag
        )

        AUTHENTICATE_REQUEST.httpUpload(Method.POST, params)
                .dataParts { _, _ -> listOf() }
                .responseObject(UserDTO.Deserializer()) { _, _, either ->
                    either.fold(
                            success = { success(it) },
                            failure = {
                                failure(when (it.response.statusCode) {
                                    403 -> ErrorType.NOT_FOUND
                                    else -> ErrorType.UNDEFINED
                                })
                            })
                }
    }

    override fun login(username: String, password: String,
                       success: (UserDTO) -> Unit, failure: (ErrorType) -> Unit) {
        val params = listOf(
                "username" to username,
                "password" to password)

        LOGIN_REQUEST.httpUpload(Method.POST, params)
                .dataParts { _, _ -> listOf() }
                .responseObject(UserDTO.Deserializer()) { _, _, result ->
                    when (result) {
                        is Result.Success -> {
                            success(result.get())
                        }
//                        else -> null
                    }
                }
    }

    override fun logout(user: User, success: (Boolean) -> Unit, failure: (ErrorType) -> Unit) {
        LOGOUT_REQUEST.httpPost()
                .header("Authorization" to "Bearer ${user.bearer}")
                .response { _, response, _ ->
                    success(response.statusCode == 200)
                }
    }

    override fun searchByScan(tagContent: String, tagType: TagType,
                              success: (BasicOrderViewDTO) -> Unit, failure: (ErrorType) -> Unit) {
        val params = listOf(
                if (tagType == TagType.URL) {
                    "url" to tagContent
                } else {
                    "code" to tagContent
                }
        )

        SEARCH_BY_SCAN_REQUEST.httpGet(params)
                .responseObject(BasicOrderViewDTO.Deserializer()) { _, _, result ->
                    when (result) {
                        is Result.Success -> {
                            success(result.get())
                        }
//                        else -> null
                    }
                }
    }

    override fun orderInfo(user: User, orderCode: String, success: (OrderDTO) -> Unit, failure: (ErrorType) -> Unit) {
        val params = listOf(
                "code" to orderCode,
                "skip" to 0,
                "limit" to 1
        )

        ORDERS_REQUEST.httpGet(params)
                .header("Authorization" to "Bearer ${user.bearer}")
                .responseObject(OrderDTO.Deserializer()) { req, res, result ->
                    when (result) {
                        is Result.Success -> {
                            success(result.get())
                        }
                    }
                }
    }

    override fun orderStatusUpdate(user: User, orderCode: String, currentStatus: String,
                                   newStatus: String, success: (Boolean) -> Unit,
                                   failure: (ErrorType) -> Unit) {
        val params = mapOf(
                "who" to user.bearer,
                "code" to orderCode,
                "description" to "blah-blah-blah",
                "location" to "U menya doma",
                "currentStatus" to currentStatus,
                "newStatus" to newStatus
        )
        UPDATE_STATUS_REQUEST.httpPost()
                .authenticate(user.username!!, user.bearer)
                .header(
                        "Authorization" to "Bearer ${user.bearer}",
                        "Content-Type" to "application/json")
                .body(Gson().toJson(params))
                .responseString { request, response, _ ->
                    success(when (response.statusCode) {
                        201 -> true
                    // TODO() exception
                        409 -> throw IllegalArgumentException("The fromStatus does not match the current order status")
                        else -> false
                    })
                }
    }
}