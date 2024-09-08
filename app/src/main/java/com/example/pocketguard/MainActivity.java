
package com.example.pocketguard;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView rv = findViewById(R.id.rv_budget);
        rv.setHasFixedSize(false);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 1);
        rv.setLayoutManager(layoutManager);

        BudgetAdapter adapter = new BudgetAdapter();
        rv.setAdapter(adapter);

        RecyclerView rv2 = findViewById(R.id.rv_transactions);
        rv2.setHasFixedSize(false);

        RecyclerView.LayoutManager layoutManager2 = new GridLayoutManager(this, 1);
        rv2.setLayoutManager(layoutManager2);

        TransactionsAdapter adapter2 = new TransactionsAdapter();
        rv2.setAdapter(adapter2);

        // Use ImageButton (imageButton7) to navigate to BudgetActivity
        ImageButton imageButton = findViewById(R.id.imageButton7);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to BudgetActivity
                Intent intent = new Intent(MainActivity.this, BudgetActivity.class);
                startActivity(intent);
            }
        });
    }
}
