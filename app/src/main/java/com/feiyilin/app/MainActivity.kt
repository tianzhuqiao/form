package com.feiyilin.app

import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.feiyilin.form.*
import com.squareup.picasso.Picasso
import java.util.*

class MainActivity : FormActivity() {

    var newItemCreated = 0
    var newSectionCreated = 0
    override fun initForm() {
        val cal = Calendar.getInstance()
        cal.set(2020, 6, 19)

        adapter?.apply {
            +FormItemSection().title("Text").tag("sec_text").apply {
                +FormItemText().title("Text").tag("text").required(true)
                +FormItemText().title("Text").subTitle("here is subtitle").tag("text_subtitle")
                +FormItemText().title("Text").subTitle("dragable").dragable(true)
                    .tag("text_dragable")
                +FormItemText().title("With icon").iconTitle(drawable(R.drawable.ic_form_info))
                    .tag("text_icon")
                +FormItemText().title("Ready only").tag("read_only").value("www.feiyilin.com")
                    .readOnly(true)
                +FormItemTextFloatingHint().hint("Text with floating hint").tag("text")
                    .gravity(Gravity.START)
                +FormItemText().title("Email").tag("email")
                    .inputType(EditorInfo.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
                +FormItemText().title("Number").tag("number")
                    .inputType(EditorInfo.TYPE_CLASS_NUMBER)
                +FormItemText().title("Phone").tag("phone")
                    .inputType(EditorInfo.TYPE_CLASS_PHONE)
            }
            +FormItemSection().title("Multi-line text").apply {
                +FormItemTextArea().hint("Multi-line text here ...").tag("notes")
                +FormItemTextAreaFloatingHint().hint("Multi-line text with floating hint here ...")
                    .tag("notes")
            }

            +FormItemSection().title("Navigation item").apply {
                //FormItemLabel().title("Label").tag("label"),
                +FormItemNav().title("Nav item").tag("nav_item")
                +FormItemNav().title("Nav item with subtitle").subTitle("www.abc.com")
                    .tag("nav_item_subtitle")
                +FormItemNav().title("Nav item with badge").tag("nav_item_badge").badge("")
                +FormItemNav().title("Nav item with badge and icon").tag("nav_item_badge_icon")
                    .badge("")
                    .iconTitle(drawable(R.drawable.ic_form_info))
                +FormItemNav().title("Nav item with number badge").tag("nav_item_badge_num")
                    .badge("99")
                    .iconSize(44, 44)
                    .iconTitle(drawable(R.drawable.ic_form_info))
            }

            +FormItemSection().title("Radio").apply {
                +FormItemRadio().isOn(true).group("radio0")
                    .title("item 0")
                    .tag("radio0_item0")
                +FormItemRadio().group("radio0").title("item 1")
                    .tag("radio0_item1")
            }

            +FormItemSection().title("Radio native").apply {
                +FormItemRadioNative().isOn(true).group("radio1")
                    .title("item 0")
                    .tag("radio1_item0")
                +FormItemRadioNative().group("radio1").title("item 1")
                    .tag("radio1_item1")
            }

            +FormItemSection().title("Switch").apply {
                +FormItemSwitch().isOn(true).title("Switch").tag("switch")
                +FormItemSwitchNative().isOn(true).title("Switch native").tag("switch_native")

                +FormItemSwitchNative().isOn(true).title("Show action item")
                    .tag("switch_show_action")
                +FormItemAction().title("Action").tag("action").subTitle("description")
                    .iconTitle(drawable(R.drawable.ic_form_info))

                +FormItemSwitchNative().isOn(true).title("Show date/time section")
                    .tag("switch_show_date")
            }

            +FormItemSection().title("Date / Time").tag("sec_date").apply {
                +FormItemDate().tag("date").title("Date").date(cal.time)
                +FormItemDate().tag("date_only").title("Date only").date(cal.time)
                    .dateOnly(true)
                +FormItemDate().tag("time_only").title("Time only").date(cal.time)
                    .timeOnly(true)
            }

            +FormItemSection().title("Swipe Action").tag("sec_swipe").apply {
                +FormItemNav().title("Swipe left").trailingSwipe(
                    listOf(
                        FormSwipeAction().title("Delete").style(FormSwipeAction.Style.Destructive)
                    )
                )
                +FormItemNav().title("Swipe left: with icon").trailingSwipe(
                    listOf(
                        FormSwipeAction().title("Delete").style(FormSwipeAction.Style.Destructive)
                            .icon(drawable(R.drawable.ic_delete_white))
                    )
                )
                +FormItemNav().title("Swipe left: multiple actions").trailingSwipe(
                    listOf(
                        FormSwipeAction().title("Delete").backgroundColor(
                            color(android.R.color.holo_red_light)
                        ),
                        FormSwipeAction().title("Archive").backgroundColor(
                            color(android.R.color.holo_blue_light)
                        ),
                        FormSwipeAction().title("Mark as unread").backgroundColor(
                            color(android.R.color.holo_green_light)
                        )
                    )
                )
                +FormItemNav().title("Swipe left: multiple actions with icon").trailingSwipe(
                    listOf(
                        FormSwipeAction().title("Delete").backgroundColor(
                            color(android.R.color.holo_red_light)
                        ).icon(drawable(R.drawable.ic_delete_white)),
                        FormSwipeAction().title("Archive").backgroundColor(
                            color(android.R.color.holo_blue_light)
                        ).icon(drawable(R.drawable.ic_archive_white)),
                        FormSwipeAction().title("Mark as unread").backgroundColor(
                            color(android.R.color.holo_green_light)
                        ).icon(drawable(R.drawable.ic_markunread_white))
                    )
                )
                +FormItemNav().title("Swipe right").leadingSwipe(
                    listOf(
                        FormSwipeAction().title("Delete").style(FormSwipeAction.Style.Destructive)
                    )
                )
                +FormItemNav().title("Swipe right: with icon").leadingSwipe(
                    listOf(
                        FormSwipeAction().title("Delete").style(FormSwipeAction.Style.Destructive)
                            .icon(drawable(R.drawable.ic_delete_white))
                    )
                )
                +FormItemNav().title("Swipe right: multiple actions").leadingSwipe(
                    listOf(
                        FormSwipeAction().title("Delete").backgroundColor(
                            color(android.R.color.holo_red_light)
                        ),
                        FormSwipeAction().title("Archive").backgroundColor(
                            color(android.R.color.holo_blue_light)
                        ),
                        FormSwipeAction().title("Mark as unread").backgroundColor(
                            color(android.R.color.holo_green_light)
                        )
                    )
                )
                +FormItemNav().title("Swipe right: multiple actions with icon").leadingSwipe(
                    listOf(
                        FormSwipeAction().title("Delete").backgroundColor(
                            color(android.R.color.holo_red_light)
                        ).icon(drawable(R.drawable.ic_delete_white)),
                        FormSwipeAction().title("Archive").backgroundColor(
                            color(android.R.color.holo_blue_light)
                        ).icon(drawable(R.drawable.ic_archive_white)),
                        FormSwipeAction().title("Mark as unread").backgroundColor(
                            color(android.R.color.holo_green_light)
                        ).icon(drawable(R.drawable.ic_markunread_white))
                    )
                )
            }

            +FormItemSection().title("Choice").apply {
                +FormItemSelect().tag("select").title("Select").value("Monday")
                    .selectorTitle("Select day of week")
                    .options(arrayOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday"))
                +FormItemChoice().tag("choice").title("Choice").value("Tuesday")
                    .selectorTitle("Select day of week")
                    .options(arrayOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday"))
                +FormItemPicker().tag("picker").title("Picker").value("Wednesday")
                    .selectorTitle("Select day of week")
                    .options(arrayOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday"))
                +FormItemPickerInline().tag("picker_inline").title("Picker Inline")
                    .value("Thursday")
                    .selectorTitle("Select day of week")
                    .options(arrayOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday"))
                +FormItemMultipleChoice().tag("multiple_choice").title("Multiple Choice")
                    .value("Thursday").selectorTitle("Select day of week")
                    .options(arrayOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday"))
                +FormItemColor().tag("color_choice").title("Color").value("#03a9f4")
                +FormItemColor().tag("color_choice2").title("Color").value("#ff9800")
                    .cornerRadius(5)
            }

            +FormItemSection().title("Separator").apply {
                +FormItemNav().title("No separator").separator(FormItem.Separator.NONE)
                    .iconTitle(drawable(R.drawable.ic_form_info))
                +FormItemNav().title("Ignore icon").separator(FormItem.Separator.IGNORE_ICON)
                    .iconTitle(drawable(R.drawable.ic_form_info))
                +FormItemNav().title("Default").separator(FormItem.Separator.DEFAULT)
                    .iconTitle(drawable(R.drawable.ic_form_info))
            }
            +FormItemSection().title("Custom item").apply {
                +FormItemImage().tag("image").image(R.drawable.image1)
            }
            +FormItemSection().title("Dynamic add/delete").apply {
                +FormItemNav().title("Add item").tag("add_item")
                +FormItemNav().title("Add section").tag("add_section")
            }
        }?.update()

        adapter?.registerViewHolder(
            FormItemImage::class.java,
            R.layout.form_item_image,
            FormImageViewHolder::class.java
        )
    }

    override var onFormItemListener: FormItemCallback? = object : FormItemCallback {
        override fun onValueChanged(item: FormItem) {
            Log.i("onValueChanged", item.toString())
            if (item.tag == "switch_show_date") {
                if (item is FormItemSwitchNative) {
                    adapter?.let { adapter ->
                        val action = adapter.itemBy("sec_date")
                        action?.let {
                            it.hidden = !item.isOn
                            runOnUiThread {
                                adapter.evaluateHidden(it)
                            }
                        }
                    }
                }
            } else if (item.tag == "switch_show_action") {
                if (item is FormItemSwitchNative) {
                    adapter?.let { adapter ->
                        val action = adapter.itemBy("action")
                        action?.let {
                            it.hidden = !item.isOn
                            runOnUiThread {
                                adapter.evaluateHidden(it)
                            }
                        }
                    }
                }
            }
        }

        override fun onItemClicked(item: FormItem, viewHolder: RecyclerView.ViewHolder) {
            Log.i("onItemClicked", item.toString())
            when (item.tag) {
                "add_item" -> {
                    newItemCreated += 1
                    adapter?.apply {
                        val index = indexOf(item)
                        add(
                            item, FormItemNav().title("New item $newItemCreated").trailingSwipe(
                                listOf(
                                    FormSwipeAction().title("Delete")
                                        .style(FormSwipeAction.Style.Destructive)
                                )
                            )
                        )
                    }
                }
                "add_section" -> {
                    newSectionCreated += 1
                    adapter?.apply {
                        val index = indexOf(item)
                        val sec = FormItemSection().title("New section $newSectionCreated").apply {
                            trailingSwipe(
                                listOf(FormSwipeAction().title("Delete").style(FormSwipeAction.Style.Destructive))
                            )
                            +FormItemNav().title("Item 0")
                            +FormItemNav().title("item 1")
                        }
                        add(item, sec)
                    }
                }
            }
        }

        override fun onSwipedAction(
            item: FormItem,
            action: FormSwipeAction,
            viewHolder: RecyclerView.ViewHolder
        ): Boolean {
            super.onSwipedAction(item, action, viewHolder)
            Toast.makeText(this@MainActivity, "${item.title}: ${action.title}", Toast.LENGTH_SHORT)
                .show()
            if (action.title == "Delete") {
                return adapter?.remove(item) ?: false
            }
            return false
        }

        override fun getMinItemHeight(item: FormItem): Int {
            if (item is FormItemSection) {
                return 0
            }
            return 0
        }
    }
}

open class FormItemImage : FormItem() {
    var image: Int = 0
}

fun <T : FormItemImage> T.image(image: Int) = apply {
    this.image = image
}

class FormImageViewHolder(inflater: LayoutInflater, resource: Int, parent: ViewGroup) :
    FormViewHolder(inflater, resource, parent) {
    private var imgView: ImageView? = null

    init {
        imgView = itemView.findViewById(R.id.formELementImage)
    }

    override fun bind(s: FormItem, listener: FormItemCallback?) {

        if (s is FormItemImage) {
            Picasso.get().load(s.image).fit().centerInside().into(imgView)

            imgView?.setOnClickListener {
                listener?.onValueChanged(s)
            }
        }
    }
}