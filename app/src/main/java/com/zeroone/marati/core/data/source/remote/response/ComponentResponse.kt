package com.zeroone.marati.core.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class ComponentResponse(

	@field:SerializedName("data")
	val data: List<ComponentItem?>? = null,

	@field:SerializedName("is_error")
	val isError: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class ComponentItem(

	@field:SerializedName("w")
	val w: Int? = null,

	@field:SerializedName("x")
	val x: Int? = null,

	@field:SerializedName("h")
	val h: Int? = null,

	@field:SerializedName("y")
	val y: Int? = null,

	@field:SerializedName("topic")
	val topic: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("rules")
	val rules: String? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("model_id")
	val modelId: Any? = null,

	@field:SerializedName("type")
	val type: String? = null,

	@field:SerializedName("content")
	var content: String? = null,

	@field:SerializedName("dashboard_id")
	val dashboardId: String? = null
)
