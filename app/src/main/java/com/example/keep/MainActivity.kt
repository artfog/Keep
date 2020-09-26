package com.example.keep

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), ClickListener {


    private val db get() = Database.getInstance(this)

    private val items = mutableListOf<TextItems>()

   private lateinit var adapter: KeepRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        items.addAll(db.textItemDao().getAll())
        items.sortByDescending { it.tid }
        adapter = KeepRecyclerAdapter(this, items)
        mainItems.adapter = adapter

        imageAddButton.setOnClickListener {
            val intent = Intent(this,AddTextActivity::class.java)
                .putExtra(EXTRA_ID, 0)
            startActivityForResult(intent, REQUEST_CODE_DETAILS)

          }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

       // println("REQUEST_CODE_DETAILS $REQUEST_CODE_DETAILS RESULT_OK $RESULT_OK")
        if (requestCode == REQUEST_CODE_DETAILS && resultCode == RESULT_OK && data != null) {
            val vvv = data.getLongExtra(EXTRA2, 0)
           // println("vvv $vvv ")
            if (vvv == 1L) {
                val id = data.getLongExtra(EXTRA_ID, 0)
                 val item = db.textItemDao().getItemById(id)
                val position = items.indexOfFirst { it.tid == item.tid }
                items[position] = item
                adapter.notifyItemChanged(position)
            }
//            if (vvv == 2L) {

//                adapter.notifyItemInserted(items.size+1)
//                mainItems.smoothScrollToPosition(0)
//            }
            if (vvv == 3L) {
//                val id = data.getLongExtra(EXTRA_ID, 0)
//                val item = db.textItemDao().getItemById(id)
               // val position = items.indexOfFirst { it.tid == item.tid }
               // println("id $id")
                items.clear()
                items.addAll(db.textItemDao().getAll())
                items.sortByDescending { it.tid }

                adapter = KeepRecyclerAdapter(this, items)
                mainItems.adapter = adapter
                mainItems.smoothScrollToPosition(0)
               // adapter.notifyItemRemoved(position)
            }
        }



    }
     override fun itemClicked(item: TextItems) {
        val intent = Intent(this,AddTextActivity::class.java)
            .putExtra(EXTRA_ID, item.tid)
        startActivityForResult(intent, REQUEST_CODE_DETAILS)

    }

    companion object {
        const val EXTRA_ID = "com.example.keep.keep_item_id"
        const val EXTRA2 = "com.example.keep.keep_vvv"
        const val REQUEST_CODE_DETAILS = 12345
    }
}

interface ClickListener {

    fun itemClicked(item: TextItems)

}