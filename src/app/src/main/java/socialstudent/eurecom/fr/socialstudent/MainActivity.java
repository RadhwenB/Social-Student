package socialstudent.eurecom.fr.socialstudent;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{
    public static final int MY_PERMISSIONS_LOCATION = 0;
    DatabaseReference myRef;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    RecyclerView mainRecycler;
    TextView userName = null ;
    TextView userEmail=null ;
    FloatingActionButton addEvent = null ;
    SharedPreferences sharedPref ;
    ImageView profilImage;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Events");
        setSupportActionBar(toolbar);


        addEvent= findViewById(R.id.fab) ;
        addEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,AddEventActivity.class));
            }
        });


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {

                } else {
                    // User is signed out
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }
                // ...
            }

        };

        myRef = FirebaseDatabase.getInstance().getReference("Events");
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = null ;
        if(mAuth!=null) {
            mAuth.addAuthStateListener(mAuthListener);
            currentUser = mAuth.getCurrentUser();
            if(currentUser==null){
                mAuth.signOut();
            }
        }


        mainRecycler = findViewById(R.id.mainRecycler);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header=navigationView.getHeaderView(0) ;



        userName=header.findViewById(R.id.userNameNav);
        userEmail=header.findViewById(R.id.userEmailNav);
        profilImage=header.findViewById(R.id.userPhotoNav);


        final StorageReference myref = FirebaseStorage.getInstance().getReference().child("profile_images").child(currentUser.getUid());
        myref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                GlideApp.with(MainActivity.this)
                        .load(myref)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                return false;
                            }
                        }).fitCenter().into(profilImage);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                profilImage.setImageResource(R.drawable.ic_user);
            }
        });

        //Update Email and name in Navigation Menu !
        FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    userName.setText(user.getName());
                    userEmail.setText(user.getEmail());
                } else {
                    Toast.makeText(getApplicationContext(),"No user object retrieved ",Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

    @Override
    protected void onStart() {
        super.onStart();


        FirebaseRecyclerAdapter<Event, EventViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Event, EventViewHolder>(Event.class, R.layout.design_row, EventViewHolder.class, myRef) {
                    @Override
                    protected void populateViewHolder(final EventViewHolder viewHolder, final Event model, int position) {

                        final int[] count = {0};
                        FirebaseDatabase.getInstance().getReference().child("Participations").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                RelationEventUser aux;

                                for (DataSnapshot data : dataSnapshot.getChildren()) {
                                    aux = data.getValue(RelationEventUser.class);
                                    assert aux != null;
                                    if (aux.getEventKey().equals(model.getKey())) {
                                        count[0]++;
                                    }
                                }
                                viewHolder.setParticipantsNumber(count[0]);

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View view) {
                                                                    Intent intent = new Intent(MainActivity.this, ShowEvent.class);
                                                                    //intent.putExtra("key", model.getKey());
                                                                    intent.putExtra("title", model.getName())
                                                                            .putExtra("type", model.getType())
                                                                            .putExtra("time", model.getTime())
                                                                            .putExtra("date", model.getDate())
                                                                            .putExtra("place", model.getPlace())
                                                                            .putExtra("key", model.getKey())
                                                                            .putExtra("lat", model.getLat())
                                                                            .putExtra("private",model.isPrivate())
                                                                            .putExtra("lng", model.getLng())
                                                                            .putExtra("organizer", model.getOrganizer())
                                                                            .putExtra("description", model.getDescription())
                                                                            .putExtra("participantNumber", count[0]);
                                                                    startActivity(intent);
                                                                }
                                                            }
                        );

                        if(!model.isPrivate()) {
                            viewHolder.setDate(model.getDate());
                            viewHolder.setDescription(model.getDescription());
                            viewHolder.setName(model.getName());
                            viewHolder.setTime(model.getTime());
                            viewHolder.setPlace(model.getPlace());

                        } else {
                            viewHolder.setDescription(model.getDescription());
                            viewHolder.setName(model.getName());
                            viewHolder.preparePrivate();

                        }


                        StorageReference myref = FirebaseStorage.getInstance().getReference().child("EventsImages").child(model.getKey()).child("1");

                        final ProgressBar progressBar = (ProgressBar) viewHolder.mView.findViewById(R.id.viewHolderProgressBar);
                        progressBar.setVisibility(View.VISIBLE);
                        GlideApp.with(MainActivity.this)
                                .load(myref)
                                .listener(new RequestListener<Drawable>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                        progressBar.setVisibility(View.GONE);
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                        progressBar.setVisibility(View.GONE);
                                        return false;
                                    }
                                }).override(600, 600).fitCenter().into(viewHolder.Image);
                    }
                };




        mainRecycler.setAdapter(firebaseRecyclerAdapter);
        mainRecycler.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setAutoMeasureEnabled(false);
        mainRecycler.setLayoutManager(llm);


    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_logout) {
            // Handle the camera action
            sharedPref = this.getSharedPreferences("LoginPreferences", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.remove("username") ;
            editor.remove("password");
            editor.apply();
            mAuth.signOut();
        }

        if (id == R.id.nav_profil) {
            finish();
            startActivity(new Intent(this, ProfileEdit.class));
        }

        if (id == R.id.nav_directory) {
            finish();
            startActivity(new Intent(this, DirectoryActivity.class));
        }

        if (id == R.id.nav_events) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case MY_PERMISSIONS_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i("Access:", "Now Permissions are granted");
                    Intent i = new Intent(MainActivity.this, AddEventActivity.class);
                    startActivity(i);
                } else {
                    Log.i("Access: ", "Permissions are denied");
                    Toast.makeText(MainActivity.this, "Localisation permission required to add an event ", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }

}
