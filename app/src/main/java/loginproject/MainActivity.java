package loginproject;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.zjrb.coreprojectlibrary.common.base.BaseActivity;
import com.zjrb.coreprojectlibrary.nav.Nav;
import com.zjrb.loginproject.R;


public class MainActivity extends BaseActivity implements View.OnClickListener {

    private TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = (TextView)findViewById(R.id.tv_text);
        tv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_text:
                Nav.with(this).to(Uri.parse("http://www.8531.cn/login/LoginActivity")
                        .buildUpon()
                        .build(), 0);
//                ARouter.getInstance().build("/module/login/ZBLoginActivity")
//                        .navigation();

                break;
        }
    }
}
