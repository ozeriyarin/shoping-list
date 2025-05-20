package com.example.ex2.activities.fragments;

import android.app.AlertDialog;
import android.graphics.Canvas;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.ex2.R;
import com.example.ex2.activities.adapters.CustomeAdapter;
import com.example.ex2.activities.models.Product;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentRecyclerView#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentRecyclerView extends Fragment {

    private CustomeAdapter customeAdapter;
    private DatabaseReference productsRef;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public FragmentRecyclerView() {
        // Required empty public constructor
    }

    public static FragmentRecyclerView newInstance(String param1, String param2) {
        FragmentRecyclerView fragment = new FragmentRecyclerView();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String mParam1 = getArguments().getString(ARG_PARAM1);
            String mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_recycler_view,
                container, false);

        RecyclerView recyclerView = view.findViewById(R.id.rvcon);
        SearchView searchView = view.findViewById(R.id.searchView);
        FloatingActionButton fabAdd = view.findViewById(R.id.fabAdd);
        TextView username = view.findViewById(R.id.username);

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        productsRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(uid)
                .child("products");

        String userName = getArguments() != null
                ? getArguments().getString("user_name", "User")
                : "User";
        username.setText("Hello, " + userName);

        recyclerView.setLayoutManager(
                new LinearLayoutManager(requireContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        ArrayList<Product> productList = new ArrayList<>();
        customeAdapter = new CustomeAdapter(productList);
        recyclerView.setAdapter(customeAdapter);

        /* ----- Load products once + listen for future changes ----- */
        productsRef.addValueEventListener(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snap) {
                List<Product> tmp = new ArrayList<>();
                for (DataSnapshot child : snap.getChildren()) {
                    Product p = child.getValue(Product.class);
                    if (p != null) tmp.add(p);
                }
                customeAdapter.setData(tmp);
            }
            @Override public void onCancelled(@NonNull DatabaseError e) {
                Snackbar.make(recyclerView, "Failed: " + e.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });

        searchView.setOnQueryTextListener(
                new SearchView.OnQueryTextListener() {
                    @Override public boolean onQueryTextSubmit(String query) {
                        customeAdapter.getFilter().filter(query);
                        return true;
                    }
                    @Override public boolean onQueryTextChange(String text) {
                        customeAdapter.getFilter().filter(text);
                        return true;
                    }
                });


        fabAdd.setOnClickListener(v1 -> showAddDialog());

        ItemTouchHelper.SimpleCallback swipeCallback =
                new ItemTouchHelper.SimpleCallback(0,
                        ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

                    @Override public boolean onMove(@NonNull RecyclerView rv,
                                                    @NonNull RecyclerView.ViewHolder vh,
                                                    @NonNull RecyclerView.ViewHolder tgt) {
                        return false;
                    }

                    @Override public void onSwiped(@NonNull RecyclerView.ViewHolder vh,
                                                   int direction) {

                        int pos = vh.getAdapterPosition();

                        //delete visually & from data
                        Product deleted = customeAdapter.removeItem(pos);

                        productsRef.child(deleted.getId()).removeValue();

                        Snackbar.make(recyclerView, "Deleted " + deleted.getName(), Snackbar.LENGTH_LONG)
                                .setAction("UNDO", v2 -> productsRef.child(deleted.getId()).setValue(deleted))
                                .show();
                    }

                    // paint red bg + trash‑icon while swiping
                    @Override
                    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView rv,
                                            @NonNull RecyclerView.ViewHolder vh,
                                            float dX, float dY,
                                            int state, boolean isActive) {

                        new RecyclerViewSwipeDecorator.Builder(c, rv, vh, dX, dY,
                                state, isActive)
                                .addBackgroundColor(ContextCompat.getColor(requireContext(),
                                        R.color.red))
                                .addActionIcon(R.drawable.ic_delete)
                                .create()
                                .decorate();

                        super.onChildDraw(c, rv, vh, dX, dY, state, isActive);
                    }
                };

        new ItemTouchHelper(swipeCallback).attachToRecyclerView(recyclerView);

        return view;
    }

    private void showAddDialog() {
        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_add_product, null, false);
        EditText etName  = dialogView.findViewById(R.id.etName);
        EditText etDesc  = dialogView.findViewById(R.id.etDesc);
        EditText etQty   = dialogView.findViewById(R.id.etQty);
        EditText etImage = dialogView.findViewById(R.id.etImage);

        new AlertDialog.Builder(requireContext())
                .setTitle("Add product")
                .setView(dialogView)
                .setPositiveButton("Save", (d, which) -> {
                    String name  = etName.getText().toString().trim();
                    String desc  = etDesc.getText().toString().trim();
                    String qtyStr= etQty.getText().toString().trim();
                    String img   = etImage.getText().toString().trim();

                    if (name.isEmpty() || qtyStr.isEmpty()) {
                        Snackbar.make(requireView(), "Name & quantity required", Snackbar.LENGTH_LONG).show();
                        return;
                    }

                    int qty = Integer.parseInt(qtyStr);
                    String key = productsRef.push().getKey();
                    Product p = new Product(key, name, desc, qty, img);
                    productsRef.child(key).setValue(p);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}