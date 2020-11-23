package com.feiyilin.form

import android.graphics.Color
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.util.Size
import android.view.Gravity
import android.view.inputmethod.EditorInfo
import androidx.recyclerview.widget.RecyclerView
import java.util.*

open class FormSwipeAction {
    enum class Style {
        Destructive,
        Normal
    }

    var tag: String = ""
    var title: String = ""
    var icon: Drawable? = null
    var iconSize: Int = 24
    var textSize: Float = 12.0f
    var backgroundColor: Int = Color.parseColor("#ffff4444")
    var width: Float = 0F
    var style: Style = Style.Normal
    var rect = RectF()
    var padding: Float = 50f
}

fun <T : FormSwipeAction> T.tag(tag: String) = apply {
    this.tag = tag
}

fun <T : FormSwipeAction> T.title(title: String) = apply {
    this.title = title
}

fun <T : FormSwipeAction> T.icon(icon: Drawable?) = apply {
    this.icon = icon
}

fun <T : FormSwipeAction> T.iconSize(iconSize: Int) = apply {
    this.iconSize = iconSize
}

fun <T : FormSwipeAction> T.textSize(textSize: Float) = apply {
    this.textSize = textSize
}

fun <T : FormSwipeAction> T.backgroundColor(backgroundColor: Int) = apply {
    this.backgroundColor = backgroundColor
}

fun <T : FormSwipeAction> T.width(width: Float) = apply {
    this.width = width
}

fun <T : FormSwipeAction> T.style(style: FormSwipeAction.Style) = apply {
    this.style = style
}

fun <T : FormSwipeAction> T.padding(padding: Float) = apply {
    this.padding = padding
}

open class FormItem {
    enum class Separator {
        DEFAULT,
        NONE,
        IGNORE_ICON;
    }

    var title: String = ""
    var titleColor: Int? = null
    var subTitle: String = ""
    var subTitleColor: Int? = null
    var iconTitle: Drawable? = null
    var iconSize: Size = Size(24, 24)
    var draggable: Boolean = false
    var tag: String = ""
    var required: Boolean = false
    var hidden: Boolean = false
    var minHeight: Int = 0
    var separator: Separator? = null
    var badge: String? = null
    var leadingSwipe = listOf<FormSwipeAction>()
    var trailingSwipe = listOf<FormSwipeAction>()
    var section: FormItemSection? = null

    // callback
    var onSetup: ((viewHolder: RecyclerView.ViewHolder) -> Unit)? = null
    var onValueChanged: (() -> Unit)? = null
    var onItemClicked: ((viewHolder: RecyclerView.ViewHolder) -> Unit)? = null
    var onTitleImageClicked: ((viewHolder: RecyclerView.ViewHolder) -> Unit)? = null
    var onStartReorder: ((viewHolder: RecyclerView.ViewHolder) -> Boolean)? = null
    var onMoveItem: ((src: Int, dest: Int) -> Boolean)? = null
    var onSwipedAction: ((action: FormSwipeAction, viewHolder: RecyclerView.ViewHolder) -> Boolean)? = null
    var onEditorAction: ((actionId: Int, viewHolder: RecyclerView.ViewHolder) -> Boolean)? = null
}

fun <T : FormItem> T.title(title: String) = apply {
    this.title = title
}

fun <T : FormItem> T.titleColor(titleColor: Int?) = apply {
    this.titleColor = titleColor
}

fun <T : FormItem> T.subTitle(subTitle: String) = apply {
    this.subTitle = subTitle
}

fun <T : FormItem> T.subTitleColor(subTitleColor: Int) = apply {
    this.subTitleColor = subTitleColor
}

fun <T : FormItem> T.iconTitle(iconTitle: Drawable?) = apply {
    this.iconTitle = iconTitle
}

fun <T : FormItem> T.iconSize(iconSize: Size) = apply {
    this.iconSize = iconSize
}

fun <T : FormItem> T.iconSize(weight: Int, height: Int) = apply {
    this.iconSize = Size(weight, height)
}

fun <T : FormItem> T.iconSize(iconSize: Int) = apply {
    this.iconSize = Size(iconSize, iconSize)
}

fun <T : FormItem> T.tag(tag: String) = apply {
    this.tag = tag
}

fun <T : FormItem> T.draggable(draggable: Boolean = true) = apply {
    this.draggable = draggable
}

fun <T : FormItem> T.required(required: Boolean = true) = apply {
    this.required = required
}

fun <T : FormItem> T.hidden(hidden: Boolean = true) = apply {
    this.hidden = hidden
}

fun <T : FormItem> T.minHeight(height: Int) = apply {
    this.minHeight = height
}

fun <T : FormItem> T.separator(separator: FormItem.Separator?) = apply {
    this.separator = separator
}

fun <T : FormItem> T.badge(badge: String?) = apply {
    this.badge = badge
}

fun <T : FormItem> T.leadingSwipe(actions: List<FormSwipeAction>) = apply {
    this.leadingSwipe = actions
}

fun <T : FormItem> T.trailingSwipe(actions: List<FormSwipeAction>) = apply {
    this.trailingSwipe = actions
}

fun <T : FormItem> T.onSetup(callback: ((item: T, viewHolder: RecyclerView.ViewHolder) -> Unit)?) = apply {
    if (callback == null) {
        this.onSetup = null
    } else {
        this.onSetup = { viewHolder ->
            callback.invoke(this, viewHolder)
        }
    }
}

fun <T : FormItem> T.onValueChanged(callback: ((item: T) -> Unit)?) = apply {
    if (callback == null) {
        this.onValueChanged = null
    } else {
        this.onValueChanged = {
            callback.invoke(this)
        }
    }
}

fun <T : FormItem> T.onItemClicked(callback: ((item: T, viewHolder: RecyclerView.ViewHolder) -> Unit)?) = apply {
    if (callback == null) {
        this.onItemClicked = null
    } else {
        this.onItemClicked = { viewHolder ->
            callback.invoke(this, viewHolder)
        }
    }
}

fun <T : FormItem> T.onTitleImageClicked(callback: ((item: T, viewHolder: RecyclerView.ViewHolder) -> Unit)?) = apply {
    if (callback == null) {
        this.onTitleImageClicked = null
    } else {
        this.onTitleImageClicked = { viewHolder ->
            callback.invoke(this, viewHolder)
        }
    }
}

fun <T : FormItem> T.onStartReorder(callback: ((item: T, viewHolder: RecyclerView.ViewHolder) -> Boolean)?) = apply {
    if (callback == null) {
        this.onStartReorder = null
    } else {
        this.onStartReorder = { viewHolder ->
            callback.invoke(this, viewHolder)
        }
    }
}

fun <T : FormItem> T.onMoveItem(callback: ((src: Int, dest: Int) -> Boolean)?) = apply {
    this.onMoveItem = callback
}

fun <T : FormItem> T.onSwipedAction(callback: ((item: T, action: FormSwipeAction, viewHolder: RecyclerView.ViewHolder) -> Boolean)?) = apply {
    if (callback == null) {
        this.onSwipedAction = null
    } else {
        this.onSwipedAction = { action, viewHolder ->
            callback.invoke(this, action, viewHolder)
        }
    }
}

fun <T : FormItem> T.onEditorAction(callback: ((item: T, actionId: Int, viewHolder: RecyclerView.ViewHolder) -> Boolean)?) = apply {
    if (callback == null) {
        this.onEditorAction = null
    } else {
        this.onEditorAction = { actionId, viewHolder ->
            callback.invoke(this, actionId, viewHolder)
        }
    }
}


open class FormItemLabel : FormItem() {
}

open class FormItemText : FormItem() {
    var value: String = ""
    var valueColor: Int? = null
    var hint: String = ""
    var hintColor: Int? = null
    var gravity: Int = Gravity.END
    var readOnly: Boolean = false
    var imeOptions: Int =
        EditorInfo.IME_ACTION_NEXT or EditorInfo.IME_FLAG_NAVIGATE_NEXT or EditorInfo.IME_FLAG_NAVIGATE_PREVIOUS
    var inputType: Int = EditorInfo.TYPE_TEXT_FLAG_AUTO_CORRECT or EditorInfo.TYPE_CLASS_TEXT
    var focused: Boolean = false
    var clearIcon: Boolean = false
}

fun <T : FormItemText> T.value(value: String) = apply {
    this.value = value
}

fun <T : FormItemText> T.valueColor(valueColor: Int) = apply {
    this.valueColor = valueColor
}

fun <T : FormItemText> T.hint(hint: String) = apply {
    this.hint = hint
}

fun <T : FormItemText> T.hintColor(hintColor: Int) = apply {
    this.hintColor = hintColor
}

fun <T : FormItemText> T.gravity(gravity: Int) = apply {
    this.gravity = gravity
}

fun <T : FormItemText> T.readOnly(readOnly: Boolean = true) = apply {
    this.readOnly = readOnly
}

fun <T : FormItemText> T.imeOptions(imeOptions: Int) = apply {
    this.imeOptions = imeOptions
}

fun <T : FormItemText> T.inputType(inputType: Int) = apply {
    this.inputType = inputType
}

fun <T : FormItemText> T.focused(focused: Boolean = true) = apply {
    this.focused = focused
}

fun <T : FormItemText> T.clearIcon(clearIcon: Boolean = true) = apply {
    this.clearIcon = clearIcon
}

open class FormItemPassword : FormItemText() {
    var shownPassword: Boolean = false
    init {
        this.inputType = EditorInfo.TYPE_TEXT_VARIATION_PASSWORD
    }
}

fun <T : FormItemPassword> T.shown(shown: Boolean = true) = apply {
    this.shownPassword = shown
}

open class FormItemTextFloatingHint : FormItemText() {
}

open class FormItemTextArea : FormItemText() {
    var minLines: Int = 3
    var maxLines: Int = 6

    init {
        this.gravity = Gravity.START
        this.inputType =
            (EditorInfo.TYPE_TEXT_FLAG_CAP_SENTENCES or EditorInfo.TYPE_TEXT_FLAG_NO_SUGGESTIONS
                    or EditorInfo.TYPE_TEXT_FLAG_MULTI_LINE or EditorInfo.TYPE_CLASS_TEXT)
        this.imeOptions = EditorInfo.IME_NULL
    }
}

open class FormItemTextAreaFloatingHint : FormItemTextArea() {
}

fun <T : FormItemTextArea> T.minLines(minLines: Int) = apply {
    this.minLines = minLines
}

fun <T : FormItemTextArea> T.maxLines(maxLines: Int) = apply {
    this.maxLines = maxLines
}

open class FormItemAction : FormItem() {
    var alignment: Int = Gravity.CENTER
}

fun <T : FormItemAction> T.alignment(alignment: Int) = apply {
    this.alignment = alignment
}

abstract class FormItemToggle : FormItem() {
    var isOn: Boolean = false
}

fun <T : FormItemToggle> T.isOn(isOn: Boolean = true) = apply {
    this.isOn = isOn
}

open class FormItemSwitch : FormItemToggle() {
}

open class FormItemSwitchCustom : FormItemSwitch() {
    var iconOff: Drawable? = null
    var iconOn: Drawable? = null

    init {
        this.iconSize(Size(48, 24))
    }
}

fun <T : FormItemSwitchCustom> T.iconOn(iconOn: Drawable?) = apply {
    this.iconOn = iconOn
}

fun <T : FormItemSwitchCustom> T.iconOff(iconOff: Drawable?) = apply {
    this.iconOff = iconOff
}

open class FormItemRadio : FormItemToggle() {
    var group: String = ""
}

fun <T : FormItemRadio> T.group(group: String) = apply {
    this.group = group
}

open class FormItemRadioCustom : FormItemRadio() {
    var iconOff: Drawable? = null
    var iconOn: Drawable? = null

    init {
        this.iconSize(Size(24, 24))
    }
}

fun <T : FormItemRadioCustom> T.iconOn(iconOn: Drawable?) = apply {
    this.iconOn = iconOn
}

fun <T : FormItemRadioCustom> T.iconOff(iconOff: Drawable?) = apply {
    this.iconOff = iconOff
}

open class FormItemCheck : FormItemToggle() {
}

open class FormItemCheckCustom : FormItemCheck() {
    var iconOff: Drawable? = null
    var iconOn: Drawable? = null

    init {
        this.iconSize(Size(32, 32))
    }
}

fun <T : FormItemCheckCustom> T.iconOn(iconOn: Drawable?) = apply {
    this.iconOn = iconOn
}

fun <T : FormItemCheckCustom> T.iconOff(iconOff: Drawable?) = apply {
    this.iconOff = iconOff
}


open class FormItemNav : FormItem() {
    var value: String = ""
}

fun <T : FormItemNav> T.value(value: String) = apply {
    this.value = value
}

open class FormItemDate : FormItem() {
    var date: Date = Date()
    var dateOnly: Boolean = false
    var timeOnly: Boolean = false
    var timeFormat: String = "hh:mm a"
    var dateFormat: String = "MM/dd/yyyy"
    var dateColor: Int? = null
    var timeColor: Int? = null

    var year: Int
        get() {
            val calendar: Calendar = Calendar.getInstance()
            calendar.time = date
            return calendar[Calendar.YEAR]
        }
        set(value) {
            val calendar: Calendar = Calendar.getInstance()
            calendar.time = date
            calendar.set(Calendar.YEAR, value)
            date = calendar.time
        }

    var month: Int
        get() {
            val calendar: Calendar = Calendar.getInstance()
            calendar.time = date
            return calendar[Calendar.MONTH]
        }
        set(value) {
            val calendar: Calendar = Calendar.getInstance()
            calendar.time = date
            calendar.set(Calendar.MONTH, value)
            date = calendar.time
        }

    var day: Int
        get() {
            val calendar: Calendar = Calendar.getInstance()
            calendar.time = date
            return calendar[Calendar.DAY_OF_MONTH]
        }
        set(value) {
            val calendar: Calendar = Calendar.getInstance()
            calendar.time = date
            calendar.set(Calendar.DAY_OF_MONTH, value)
            date = calendar.time
        }

    var hour: Int
        get() {
            val calendar: Calendar = Calendar.getInstance()
            calendar.time = date
            return calendar[Calendar.HOUR_OF_DAY]
        }
        set(value) {
            val calendar: Calendar = Calendar.getInstance()
            calendar.time = date
            calendar.set(Calendar.HOUR_OF_DAY, value)
            date = calendar.time
        }

    var minute: Int
        get() {
            val calendar: Calendar = Calendar.getInstance()
            calendar.time = date
            return calendar[Calendar.MINUTE]
        }
        set(value) {
            val calendar: Calendar = Calendar.getInstance()
            calendar.time = date
            calendar.set(Calendar.MINUTE, value)
            date = calendar.time
        }
}

fun <T : FormItemDate> T.date(date: Date) = apply {
    this.date = date
}

fun <T : FormItemDate> T.dateOnly(dateOnly: Boolean = true) = apply {
    this.dateOnly = dateOnly
    if (dateOnly) {
        this.timeOnly = false
    }
}

fun <T : FormItemDate> T.timeOnly(timeOnly: Boolean = true) = apply {
    this.timeOnly = timeOnly
    if (timeOnly) {
        this.dateOnly = false
    }
}

fun <T : FormItemDate> T.dateFormat(dateFormat: String) = apply {
    this.dateFormat = dateFormat
}

fun <T : FormItemDate> T.timeFormat(timeFormat: String) = apply {
    this.timeFormat = timeFormat
}

fun <T : FormItemDate> T.dateColor(dateColor: Int?) = apply {
    this.dateColor = dateColor
}

fun <T : FormItemDate> T.timeColor(timeColor: Int?) = apply {
    this.timeColor = timeColor
}

open class FormItemSelect : FormItem() {
    var value: String = ""
    var selectorTitle = ""
    var options: Array<String> = arrayOf()
}

fun <T : FormItemSelect> T.value(value: String) = apply {
    this.value = value
}

fun <T : FormItemSelect> T.selectorTitle(title: String) = apply {
    this.selectorTitle = title
}

fun <T : FormItemSelect> T.options(options: Array<String>) = apply {
    this.options = options
}

open class FormItemChoice : FormItemSelect() {
    var yesButtonTitle = "Ok"
    var noButtonTitle = "Cancel"
}

fun <T : FormItemChoice> T.yesButtonTitle(title: String) = apply {
    this.yesButtonTitle = title
}

fun <T : FormItemChoice> T.noButtonTitle(title: String) = apply {
    this.noButtonTitle = title
}

open class FormItemPicker : FormItemChoice() {
}

open class FormItemPickerInline : FormItemPicker() {
}

open class FormItemMultipleChoice : FormItemChoice() {
    var checked: Array<Boolean> = arrayOf()
}

fun <T : FormItemMultipleChoice> T.options(
    options: Array<String>,
    checked: Array<Boolean> = arrayOf()
) = apply {
    this.options = options
    this.checked = checked
    if (this.checked.size != this.options.size) {
        this.checked = Array(this.options.size) { false }
        for (v in this.value.split(",")) {
            val index = this.options.indexOf(v)
            if (index != -1) {
                this.checked[index] = true
            }
        }
    } else {
        this.value = this.options.filterIndexed { index, _ -> this.checked[index] == true }
            .joinToString(", ")
    }
}

fun <T : FormItemMultipleChoice> T.value(value: String) = apply {
    this.value = value
    if (this.options.size > 0) {
        this.checked = Array(this.options.size) { false }
        for (v in this.value.split(',')) {
            val index = this.options.indexOf(v.trim())
            if (index != -1) {
                this.checked[index] = true
            }
        }
    }
}

open class FormItemColor : FormItem() {
    var colors: Array<String> = arrayOf(
        "#f44336", "#e81e63", "#9c27b0", "#673ab7", "#3f51b5", "#2196f3", "#03a9f4",
        "#00bcd4", "#009688", "#4caf50", "#8bc34a", "#cddc39", "#ffeb3b", "#ffc107",
        "#ff9800", "#ff5722", "#795548", "#9e9e9e", "#607d8b"
    )
    var value: String = ""
    var cornerRadius: Int = 20
    var rows: Int = 2
}

fun <T : FormItemColor> T.colors(colors: Array<String>) = apply {
    this.colors = colors
}

fun <T : FormItemColor> T.value(value: String) = apply {
    this.value = value
}

fun <T : FormItemColor> T.cornerRadius(cornerRadius: Int) = apply {
    this.cornerRadius = cornerRadius
}

fun <T : FormItemColor> T.rows(rows: Int) = apply {
    this.rows = rows
}

open class FormItemInteger : FormItem() {
    var value: Int = 0
    var maxValue: Int = 100
    var minValue: Int = 0
}

fun <T : FormItemInteger> T.value(value: Int) = apply {
    this.value = value
}

fun <T : FormItemInteger> T.maxValue(maxValue: Int) = apply {
    this.maxValue = maxValue
}

fun <T : FormItemInteger> T.minValue(minValue: Int) = apply {
    this.minValue = minValue
}

open class FormItemSeekBar : FormItemInteger() {
}

open class FormItemStepper : FormItemInteger() {
}