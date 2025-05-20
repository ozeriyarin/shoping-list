package com.example.ex2.activities.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ex2.R;
import com.example.ex2.activities.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentLogin#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentLogin extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private FirebaseAuth mAuth;

    public FragmentLogin() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentOne.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentLogin newInstance(String param1, String param2) {
        FragmentLogin fragment = new FragmentLogin();
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

        View v = inflater.inflate(R.layout.fragment_login, container, false);
        mAuth = FirebaseAuth.getInstance();

        Button btnLogin = v.findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(view -> {
            String email = ((EditText) v.findViewById(R.id.loginEmail))
                    .getText().toString().trim();
            String pass  = ((EditText) v.findViewById(R.id.loginPassword))
                    .getText().toString().trim();

            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(getContext(), "Both fields required", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.signInWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(requireActivity(), task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Login success", Toast.LENGTH_SHORT).show();

                            Bundle bundle = getBundle(email);

                            Navigation.findNavController(view)
                                    .navigate(R.id.action_fragmentLogin_to_fragmentCalender, bundle);
                            MainActivity mainActivity = (MainActivity) getActivity();
                            // Get the current user
                            mainActivity.getStudent();
                        } else {
                            Toast.makeText(getContext(), "Login failed", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        v.findViewById(R.id.btnRegister)
                .setOnClickListener(navView ->
                        Navigation.findNavController(navView)
                                .navigate(R.id.action_fragmentLogin_to_fragmentRegister));

        return v;
    }

    @NonNull
    private Bundle getBundle(String email) {
        String userName = mAuth.getCurrentUser().getDisplayName();
        if (userName == null || userName.isEmpty()) {
            userName = email;
        }

        // adjust the userName to be the first part of the email
        int atIndex = userName.indexOf('@');
        if (atIndex != -1) {
            userName = userName.substring(0, atIndex);
        }

        Bundle bundle = new Bundle();
        bundle.putString("user_name", userName);
        return bundle;
    }
}