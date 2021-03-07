package com.example.newrecipe;

import android.os.Parcel;
import android.os.Parcelable;

public class IngredientEntry implements Parcelable {
    private final String amount;
    private final String unit;
    private final String ingredient;

    public IngredientEntry(String amount, String unit, String ingredient) {
        this.amount = amount;
        this.unit = unit;
        this.ingredient = ingredient;
    }

    public String getAmount() {
        return amount;
    }

    public String getUnit() {
        return unit;
    }

    public String getIngredient() {
        return ingredient;
    }

    protected IngredientEntry(Parcel in) {
        amount = in.readString();
        unit = in.readString();
        ingredient = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(amount);
        dest.writeString(unit);
        dest.writeString(ingredient);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<IngredientEntry> CREATOR = new Creator<IngredientEntry>() {
        @Override
        public IngredientEntry createFromParcel(Parcel in) {
            return new IngredientEntry(in);
        }

        @Override
        public IngredientEntry[] newArray(int size) {
            return new IngredientEntry[size];
        }
    };
}
