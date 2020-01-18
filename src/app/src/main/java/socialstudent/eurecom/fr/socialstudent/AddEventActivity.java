package socialstudent.eurecom.fr.socialstudent;

import android.Manifest;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

public class AddEventActivity extends AppCompatActivity{

    static final int MY_PERMISSIONS_STORAGE=0 ;
    int PLACE_PICKER_REQUEST = 1;
    int PICK_IMAGE_REQUEST = 2;
    private static String time;
    private static String date ;
    ImageButton datebutton =null ;
    ImageButton timebutton = null;
    private static ImageView checktime = null ;
    private static ImageView checkdate = null ;
    private static ImageView checkplace = null ;
    ImageButton addButton = null ;
    Spinner eventSpinner = null ;
    EditText editName = null ;
    EditText editDescription = null ;
    ImageButton placebutton = null ;
    ImageButton addPic = null ;
    Uri selectedImage =null ;
    UploadTask uploadTask ;
    Bitmap scaled;
    String location = null ;
    double lng ;
    double lat ;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();
        
        final DatabaseReference myRef=FirebaseDatabase.getInstance().getReference();

        timebutton= findViewById(R.id.timeButton);
        datebutton= findViewById(R.id.dateButton);
        editName= findViewById(R.id.editName);
        editDescription= findViewById(R.id.editDescription);
        placebutton= findViewById(R.id.placeButton);
        addButton = findViewById(R.id.submitButton);
        checktime = findViewById(R.id.checktime);
        checkdate = findViewById(R.id.checkdate);
        checkplace = findViewById(R.id.checkplace);
        eventSpinner = findViewById(R.id.eventTypeSpinner) ;
        addPic = findViewById(R.id.addPict) ;
        mAuth = FirebaseAuth.getInstance();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.eventType_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        eventSpinner.setAdapter(adapter);

        final Switch isPrivate =findViewById(R.id.eventPrivacySwitch);


        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    String Name = editName.getText().toString();
                    String Description = editDescription.getText().toString();

                    if(     ///verify that fields not empty
                            !Name.isEmpty() && !Description.isEmpty()
                                    && time !=null  && date!= null
                                    && location != null && selectedImage!=null
                            ){

                        ///Generate unique key
                        final String key = myRef.child("Events").push().getKey();

                        //Punlish the event

                        long eventType = eventSpinner.getSelectedItemId();
                        Event event = new Event(Name,Description,lat,lng,location,time,date,key,eventType,Objects.requireNonNull(mAuth.getCurrentUser()).getUid(),isPrivate.isChecked());
                        if (key != null) {
                            myRef.child("Events").child(key).setValue(event);
                        } else {
                            Toast.makeText(AddEventActivity.this,"An error has occurred",Toast.LENGTH_LONG).show();
                        }

                        final ProgressDialog progressDialog = new ProgressDialog(AddEventActivity.this);

                        StorageReference imageRef = FirebaseStorage.getInstance().getReference().child("EventsImages").child(key).child("1");
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        scaled.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] data = baos.toByteArray();


                        uploadTask = imageRef.putBytes(data);
                        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                progressDialog.setMessage("Publication ...");
                                progressDialog.show();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                progressDialog.dismiss();
                                if (mAuth.getCurrentUser()!=null) {
                                    Intent intent = new Intent(AddEventActivity.this, MainActivity.class);
                                    startActivity(intent);
                                } else {
                                    Intent intent = new Intent(AddEventActivity.this,LoginActivity.class);
                                    startActivity(intent);
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();

                                //if image upload failed , remove the published event !

                                myRef.child("Events").child(key).removeValue();
                                Toast.makeText(AddEventActivity.this, "Publication failed",
                                        Toast.LENGTH_LONG).show();
                            }
                        }) ;

                    }
            }
        });
        timebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = new TimePickerFragment();
                FragmentManager fragmentManager = getFragmentManager();
                newFragment.show(fragmentManager, "timePicker");
            }
        });

        datebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = new DatePickerFragment();
                FragmentManager fragmentManager = getFragmentManager();
                newFragment.show(fragmentManager, "datePicker");
            }
        });
        placebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

                try {
                    startActivityForResult(builder.build(AddEventActivity.this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                location=place.getName().toString();
                lat=place.getLatLng().latitude;
                lng=place.getLatLng().longitude;
                updateLocation(place.getName().toString());
            }
        }

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
                int nh = (int) ( bitmapImage.getHeight() * (512.0 / bitmapImage.getWidth()) );
                scaled = Bitmap.createScaledBitmap(bitmapImage, 512, nh, true);
                addPic.setImageBitmap(scaled);
            }
        }
    }

    static void updateTime(TimePicker view, int hourOfDay, int minute){
        time = String.valueOf(hourOfDay)+":"+String.valueOf(minute);
        checktime.setVisibility(View.VISIBLE);

    }

    static void updateDate(DatePicker view, int year, int month, int day){
        date = String.valueOf(day)+"/"+String.valueOf(month)+"/"+String.valueOf(year);
        checkdate.setVisibility(View.VISIBLE);
    }

    static void updateLocation(String location){
checkplace.setVisibility(View.VISIBLE);
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

    public void addPicture(View view) {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
            Log.i("Permisison","To be Checked");
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},MY_PERMISSIONS_STORAGE);

        } else {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent,"Select Picture"), PICK_IMAGE_REQUEST);

        }
    }
}
