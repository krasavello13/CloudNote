package ivan.kovalenko.login;

import ivan.kovalenko.notenote.R;
import android.app.ActionBar;
import android.app.ActionBar.TabListener;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends Activity implements TabListener{
	
	private ViewPager viewPager;
	private ActionBar actionBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main_activity);

		viewPager = (ViewPager) findViewById(R.id.pager);
		viewPager.setAdapter(new ViewPagerAdapter(getFragmentManager()));
		viewPager.setPageTransformer(false, new ViewPager.PageTransformer() {
		
			@Override
			// anim pager
			public void transformPage(View page, float position) {
				 page.setRotationY(position * -30);
			}
		});
		
		viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				actionBar.setSelectedNavigationItem(arg0);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});

		actionBar = getActionBar();
		
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		actionBar.addTab(actionBar.newTab().setText(getResources().getString(R.string.login)).setTabListener(this));
		actionBar.addTab(actionBar.newTab().setText(getResources().getString(R.string.registration)).setTabListener(this));
	
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {

		// for change tabs 
		viewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {

	}

	public static void showToast(Activity activity, String text){
		Toast toast = Toast.makeText(activity, text, Toast.LENGTH_LONG);
		toast.setGravity(Gravity.BOTTOM, 10, 50);
		toast.show();
	}
}