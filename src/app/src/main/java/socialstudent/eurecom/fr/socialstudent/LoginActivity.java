package socialstudent.eurecom.fr.socialstudent;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowInsets;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private Button buttonSignin;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private TextView registerButton;
    private TextView textViewForgotPassword;
    private FirebaseAuth firebaseAuth;
    ImageView background ;
    CheckBox remember = null ;
    SharedPreferences sharedPref ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPref = this.getSharedPreferences("LoginPreferences", Context.MODE_PRIVATE);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();
        firebaseAuth = FirebaseAuth.getInstance();

        remember=findViewById(R.id.remember);

        //verify if the user coordinates are saved in preferences => login automatically

        buttonSignin = findViewById(R.id.loginButton);
        textViewForgotPassword = findViewById(R.id.textViewForgotPassword);
        editTextEmail = findViewById(R.id.emailEdit);
        editTextPassword = findViewById(R.id.passwordEdit);
        registerButton = findViewById(R.id.registerButton);
        //textViewForgotPassword = (TextView) findViewById(R.id.textViewForgotPassword);

        buttonSignin.setOnClickListener(this);
        registerButton.setOnClickListener(this);
        textViewForgotPassword.setOnClickListener(this);
        //textViewForgotPassword.setOnClickListener(this);
    }

    private void userLogin(){
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if(TextUtils.isEmpty(email)){
            editTextEmail.setError("Please enter your Email");
            editTextEmail.requestFocus();
            return;
        }

        if(TextUtils.isEmpty(password)){
            editTextPassword.setError("Please enter your password");
            editTextPassword.requestFocus();
            return;

        }

        if (password.length() < 6) {
            editTextPassword.setError("Password too short");
            editTextPassword.requestFocus();
            return;
        }


        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            checkIfEmailVerified();
                        }else {
                            Toast.makeText(getApplicationContext(), "Email or Password is Incorrect", Toast.LENGTH_LONG).show();
                        }

                    }
                });

    }

    private void checkIfEmailVerified(){

        FirebaseUser users = FirebaseAuth.getInstance().getCurrentUser();
        boolean emailVerified = users.isEmailVerified();
        if(!emailVerified){
            Toast.makeText(this,"Verify your Email address first",Toast.LENGTH_LONG).show();
            firebaseAuth.signOut();
        }else {
            if (remember.isChecked()) {
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("username", editTextEmail.getText().toString());
                editor.putString("password", editTextPassword.getText().toString());
                editor.apply();
            }
            finish();
            startActivity(new Intent(this,MainActivity.class));
        }
    }


    @Override
    public void onClick(View view) {
        if(view == buttonSignin){
            userLogin();
        }
        if(view == registerButton){
            finish();
            startActivity(new Intent(this, RegisterActivity.class));
        }
        if(view == textViewForgotPassword){
            final String email = editTextEmail.getText().toString().trim();
            try {
                firebaseAuth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(getApplicationContext(),"A new Password was sent to your Email : "+email, Toast.LENGTH_LONG).show();
                                } else {
                                    editTextEmail.setError("Please verify your Email");
                                    editTextEmail.requestFocus();
                                }
                            }
                        });
            } catch (Exception e) {
                e.printStackTrace();
                editTextEmail.setError("Please enter your Email");
                editTextEmail.requestFocus();
            }

        }

    }

    @Override
    public void onBackPressed() {
        }


}
