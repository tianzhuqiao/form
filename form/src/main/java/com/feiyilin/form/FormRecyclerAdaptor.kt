package com.feiyilin.form

import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Color
import android.os.Handler
import android.text.Editable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.util.TypedValue
import android.view.*
import android.view.View.OnTouchListener
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat

interface FormItemCallback {
    fun onSetup(item: FormItem, viewHolder: RecyclerView.ViewHolder) {}
    fun onValueChanged(item: FormItem) {}
    fun onItemClicked(item: FormItem, viewHolder: RecyclerView.ViewHolder) {}
    fun onTitleImageClicked(item: FormItem) {}
    fun onStartReorder(item: FormItem, viewHolder: RecyclerView.ViewHolder): Boolean {
        return false
    }

    fun onMoveItem(src: Int, dest: Int): Boolean {
        return false
    }

    fun onSwipedAction(item: FormItem, action: FormSwipeAction, viewHolder: RecyclerView.ViewHolder) {}
    fun getMinItemHeight(item: FormItem) : Int { return 0 }
}

open class FormRecyclerAdaptor(
    private var settings: MutableList<FormItem>,
    private var listener: FormItemCallback? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var itemTouchHelper: ItemTouchHelper? = null
    private var recyclerView: RecyclerView? = null
    private var settingsVisible = mutableListOf<FormItem>()

    class ViewHolderItem(
        var type: Class<out FormItem>,
        var layoutId: Int,
        var viewHolderClass: Class<out FormViewHolder>
    )

    private var viewHolders: MutableList<ViewHolderItem> = mutableListOf()

    init {
        setSettings(settings)
        viewHolders = mutableListOf(
            ViewHolderItem(
                FormItemSection::class.java,
                R.layout.form_item_section,
                FormSectionViewHolder::class.java
            ),
            ViewHolderItem(
                FormItemText::class.java,
                R.layout.form_item_text,
                FormTextViewHolder::class.java
            ),
            ViewHolderItem(
                FormItemTextFloatingHint::class.java,
                R.layout.form_item_text_floating_hint,
                FormTextViewHolder::class.java
            ),
            ViewHolderItem(
                FormItemTextArea::class.java,
                R.layout.form_item_text,
                FormTextAreaViewHolder::class.java
            ),
            ViewHolderItem(
                FormItemTextAreaFloatingHint::class.java,
                R.layout.form_item_text_floating_hint,
                FormTextAreaViewHolder::class.java
            ),
            ViewHolderItem(
                FormItemAction::class.java,
                R.layout.form_item_action,
                FormActionViewHolder::class.java
            ),
            ViewHolderItem(
                FormItemSwitch::class.java,
                R.layout.from_item_switch,
                FormSwitchViewHolder::class.java
            ),
            ViewHolderItem(
                FormItemSwitchNative::class.java,
                R.layout.from_item_switch_native,
                FormSwitchNativeViewHolder::class.java
            ),
            ViewHolderItem(
                FormItemRadio::class.java,
                R.layout.form_item_radio,
                FormRadioViewHolder::class.java
            ),
            ViewHolderItem(
                FormItemRadioNative::class.java,
                R.layout.form_item_radio_native,
                FormRadioNativeViewHolder::class.java
            ),
            ViewHolderItem(
                FormItemNav::class.java,
                R.layout.form_item_nav,
                FormNavViewHolder::class.java
            ),
            ViewHolderItem(
                FormItemLabel::class.java,
                R.layout.form_item_label,
                FormLabelViewHolder::class.java
            ),
            ViewHolderItem(
                FormItemDate::class.java,
                R.layout.form_item_date,
                FormDateViewHolder::class.java
            ),
            ViewHolderItem(
                FormItemSelect::class.java,
                R.layout.form_item_choice,
                FormSelectViewHolder::class.java
            ),
            ViewHolderItem(
                FormItemChoice::class.java,
                R.layout.form_item_choice,
                FormChoiceViewHolder::class.java
            ),
            ViewHolderItem(
                FormItemPicker::class.java,
                R.layout.form_item_choice,
                FormPickerViewHolder::class.java
            ),
            ViewHolderItem(
                FormItemPickerInline::class.java,
                R.layout.form_item_picker,
                FormPickerInlineViewHolder::class.java
            )
        )
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
        val touchHelper =
            object : FormSwipeHelper() {

                override fun getFormItem(pos: Int): FormItem {
                    return settingsVisible[pos]
                }

                override fun updateItem(pos: Int) {
                    recyclerView.adapter?.notifyItemChanged(pos)
                }

                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    val src = viewHolder.adapterPosition
                    val des = target.adapterPosition
                    return onFormItemCallback.onMoveItem(src, des)
                }

                override fun onActionClicked(pos: Int, action: FormSwipeAction) {
                    val item = settingsVisible[pos]
                    recyclerView.findViewHolderForAdapterPosition(pos)?.let {
                        onFormItemCallback.onSwipedAction(item, action, it)
                    }
                    if (settingsVisible.indexOf(item) >= 0) {
                        updateItem(pos)
                    }
                }
            }

        itemTouchHelper = ItemTouchHelper(touchHelper)
        itemTouchHelper?.attachToRecyclerView(recyclerView)
        touchHelper.attachToRecyclerView(recyclerView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val item = viewHolders[viewType]
        val viewHolder = item.viewHolderClass.kotlin
        return viewHolder.constructors.first().call(inflater, item.layoutId, parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val s = settingsVisible[position]
        if (holder is FormViewHolder) {
            holder.bind(s, onFormItemCallback)
            listener?.onSetup(s, holder)
        }
    }

    override fun getItemCount(): Int {
        return settingsVisible.size
    }

    fun setSettings(settings: List<FormItem>) {
        this.settings = settings.toMutableList()
        this.settingsVisible.clear()
        var hideSection = false
        for (item in this.settings) {
            if (item is FormItemSection) {
                hideSection = item.hidden
            }
            if (item.hidden || hideSection) {
                continue
            }
            settingsVisible.add(item)
        }
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        val m = settingsVisible[position]
        return viewHolders.indexOfFirst { it.type == m::class.java }
    }

    fun registerViewHolder(
        type: Class<out FormItem>,
        layoutId: Int,
        viewHolderClass: Class<out FormViewHolder>
    ): Boolean {
        viewHolders.add(ViewHolderItem(type, layoutId, viewHolderClass))
        return true
    }

    fun updateItem(item: FormItem) {
        val index = settingsVisible.indexOf(item)
        if (index >= 0 && index < settingsVisible.size) {
            val activity = recyclerView?.context as? Activity
            activity?.let {
                it.runOnUiThread {
                    notifyItemChanged(index)
                }
            }
        }
    }

    fun updateRadioGroup(group: String, selected: String) {
        for (i in 0 until settingsVisible.size) {
            val item = settingsVisible[i]
            if (item is FormItemRadio && item.group == group) {
                item.isOn = (item.tag == selected)
                updateItem(item)
            } else if (item is FormItemRadioNative && item.group == group) {
                item.isOn = (item.tag == selected)
                updateItem(item)
            }
        }
    }

    private var onFormItemCallback = object : FormItemCallback {
        override fun onValueChanged(item: FormItem) {
            if (item is FormItemRadio) {
                updateRadioGroup(item.group, item.tag)
            }
            if (item is FormItemRadioNative) {
                updateRadioGroup(item.group, item.tag)
            }
            if (item is FormItemSelect) {
                //updateItem(item)
            }
            listener?.onValueChanged(item)
        }

        override fun onItemClicked(item: FormItem, viewHolder: RecyclerView.ViewHolder) {
            val index = settingsVisible.indexOf(item)

            Handler().postDelayed({
                val act = recyclerView?.context as? Activity
                //Do something after 100ms
                act?.runOnUiThread {
                    recyclerView?.smoothScrollToPosition(index)
                }
            }, 100)
            listener?.onItemClicked(item, viewHolder)
        }

        override fun onSetup(item: FormItem, viewHolder: RecyclerView.ViewHolder) {
            listener?.onSetup(item, viewHolder)
        }

        override fun onStartReorder(
            item: FormItem,
            viewHolder: RecyclerView.ViewHolder
        ): Boolean {
            if (listener?.onStartReorder(item, viewHolder) == true)
                return true
            itemTouchHelper?.startDrag(viewHolder)
            return true
        }

        override fun onMoveItem(src: Int, dest: Int): Boolean {
            super.onMoveItem(src, dest)
            if (listener?.onMoveItem(src, dest) == true)
                return true

            val orgSrc = settings.indexOf(settingsVisible[src])
            val orgDest = settings.indexOf(settingsVisible[dest])
            var item = settings.removeAt(orgSrc)
            settings.add(orgDest, item)
            item = settingsVisible.removeAt(src)
            settingsVisible.add(dest, item)

            notifyItemMoved(src, dest)
            return true
        }

        override fun onTitleImageClicked(item: FormItem) {
            listener?.onTitleImageClicked(item)
        }

        override fun onSwipedAction(
            item: FormItem,
            action: FormSwipeAction,
            viewHolder: RecyclerView.ViewHolder
        ) {
            super.onSwipedAction(item, action, viewHolder)
            listener?.onSwipedAction(item, action, viewHolder)
        }

        override fun getMinItemHeight(item: FormItem): Int {
            return listener?.getMinItemHeight(item) ?: 0
        }
    }

    fun itemByTag(tag: String) : FormItem? {
        return settings.firstOrNull { it.tag == tag }
    }

    fun hideSection(item: FormItemSection, hide: Boolean) {
        // show/hide all visible children of a section (not the section item itself)
        var idx = settingsVisible.indexOf(item)
        if (idx == -1) {
            return
        }
        val orgIdx = settings.indexOf(item)
        if (orgIdx == -1) {
            return
        }
        if (hide) {
            idx += 1
            while (settingsVisible.size > idx) {
                val child = settingsVisible[idx]
                if (child is FormItemSection) {
                    // start the next section
                    break
                }
                // hide the child
                settingsVisible.removeAt(idx)
                notifyItemRemoved(idx)
            }
        } else {
            var orgChildIdx = orgIdx + 1
            while (orgChildIdx < settings.size) {
                val child = settings[orgChildIdx]
                orgChildIdx += 1
                if (child is FormItemSection) {
                    // start the next section
                    break
                }
                if (child.hidden) {
                    continue
                }
                // show the child
                idx += 1
                settingsVisible.add(idx, child)
                notifyItemInserted(idx)
            }
        }
    }

    fun evaluateHidden(item: FormItem) : Boolean {
        val orgIdx = settings.indexOf(item)
        if (orgIdx == -1) {
            return false
        }

        var idx = settingsVisible.indexOf(item)
        if (item.hidden) {
            if (idx != -1) {
                if (item is FormItemSection) {
                    // if item is section, hide all its children
                    hideSection(item, true)
                }
                settingsVisible.removeAt(idx)
                notifyItemRemoved(idx)
            }
        } else {
            if (idx == -1) {
                // find the corresponding index in settingVisible
                idx = 0
                for (tmp in settings) {
                    if (item == tmp) {
                        break
                    }
                    if (!tmp.hidden) {
                        idx += 1
                    }
                }
                settingsVisible.add(idx, item)
                notifyItemInserted(idx)

                if (item is FormItemSection) {
                    // if item is section, show all its children
                    hideSection(item, false)
                }
            }
        }
        return true
    }
}

open class FormViewHolder(inflater: LayoutInflater, resource: Int, parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(resource, parent, false)) {

    var titleView: TextView? = null
    var subtitleView: TextView? = null
    var titleImageView: ImageView? = null
    var reorderView: ImageView? = null
    var mainView: View? = null

    init {
        titleView = itemView.findViewById(R.id.formElementTitle)
        subtitleView = itemView.findViewById(R.id.formElementSubTitle)
        titleImageView = itemView.findViewById(R.id.formElementTitleImage)
        reorderView = itemView.findViewById(R.id.formElementReorder)
        mainView = itemView.findViewById(R.id.formElementMainLayout)
    }

    open fun bind(s: FormItem, listener: FormItemCallback?) {
        var minHeight = s.minHeight
         if (minHeight == 0) {
             minHeight = listener?.getMinItemHeight(s) ?: 0
         }
        if (minHeight > 0) {
            mainView?.minimumHeight = dpToPx(minHeight)
        }
        itemView.setOnClickListener {
            listener?.onItemClicked(s, this)
        }
        titleView?.text = s.title
        if (s.title.isNotEmpty()) {
            titleView?.visibility = View.VISIBLE
        } else {
            titleView?.visibility = View.GONE
        }
        if (s.required) {
            val styledString = SpannableString(s.title + "*")
            styledString.setSpan(
                ForegroundColorSpan(Color.RED),
                styledString.length - 1,
                styledString.length,
                0
            )
            titleView?.text = styledString
        }

        s.titleColor?.let {
            titleView?.setTextColor(it)
        } ?: run {
            titleView?.setTextColor(
                ContextCompat.getColor(
                    itemView.context,
                    R.color.colorFormTitle
                )
            )
        }

        subtitleView?.text = s.subTitle
        if (s.subTitle.isNotEmpty()) {
            subtitleView?.visibility = View.VISIBLE
        } else {
            subtitleView?.visibility = View.GONE
        }
        s.subTitleColor?.let {
            subtitleView?.setTextColor(it)
        } ?: run {
            subtitleView?.setTextColor(
                ContextCompat.getColor(
                    itemView.context,
                    R.color.colorFormSubtitle
                )
            )
        }

        titleImageView?.layoutParams?.height = dpToPx(s.iconSize.height)
        titleImageView?.layoutParams?.width = dpToPx(s.iconSize.width)
        titleImageView?.setImageDrawable(s.iconTitle)
        if (s.iconTitle != null) {
            titleImageView?.visibility = View.VISIBLE
            titleImageView?.setOnClickListener {
                listener?.onTitleImageClicked(s)
            }
        } else {
            titleImageView?.visibility = View.GONE
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
            dp.toFloat(),
            Resources.getSystem().displayMetrics
        ).toInt()
    }
}

open class FormBaseTextViewHolder(inflater: LayoutInflater, resource: Int, parent: ViewGroup) :
    FormViewHolder(inflater, resource, parent) {
    var valueView: EditText? = null
    var listener: FormItemCallback? = null
    var item: FormItemText? = null
    var hintView: TextInputLayout? = null

    init {
        valueView = itemView.findViewById(R.id.formElementValue)
        hintView = itemView.findViewById(R.id.formElementValueHint)
        reorderView = itemView.findViewById(R.id.formElementReorder)
        valueView?.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                item?.let {
                    if (it.value != s.toString()) {
                        it.value = s.toString()
                        listener?.onValueChanged(it)
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
        }
        )
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

    override fun bind(s: FormItem, listener: FormItemCallback?) {
        super.bind(s, listener)

        this.listener = listener
        if (s is FormItemText) {
            this.item = s
            itemView.setOnClickListener {
                listener?.onItemClicked(s, this)
                valueView?.requestFocus()
                if (!s.readOnly) {
                    val imm =
                        itemView.context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
                    if (imm is InputMethodManager) {
                        imm.showSoftInput(valueView, InputMethodManager.SHOW_IMPLICIT)
                    }
                }
            }
            valueView?.gravity = s.gravity
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
            s.valueColor?.let {
                valueView?.setTextColor(it)
            } ?: run {
                valueView?.setTextColor(
                    ContextCompat.getColor(
                        itemView.context,
                        R.color.colorFormText
                    )
                )
            }

            var hint = s.hint
            if (!s.readOnly && s.hint.isEmpty()) {
                hint = "Enter ${s.title} here"
            }
            if (hintView != null) {
                hintView?.hint = hint
                valueView?.hint = ""
            } else {
                valueView?.hint = hint
            }
            s.hintColor?.let {
                valueView?.setHintTextColor(it)
                hintView?.hintTextColor = ColorStateList.valueOf(it)
            } ?: run {

                valueView?.setHintTextColor(
                    ContextCompat.getColor(
                        itemView.context,
                        R.color.colorFormHint
                    )
                )
                hintView?.hintTextColor = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        itemView.context,
                        R.color.colorFormHint
                    )
                )
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
}

open class FormTextViewHolder(inflater: LayoutInflater, resource: Int, parent: ViewGroup) :
    FormBaseTextViewHolder(inflater, resource, parent) {
}

open class FormTextGroupViewHolder(inflater: LayoutInflater, resource: Int, parent: ViewGroup) :
    FormBaseTextViewHolder(inflater, resource, parent) {

    override fun bind(s: FormItem, listener: FormItemCallback?) {
        super.bind(s, listener)

        valueView?.gravity = Gravity.START
        valueView?.textAlignment = View.TEXT_ALIGNMENT_TEXT_START

    }
}

open class FormTextAreaViewHolder(inflater: LayoutInflater, resource: Int, parent: ViewGroup) :
    FormBaseTextViewHolder(inflater, resource, parent) {

    init {
        valueView?.gravity = Gravity.START
    }

    override fun bind(s: FormItem, listener: FormItemCallback?) {
        super.bind(s, listener)
        if (s is FormItemTextArea) {
            valueView?.minLines = s.minLines
            valueView?.maxLines = s.maxLines
        }
    }
}

open class FormSectionViewHolder(inflater: LayoutInflater, resource: Int, parent: ViewGroup) :
    FormViewHolder(inflater, resource, parent) {

    override fun bind(s: FormItem, listener: FormItemCallback?) {
        super.bind(s, listener)

        titleView?.text = s.title.toUpperCase()
    }
}

open class FormActionViewHolder(inflater: LayoutInflater, resource: Int, parent: ViewGroup) :
    FormViewHolder(inflater, resource, parent) {
    private var leftSpace: Space? = null
    private var rightSpace: Space? = null

    init {
        leftSpace = itemView.findViewById(R.id.formSapceLeft)
        rightSpace = itemView.findViewById(R.id.formSapceRight)
    }

    override fun bind(s: FormItem, listener: FormItemCallback?) {
        super.bind(s, listener)
        if (s is FormItemAction) {
            when (s.alignment) {
                Gravity.CENTER -> {
                    leftSpace?.visibility = View.VISIBLE
                    rightSpace?.visibility = View.VISIBLE
                }
                Gravity.START, Gravity.LEFT -> {
                    leftSpace?.visibility = View.GONE
                    rightSpace?.visibility = View.VISIBLE
                }
                Gravity.END, Gravity.RIGHT -> {
                    leftSpace?.visibility = View.VISIBLE
                    rightSpace?.visibility = View.GONE
                }
            }
        }
    }
}

open class FormSwitchNativeViewHolder(inflater: LayoutInflater, resource: Int, parent: ViewGroup) :
    FormViewHolder(inflater, resource, parent) {
    private var switchView: Switch? = null

    init {
        switchView = itemView.findViewById(R.id.formElementSwitch)
    }

    override fun bind(s: FormItem, listener: FormItemCallback?) {
        super.bind(s, listener)
        if (s is FormItemSwitchNative) {
            itemView.setOnClickListener {
                listener?.onItemClicked(s, this)
                s.isOn = !s.isOn
                switchView?.isChecked = s.isOn
                listener?.onValueChanged(s)
            }

            switchView?.setOnClickListener {
                s.isOn = switchView?.isChecked ?: (!s.isOn)
                listener?.onValueChanged(s)
            }
            switchView?.isChecked = s.isOn
        }
    }
}

open class FormSwitchViewHolder(inflater: LayoutInflater, resource: Int, parent: ViewGroup) :
    FormViewHolder(inflater, resource, parent) {
    private var switchView: ImageView? = null
    private var item: FormItemRadio? = null

    init {
        switchView = itemView.findViewById(R.id.formElementSwitch)
    }

    override fun bind(s: FormItem, listener: FormItemCallback?) {
        super.bind(s, listener)

        if (s is FormItemSwitch) {
            itemView.setOnClickListener {
                listener?.onItemClicked(s, this)
                s.isOn = !s.isOn
                setSwitchImage(s.isOn)
                listener?.onValueChanged(s)
            }
            switchView?.layoutParams?.height = dpToPx(s.iconSize.height)
            switchView?.layoutParams?.width = dpToPx(s.iconSize.width)
            setSwitchImage(s.isOn)
        }
    }

    fun setSwitchImage(checked: Boolean) {
        if (checked) {
            if (item?.iconOn != null) {
                switchView?.setImageDrawable(item?.iconOn)
            } else {
                switchView?.setImageResource(R.drawable.ic_form_toggle_on)
            }
        } else {
            if (item?.iconOff != null) {
                switchView?.setImageDrawable(item?.iconOff)
            } else {
                switchView?.setImageResource(R.drawable.ic_form_toggle_off)
            }
        }
    }
}

open class FormRadioViewHolder(inflater: LayoutInflater, resource: Int, parent: ViewGroup) :
    FormViewHolder(inflater, resource, parent) {
    private var radioView: ImageView? = null
    private var item: FormItemRadio? = null

    init {
        radioView = itemView.findViewById(R.id.formElementRadio)
    }

    override fun bind(s: FormItem, listener: FormItemCallback?) {
        super.bind(s, listener)
        itemView.setOnClickListener {
            if (s is FormItemRadio) {
                listener?.onItemClicked(s, this)
                if (s.isOn) {
                    return@setOnClickListener
                }
                s.isOn = !s.isOn
                listener?.onValueChanged(s)
            }
        }
        if (s is FormItemRadio) {
            item = s
            radioView?.layoutParams?.height = dpToPx(s.iconSize.height)
            radioView?.layoutParams?.width = dpToPx(s.iconSize.width)
            setRadioImage(s.isOn)
        }
    }

    fun setRadioImage(checked: Boolean) {
        if (checked) {
            if (item?.iconOn != null) {
                radioView?.setImageDrawable(item?.iconOn)
            } else {
                radioView?.setImageResource(R.drawable.ic_form_radio_on)
            }
        } else {
            if (item?.iconOff != null) {
                radioView?.setImageDrawable(item?.iconOff)
            } else {
                radioView?.setImageResource(R.drawable.ic_form_radio_off)
            }
        }
    }
}

open class FormRadioNativeViewHolder(inflater: LayoutInflater, resource: Int, parent: ViewGroup) :
    FormViewHolder(inflater, resource, parent) {
    private var radioView: RadioButton? = null

    init {
        radioView = itemView.findViewById(R.id.formElementRadio)
    }

    override fun bind(s: FormItem, listener: FormItemCallback?) {
        super.bind(s, listener)
        itemView.setOnClickListener {
            if (s is FormItemRadioNative) {
                listener?.onItemClicked(s, this)
                if (s.isOn) {
                    return@setOnClickListener
                }
                s.isOn = !s.isOn
                listener?.onValueChanged(s)
            }
        }
        if (s is FormItemRadioNative) {
            radioView?.isChecked = s.isOn
            radioView?.setOnCheckedChangeListener { _, checked ->
                s.isOn = checked
                listener?.onValueChanged(s)
            }
        }
    }
}

open class FormNavViewHolder(inflater: LayoutInflater, resource: Int, parent: ViewGroup) :
    FormViewHolder(inflater, resource, parent) {
    var badgeView: ConstraintLayout? = null
    var badgeViewTitle: TextView? = null
    var titleImageWrap: View? = null

    init {
        badgeView = itemView.findViewById(R.id.formElementBadge)
        badgeViewTitle = itemView.findViewById(R.id.formElementBadgeTitle)
        titleImageWrap = itemView.findViewById(R.id.formElementTitleImageWrap)
    }

    override fun bind(s: FormItem, listener: FormItemCallback?) {
        super.bind(s, listener)
        if (s is FormItemNav) {
            if (s.badge == null) {
                badgeView?.visibility = View.GONE
            } else if (s.badge?.isEmpty() ?: true) {
                // dot
                badgeView?.visibility = View.VISIBLE
                badgeViewTitle?.visibility = View.GONE
                badgeViewTitle?.text = s.badge
                badgeView?.minHeight = dpToPx(10)
                badgeView?.minWidth = dpToPx(10)
            } else {
                badgeView?.visibility = View.VISIBLE
                badgeViewTitle?.visibility = View.VISIBLE
                badgeViewTitle?.text = s.badge
                badgeView?.minHeight = dpToPx(20)
                badgeView?.minWidth = dpToPx(20)
            }
            val param = badgeView?.layoutParams as? ViewGroup.MarginLayoutParams
            if (titleImageView?.visibility == View.VISIBLE) {
                param?.leftMargin = dpToPx(-10)
            } else {
                param?.leftMargin = dpToPx(2)
            }
            if (titleImageView?.visibility == View.GONE && badgeView?.visibility == View.GONE) {
                titleImageWrap?.visibility = View.GONE
            } else {
                titleImageWrap?.visibility = View.VISIBLE
            }
        }
    }
}

open class FormLabelViewHolder(inflater: LayoutInflater, resource: Int, parent: ViewGroup) :
    FormViewHolder(inflater, resource, parent) {
}

open class FormDateViewHolder(inflater: LayoutInflater, resource: Int, parent: ViewGroup) :
    FormViewHolder(inflater, resource, parent) {

    var dateView: TextView? = null
    var datePickerView: DatePicker? = null
    var timeView: TextView? = null
    var timePickerView: TimePicker? = null

    init {
        dateView = itemView.findViewById(R.id.formElementDate)
        datePickerView = itemView.findViewById(R.id.formElementDatePicker)
        timeView = itemView.findViewById(R.id.formElementTime)
        timePickerView = itemView.findViewById(R.id.formElementTimePicker)
    }

    override fun bind(s: FormItem, listener: FormItemCallback?) {
        super.bind(s, listener)
        if (s is FormItemDate) {
            dateView?.setOnClickListener {
                if (datePickerView?.visibility == View.GONE)
                    datePickerView?.visibility = View.VISIBLE
                else
                    datePickerView?.visibility = View.GONE
                timePickerView?.visibility = View.GONE
                listener?.onItemClicked(s, this)
            }
            timeView?.setOnClickListener {
                datePickerView?.visibility = View.GONE
                if (timePickerView?.visibility == View.GONE) {
                    timePickerView?.visibility = View.VISIBLE
                } else {
                    timePickerView?.visibility = View.GONE
                }
                listener?.onItemClicked(s, this)
            }

            timePickerView?.setOnTimeChangedListener { _, hour, minute ->
                s.hour = hour
                s.minute = minute
                timeView?.text = SimpleDateFormat(s.timeFormat).format(s.date)
                listener?.onValueChanged(s)
            }
            dateView?.text = SimpleDateFormat(s.dateFormat).format(s.date)
            timeView?.text = SimpleDateFormat(s.timeFormat).format(s.date)

            s.dateColor?.let {
                dateView?.setTextColor(it)
            } ?: run {
                dateView?.setTextColor(
                    ContextCompat.getColor(
                        itemView.context,
                        R.color.colorFormText
                    )
                )
            }

            s.timeColor?.let {
                timeView?.setTextColor(it)
            } ?: run {
                timeView?.setTextColor(
                    ContextCompat.getColor(
                        itemView.context,
                        R.color.colorFormText
                    )
                )
            }

            if (s.dateOnly) {
                timeView?.visibility = View.GONE
                timePickerView?.visibility = View.GONE
            } else {
                timeView?.visibility = View.VISIBLE
                timePickerView?.hour = s.hour
                timePickerView?.minute = s.minute
            }
            if (s.timeOnly) {
                dateView?.visibility = View.GONE
                datePickerView?.visibility = View.GONE
            } else {
                dateView?.visibility = View.VISIBLE
                datePickerView?.init(s.year, s.month, s.day) { _, year, month, dayOfMonth ->
                    s.year = year
                    s.month = month
                    s.day = dayOfMonth
                    dateView?.text = SimpleDateFormat(s.dateFormat).format(s.date)
                    listener?.onValueChanged(s)
                }
            }
        }
    }
}


open class FormSelectViewHolder(inflater: LayoutInflater, resource: Int, parent: ViewGroup) :
    FormViewHolder(inflater, resource, parent) {

    var valueView: TextView? = null
    var item: FormItemSelect? = null
    var listener: FormItemCallback? = null
    init {
        valueView = itemView.findViewById(R.id.formElementValue)
    }

    override fun bind(s: FormItem, listener: FormItemCallback?) {
        super.bind(s, listener)
        this.listener = listener
        itemView.setOnClickListener {
            showAlertWithChoice()
            listener?.onItemClicked(s, this)
        }
        if (s is FormItemSelect) {
            valueView?.text = s.value
            item = s
        }
    }

    open fun showAlertWithChoice() {
        // setup the alert builder
        item?.let {
            val builder = AlertDialog.Builder(itemView.context)
            builder.setTitle(it.selectorTitle)
            builder.setItems(it.options.toTypedArray(), { dialog, item ->
                it.value = it.options[item]
                valueView?.text = it.value
                this.listener?.onValueChanged(it)
            })
            val dialog = builder.create()
            dialog.show()
        }
    }
}

open class FormChoiceViewHolder(inflater: LayoutInflater, resource: Int, parent: ViewGroup) :
    FormSelectViewHolder(inflater, resource, parent) {

    override fun showAlertWithChoice() {
        // setup the alert builder
        (item as? FormItemChoice)?.let {
            val checkedItem = it.options.indexOf(it.value)
            val builder = AlertDialog.Builder(itemView.context)
                .setTitle(it.selectorTitle)
                .setSingleChoiceItems(it.options.toTypedArray(), checkedItem, null)
                .setPositiveButton(it.yesButtonTitle) { dialog, _ ->
                    (dialog as? AlertDialog)?.listView?.let { lw ->
                        it.value = it.options[lw.getCheckedItemPosition()]
                        valueView?.text = it.value
                        this.listener?.onValueChanged(it)
                    }
                }
                .setNegativeButton(it.noButtonTitle) { _, _ ->
                }

            val dialog = builder.create()
            dialog.show()
        }
    }
}

open class FormPickerViewHolder(inflater: LayoutInflater, resource: Int, parent: ViewGroup) :
    FormChoiceViewHolder(inflater, resource, parent) {

    override fun showAlertWithChoice() {
        // setup the alert builder
        (item as? FormItemChoice)?.let {
            val checkedItem = it.options.indexOf(it.value)
            val view = LayoutInflater.from(itemView.context).inflate(R.layout.form_picker, null)
            val picker = view?.findViewById<NumberPicker>(R.id.formElementNumberPicker)
            picker?.minValue = 0
            picker?.maxValue = it.options.size - 1
            picker?.displayedValues = it.options.toTypedArray()
            picker?.value = checkedItem

            val builder = AlertDialog.Builder(itemView.context)
                .setTitle(it.selectorTitle)
                .setView(view)
                .setPositiveButton(it.yesButtonTitle) { dialog, _ ->
                    picker?.value?.let { item ->
                        it.value = it.options[item]
                        valueView?.text = it.value
                        this.listener?.onValueChanged(it)
                    }
                }
                .setNegativeButton(it.noButtonTitle) { _, _ ->
                }

            val dialog = builder.create()
            dialog.show()
        }
    }
}


open class FormPickerInlineViewHolder(inflater: LayoutInflater, resource: Int, parent: ViewGroup) :
    FormViewHolder(inflater, resource, parent) {

    var valueView: TextView? = null
    var pickerView: NumberPicker? = null

    init {
        valueView = itemView.findViewById(R.id.formElementValue)
        pickerView = itemView.findViewById(R.id.formElementNumberPicker)
    }

    override fun bind(s: FormItem, listener: FormItemCallback?) {
        super.bind(s, listener)
        if (s is FormItemPickerInline) {
            itemView.setOnClickListener {
                if (pickerView?.visibility == View.GONE)
                    pickerView?.visibility = View.VISIBLE
                else
                    pickerView?.visibility = View.GONE
                listener?.onItemClicked(s, this)
            }
            valueView?.text = s.value
            pickerView?.minValue = 0
            pickerView?.maxValue = s.options.size - 1
            pickerView?.displayedValues = s.options.toTypedArray()
            pickerView?.value = s.options.indexOf(s.value)
            pickerView?.setOnValueChangedListener { _, _, new ->
                if (s.value != s.options[new]) {
                    s.value = s.options[new]
                    valueView?.text = s.value
                    listener?.onValueChanged(s)
                }
            }
        }
    }
}
