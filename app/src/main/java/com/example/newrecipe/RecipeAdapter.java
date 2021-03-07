package com.example.newrecipe;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> implements Filterable {
    private final ArrayList<RecipeCard> recipeCards;
    private final ArrayList<RecipeCard> allRecipeCards;
    private OnItemClickListener listener;

    public RecipeAdapter(ArrayList<RecipeCard> recipeCards) {
        this.recipeCards = recipeCards;
        allRecipeCards = new ArrayList<>(this.recipeCards);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public static class RecipeViewHolder extends RecyclerView.ViewHolder {
        public ImageView recipeImage;
        public ImageView favouriteImg;
        public TextView recipeName;
        public TextView prepTime;
        public TextView cookTime;

        public RecipeViewHolder(View itemView, OnItemClickListener listener) {
            super(itemView);
            recipeImage = itemView.findViewById(R.id.recipe_image_cardview);
            favouriteImg = itemView.findViewById(R.id.favourite_cardview);
            recipeName = itemView.findViewById(R.id.recipe_name_cardview);
            prepTime = itemView.findViewById(R.id.prep_time_cardview);
            cookTime = itemView.findViewById(R.id.cook_time_cardview);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position);
                    }
                }
            });
        }
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_list_card, parent, false);
        return new RecipeViewHolder(v, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        RecipeCard currentCard = recipeCards.get(position);
        if (currentCard.getRecipeImage() != null) {
            holder.recipeImage.setImageURI(Uri.parse(currentCard.getRecipeImage()));
        }
        else {
            holder.recipeImage.setImageResource(R.drawable.card_default);
        }
        if (currentCard.getFavourite()) {
            holder.favouriteImg.setVisibility(View.VISIBLE);
        } else {
            holder.favouriteImg.setVisibility(View.GONE);
        }

        holder.recipeName.setText(currentCard.getRecipeName());

        String prepTimeHolder = currentCard.getPrepTime();
        String cookTimeHolder = currentCard.getCookTime();
        if (prepTimeHolder.equals("")) {
            prepTimeHolder = holder.recipeImage.getContext().getString(R.string.prep_time, " - ");
        } else {
            prepTimeHolder = holder.recipeImage.getContext()
                    .getString(R.string.prep_time, currentCard.getPrepTime());
        }
        if (cookTimeHolder.equals("")) {
            cookTimeHolder = holder.recipeImage.getContext()
                    .getString(R.string.cook_time, " - ");
        } else {
            cookTimeHolder = holder.recipeImage.getContext()
                    .getString(R.string.cook_time, currentCard.getCookTime());
        }

        holder.prepTime.setText(prepTimeHolder);
        holder.cookTime.setText(cookTimeHolder);
    }

    @Override
    public int getItemCount() {
        return recipeCards.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    //FIX FILTER WHEN FAVOURITES IS TOGGLED
    private final Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<RecipeCard> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(allRecipeCards);
            } else {
                String filterString = constraint.toString().toLowerCase().trim();

                for (RecipeCard recipeCard : allRecipeCards) {
                    if (recipeCard.getRecipeName().toLowerCase().contains(filterString)) {
                        filteredList.add(recipeCard);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            recipeCards.clear();
            recipeCards.addAll((ArrayList<RecipeCard>) results.values);
            notifyDataSetChanged();
        }
    };
}
