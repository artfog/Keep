package com.example.keep


import android.app.AlertDialog
import android.content.ContentProvider
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.keep.MainActivity.Companion.EXTRA2
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

            //imageDeleteButton.setOnClickListener { removeItem() }
        }
        else {

            //imageDeleteButton.setVisibility(View.INVISIBLE)
            addtextButton.setOnClickListener { appendItem() }
        }
    }

//
    private fun appendItem() {

        var colorBG = textBgColor.toString()
        if (colorBG == "Red") colorBG = "#F29082"
        else if(colorBG == "Green") colorBG = "#A7F282"
        else colorBG = "#A7F282"
        val item = TextItems(editTitle.text.toString(), editTextLong.text.toString(), colorBG)
        item.tid = db.textItemDao().insertAll(item).first()
       // val tids = db.textItemDao().insertAll(item).first()
           // items.add(item)
    //        adapter.notifyItemInserted(items.size+1)
    //        mainItems.smoothScrollToPosition(0)
        //println("tid = $tids")
        val intent = Intent().putExtra(EXTRA2,3L)
        setResult(RESULT_OK, intent)
        finish()
    }

    private fun removeItem() {

        val id = intent.getLongExtra(EXTRA_ID, 0)
        val item = db.textItemDao().getItemById(id)
        db.textItemDao().delete(item)
//        val position = items.indexOfFirst { it.tid == item.tid }
//        println("id $id")
//        adapter.notifyItemRemoved(position)

        val intent = Intent().putExtra(EXTRA_ID, item.tid).putExtra(EXTRA2,3L)
        setResult(RESULT_OK, intent)
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
       //println("id $id")
        val intent = Intent().putExtra(EXTRA_ID, item.tid).putExtra(EXTRA2,1L)
        setResult(RESULT_OK, intent)

        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val id = intent.getLongExtra(EXTRA_ID, 0)
        if (id > 0) menuInflater.inflate(R.menu.menu_settings, menu)
        return true
    }

    //back button
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            R.id.shareInfo -> {


                val sendIntent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:")
//                  putExtra(Intent.EXTRA_EMAIL, arrayOf(editEmail.text.toString()))
                    putExtra(Intent.EXTRA_SUBJECT, editTitle.text.toString())
                    putExtra(Intent.EXTRA_TEXT, editTextLong.text.toString())
                }
                intent.resolveActivity(packageManager)?.let {
                    startActivity(sendIntent)
                }
                true
            }
            R.id.deleteItem -> {

                val builder = AlertDialog.Builder(this)
                builder.setTitle(R.string.delete)
                .setMessage(R.string.delete_info)
                    .setPositiveButton(R.string.delete) { _, _ ->
                        removeItem()
                        Toast.makeText(this, R.string.item_delete, Toast.LENGTH_LONG).show()
                    }
                    .setNegativeButton(R.string.cancel) { _, _ -> }
                val dialog = builder.create()
                dialog.show()



                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}