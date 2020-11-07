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
Add a **RecyclerView** to the activity's layout
```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context="com.feiyilin.app.MainActivity">
    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/setting_profile_recyclerView"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:paddingBottom="16dp"
        android:descendantFocusability="beforeDescendants">

    </androidx.recyclerview.widget.RecyclerView>
</androidx.constraintlayout.widget.ConstraintLayout>
```
Add **FormItem** list to hold all **FormItem**
```kotlin
class MainActivity : AppCompatActivity() {
    private var settings = mutableListOf<FormItem>()
    
    ...
}
```
Add items to the list
```kotlin
class MainActivity : AppCompatActivity() {
    ...
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        settings = mutableListOf(
            FormItemSection().title("Text"),
            FormItemText().title("Text").tag("text"),
            FormItemText().title("Text").subTitle("here is subtitle").tag("text_subtitle"),
            FormItemText().title("Text").subTitle("dragable").dragable(true)
                .tag("text_dragable"),
            FormItemText().title("With icon")
            ...
            )
     }
 ...
 }
 ```
 Initialize **RecyclerView** with **FormRecyclerAdaptor**
```kotlin
class MainActivity : AppCompatActivity() {
    ...
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ...
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)

            adapter = FormRecyclerAdaptor(settings, onSettingProfileItemClickListener).apply {
            }
        }
    }
}
```
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

# Hide/show items
You can dynamically show or hide items by 
1. setting the **hidden** option;
2. call **adapter.evaluateHidden()** to update the **RecyclerView**.

For example
```kotlin
adapter.itemByTag("action")?.let {
    it.hidden = !item.isOn
    runOnUiThread {
       adapter.evaluateHidden(it)
    }
}
```

If the item is a section, it will show/hide the item itself and all its visible children.

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

# Custom item
Design the layout of your item, e.g., **form_item_image.xml**
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

Derive an item from **FormItem**,
```kotlin
open class FormItemImage : FormItem() {
    var image: Int = 0
}

fun <T : FormItemImage> T.image(image: Int) = apply {
    this.image = image
}
```
Derive a view holder class from **FormViewHolder**, and override **bind**
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

Register the item with **registerViewHolder**
```kotlin
class MainActivity : AppCompatActivity() {
    ...
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ...
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)

            adapter = FormRecyclerAdaptor(settings, onSettingProfileItemClickListener).apply {
                this.registerViewHolder(
                    FormItemImage::class.java,
                    R.layout.form_item_image,
                    FormImageViewHolder::class.java
                )
            }
        }
    }
}
```



