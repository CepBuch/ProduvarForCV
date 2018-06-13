package produvar.interactionwithapi

import produvar.interactionwithapi.interfaces.ApiProvider

object Factory {
    fun getApiProvider(): ApiProvider = TestAPIProvider()
}