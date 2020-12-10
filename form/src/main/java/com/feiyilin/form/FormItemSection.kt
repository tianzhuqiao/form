package com.feiyilin.form

import java.util.*

open class FormItemSection(private val visible: Boolean=true): FormItem() {
    var collapsed: Boolean = false
    var enableCollapse: Boolean = false
    private var _items: MutableList<FormItem> = mutableListOf()
    val items: List<FormItem>
        get() = _items
    private var _itemsVisible: MutableList<FormItem> = mutableListOf()
    val itemsVisible: List<FormItem>
        get() = _itemsVisible

    internal var adapter: FormRecyclerAdapter? = null

    init {
        if (visible) {
            // add item to list (all non-hidden item in the list is visible)
            +this
        }
        // self always in this section
        section = this
    }

    val sizeVisible: Int
        get() = _itemsVisible.size
    val isEmpty: Boolean
        get() = _itemsVisible.isEmpty()
    val isNotEmpty: Boolean
        get() = _itemsVisible.isNotEmpty()
    fun indexOf(item: FormItem): Int {
        return _itemsVisible.indexOf(item)
    }

    /**
     * get the visible item by index
     * @param index the index of item in visibleItems
     * @return item at index
     */
    operator fun get(index: Int):FormItem {
        require(index in 0 until sizeVisible) { "index out of range" }
        return _itemsVisible[index]
    }

    /**
     * add an non-section item to the section, need to call update() after adding all items
     */
    operator fun FormItem.unaryPlus() {
        if(section != null || indexOf(this) != -1) {
            return
        }
        add(this)
    }

    /**
     * return the item by its tag or null if not found
     * @param tag: tag of the item
     */
    fun itemBy(tag: String): FormItem? {
        return items.firstOrNull { it.tag == tag }
    }

    /**
     * add all visible items to itemsVisible
     */
    internal fun update() {
        updateRadioGroup()
        _itemsVisible.clear()
        if (!hidden) {
            if (collapsed) {
                if (items.firstOrNull() == this) {
                    _itemsVisible.add(items[0])
                }
            } else {
                items.forEach {
                    if (!it.hidden) {
                        _itemsVisible.add(it)
                    }
                }
            }
        }
    }

    internal fun updateRadioGroup() {
        val groupChecked = mutableListOf<String>()
        for (item in items.reversed()) {
            if (item is FormItemRadio) {
                if (item.isOn && item.group.isNotEmpty()) {
                    if (item.group in groupChecked) {
                        item.isOn(false)
                    } else {
                        groupChecked.add(item.group)
                    }
                }
            }
        }
    }

    /**
     * update the radio group
     * @param item the selected item in the radio group
     */
    fun updateRadioGroup(selected: FormItemRadio) {
        if (selected.section != this) {
            return
        }
        selected.isOn(true)
        if (selected.group.isEmpty()) {
            return
        }
        for (item in items) {
            if (item is FormItemRadio && item.group == selected.group) {
                item.isOn(item == selected)
                adapter?.updateItem(item)
            }
        }
    }

    /**
     * select the item in radio group
     * @param item the selected item in the radio group
     */
    fun selectRadioItem(item: FormItemRadio) {
        if (item.section != this) {
            return
        }
        adapter?.let {
            adapter?.selectRadioItem(item)
        }?: run {
            updateRadioGroup(item)
        }
    }

    /**
     * show/hide the item according to its "hidden" field
     * @param item item to be updated
     * @return the offset in itemsVisible if updated, or -1 if it is not in itemsVisible
     */
    private fun update(item: FormItem): Int {
        if (item.section != this) {
            // not belong to this section
            return -1
        }
        val index = indexOf(item)
        if (item.hidden) {
            if (index != -1) {
                _itemsVisible.removeAt(index)
                return index
            }
        } else {
            if (index == -1) {
                val count = offset(item)
                _itemsVisible.add(count, item)
                return count
            }
        }
        return -1
    }

    /**
     * remove item from the section
     * @param item item to be removed
     * @param update update the adapter if it is true
     * @return true if succeed
     */
     fun remove(item: FormItem, update: Boolean=true): Boolean {
        if (item.section != this) {
            return false
        }
        _items.remove(item)
        val index = _itemsVisible.indexOf(item)
        if (index != -1) {
            _itemsVisible.remove(item)
            if (update) {
                adapter?.activity?.runOnUiThread {
                    val start = adapter?.startOfSection(this) ?: -1
                    if (start != -1) {
                        adapter?.notifyItemRemoved(start + index)
                    }
                }
            }
        }
        // reset the item section
        item.section = null

        return true
    }

    /**
     * add an item to this section
     * @param item item to be added
     * @param update update the adapter if true
     * @return true if succeed
     */
    fun add(item: FormItem, update: Boolean=true): Boolean {
        return add(items.size, item, update)
    }

    /**
     * add an item after an existing item
     * @param after item before the new item
     * @param item item to be added
     * @param update update the adapter if true
     * @return true if succeed
     */
    fun add(after: FormItem, item: FormItem, update: Boolean=true): Boolean {
        if (after.section != this) {
            return false
        }
        var index = items.indexOf(after)
        if (index == -1) {
            if (after == this) {
                index = 0
            } else {
                return false
            }
        } else {
            index += 1
        }
        return add(index, item, update)
    }
    /**
     * add an item at given position
     * @param index the position in all items (include the invisible items)
     * @param item item to be added
     * @param update update the adapter if true
     * @return true if succeed
     */
     internal fun add(index: Int, item: FormItem, update: Boolean=true): Boolean {
        if (item is FormItemSection && item != this) {
            Objects.requireNonNull(null, "No embedded section")
            return false
        }

        if (item.section == this) {
            // already in section
            return false
        }
        item.section = this
        _items.add(index, item)

        adapter?.let { adapter ->
            // only update the itemsVisible if adapter is valid; otherwise, the adapter will call
            // update() once the section is added.
            if (!item.hidden && !collapsed) {
                val count = offset(item)
                _itemsVisible.add(count, item)
                if (update) {
                    adapter.activity?.runOnUiThread {
                        val start = adapter.startOfSection(this)
                        if (start != -1) {
                            adapter.notifyItemInserted(start + itemsVisible.indexOf(item))
                        }
                    }
                }
            }
        }
        return true
    }

    /**
     * get the offset of item in itemsVisible
     * @param item the item to get the offset
     * @return offset in itemsVisible
     */
    private fun offset(item: FormItem): Int {
        // get the offset of item in itemsVisible
        var count = 0
        for (child in items) {
            if (child == item) {
                break
            }
            if (!child.hidden) {
                count += 1
            }
        }
        return count
    }

    /**
     * show/hide an item
     * @param item item to be shown/hidden
     * @return true if succeed
     */
    fun hide(item: FormItem, hide: Boolean): Boolean {
        if (item.section != this || item is FormItemSection) {
            return false
        }
        if (item.hidden == hide) {
            return true
        }
        item.hidden = hide
        val offset = update(item)
        if (offset != -1) {
            adapter?.activity?.runOnUiThread {
                val index = adapter?.startOfSection(this) ?: -1
                if (index != -1) {
                    if (item.hidden) {
                        adapter?.notifyItemRemoved(index + offset)
                    } else {
                        adapter?.notifyItemInserted(index + offset)
                    }
                }
            }
        }
        return true
    }

    /**
     * clear all the children
     */
    fun clear() {
        _items.clear()
        if(visible) {
            this.section = this
            _items.add(this)
        }
        adapter?.let {adapter ->
            if (sizeVisible == 0) {
                return
            }
            val index = adapter.indexOf(itemsVisible[0])
            val size = sizeVisible
            update()
            val sizeNew = sizeVisible
            if (size > sizeNew) {
                // the last (size - sizeNew) items are hidden
                adapter.activity?.runOnUiThread {
                    adapter.notifyItemRangeRemoved(index + sizeNew, size - sizeNew)
                }
            }
        }
    }
}

fun <T : FormItemSection> T.collapsed(collapsed: Boolean) = apply {
    this.collapsed = collapsed
}

fun <T : FormItemSection> T.enableCollapse(enable: Boolean = true) = apply {
    this.enableCollapse = enable
}