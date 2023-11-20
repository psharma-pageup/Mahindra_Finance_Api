package com.app.mahindrafinancemfact.adaptors;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.app.mahindrafinancemfact.fragments.CompletedAssetList;
import com.app.mahindrafinancemfact.fragments.PendingAssetList;
import com.app.mahindrafinancemfact.fragments.TotalAssetList;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    public ViewPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new CompletedAssetList();
            case 1:
                return new PendingAssetList();
            case 2:
                return new TotalAssetList();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}
