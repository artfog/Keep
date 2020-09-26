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

        adapter = KeepRecyclerAdapter(this, items)
        mainItems.adapter = adapter

        mainButtonAdd.setOnClickListener {
            val intent = Intent(this, AddTextActivity ::class.java)
            startActivity(intent)
          }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_DETAILS && resultCode == RESULT_OK && data != null) {
            val id = data.getLongExtra(EXTRA_ID, 0)
            val item = db.textItemDao().getItemById(id)
            val position = items.indexOfFirst { it.tid == item.tid }
            items[position] = item
            adapter.notifyItemChanged(position)
        }



    }
     override fun itemClicked(item: TextItems) {
        val intent = Intent(this,AddTextActivity::class.java)
            .putExtra(EXTRA_ID, item.tid)
        startActivityForResult(intent, REQUEST_CODE_DETAILS)

    }

    companion object {
        const val EXTRA_ID = "com.example.keep.keep_item_id"
        const val REQUEST_CODE_DETAILS = 12345
    }
}

interface ClickListener {

    fun itemClicked(item: TextItems)

}