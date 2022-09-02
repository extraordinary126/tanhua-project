package com.yuhao.dubbo.api;

import com.yuhao.bean.Settings;

public interface SettingsApi {
    Settings getSettings(Long id);

    void setNofication(Settings settings);

    void updatePhoneNumber(String oldPhoneNumber, String newPhoneNumber);
}
