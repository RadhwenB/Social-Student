package socialstudent.eurecom.fr.socialstudent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.os.Handler;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.heetch.countrypicker.Utils;

public class SplashScreen extends AppCompatActivity {


    ImageView background = null ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();


        TextView tv = findViewById(R.id.tv);
        TextView tv2 = findViewById(R.id.tv2);
        ImageView img = findViewById(R.id.img);


        Animation myanimation = AnimationUtils.loadAnimation(getApplication(), R.anim.myanimation);
        Animation slidedown = AnimationUtils.loadAnimation(getApplication(), R.anim.slidedown);
        //tv.startAnimation(slidedown);
        //tv2.startAnimation(myanimation);
        //img.startAnimation(slidedown);

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("LoginPreferences", Context.MODE_PRIVATE);

        String username = sharedPref.getString("username", "NULL");
        String password = sharedPref.getString("password", "NULL");


        if (!username.equals("NULL") && !password.equals("NULL")) {
            firebaseAuth.signInWithEmailAndPassword(username, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        startActivity(new Intent(SplashScreen.this, MainActivity.class));
                                    }
                                }, 2000);
                            } else {
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        startActivity(new Intent(SplashScreen.this, LoginActivity.class));
                                    }
                                }, 2000);

                            }

                        }
                    });
        }
        else{
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(SplashScreen.this, LoginActivity.class));
                }
            }, 2000);
        }
    }
}
