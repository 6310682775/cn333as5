package com.example.numphone.domain.model
import com.example.numphone.database.TagDbModel

data class TagModel (
    val id: Long,
    val type: String,
    ) {
        companion object{
            val DEFAULT = with(TagDbModel.DEFAULT_TAG){ TagModel(id,type)}
        }
    }
