package com.example.newrecipe;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecipeAdapter adapter;

    private ArrayList<RecipeCard> recipeCardList;

    private Comparator<RecipeCard> alphabetical;

    private static final int NEW_RECIPE_REQ = 1;
    private static final int DISPLAY_RECIPE_REQ = 2;
    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String RECIPE_CARD_LIST = "recipe card list";
    public static final String RECIPE_CARD = "recipe card";
    public static final String NEW_RECIPE = "new recipe";
    private static Boolean FAVOURITES_TOGGLED;

    private final Gson gson = new Gson();
    private String json;
    private SharedPreferences sharedPreferences;
    private int cardPos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        setContentView(R.layout.activity_main);
        FAVOURITES_TOGGLED = false;

        FloatingActionButton addRecipeBtn = findViewById(R.id.add_recipe_btn);
        addRecipeBtn.setOnClickListener(v -> addRecipe());

        alphabetical = (RecipeCard card1, RecipeCard card2) ->
                card1.getRecipeName().toLowerCase().compareTo(
                        card2.getRecipeName().toLowerCase());

        loadData();
        buildRecipeList();
        checkEmpty();
    }

    private void checkEmpty() {
        if (recipeCardList.isEmpty()) findViewById(R.id.intro_img).setVisibility(View.VISIBLE);
        else findViewById(R.id.intro_img).setVisibility(View.GONE);
    }

    private void addRecipe() {
        Intent intent = new Intent(this, AddNewRecipe.class);
        startActivityForResult(intent, NEW_RECIPE_REQ);
    }

    private void editData(RecipeCard editedCard) {
        recipeCardList.set(cardPos, editedCard);
        recipeCardList.sort(alphabetical);
        adapter.notifyDataSetChanged();
        saveData();
    }

    private void saveData() {
        sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        json = gson.toJson(recipeCardList);
        editor.putString(RECIPE_CARD_LIST, json);
        editor.apply();
    }

    private void loadData() {
        sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        json = sharedPreferences.getString(RECIPE_CARD_LIST, null);
        Type type = new TypeToken<ArrayList<RecipeCard>>() {}.getType();
        recipeCardList = gson.fromJson(json, type);

        if (recipeCardList == null) {
            recipeCardList = new ArrayList<>();
        }
    }

    private void buildRecipeList() {
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        adapter = new RecipeAdapter(recipeCardList);
        RecyclerView recipeList = findViewById(R.id.recipeList);

        recipeList.setAdapter(adapter);
        recipeList.setLayoutManager(layoutManager);

        adapter.setOnItemClickListener(position -> {
            cardPos = position;
            RecipeCard recipe = recipeCardList.get(cardPos);
            openRecipeCard(recipe);
        });
    }

    private void openRecipeCard(RecipeCard recipe) {
        Intent intent = new Intent( this, DisplayRecipe.class);
        intent.putExtra(RECIPE_CARD, recipe);
        startActivityForResult(intent, DISPLAY_RECIPE_REQ);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == NEW_RECIPE_REQ) {
                if (data != null) {
                    RecipeCard newRecipeCard = data.getParcelableExtra(NEW_RECIPE);
                    recipeCardList.add(newRecipeCard);
                    recipeCardList.sort(alphabetical);
                    adapter.notifyDataSetChanged();
                    checkEmpty();
                }
            } else if (requestCode == DISPLAY_RECIPE_REQ) {
                if (data == null) {
                    recipeCardList.remove(cardPos);
                    adapter.notifyItemRemoved(cardPos);
                    checkEmpty();
                } else {
                    RecipeCard editedRecipeCard = data.getParcelableExtra(DisplayRecipe.EDIT_RECIPE);
                    editData(editedRecipeCard);
                    if (data.getIntExtra(DisplayRecipe.FAVOURITE, 0) == 0) {
                        openRecipeCard(editedRecipeCard);
                    }
                }
            }
            saveData();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        return true;
    }

    private void sortFavourites() {
        List<RecipeCard> favouriteCards = new ArrayList<>();
        List<RecipeCard> normalCards = new ArrayList<>();
        for (RecipeCard card : recipeCardList) {
            if (card.getFavourite()) favouriteCards.add(card);
            else normalCards.add(card);
        }

        favouriteCards.sort(alphabetical);

        normalCards.sort(alphabetical);

        recipeCardList.clear();
        recipeCardList.addAll(favouriteCards);
        recipeCardList.addAll(normalCards);
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.get_favourites) {
            if (!FAVOURITES_TOGGLED) {
                item.setIcon(R.drawable.filled_heart);
                sortFavourites();
            } else {
                item.setIcon(R.drawable.unfilled_heart);
                recipeCardList.sort(alphabetical);
                adapter.notifyDataSetChanged();
            }
            FAVOURITES_TOGGLED = !FAVOURITES_TOGGLED;
            return true;
        } else {
            /*
            * Bugg som gör att favoritsortering försvinner första gången man klickar
            * på sök efter att aktiviteten har skapats. Ingen aning vad som händer eller var.
            * */
            androidx.appcompat.widget.SearchView searchView = (androidx.appcompat.widget.SearchView) item.getActionView();
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    adapter.getFilter().filter(newText);
                    return false;
                }
            });
        }
        return super.onOptionsItemSelected(item);
    }
}