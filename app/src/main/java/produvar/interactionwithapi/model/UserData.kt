package produvar.interactionwithapi.model

import java.io.Serializable

data class UserData(val bearer: String, val username: String, val name: String, val role: String) : Serializable {}