package com.feiyilin.form

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
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
    }

    abstract var onFormItemListener: FormItemCallback?
}