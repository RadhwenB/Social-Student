package socialstudent.eurecom.fr.socialstudent;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.camera2.CameraManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hbb20.CountryCodePicker;
import com.heetch.countrypicker.Country;
import com.heetch.countrypicker.CountryPickerCallbacks;
import com.heetch.countrypicker.CountryPickerDialog;
import com.heetch.countrypicker.Utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class ProfileEdit extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    static final int MY_PERMISSIONS_STORAGE=0 ;
    int PICK_IMAGE_REQUEST = 2;
    Uri selectedImage =null ;
    UploadTask uploadTask ;

    Bitmap scaled;

    TextView userName;
    TextView userGender;
    TextView userEmail;
    TextView residence;
    TextView showInterests;
    TextView showStudies;
    TextView showLanguages;
    TextView showBirthdate;
    TextView showPhonenumber;

    ImageView userImageProfile;
    TextView userDescription;
    ImageButton modifyDescription;
    ImageButton modifyInterrests;
    ImageButton modifyStudies;
    ImageButton modifyLaanguages;
    ImageButton modifyBirthdate;
    ImageButton modifyPhonenumber;
    ImageButton modifyCountry;
    ImageButton modifyProfilImage ;

    LinearLayout phonebox;
    User user = null;
    EditText editDescription;
    Button saveButton;
    CountryCodePicker ccp;
    EditText editTextCarrierNumber;
    Country country=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();

        residence = findViewById(R.id.userResidence);
        userName = findViewById(R.id.userName);
        userGender = findViewById(R.id.userGender);
        userEmail = findViewById(R.id.userEmail);
        userImageProfile = findViewById(R.id.userProfilImage);
        userDescription = findViewById(R.id.userDescription);

        modifyDescription = findViewById(R.id.modifyDescription);
        modifyInterrests = findViewById(R.id.modifyInterrests);
        modifyStudies = findViewById(R.id.modifyStudies);
        modifyLaanguages = findViewById(R.id.modifyLanguage);
        modifyBirthdate = findViewById(R.id.modifyBirthdate);
        modifyPhonenumber = findViewById(R.id.modifyPhonenumber);
        modifyCountry = findViewById(R.id.profileAfficheFlag);
        modifyProfilImage = findViewById(R.id.modifyProfileImage) ;



        editDescription = findViewById(R.id.editDescription);
        saveButton = findViewById(R.id.saveButton);
        showInterests = findViewById(R.id.showInterrests);
        showStudies = findViewById(R.id.showStudies);
        showLanguages = findViewById(R.id.showLanguages);
        showBirthdate = findViewById(R.id.showBirthdate);
        showPhonenumber = findViewById(R.id.showPhonenumber);
        phonebox = findViewById(R.id.phoneBox);

        ccp = findViewById(R.id.ccp);
        editTextCarrierNumber = findViewById(R.id.editText_carrierNumber);
        ccp.registerCarrierNumberEditText(editTextCarrierNumber);
        ccp.setNumberAutoFormattingEnabled(true);


        final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();


        final DatabaseReference uidRef = rootRef.child("Users").child(uid);
        uidRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                userEmail.setText(user.getEmail());
                userName.setText(user.getName());
                userGender.setText(user.getGender());
                residence.setText(getResources().getStringArray(R.array.residency_array)[user.getResidence()]);
                userDescription.setText(user.getDesc());
                editDescription.setText(user.getDesc());
                if (!user.getInterrests().equals("")) {
                    showInterests.setText(user.getInterrests());
                } else {
                    showInterests.setText(R.string.add_interests);
                }
                showStudies.setText(user.getStudies());
                showLanguages.setText(user.getLangages());
                showBirthdate.setText(user.getBirthdate());
                showPhonenumber.setText(user.getPhone());
                if (!user.getCountry().equals("")) {
                    modifyCountry.setImageResource(getResources().getIdentifier(user.getCountry().toLowerCase() + "_flag", "mipmap", ProfileEdit.this.getPackageName()));
                }
    }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        final StorageReference myref = FirebaseStorage.getInstance().getReference().child("profile_images").child(uid);
        myref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                GlideApp.with(getApplicationContext())
                        .load(FirebaseStorage.getInstance().getReference().child("profile_images").child(uid))
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .into(userImageProfile);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                userImageProfile.setImageResource(R.drawable.ic_user);
            }
        });




        modifyProfilImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(ProfileEdit.this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                    Log.i("Permisison","To be Checked");
                    ActivityCompat.requestPermissions(ProfileEdit.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},MY_PERMISSIONS_STORAGE);

                } else {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent,"Select Picture"), PICK_IMAGE_REQUEST);

                }
            }
        });
        modifyCountry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CountryPickerDialog countryPicker =
                        new CountryPickerDialog(ProfileEdit.this, new CountryPickerCallbacks() {
                            @Override
                            public void onCountrySelected(Country country, int flagResId) {
                                modifyCountry.setImageResource(flagResId);
                                ProfileEdit.this.country = country;
                            }
                        });
                countryPicker.show();
            }
        });


        modifyDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userDescription.setVisibility(View.INVISIBLE);
                editDescription.setVisibility(View.VISIBLE);

            }
        });

        modifyInterrests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog;
                final String[] items = getResources().getStringArray(R.array.interests);
                final ArrayList itemsSelected = new ArrayList();

                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileEdit.this);
                builder.setTitle("Select your interests : ");
                builder.setMultiChoiceItems(items, null,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int selectedItemId,
                                                boolean isSelected) {
                                if (isSelected) {
                                    itemsSelected.add(selectedItemId);
                                } else if (itemsSelected.contains(selectedItemId)) {
                                    itemsSelected.remove(Integer.valueOf(selectedItemId));
                                }
                            }
                        })
                        .setPositiveButton("Done!", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                StringBuilder toShow = new StringBuilder();
                                for (Object i : itemsSelected) {
                                    toShow.append(items[(int) i]).append(" ");
                                }
                                showInterests.setText(toShow);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });
                dialog = builder.create();
                dialog.show();
            }
        });

        modifyStudies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog;
                final String[] items = getResources().getStringArray(R.array.study);
                final int[] itemSelected = new int[1];

                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileEdit.this);
                builder.setTitle("Select your study level : ");
                builder.setSingleChoiceItems(items, 0,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                itemSelected[0] = i;
                            }
                        })
                        .setPositiveButton("Done!", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                StringBuilder toShow = new StringBuilder();
                                toShow.append(items[itemSelected[0]]);
                                showStudies.setText(toShow);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });
                dialog = builder.create();
                dialog.show();
            }
        });

        modifyLaanguages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog;
                final String[] items = getResources().getStringArray(R.array.languages);
                final ArrayList itemsSelected = new ArrayList();

                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileEdit.this);
                builder.setTitle("Select your interests : ");
                builder.setMultiChoiceItems(items, null,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int selectedItemId,
                                                boolean isSelected) {
                                if (isSelected) {
                                    itemsSelected.add(selectedItemId);
                                } else if (itemsSelected.contains(selectedItemId)) {
                                    itemsSelected.remove(Integer.valueOf(selectedItemId));
                                }
                            }
                        })
                        .setPositiveButton("Done!", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                StringBuilder toShow = new StringBuilder();
                                for (Object i : itemsSelected) {
                                    toShow.append(items[(int) i]).append(" ");
                                }
                                showLanguages.setText(toShow);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });
                dialog = builder.create();
                dialog.show();
            }
        });

        modifyBirthdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        ProfileEdit.this, ProfileEdit.this, year, month, day);
                datePickerDialog.show();

            }
        });


        modifyPhonenumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                phonebox.setVisibility(View.VISIBLE);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                userDescription.setText(editDescription.getText());
                userDescription.setVisibility(View.VISIBLE);
                editDescription.setVisibility(View.INVISIBLE);
                user.setDesc(userDescription.getText().toString());
                user.setInterrests(showInterests.getText().toString());
                user.setStudies(showStudies.getText().toString());
                user.setLangages(showLanguages.getText().toString());
                user.setBirthdate(showBirthdate.getText().toString());
                if(country!=null) {
                    user.setCountry(country.getIsoCode());
                }
                if (ccp.isValidFullNumber()) {
                    user.setPhone(ccp.getFormattedFullNumber());
                }

                uidRef.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (ccp.isValidFullNumber()) {
                            Toast.makeText(getApplicationContext(), "Information saved with success", Toast.LENGTH_LONG).show();
                            phonebox.setVisibility(View.INVISIBLE);

                        } else {
                            Toast.makeText(ProfileEdit.this, "Invalid Phone Number, Information are saved except Phone number ", Toast.LENGTH_LONG).show();
                        }

                    }
                });

                if(selectedImage!=null){
                    final ProgressDialog progressDialog = new ProgressDialog(ProfileEdit.this);

                    StorageReference imageRef = FirebaseStorage.getInstance().getReference().child("profile_images").child(uid);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    scaled.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] data = baos.toByteArray();


                    uploadTask = imageRef.putBytes(data);
                    uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.setMessage("Uploading your photo ...");
                            progressDialog.show();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            progressDialog.dismiss();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();

                        }
                    }) ;
                }

            }
        });


    }


    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(this, MainActivity.class));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        showBirthdate.setText(String.valueOf(i) + "-" + String.valueOf(i1 + 1) + "-" + String.valueOf(i2));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case MY_PERMISSIONS_STORAGE: {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Log.i("Access:", "Now Permissions are granted");
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent,"Select Picture"), PICK_IMAGE_REQUEST);
                } else {
                    Log.i("Access: ","Permissions are denied");
                }
                break;
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && null != data) {
            // Get the Image from data
            if(data.getData()!=null) {
                selectedImage = data.getData();

                //Show selected image
                Bitmap bitmapImage = null;
                try {
                    bitmapImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                assert bitmapImage != null;
                int nh = (int) ( bitmapImage.getHeight() * (512.0 / bitmapImage.getWidth()) );
                scaled = Bitmap.createScaledBitmap(bitmapImage, 512, nh, true);
                userImageProfile.setImageBitmap(scaled);
            }
        }
    }

}
