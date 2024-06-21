package com.example.vinscannerapp.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vinscannerapp.R;
import com.example.vinscannerapp.adapter.VinInfoAdapter;
import com.example.vinscannerapp.entities.VinInfo;
import com.example.vinscannerapp.viewmodel.VinViewModel;

import java.util.List;

public class VinListActivity extends AppCompatActivity {

    private VinViewModel vinViewModel;
    private VinInfoAdapter adapter;

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);

        RecyclerView recyclerView = findViewById(R.id.id_recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setHasFixedSize(true);

        adapter = new VinInfoAdapter();
        recyclerView.setAdapter(adapter);

        vinViewModel = new ViewModelProvider(this).get(VinViewModel.class);
        int listId = getIntent().getIntExtra("listId", -1);

        vinViewModel.getVinInfoForList(listId).observe(this, new Observer<List<VinInfo>>() {
            @Override
            public void onChanged(List<VinInfo> vinInfos) {
                adapter.setVinInfos(vinInfos);
            }
        });

    }
}
