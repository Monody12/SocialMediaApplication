package com.example.socialmediaapplication.view

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop

/**
 * 自定义ImageView
 */
class MyImageView : AppCompatImageView {
    constructor(context: Context?) : super(context!!)
    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!, attrs
    )

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context!!, attrs, defStyleAttr
    ) {

    }

    companion object {
        /**
         * 自定义属性
         * requireAll = false 表示只要有这两个参数之一，就会调用这个方法
         */
        @BindingAdapter(value = ["image_url", "isCircle"], requireAll = false)
        fun setImageUrl(view: MyImageView, imageUrl: String, isCircle: Boolean) {
            val builder = Glide.with(view).load(imageUrl)
            if (isCircle) {
                builder.transform(CircleCrop())
            }
            val layoutParams = view.layoutParams
            if (layoutParams != null  && layoutParams.width > 0 && layoutParams.height > 0) {
                builder.override(layoutParams.width, layoutParams.height)
            }
            builder.into(view)
        }
    }
}