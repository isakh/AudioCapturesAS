package ws.isak.audiocapturesas.activities;

import ws.isak.audiocapturesas.ui.PagerAdapter;
import ws.isak.audiocapturesas.R;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


public class AC_AS_Main extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ac_as_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar (toolbar);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab (tabLayout.newTab().setText("Tab Rec Java"));
        tabLayout.addTab (tabLayout.newTab().setText("Rec Test Java"));
        tabLayout.addTab (tabLayout.newTab().setText("Tab 3 Java"));
        tabLayout.setTabGravity (TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter (adapter);
        viewPager.addOnPageChangeListener (new TabLayout.TabLayoutOnPageChangeListener (tabLayout));
        tabLayout.setOnTabSelectedListener (new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected (TabLayout.Tab tab) {
                viewPager.setCurrentItem (tab.getPosition());
            }

            @Override
            public void onTabUnselected (TabLayout.Tab tab) {}

            @Override
            public void onTabReselected (TabLayout.Tab tab) {}
        });


    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        getMenuInflater().inflate(R.menu.menu_test, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.action_user) {
            Toast.makeText (this, "Action User Button Pressed", Toast.LENGTH_LONG).show();
            return true;
        }
        if (id == R.id.action_search) {
            Toast.makeText(this, "Action Search Button Pressed", Toast.LENGTH_LONG).show();
            return true;
        }
        if (id == R.id.menu_bookmark) {
            Toast.makeText(this, "Menu Bookmark Pressed", Toast.LENGTH_LONG).show();
            return true;
        }
        return super.onOptionsItemSelected (item);
    }
}
