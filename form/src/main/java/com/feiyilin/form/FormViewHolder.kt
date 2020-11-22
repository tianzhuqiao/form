package com.feiyilin.form

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Color
import android.os.Handler
import android.text.Editable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.text.style.ForegroundColorSpan
import android.util.TypedValue
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SwitchCompat
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.*

open class FormViewHolder(inflater: LayoutInflater, resource: Int, parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(resource, parent, false)) {

    var titleView: TextView? = null
    var subtitleView: TextView? = null
    var titleImageView: ImageView? = null
    var badgeView: ConstraintLayout? = null
    var badgeViewTitle: TextView? = null
    var titleImageWrap: View? = null
    var reorderView: ImageView? = null
    var mainView: View? = null
    var dividerView: View? = null
    var item: FormItem? = null
    var listener: FormItemCallback? = null

    init {
        titleView = itemView.findViewById(R.id.formElementTitle)
        subtitleView = itemView.findViewById(R.id.formElementSubTitle)
        titleImageView = itemView.findViewById(R.id.formElementTitleImage)
        badgeView = itemView.findViewById(R.id.formElementBadge)
        badgeViewTitle = itemView.findViewById(R.id.formElementBadgeTitle)
        titleImageWrap = itemView.findViewById(R.id.formElementTitleImageWrap)
        reorderView = itemView.findViewById(R.id.formElementReorder)
        mainView = itemView.findViewById(R.id.formElementMainLayout)
        dividerView = itemView.findViewById(R.id.formElementDivider)
    }

    open fun bind(s: FormItem, listener: FormItemCallback?) {
        this.item = s
        this.listener = listener
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

        updateIcon()
        reorderView?.visibility = if (s.draggable) View.VISIBLE else View.GONE
        reorderView?.setOnTouchListener { _, event ->
            if (event.actionMasked == MotionEvent.ACTION_DOWN)
                listener?.onStartReorder(s, this)
            false
        }

        updateSeparator()
    }

    fun dpToPx(dp: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            Resources.getSystem().displayMetrics
        ).toInt()
    }

    fun updateSeparator() {
        item?.let {item ->
            val separator = item.separator ?: listener?.getSeparator(item)
            val param = dividerView?.layoutParams as? ViewGroup.MarginLayoutParams
            param?.leftMargin = dpToPx(0)
            when (separator) {
                FormItem.Separator.NONE -> {
                    dividerView?.visibility = View.GONE
                }
                FormItem.Separator.IGNORE_ICON -> {
                    dividerView?.visibility = View.VISIBLE
                    if (titleImageView?.visibility == View.VISIBLE)
                        param?.leftMargin = dpToPx(16 + item.iconSize.width + 8)
                }
                else -> {
                    dividerView?.visibility = View.VISIBLE
                    param?.leftMargin = dpToPx(0)
                }
            }
            dividerView?.requestLayout()
        }
    }

    fun updateIcon() {
        item?.let {
            titleImageView?.layoutParams?.height = dpToPx(it.iconSize.height)
            titleImageView?.layoutParams?.width = dpToPx(it.iconSize.width)
            titleImageView?.setImageDrawable(it.iconTitle)
            if (it.iconTitle != null) {
                titleImageWrap?.visibility = View.VISIBLE
                titleImageView?.visibility = View.VISIBLE
                titleImageView?.setOnClickListener { _ ->
                    listener?.onTitleImageClicked(it, this)
                }
            } else {
                titleImageView?.visibility = View.GONE
            }

            if (it.badge == null) {
                badgeView?.visibility = View.GONE
            } else if (it.badge?.isEmpty() != false) {
                // dot
                badgeView?.visibility = View.VISIBLE
                badgeViewTitle?.visibility = View.GONE
                badgeViewTitle?.text = it.badge
                badgeView?.minHeight = dpToPx(10)
                badgeView?.minWidth = dpToPx(10)
            } else {
                badgeView?.visibility = View.VISIBLE
                badgeViewTitle?.visibility = View.VISIBLE
                badgeViewTitle?.text = it.badge
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

open class FormBaseTextViewHolder(inflater: LayoutInflater, resource: Int, parent: ViewGroup) :
    FormViewHolder(inflater, resource, parent) {
    var valueView: EditText? = null
    var hintView: TextInputLayout? = null

    init {
        valueView = itemView.findViewById(R.id.formElementValue)
        hintView = itemView.findViewById(R.id.formElementValueHint)
        reorderView = itemView.findViewById(R.id.formElementReorder)
        valueView?.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                (item as? FormItemText)?.let {
                    if (it.value != s.toString()) {
                        it.value = s.toString()
                        listener?.onValueChanged(it)
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {
                (item as? FormItemText)?.let {
                    if (it.clearIcon) {
                        val clearIcon = if (s?.isNotEmpty() == true) R.drawable.ic_form_clear else 0
                        valueView?.setCompoundDrawablesWithIntrinsicBounds(0, 0, clearIcon, 0)
                    } else {
                        valueView?.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                    }
                }
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
        valueView?.setOnEditorActionListener { v, actionId, _ ->
            item?.let {
                listener?.onEditAction(it, actionId, this)
                return@setOnEditorActionListener true
            }
            false
        }
    }

    override fun bind(s: FormItem, listener: FormItemCallback?) {
        super.bind(s, listener)

        if (s is FormItemText) {
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
            valueView?.setOnTouchListener(object : View.OnTouchListener {
                override fun onTouch(v: View, event: MotionEvent): Boolean {
                    if (s.readOnly) {
                        return true
                    }
                    if (s.clearIcon && event.action == MotionEvent.ACTION_UP) {
                        if (event.rawX >= ((valueView?.right ?: 0) - (valueView?.compoundPaddingRight ?: 0))) {
                            valueView?.setText("")
                            return true
                        }
                    }
                    return false
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

            valueView?.imeOptions = s.imeOptions
            valueView?.inputType = s.inputType


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

open class FormPasswordViewHolder(inflater: LayoutInflater, resource: Int, parent: ViewGroup) :
    FormTextViewHolder(inflater, resource, parent) {
    override fun bind(s: FormItem, listener: FormItemCallback?) {
        super.bind(s, listener)
        if (s is FormItemPassword) {
            if (s.shownPassword) {
                valueView?.transformationMethod = HideReturnsTransformationMethod.getInstance()
            } else {
                valueView?.transformationMethod = PasswordTransformationMethod.getInstance()
            }
        }
    }
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
    var imageArrowUp: ImageView? = null
    init {
        imageArrowUp = itemView.findViewById(R.id.formElementArrowUp)
    }
    override fun bind(s: FormItem, listener: FormItemCallback?) {
        super.bind(s, listener)
        itemView.setOnClickListener {
            listener?.onItemClicked(s, this)

            updateCollapse()
        }
        titleView?.text = s.title.toUpperCase(Locale.getDefault())
        updateCollapse()
    }
    fun updateCollapse() {
        (item as? FormItemSection)?.let {
            if (it.enableCollapse) {
                imageArrowUp?.visibility = View.VISIBLE
                if (!it.collapsed) {
                    imageArrowUp?.animate()?.rotation(180F)
                } else {
                    imageArrowUp?.animate()?.rotation(0F)
                }
            } else {
                imageArrowUp?.visibility = View.GONE
            }
        }
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

open class FormSwitchViewHolder(inflater: LayoutInflater, resource: Int, parent: ViewGroup) :
    FormViewHolder(inflater, resource, parent) {
    protected var switchView: SwitchCompat? = null

    init {
        switchView = itemView.findViewById(R.id.formElementSwitch)
    }

    override fun bind(s: FormItem, listener: FormItemCallback?) {
        super.bind(s, listener)
        if (s is FormItemSwitch) {
            itemView.setOnClickListener {
                listener?.onItemClicked(s, this)
                s.isOn = !s.isOn
                switchView?.isChecked = s.isOn
                listener?.onValueChanged(s)
            }

            switchView?.setOnClickListener(null)
            switchView?.isChecked = s.isOn
            switchView?.setOnClickListener {
                s.isOn = switchView?.isChecked ?: (!s.isOn)
                listener?.onValueChanged(s)
            }

        }
    }
}

open class FormSwitchCustomViewHolder(inflater: LayoutInflater, resource: Int, parent: ViewGroup) :
    FormViewHolder(inflater, resource, parent) {
    private var switchView: ImageView? = null

    init {
        switchView = itemView.findViewById(R.id.formElementSwitch)
    }

    override fun bind(s: FormItem, listener: FormItemCallback?) {
        super.bind(s, listener)

        if (s is FormItemSwitchCustom) {
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
        (item as? FormItemSwitchCustom)?.let { item ->
            if (checked) {
                if (item.iconOn != null) {
                    switchView?.setImageDrawable(item.iconOn)
                } else {
                    switchView?.setImageResource(R.drawable.ic_form_toggle_on)
                }
            } else {
                if (item.iconOff != null) {
                    switchView?.setImageDrawable(item.iconOff)
                } else {
                    switchView?.setImageResource(R.drawable.ic_form_toggle_off)
                }
            }
        }
    }
}

open class FormRadioCustomViewHolder(inflater: LayoutInflater, resource: Int, parent: ViewGroup) :
    FormViewHolder(inflater, resource, parent) {
    private var radioView: ImageView? = null

    init {
        radioView = itemView.findViewById(R.id.formElementRadio)
    }

    override fun bind(s: FormItem, listener: FormItemCallback?) {
        super.bind(s, listener)
        itemView.setOnClickListener {
            if (s is FormItemRadioCustom) {
                listener?.onItemClicked(s, this)
                if (s.isOn) {
                    return@setOnClickListener
                }
                s.isOn = !s.isOn
                listener?.onValueChanged(s)
            }
        }
        if (s is FormItemRadioCustom) {
            radioView?.layoutParams?.height = dpToPx(s.iconSize.height)
            radioView?.layoutParams?.width = dpToPx(s.iconSize.width)
            setRadioImage(s.isOn)
        }
    }

    fun setRadioImage(checked: Boolean) {
        (item as? FormItemRadioCustom)?.let { item ->
            if (checked) {
                if (item.iconOn != null) {
                    radioView?.setImageDrawable(item.iconOn)
                } else {
                    radioView?.setImageResource(R.drawable.ic_form_radio_on)
                }
            } else {
                if (item.iconOff != null) {
                    radioView?.setImageDrawable(item.iconOff)
                } else {
                    radioView?.setImageResource(R.drawable.ic_form_radio_off)
                }
            }
        }
    }
}

open class FormRadioViewHolder(inflater: LayoutInflater, resource: Int, parent: ViewGroup) :
    FormViewHolder(inflater, resource, parent) {
    private var radioView: RadioButton? = null

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
            radioView?.setOnCheckedChangeListener(null)
            radioView?.isChecked = s.isOn
            radioView?.setOnCheckedChangeListener { _, checked ->
                s.isOn = checked
                listener?.onValueChanged(s)
            }
        }
    }
}

open class FormCheckViewHolder(inflater: LayoutInflater, resource: Int, parent: ViewGroup) :
    FormViewHolder(inflater, resource, parent) {
    protected var checkView: CheckBox? = null

    init {
        checkView = itemView.findViewById(R.id.formElementCheck)
    }

    override fun bind(s: FormItem, listener: FormItemCallback?) {
        super.bind(s, listener)
        if (s is FormItemCheck) {
            itemView.setOnClickListener {
                listener?.onItemClicked(s, this)
                s.isOn = !s.isOn
                checkView?.isChecked = s.isOn
                listener?.onValueChanged(s)
            }

            checkView?.setOnClickListener(null)
            checkView?.isChecked = s.isOn
            checkView?.setOnClickListener {
                s.isOn = checkView?.isChecked ?: (!s.isOn)
                listener?.onValueChanged(s)
            }
        }
    }
}

open class FormCheckCustomViewHolder(inflater: LayoutInflater, resource: Int, parent: ViewGroup) :
    FormViewHolder(inflater, resource, parent) {
    var imageView: ImageView? = null

    init {
        imageView = itemView.findViewById(R.id.formElementCheck)
    }

    override fun bind(s: FormItem, listener: FormItemCallback?) {
        super.bind(s, listener)

        if (s is FormItemCheckCustom) {
            itemView.setOnClickListener {
                listener?.onItemClicked(s, this)
                s.isOn = !s.isOn
                setImage(s.isOn)
                listener?.onValueChanged(s)
            }
            imageView?.layoutParams?.height = dpToPx(s.iconSize.height)
            imageView?.layoutParams?.width = dpToPx(s.iconSize.width)
            setImage(s.isOn)
        }
    }

    fun setImage(checked: Boolean) {
        (item as? FormItemCheckCustom)?.let { item ->
            if (checked) {
                if (item.iconOn != null) {
                    imageView?.setImageDrawable(item.iconOn)
                } else {
                    imageView?.setImageResource(R.drawable.ic_form_check_on)
                }
            } else {
                if (item.iconOff != null) {
                    imageView?.setImageDrawable(item.iconOff)
                } else {
                    imageView?.setImageResource(R.drawable.ic_form_check_off)
                }
            }
        }
    }
}

open class FormNavViewHolder(inflater: LayoutInflater, resource: Int, parent: ViewGroup) :
    FormViewHolder(inflater, resource, parent) {
    var valueView: TextView? = null

    init {
        valueView = itemView.findViewById(R.id.formElementValue)
    }

    override fun bind(s: FormItem, listener: FormItemCallback?) {
        super.bind(s, listener)
        if (s is FormItemNav) {
            valueView?.text = s.value
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
    var valueColor: Int = ContextCompat.getColor(itemView.context, R.color.colorFormText)

    init {
        dateView = itemView.findViewById(R.id.formElementDate)
        datePickerView = itemView.findViewById(R.id.formElementDatePicker)
        timeView = itemView.findViewById(R.id.formElementTime)
        timePickerView = itemView.findViewById(R.id.formElementTimePicker)

        dateView?.setOnClickListener {
            showDateTimePicker(true)
        }
        timeView?.setOnClickListener {
            showDateTimePicker(false)
        }
    }

    override fun bind(s: FormItem, listener: FormItemCallback?) {
        super.bind(s, listener)
        if (s is FormItemDate) {

            dateView?.text = SimpleDateFormat(s.dateFormat).format(s.date)
            timeView?.text = SimpleDateFormat(s.timeFormat).format(s.date)

            s.dateColor?.let {
                dateView?.setTextColor(it)
            } ?: run {
                dateView?.setTextColor(valueColor)
            }

            s.timeColor?.let {
                timeView?.setTextColor(it)
            } ?: run {
                timeView?.setTextColor(valueColor)
            }

            if (s.dateOnly) {
                timeView?.visibility = View.GONE
                timePickerView?.visibility = View.GONE
            } else {
                timeView?.visibility = View.VISIBLE
                timePickerView?.setOnTimeChangedListener(null)
                timePickerView?.hour = s.hour
                timePickerView?.minute = s.minute
                timePickerView?.setOnTimeChangedListener { _, hour, minute ->
                    s.hour = hour
                    s.minute = minute
                    timeView?.text = SimpleDateFormat(s.timeFormat).format(s.date)
                    listener?.onValueChanged(s)
                }
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

    fun showDateTimePicker(showDate: Boolean) {
        item?.let {
            var showView: View? = datePickerView
            var hideView: View? = timePickerView
            if (!showDate) {
                showView = timePickerView
                hideView = datePickerView
            }
            if (showView?.visibility == View.GONE) {
                showView.visibility = View.VISIBLE
            } else {
                showView?.visibility = View.GONE
            }
            hideView?.visibility = View.GONE
            listener?.onItemClicked(it, this)
        }
    }
}

open class FormSelectViewHolder(inflater: LayoutInflater, resource: Int, parent: ViewGroup) :
    FormViewHolder(inflater, resource, parent) {

    var valueView: TextView? = null
    init {
        valueView = itemView.findViewById(R.id.formElementValue)
    }

    override fun bind(s: FormItem, listener: FormItemCallback?) {
        super.bind(s, listener)
        itemView.setOnClickListener {
            showAlertWithChoice()
            listener?.onItemClicked(s, this)
        }
        if (s is FormItemSelect) {
            valueView?.text = s.value
        }
    }

    open fun showAlertWithChoice() {
        // setup the alert builder
        (item as? FormItemSelect)?.let {
            val builder = AlertDialog.Builder(itemView.context)
            builder.setTitle(it.selectorTitle)
            builder.setItems(it.options) { _, item ->
                it.value = it.options[item]
                valueView?.text = it.value
                this.listener?.onValueChanged(it)
            }
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
                .setSingleChoiceItems(it.options, checkedItem, null)
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
            picker?.displayedValues = it.options
            picker?.value = checkedItem

            val builder = AlertDialog.Builder(itemView.context)
                .setTitle(it.selectorTitle)
                .setView(view)
                .setPositiveButton(it.yesButtonTitle) { _, _ ->
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
            pickerView?.displayedValues = s.options
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

open class FormMultipleChoiceViewHolder(inflater: LayoutInflater, resource: Int, parent: ViewGroup) :
    FormChoiceViewHolder(inflater, resource, parent) {

    override fun showAlertWithChoice() {
        // setup the alert builder
        (item as? FormItemMultipleChoice)?.let {
            if (it.checked.size == 0) {
                it.checked = Array(it.options.size) { false }
            }
            val checked = it.checked.clone()
            val builder = AlertDialog.Builder(itemView.context)
                .setTitle(it.selectorTitle)
                .setMultiChoiceItems(it.options, checked.toBooleanArray()) { _, item, isChecked ->
                    checked[item] = isChecked
                }
                .setPositiveButton(it.yesButtonTitle) { _, _ ->
                    it.checked = checked
                    it.value = it.options.filterIndexed { index, _ -> checked[index] == true }.joinToString(", ")
                    valueView?.text = it.value
                    this.listener?.onValueChanged(it)
                }
                .setNegativeButton(it.noButtonTitle) { _, _ ->
                }

            val dialog = builder.create()
            dialog.show()
        }
    }
}

open class FormColorViewHolder(inflater: LayoutInflater, resource: Int, parent: ViewGroup) :
    FormViewHolder(inflater, resource, parent) {

    var valueView: CardView? = null
    var collectionView: RecyclerView? = null
    var initialized = false
    init {
        valueView = itemView.findViewById(R.id.formElementValue)
        collectionView = itemView.findViewById(R.id.formElementCollection)
    }

    override fun bind(s: FormItem, listener: FormItemCallback?) {
        super.bind(s, listener)

        itemView.setOnClickListener {
            if (collectionView?.visibility == View.GONE) {
                initCollections()
                collectionView?.visibility = View.VISIBLE
            } else {
                collectionView?.visibility = View.GONE
            }
            listener?.onItemClicked(s, this)
        }
        if (s is FormItemColor) {
            valueView?.setCardBackgroundColor(Color.parseColor(s.value))
            valueView?.radius = dpToPx(s.cornerRadius).toFloat()
            initialized = false

            collectionView?.apply {
                layoutManager =
                    GridLayoutManager(itemView.context, s.rows, GridLayoutManager.HORIZONTAL, false)

                adapter = FormRecyclerAdapter(onItemClickListener).apply {
                    this.registerViewHolder(
                        FormItemSingleColor::class.java,
                        R.layout.form_color,
                        FormSingleColorViewHolder::class.java
                    )
                }
            }
        }
    }

    private fun initCollections() {
        if (initialized)
            return
        initialized = true
        (collectionView?.adapter as? FormRecyclerAdapter)?.apply {
            clear()
            val s = item
            if (s is FormItemColor) {
                +FormItemSection(false).apply {
                    for (clr in s.colors) {
                        +FormItemSingleColor().tag(clr).color(clr).selected(clr == s.value)
                            .cornerRadius(s.cornerRadius)
                    }
                }
                update()
            }
        }
    }
    private var onItemClickListener = object : FormItemCallback {
        override fun onItemClicked(item: FormItem, viewHolder: RecyclerView.ViewHolder) {
            super.onItemClicked(item, viewHolder)
            if (item is FormItemSingleColor) {
                (this@FormColorViewHolder.item as? FormItemColor)?.let {
                    (collectionView?.adapter as? FormRecyclerAdapter)?.let { adapter ->
                        (adapter.itemBy(it.value) as? FormItemSingleColor)?.let { old ->
                            old.selected(false)
                            adapter.updateItem(old)
                        }
                        (adapter.itemBy(item.color) as? FormItemSingleColor)?.let { new ->
                            new.selected(true)
                            adapter.updateItem(new)
                        }
                    }
                    it.value = item.color
                    valueView?.setCardBackgroundColor(Color.parseColor(item.color))
                    listener?.onValueChanged(it)
                }
            }
        }
    }

    class FormItemSingleColor: FormItem() {
        var color: String = ""
        var selected: Boolean = false
        var cornerRadius: Int = 20
    }

    fun FormItemSingleColor.color(color: String) = apply {
        this.color = color
    }

    fun FormItemSingleColor.selected(selected: Boolean) = apply {
        this.selected = selected
    }

    fun FormItemSingleColor.cornerRadius(cornerRadius: Int) = apply {
        this.cornerRadius = cornerRadius
    }
    class FormSingleColorViewHolder(inflater: LayoutInflater, resource: Int, parent: ViewGroup) :
        FormViewHolder(inflater, resource, parent) {
        var valueView: MaterialCardView? = null
        init {
            valueView = itemView.findViewById(R.id.formElementValue)
        }
        override fun bind(s: FormItem, listener: FormItemCallback?) {
            super.bind(s, listener)
            if (s is FormItemSingleColor) {
                valueView?.setCardBackgroundColor(Color.parseColor(s.color))
                valueView?.radius = dpToPx(s.cornerRadius).toFloat()
                if (s.selected) {
                    valueView?.strokeWidth = 3
                } else {
                    valueView?.strokeWidth = 0
                }
            }
        }
    }
}

open class FormSeekBarViewHolder(inflater: LayoutInflater, resource: Int, parent: ViewGroup) :
    FormViewHolder(inflater, resource, parent) {

    var seekBar: SeekBar? = null
    var valueView: TextView? = null

    init {
        seekBar = itemView.findViewById(R.id.formElementSeekBar)
        valueView = itemView.findViewById(R.id.formElementValue)
    }

    override fun bind(s: FormItem, listener: FormItemCallback?) {
        super.bind(s, listener)
        if (s is FormItemSeekBar) {
            valueView?.setOnClickListener {
                if (seekBar?.visibility == View.GONE)
                    seekBar?.visibility = View.VISIBLE
                else
                    seekBar?.visibility = View.GONE
                listener?.onItemClicked(s, this)
            }

            seekBar?.setOnSeekBarChangeListener(null)

            if (s.value >= s.minValue) {
                valueView?.text = s.value.toString()
                seekBar?.progress = s.value - s.minValue
            } else {
                valueView?.text = ""
                seekBar?.progress = 0
            }
            seekBar?.max = s.maxValue - s.minValue
            //seekBar?.min = 0

            seekBar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    s.value = progress + s.minValue
                    valueView?.text = s.value.toString()
                    if (fromUser) {
                        listener?.onValueChanged(s)
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                }
            })

        }
    }
}