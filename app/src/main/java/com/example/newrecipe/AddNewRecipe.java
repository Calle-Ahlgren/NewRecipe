package com.example.newrecipe;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AddNewRecipe extends AppCompatActivity implements TimeDialog.TimeDialogListener {
    private EditText nameInput;
    private EditText amountInput;
    private EditText unitInput;
    private EditText ingredientInput;
    private EditText prepTimeInput;
    private EditText cookTimeInput;
    private EditText descriptionInput;

    private Button addBtn;
    private Button removeBtn;
    private Button saveBtn;

    private ImageView recipeImg;
    private String imgStrUri;

    private Button imgBtn;

    private int numOfIngredientRows;
    private List<Integer> amountIds;
    private List<Integer> unitIds;
    private List<Integer> ingredientIds;

    private ConstraintLayout constraintLayout;

    private ConstraintLayout.LayoutParams buttonParams;
    private RecipeCard recipe;

    private static final int PREP_INPUT = 1;
    private static final int COOK_INPUT = 2;
    private static final int IMG_INPUT = 3;
    public static final String TIME_INPUT_FLAG = "time input";
    private static final String ROTATION_FLAG = "rotation";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_recipe);
        Objects.requireNonNull(getSupportActionBar()).setTitle("New Recipe");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        numOfIngredientRows = 1;
        amountIds = new ArrayList<>();
        unitIds = new ArrayList<>();
        ingredientIds = new ArrayList<>();

        buttonParams = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        constraintLayout = findViewById(R.id.new_recipe_layout);

        setViews();

        setBtnListeners();

        Intent intent = getIntent();
        if ((recipe = intent.getParcelableExtra(MainActivity.RECIPE_CARD)) != null) {
            fillInputs(recipe);
        } else if (savedInstanceState != null) {
            fillInputs(savedInstanceState.getParcelable(ROTATION_FLAG));
        }
    }

    private void setViews() {
        imgBtn = findViewById(R.id.image_button);
        recipeImg = findViewById(R.id.image_view);
        addBtn = findViewById(R.id.add_ingredient);
        removeBtn = findViewById(R.id.remove_ingredient);
        saveBtn = findViewById(R.id.save_recipe);

        nameInput = findViewById(R.id.name_input);
        amountInput = findViewById(R.id.amount_input1);
        unitInput =  findViewById(R.id.unit_input1);
        ingredientInput = findViewById(R.id.ingredient_input1);
        prepTimeInput = findViewById(R.id.prep_input);
        prepTimeInput.setShowSoftInputOnFocus(false);
        cookTimeInput = findViewById(R.id.cook_input);
        cookTimeInput.setShowSoftInputOnFocus(false);
        descriptionInput = findViewById(R.id.description_input);

        amountIds.add(amountInput.getId());
        unitIds.add(unitInput.getId());
        ingredientIds.add(ingredientInput.getId());
    }

    private void setBtnListeners() {
        imgBtn.setOnClickListener(v -> {
            if (((Button) v).getText().toString().equals(getString(R.string.add_image))) {
                chooseImg();
            } else {
                removeImg();
            }
        });

        addBtn.setOnClickListener(v -> addIngredientRow());

        removeBtn.setOnClickListener(v -> removeIngredientRow());

        prepTimeInput.setOnClickListener(v -> openDialog(PREP_INPUT));
        prepTimeInput.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                openDialog(PREP_INPUT);
            }
        });

        cookTimeInput.setOnClickListener(v -> openDialog(COOK_INPUT));
        cookTimeInput.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                openDialog(COOK_INPUT);
            }
        });

        saveBtn.setOnClickListener(v -> saveRecipe());
    }

    private void removeImg() {
        recipeImg.setImageBitmap(null);
        recipeImg.setVisibility(View.GONE);
        imgStrUri = null;
        imgBtn.setText(getString(R.string.add_image));
    }

    private void chooseImg() {
        Intent imgIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        imgIntent.setType("image/*");
        startActivityForResult(imgIntent, IMG_INPUT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == IMG_INPUT) {
                if (data != null) {
                    Uri imgUri = data.getData();
                    getContentResolver().takePersistableUriPermission(imgUri,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    if (imgUri != null) {
                        imgStrUri = imgUri.toString();
                        setImg(imgUri);
                    }
                } else {
                    Toast.makeText(this, "Something went wrong",Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void setImg(Uri imgUri) {
        recipeImg.setImageURI(imgUri);
        recipeImg.setVisibility(View.VISIBLE);
        imgBtn.setText(getString(R.string.remove_image));
    }

    private void openDialog(int timeInputFlag) {
        TimeDialog dialog = new TimeDialog();
        Bundle bundle = new Bundle();
        bundle.putInt(TIME_INPUT_FLAG, timeInputFlag);
        dialog.setArguments(bundle);
        dialog.show(getSupportFragmentManager(), "time dialog");
    }

    private void addIngredientRow() {
        EditText amountInput = new EditText(this);
        EditText unitInput = new EditText(this);
        EditText ingredientInput = new EditText(this);

        amountInput.setId(View.generateViewId());
        amountIds.add(amountInput.getId());

        unitInput.setId(View.generateViewId());
        unitIds.add(unitInput.getId());

        ingredientInput.setId(View.generateViewId());
        ingredientIds.add(ingredientInput.getId());

        setRowParams(amountInput, amountIds.get(numOfIngredientRows - 1), 4);
        amountInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        setRowParams(unitInput, unitIds.get(numOfIngredientRows - 1), 3);
        setRowParams(ingredientInput, ingredientIds.get(numOfIngredientRows - 1), 7);

        constraintLayout.addView(amountInput);
        constraintLayout.addView(unitInput);
        constraintLayout.addView(ingredientInput);

        buttonParams.topToBottom = amountInput.getId();
        buttonParams.startToStart = constraintLayout.getId();
        buttonParams.endToStart = addBtn.getId();
        removeBtn.setLayoutParams(buttonParams);

        numOfIngredientRows++;
    }

    private void setRowParams(EditText inputField, int id, int ems) {
        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        inputField.setEms(ems);
        params.topToBottom = id;
        params.startToStart = id;
        inputField.setLayoutParams(params);
    }

    private void removeIngredientRow() {
        if (numOfIngredientRows > 1) {
            constraintLayout.removeView(findViewById(amountIds.get(numOfIngredientRows - 1)));
            constraintLayout.removeView(findViewById(unitIds.get(numOfIngredientRows - 1)));
            constraintLayout.removeView(findViewById(ingredientIds.get(numOfIngredientRows - 1)));

            buttonParams.topToBottom = amountIds.get(numOfIngredientRows - 2);

            removeBtn.setLayoutParams(buttonParams);

            amountIds.remove(amountIds.size() - 1);
            unitIds.remove(unitIds.size() - 1);
            ingredientIds.remove(ingredientIds.size() - 1);

            numOfIngredientRows--;
        }
    }

    private void createCard() {
        List<IngredientEntry> ingredientEntries = new ArrayList<>();
        IngredientEntry entry;
        for (int i = 0; i < numOfIngredientRows; i++) {
            if (i > 0) {
                constraintLayout = findViewById(amountIds.get(i-1));
                amountInput = (EditText) constraintLayout.getChildAt(0);
                unitInput = (EditText) constraintLayout.getChildAt(1);
                ingredientInput = (EditText) constraintLayout.getChildAt(2);
            }

            if (!ingredientInput.getText().toString().equals("")) {
                entry = new IngredientEntry(amountInput.getText().toString(),
                        unitInput.getText().toString(),
                        ingredientInput.getText().toString());
                ingredientEntries.add(entry);
            }
        }
        recipe = RecipeCard.builder()
                .name(nameInput.getText().toString())
                .image(imgStrUri)
                .ingredients(ingredientEntries)
                .prepTime(prepTimeInput.getText().toString())
                .cookTime(cookTimeInput.getText().toString())
                .description(descriptionInput.getText().toString())
                .build();
    }

    private void saveRecipe() {
        if (ingredientInput.getText().toString().equals("") || nameInput.getText().toString().equals("")) {
            Toast.makeText(this, "A recipe needs to have a name and at least one ingredient.", Toast.LENGTH_SHORT).show();
        } else {
            createCard();

            Intent resultIntent = new Intent();
            resultIntent.putExtra(MainActivity.NEW_RECIPE, recipe);

            setResult(RESULT_OK, resultIntent);
            finish();
        }
    }

    private void fillInputs(RecipeCard recipe) {
        nameInput.setText(recipe.getRecipeName());
        descriptionInput.setText(recipe.getDescription());
        prepTimeInput.setText(recipe.getPrepTime());
        cookTimeInput.setText(recipe.getCookTime());
        List<IngredientEntry> ingredientEntries = recipe.getIngredients();
        IngredientEntry entry = ingredientEntries.get(0);
        amountInput.setText(entry.getAmount());
        unitInput.setText(entry.getUnit());
        ingredientInput.setText(entry.getIngredient());

        imgStrUri = recipe.getRecipeImage();
        if (imgStrUri != null) {
            setImg(Uri.parse(imgStrUri));
        }

        for (int i = 1; i < ingredientEntries.size(); i++) {
            addIngredientRow();
            entry = ingredientEntries.get(i);

            constraintLayout = findViewById(amountIds.get(i - 1));
            amountInput = (EditText) constraintLayout.getChildAt(0);
            unitInput = (EditText) constraintLayout.getChildAt(1);
            ingredientInput = (EditText) constraintLayout.getChildAt(2);

            amountInput.setText(entry.getAmount());
            unitInput.setText(entry.getUnit());
            ingredientInput.setText(entry.getIngredient());
        }

        amountInput = findViewById(R.id.amount_input1);
        unitInput =  findViewById(R.id.unit_input1);
        ingredientInput = findViewById(R.id.ingredient_input1);
    }

    @Override
    public void applyText(String hours, String minutes, int timeInputFlag) {
        String time = "";

        if (!hours.equals("")) {
            time += hours;
            if (Integer.parseInt(hours) > 1) {
                time += " hrs ";
            } else {
                time += " hr ";
            }
        }

        if (!minutes.equals("")) {
            time += minutes;
            if (Integer.parseInt(minutes) > 1) {
                time += " mins";
            } else {
                time += " min";
            }
        }

        if (timeInputFlag == PREP_INPUT) {
            prepTimeInput.setText(time);
        } else {
            cookTimeInput.setText(time);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        createCard();

        outState.putParcelable(ROTATION_FLAG, recipe);
    }
}