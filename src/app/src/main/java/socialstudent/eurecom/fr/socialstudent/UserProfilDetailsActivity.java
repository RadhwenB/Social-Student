package socialstudent.eurecom.fr.socialstudent;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class UserProfilDetailsActivity extends AppCompatActivity {

    TextView userName;
    TextView userGender;
    TextView userEmail;
    TextView residence;
    TextView showInterests;
    TextView showStudies;
    TextView showLanguages;
    TextView showBirthdate;
    TextView showPhonenumber;
    ImageButton modifyCountry;

    ImageView userImageProfile;
    TextView userDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profil_details);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();

        residence = findViewById(R.id.userResidence);
        userName = findViewById(R.id.userName);
        userGender = findViewById(R.id.userGender);
        userEmail = findViewById(R.id.userEmail);
        userImageProfile = findViewById(R.id.userProfilImage);
        userDescription = findViewById(R.id.userDescription);

        showInterests = findViewById(R.id.showInterrests);
        showStudies = findViewById(R.id.showStudies);
        showLanguages = findViewById(R.id.showLanguages);
        showBirthdate = findViewById(R.id.showBirthdate);
        showPhonenumber = findViewById(R.id.showPhonenumber);
        modifyCountry = findViewById(R.id.profileAfficheFlag);

        Intent i = getIntent() ;
        String profileId = i.getStringExtra("key");
        FirebaseDatabase.getInstance().getReference().child("Users").child(profileId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user;
                user = dataSnapshot.getValue(User.class);

                userEmail.setText(user.getEmail());
                userName.setText(user.getName());
                userGender.setText(user.getGender());
                residence.setText(getResources().getStringArray(R.array.residency_array)[user.getResidence()]);
                userDescription.setText(user.getDesc());
                showStudies.setText(user.getStudies());
                showLanguages.setText(user.getLangages());
                showBirthdate.setText(user.getBirthdate());
                showInterests.setText(user.getInterrests());
                showPhonenumber.setText(user.getPhone());
                if (!user.getCountry().equals("")) {
                    modifyCountry.setImageResource(getResources().getIdentifier(user.getCountry().toLowerCase() + "_flag", "mipmap", UserProfilDetailsActivity.this.getPackageName()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final StorageReference myref = FirebaseStorage.getInstance().getReference().child("profile_images").child(profileId);
        myref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                GlideApp.with(UserProfilDetailsActivity.this)
                        .load(myref)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                return false;
                            }
                        }).fitCenter().into(userImageProfile);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                userImageProfile.setImageResource(R.drawable.ic_user);
            }
        });

    }
}