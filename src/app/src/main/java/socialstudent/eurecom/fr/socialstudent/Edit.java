package socialstudent.eurecom.fr.socialstudent;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.heetch.countrypicker.Country;
import com.heetch.countrypicker.CountryPickerCallbacks;
import com.heetch.countrypicker.CountryPickerDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Edit extends AppCompatActivity  {

    private Button button;
    private ImageView image;
    private AutoCompleteTextView autoCompleteTextView;
    private EditText editText;
    private Button birthdate;
    private Button save;
    private Button inter;
    private Button level;
    private Button lang;

    private String pays;

    private String interests;
    private String levelou;
    private String languagou;

    private int yyyy;
    private int age;

    String[] interItems;
    Boolean[] chekedInter;
    ArrayList<Integer> profileInter = new ArrayList<>();

    String[] levItems;
    Boolean[] chekedLev;
    ArrayList<Integer> profileLev = new ArrayList<>();

    String[] langItems;
    Boolean[] chekedLang;
    ArrayList<Integer> profileLang = new ArrayList<>();

    private DatePickerDialog.OnDateSetListener dateSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference uidRef = rootRef.child("Users").child(uid);

        button = findViewById(R.id.button2);
        image = findViewById(R.id.imageView6);
        editText = findViewById(R.id.editText);
        birthdate = findViewById(R.id.textView27);
        autoCompleteTextView = findViewById(R.id.autoCompleteTextView);
        inter = findViewById(R.id.button3);
        level = findViewById(R.id.button4);
        lang = findViewById(R.id.button6);
        save = findViewById(R.id.button5);

        interItems = getResources().getStringArray(R.array.interests);
        chekedInter = new Boolean[interItems.length];
        inter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Edit.this);
                builder.setTitle("Select your Interests");
                builder.setMultiChoiceItems(interItems, null, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if(isChecked){
                            profileInter.add(which);
                        }
                    }
                });
                builder.setCancelable(false);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String item ="";
                        for(int i = 0;i<profileInter.size();i++){
                            item = item + interItems[profileInter.get(i)];
                            if(i!=profileInter.size()-1){
                                item = item+", ";
                            }
                        }
                        interests = item;
                        inter.setText(interests);
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        levItems = getResources().getStringArray(R.array.study);
        chekedLev = new Boolean[levItems.length];
        level.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Edit.this);
                builder.setTitle("Select your Study");
                builder.setMultiChoiceItems(levItems, null, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if(isChecked){
                            profileLev.add(which);
                        }
                    }
                });
                builder.setCancelable(false);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String item ="";
                        for(int i = 0;i<profileLev.size();i++){
                            item = item + levItems[profileLev.get(i)];
                            if(i!=profileLev.size()-1){
                                item = item+", ";
                            }
                        }
                        levelou = item;
                        level.setText(levelou);
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        langItems = getResources().getStringArray(R.array.languages);
        chekedLang = new Boolean[langItems.length];
        lang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Edit.this);
                builder.setTitle("Select your Languages");
                builder.setMultiChoiceItems(langItems, null, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if(isChecked){
                            profileLang.add(which);
                        }
                    }
                });
                builder.setCancelable(false);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String item ="";
                        for(int i = 0;i<profileLang.size();i++){
                            item = item + langItems[profileLang.get(i)];
                            if(i!=profileLang.size()-1){
                                item = item+", ";
                            }
                        }
                        languagou = item;
                        lang.setText(languagou);
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String desc = autoCompleteTextView.getText().toString().trim();
                final String phone = editText.getText().toString().trim();

                Map newImage = new HashMap();

                newImage.put("interests",interests);
                newImage.put("study",levelou);
                newImage.put("languages",languagou);
                newImage.put("profileAge",age);
                newImage.put("profileDesc",desc);
                newImage.put("phone",phone);
                newImage.put("pays",pays);


                uidRef.updateChildren(newImage);
                return;
            }
        });

        birthdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                yyyy = year;
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(Edit.this,android.R.style.Theme_Holo_Light_Dialog_MinWidth,dateSetListener,year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                age =   yyyy - year;
                birthdate.setText("Age: "+ age);
            }
        };

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CountryPickerDialog countryPicker =
                        new CountryPickerDialog(Edit.this, new CountryPickerCallbacks() {
                            @Override
                            public void onCountrySelected(Country country, int flagResId) {
                                image.setImageResource(flagResId);
                                pays = country.getIsoCode();
                                button.setText("Country selected: "+pays);
                            }
                        });
                countryPicker.show();

            }
        });

    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(this, ProfileEdit.class));
    }
}
