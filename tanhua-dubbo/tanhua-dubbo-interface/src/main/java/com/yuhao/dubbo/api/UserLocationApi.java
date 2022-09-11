package com.yuhao.dubbo.api;

import java.util.List;

public interface UserLocationApi {

    Boolean updateLocation(Long id, Double longitude, Double latitude, String address);

    List<Long> getNearUser(Long id, Double valueOf);
}
