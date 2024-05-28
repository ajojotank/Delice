package com.example.delice.ui.recipeBook;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.delice.R;
import com.example.delice.ui.search.IngredientFilterAdapter;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.Arrays;

public class AddRecipeActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView recipeImageView;
    private DrawerLayout drawerLayout;
    private RecyclerView ingredientDrawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);

        recipeImageView = findViewById(R.id.imageRecipeMain);
        recipeImageView.setOnClickListener(v -> openGallery());

        // Setup the drawer layout and ingredient list
        drawerLayout = findViewById(R.id.drawer_layout);
        ingredientDrawer = findViewById(R.id.ingredientDrawer);
        setupIngredientDrawer();

        Button addButton = findViewById(R.id.buttonSelectIngredients);
        addButton.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
                drawerLayout.closeDrawer(GravityCompat.END);
            } else {
                drawerLayout.openDrawer(GravityCompat.END);
            }
        });

        Button addRecipeButton = findViewById(R.id.buttonAddRecipe);
        addRecipeButton.setOnClickListener(v -> {
            // This will close the current activity and return to the RecipeBookFragment
            finish();
        });
    }

    private void setupIngredientDrawer() {
        ArrayList<String> ingredients = new ArrayList<>(Arrays.asList("Tomato", "Potato", "Onion", "Garlic"));
        IngredientFilterAdapter adapter = new IngredientFilterAdapter(ingredients, getLayoutInflater());
        ingredientDrawer.setLayoutManager(new LinearLayoutManager(this));
        ingredientDrawer.setAdapter(adapter);
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            Picasso.get().load(imageUri).into(recipeImageView);
        }
    }
}
