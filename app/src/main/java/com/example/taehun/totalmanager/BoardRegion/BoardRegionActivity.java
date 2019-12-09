package com.example.taehun.totalmanager.BoardRegion;

import android.content.Intent;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.taehun.totalmanager.MainActivity;
import com.example.taehun.totalmanager.R;

public class BoardRegionActivity extends AppCompatActivity {

    SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_region);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container); // 뷰페이저를 이용해 드레그시 화면 전환
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsingToolbar);
        collapsingToolbarLayout.setTitleEnabled(false);

        toolbar.setTitle("실종 게시판");

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.layout_left_in, R.anim.layout_right_out);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) { // 화면전환 코드

            switch (position) {
                case 0: {
                    Region1Fragment region1Fragment = new Region1Fragment();
                    return region1Fragment;
                }

                case 1: {
                    Region2Fragment region2Fragment = new Region2Fragment();
                    return region2Fragment;
                }

                case 2: {
                    Region3Fragment region3Fragment = new Region3Fragment();
                    return region3Fragment;
                }

                case 3: {
                    Region4Fragment region4Fragment = new Region4Fragment();
                    return region4Fragment;
                }

                case 4: {
                    Region5Fragment region5Fragment = new Region5Fragment();
                    return region5Fragment;
                }

                case 5: {
                    Region6Fragment region6Fragment = new Region6Fragment();
                    return region6Fragment;
                }

                case 6: {
                    Region7Fragment region7Fragment = new Region7Fragment();
                    return region7Fragment;
                }

                case 7: {
                    Region8Fragment region8Fragment = new Region8Fragment();
                    return region8Fragment;
                }

                case 8: {
                    Region9Fragment region9Fragment = new Region9Fragment();
                    return region9Fragment;
                }

                case 9: {
                    Region10Fragment region10Fragment = new Region10Fragment();
                    return region10Fragment;
                }

                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 10;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.write, menu);
        getMenuInflater().inflate(R.menu.search, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { // 메뉴 아이템 클릭시

        switch (item.getItemId()) {

            case R.id.nav_write: {

                Intent intent = new Intent(getApplicationContext(), BoardRegionMapActivity.class);
                startActivity(intent);
                break;
            }

            case R.id.nav_search: {

                Intent intent = new Intent(getApplicationContext(), BoardRegionSearchActivity.class);
                startActivity(intent);
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }
}
