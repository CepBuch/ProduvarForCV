package produvar.interactionwithapi.model

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson
import java.io.Serializable
import java.util.*

data class UserData(val bearer: String?, val username: String?, val name: String?, val role: String?){
    class Deserializer : ResponseDeserializable<UserData>{
        override fun deserialize(content: String): UserData? = Gson().fromJson(content, UserData::class.java)
    }
}


enum class LoginType {
    PersonalAccount, QR
}

data class User(val loginType: LoginType, val bearer: String, val username: String?, val name: String?, val role: String?) {
    constructor(bearer: String) : this(LoginType.QR, bearer, null, null, null)

    val logoutDate: Date
    init {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE, 1)
        logoutDate = calendar.time
    }





}