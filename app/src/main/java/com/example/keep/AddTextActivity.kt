package com.example.keep

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.example.keep.MainActivity.Companion.EXTRA_ID
import kotlinx.android.synthetic.main.activity_add_text.*
import kotlinx.android.synthetic.main.activity_main.*

class AddTextActivity : AppCompatActivity() {
    private val db get() = Database.getInstance(this)

    private val items = mutableListOf<TextItems>()

    private lateinit var adapter: KeepRecyclerAdapter



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_text)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val id = intent.getLongExtra(EXTRA_ID, 0)
        val item = db.textItemDao().getItemById(id)


        if (id > 0){
            addtextButton.text = getString(R.string.save)
            editTitle.setText(item.text_title)
            editTextLong.setText(item.text_long)
            addtextButton.setOnClickListener { updateItem() }
           // buttonDelete.setOnClickListener { removeItem() }
            imageDeleteButton.setOnClickListener { removeItem() }
        }
        else {
          //  buttonDelete.setVisibility(View.INVISIBLE)
            imageDeleteButton.setVisibility(View.INVISIBLE)
            addtextButton.setOnClickListener { appendItem() }
        }
    }

//
    private fun appendItem() {

        val item = TextItems(editTitle.text.toString(), editTextLong.text.toString())
        item.tid = db.textItemDao().insertAll(item).first()
        items.add(item)
        adapter.notifyItemInserted(items.size+1)
      //  mainItems.smoothScrollToPosition(0)
    val intent = Intent(this, MainActivity ::class.java)
    startActivity(intent)
      //  finish()
    }

    private fun removeItem() {

        val id = intent.getLongExtra(EXTRA_ID, 0)
        val item = db.textItemDao().getItemById(id)
        db.textItemDao().delete(item)
        val position = items.indexOfFirst { it.tid == item.tid }
        println("id $id")
        adapter.notifyItemRemoved(position)
        finish()
    }
    private fun updateItem() {
        val id = intent.getLongExtra(EXTRA_ID, 0)
        val item = db.textItemDao().getItemById(id)
        db.textItemDao().update(
            item.copy(
                text_title = editTitle.text.toString(),
                text_long = editTextLong.text.toString()
            )
        )
        println("id $id")
        val intent = Intent().putExtra(EXTRA_ID, item.tid,)
        setResult(RESULT_OK, intent)

        finish()
    }

//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.menu_settings, menu)
//        return true
//    }




    //back button
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        return when (item.itemId) {
//            android.R.id.home -> {
//                finish()
//                true
//            }
//            R.id.shareInfo -> {
//                Toast.makeText(
//                    this,
//                    "Clicked menu icon",
//                    Toast.LENGTH_SHORT
//                ).show()
//                true
//            }
////            R.id.deleteItem -> {
////                removeItem()
////                true
////            }
//            else -> super.onOptionsItemSelected(item)
//        }
//    }

}