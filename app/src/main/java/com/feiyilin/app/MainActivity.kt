package com.feiyilin.app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.feiyilin.form.FylFormRecyclerAdaptor
import com.feiyilin.form.*
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
            FylFormItemSection().title("Text"),
            FylFormItemText().title("Text").tag("text"),
            FylFormItemText().title("Text").subTitle("here is subtitle").tag("text_subtitle"),
            FylFormItemText().title("Text").subTitle("dragable").dragable(true)
                .tag("text_dragable"),
            FylFormItemText().title("Profession")
                .iconTitle(resources.getDrawable(R.drawable.ic_form_info)).tag("profession"),
            FylFormItemText().title("Company").tag("company"),
            FylFormItemText().title("Ready only").tag("read_only").value("www.feiyilin.com")
                .readOnly(true),
            FylFormItemLabel().title("Label").tag("label"),
            FylFormItemSection(),
            FylFormItemNav().title("Check my website").subTitle("www.abc.com")
                .tag("website").badge("1")
                .iconTitle(resources.getDrawable(R.drawable.ic_form_info)),
            FylFormItemText().title("Email").tag("email"),
            FylFormItemSection().title("BULLETIN BOARD"),
            FylFormItemTextArea().placeholder("Tell people what you think ...").tag("notes"),

            FylFormItemSection().title("Radio"),

            FylFormItemRadio().isOn(true).group("privacy")
                .title("Public: open to chat with anyone")
                .tag("privacy_public"),
            FylFormItemRadio().group("privacy").title("Private: need my approval")
                .tag("privacy_private"),
            FylFormItemSection().title("Switch"),
            FylFormItemSwitch().isOn(true).title("Switch").tag("switch"),
            FylFormItemSwitchNative().isOn(true).title("Switch native").tag("switch_native"),
            FylFormItemAction().title("Action").tag("action").subTitle("description")
                .iconTitle(resources.getDrawable(R.drawable.ic_form_info)),
            FylFormItemImage().tag("image").image(R.drawable.image1),
            FylFormItemDate().tag("date").title("Date").date(cal.time),
            FylFormItemDate().tag("date_only").title("Date only").date(cal.time).dateOnly(true),
            FylFormItemDate().tag("time_only").title("Time only").date(cal.time).timeOnly(true)
        )
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)

            adapter = FylFormRecyclerAdaptor(settings, onSettingProfileItemClickListener).apply {
                this.registerViewHolder(
                    "image",
                    R.layout.form_item_image,
                    FylFormImageViewHolder::class.java
                )
            }
        }
    }

    fun updateItem(item: FylFormItem) {
        val index = settings.indexOf(item)
        if (index >= 0 && index < settings.size) {
            this.runOnUiThread {
                val adapter = setting_profile_recyclerView.adapter as? FylFormRecyclerAdaptor
                adapter?.notifyItemChanged(index)
            }
        }
    }

    private var onSettingProfileItemClickListener = object : FlyFormItemCallback {
        override fun onValueChanged(item: FylFormItem) {

        }

        override fun onItemClicked(item: FylFormItem, viewHolder: RecyclerView.ViewHolder) {

        }
    }
}

open class FylFormItemImage() : FylFormItem(type = "image") {
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
            imgView?.setImageResource(s.image)

            imgView?.setOnClickListener {
                listener?.onValueChanged(s)
            }
        }
    }
}