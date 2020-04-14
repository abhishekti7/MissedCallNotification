package abhishekti.spacenos;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;

public class HomeFragment extends Fragment {

    private DatabaseHelper databaseHelper;
    private LottieAnimationView listerning;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        databaseHelper = new DatabaseHelper(view.getContext());
        listerning = view.findViewById(R.id.listening_loader);

        listerning.setProgress(0);
        listerning.addAnimatorListener(new AnimatorListenerAdapter() {
            @Override public void onAnimationEnd(Animator animation) {
                listerning.setProgress(0);
            }
        });


        return view;
    }

}
