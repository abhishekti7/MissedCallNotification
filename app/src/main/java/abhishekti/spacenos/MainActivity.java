package abhishekti.spacenos;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationMenu;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Arrays;

import nl.psdcompany.duonavigationdrawer.views.DuoDrawerLayout;
import nl.psdcompany.duonavigationdrawer.views.DuoMenuView;
import nl.psdcompany.duonavigationdrawer.widgets.DuoDrawerToggle;

public class MainActivity extends AppCompatActivity implements DuoMenuView.OnMenuClickListener {

    private Toolbar toolbar;
    private abhishekti.spacenos.MenuAdapter mMenuAdapter;
    private ViewHolder mViewHolder;
    public static int HOME=0;
    public static int ENABLE_RECEIVERS=1;
    DatabaseHelper databaseHelper;

    private int PERMISSION_ALL = 1;
    private String[] PERMISSIONS = {
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.READ_SMS,
            Manifest.permission.SEND_SMS,
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.CALL_PHONE
    };
    private ArrayList<String> mTitles = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mTitles = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.menuOptions)));
        mViewHolder = new ViewHolder();

        createNotificationChannel();

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }

        //Set up Database
        databaseHelper = new DatabaseHelper(this);
        HOME = 1;
        openFragment(new HomeFragment());

        handleMenu();
        handleDrawer();
        mMenuAdapter.setViewSelected(0, true);
        setTitle(mTitles.get(0));
    }
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("default", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
//    public void AddNotification(String title, String message){
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this)
//                .setSmallIcon(R.drawable.ic_home_black_24dp)
//                .setContentTitle("Urgent Call")
//                .setContentText("You have a missed call from "+"fdaflkndsf")
//                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
//
//        NotificationManager mNotificationManager =
//
//                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        NotificationManager.notify().
//                mNotificationManager.notify(001, builder.build());
//    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(HOME==0){
            openFragment(new HomeFragment());
        }else{
            finish();
        }
    }
    private void handleDrawer() {
        DuoDrawerToggle duoDrawerToggle = new DuoDrawerToggle(this,
                mViewHolder.mDuoDrawerLayout,
                mViewHolder.mToolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);

        mViewHolder.mDuoDrawerLayout.setDrawerListener(duoDrawerToggle);
        duoDrawerToggle.syncState();

    }
    private void handleMenu() {
        mMenuAdapter = new MenuAdapter(mTitles);

        mViewHolder.mDuoMenuView.setOnMenuClickListener(this);
        mViewHolder.mDuoMenuView.setAdapter(mMenuAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.btn_status:
                String message="";
                if(ENABLE_RECEIVERS==1){
                    ENABLE_RECEIVERS = 0;
                    message = "The receivers have been disabled.";
                    item.setTitle("Enable Receivers");
                }else{
                    ENABLE_RECEIVERS = 1;
                    message = "The receivers have been enabled";
                    item.setTitle("Disable Receivers");
                }
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }

    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onFooterClicked() {
//        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://spacenos.com/"));
//        startActivity(browserIntent);
    }

    @Override
    public void onHeaderClicked() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://spacenos.com/"));
        startActivity(browserIntent);
    }

    @Override
    public void onOptionClicked(int position, Object objectClicked) {
        setTitle(mTitles.get(position));

        // Set the right options selected
        mMenuAdapter.setViewSelected(position, true);

        // Navigate to the right fragment
        switch (position) {
            case 0:
                openFragment(new HomeFragment());
                break;
            case 1:
                openFragment(new CallLogsFragment());
                break;

            default:
                openFragment(new HomeFragment());
                break;
        }

        // Close the drawer
        mViewHolder.mDuoDrawerLayout.closeDrawer();
    }

    private class ViewHolder {
        private DuoDrawerLayout mDuoDrawerLayout;
        private DuoMenuView mDuoMenuView;
        private Toolbar mToolbar;

        ViewHolder() {
            mDuoDrawerLayout = (DuoDrawerLayout) findViewById(R.id.drawer);
            mDuoMenuView = (DuoMenuView) mDuoDrawerLayout.getMenuView();
            mToolbar = (Toolbar) findViewById(R.id.toolbar);
        }
    }
}
