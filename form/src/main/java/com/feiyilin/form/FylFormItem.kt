package com.feiyilin.form

import android.graphics.drawable.Drawable
import android.view.Gravity
import android.view.View
import android.view.inputmethod.EditorInfo

open class FylFormItem(var type: String) {
    var title: String = ""
    var subTitle: String = ""
    var iconTitle: Drawable? = null
    var iconSize: Int = 24
    var value: String = ""
    var dragable: Boolean = false
    var tag: String = ""
    var originalValue: String = ""
}

fun <T : FylFormItem> T.title(title: String) = apply {
    this.title = title
}

fun <T : FylFormItem> T.subTitle(subTitle: String) = apply {
    this.subTitle = subTitle
}

fun <T : FylFormItem> T.iconTitle(iconTitle: Drawable?) = apply {
    this.iconTitle = iconTitle
}

fun <T : FylFormItem> T.iconSize(iconSize: Int) = apply {
    this.iconSize = iconSize
}

fun <T : FylFormItem> T.value(value: String) = apply {
    this.value = value
}

fun <T : FylFormItem> T.tag(tag: String) = apply {
    this.tag = tag
}

fun <T : FylFormItem> T.dragable(dragable: Boolean) = apply {
    this.dragable = dragable
}


open class FylFormItemLabel() : FylFormItem("label") {
}

open class FylFormItemImage() : FylFormItem(type = "image") {
}

open class FylFormItemText() : FylFormItem(type = "text") {
    var placeholder: String = ""
    var textAlignment: Int = View.TEXT_ALIGNMENT_TEXT_END
    var readOnly: Boolean = false
    var imeOptions: Int = 0
    var inputType: Int = 0
    var focused: Boolean = false
}

fun <T : FylFormItemText> T.placeholder(placeholder: String) = apply {
    this.placeholder = placeholder
}

fun <T : FylFormItemText> T.textAlignment(textAlignment: Int) = apply {
    this.textAlignment = textAlignment
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

class FylFormItemTextArea() : FylFormItemText() {
    var minLines: Int = 3
    var maxLines: Int = 6

    init {
        this.type = "text_area"
        this.textAlignment = View.TEXT_ALIGNMENT_TEXT_START
        this.inputType =
            (EditorInfo.TYPE_TEXT_FLAG_CAP_SENTENCES or EditorInfo.TYPE_TEXT_FLAG_NO_SUGGESTIONS
                    or EditorInfo.TYPE_TEXT_FLAG_MULTI_LINE or EditorInfo.TYPE_CLASS_TEXT)
        this.imeOptions = EditorInfo.IME_NULL
    }
}

fun <T : FylFormItemTextArea> T.minLines(minLines: Int) = apply {
    this.minLines = minLines
}

fun <T : FylFormItemTextArea> T.maxLines(maxLines: Int) = apply {
    this.maxLines = maxLines
}

class FylFormItemSection() : FylFormItem("section") {
}

class FylFormItemAction() : FylFormItem("action") {
    var alignment: Int = Gravity.CENTER
}

fun <T : FylFormItemAction> T.alignment(alignment: Int) = apply {
    this.alignment = alignment
}

abstract class FylFormItemToggle(type: String) : FylFormItem(type) {
    var isOn: Boolean = false
}

fun <T : FylFormItemToggle> T.isOn(isOn: Boolean) = apply {
    this.isOn = isOn
}

abstract class FylFormItemToggleCustomDraw(type: String) : FylFormItemToggle(type) {
    var iconOff: Drawable? = null
    var iconOn: Drawable? = null
}

fun <T : FylFormItemToggleCustomDraw> T.iconOn(iconOn: Drawable?) = apply {
    this.iconOn = iconOn
}

fun <T : FylFormItemToggleCustomDraw> T.iconOff(iconOff: Drawable?) = apply {
    this.iconOff = iconOff
}


class FylFormItemSwitchNative() : FylFormItemToggle("switch_native") {
}

class FylFormItemSwitch() : FylFormItemToggleCustomDraw("switch") {
}

class FylFormItemRadio() : FylFormItemToggleCustomDraw("radio") {
    var group: String = ""
}

fun <T : FylFormItemRadio> T.group(group: String) = apply {
    this.group = group
}

class FylFormItemNav() : FylFormItem("nav") {
    var badge: String? = null
}

fun <T : FylFormItemNav> T.badge(badge: String?) = apply {
    this.badge = badge
}
