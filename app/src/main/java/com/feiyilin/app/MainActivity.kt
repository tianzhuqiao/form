package com.feiyilin.app

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.Gravity
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

        //settings.add(SettingProfileItem(SETTING_TYPE.SETTING_IMAGE,"image", Profile.image, tag="image"))
        settings.add(FylFormItemSection().title("Text"))
        settings.add(FylFormItemText().title("Text").tag("text"))
        settings.add(FylFormItemText().title("Text").subTitle("here is subtitle").tag("text_subtitle"))
        settings.add(FylFormItemText().title("Text").subTitle("dragable").dragable(true).tag("text_dragable"))
        settings.add(FylFormItemText().title("Profession").tag( "profession"))
        settings.add(FylFormItemText().title("Company").tag("company"))
        settings.add(FylFormItemSection())
        settings.add(FylFormItemNav().badge("1").title("Check my website").subTitle("www.abc.com").tag( "website"))
        settings.add(FylFormItemText().title("Email").tag("email"))
        settings.add(FylFormItemSection().title("BULLETIN BOARD"))
        settings.add(FylFormItemTextArea().placeholder("Tell people what you think ...").tag("notes"))

        settings.add(FylFormItemSection().title("Privacy"))
        settings.add(
            FylFormItemRadio().isOn(true).group("privacy")
                .title("Public: open to chat with anyone")
                .tag("privacy_public"))
        settings.add(FylFormItemRadio().group("privacy").title("Private: need my approval").tag("privacy_private"))

        settings.add(FylFormItemSection().title("More professional exposure of my work"))
        settings.add(FylFormItemSwitch().isOn(true).title("Switch").tag("switch"))
        settings.add(FylFormItemSwitchNative().isOn(true).title("Switch native").tag("switch_native"))

        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)

            adapter = FylFormRecyclerAdaptor ( settings, onSettingProfileItemClickListener).apply {

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