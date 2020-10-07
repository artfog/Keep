package com.example.keep


import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.keep.MainActivity.Companion.EXTRA2
import com.example.keep.MainActivity.Companion.EXTRA_ID
import kotlinx.android.synthetic.main.activity_add_text.*

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

            var colorBG = item.text_bg_color
            colorBG = when (colorBG) {
                "Red" -> "#FCA8A2"
                "Green" -> "#A7F282"
                "Blue" -> "#82D2F2"
                "Pink" -> "#F2ACF3"
                else -> "#F2F6BC"
            }

            window.decorView.setBackgroundColor(Color.parseColor(colorBG));
            addtextButton.visibility = View.GONE

            if (item.text_title == ""){
                editTitle.visibility = View.GONE
                textView.visibility = View.GONE
            }
            else editTitle.setText(item.text_title)

            if (item.text_long == "") {
                editTextLong.visibility = View.GONE
                textView2.visibility = View.GONE
            }
            else editTextLong.setText(item.text_long)


            textBgColor.visibility = View.GONE
            textView3.visibility = View.GONE
            val units = resources.getStringArray(R.array.color_list)
            textBgColor.setSelection(units.indexOfFirst { it == item.text_bg_color })

            editTitle.isFocusable = false
            editTextLong.isFocusable = false
            textBgColor.isEnabled = false

            addtextButton.setOnClickListener { updateItem() }
        }
        else {
            addtextButton.setOnClickListener { appendItem() }
        }
    }

    private fun appendItem() {

        val item = TextItems(editTitle.text.toString(), editTextLong.text.toString(), textBgColor.selectedItem.toString(), "")
        item.tid = db.textItemDao().insertAll(item).first()

        val intent = Intent().putExtra(EXTRA2,3L)
        setResult(RESULT_OK, intent)
        Toast.makeText(this, R.string.item_create, Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun removeItem() {

        val id = intent.getLongExtra(EXTRA_ID, 0)
        val item = db.textItemDao().getItemById(id)
        db.textItemDao().delete(item)
        val intent = Intent().putExtra(EXTRA_ID, item.tid).putExtra(EXTRA2,3L)
        setResult(RESULT_OK, intent)
        finish()

    }

    private fun updateItem() {
        val id = intent.getLongExtra(EXTRA_ID, 0)
        val item = db.textItemDao().getItemById(id)
        var bgcolor = textBgColor.selectedItem.toString()
        if (bgcolor =="") bgcolor="yellow"

        db.textItemDao().update(
            item.copy(
                text_title = editTitle.text.toString(),
                text_long = editTextLong.text.toString(),
                text_bg_color = bgcolor
            )
        )

        val intent = Intent().putExtra(EXTRA_ID, item.tid).putExtra(EXTRA2,1L)
        setResult(RESULT_OK, intent)
        Toast.makeText(this, R.string.item_update, Toast.LENGTH_SHORT).show()
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
            R.id.editItem ->{
                addtextButton.text = getString(R.string.save)
                addtextButton.visibility = View.VISIBLE
                editTitle.visibility = View.VISIBLE
                editTextLong.visibility = View.VISIBLE
                textBgColor.visibility = View.VISIBLE
                textView.visibility = View.VISIBLE
                textView2.visibility = View.VISIBLE
                textView3.visibility = View.VISIBLE
                editTitle.isFocusableInTouchMode = true
                editTextLong.isFocusableInTouchMode = true
                textBgColor.isEnabled = true
                true
            }
            R.id.shareInfo -> {
                val sendIntent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:")
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
                        Toast.makeText(this, R.string.item_delete, Toast.LENGTH_SHORT).show()
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