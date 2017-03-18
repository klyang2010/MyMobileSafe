package com.mymobilesafe.activities;

import com.mymobilesafe.domain.ContactsBean;
import com.mymobilesafe.engine.ReadContactsEngine;

import java.util.List;

/**
 * Created by mrka on 17-2-6.
 */

public class SmslogsActivity extends BaseTelSmsFriendsActivity {
    @Override
    protected List<ContactsBean> getDatas() {
        return ReadContactsEngine.readSmslog(getApplicationContext());
    }
}
