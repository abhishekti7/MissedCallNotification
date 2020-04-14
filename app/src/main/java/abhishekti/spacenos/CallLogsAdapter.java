package abhishekti.spacenos;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CallLogsAdapter extends RecyclerView.Adapter<CallLogsAdapter.ViewHolder> {

    private Context context;
    private List<MissedCall> missedCallList;

    public CallLogsAdapter(Context context, List<MissedCall> missedCallList) {
        this.context = context;
        this.missedCallList = missedCallList;
    }

    @NonNull
    @Override
    public CallLogsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.call_logs_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CallLogsAdapter.ViewHolder holder, int position) {
        holder.missedCall_View.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_in));
        holder.setData(missedCallList.get(position).getPhnum(), missedCallList.get(position).getTime());
    }

    @Override
    public int getItemCount() {
        return missedCallList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private CardView missedCall_View;
        private TextView tv_phnum;
        private TextView tv_time;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            missedCall_View = itemView.findViewById(R.id.missedCall_View);
            tv_phnum = itemView.findViewById(R.id.tv_phnum);
            tv_time = itemView.findViewById(R.id.tv_time);
        }

        public void setData(final String num, String time){
            tv_phnum.setText("From: "+num);
            tv_time.setText("Missed at: "+time);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:"+num));
                    itemView.getContext().startActivity(intent);
                }
            });
        }
    }
}
