package com.shreespark.pos_api.device.mapper;

import com.shreespark.pos_api.device.dto.response.DeviceResponse;
import com.shreespark.pos_api.device.entity.Device;
import org.springframework.stereotype.Component;

@Component
public class DeviceMapper {
    public DeviceResponse toResponse(Device d) {
        return new DeviceResponse(
                d.getId(), d.getDeviceCode(), d.getDeviceName(),
                d.getPlatform(), d.getAppVersion(), d.getStatus(),
                d.getLastSeenAt(), d.getCreatedAt()
        );
    }
}
