package produvar.interactionwithapi.model

import java.io.Serializable
import java.util.*

data class UserData(val bearer: String?, val username: String?, val name: String?, val role: String?)


enum class LoginType {
    PersonalAccount, QR
}

data class User(val loginType: LoginType, val bearer: String?, val username: String?, val name: String?, val role: String?) : Serializable {
//    var loginDate: Date? = Calendar.getInstance().time

}