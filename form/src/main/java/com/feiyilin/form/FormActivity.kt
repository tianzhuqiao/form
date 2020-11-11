package com.feiyilin.form

import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

abstract class FormActivity : AppCompatActivity() {
    var settings = mutableListOf<FormItem>()
    var recyclerView: RecyclerView? = null
    val adapter: FormRecyclerAdaptor?
        get() {
            return recyclerView?.adapter as? FormRecyclerAdaptor
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.form_activity)

        recyclerView = findViewById<RecyclerView>(R.id.formRecyclerView)
        recyclerView?.apply {
            layoutManager = LinearLayoutManager(this@FormActivity)

            adapter = FormRecyclerAdaptor(settings, onFormItemListener).apply {
            }
        }
        initForm()
    }

    abstract var onFormItemListener: FormItemCallback?

    abstract fun initForm()

    fun drawable(res: Int): Drawable? {
        return ContextCompat.getDrawable(this, res)
    }

    fun color(res: Int): Int {
        return ContextCompat.getColor(this, res)
    }
}