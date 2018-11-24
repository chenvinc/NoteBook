package com.example.chenm.notebook.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.blankj.utilcode.util.LogUtils;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

/**
 * @author chenhongyu
 * @Date 2018/11/24
 * @Time 22:24
 * @Version 1.0
 * @Description ${DESCRIPTION}
 */
public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    private static final String APP_ID = "wxd242d60be526023a";
    IWXAPI iwxapi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //WXShareUtils share = new WXShareUtils(this);
        //api = share.getApi();
        //注意：
        //第三方开发者如果使用透明界面来实现WXEntryActivity，需要判断handleIntent的返回值，
        //如果返回值为false，则说明入参不合法未被SDK处理，应finish当前透明界面，
        //避免外部通过传递非法参数的Intent导致停留在透明界面，引起用户的疑惑
        iwxapi = WXAPIFactory.createWXAPI(this,APP_ID);
        try {
            if (!iwxapi.handleIntent(getIntent(), this)) {
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if (!iwxapi.handleIntent(intent, this)) {
            finish();
        }
    }
    @Override
    public void onReq(BaseReq baseReq) {
        LogUtils.e(baseReq.openId);
    }

    @Override
    public void onResp(BaseResp baseResp) {
        LogUtils.e(baseResp.errCode);
        LogUtils.e(baseResp.errStr);
        LogUtils.e(baseResp.openId);

    }
}
