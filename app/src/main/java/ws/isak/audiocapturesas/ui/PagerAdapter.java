package ws.isak.audiocapturesas.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.content.Context;

/**
 * Created by isakherman on 7/19/16. This is only a template with a few things extra
 */
public class PagerAdapter extends FragmentStatePagerAdapter {

    private int mNumOfTabs;


    //constructor
    public PagerAdapter (FragmentManager fm, int NumOfTabs) {
        super (fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem (int position) {
        switch (position) {
            case 0:
                RecFrag tabRec = new RecFrag();
                return tabRec;
            case 1:
                //TabFragment2 tab2 = new TabFragment2();
                //return tab2;
                TabFragment2 tab2 = new TabFragment2();
                return tab2;
            case 2:
                TabFragment3 tab3 = new TabFragment3();
                return tab3;
            default:
                return null;
        }
    }

    @Override
    public int getCount () {
        return mNumOfTabs;
    }
}
