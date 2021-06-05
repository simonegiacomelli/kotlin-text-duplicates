package rpc

import Pattern
import kotlinx.serialization.Serializable

@Serializable
class ApiRequest1(val ping: String) : Request<ApiResponse1>

@Serializable
class ApiResponse1(val pong: String)

@Serializable
class ApiRequestSum(val a: Int, val b: Int) : Request<ApiResponseSum>

@Serializable
class ApiResponseSum(val sum: Int)


@Serializable
class ApiFindRequest(val text: String, val k: Int) : Request<ApiFindResponse>

@Serializable
data class ApiIntRange(val first: Int, val last: Int)

@Serializable
data class ApiDuplicates(val text: String, val ranges: List<ApiIntRange>)

fun Pattern.toApiDuplicates() = ApiDuplicates(text, ranges.map { ApiIntRange(it.first, it.last) })

@Serializable
class ApiFindResponse(val duplicates: List<ApiDuplicates>)


