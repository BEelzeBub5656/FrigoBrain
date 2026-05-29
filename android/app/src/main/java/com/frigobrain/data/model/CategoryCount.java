package com.frigobrain.data.model;

import androidx.annotation.NonNull;

public class CategoryCount {
    @NonNull
    public String name;

    public int count;

    public CategoryCount(@NonNull String name, int count) {
        this.name = name;
        this.count = count;
    }
}
