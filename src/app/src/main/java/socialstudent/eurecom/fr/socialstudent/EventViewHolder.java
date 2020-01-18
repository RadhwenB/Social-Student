package socialstudent.eurecom.fr.socialstudent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by aliouidhia on 26/11/2017.
 */

public class EventViewHolder extends RecyclerView.ViewHolder {

    View mView ;
    ImageView Image = null ;

    public EventViewHolder(View itemView) {
        super(itemView);
        mView = itemView;
        Image = mView.findViewById(R.id.eventImage);

    }

    public void preparePrivate() {
        ImageView map = mView.findViewById(R.id.rowMapIcon) ;
        ImageView date = mView.findViewById(R.id.rowDateIcon) ;
        ImageView time = mView.findViewById(R.id.rowTimeIcon) ;
        TextView place = mView.findViewById(R.id.eventPlace);
        TextView timedata = mView.findViewById(R.id.eventDate);
        TextView datedata = mView.findViewById(R.id.eventTime);
        ImageView star =mView.findViewById(R.id.privateStar) ;
        TextView privateInfo = mView.findViewById(R.id.privateInfo) ;


        map.setVisibility(View.INVISIBLE);
        date.setVisibility(View.INVISIBLE);
        time.setVisibility(View.INVISIBLE);
        place.setVisibility(View.INVISIBLE);
        timedata.setVisibility(View.INVISIBLE);
        datedata.setVisibility(View.INVISIBLE);

        star.setVisibility(View.VISIBLE);
        privateInfo.setVisibility(View.VISIBLE);

    }

    public void setName(String name){

        TextView name1 = mView.findViewById(R.id.eventName);
        name1.setText(name);
    }
    void setPlace(String location){

        TextView place = mView.findViewById(R.id.eventPlace);
        place.setText(location);
    }
    void setDate(String date){

        TextView date1 = mView.findViewById(R.id.eventDate);
        date1.setText(date);
    }
    void setTime(String time){

        TextView time1 = mView.findViewById(R.id.eventTime);
        time1.setText(time);
    }
    void setDescription(String description){

        TextView description1 = mView.findViewById(R.id.shortDescription);
        description1.setText(description);
    }

    void setParticipantsNumber(int participantsNumber) {
        TextView number = mView.findViewById(R.id.participantsNumber);
        number.setText(String.valueOf(participantsNumber));
    }
}
