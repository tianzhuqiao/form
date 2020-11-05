package com.feiyilin.app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.feiyilin.form.FylFormRecyclerAdaptor
import com.feiyilin.form.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {
    private var settings = mutableListOf<FylFormItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.setting_profile_recyclerView)

        val cal = Calendar.getInstance()
        cal.set(2020, 6, 19)

        settings = mutableListOf(
            FylFormItemSection().title("Text").tag("sec_text"),
            FylFormItemText().title("Text").tag("text").required(true),
            FylFormItemText().title("Text").subTitle("here is subtitle").tag("text_subtitle"),
            FylFormItemText().title("Text").subTitle("dragable").dragable(true)
                .tag("text_dragable"),
            FylFormItemText().title("With icon")
                .iconTitle(ContextCompat.getDrawable(this, R.drawable.ic_form_info)).tag("text_icon"),
            FylFormItemText().title("Ready only").tag("read_only").value("www.feiyilin.com")
                .readOnly(true),
            FylFormItemTextFloatingHint().hint("Text with floating hint").tag("text").gravity(Gravity.START),
            FylFormItemText().title("Email").tag("email").inputType(EditorInfo.TYPE_TEXT_VARIATION_EMAIL_ADDRESS),
            FylFormItemText().title("Number").tag("number").inputType(EditorInfo.TYPE_CLASS_NUMBER),
            FylFormItemText().title("Phone").tag("phone").inputType(EditorInfo.TYPE_CLASS_PHONE),
            FylFormItemSection().title("Multi-line text"),
            FylFormItemTextArea().hint("Multi-line text here ...").tag("notes"),
            FylFormItemTextAreaFloatingHint().hint("Multi-line text with floating hint here ...").tag("notes"),

            FylFormItemSection().title("Navigation item"),
            //FylFormItemLabel().title("Label").tag("label"),
            FylFormItemNav().title("Nav item").tag("nav_item"),
            FylFormItemNav().title("Nav item with subtitle").subTitle("www.abc.com")
                .tag("nav_item_subtitle"),
            FylFormItemNav().title("Nav item with badge").tag("nav_item_badge").badge(""),
            FylFormItemNav().title("Nav item with badge and icon").tag("nav_item_badge_icon").badge("")
                .iconTitle(ContextCompat.getDrawable(this, R.drawable.ic_form_info)),
            FylFormItemNav().title("Nav item with number badge").tag("nav_item_badge_num").badge("99").iconSize(32, 32)
                .iconTitle(ContextCompat.getDrawable(this, R.drawable.ic_form_info)),

            FylFormItemSection().title("Radio"),
            FylFormItemRadio().isOn(true).group("radio0")
                .title("item 0")
                .tag("radio0_item0"),
            FylFormItemRadio().group("radio0").title("item 1")
                .tag("radio0_item1"),

            FylFormItemSection().title("Radio native"),
            FylFormItemRadioNative().isOn(true).group("radio1")
                .title("item 0")
                .tag("radio1_item0"),
            FylFormItemRadioNative().group("radio1").title("item 1")
                .tag("radio1_item1"),

            FylFormItemSection().title("Switch"),
            FylFormItemSwitch().isOn(true).title("Switch").tag("switch"),
            FylFormItemSwitchNative().isOn(true).title("Switch native").tag("switch_native"),

            FylFormItemSwitchNative().isOn(true).title("Show action item").tag("switch_show_action"),
            FylFormItemAction().title("Action").tag("action").subTitle("description")
                .iconTitle(ContextCompat.getDrawable(this, R.drawable.ic_form_info)),

            FylFormItemSwitchNative().isOn(true).title("Show date/time section").tag("switch_show_date"),

            FylFormItemSection().title("Date / Time").tag("sec_date"),
            FylFormItemDate().tag("date").title("Date").date(cal.time),
            FylFormItemDate().tag("date_only").title("Date only").date(cal.time).dateOnly(true),
            FylFormItemDate().tag("time_only").title("Time only").date(cal.time).timeOnly(true),

            FylFormItemSection().title("Swipe Action").tag("sec_swipe"),
            FylFormItemNav().title("Swipe left").trailingSwipe(listOf(FylFormSwipeAction().title("Delete").width(400f).icon(ContextCompat.getDrawable(this, R.drawable.ic_delete_white)))),
            FylFormItemNav().title("Swipe right").leadingSwipe(listOf(FylFormSwipeAction().title("Delete").width(400f).icon(ContextCompat.getDrawable(this, R.drawable.ic_delete_white)))),
            FylFormItemNav().title("Swipe left with multiple actions").trailingSwipe(listOf(
                FylFormSwipeAction().title("Delete").backgroundColor(ContextCompat.getColor(this, android.R.color.holo_red_light)),
                FylFormSwipeAction().title("Archive").backgroundColor(ContextCompat.getColor(this, android.R.color.holo_blue_light)),
                FylFormSwipeAction().title("Mark as unread").backgroundColor(ContextCompat.getColor(this, android.R.color.holo_green_light))
            )),
            FylFormItemNav().title("Swipe right with multiple actions").leadingSwipe(listOf(
                FylFormSwipeAction().title("Delete").backgroundColor(ContextCompat.getColor(this, android.R.color.holo_red_light)),
                FylFormSwipeAction().title("Archive").backgroundColor(ContextCompat.getColor(this, android.R.color.holo_blue_light)),
                FylFormSwipeAction().title("Mark as unread").backgroundColor(ContextCompat.getColor(this, android.R.color.holo_green_light))
            )),

            FylFormItemSection().title("Custom item"),
            FylFormItemImage().tag("image").image(R.drawable.image1)
        )

        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)

            adapter = FylFormRecyclerAdaptor(settings, onSettingProfileItemClickListener).apply {
                this.registerViewHolder(
                    FylFormItemImage::class.java,
                    R.layout.form_item_image,
                    FylFormImageViewHolder::class.java
                )
            }
        }
    }

    private var onSettingProfileItemClickListener = object : FlyFormItemCallback {
        override fun onValueChanged(item: FylFormItem) {
            Log.i("onValueChanged", item.toString())
            if (item.tag == "switch_show_date") {
                if (item is FylFormItemSwitchNative) {
                    (setting_profile_recyclerView?.adapter as? FylFormRecyclerAdaptor)?.let { adapter ->
                        val action = adapter.itemByTag(
                                "sec_date"
                            )
                        action?.let {
                            it.hidden = !item.isOn
                            runOnUiThread {
                                adapter.evaluateHidden(it)
                            }
                        }
                    }
                }
            } else if (item.tag == "switch_show_action") {
                if (item is FylFormItemSwitchNative) {
                    (setting_profile_recyclerView?.adapter as? FylFormRecyclerAdaptor)?.let { adapter ->
                        val action = adapter.itemByTag(
                            "action"
                        )
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

        override fun onItemClicked(item: FylFormItem, viewHolder: RecyclerView.ViewHolder) {
            Log.i("onItemClicked", item.toString())
        }

        override fun onSwipeAction(
            item: FylFormItem,
            action: FylFormSwipeAction,
            viewHolder: RecyclerView.ViewHolder
        ) {
            super.onSwipeAction(item, action, viewHolder)
            Toast.makeText(this@MainActivity, "${item.title}: ${action.title}", Toast.LENGTH_SHORT).show()
        }
    }
}

open class FylFormItemImage : FylFormItem() {
    var image: Int = 0
}

fun <T : FylFormItemImage> T.image(image: Int) = apply {
    this.image = image
}

class FylFormImageViewHolder(inflater: LayoutInflater, resource: Int, parent: ViewGroup) :
    FylFormViewHolder(inflater, resource, parent) {
    private var imgView: ImageView? = null

    init {
        imgView = itemView.findViewById(R.id.formELementImage)
    }

    override fun bind(s: FylFormItem, listener: FlyFormItemCallback?) {

        if (s is FylFormItemImage) {
            Picasso.get().load(s.image).fit().centerInside().into(imgView)

            imgView?.setOnClickListener {
                listener?.onValueChanged(s)
            }
        }
    }
}