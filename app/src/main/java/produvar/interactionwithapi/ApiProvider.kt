package produvar.interactionwithapi

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.httpHead
import com.github.kittinunf.result.Result
import produvar.interactionwithapi.models.*

class ApiProvider {

    enum class TagType {
        URL, CODE
    }

    companion object {
        const val BASE_URL = "https://prodapp.000webhostapp.com"
        const val AUTHENTICATION_REQUEST = "/authenticate"
        const val LOGIN_REQUEST = "/login"
        const val SEARCH_BY_SCAN_REQUEST = "/searchbyscan"
        const val ORDERS_REQUEST = "/orders"
        const val UPDATE_STATUS_REQUEST = "/orderstatusupdate"
    }

    init {
        FuelManager.instance.basePath = BASE_URL
    }

    fun authenticate(authTagContent: String): UserDTO? {
        val params = listOf(
                "code" to authTagContent
        )

        var parsedUserData: UserDTO? = null
        // Not the best but working way to send a requet in form-data
        Fuel.upload(AUTHENTICATION_REQUEST, parameters = params)
                .dataParts { _, _ -> listOf() }
                .responseObject(UserDTO.Deserializer()) { req, res, result ->
                    when (result) {
                        is Result.Success -> {
                            parsedUserData = result.get()
                        }
                    }
                }
        return parsedUserData
    }

    fun login(username: String, password: String): UserDTO? {
        val params = mapOf(
                "username" to username,
                "password" to password)

        var parsedUserData: UserDTO? = null
        Fuel.post(LOGIN_REQUEST)
                .header(params)
                .responseObject(UserDTO.Deserializer()) { req, res, result ->
                    when (result) {
                        is Result.Success -> {
                            parsedUserData = result.get()
                        }
                    }
                }
        return parsedUserData
    }

    fun searchByScan(tagContent: String, tagType: TagType): BasicOrderViewDTO? {
        val params = listOf(
                if (tagType == TagType.URL) {
                    "url" to tagContent
                } else {
                    "code" to tagContent
                }
        )

        var parsedBasicOrderView: BasicOrderViewDTO? = null
        Fuel.get(SEARCH_BY_SCAN_REQUEST, params)
                .responseObject(BasicOrderViewDTO.Deserializer()) { req, res, result ->
                    when (result) {
                        is Result.Success -> {
                            parsedBasicOrderView = result.get()
                        }
                    }
                }
        return parsedBasicOrderView
    }

    fun orderInfo(user: User, orderCode: String): OrderDTO? {
        val params = listOf(
                "code" to orderCode,
                "skip" to 0,
                "limit" to 1
        )
        var parsedOrderDTO: OrderDTO? = null

        Fuel.get(ORDERS_REQUEST, params)
                .authenticate(user.username!!, user.bearer)
                .responseObject(OrderDTO.Deserializer()) { req, res, result ->
                    when (result) {
                        is Result.Success -> {
                            parsedOrderDTO = result.get()
                        }
                    }
                }
        return parsedOrderDTO
    }

    fun orderStatusUpdate(user: User, orderCode: String, currentStatus: String, newStatus: String): Boolean {
        val params = listOf(
                "who" to user.bearer,
                "code" to orderCode,
                "currentStatus" to currentStatus,
                "newStatus" to newStatus

        )
        var flag = false
        Fuel.post(UPDATE_STATUS_REQUEST, params)
                .authenticate(user.username!!, user.bearer)
                .header("Content-Type" to "application/json")
                .responseString { req, res, result ->
                    flag = when (res.statusCode) {
                        201 -> true
                        409 -> throw IllegalArgumentException("The fromStatus does not match the current order status")
                        else -> false
                    }
                }
        return flag

    }
}