package produvar.interactionwithapi

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.FuelManager
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
    }

    init {
        FuelManager.instance.basePath = BASE_URL
    }

    fun authenticate(authTagContent: String): UserData? {
        val params = listOf(
                "code" to authTagContent
        )

        var parsedUserData: UserData? = null
        // Classic option
//        Fuel.post(AUTHENTICATION_REQUEST, parameters = params)
        // multipart/form-data
        Fuel.upload(AUTHENTICATION_REQUEST, parameters = params)
                .dataParts { _, _ -> listOf() }
                .responseObject(UserData.Deserializer()) { req, res, result ->
                    when (result) {
                        is Result.Success -> {
                            parsedUserData = result.get()
                        }
                    }
                }
        return parsedUserData
    }

    fun login(username: String, password: String) {
        //TODO: wait for Olga and learn how to add values in header
    }

    fun searchByScan(tagContent: String, tagType: TagType): BasicOrderView? {
        val params = listOf(
                if (tagType == TagType.URL) {
                    "url" to tagContent
                } else {
                    "code" to tagContent
                }
        )

        var parsedBasicOrderView: BasicOrderView? = null
        Fuel.get(SEARCH_BY_SCAN_REQUEST, params)
                .responseObject(BasicOrderView.Deserializer()) { req, res, result ->
                    when (result) {
                        is Result.Success -> {
                            parsedBasicOrderView = result.get()
                        }
                    }
                }
        return parsedBasicOrderView
    }

    fun orderInfo(user: User, orderCode: String): Order? {
        val params = listOf(
                "code" to orderCode,
                "skip" to 0,
                "limit" to 1
        )
        var parsedOrder: Order? = null

        Fuel.get(ORDERS_REQUEST, params)
                .authenticate(user.username!!, user.bearer)
                .responseObject(Order.Deserializer()) { req, res, result ->
                    when (result) {
                        is Result.Success -> {
                            parsedOrder = result.get()
                        }
                    }
                }
        return parsedOrder
    }
}