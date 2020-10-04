package com.example.keep

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.StaggeredGridLayoutManager
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

        val orientation = this.resources.configuration.orientation
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            mainItems.layoutManager =
                StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        } else {
            mainItems.layoutManager =
                StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
        }


        textAddButton.setOnClickListener {
            val intent = Intent(this, AddTextActivity::class.java)
                .putExtra(EXTRA_ID, 0)
            startActivityForResult(intent, REQUEST_CODE_DETAILS)

          }
        imageAddButton.setOnClickListener {
            val intent = Intent(this, ImageActivity::class.java)
                .putExtra(EXTRA_ID, 0)
            startActivityForResult(intent, REQUEST_CODE_DETAILS)

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_DETAILS && resultCode == RESULT_OK && data != null) {
            val vvv = data.getLongExtra(EXTRA2, 0)

            if (vvv == 1L) {
                val id = data.getLongExtra(EXTRA_ID, 0)
                 val item = db.textItemDao().getItemById(id)
                val position = items.indexOfFirst { it.tid == item.tid }
                items[position] = item
                adapter.notifyItemChanged(position)
            }

            if (vvv == 3L) {
                items.clear()
                items.addAll(db.textItemDao().getAll())
                items.sortByDescending { it.tid }

                adapter = KeepRecyclerAdapter(this, items)
                mainItems.adapter = adapter
                mainItems.smoothScrollToPosition(0)

            }
        }



    }
     override fun itemClicked(item: TextItems) {
         if (item.text_title == "") {
             val intent = Intent(this, ImageActivity::class.java)
                 .putExtra(EXTRA_ID, item.tid)
             startActivityForResult(intent, REQUEST_CODE_DETAILS)
         }
         else {
             val intent = Intent(this, AddTextActivity::class.java)
                 .putExtra(EXTRA_ID, item.tid)
             startActivityForResult(intent, REQUEST_CODE_DETAILS)
         }


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