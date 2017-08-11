package loginproject;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.zjrb.coreprojectlibrary.common.base.BaseActivity;
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
                ARouter.getInstance().build("/module/login/ZBLoginActivity")
                        .navigation();

                break;
        }
    }
}
