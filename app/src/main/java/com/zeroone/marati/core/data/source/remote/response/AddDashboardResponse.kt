package com.zeroone.marati.core.data.source.remote.response

data class AddDashboardResponse(
	val data: List<DataItem>? = null,
	val isError: Boolean? = null,
	val message: String? = null
)


