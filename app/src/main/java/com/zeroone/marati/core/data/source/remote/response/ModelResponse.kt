package com.zeroone.marati.core.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class ModelResponse(

	@field:SerializedName("data")
	val data: List<ModelItem?>? = null,

	@field:SerializedName("is_error")
	val isError: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class Input(

	@field:SerializedName("name")
	var name: String? = null,

	@field:SerializedName("type")
	var type: String? = null
)

data class Output(

	@field:SerializedName("name")
	var name: String? = null,

	@field:SerializedName("type")
	var type: String? = null
)

data class ModelItem(

	@field:SerializedName("output")
	var output: Output? = null,

	@field:SerializedName("input")
	var input: Input? = null,

	@field:SerializedName("filename")
	val filename: String? = null,

	@field:SerializedName("owner_id")
	val ownerId: String? = null,

	@field:SerializedName("name")
	var name: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("description")
	var description: String? = null,

	@field:SerializedName("category")
	var category: String? = null,

	@field:SerializedName("id")
	val id: String? = null
)
