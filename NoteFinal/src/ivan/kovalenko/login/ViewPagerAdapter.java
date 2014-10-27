package ivan.kovalenko.login;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

public class ViewPagerAdapter extends FragmentPagerAdapter {

	public ViewPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int arg0) {
		Fragment fragment = null;
		switch (arg0) {
		case 0:
			fragment = new LoginFragment();
			break;
		case 1:
			fragment = new RegistrationFragment();
			break;
		}
		return fragment;
	}
	
	@Override
	public int getCount() {
		return 2;
	}
}
