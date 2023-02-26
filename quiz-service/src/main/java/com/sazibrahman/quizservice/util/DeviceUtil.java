package com.sazibrahman.quizservice.util;

import org.springframework.mobile.device.Device;


public class DeviceUtil {

    private DeviceUtil() {
    }

    public static int determinePageSize(Device device) {
		int pageSize = 10;
		
    	if(device == null) {
    		pageSize = 10;
    	} else if(device.isMobile()) {
    		pageSize = 1;
    	} else {
    		pageSize = 10;
    	}
    	
		return pageSize;
	}

}
