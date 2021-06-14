package com.babapanda.rxoperators.utility;

import android.content.Context;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import com.babapanda.rxoperators.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LogAdapter extends ArrayAdapter<String> {
    public LogAdapter(@NonNull @NotNull Context context, List<String> logs) {
        super(context, R.layout.item_log, R.id.item_log, logs);
    }
}
