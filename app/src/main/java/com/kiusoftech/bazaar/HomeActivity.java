package com.kiusoftech.bazaar;

import android.content.Intent;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    public FloatingActionButton importFab,exportFab,expenseFab;
    public Intent intent;
    String mon;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        importFab = (FloatingActionButton) findViewById(R.id.importFab);
        exportFab = (FloatingActionButton) findViewById(R.id.exportFab);
        expenseFab = (FloatingActionButton) findViewById(R.id.expenseFab);

        importFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(HomeActivity.this,"Import Button clicked",Toast.LENGTH_SHORT).show();

                intent = new Intent(HomeActivity.this,ImportEditorActivity.class);
                startActivity(intent);
            }
        });

        exportFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(HomeActivity.this,"Export Button Clicked",Toast.LENGTH_SHORT).show();

                intent = new Intent(HomeActivity.this, ExportEditorActivity.class);
                startActivity(intent);

            }
        });

        expenseFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(HomeActivity.this,"Expense Button Clicked",Toast.LENGTH_SHORT).show();

                intent = new Intent(HomeActivity.this, ExpenseEditorActivity.class);
                startActivity(intent);

            }
        });

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                animateFab(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                animateFab(position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        // trying to achieve movement

    /*    viewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()){

                    case MotionEvent.ACTION_DOWN:

                        toolbar.showOverflowMenu();
                        break;
                    case MotionEvent.ACTION_UP:
                        toolbar.hideOverflowMenu();
                        break;
                }

                return false;
            }
        });*/

    }

    private void animateFab(int position) {
        switch (position) {
            case 0:
                expenseFab.hide();
                exportFab.hide();
                importFab.show();
                break;
            case 1:
                expenseFab.hide();
                importFab.hide();
                exportFab.show();
                break;
            case 2:
                exportFab.hide();
                importFab.hide();
                expenseFab.show();
                break;
            case 3:
                exportFab.hide();
                importFab.hide();
                expenseFab.hide();
                break;
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ImportFragment(), "IMPORT");
        adapter.addFragment(new ExportFragment(), "EXPORT");
        adapter.addFragment(new ExpenseFragment(), "EXPENSE");
        adapter.addFragment(new ReportFragment(), "REPORT");
        viewPager.setAdapter(adapter);

    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(ImportFragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }
        public void addFragment(ExportFragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }
        public void addFragment(ExpenseFragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }
        public void addFragment(ReportFragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }
        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_items, menu);

        MenuItem item = menu.getItem(1);

        // Todays date

        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String formatedDate= dateFormat.format(c.getTime());
        int day = c.get(Calendar.DAY_OF_WEEK);

        switch (day){
            case Calendar.SUNDAY:
                mon =", SUNDAY";
                item.setTitle(formatedDate+mon);
                break;
            case Calendar.MONDAY:
                mon =", MONDAY";
                item.setTitle(formatedDate + mon);
                break;
            case Calendar.TUESDAY:
                mon =", TUESDAY";
                item.setTitle(formatedDate + mon);
                break;
            case Calendar.WEDNESDAY:
                mon =", WEDNESDAY";
                item.setTitle(formatedDate+mon);
                break;
            case Calendar.THURSDAY:
                mon =", THURSDAY";
                item.setTitle(formatedDate + mon);
                break;
            case Calendar.FRIDAY:
                mon =", FRIDAY";
                item.setTitle(formatedDate + mon);
                break;
            case Calendar.SATURDAY:
                mon =", SATURDAY";
                item.setTitle(formatedDate + mon);
                break;
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.exit:
                finishAffinity();
                return true;
            /*case R.id.getReport:
                Toast.makeText(HomeActivity.this,"Report Generated.",Toast.LENGTH_SHORT).show();
                return true; */
        }
        return super.onOptionsItemSelected(item);
    }

}

