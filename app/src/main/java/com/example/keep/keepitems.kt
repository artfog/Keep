package com.example.keep

import androidx.room.*



@Entity(tableName = "text_items")
data class TextItems(
    val text_title: String,
    val text_long: String,
    //val text_item_color: String,
    @PrimaryKey(autoGenerate = true) var tid: Long = 0
)

@Dao
interface TextItemsDao {
    @Query("SELECT * FROM text_items")
    fun getAll(): List<TextItems>

    @Query("SELECT * FROM text_items WHERE tid = :itemId")
    fun getItemById(itemId: Long): TextItems

    @Insert
    fun insertAll(vararg items: TextItems): List<Long>

    @Update
    fun update(item: TextItems)

    @Delete
    fun delete(item: TextItems)

}
