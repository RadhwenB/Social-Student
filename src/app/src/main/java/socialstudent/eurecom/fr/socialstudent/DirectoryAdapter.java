package socialstudent.eurecom.fr.socialstudent;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class DirectoryAdapter extends RecyclerView.Adapter<DirectoryAdapter.ViewHolder> {
    private Context context;
    private List<User> items;

    DirectoryAdapter(Context context, List<User> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.profil_item_layout, parent, false);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final User user = items.get(position);
        holder.name.setText(user.getName());
        holder.langages.setText(user.getLangages());
        if (!user.getCountry().equals("")) {
            holder.country.setImageResource(context.getResources().getIdentifier(user.getCountry().toLowerCase() + "_flag", "mipmap", context.getPackageName()));
        }
        final StorageReference myref = FirebaseStorage.getInstance().getReference().child("profile_images").child(user.getKey());
        myref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                GlideApp.with(context)
                        .load(FirebaseStorage.getInstance().getReference("profile_images").child(user.getKey()))
                        .fitCenter().into(holder.img);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                holder.img.setImageResource(R.drawable.ic_user);
            }
        });

        holder.setListener(position);

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private TextView langages;
        private ImageView country;
        private ImageView img;
        private View view ;

        ViewHolder(View itemView) {
            super(itemView);
            view=itemView;
            img = itemView.findViewById(R.id.item_photo);
            name = itemView.findViewById(R.id.item_name);
            langages = itemView.findViewById(R.id.item_langages);
            country = itemView.findViewById(R.id.item_flag);

        }

        void setListener(int position) {
            final User user = items.get(position);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(context,UserProfilDetailsActivity.class);
                    i.putExtra("key",user.getKey());
                    context.startActivity(i);
                }
            });
        }

    }
}
