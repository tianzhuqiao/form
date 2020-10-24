package com.feiyilin.form

import android.graphics.drawable.Drawable
import android.view.Gravity
import android.view.inputmethod.EditorInfo
import java.util.*

open class FylFormItem {
    var title: String = ""
    var titleColor: Int? = null
    var subTitle: String = ""
    var subTitleColor: Int? = null
    var iconTitle: Drawable? = null
    var iconSize: Int = 24
    var dragable: Boolean = false
    var tag: String = ""
    var originalValue: String = ""
    var required: Boolean = false
    var hidden: Boolean = false
}

fun <T : FylFormItem> T.title(title: String) = apply {
    this.title = title
}

fun <T : FylFormItem> T.titleColor(titleColor: Int?) = apply {
    this.titleColor = titleColor
}

fun <T : FylFormItem> T.subTitle(subTitle: String) = apply {
    this.subTitle = subTitle
}

fun <T : FylFormItem> T.subTitleColor(subTitleColor: Int) = apply {
    this.subTitleColor = subTitleColor
}

fun <T : FylFormItem> T.iconTitle(iconTitle: Drawable?) = apply {
    this.iconTitle = iconTitle
}

fun <T : FylFormItem> T.iconSize(iconSize: Int) = apply {
    this.iconSize = iconSize
}

fun <T : FylFormItem> T.tag(tag: String) = apply {
    this.tag = tag
}

fun <T : FylFormItem> T.dragable(dragable: Boolean) = apply {
    this.dragable = dragable
}

fun <T : FylFormItem> T.required(required: Boolean=true) = apply {
    this.required = required
}

fun <T : FylFormItem> T.hidden(hidden: Boolean=true) = apply {
    this.hidden = hidden
}

open class FylFormItemLabel : FylFormItem() {
}

open class FylFormItemText : FylFormItem() {
    var value: String = ""
    var valueColor: Int? = null
    var hint: String = ""
    var hintColor: Int? = null
    var gravity: Int = Gravity.END
    var readOnly: Boolean = false
    var imeOptions: Int = 0
    var inputType: Int = 0
    var focused: Boolean = false
}

open class FylFormItemTextFloatingHint : FylFormItemText() {
}

fun <T : FylFormItemText> T.value(value: String) = apply {
    this.value = value
}

fun <T : FylFormItemText> T.valueColor(valueColor: Int) = apply {
    this.valueColor = valueColor
}

fun <T : FylFormItemText> T.hint(hint: String) = apply {
    this.hint = hint
}

fun <T : FylFormItemText> T.hintColor(hintColor: Int) = apply {
    this.hintColor = hintColor
}

fun <T : FylFormItemText> T.gravity(gravity: Int) = apply {
    this.gravity = gravity
}

fun <T : FylFormItemText> T.readOnly(readOnly: Boolean) = apply {
    this.readOnly = readOnly
}

fun <T : FylFormItemText> T.imeOptions(imeOptions: Int) = apply {
    this.imeOptions = imeOptions
}

fun <T : FylFormItemText> T.inputType(inputType: Int) = apply {
    this.inputType = inputType
}

fun <T : FylFormItemText> T.focused(focused: Boolean) = apply {
    this.focused = focused
}

open class FylFormItemTextArea : FylFormItemText() {
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

open class FylFormItemTextAreaFloatingHint : FylFormItemTextArea() {
}

fun <T : FylFormItemTextArea> T.minLines(minLines: Int) = apply {
    this.minLines = minLines
}

fun <T : FylFormItemTextArea> T.maxLines(maxLines: Int) = apply {
    this.maxLines = maxLines
}

open class FylFormItemSection : FylFormItem() {
}

open class FylFormItemAction : FylFormItem() {
    var alignment: Int = Gravity.CENTER
}

fun <T : FylFormItemAction> T.alignment(alignment: Int) = apply {
    this.alignment = alignment
}

abstract class FylFormItemToggle : FylFormItem() {
    var isOn: Boolean = false
}

fun <T : FylFormItemToggle> T.isOn(isOn: Boolean) = apply {
    this.isOn = isOn
}

abstract class FylFormItemToggleCustomDraw : FylFormItemToggle() {
    var iconOff: Drawable? = null
    var iconOn: Drawable? = null
}

fun <T : FylFormItemToggleCustomDraw> T.iconOn(iconOn: Drawable?) = apply {
    this.iconOn = iconOn
}

fun <T : FylFormItemToggleCustomDraw> T.iconOff(iconOff: Drawable?) = apply {
    this.iconOff = iconOff
}


open class FylFormItemSwitchNative : FylFormItemToggle() {
}

open class FylFormItemSwitch : FylFormItemToggleCustomDraw() {
}

open class FylFormItemRadioNative : FylFormItemToggle() {
    var group: String = ""
}

fun <T : FylFormItemRadioNative> T.group(group: String) = apply {
    this.group = group
}

open class FylFormItemRadio : FylFormItemToggleCustomDraw() {
    var group: String = ""
}

fun <T : FylFormItemRadio> T.group(group: String) = apply {
    this.group = group
}

open class FylFormItemNav : FylFormItem() {
    var badge: String? = null
}

fun <T : FylFormItemNav> T.badge(badge: String?) = apply {
    this.badge = badge
}

open class FylFormItemDate : FylFormItem() {
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

    var  day : Int
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

    var hour : Int
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

fun <T : FylFormItemDate> T.date(date: Date) = apply {
    this.date = date
}

fun <T : FylFormItemDate> T.dateOnly(dateOnly: Boolean) = apply {
    this.dateOnly = dateOnly
}
fun <T : FylFormItemDate> T.timeOnly(timeOnly: Boolean) = apply {
    this.timeOnly = timeOnly
}

fun <T : FylFormItemDate> T.dateFormat(dateFormat: String) = apply {
    this.dateFormat = dateFormat
}
fun <T : FylFormItemDate> T.timeFormat(timeFormat: String) = apply {
    this.timeFormat = timeFormat
}

fun <T : FylFormItemDate> T.dateColor(dateColor: Int?) = apply {
    this.dateColor = dateColor
}

fun <T : FylFormItemDate> T.timeColor(timeColor: Int?) = apply {
    this.timeColor = timeColor
}