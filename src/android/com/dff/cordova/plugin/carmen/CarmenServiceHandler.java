package com.dff.cordova.plugin.carmen;

import android.app.Service;
import com.dff.cordova.plugin.carmen.service.CarmenService;
import com.dff.cordova.plugin.common.service.ServiceHandler;
import org.apache.cordova.CordovaInterface;

/**
 * Created by frank on 21.12.16.
 */
public class CarmenServiceHandler extends ServiceHandler {
    public CarmenServiceHandler(CordovaInterface cordova) {
        super(cordova, CarmenService.class);
    }
}
