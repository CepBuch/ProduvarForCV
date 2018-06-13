package produvar.interactionwithapi.models

import com.github.kittinunf.fuel.core.ResponseDeserializable
import produvar.interactionwithapi.enums.LoginType
import java.text.SimpleDateFormat
import java.util.*

data class User(val loginType: LoginType, val bearer: String, val username: String?, val name: String?, val role: String?) {
    constructor(bearer: String) : this(LoginType.QR, bearer, null, null, null)

    val logoutDate: Date

    init {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE, 1)
        logoutDate = calendar.time
    }
}

data class Order(val code: String?, val label: String?, val dueDate: String?,
                 val items: List<Item>, val process: List<ProcessStep>,
                 val statusFlow: List<WorkflowStep>
) {
    fun tryGetParsedDate(): Date? {
        val format = SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
        format.timeZone = TimeZone.getTimeZone("UTC")

        try {
            return format.parse(dueDate)
        } catch (ex: Exception) {
            return null
        }
    }

    fun containsUsefulData(): Boolean {
        return !label.isNullOrBlank() || !dueDate.isNullOrBlank()
    }
}

data class Manufacturer(val name: String?, val website: String?,
                        val phoneNumber: String?, val email: String?) {

    fun containsUsefulData(): Boolean {
        return !name.isNullOrBlank() || !website.isNullOrBlank() ||
                !phoneNumber.isNullOrBlank() || !email.isNullOrBlank()
    }
}

data class Item(val label: String)

data class WorkflowStep(val status: String, val isCurrent: Boolean, val isFinished: Boolean)

data class ProcessStep(val label: String?, val time: String?, val description: String?)