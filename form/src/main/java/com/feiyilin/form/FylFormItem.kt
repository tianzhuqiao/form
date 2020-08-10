package com.feiyilin.form

import android.graphics.drawable.Drawable
import android.view.View
import android.view.inputmethod.EditorInfo

open class FylFormItem(var type: String) {
    var title: String = ""
    var subTitle: String = ""
    var imageResId: Int? = null
    var value: String = ""
    var dragable: Boolean = false
    var tag: String = ""
    var originalValue: String = ""

    fun title(title: String) : FylFormItem {
        this.title = title
        return this
    }
    fun subTitle(subTitle: String) : FylFormItem {
        this.subTitle = subTitle
        return this
    }
    fun imageResId(imageResId: Int) : FylFormItem {
        this.imageResId = imageResId
        return this
    }
    fun value(value: String) : FylFormItem {
        this.value = value
        return this
    }
    fun tag(tag: String) : FylFormItem {
        this.tag = tag
        return this
    }

    fun dragable(dragable: Boolean) : FylFormItem {
        this.dragable = dragable
        return this
    }
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

    fun placeholder(placeholder: String) : FylFormItemText {
        this.placeholder = placeholder
        return this
    }

    fun textAlignment(textAlignment: Int) : FylFormItemText {
        this.textAlignment = textAlignment
        return this
    }

    fun readOnly(readOnly: Boolean) : FylFormItemText {
        this.readOnly = readOnly
        return this
    }

    fun imeOptions(imeOptions: Int) : FylFormItemText {
        this.imeOptions = imeOptions
        return this
    }

    fun inputType(inputType: Int) : FylFormItemText {
        this.inputType = inputType
        return this
    }

    fun focused(focused: Boolean) : FylFormItemText {
        this.focused = focused
        return this
    }
}

class FylFormItemTextArea() : FylFormItemText() {
    var minLines: Int = 3
    var maxLines: Int = 6
    init {
        this.type = "text_area"
        this.textAlignment = View.TEXT_ALIGNMENT_TEXT_START
        this.inputType = (EditorInfo.TYPE_TEXT_FLAG_CAP_SENTENCES or EditorInfo.TYPE_TEXT_FLAG_NO_SUGGESTIONS
                          or EditorInfo.TYPE_TEXT_FLAG_MULTI_LINE or EditorInfo.TYPE_CLASS_TEXT)
        this.imeOptions = EditorInfo.IME_NULL
    }

    fun minLines(minLines: Int) : FylFormItemTextArea {
        this.minLines = minLines
        return this
    }

    fun maxLines(maxLines: Int) : FylFormItemTextArea {
        this.maxLines = maxLines
        return this
    }
}

class FylFormItemSection() : FylFormItem("section") {
}

class FylFormItemAction() : FylFormItem("action") {
    var textAlignment: Int = View.TEXT_ALIGNMENT_CENTER
    fun textAlignment(textAlignment: Int) : FylFormItemAction {
        this.textAlignment = textAlignment
        return this
    }
}

class FylFormItemSwitchNative() : FylFormItem("switch_native") {
    var isOn: Boolean = false
    fun isOn(isOn : Boolean) : FylFormItemSwitchNative {
        this.isOn = isOn
        return this
    }
}

class FylFormItemSwitch() : FylFormItem("switch") {
    var isOn: Boolean = false
    fun isOn(isOn : Boolean) : FylFormItemSwitch {
        this.isOn = isOn
        return this
    }
}

class FylFormItemRadio() : FylFormItem( "radio") {
    var isOn: Boolean = false
    var group: String = ""
    var iconOff: Drawable? = null
    var iconOn: Drawable? = null

    fun isOn(isOn : Boolean) : FylFormItemRadio {
        this.isOn = isOn
        return this
    }
    fun group(group : String) : FylFormItemRadio {
        this.group = group
        return this
    }
    fun iconOn(iconOn: Drawable?) : FylFormItemRadio {
        this.iconOn = iconOn
        return this
    }
    fun iconOff(iconOff: Drawable?) : FylFormItemRadio {
        this.iconOff = iconOff
        return this
    }
}

class FylFormItemNav() : FylFormItem( "nav") {
    var badge: String? = null

    fun badge(badge : String?) : FylFormItemNav {
        this.badge = badge
        return this
    }
}
