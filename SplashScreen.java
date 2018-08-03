package in.kriscent.demostore;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Patterns;
import android.view.Window;
import android.view.WindowManager;

import java.util.regex.Pattern;

import in.kriscent.demostore.util.PrefernceSettings;

public class SplashScreen extends RuntimePermissionsActivity {
    private static final String TAG = "SplashScreen";
    private Context mContext;
    private static final int REQUEST_PERMISSIONS = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mContext = this;
        PrefernceSettings.openDataBase(mContext);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        int dens = dm.densityDpi;
        Log.e(TAG, "" + width + " " + height);
        PrefernceSettings.setWidth(String.valueOf(width));
        PrefernceSettings.setHeight(String.valueOf(height));


        SplashScreen.super.requestAppPermissions(new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_SMS}, R.string.runtime_permissions_txt, REQUEST_PERMISSIONS);


       /* Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
        Account[] accounts = AccountManager.get(mContext).getAccounts();
        for (Account account : accounts) {
            if (emailPattern.matcher(account.name).matches()) {
                String possibleEmail = account.name;
                Log.e("hjklo",""+possibleEmail);
            }
        }*/
    }

    private String getPhone() {
        TelephonyManager phoneMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return "";
        }
        return phoneMgr.getLine1Number();
    }

    @Override
    public void onPermissionsGranted(int requestCode) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String number = "";
                number=getPhone();
                if(number!=null){
                    if (!number.equals("")) {
                        if (number.length() > 10) {
                            if (number.length() == 11)
                                number = number.substring(1);
                            else if (number.length() == 12)
                                number = number.substring(2);
                            else if (number.length() == 13)
                                number = number.substring(3);
                        }
                    }
                }
                PrefernceSettings.setUserMobile(number);
                Log.e("nmbb",""+number);
                        Intent mainIntent = new Intent(SplashScreen.this, Navigation.class);
                        startActivity(mainIntent);
                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                        finish();
            }
        }, 3000);
    }

}
