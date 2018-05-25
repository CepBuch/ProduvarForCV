package produvar.interactionwithapi.model

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

data class BasicOrderView(val orderCode: String?, val manufacturer: BasicManufacturerView?)

data class BasicManufacturerView(val name: String?, val website: String?,
                                 val phoneNumber: String?, val email: String?) {

    fun containsUsefulData(): Boolean {
        return !name.isNullOrBlank() || !website.isNullOrBlank() ||
                !phoneNumber.isNullOrBlank() || !email.isNullOrBlank()
    }
}

data class Order(val code: String?, val label: String?, val dueDate: String?,
                 val items: List<OrderItem?>?, val process: List<OrderProcess?>?,
                 val statusFlow: List<WorkFlowStep?>?
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

    fun filterOrderItems(): List<OrderItem> {
        return items?.filterNotNull()?.filterNot { it.label.isNullOrBlank() }
                ?: emptyList()
    }

    fun filterOrderProcesses(): List<OrderProcess> {
        return process?.filterNotNull()?.filterNot { it.label.isNullOrBlank() }
                ?: emptyList()
    }

    fun filterStatusFlow(): List<WorkFlowStep> {
        return statusFlow?.filterNotNull()?.filterNot { it.status.isNullOrBlank() }
                ?: emptyList()
    }
}

data class OrderItem(val label: String?)

data class WorkFlowStep(val status: String?)

data class OrderProcess(val label: String?)


