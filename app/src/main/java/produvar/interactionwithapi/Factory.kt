package produvar.interactionwithapi

import produvar.interactionwithapi.interfaces.ApiProvider
import produvar.interactionwithapi.interfaces.AsyncApiProvider

object Factory {
    fun getApiProvider(): AsyncApiProvider = TestAPIProvider()
}