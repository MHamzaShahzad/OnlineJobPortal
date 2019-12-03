package com.example.onlinejobportal.user;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.onlinejobportal.R;
import com.example.onlinejobportal.controllers.MyFirebaseDatabase;
import com.example.onlinejobportal.models.LookForTrusted;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentApplyForVerify extends DialogFragment {

    private static final String TAG = FragmentApplyForVerify.class.getName();
    private Context context;

    private EditText editText_enter_message;
    private Button btn_submit;

    private FirebaseUser firebaseUser;

    public FragmentApplyForVerify() {
        // Required empty public constructor
    }

    public static FragmentApplyForVerify newInstance() {
        return new FragmentApplyForVerify();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        context = getContext();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = ((Activity) context).getLayoutInflater().inflate(R.layout.fragment_apply_for_verify, null);
        initDialogWidgets(view);
        builder.setView(view);
        return builder.create();
    }

    private void initDialogWidgets(View view) {
        editText_enter_message = view.findViewById(R.id.editText_enter_message);
        btn_submit = view.findViewById(R.id.btn_submit);
        setBtn_submit();
    }

    private void setBtn_submit() {
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyFirebaseDatabase.MAKE_TRUSTED_REFERENCE.child(firebaseUser.getUid()).setValue(buildLookForTrusted()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(context, "Applied for verification, wait till you received a notification for your interview.", Toast.LENGTH_LONG).show();
                            FragmentApplyForVerify.this.dismiss();
                        } else
                            Toast.makeText(context, "Can't send, please try again!", Toast.LENGTH_LONG).show();

                    }
                });
            }
        });
    }

    private LookForTrusted buildLookForTrusted() {
        return new LookForTrusted(
                firebaseUser.getUid(),
                editText_enter_message.getText().toString().trim(),
                new SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH).format(Calendar.getInstance().getTime()),
                false
        );
    }
}
