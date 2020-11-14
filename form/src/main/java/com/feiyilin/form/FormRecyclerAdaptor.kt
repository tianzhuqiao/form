package com.feiyilin.form

import android.app.Activity
import android.os.Handler
import android.view.*
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

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

    fun onSwipedAction(item: FormItem, action: FormSwipeAction, viewHolder: RecyclerView.ViewHolder): Boolean { return false }
    fun getMinItemHeight(item: FormItem) : Int { return 0 }
    fun getSeparator(item: FormItem) : FormItem.Separator  { return FormItem.Separator.DEFAULT }
}

open class FormRecyclerAdaptor(
    private var listener: FormItemCallback? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var sections: MutableList<FormItemSection> = mutableListOf()
    private var itemTouchHelper: ItemTouchHelper? = null
    private var recyclerView: RecyclerView? = null

    val activity: Activity?
        get() {
            return recyclerView?.context as? Activity
        }

    class ViewHolderItem(
        var type: Class<out FormItem>,
        var layoutId: Int,
        var viewHolderClass: Class<out FormViewHolder>
    )

    private var viewHolders: MutableList<ViewHolderItem> = mutableListOf()

    init {
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
            ),
            ViewHolderItem(
                FormItemMultipleChoice::class.java,
                R.layout.form_item_choice,
                FormMultipleChoiceViewHolder::class.java
            ),
            ViewHolderItem(
                FormItemColor::class.java,
                R.layout.form_item_color,
                FormColorViewHolder::class.java
            )
        )
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
        val touchHelper =
            object : FormSwipeHelper() {

                override fun getFormItem(pos: Int): FormItem {
                    return itemBy(pos)!!
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
                    itemBy(pos)?.let {item ->
                        var processed = false
                        recyclerView.findViewHolderForAdapterPosition(pos)?.let {
                            processed = onFormItemCallback.onSwipedAction(item, action, it)
                        }
                        if (!processed && indexOf(item) >= 0) {
                            updateItem(pos)
                        }
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
        val s = itemBy(position)

        if (holder is FormViewHolder && s != null) {
            holder.bind(s, onFormItemCallback)
            listener?.onSetup(s, holder)
        }
    }

    override fun getItemCount(): Int {
        return sections.sumBy { it.itemsVisible.size}
    }

    fun setSections(sections: List<FormItemSection>) {
        this.sections = sections.toMutableList()
        update()
    }

    fun update() {
        activity?.runOnUiThread {
            for (item in sections) {
                item.update()
            }
            notifyDataSetChanged()
        }
    }

    override fun getItemViewType(position: Int): Int {
        val m = itemBy(position)!!
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
        val index = indexOf(item)
        if (index != -1 ) {
            activity?.let {
                it.runOnUiThread {
                    notifyItemChanged(index)
                }
            }
        }
    }

    fun updateRadioGroup(item: FormItem) {
        var group = ""
        if (item is FormItemRadio) {
            group = item.group
            item.isOn = true
        } else if (item is FormItemRadioNative) {
            group = item.group
            item.isOn = true
        }
        updateItem(item)
        if (group.isEmpty()) {
            return
        }
        (item.section as? FormItemSection)?.let { sec ->
            for (i in 0 until sec.itemsVisible.size) {
                val child = sec.itemsVisible[i]
                if (child is FormItemRadio && item != child ) {
                    child.isOn = false
                    updateItem(child)
                } else if (child is FormItemRadioNative) {
                    child.isOn = (item == child)
                    updateItem(child)
                }
            }
        }
    }

    private var onFormItemCallback = object : FormItemCallback {
        override fun onValueChanged(item: FormItem) {
            if (item is FormItemRadio) {
                updateRadioGroup(item)
            }
            if (item is FormItemRadioNative) {
                updateRadioGroup(item)
            }
            listener?.onValueChanged(item)
        }

        override fun onItemClicked(item: FormItem, viewHolder: RecyclerView.ViewHolder) {
            val index = indexOf(item)

            Handler().postDelayed({
                //Do something after 100ms
                activity?.runOnUiThread {
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

            val itemSrc = itemBy(src)!!
            val itemDec = itemBy(dest)!!
            if (itemDec == itemSrc.section && dest == 0) {
                // not allow to move before the first section
                return true
            }

            (itemDec.section as? FormItemSection)?.let {
                val offset = it.items.indexOf(itemDec)
                val secSrc = itemSrc.section
                (itemSrc.section as? FormItemSection)?.remove(itemSrc)
                if (it == secSrc) {
                    // src, dest are in same section
                    if (it != itemDec) {
                        it.add(offset, itemSrc)
                    } else {
                        // move to the previous section
                        (itemBy(dest-1)?.section as? FormItemSection)?.add(itemSrc)
                    }
                } else if (dest > src) {
                    it.add(offset + 1, itemSrc)
                } else {
                    it.add(offset, itemSrc)
                }
            }
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
        ): Boolean {
            super.onSwipedAction(item, action, viewHolder)
            return listener?.onSwipedAction(item, action, viewHolder) ?: false
        }

        override fun getMinItemHeight(item: FormItem): Int {
            return listener?.getMinItemHeight(item) ?: 0
        }

        override fun getSeparator(item: FormItem): FormItem.Separator {
            return listener?.getSeparator(item) ?: super.getSeparator(item)
        }
    }

    fun itemBy(tag: String): FormItem? {
        var item: FormItem? = null
        for (sec in sections) {
            if (sec.tag == tag) {
                item = sec
                break
            }
            item = sec.itemBy(tag)
            if (item != null) {
                break
            }
        }
        return item
    }

    fun itemBy(index: Int): FormItem? {
        var item : FormItem? = null
        var count = 0
        for (sec in sections) {
            if (index < count + sec.itemsVisible.size) {
                item = sec.itemsVisible[index - count]
                break
            }
            count += sec.itemsVisible.size
        }
        return item
    }

    fun indexOf(item: FormItem) : Int {
        var count = 0
        for (sec in sections) {
            if (sec == item.section) {
                val index = sec.itemsVisible.indexOf(item)
                if (index != -1) {
                    return index + count
                }
                break
            }
            count += sec.itemsVisible.size
        }
        return -1
    }

    fun hideSection(item: FormItemSection, hide: Boolean) {
        // show/hide all visible children of a section (not the section item itself)
        var found = false
        var index = 0
        for (sec in sections) {
            if (sec == item) {
                found = true
                break
            }
            index += sec.itemsVisible.size
        }
        if (!found) {
            return
        }
        if (hide) {
            val end = index + item.itemsVisible.size
            item.update()
            val start = index + item.itemsVisible.size
            notifyItemRangeRemoved(start, end-start)
        } else {
            val start = index + item.itemsVisible.size
            item.update()
            val end = index + item.itemsVisible.size
            notifyItemRangeInserted(start, end-start)
        }
    }

    fun evaluateHidden(item: FormItem): Boolean {
        if (item is FormItemSection) {
            hideSection(item, item.hidden)
        } else {
            (item.section as? FormItemSection)?.let { sec ->
                val offset = sec.update(item)
                if (offset != -1) {
                    val index = indexOf(sec)
                    if (item.hidden) {
                        notifyItemRemoved(index + offset)
                    } else {
                        notifyItemInserted(index + offset)
                    }
                }
            }
        }
        return true
    }

    operator fun FormItem.unaryPlus() {
        if (this is FormItemSection) {
            sections.add(this)
        } else {
            val sec: FormItemSection
            if (sections.isEmpty()) {
                // add a dummy section
                sec = FormItemSection(false)
                sections.add(sec)
            } else {
                sec = sections.last()
            }
            sec.apply {
                +this@unaryPlus
            }
        }
    }

    fun findItem (item: FormItem): Pair<FormItemSection?, Int> {
        val sec = item.section as? FormItemSection
        var index = -1
        if (sec != null) {
            index = sec.items.indexOf(item)
        }
        return Pair(sec, index)
    }

    fun has(item: FormItem): Boolean {
        for (it in sections) {
            if (it == item) {
                return true
            }
            if (it.items.indexOf(item) != -1) {
                return true
            }
        }
        return false
    }

    fun add(item: FormItemSection) {
        if (sections.indexOf(item) != -1) {
            return
        }
        val start = itemCount
        sections.add(item)
        item.update()
        val end = itemCount
        if (end > start) {
            activity?.runOnUiThread {
                notifyItemRangeInserted(start, end - start)
            }
        }
    }

    fun remove(item: FormItemSection) {
        val index = indexOf(item)
        if (index > -1) {
            sections.remove(item)
            if (item.itemsVisible.size > 0) {
                activity?.runOnUiThread {
                    notifyItemRangeRemoved(index, item.itemsVisible.size)
                }
            }
        }
    }
}