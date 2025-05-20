package com.example.ex2.activities.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ex2.R;
import com.example.ex2.activities.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentRegister#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentRegister extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private FirebaseAuth mAuth;

    public FragmentRegister() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentTwo.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentRegister newInstance(String param1, String param2) {
        FragmentRegister fragment = new FragmentRegister();
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
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_register, container, false);
        mAuth = FirebaseAuth.getInstance();

        v.findViewById(R.id.SubmitRegister).setOnClickListener(view -> {
            String email = ((EditText) v.findViewById(R.id.regEmail)).getText().toString().trim();
            String pass  = ((EditText) v.findViewById(R.id.regPassword)).getText().toString().trim();
            String confirm = ((EditText) v.findViewById(R.id.regConfirmPassword)).getText().toString().trim();
            String phoneInput = ((EditText) v.findViewById(R.id.regPhone)).getText().toString().trim();

            // Check if any field is empty
            if (email.isEmpty() || pass.isEmpty() || confirm.isEmpty() || phoneInput.isEmpty()) {
                Toast.makeText(getContext(), "All fields required", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!pass.equals(confirm)) {
                Toast.makeText(getContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if password is at least 6 characters
            if (pass.length() < 6) {
                Toast.makeText(getContext(), "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                return;
            }

            // check if email is valid
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(getContext(), "Invalid email address", Toast.LENGTH_SHORT).show();
                return;
            }


            DatabaseReference usersRef =
                    FirebaseDatabase.getInstance().getReference("users");

            usersRef.orderByChild("phone")
                    .equalTo(phoneInput)
                    .limitToFirst(1)
                    .addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override public void onDataChange(@NonNull DataSnapshot snap) {
                            if (snap.exists()) {
                                Toast.makeText(getContext(),
                                        "Phone number already exists",
                                        Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                        @Override public void onCancelled(@NonNull DatabaseError error) {
                            Log.e("TAG", "onCancelled: ", error.toException());
                        }
                    });


            mAuth.createUserWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(requireActivity(), task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Registered!", Toast.LENGTH_SHORT).show();
                            MainActivity mainActivity = (MainActivity) getActivity();
                            mainActivity.addData();
                            Navigation.findNavController(view)
                                    .navigate(R.id.action_fragmentRegister_to_fragmentLogin);
                        } else {
                            Toast.makeText(getContext(), "Registration failed", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
        return v;
    }
}