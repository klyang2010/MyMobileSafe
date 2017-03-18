package com.mymobilesafe.domain;

/**
 * Created by mrka on 17-2-5.
 */

public interface BlackTable {
    String phone = "phone";
    String mode = "mode";
    String time = "time";
    String blackTable = "blacktb";

    int SMS = 1 << 0;
    int TEL = 1 << 1;
    int ALL = SMS | TEL;
}
