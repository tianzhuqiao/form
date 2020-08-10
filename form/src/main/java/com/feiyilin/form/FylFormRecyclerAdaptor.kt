package com.feiyilin.form

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.net.Uri
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import android.view.MotionEvent
import android.view.View.OnTouchListener
import androidx.constraintlayout.widget.ConstraintLayout

interface FlyFormItemCallback {
    fun onSetup(item: FylFormItem, viewHolder: RecyclerView.ViewHolder) {}
    fun onValueChanged(item: FylFormItem) {}
    fun onItemClicked(item: FylFormItem, viewHolder: RecyclerView.ViewHolder) {}
    fun onStartReorder(item: FylFormItem, viewHolder: RecyclerView.ViewHolder) {}
    fun onTitleImageClicked(item: FylFormItem) {}
    fun onAction(item: FylFormItem) {}
}


open class FylFormRecyclerAdaptor(
    private var settings: List<FylFormItem>,
    private var listener: FlyFormItemCallback? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class ViewHolderItem(var type: String, var layoutId: Int, var viewHolderClass: Class<out FylFormViewHolder>)
    private var viewHolders: MutableList<ViewHolderItem> = mutableListOf()
    init {
        viewHolders = mutableListOf (
            ViewHolderItem("text", R.layout.form_item_text, FylFormTextViewHolder::class.java),
            ViewHolderItem("image", R.layout.form_item_image, FylFormImageViewHolder::class.java),
            ViewHolderItem("section", R.layout.form_item_section, FylFormSectionViewHolder::class.java),
            ViewHolderItem("text_area", R.layout.form_item_text, FylFormTextAreaViewHolder::class.java),
            ViewHolderItem("action", R.layout.form_item_action, FylFormActionViewHolder::class.java),
            ViewHolderItem("text_group", R.layout.form_item_text, FylFormTextGroupViewHolder::class.java),
            ViewHolderItem("switch", R.layout.from_item_switch, FylFormSwitchViewHolder::class.java),
            ViewHolderItem("switch_native", R.layout.from_item_switch_native, FylFormSwitchNativeViewHolder::class.java),
            ViewHolderItem("radio", R.layout.form_item_radio, FylFormRadioViewHolder::class.java),
            ViewHolderItem("nav", R.layout.form_item_nav, FylFormNavViewHolder::class.java),
            ViewHolderItem("label", R.layout.form_item_label, FylFormLabelViewHolder::class.java)
        )
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val item = viewHolders[viewType]
        val viewHolder = item.viewHolderClass.kotlin
        return viewHolder.constructors.first().call(inflater, item.layoutId, parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val s = settings[position]
        if (holder is FylFormViewHolder) {
            holder.bind(s, onFormItemCallback)
        }
    }

    override fun getItemCount(): Int {
        return settings.size
    }

    fun setSettings(settings: List<FylFormItem>) {
        this.settings = settings
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        val m = settings[position]
        val index = viewHolders.indexOfFirst {it.type == m.type}
        return index
    }

    fun registerViewHolder(type: String, layoutId: Int, viewHolderClass: Class<out FylFormViewHolder>) : Boolean {
        viewHolders.add(ViewHolderItem(type, layoutId, viewHolderClass))
        return true
    }

    fun updateItem(item: FylFormItem) {
        val index = settings.indexOf(item)
        if (index >= 0 && index < settings.size) {
            notifyItemChanged(index)
        }
    }

    fun updateRadioGroup(group: String, selected: String) {
        for(i in 0 until settings.size) {
            val item =  settings[i]
            if(item is FylFormItemRadio && item.group == group ) {
                    item.isOn = (item.tag == selected)
                    updateItem(item)
            }
        }
    }

    private var onFormItemCallback = object : FlyFormItemCallback {
        override fun onValueChanged(item: FylFormItem) {
             if (item is FylFormItemRadio) {
                updateRadioGroup(item.group, item.tag)
            }
            listener?.onValueChanged(item)
        }
        override fun onItemClicked(item: FylFormItem, viewHolder: RecyclerView.ViewHolder) {
            listener?.onItemClicked(item, viewHolder)
        }
        override fun onSetup(item: FylFormItem, viewHolder: RecyclerView.ViewHolder) {
            listener?.onSetup(item, viewHolder)
        }
        override fun onStartReorder(item: FylFormItem, viewHolder: RecyclerView.ViewHolder) {
            listener?.onStartReorder(item, viewHolder)
        }
        override fun onTitleImageClicked(item: FylFormItem) {
            listener?.onTitleImageClicked(item)
        }
        override fun onAction(item: FylFormItem) {
            listener?.onAction(item)
        }
    }
}

open class FylFormViewHolder(inflater: LayoutInflater, resource: Int, parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(resource, parent, false)) {

    var titleView: TextView? = null
    var subtitleView: TextView? = null
    var reorderView: ImageView? = null
    init {
        titleView = itemView.findViewById(R.id.formElementTitle)
        subtitleView = itemView.findViewById(R.id.formElementSubTitle)
        reorderView = itemView.findViewById(R.id.formElementReorder)
    }
    open fun bind(s: FylFormItem, listener: FlyFormItemCallback?) {
        titleView?.text = s.title
        if (s.title.isNotEmpty()) {
            titleView?.visibility = View.VISIBLE
        } else {
            titleView?.visibility = View.GONE
        }
        subtitleView?.text = s.subTitle
        if (s.subTitle.isNotEmpty()) {
            subtitleView?.visibility = View.VISIBLE
        } else {
            subtitleView?.visibility = View.GONE
        }
        reorderView?.visibility = if (s.dragable) View.VISIBLE else View.GONE
        reorderView?.setOnTouchListener { _, event ->
            if (event.actionMasked == MotionEvent.ACTION_DOWN)
                listener?.onStartReorder(s, this)
            false
        }
    }

    fun dpToPx(dp: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat() ?: 0f, Resources.getSystem().displayMetrics).toInt()
    }
}


class FylFormImageViewHolder(inflater: LayoutInflater, resource: Int, parent: ViewGroup) :
    FylFormViewHolder(inflater, resource, parent) {
    private var imgView: ImageView? = null
    init {
        imgView = itemView.findViewById(R.id.profile_image)
    }

    override fun bind(s: FylFormItem, listener: FlyFormItemCallback?) {

        val file = File(s.value)
        if (file.exists()) {
            imgView?.setImageURI(Uri.fromFile(file))
        } else {
            //imgView?.profileImage(null, s.value, R.drawable.ic_btn_upload_photo_sign_in)
        }

        imgView?.setOnClickListener {
            listener?.onValueChanged(s)
        }
    }
}

open class FylFormBaseTextViewHolder(inflater: LayoutInflater, resource: Int, parent: ViewGroup) :
    FylFormViewHolder(inflater, resource, parent) {
    var valueView: EditText? = null
    var listener: FlyFormItemCallback? = null
    var item: FylFormItem? = null

    init {
        valueView = itemView.findViewById(R.id.formElementValue)
        reorderView = itemView.findViewById(R.id.formElementReorder)
        valueView?.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                if (v is EditText) {
                    Handler().postDelayed({
                        v.setSelection(v.text.length)
                    }, 10)
                }
            }
        }
    }

    override fun bind(s: FylFormItem, listener: FlyFormItemCallback?) {
        super.bind(s, listener)

        this.listener = listener
        this.item = s

        itemView.setOnClickListener {
            valueView?.requestFocus()
            if (s is FylFormItemText && !s.readOnly) {
                val imm = itemView.context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
                if (imm is InputMethodManager) {
                    imm.showSoftInput(valueView, InputMethodManager.SHOW_IMPLICIT)
                }
            }
        }



        if (s is FylFormItemText) {
            valueView?.textAlignment = s.textAlignment
            valueView?.isEnabled = !s.readOnly
            if (s.readOnly) {
                valueView?.setTextColor(Color.GRAY)
            } else {
                valueView?.setTextColor(Color.BLACK)
            }
            valueView?.setOnTouchListener(object : OnTouchListener {
                override fun onTouch(v: View, event: MotionEvent): Boolean {
                    return s.readOnly // the listener has consumed the event
                }
            })




            valueView?.setText(s.value)
            if (s.placeholder.isEmpty()) {
                valueView?.hint = "Enter ${s.title} here"
            } else {
                valueView?.hint = s.placeholder
            }
            if (s.imeOptions != 0) {
                valueView?.imeOptions = s.imeOptions
            } else {
                valueView?.imeOptions =
                    EditorInfo.IME_ACTION_NEXT or EditorInfo.IME_FLAG_NAVIGATE_NEXT or EditorInfo.IME_FLAG_NAVIGATE_PREVIOUS
            }
            if (s.inputType != 0) {
                valueView?.inputType = s.inputType
            }

            valueView?.addTextChangedListener(valueWatcher)
            if (s.focused) {
                Handler().postDelayed({
                    s.focused = false
                    valueView?.requestFocus()
                    val imm =
                        itemView.context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
                    if (imm is InputMethodManager) {
                        imm.showSoftInput(valueView, InputMethodManager.SHOW_FORCED)
                    }
                }, 200)
            }
        }
    }

    private val valueWatcher = object : TextWatcher {
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            item?.value = s.toString()
            item?.let {
                listener?.onValueChanged(it)
            }
        }

        override fun afterTextChanged(s: Editable?) {
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }
    }
}

open class FylFormTextViewHolder(inflater: LayoutInflater, resource: Int, parent: ViewGroup) :
    FylFormBaseTextViewHolder(inflater, resource, parent) {
}

class FylFormTextGroupViewHolder(inflater: LayoutInflater, resource: Int, parent: ViewGroup) :
    FylFormBaseTextViewHolder(inflater, resource, parent) {
    private var titleImgView: ImageView? = null

    init {
        titleImgView = itemView.findViewById(R.id.formElementTitleImage)
    }

    override fun bind(s: FylFormItem, listener: FlyFormItemCallback?) {
        super.bind(s, listener)

        valueView?.gravity = Gravity.START
        valueView?.textAlignment = View.TEXT_ALIGNMENT_TEXT_START

        if (s.imageResId != null) {
            titleImgView?.setImageResource(s.imageResId ?: 0)
            titleImgView?.visibility = View.VISIBLE
            titleImgView?.setOnClickListener {
                listener?.onTitleImageClicked(s)
            }
        } else {
            titleImgView?.visibility = View.GONE
        }
    }
}

class FylFormTextAreaViewHolder(inflater: LayoutInflater, resource: Int, parent: ViewGroup) :
    FylFormBaseTextViewHolder(inflater, resource, parent) {

    init {
        titleView?.visibility = View.GONE
        valueView?.textAlignment = View.TEXT_ALIGNMENT_TEXT_START
        valueView?.minLines = 6
        valueView?.maxLines = 6
        valueView?.minHeight = 120
        valueView?.gravity = Gravity.START
        valueView?.inputType = (EditorInfo.TYPE_TEXT_FLAG_CAP_SENTENCES or EditorInfo.TYPE_TEXT_FLAG_NO_SUGGESTIONS
                or EditorInfo.TYPE_TEXT_FLAG_MULTI_LINE or EditorInfo.TYPE_CLASS_TEXT)
        valueView?.imeOptions = EditorInfo.IME_NULL
    }

    override fun bind(s: FylFormItem, listener: FlyFormItemCallback?) {
        super.bind(s, listener)
        if (s is FylFormItemTextArea) {
            valueView?.minLines = s.minLines
            valueView?.maxLines = s.maxLines
        }
    }
}

class FylFormSectionViewHolder(inflater: LayoutInflater, resource: Int, parent: ViewGroup) :
    FylFormViewHolder(inflater, resource, parent) {

    init {
        titleView = itemView.findViewById(R.id.formElementTitle)
    }

    override fun bind(s: FylFormItem, listener: FlyFormItemCallback?) {
        super.bind(s, listener)
        if (s.title.isNotEmpty()) {
            titleView?.text = s.title.toUpperCase()
            titleView?.visibility = View.VISIBLE
        } else {
            titleView?.visibility = View.GONE
        }
    }
}

class FylFormActionViewHolder(inflater: LayoutInflater, resource: Int, parent: ViewGroup) :
    FylFormViewHolder(inflater, resource, parent) {
    var imgView: ImageView? = null

    init {
        titleView = itemView.findViewById(R.id.formElementTitle)
        imgView = itemView.findViewById(R.id.formElementImage)
    }

    override fun bind(s: FylFormItem, listener: FlyFormItemCallback?) {
        super.bind(s, listener)
        titleView?.setOnClickListener {
            listener?.onItemClicked(s, this)
        }

        titleView?.text = s.title
        if (s is FylFormItemAction) {
            titleView?.textAlignment = s.textAlignment
        }
        imgView?.visibility = View.GONE
        if (s.imageResId != null) {
            imgView?.setImageResource(s.imageResId ?: 0)
            imgView?.visibility = View.VISIBLE
        }
        if (s.value.isNotEmpty()) {
            //imgView?.nearalImage(null, s.value, null)
            imgView?.visibility = View.VISIBLE
        }
        listener?.onSetup(s, this)
    }
}

class FylFormSwitchNativeViewHolder(inflater: LayoutInflater, resource: Int, parent: ViewGroup) :
    FylFormViewHolder(inflater, resource, parent) {
    private var imgView: ImageView? = null
    private var switchView: Switch? = null

    init {
        titleView = itemView.findViewById(R.id.formElementTitle)
        imgView = itemView.findViewById(R.id.formElementImage)
        switchView = itemView.findViewById(R.id.formElementSwitch)
    }

    override fun bind(s: FylFormItem, listener: FlyFormItemCallback?) {
        super.bind(s, listener)
        if (s is FylFormItemSwitchNative) {
            itemView.setOnClickListener {
                s.isOn = !s.isOn
                switchView?.isChecked = s.isOn
                listener?.onValueChanged(s)
            }

            titleView?.text = s.title
            imgView?.visibility = View.GONE
            if (s.imageResId != null) {
                imgView?.setImageResource(s.imageResId ?: 0)
                imgView?.visibility = View.VISIBLE
            }

            switchView?.setOnClickListener {
                s.isOn = !s.isOn
                listener?.onValueChanged(s)
            }
            switchView?.isChecked = s.isOn
        }
    }
}

class FylFormSwitchViewHolder(inflater: LayoutInflater, resource: Int, parent: ViewGroup) :
    FylFormViewHolder(inflater, resource, parent) {
    private var switchView: ImageView? = null
    private var item : FylFormItemRadio? = null

    init {
        switchView = itemView.findViewById(R.id.formElementSwitch)
    }

    override fun bind(s: FylFormItem, listener: FlyFormItemCallback?) {
        super.bind(s, listener)

        if (s is FylFormItemSwitch) {
            itemView.setOnClickListener {
                s.isOn = !s.isOn
                setSwitchImage(s.isOn)
                listener?.onValueChanged(s)
            }
            setSwitchImage(s.isOn)
        }
    }

    fun setSwitchImage(checked: Boolean) {
        if (checked) {
            if (item?.iconOn != null) {
                switchView?.setImageDrawable(item?.iconOn)
            } else {
                switchView?.setImageResource(R.drawable.ic_toggle_on)
            }
        } else {
            if (item?.iconOff != null) {
                switchView?.setImageDrawable(item?.iconOff)
            } else {
                switchView?.setImageResource(R.drawable.ic_toggle_off)
            }
        }
    }
}


class FylFormRadioViewHolder(inflater: LayoutInflater, resource: Int, parent: ViewGroup) :
    FylFormViewHolder(inflater, resource, parent) {
    private var radioView: ImageView? = null
    private var item : FylFormItemRadio? = null

    init {
        radioView = itemView.findViewById(R.id.formElementRadio)
    }

    override fun bind(s: FylFormItem, listener: FlyFormItemCallback?) {
        super.bind(s, listener)
        itemView.setOnClickListener {
            if (s is FylFormItemRadio) {
                if (s.isOn) {
                    return@setOnClickListener
                }
                s.isOn = !s.isOn
                //setRadioImage(s.isOn)
                listener?.onValueChanged(s)
            }
        }
        if (s is FylFormItemRadio) {
            item = s
            setRadioImage(s.isOn)
        }
    }

    fun setRadioImage(checked: Boolean) {
        if (checked) {
            if (item?.iconOn != null) {
                radioView?.setImageDrawable(item?.iconOn)
            } else {
                radioView?.setImageResource(R.drawable.ic_radio_on)
            }
        } else {
            if (item?.iconOff != null) {
                radioView?.setImageDrawable(item?.iconOff)
            } else {
                radioView?.setImageResource(R.drawable.ic_radio_off)
            }
        }
    }
}

class FylFormNavViewHolder(inflater: LayoutInflater, resource: Int, parent: ViewGroup) :
    FylFormViewHolder(inflater, resource, parent) {
    var imgView: ImageView? = null
    var badgeView: ConstraintLayout? = null
    var badgeViewTitle: TextView? = null

    init {
        imgView = itemView.findViewById(R.id.formElementTitleImage)
        badgeView = itemView.findViewById(R.id.formElementBadge)
        badgeViewTitle = itemView.findViewById(R.id.formElementBadgeTitle)
    }

    override fun bind(s: FylFormItem, listener: FlyFormItemCallback?) {
        super.bind(s, listener)
        itemView.setOnClickListener {
            listener?.onItemClicked(s, this)
        }

        imgView?.visibility = View.GONE
        if (s.imageResId != null) {
            imgView?.setImageResource(s.imageResId ?: 0)
            imgView?.visibility = View.VISIBLE
        }
        if (s.value.isNotEmpty()) {
            //imgView?.nearalImage(null, s.value, null)
            imgView?.visibility = View.VISIBLE
        }
        if (s is FylFormItemNav) {
            if (s.badge == null) {
                badgeView?.visibility = View.GONE
            } else if (s.badge?.isEmpty() ?: true) {
                // dot
                badgeView?.visibility = View.VISIBLE
                badgeViewTitle?.visibility = View.GONE
                badgeView?.minHeight = dpToPx(10)
                badgeView?.minWidth = dpToPx(10)
            } else {
                badgeView?.visibility = View.VISIBLE
                badgeViewTitle?.visibility = View.VISIBLE
                badgeView?.minHeight = dpToPx(20)
                badgeView?.minWidth = dpToPx(20)
            }
        }
        listener?.onSetup(s, this)
    }
}

class FylFormLabelViewHolder(inflater: LayoutInflater, resource: Int, parent: ViewGroup) :
    FylFormViewHolder(inflater, resource, parent)  {

    init {
    }

    override fun bind(s: FylFormItem, listener: FlyFormItemCallback?) {
        super.bind(s, listener)
        titleView?.setOnClickListener {
            listener?.onItemClicked(s, this)
        }

        listener?.onSetup(s, this)
    }
}