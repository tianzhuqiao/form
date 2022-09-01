package com.feiyilin.form

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

abstract class FormFragment : Fragment() {
    var recyclerView: RecyclerView? = null
    val adapter: FormRecyclerAdapter?
        get() {
            return recyclerView?.adapter as? FormRecyclerAdapter
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.form_activity, container, false)
        recyclerView = view.findViewById(R.id.formRecyclerView)
        recyclerView?.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = FormRecyclerAdapter(onFormItemListener).apply { }
        }
        initForm()

        return view
    }

    open var onFormItemListener: FormItemCallback? = null

    abstract fun initForm()

    fun drawable(res: Int): Drawable? {
        return ContextCompat.getDrawable(requireContext(), res)
    }

    fun color(res: Int): Int {
        return ContextCompat.getColor(requireContext(), res)
    }
}