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
    private var itemsAll: MutableList<FormItem>,
    private var listener: FormItemCallback? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var itemTouchHelper: ItemTouchHelper? = null
    private var recyclerView: RecyclerView? = null
    private var itemsVisible = mutableListOf<FormItem>()

    class ViewHolderItem(
        var type: Class<out FormItem>,
        var layoutId: Int,
        var viewHolderClass: Class<out FormViewHolder>
    )

    private var viewHolders: MutableList<ViewHolderItem> = mutableListOf()

    init {
        setItems(itemsAll)
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
                    return itemsVisible[pos]
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
                    val item = itemsVisible[pos]
                    var processed = false
                    recyclerView.findViewHolderForAdapterPosition(pos)?.let {
                        processed = onFormItemCallback.onSwipedAction(item, action, it)
                    }
                    if (!processed && itemsVisible.indexOf(item) >= 0) {
                        updateItem(pos)
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

        val s = itemsVisible[position]
        if (holder is FormViewHolder) {
            holder.bind(s, onFormItemCallback)
            listener?.onSetup(s, holder)
        }
    }

    override fun getItemCount(): Int {
        return itemsVisible.size
    }

    fun setItems(items: List<FormItem>) {
        this.itemsAll = items.toMutableList()
        update()
    }

    fun update() {
        this.itemsVisible.clear()
        var hideSection = false
        for (item in this.itemsAll) {
            if (item is FormItemSection) {
                hideSection = item.hidden
            }
            if (item.hidden || hideSection) {
                continue
            }
            itemsVisible.add(item)
        }
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        val m = itemsVisible[position]
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
        val index = itemsVisible.indexOf(item)
        if (index >= 0 && index < itemsVisible.size) {
            val activity = recyclerView?.context as? Activity
            activity?.let {
                it.runOnUiThread {
                    notifyItemChanged(index)
                }
            }
        }
    }

    fun updateRadioGroup(group: String, selected: String) {
        for (i in 0 until itemsVisible.size) {
            val item = itemsVisible[i]
            if (item is FormItemRadio && item.group == group) {
                item.isOn = (item.tag == selected)
                updateItem(item)
            } else if (item is FormItemRadioNative && item.group == group) {
                item.isOn = (item.tag == selected)
                updateItem(item)
            }
        }
    }

    private var onFormItemCallback = object : FormItemCallback {
        override fun onValueChanged(item: FormItem) {
            if (item is FormItemRadio) {
                updateRadioGroup(item.group, item.tag)
            }
            if (item is FormItemRadioNative) {
                updateRadioGroup(item.group, item.tag)
            }
            listener?.onValueChanged(item)
        }

        override fun onItemClicked(item: FormItem, viewHolder: RecyclerView.ViewHolder) {
            val index = itemsVisible.indexOf(item)

            Handler().postDelayed({
                val act = recyclerView?.context as? Activity
                //Do something after 100ms
                act?.runOnUiThread {
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

            val orgSrc = itemsAll.indexOf(itemsVisible[src])
            val orgDest = itemsAll.indexOf(itemsVisible[dest])
            var item = itemsAll.removeAt(orgSrc)
            itemsAll.add(orgDest, item)
            item = itemsVisible.removeAt(src)
            itemsVisible.add(dest, item)

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

    fun itemByTag(tag: String): FormItem? {
        return itemsAll.firstOrNull { it.tag == tag }
    }

    fun hideSection(item: FormItemSection, hide: Boolean) {
        // show/hide all visible children of a section (not the section item itself)
        var idx = itemsVisible.indexOf(item)
        if (idx == -1) {
            return
        }
        val orgIdx = itemsAll.indexOf(item)
        if (orgIdx == -1) {
            return
        }
        if (hide) {
            idx += 1
            while (itemsVisible.size > idx) {
                val child = itemsVisible[idx]
                if (child is FormItemSection) {
                    // start the next section
                    break
                }
                // hide the child
                itemsVisible.removeAt(idx)
                notifyItemRemoved(idx)
            }
        } else {
            var orgChildIdx = orgIdx + 1
            while (orgChildIdx < itemsAll.size) {
                val child = itemsAll[orgChildIdx]
                orgChildIdx += 1
                if (child is FormItemSection) {
                    // start the next section
                    break
                }
                if (child.hidden) {
                    continue
                }
                // show the child
                idx += 1
                itemsVisible.add(idx, child)
                notifyItemInserted(idx)
            }
        }
    }

    fun evaluateHidden(item: FormItem): Boolean {
        val orgIdx = itemsAll.indexOf(item)
        if (orgIdx == -1) {
            return false
        }

        var idx = itemsVisible.indexOf(item)
        if (item.hidden) {
            if (idx != -1) {
                if (item is FormItemSection) {
                    // if item is section, hide all its children
                    hideSection(item, true)
                }
                itemsVisible.removeAt(idx)
                notifyItemRemoved(idx)
            }
        } else {
            if (idx == -1) {
                // find the corresponding index in settingVisible
                idx = 0
                for (tmp in itemsAll) {
                    if (item == tmp) {
                        break
                    }
                    if (!tmp.hidden) {
                        idx += 1
                    }
                }
                itemsVisible.add(idx, item)
                notifyItemInserted(idx)

                if (item is FormItemSection) {
                    // if item is section, show all its children
                    hideSection(item, false)
                }
            }
        }
        return true
    }

    operator fun FormItem.unaryPlus() {
        itemsAll.add(this)
    }
}