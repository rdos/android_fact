package ru.smartro.worknote

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build

fun getActivityProperly(
    context: Context,
    requestCode: Int,
    intent: Intent,
    flags: Int
): PendingIntent = 
    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
        PendingIntent.getActivity(context, requestCode, intent, flags or PendingIntent.FLAG_IMMUTABLE)
    else
        PendingIntent.getActivity(context, requestCode, intent, flags)