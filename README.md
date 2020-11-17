# Form: form builder in Kotlin for Android

<img src="./images/image1.png" width="22%"> <img src="./images/image4.png" width="22%"> <img src="./images/image3.png" width="22%"> <img src="./images/image2.png" width="22%">

# Usage
## 1. Add to project
Add **jcenter()** to repository in your project's build.gradle:
```gradle
allprojects {
    repositories {
        ...
        jcenter()
    }
}
```

Add **form** to dependencies in your app's build.gradle: 
```gradle
dependencies {
    ...
    implementation 'com.feiyilin:form:0.2'
}
```
## 2. Update Activity
1. Add **FormItem** to the adapter

```kotlin
class MainActivity : FormActivity() {
    ...
    override fun initForm() {
        adapter?.apply {
            +FormItemSection().title("Text").tag("sec_text").apply {
                enableCollapse(true)
                +FormItemText().title("Text").tag("text").required(true)
                +FormItemText().title("Text").subTitle("here is subtitle").tag("text_subtitle")
                +FormItemText().title("Text").subTitle("dragable").dragable(true)
                    .tag("text_dragable")
                ...
            }
        }
     }
...
}
```
2. override the callbacks
```kotlin
class MainActivity : FormActivity() {
    ...
    override var onFormItemListener: FormItemCallback? = object : FormItemCallback {
        override fun onValueChanged(item: FormItem) {
        ...
        }
    }
    ...
}
```
Or check [FormActivity](./form/src/main/java/com/feiyilin/form/FormActivity.kt) if you want to use **FormRecyclerAdapter** directly in the activity.

# Using the callbacks
**FormItemCallback** can be used to change the appearance and behavior of an item

* **onSetup**

    Called when the item is configured.
    
* **onValueChanged**

    Called when the value of an item changes.
    
* **onItemClicked**

    Called when an item is clicked.
    
* **onTitleImageClicked**

    Called when the title icon is clicked
    
* **onStartReorder**

    Called before moving/reordering an item. Return **true** from the callback to disable the default action.

* **onMoveItem**

    Called before finishing moving an item. Return **true** from the callback to disable the default action.

* **onSwipedAction**

    Called when a swipe action is triggered.

* **getMinItemHeight**

    Called when configure/bind an item. Can be used to update the minimum height for all (or a group of) items.

# Collapse section
To collapse/expand a section (show/hide its children),
1. enable collapse/expand on the section, which will also show an indicator icon 
```kotlin
section.enableCollapse(true)
```
2. collapse/expand the section by calling "adapter.collapse"
```kotlin
override fun onItemClicked(item: FormItem, viewHolder: RecyclerView.ViewHolder) {
    if (item is FormItemSection) {
        if (item.enableCollapse) {
            adapter?.collapse(item, !item.collapsed)
        }
    }
...
}
```
<img src="./images/collapse.gif" width="36%">

# Hide/show item/section
To dynamically show or hide item/section, call
1. **section.hide** to hide an item in the section.
2. **adapter.hide** to hide the whole section (section item and all its visible children)

For example
```kotlin
// hide item
adapter.itemBy("action")?.let {
    it.section?.hide(it, true)
}
// hide section
adapter.sectionBy("sec_date")?.let {
    adapter.hide(it, true)
}
```

<img src="./images/hide.gif" width="36%">

# Swipe actions
For each item, we can define the leading/left or trailing/right swipe actions (following the idea [here](https://stackoverflow.com/questions/44965278/recyclerview-itemtouchhelper-buttons-on-swipe/45062745#45062745)).
 For example
```kotlin
 FormItemNav().title("Swipe left with multiple actions").trailingSwipe(listOf(
                FormSwipeAction().title("Delete")
                                    .backgroundColor(ContextCompat.getColor(this, android.R.color.holo_red_light)),
                FormSwipeAction().title("Archive")
                                    .backgroundColor(ContextCompat.getColor(this, android.R.color.holo_blue_light)),
                FormSwipeAction().title("Mark as unread")
                                    .backgroundColor(ContextCompat.getColor(this, android.R.color.holo_green_light))
            )),
```
Once an action is triggered, **onSwipedAction** callback will be called
```kotlin
    override fun onSwipedAction(
            item: FormItem,
            action: FormSwipeAction,
            viewHolder: RecyclerView.ViewHolder
        ) {
            super.onSwipedAction(item, action, viewHolder)
            Toast.makeText(this@MainActivity, "${item.title}: ${action.title}", Toast.LENGTH_SHORT).show()
        }
```

<img src="./images/swipe.gif" width="36%">

# Built-in items
|      |      |
|------|------|
|Text|<img src="./images/item_text.png" width="36%">|
|Text area|<img src="./images/item_textarea.png" width="36%">|
|Switch|<img src="./images/item_switch.png" width="36%">|
|Radio|<img src="./images/item_radio.png" width="36%">|
|Nav|<img src="./images/item_nav.png" width="36%">|
|Action|<img src="./images/item_action.png" width="36%">|
|Date|<img src="./images/item_date.png" width="36%">|
|Select|<img src="./images/item_select.png" width="22%"> <img src="./images/item_choice.png" width="22%"> <img src="./images/item_picker.png" width="22%">|
|Picker inline|<img src="./images/item_picker_inline.png" width="36%">|
|Color|<img src="./images/item_color.png" width="36%">|

# Custom item
1. Design the layout of your item, e.g., **form_item_image.xml**
```xml
<?xml version="1.0" encoding="utf-8"?>

<androidx.constraintlayout.widget.ConstraintLayout
        xmlns:android="http://schemas.android.com/apk/res/android"
              xmlns:app="http://schemas.android.com/apk/res-auto"
              xmlns:tools="http://schemas.android.com/tools"
              android:layout_width="match_parent"
              android:layout_height="wrap_content"
              android:orientation="vertical">

    <androidx.cardview.widget.CardView
            android:id="@+id/profile_image_wrap"
            app:cardCornerRadius="63dp"
            android:layout_width="126dp"
            android:layout_height="126dp"
            android:layout_marginTop="9dp"
            android:layout_marginBottom="9dp"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintTop_toTopOf="parent"
            android:layout_gravity="center_horizontal"
            app:cardBackgroundColor="#00FFFFFF">
        <ImageView
                android:id="@+id/formELementImage"
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                android:adjustViewBounds="true"
                app:srcCompat="@drawable/form_image_placeholder"
                android:scaleType="fitCenter"/>
    </androidx.cardview.widget.CardView>

    <View
            android:layout_width="match_parent"
            android:layout_height="0.5dp"
            android:layout_marginTop="16dp"
            app:layout_constraintTop_toBottomOf="@+id/profile_image_wrap"
            android:background="#FFE0E0E0"/>
</androidx.constraintlayout.widget.ConstraintLayout>
```

2. Derive an item from **FormItem**,
```kotlin
open class FormItemImage : FormItem() {
    var image: Int = 0
}

fun <T : FormItemImage> T.image(image: Int) = apply {
    this.image = image
}
```

3. Derive a view holder class from **FormViewHolder**, and override **bind**
```kotlin
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
```

4. Register the item with **registerViewHolder**
```kotlin
class MainActivity : FormActivity() {
    ...
    override fun initForm() {
        ...
        adapter?.registerViewHolder(
            FormItemImage::class.java,
            R.layout.form_item_image,
            FormImageViewHolder::class.java
        )
        ...
    }
}
```
```



