package produvar.interactionwithapi.models

import java.util.*

enum class LoginType {
    PersonalAccount, QR
}

data class User(val loginType: LoginType, val bearer: String, val username: String?, val name: String?, val role: String?) {
    constructor(bearer: String) : this(LoginType.QR, bearer, null, null, null)

    companion object {
        fun fromDTO(loginType: LoginType, userData: UserData): User {
            return User(loginType, userData.bearer, userData.username, userData.name, userData.role)
        }
    }

    val logoutDate: Date

    init {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE, 1)
        logoutDate = calendar.time
    }


}