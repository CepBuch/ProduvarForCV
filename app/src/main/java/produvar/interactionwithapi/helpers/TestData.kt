package produvar.interactionwithapi.helpers

import produvar.interactionwithapi.model.*

class TestData {
    companion object {
        val manufacturer = BasicManufacturerView("Evolve Productions B.V",
                "https://www.evolveproductions.nl", "+79256410156",
                "info@evolveproductions.nl")
        val orderItems = listOf<String>("Worktop X34 for Johnson family", "everything is added dynamically",
                "according to server response", "if server doesn't provide data for one or other card it won't be shown",
                "btw, not sure if icons in this card are ok").map { OrderItem(it) }

        private val messages = listOf<String>("2017-03-03 15:33 Pete Manson polished the blade",
                "2018-05-23 2:37 if it has date it will be highlighted", "otherwise, just shown as a string",
                "i'm also thinking of making every card expandable/collapsable",
                "2018-05-23 2:46 When opened, probably show ''expanded'' only Manufacturer and Order info and collapse others")
                .map { OrderProcess(it) }

        private val workFlowSteps = listOf<String>("1st previous step", "2nd step", "these", "are", "steps", "of execution",
                "transportation", "Current step").map { WorkFlowStep(it) }

        val futureSteps = listOf<String>("futureStep1", "futureStep2", "futureStep3", "These steps", "should also",
                "be displayed", "in statusFlowProcess", "if we can distinguish", "previous current and future").map{WorkFlowStep(it)}

        val basicOrderView = BasicOrderView("2018004938", manufacturer)

        val order = Order(basicOrderView.orderCode, "Worktop X34 for Johnson family",
                "2018-07-29T09:12:33.001Z", orderItems, messages, workFlowSteps)
    }
}