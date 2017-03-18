package com.mymobilesafe.activities;

import com.mymobilesafe.domain.ContactsBean;
import com.mymobilesafe.engine.ReadContactsEngine;

import java.util.List;

/*联系人界面*/

public class FriendsActivity extends BaseTelSmsFriendsActivity {

    @Override
    protected List<ContactsBean> getDatas() {
        return ReadContactsEngine.readContacts(getApplicationContext());
    }
}
