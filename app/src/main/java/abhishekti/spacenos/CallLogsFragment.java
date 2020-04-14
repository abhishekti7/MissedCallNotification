package abhishekti.spacenos;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class CallLogsFragment extends Fragment {

    private RecyclerView recyclerView;
    private CallLogsAdapter callLogsAdapter;
    private DatabaseHelper databaseHelper;
    private List<MissedCall> missedCallList;
    private Button btn_clear_logs;

    public CallLogsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_call_logs, container, false);
        recyclerView = view.findViewById(R.id.recyclerview);
        missedCallList = new ArrayList<>();
        btn_clear_logs = view.findViewById(R.id.btn_clear_logs);
        databaseHelper = new DatabaseHelper(view.getContext());

        Cursor cursor = databaseHelper.getAllCallData();
        if(cursor.getCount()==0){
            Toast.makeText(view.getContext(), "NO DATA", Toast.LENGTH_LONG).show();
            btn_clear_logs.setVisibility(View.GONE);
        }else{
            btn_clear_logs.setVisibility(View.VISIBLE);
            while (cursor.moveToNext()) {
                String phnum = cursor.getString(1);
                String time = cursor.getString(2);
                missedCallList.add(new MissedCall(phnum, time));
            }
        }

        btn_clear_logs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                AlertDialog builder = new AlertDialog.Builder(view.getContext())
                        .setIcon(R.drawable.alert_dialog)
                        .setTitle("Are You Sure?")
                        .setMessage("This will erase all call records")
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                databaseHelper.dropCallRecords();
                                missedCallList.clear();
                                callLogsAdapter.notifyDataSetChanged();
                                setUpRecyclerView();
                                Toast.makeText(view.getContext(), "Records Deleted Successfully", Toast.LENGTH_LONG).show();
                            }
                        })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .setCancelable(true)
                        .show();
            }
        });
        setUpRecyclerView();
        return view;
    }

    private void setUpRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        callLogsAdapter = new CallLogsAdapter(getContext(), missedCallList);
        recyclerView.setAdapter(callLogsAdapter);
        callLogsAdapter.notifyDataSetChanged();
    }

}
