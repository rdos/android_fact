package ru.smartro.worknote.extensions

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners


fun ImageView.loadImage(url: String?) {
    try {
        Glide.with(context)
            .load(url)
            //   .error(R.drawable.img_placeholder_loading)
            //     .thumbnail(Glide.with(this).load(R.drawable.img_placeholder_not_found))
            .fitCenter()
            .transform(RoundedCorners(12))
            .into(this)
    } catch (e: Exception) {
    }

}

fun ImageView.loadImageWithoutCorner(url: String?) {
    try {
        Glide.with(context)
            .load(url)
            //     .error(R.drawable.img_placeholder_loading)
            //     .thumbnail(Glide.with(this).load(R.drawable.img_placeholder_not_found))
            .into(this)
    } catch (e: Exception) {
    }

}