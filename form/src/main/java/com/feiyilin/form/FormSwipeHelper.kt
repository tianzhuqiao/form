package com.feiyilin.form

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.text.TextPaint
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

abstract class FormSwipeHelper: ItemTouchHelper.SimpleCallback(ItemTouchHelper.DOWN or ItemTouchHelper.UP, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

    // the current item under swiping
    var swipeingPosition = -1
    // the latest item get swiped
    var swipedPosition = -1
    var swipedDirection = ItemTouchHelper.LEFT
    var endSwipedOffset : Float = -1.0f
    var closedSwipe = false

    abstract fun getFormItem(pos: Int) : FormItem

    abstract fun updateItem(pos: Int)

    fun dpToPx(dp: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            Resources.getSystem().displayMetrics
        ).toInt()
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        super.onSelectedChanged(viewHolder, direction)
        swipedPosition = viewHolder.adapterPosition
        swipedDirection = direction

        if (swipedPosition >= 0) {
            val item = getFormItem(swipedPosition)
            val actions = if (swipedDirection == ItemTouchHelper.LEFT) {
                item.trailingSwipe
            } else {
                item.leadingSwipe
            }
            if (isDestructive(actions)) {
                onActionClicked(swipedPosition, actions[0])
            }
        }
    }

    fun isDestructive(actions: List<FormSwipeAction>): Boolean {
        return actions.size == 1 && actions[0].style == FormSwipeAction.Style.Destructive
    }

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        var flag = super.getMovementFlags(recyclerView, viewHolder)
        val position = viewHolder.adapterPosition
        val item = getFormItem(position)
        if (item.leadingSwipe.isEmpty()) {
            flag = flag xor ItemTouchHelper.Callback.makeFlag(
                ItemTouchHelper.ACTION_STATE_SWIPE,
                ItemTouchHelper.RIGHT
            )
        }
        if (item.trailingSwipe.isEmpty()) {
            flag = flag xor ItemTouchHelper.Callback.makeFlag(
                ItemTouchHelper.ACTION_STATE_SWIPE,
                ItemTouchHelper.LEFT
            )
        }
        return flag
    }

    override fun isLongPressDragEnabled(): Boolean {
        return false
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        super.onSelectedChanged(viewHolder, actionState)
        when (actionState) {
            ItemTouchHelper.ACTION_STATE_SWIPE -> {
                viewHolder?.adapterPosition?.let {
                    // start swiping an item
                    swipeingPosition = it

                    endSwipedOffset = -1.0f
                    closedSwipe = false

                    if (swipedPosition != swipeingPosition) {
                        // restore the previous swiped item
                        updateItem(swipedPosition)
                        swipedPosition = -1
                    }
                }
            }
            ItemTouchHelper.ACTION_STATE_IDLE -> {
                if (endSwipedOffset == 0.0f && swipedPosition == swipeingPosition) {
                    // swipe back to original place, restore the item
                    closedSwipe = true
                    updateItem(swipedPosition)
                    swipedPosition = -1
                }
                endSwipedOffset = -1.0f
            }
        }
    }

    fun getActionsWidth(actions: List<FormSwipeAction>, context: Context) : Float {
        var width = 0.0f
        for (action in actions) {
            if (action.width == 0f) {
                val paint = Paint()
                paint.textSize = action.textSize * context.resources.displayMetrics.density
                paint.typeface = Typeface.DEFAULT_BOLD
                paint.textAlign = Paint.Align.LEFT
                val titleBounds = Rect()
                paint.getTextBounds(action.title, 0, action.title.length, titleBounds)
                var actionWidth = titleBounds.width()
                action.icon?.let {
                    val size = dpToPx(action.iconSize)
                    actionWidth = max(size, actionWidth)
                }
                action.width = actionWidth + 2 * action.padding
            }
            width += action.width
        }
        return width
    }

    fun drawAction(action: FormSwipeAction, canvas: Canvas, context: Context, alignLeft: Boolean=true) {
        val paint = TextPaint()

        val rect = action.rect
        // Draw background
        paint.color = action.backgroundColor
        canvas.drawRect(rect, paint)

        // Draw title
        paint.color = ContextCompat.getColor(context, android.R.color.white)
        paint.textSize = action.textSize * context.resources.displayMetrics.density
        paint.typeface = Typeface.DEFAULT
        paint.textAlign = Paint.Align.LEFT
        paint.isAntiAlias = true

        val text = action.title
        val titleBounds = Rect()
        paint.getTextBounds(text, 0, text.length, titleBounds)

        var leftText = rect.left + action.padding
        if (!alignLeft) {
            leftText = rect.right - titleBounds.width() - action.padding
        }
        action.icon?.let {
            val size = dpToPx(action.iconSize)
            val height = size + titleBounds.height() + 5

            val bottomText = rect.height() / 2 + titleBounds.height() / 2 - titleBounds.bottom + size/2 + 5

            canvas.drawText(text, leftText, rect.top + bottomText, paint)

            val leftIcon = (leftText + titleBounds.width() / 2 - size / 2).toInt()
            val topIcon = (rect.top + rect.height() / 2 - height / 2).toInt()
            it.setBounds(leftIcon, topIcon, leftIcon + size, topIcon + size)
            it.draw(canvas)
        } ?: run {
            val bottomText = rect.height() / 2 + titleBounds.height() / 2 - titleBounds.bottom
            canvas.drawText(text, leftText, rect.top + bottomText, paint)
        }
    }
    
    fun drawActions(actions: List<FormSwipeAction>, canvas: Canvas, itemView: View, dX: Float, rightEnd: Boolean=true) {
        val intrinsicWidth = getActionsWidth(actions, itemView.context)
        if (intrinsicWidth == 0f || dX == 0f) {
            return
        }
        if (rightEnd) {
            var right = itemView.right
            actions.forEach { action ->
                val width = action.width /intrinsicWidth * abs(dX)
                val left = right - width
                action.rect = RectF(left, itemView.top.toFloat(), right.toFloat(), itemView.bottom.toFloat())
                right = left.toInt()
            }
            actions.reversed().forEach { action ->
                drawAction(action, canvas, itemView.context, !isDestructive(actions))
            }
        } else {
            var left = 0f
            actions.forEach { action ->
                val width = action.width / intrinsicWidth * abs(dX)
                action.rect = RectF(left, itemView.top.toFloat(), left + width, itemView.bottom.toFloat())
                drawAction( action, canvas, itemView.context)
                left += width
            }
        }

    }

    override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float,
                             dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        var maxDX = dX
        val position = viewHolder.adapterPosition
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE && position == swipeingPosition) {
            val itemView = viewHolder.itemView
            if (dX < 0) {
                // swipe to left
                val actions = getFormItem(position).trailingSwipe
                if (actions.isNotEmpty()) {
                    if (!isDestructive(actions)) {
                        val minSwipedOffset = -recyclerView.width.toFloat()
                        val actionsWidth = getActionsWidth(actions, recyclerView.context)
                        maxDX = max(-actionsWidth, dX)
                        if (swipedPosition == swipeingPosition) {
                            // the current item is swiped, the user is trying to close it
                            maxDX = max(maxDX, -actionsWidth - (minSwipedOffset - dX))
                            maxDX = min(0f, maxDX)
                            endSwipedOffset = maxDX
                        }
                        if (closedSwipe) {
                            // we have un-swiped to the original pos, keep it there to avoid flashing
                            maxDX = 0f
                        }
                    }
                    drawActions(actions, c, itemView, maxDX)
                }
            }
            if (dX > 0) {
                // swipe to right
                val actions = getFormItem(position).leadingSwipe
                if (actions.isNotEmpty()) {
                    if (!isDestructive(actions)) {
                        val maxSwipedOffset = recyclerView.width.toFloat()
                        val actionsWidth = getActionsWidth(actions, recyclerView.context)
                        maxDX = min(actionsWidth, dX)
                        if (swipedPosition == swipeingPosition) {
                            // the current item is swiped, the user is trying to close it
                            maxDX = min(maxDX, actionsWidth - (maxSwipedOffset - dX))
                            maxDX = max(0f, maxDX)
                            endSwipedOffset = maxDX
                        }
                        if (closedSwipe) {
                            // we have un-swiped to the original pos, keep it there to avoid flashing
                            maxDX = 0f
                        }
                    }
                    drawActions(actions, c, itemView, maxDX, false)
                }
            }
        }
        super.onChildDraw(c, recyclerView, viewHolder, maxDX, dY, actionState, isCurrentlyActive)
    }

    open fun onActionClicked(pos: Int, action: FormSwipeAction) {
    }

    @SuppressLint("ClickableViewAccessibility")
    open fun attachToRecyclerView(recyclerView: RecyclerView) {
        recyclerView.setOnTouchListener {_, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                if (swipedPosition >= 0) {
                    val item = getFormItem(swipedPosition)
                    val actions = if (swipedDirection == ItemTouchHelper.LEFT) {
                        item.trailingSwipe
                    } else {
                        item.leadingSwipe
                    }
                    for (action in actions) {
                        if (action.rect.contains(event.x, event.y)) {
                            onActionClicked(swipedPosition, action)
                            break
                        }
                    }
                }
            }
            false
        }
    }
}