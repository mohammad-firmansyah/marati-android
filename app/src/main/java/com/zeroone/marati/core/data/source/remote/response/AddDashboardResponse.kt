package com.zeroone.marati.core.data.source.remote.response

data class AddDashboardResponse(
	val data: DataDashboard? = null,
	val isError: Boolean? = null,
	val message: String? = null
)

data class DataDashboard(
	val server: String? = null,
	val password: String? = null,
	val ownerId: String? = null,
	val name: String? = null,
	val description: String? = null,
	val createdAt: String? = null,
	val id: String? = null,
	val username: String? = null
)

