package android.app.printerapp;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SAMSUNG on 2017-11-18.
 */

public class MainFragmentManager {

    private static MainFragmentManager mafc;
    private List<Fragment> allFragments;
    private FragmentManager mManager;
    private Fragment mCurrent;

    private MainFragmentManager(FragmentManager manager){
        this.mManager = manager;
        allFragments = new ArrayList<>();

        mManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {

            }
        });
    }

    private void updateInstance(Fragment fragment){
        if(fragment != null) {
            mCurrent = fragment;
            allFragments.add(fragment);
        }


    }

    public static MainFragmentManager getInstance(FragmentManager fragmentManager) {

//      If we have not created an instance yet, do that.
//      else, if the given context has the same fragment manager as the one saved,
//      return that
        try{
            if(mafc == null) {
                return mafc = new MainFragmentManager(
                        fragmentManager);
            }
          //  }else if(activity.getFragmentManager() == mManager) {
                return mafc;
            //}

        }catch(ClassCastException e){
            Log.d("MainFragmentManager", "Cannot cast this context to activity");
            e.printStackTrace();
        }
        return null;
    }

    public void setFragment(int id, Fragment fragment, String tag) {

        //start transaction
        FragmentTransaction fragmentTransaction = mManager.beginTransaction();

        //Pop backstack to avoid having bad references when coming from a Detail view
        mManager.popBackStack();

        //If there is a fragment being shown, hide it to show the new one
        if (mCurrent != null) {
            try {
                fragmentTransaction.hide(mCurrent);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }

        //Select fragment
        //Check if we already created the Fragment to avoid having multiple instances
        if (mManager.findFragmentByTag(tag) == null) {
            fragmentTransaction.add(id, fragment, tag);
        }
        updateInstance(fragment);

        //Show current fragment
        if (mCurrent != null) {
            Log.i("OUT", "Changing " + mCurrent.getTag());
            fragmentTransaction.show(mCurrent).commit();
        }
    }

    public void replaceFragment(int id, Fragment newFragment, String tag){

        FragmentTransaction fragmentTransaction = mManager.beginTransaction();
        mManager.popBackStack();

        if(mManager.findFragmentById(id) == null) {
            fragmentTransaction.add(id, newFragment, tag);
        }else {
            fragmentTransaction.replace(id, newFragment);
        }
        //TODO: What happens when we do replace?
        updateInstance(newFragment);

        fragmentTransaction.show(newFragment).commit();
    }

    public Fragment findFragmentByTag(String tag){
        return mManager.findFragmentByTag(tag);
    }

    public Fragment getActiveFragment(){
        return mCurrent;
    }
}
