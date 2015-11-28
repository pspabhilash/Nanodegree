package barqsoft.footballscores;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by yehya khaled on 2/27/2015.
 */
public class PagerFragment extends Fragment {
    public static final int NUM_PAGES = MainActivity.SPAN * 2 + 1;
    public ViewPager mPagerHandler;
    private myPageAdapter mPagerAdapter;
    private MainScreenFragment[] viewFragments = new MainScreenFragment[NUM_PAGES];

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.pager_fragment, container, false);
        mPagerHandler = (ViewPager) rootView.findViewById(R.id.pager);
        mPagerAdapter = new myPageAdapter(getChildFragmentManager());
        for (int i = 0; i < NUM_PAGES; i++) {
            viewFragments[i] = new MainScreenFragment();
            viewFragments[i].setFragmentDate(Utilies.getFragmentDate(i - MainActivity.SPAN));
        }
        mPagerHandler.setAdapter(mPagerAdapter);
        mPagerHandler.setCurrentItem(MainActivity.current_fragment);
        return rootView;
    }

    private class myPageAdapter extends FragmentStatePagerAdapter {
        @Override
        public Fragment getItem(int i) {
            return viewFragments[i];
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }

        public myPageAdapter(FragmentManager fm) {
            super(fm);
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            return Utilies.getDayName(getActivity(), Utilies.getLongDateWithOffset(position - MainActivity.SPAN));
        }

        
    }
}
