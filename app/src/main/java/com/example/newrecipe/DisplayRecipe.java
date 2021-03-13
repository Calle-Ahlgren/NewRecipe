package com.example.newrecipe;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DisplayRecipe extends AppCompatActivity implements DeleteDialog.DeleteDialogListener {

    private ConstraintLayout rowLayout;
    private List<IngredientEntry> ingredientEntries;
    private RecipeCard recipeCard;

    private static final int EDIT_RECIPE_REQ = 1;
    public static final int FAVOURITE_CODE = 142;
    public static final String EDIT_RECIPE = "edit recipe";
    public static final String FAVOURITE = "favourite";

    private ImageView recipeImg;
    private TextView recipeName;
    private Boolean favourited;
    private Boolean clickedFavourite;
    private ImageView emptyHeart;
    private ImageView filledHeart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_recipe);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        recipeCard = intent.getParcelableExtra(MainActivity.RECIPE_CARD);

        emptyHeart = findViewById(R.id.not_favourite);
        filledHeart = findViewById(R.id.favourite);

        clickedFavourite = false;
        favourited = recipeCard.getFavourite();

        if (favourited) {
            emptyHeart.setAlpha(0f);
            filledHeart.setAlpha(1f);
        }

        filledHeart.setOnClickListener(v -> favourite());

        recipeImg = findViewById(R.id.image_display);
        recipeName = findViewById(R.id.name_display);

        populateViews(recipeCard);
    }

    private void populateViews(RecipeCard recipeCard) {
        ingredientEntries = recipeCard.getIngredients();
        IngredientEntry entry = ingredientEntries.get(0);

        String prepTime = " " + recipeCard.getPrepTime();
        String cookTime = " " + recipeCard.getCookTime();

        if (!prepTime.equals("")) {
            TextView prepView = findViewById(R.id.prep_time_display);
            findViewById(R.id.prep_time_tag).setVisibility(View.VISIBLE);
            prepView.setVisibility(View.VISIBLE);
            prepView.setText(prepTime);
        }

        if (!cookTime.equals("")) {
            TextView cookView = findViewById(R.id.cook_time_display);
            findViewById(R.id.cook_time_tag).setVisibility(View.VISIBLE);
            cookView.setVisibility(View.VISIBLE);
            cookView.setText(cookTime);
        }

        String imageStr = recipeCard.getRecipeImage();

        if (imageStr != null) {
            recipeImg.setImageURI(Uri.parse(imageStr));
            recipeImg.setVisibility(View.VISIBLE);
        }

        recipeName.setText(recipeCard.getRecipeName());
        ((TextView) findViewById(R.id.ingredient_row)).setText(getString(R.string.ingredient_row_template, entry.toString()));

        ((TextView) findViewById(R.id.description_display)).setText(recipeCard.getDescription());

        int numOfIngredients;

        if ((numOfIngredients = ingredientEntries.size()) > 1) {
            generateIngredientRows(numOfIngredients);
        }
    }

    private void generateIngredientRows(int numOfIngredients) {
        ConstraintLayout recipeCardLayout = findViewById(R.id.display_constraint);
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(recipeCardLayout);
        List<Integer> rowIdList = new ArrayList<>();
        rowIdList.add(R.id.ingredient_row);
        IngredientEntry entry;
        for (int i = 1; i < numOfIngredients; i++) {
            entry = ingredientEntries.get(i);
            TextView ingredientRow = new TextView(this);

            ingredientRow.setText(getString(R.string.ingredient_row_template, entry.toString()));

            ingredientRow.setId(View.generateViewId());
            rowIdList.add(ingredientRow.getId());

            setRowParams(ingredientRow, rowIdList.get(i-1));

            recipeCardLayout.addView(ingredientRow);
        }

        TextView recipeDescriptionTag = findViewById(R.id.prep_time_tag);
        ConstraintLayout.LayoutParams descriptionTagParams = (ConstraintLayout.LayoutParams) recipeDescriptionTag.getLayoutParams();
        descriptionTagParams.topToBottom = rowIdList.get(rowIdList.size() - 1);
        recipeDescriptionTag.setLayoutParams(descriptionTagParams);
    }

    private void setRowParams(TextView inputField, int id) {
        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        params.startToStart = id;
        params.topToBottom = id;

        inputField.setTextSize(TypedValue.COMPLEX_UNIT_SP, 19);
        inputField.setLayoutParams(params);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.recipe_card_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.delete_recipe) {
            openDialog();
            return true;
        } else if (id == R.id.edit_recipe) {
            editRecipe();
            return true;
        } else if (id == android.R.id.home) {
            if (clickedFavourite) {
                setFavourite();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        setFavourite();
    }

    private void setFavourite(){
        Intent resultIntent = new Intent();
        resultIntent.putExtra(FAVOURITE, FAVOURITE_CODE);
        resultIntent.putExtra(EDIT_RECIPE, recipeCard);

        setResult(RESULT_OK, resultIntent);
        finish();
    }

    private void openDialog() {
        DeleteDialog deleteDialog = new DeleteDialog();
        deleteDialog.show(getSupportFragmentManager(), "delete dialog");
    }

    private void editRecipe() {
        Intent intent = new Intent(this, AddNewRecipe.class);
        intent.putExtra(MainActivity.RECIPE_CARD, recipeCard);
        startActivityForResult(intent, EDIT_RECIPE_REQ);
    }

    private void favourite() {
        recipeCard.favourite();
        clickedFavourite = !clickedFavourite;
        if (favourited) {
            emptyHeart.animate().alpha(1).scaleX(1.5f).scaleY(1.5f).setDuration(300)
                    .withEndAction(() -> emptyHeart.animate().scaleX(1f).scaleY(1f).setDuration(250));
            filledHeart.animate().alpha(0).scaleX(1.5f).scaleY(1.5f).setDuration(300);
            Toast.makeText(this, "Recipe removed from Favourites.", Toast.LENGTH_SHORT).show();
        } else {
            emptyHeart.animate().alpha(0).scaleX(1.5f).scaleY(1.5f).setDuration(300);
            filledHeart.animate().alpha(1).scaleX(1.5f).scaleY(1.5f).setDuration(300)
                    .withEndAction(() -> filledHeart.animate().scaleX(1f).scaleY(1f).setDuration(250));

            Toast.makeText(this, "Recipe added to Favourites.", Toast.LENGTH_SHORT).show();
        }

        favourited = !favourited;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == EDIT_RECIPE_REQ) {
                RecipeCard editedRecipeCard = data.getParcelableExtra(MainActivity.NEW_RECIPE);

                Intent resultIntent = new Intent();
                resultIntent.putExtra(EDIT_RECIPE, editedRecipeCard);

                setResult(RESULT_OK, resultIntent);
                finish();
            }
        }
    }

    @Override
    public void onPositiveClick() {
        setResult(RESULT_OK);
        finish();
    }
}