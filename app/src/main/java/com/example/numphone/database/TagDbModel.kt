package com.example.numphone.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class TagDbModel(
    @PrimaryKey(autoGenerate = true) val id:Long = 0,
    @ColumnInfo(name = "type") val type: String,
) {
    companion object{
        val DEFAULT_TAGS = listOf(
            TagDbModel(1,"Mobile"),
            TagDbModel(2,"Home"),
            TagDbModel(3,"Work"),
            TagDbModel(4,"School"),
            TagDbModel(5,"University"),
                    TagDbModel(6,"Friend")
            )

        val DEFAULT_TAG = DEFAULT_TAGS[0]
    }
}
