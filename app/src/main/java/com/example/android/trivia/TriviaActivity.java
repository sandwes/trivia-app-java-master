package com.example.android.trivia;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;

import com.example.android.trivia.fragments.GameFragment;
import com.example.android.trivia.fragments.SetupFragment;
import com.example.android.trivia.fragments.SummaryFragment;
import com.example.android.trivia.viewmodels.GameViewModel;
import com.example.android.trivia.viewmodels.SetupViewModel;
import com.example.android.trivia.viewmodels.SummaryViewModel;

public class TriviaActivity extends AppCompatActivity implements
        SetupFragment.SetupFragmentCallback,
        GameFragment.GameFragmentCallback,
        SummaryFragment.SummaryFragmentCallback {


    private FragmentManager fm;

    private ActionBar mActionBar;
    private SetupViewModel setupVm;
    private GameViewModel gameVm;
    private SummaryViewModel summaryVm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_container);


        fm = getSupportFragmentManager();
        mActionBar = getSupportActionBar();

        setupVm = ViewModelProviders.of(this).get(SetupViewModel.class);

        gameVm = ViewModelProviders.of(this)
                .get(GameViewModel.class);
        summaryVm = ViewModelProviders.of(this)
                .get(SummaryViewModel.class);

        // UI of Fragment already inflated in Fragment
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);

        /* returns null if this is the first time onCreate is being called */
        if (fragment == null) {
            addFirstFragment(SetupFragment.newInstance());
        }
    }

    protected void addFirstFragment(Fragment fragment) {
        fm.beginTransaction()
                .add(R.id.fragment_container, fragment)
                .commit();
    }

    private void replaceFragment(Fragment fragment) {
        fm.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    private void addPreviousToBackStack(Fragment fragment) {
        fm.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.trivia_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            default:
                return super.onOptionsItemSelected(item);
        }
//        return true;
    }

    /* Game callbacks */
    /*SetupFragment Callbacks*/
    public void onStartGame() {
        gameVm.resetGame();
        replaceFragment(GameFragment.newInstance());
    }

    /*GameFragment Callbacks*/
    public void onFinishGame() {
        replaceFragment(SummaryFragment.newInstance());
    }

    /*SummaryFragment/LoginSuccessFragment Callback*/
    public void onSetupNewGame() {
        setupVm.clearDialogType();
        setupVm.resetChosenOptions();
        summaryVm.resetFinalScore();

        replaceFragment(SetupFragment.newInstance());
        //setPlayerNameInActionbar();
        invalidateOptionsMenu();
    }
}


