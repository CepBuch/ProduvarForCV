package produvar.interactionwithapi

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.delay
import produvar.interactionwithapi.enums.ErrorType
import produvar.interactionwithapi.interfaces.AsyncApiProvider
import produvar.interactionwithapi.models.BasicOrderViewDTO
import produvar.interactionwithapi.models.OrderDTO
import produvar.interactionwithapi.models.User
import produvar.interactionwithapi.models.UserDTO

class OfflineAPIProvider : AsyncApiProvider {
    // ONLY TEST DATA (INCLUDING BEARER) IS USED IN THIS CLASS
    // Json strings imply answers from a server (they were copied from working API responses)
    // Code style (especially line length) in this class may be treated as poor,
    // but I wrote it very quickly just for demonstration and I don't want to store it
    // in separate files or constants:)
    // "delay(millisecs) are used to imitate a request

    override fun authenticate(authenticationTag: String): Deferred<Pair<UserDTO?, ErrorType?>> {
        return async(CommonPool) {
            delay(500)
            when (authenticationTag) {
                "12341234" -> Pair(UserDTO.Deserializer().deserialize(
                        "{\"role\":\"customer\",\"bearer\":\"1234customer\"}"), null)
                "56785678" -> Pair(UserDTO.Deserializer().deserialize(
                        "{\"bearer\":\"1234noname\"}"), null)
                else -> Pair(null, ErrorType.NOT_FOUND)
            }
        }
    }


    override fun login(username: String, password: String): Deferred<Pair<UserDTO?, ErrorType?>> {
        return async(CommonPool) {
            delay(500)
            when ("$username|$password") {
                "steve.jobs@apple.com|apple" -> Pair(UserDTO.Deserializer().deserialize(
                        "{\"role\":\"salesmanager\",\"name\":\"Steve Jobs\",\"bearer\":\"1234apple\",\"username\":\"steve.jobs@apple.com\"}"), null)
                "procter.gamble@pg.com|procter" -> Pair(UserDTO.Deserializer().deserialize(
                        "{\"role\":\"productionworker\",\"name\":\"Mister Procter\",\"bearer\":\"1234procter\",\"username\":\"procter.gamble@pg.com\"}"), null)
                "iam.helicopter@flying.com|helicopter" -> Pair(UserDTO.Deserializer().deserialize(
                        "{\"role\":\"transportworker\",\"name\":\"Helly Copter\",\"bearer\":\"1234helicopter\",\"username\":\"iam.helicopter@flying.com\"}"), null)
                "donotwork@today.com|iamlazy" -> Pair(UserDTO.Deserializer().deserialize(
                        "{\"role\":\"transportation\",\"name\":\"Jobber Hopper\",\"bearer\":\"1234notworker\",\"username\":\"donotwork@today.com\"}"), null)
                else -> Pair(null, ErrorType.NOT_FOUND)
            }
        }
    }

    override fun logout(user: User): Deferred<ErrorType?> {
        return async(CommonPool) {
            delay(500)
            null
        }
    }

    override fun searchByScan(tagContent: String): Deferred<Pair<BasicOrderViewDTO?, ErrorType?>> {
        return async(CommonPool) {
            delay(500)
            when (tagContent) {
                "https://facebook.com" -> Pair(BasicOrderViewDTO.Deserializer().deserialize(
                        "{\"manufacturer\":{\"website\":\"https:\\/\\/facebook.com\",\"name\":\"Mark Zuckerberg\",\"phonenumber\":\"0592-544633\",\"email\":\"mark@facebook.com\"},\"ordercode\":\"2018004939\"}"), null)
                "4fn34rqfrefk" -> Pair(BasicOrderViewDTO.Deserializer().deserialize(
                        "{\"manufacturer\":{\"website\":\"https:\\/\\/www.evolveproductions.nl\",\"name\":\"Evolve Productions B.V>\",\"phonenumber\":\"0592-544633\",\"email\":\"info@evolveproductions.nl\"},\"ordercode\":\"2018004938\"}"), null)
                else -> Pair(null, ErrorType.NOT_FOUND)
            }
        }
    }


    override fun orderInfo(user: User, orderCode: String): Deferred<Pair<OrderDTO?, ErrorType?>> {
        return async(CommonPool) {
            delay(500)
            when (orderCode) {
                "2018004938" -> when (user.bearer) {
                    "1234customer", "1234procter" -> Pair(OrderDTO.Deserializer().deserialize(
                            "[{\"code\":\"2018004938\",\"label\":\"Worktop X34 for Johnson family\",\"dueDate\":\"2018-07-29T09:12:33.001Z\"}]"), null)
                    "1234noname" -> Pair(OrderDTO.Deserializer().deserialize(
                            "[{\"code\":\"2018004938\",\"label\":\"Worktop X34 for Johnson family\",\"dueDate\":\"2018-07-29T09:12:33.001Z\",\"items\":[{\"label\":\"Left part of worktop X34\"},{\"label\":\"Right part of worktop X34\"},{\"label\":\"Central part of worktop X34\"},{\"label\":\"Something very important\"},{\"label\":\"Something not vital but useful\"}],\"statusFlow\":[{\"status\":\"Engineering\",\"iscurrent\":false,\"isfinished\":true},{\"status\":\"Approval\",\"iscurrent\":false,\"isfinished\":true},{\"status\":\"Production step 1\",\"iscurrent\":true,\"isfinished\":false},{\"status\":\"Production step 2\",\"iscurrent\":false,\"isfinished\":false},{\"status\":\"Production step 3\",\"iscurrent\":false,\"isfinished\":false},{\"status\":\"Production step 4\",\"iscurrent\":false,\"isfinished\":false},{\"status\":\"Production step 5\",\"iscurrent\":false,\"isfinished\":false},{\"status\":\"Transport\",\"iscurrent\":false,\"isfinished\":false}]}]"), null)
                    "1234apple", "1234notworker" -> Pair(OrderDTO.Deserializer().deserialize(
                            "[{\"code\":\"2018004938\",\"label\":\"Worktop X34 for Johnson family\",\"dueDate\":\"2018-07-29T09:12:33.001Z\",\"items\":[{\"label\":\"Left part of worktop X34\"},{\"label\":\"Right part of worktop X34\"},{\"label\":\"Central part of worktop X34\"},{\"label\":\"Something very important\"},{\"label\":\"Something not vital but useful\"}],\"process\":[{\"label\":\"2017-03-03 15:33 Sam White designed the left part\",\"when\":\"2017-03-03 15:33\",\"description\":\"Sam White designed the left part\"},{\"label\":\"2017-03-04 17:22 James Right designed the right part\",\"when\":\"2017-03-04 17:22\",\"description\":\"James Right designed the right part\"},{\"label\":\"2017-03-07 15:33 Pete Manson polished the blade\",\"when\":\"2017-03-07 15:33\",\"description\":\"Pete Manson polished the blade very well\"},{\"label\":\"2017-03-08 15:34 John Watson drinked his cup of tea\",\"when\":\"2017-03-08 15:34\",\"description\":\"John Watson drinked his cup of black tea with milk\"}],\"statusFlow\":[{\"status\":\"Engineering\",\"iscurrent\":\"false\",\"isfinished\":\"true\"},{\"status\":\"Approval\",\"iscurrent\":\"false\",\"isfinished\":\"true\"},{\"status\":\"Production step 1\",\"iscurrent\":\"true\",\"isfinished\":\"false\"},{\"status\":\"Production step 2\",\"iscurrent\":\"false\",\"isfinished\":\"false\"},{\"status\":\"Production step 3\",\"iscurrent\":\"false\",\"isfinished\":\"false\"},{\"status\":\"Production step 4\",\"iscurrent\":\"false\",\"isfinished\":\"false\"},{\"status\":\"Production step 5\",\"iscurrent\":\"false\",\"isfinished\":\"false\"},{\"status\":\"Transport\",\"iscurrent\":\"false\",\"isfinished\":\"false\"}]}]"), null)
                    else -> Pair(null, ErrorType.NOT_FOUND)
                }
                "2018004939" -> when (user.bearer) {
                    "1234apple" -> Pair(OrderDTO.Deserializer().deserialize(
                            "[{\"code\":\"2018004939\",\"label\":\"Broken laptop X2000\",\"dueDate\":\"2019-09-21T09:12:33.001Z\",\"items\":[{\"label\":\"Left part of worktop X34\"},{\"label\":\"Right part of worktop X34\"},{\"label\":\"Central part of worktop X34\"},{\"label\":\"Something very important\"},{\"label\":\"Something not vital but useful\"}],\"process\":[{\"label\":\"2017-03-03 15:33 Sam White designed the left part\",\"when\":null,\"description\":null},{\"label\":null,\"when\":\"2017-03-04 17:22\",\"description\":\"James Right designed the right part\"},{\"label\":null,\"when\":\"2017-03-07 15:33\",\"description\":null},{\"label\":\"2017-03-08 15:34 John Watson drinked his cup of tea\",\"when\":\"2017-03-08 15:34\",\"description\":\"John Watson drinked his cup of black tea with milk\"}],\"statusFlow\":[{\"status\":\"Engineering\",\"iscurrent\":false,\"isfinished\":true},{\"status\":\"Approval\",\"iscurrent\":false,\"isfinished\":true},{\"status\":\"Production step 1\",\"iscurrent\":false,\"isfinished\":true},{\"status\":\"Production step 2\",\"iscurrent\":false,\"isfinished\":true},{\"status\":\"Production step 3\",\"iscurrent\":false,\"isfinished\":false},{\"status\":\"Production step 4\",\"iscurrent\":false,\"isfinished\":false},{\"status\":\"Production step 5\",\"iscurrent\":false,\"isfinished\":false},{\"status\":\"Transport\",\"iscurrent\":false,\"isfinished\":false}]}]"), null)
                    else -> Pair(null, ErrorType.NOT_FOUND)
                }
                else -> Pair(null, ErrorType.NOT_FOUND)
            }

        }
    }

    override fun orderStatusUpdate(user: User, orderCode: String, currentStatus: String,
                                   newStatus: String, description: String, location: String): Deferred<ErrorType?> {
        return async(CommonPool) {
            delay(500)
            if (user.bearer == "1234apple" && orderCode == "4fn34rqfrefk") {
                null
            } else {
                ErrorType.FORBIDDEN
            }
        }
    }
}