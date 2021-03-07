package com.example.newrecipe;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class RecipeCard implements Parcelable {
    private final List<IngredientEntry> ingredientEntries;
    private final String name;
    private final String description;
    private final String prepTime;
    private final String cookTime;
    private final String image;
    private Boolean favourited;

    private RecipeCard(RecipeBuilder builder) {
        favourited = false;
        this.name = builder.name;
        this.image = builder.image;
        this.ingredientEntries = builder.ingredientEntries;
        this.prepTime = builder.prepTime;
        this.cookTime = builder.cookTime;
        this.description = builder.description;
    }

    public static RecipeBuilder builder() {
        return new RecipeBuilder();
    }

    public static class RecipeBuilder {
        private List<IngredientEntry> ingredientEntries;
        private String name;
        private String description;
        private String prepTime;
        private String cookTime;
        private String image;

        private RecipeBuilder () {}

        public RecipeBuilder name(String name) {
            this.name = name;
            return this;
        }

        public RecipeBuilder image(String image) {
            this.image = image;
            return this;
        }

        public RecipeBuilder ingredients(List<IngredientEntry> ingredientEntries) {
            this.ingredientEntries = ingredientEntries;
            return this;
        }

        public RecipeBuilder prepTime(String prepTime) {
            this.prepTime = prepTime;
            return this;
        }

        public RecipeBuilder cookTime(String cookTime) {
            this.cookTime = cookTime;
            return this;
        }

        public RecipeBuilder description(String description) {
            this.description = description;
            return this;
        }

        public RecipeCard build() {
            return new RecipeCard(this);
        }
    }

    protected void favourite() {
        favourited = !favourited;
    }

    protected Boolean getFavourite() { return favourited; }

    protected String getRecipeName() {
        return name;
    }

    protected String getRecipeImage() {
        return image;
    }

    protected List<IngredientEntry> getIngredients() {
        return ingredientEntries;
    }

    protected String getPrepTime() { return prepTime; }

    protected String getCookTime() { return cookTime; }

    public String getDescription()  {
        return description;
    }

    protected RecipeCard(Parcel in) {
        favourited = (in.readInt() != 0);
        image = in.readString();
        description = in.readString();
        name = in.readString();
        prepTime = in.readString();
        cookTime = in.readString();
        ingredientEntries = new ArrayList<>();
        in.readTypedList(ingredientEntries, IngredientEntry.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt((favourited ? 1 : 0));
        dest.writeString(image);
        dest.writeString(description);
        dest.writeString(name);
        dest.writeString(prepTime);
        dest.writeString(cookTime);
        dest.writeTypedList(ingredientEntries);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<RecipeCard> CREATOR = new Creator<RecipeCard>() {
        @Override
        public RecipeCard createFromParcel(Parcel in) {
            return new RecipeCard(in);
        }

        @Override
        public RecipeCard[] newArray(int size) {
            return new RecipeCard[size];
        }
    };
}
