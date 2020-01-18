package socialstudent.eurecom.fr.socialstudent;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Telephony;
import android.service.quicksettings.Tile;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeoutException;


public class ShowEvent extends AppCompatActivity {
    TextView Title = null;
    TextView Type = null;
    TextView Description = null;
    TextView Time = null;
    TextView Date = null;
    TextView Place = null;
    ImageView Photos = null;
    ImageButton showMap = null;
    Switch participate = null;
    TextView organizer= null ;
    TextView participants = null ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_event);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();

        final Intent intent = getIntent();
        Photos = findViewById(R.id.showPhotos);
        showMap = findViewById(R.id.showMap);
        Title = findViewById(R.id.showTitle);
        Type = findViewById(R.id.showType);
        Description = findViewById(R.id.showDescription);
        Time = findViewById(R.id.showTime);
        Date = findViewById(R.id.showDate);
        Place = findViewById(R.id.showLocation);
        participate = findViewById(R.id.participateSwitch);
        organizer=findViewById(R.id.organizerView);
        participants = findViewById(R.id.showEventParticipantsNumber) ;



        String origanizerId = intent.getStringExtra("organizer");

        FirebaseDatabase.getInstance().getReference().child("Users").child(origanizerId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User aux;
                aux = dataSnapshot.getValue(User.class);
                organizer.setText("Posted by "+aux.getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final int[] participantsNumber = {intent.getIntExtra("participantNumber", 0)};

        participants.setText(String.valueOf(participantsNumber[0])+" participate");
        participants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ShowEvent.this,ParticipantsShowActivity.class) ;
                i.putExtra("key",intent.getStringExtra("key"));
                if(participantsNumber[0]>0){
                    startActivity(i);
                }
            }
        });


        Title.setText(intent.getStringExtra("title"));
        Description.setText(intent.getStringExtra("description"));
        List<String> types = Arrays.asList(getResources().getStringArray(R.array.eventType_array));
        String type = types.get((int)intent.getLongExtra("type", 0));
        Type.setText(type);

        if(!intent.getBooleanExtra("private",true)){
            Time.setText(intent.getStringExtra("time"));
            Date.setText(intent.getStringExtra("date"));
            Place.setText(intent.getStringExtra("place"));


            final double lat = intent.getDoubleExtra("lat", 0);
            final double lng = intent.getDoubleExtra("lng", 0);
            showMap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Uri gmmIntentUri = Uri.parse("geo:" + String.valueOf(lat) + "," + String.valueOf(lng) + "?z=18");
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    startActivity(mapIntent);

                }
            });

            final DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("Participations");

            participate.setChecked(false);
            final String[] relationKey = new String[1];


            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    RelationEventUser aux;
                    String currentUserId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        aux = data.getValue(RelationEventUser.class);
                        assert aux != null;
                        if (aux.getUserKey().equals(currentUserId) && aux.getEventKey().equals(intent.getStringExtra("key"))) {
                            relationKey[0] = aux.getKey();
                            participate.setChecked(true);

                            break;
                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


            participate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {

                        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                RelationEventUser aux;
                                String currentUserId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
                                boolean found = false;
                                for (DataSnapshot data : dataSnapshot.getChildren()) {
                                    aux = data.getValue(RelationEventUser.class);
                                    assert aux != null;
                                    if (aux.getUserKey().equals(currentUserId) && aux.getEventKey().equals(intent.getStringExtra("key"))) {
                                        relationKey[0] = aux.getKey();
                                        found = true;
                                        break;
                                    }

                                }

                                if (!found) {
                                    final String key = myRef.push().getKey();
                                    RelationEventUser relation = new RelationEventUser(key, intent.getStringExtra("key"), FirebaseAuth.getInstance().getCurrentUser().getUid());
                                    myRef.child(key).setValue(relation).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            participate.setChecked(true);
                                            relationKey[0] = key;
                                            Toast.makeText(getApplicationContext(), "Participate in the event", Toast.LENGTH_LONG).show();
                                            participantsNumber[0]++;
                                            participants.setText(String.valueOf(participantsNumber[0])+" participate");
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            participate.setChecked(false);
                                            Toast.makeText(getApplicationContext(), "Failed to participate in the event", Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                    } else if (!b) {
                        DatabaseReference toRemove = FirebaseDatabase.getInstance().getReference().child("Participations").child(relationKey[0]);

                        toRemove.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                participate.setChecked(false);
                                Toast.makeText(getApplicationContext(), "Quit the event", Toast.LENGTH_LONG).show();
                                participantsNumber[0]--;
                                participants.setText(String.valueOf(participantsNumber[0])+" participate");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                participate.setChecked(true);
                                Toast.makeText(getApplicationContext(), "Failed to quit the event", Toast.LENGTH_LONG).show();
                            }
                        });

                    }
                }
            });

        } else {
            showMap.setVisibility(View.GONE);
            Time.setVisibility(View.GONE);
            Date.setVisibility(View.GONE);
            Place.setVisibility(View.GONE);
            participate.setVisibility(View.GONE);


            TextView mapicon =findViewById(R.id.showEventLocationView) ;
            TextView participatetext =findViewById(R.id.participatetext) ;
            ImageView dateicon = findViewById(R.id.showEventDateIcon) ;
            ImageView timeicon = findViewById(R.id.showEventTimeIcon) ;
            mapicon.setVisibility(View.GONE);
            dateicon.setVisibility(View.GONE);
            timeicon.setVisibility(View.GONE);
            participatetext.setVisibility(View.GONE);

            ConstraintLayout sendbox = findViewById(R.id.sendBox) ;

            sendbox.setVisibility(View.VISIBLE);


        }



        StorageReference myref = FirebaseStorage.getInstance().getReference().child("EventsImages").child(intent.getStringExtra("key")).child("1");
        GlideApp.with(ShowEvent.this)
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
                }).fitCenter().into(Photos);


    }


}
