package socialstudent.eurecom.fr.socialstudent;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {


    private Button registerButton;
    private TextView textView, textView2;
    private EditText nameEdit;
    private EditText emailEdit;
    private EditText passwordEdit;
    private EditText confirmPasswordEdit;
    private Spinner spinner;
    private RadioButton male_radio_btn, female_radio_btn;
    private TextView loginText;
    private ProgressBar progressBar;
    public String genderClicked = "NotSet";

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        spinner = findViewById(R.id.residence_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.residency_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        progressBar= findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        registerButton = findViewById(R.id.registerButton);
        nameEdit = findViewById(R.id.nameEdit);
        emailEdit = findViewById(R.id.emailEdit);
        passwordEdit = findViewById(R.id.passwordEdit);
        confirmPasswordEdit = findViewById(R.id.confirmPasswordEdit);
        male_radio_btn = findViewById(R.id.male_radio_btn);
        female_radio_btn = findViewById(R.id.female_radio_btn);
        loginText = findViewById(R.id.loginText);
        textView = findViewById(R.id.gender_textview);
        textView2 = findViewById(R.id.textViewResidence);

        mAuth = FirebaseAuth.getInstance();

        registerButton.setOnClickListener(this);
        male_radio_btn.setOnClickListener(this);
        female_radio_btn.setOnClickListener(this);
        loginText.setOnClickListener(this);

    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mAuth.getCurrentUser() != null){
            finish();
            startActivity(new Intent(this,MainActivity.class));
        }
    }

    private void registerUser(){
        final String email = emailEdit.getText().toString().trim();
        String password = passwordEdit.getText().toString().trim();
        String password2 = confirmPasswordEdit.getText().toString().trim();
        final String name = nameEdit.getText().toString().trim();
        final int residence = (int)spinner.getSelectedItemId();
        final String gender = genderClicked;

        if(TextUtils.isEmpty(name)){
            nameEdit.setError("Please enter your Name");
            nameEdit.requestFocus();
            return;
        }
        if(TextUtils.isEmpty(email)){
            emailEdit.setError("Please enter your Email");
            emailEdit.requestFocus();
            return;
        }
        if(TextUtils.isEmpty(password)){
            passwordEdit.setError("Please enter your Password");
            passwordEdit.requestFocus();
            return;
        }
        if(TextUtils.isEmpty(password2)){
            confirmPasswordEdit.setError("Please confirm your Password");
            confirmPasswordEdit.requestFocus();
            return;
        }
        if (password.length() < 6) {
            passwordEdit.setError("Password too short");
            passwordEdit.requestFocus();
            return;
        }
        if (!password.equals(password2)) {
            confirmPasswordEdit.setError("Password does not match");
            confirmPasswordEdit.requestFocus();
            return;
        }



        progressBar.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if(task.isSuccessful()){
                            // we will store the additional information in database
                            User user = new User(FirebaseAuth.getInstance().getCurrentUser().getUid(),name, email,gender,"","", residence,"","","","","");
                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    progressBar.setVisibility(View.GONE);
                                    if(task.isSuccessful()){
                                        Toast.makeText(RegisterActivity.this, "Registration succeed",Toast.LENGTH_LONG).show();
                                    }else{
                                        Toast.makeText(RegisterActivity.this, "Registration failed",Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                            sendEmailVerification();
                            Thread timer = new Thread(){
                                @Override
                                public void run(){
                                    try {
                                        sleep(2000);
                                    } catch (InterruptedException e){
                                        e.printStackTrace();
                                    } finally {
                                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                                        finish();
                                    }
                                }
                            };
                            timer.start();

                        }else{
                            Toast.makeText(RegisterActivity.this,task.getException().getMessage(),Toast.LENGTH_LONG).show();
                        }
                    }
                });



    }

    private void sendEmailVerification() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(RegisterActivity.this,"Check your Email for verification",Toast.LENGTH_LONG).show();
                        FirebaseAuth.getInstance().signOut();
                    }
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        if(v == male_radio_btn){
            genderClicked = "Male";
        }
        if(v == female_radio_btn){
            genderClicked = "Female";
        }
        if(v == registerButton){
            registerUser();
        }

        if(v == loginText){
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
    }
}
