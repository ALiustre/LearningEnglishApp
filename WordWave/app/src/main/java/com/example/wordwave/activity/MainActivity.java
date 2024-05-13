package com.example.wordwave.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.wordwave.R;
import com.example.wordwave.databinding.ActivityMainBinding;
import com.example.wordwave.dialog.AddDialogFragment;
import com.example.wordwave.fragment.Profile.ProfileFrament;
import com.example.wordwave.fragment.changepassword.ChangePassword;

import com.example.wordwave.fragment.home.HomeFragment;
import com.example.wordwave.fragment.library.LibraryFragment;
import com.example.wordwave.fragment.signout.SignOut;
import com.example.wordwave.user.Login;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private Uri ImageUri;

    FragmentManager fragmentManager;
    private Bitmap bitmap;
    final private ProfileFrament mMyProfileFragment = new ProfileFrament();

    TextView nameView, emailView;
    CircleImageView imageView;

    public static final int MY_REQUEST_CODE = 10;

    private BottomNavigationView bottomNavigationView;
    private NavigationView mNavigationView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        View headerView = binding.navView.getHeaderView(0);

        nameView = headerView.findViewById(R.id.nameView);
        emailView = headerView.findViewById(R.id.emailView);
        imageView = headerView.findViewById(R.id.imageView);


        setSupportActionBar(binding.appBarMain.toolbar);
        DrawerLayout drawer = binding.drawerLayout;
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, binding.appBarMain.toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        toggle.syncState();

        mNavigationView = binding.navView;
        mNavigationView.setNavigationItemSelectedListener(this);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();


                if (id == R.id.action_home) {
                    openFragment(new HomeFragment());
                    mNavigationView.setCheckedItem(R.id.nav_home);
                    return true;
                }else if (id == R.id.action_library) {
                    openFragment(new LibraryFragment());
                    mNavigationView.setCheckedItem(R.id.nav_library);
                    return true;
                }
                else if (id == R.id.add_button){
                    openDialog();
                    return true;
                }
                return false;
            }
        });

//        mAppBarConfiguration = new AppBarConfiguration.Builder(
//                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow, R.id.nav_profile, R.id.nav_change_password)
//                .setOpenableLayout(drawer)
//                .build();
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
//        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
//        NavigationUI.setupWithNavController(navigationView, navController);
        fragmentManager = getSupportFragmentManager();
        openFragment(new HomeFragment());
        mNavigationView.setCheckedItem(R.id.nav_home);
        bottomNavigationView.getMenu().findItem(R.id.action_home).setChecked(true);
        showUserInformation();

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("FRAGMENT_TO_LOAD")) {
            String fragmentToLoad = intent.getStringExtra("FRAGMENT_TO_LOAD");
            switch (fragmentToLoad) {
                case "Library":
                    openFragment(new LibraryFragment());
                    bottomNavigationView.getMenu().findItem(R.id.action_library).setChecked(true);
                    break;
                // Add more cases if needed for other fragments
            }
        }
    }
    private void openFragment(Fragment fragment) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        // Remove all fragments from the back stack to prevent going back to HomeFragment
        fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
    private void openDialog() {
        BottomSheetDialogFragment dialogFragment = new AddDialogFragment();
        dialogFragment.show(getSupportFragmentManager(), "dialog add");
    }





    public void showUserInformation(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            // Name, email address, and profile photo Url
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();

            nameView.setText(name);
            emailView.setText(email);
            Glide.with(this).load(photoUrl).error(R.drawable.default_avatar).into(imageView);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length > 0 && grantResults[0]  == PackageManager.PERMISSION_GRANTED){
            mMyProfileFragment.openGallery();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_sign_out) {
            // Thực hiện hành động Sign Out ở đây
            openFragment(new SignOut());
        }else if(id == R.id.nav_profile){
            openFragment(new ProfileFrament());
        } else if (id == R.id.nav_change_password) {
            openFragment(new ChangePassword());
        } else if (id == R.id.nav_home) {
            openFragment(new HomeFragment());
            bottomNavigationView.getMenu().findItem(R.id.action_home).setChecked(true);
        } else if (id == R.id.nav_library) {
            openFragment(new LibraryFragment());
            bottomNavigationView.getMenu().findItem(R.id.action_library).setChecked(true);
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        // Đảm bảo rằng bạn return true để đánh dấu rằng sự kiện này đã được xử lý.
        return true;
    }

}