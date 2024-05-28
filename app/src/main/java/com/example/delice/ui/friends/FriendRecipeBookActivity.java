package com.example.delice.ui.friends;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.delice.R;
import com.example.delice.ui.recipe.Recipe;
import com.example.delice.ui.recipe.RecipeCardAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FriendRecipeBookActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friendrecipebook);

        String friendUsername = getIntent().getStringExtra("friend_username");
        TextView title = findViewById(R.id.textCookbookTitle);
        title.setText(friendUsername + "'s Cookbook");

        RecyclerView recipesRecyclerView = findViewById(R.id.recipesRecyclerView);
        recipesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<Recipe> recipes = new ArrayList<>();
        recipes.add(new Recipe(
                "Breakfast: Oatmeal",
                "Oatmeal with honey, fruits and nuts",
                true,  // Favourite
                Arrays.asList("1 cup oatmeal", "1 tbsp honey", "1/2 cup mixed fruits", "1/4 cup nuts"),
                Arrays.asList("Boil water or milk", "Add oatmeal and cook for 5 minutes", "Mix in honey, fruits, and nuts before serving"),
                "Joash Paul",
                "https://images.pexels.com/photos/90894/pexels-photo-90894.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1"
        ));

        recipes.add(new Recipe(
                "Lunch: Chicken Salad",
                "Grilled chicken salad with vinaigrette",
                false,  // Not favourite
                Arrays.asList("200g chicken breast", "Mixed greens", "2 tbsp olive oil", "1 tbsp vinegar"),
                Arrays.asList("Grill the chicken until cooked", "Toss the greens in olive oil and vinegar", "Top with sliced chicken"),
                "Camilla Driks",
                "https://images.pexels.com/photos/764925/pexels-photo-764925.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1"
        ));

        recipes.add(new Recipe(
                "Dinner: Pasta",
                "Pasta with marinara sauce and basil",
                true,  // Favourite
                Arrays.asList("200g pasta", "100g marinara sauce", "Fresh basil", "Parmesan cheese"),
                Arrays.asList("Cook pasta according to package instructions", "Heat sauce over medium flame", "Combine pasta and sauce", "Garnish with basil and parmesan"),
                "Aimee Harding",
                "https://images.pexels.com/photos/1487511/pexels-photo-1487511.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1"
        ));

        RecipeCardAdapter adapter = new RecipeCardAdapter(recipes);
        recipesRecyclerView.setAdapter(adapter);
    }
}
