package produvar.interactionwithapi.helpers

class TagChecker {
    companion object {
        fun isOrderTagValid(tagContent: String): Boolean {
            // Expected to be more detailed
            return isUrl(tagContent) || isBarcode(tagContent)
        }

        fun isAuthorizationTagValid(tagContent: String): Boolean {
            // Expected to be more detailed
            return isUrl(tagContent) || isBarcode(tagContent)
        }

        private fun isUrl(tagContent: String): Boolean {
            return tagContent.startsWith("https://") || tagContent.startsWith("http://")
        }

        private fun isBarcode(tagContent: String): Boolean {
            return tagContent.all {
                it.isDigit()
            }
        }
    }
}