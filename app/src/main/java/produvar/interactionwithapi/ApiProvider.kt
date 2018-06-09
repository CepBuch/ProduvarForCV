package produvar.interactionwithapi

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import produvar.interactionwithapi.model.*

class ApiProvider {

    enum class TagType {
        URL, CODE
    }

    companion object {
        const val token = "1234apple"
        const val BASE_URL = "https://prodapp.000webhostapp.com"
        const val AUTHENTICATION_REQUEST = "/authenticate"
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

        var parsedRes: UserData? = null
        Fuel.upload(AUTHENTICATION_REQUEST, parameters = params)
                .dataParts { _, _ -> listOf() }
                .responseObject(UserData.Deserializer()) { req, res, result ->
                    when (result) {
                        is Result.Success -> {
                            parsedRes = result.get()
                        }
                    }
                }
        return parsedRes
    }

    fun searchByScan(tagContent: String, tagType: TagType) {
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
    }

    fun orderInfo(user: User, orderCode: String) {
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
    }
}