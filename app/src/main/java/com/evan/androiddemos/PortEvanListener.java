package com.evan.androiddemos;

import android.os.Message;
import android.util.Log;

import com.evan.androiddemos.http.HttpServer;

import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.types.DeviceType;
import org.fourthline.cling.model.types.ServiceType;
import org.fourthline.cling.model.types.UDADeviceType;
import org.fourthline.cling.model.types.UDAServiceType;
import org.fourthline.cling.registry.DefaultRegistryListener;
import org.fourthline.cling.registry.Registry;
import org.fourthline.cling.support.igd.PortMappingListener;
import org.fourthline.cling.support.igd.callback.PortMappingAdd;
import org.fourthline.cling.support.igd.callback.PortMappingDelete;
import org.fourthline.cling.support.model.PortMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class PortEvanListener extends DefaultRegistryListener {
    private HttpServer mHttpServer;
    private android.os.Handler mHandler;

    private static final Logger log = Logger.getLogger(PortMappingListener.class.getName());

    public static final DeviceType IGD_DEVICE_TYPE = new UDADeviceType("InternetGatewayDevice", 1);
    public static final DeviceType CONNECTION_DEVICE_TYPE = new UDADeviceType("WANConnectionDevice", 1);

    public static final ServiceType IP_SERVICE_TYPE = new UDAServiceType("WANIPConnection", 1);
    public static final ServiceType PPP_SERVICE_TYPE = new UDAServiceType("WANPPPConnection", 1);

    protected PortMapping[] portMappings;

    // The key of the map is Service and equality is object identity, this is by-design
    protected Map<Service, List<PortMapping>> activePortMappings = new HashMap<>();

    public PortEvanListener(PortMapping portMapping,android.os.Handler handler) {
        this(new PortMapping[]{portMapping},handler);
    }

    public PortEvanListener(PortMapping[] portMappings,android.os.Handler handler) {
        this.portMappings = portMappings;
        mHandler = handler;
    }

    @Override
    synchronized public void deviceAdded(Registry registry, Device device) {

        Service connectionService;
        if ((connectionService = discoverConnectionService(device)) == null) return;

        log.fine("Activating port mappings on: " + connectionService);

        final List<PortMapping> activeForService = new ArrayList<>();
        for (final PortMapping pm : portMappings) {
            new PortMappingAdd(connectionService, registry.getUpnpService().getControlPoint(), pm) {

                @Override
                public void success(ActionInvocation invocation) {
                    log.fine("Port mapping added: " + pm);
                    activeForService.add(pm);

                    Log.e("evan", "Port mapping added: " + pm);
                    Message message = new Message();
                    message.what = 1;
                    mHandler.sendMessage(message);

            }

                @Override
                public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                    handleFailureMessage("Failed to add port mapping: " + pm);
                    handleFailureMessage("Reason: " + defaultMsg);

                    Log.e("evan","Failed mapping added: " + pm);
                    Log.e("evan","Reason mapping added: " + defaultMsg);

                    Message message = new Message();
                    message.what = 2;
                    mHandler.sendMessage(message);
                }
            }.run(); // Synchronous!
        }

        activePortMappings.put(connectionService, activeForService);
    }

    @Override
    synchronized public void deviceRemoved(Registry registry, Device device) {
        for (Service service : device.findServices()) {
            Iterator<Map.Entry<Service, List<PortMapping>>> it = activePortMappings.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<Service, List<PortMapping>> activeEntry = it.next();
                if (!activeEntry.getKey().equals(service)) continue;

                if (activeEntry.getValue().size() > 0)
                    handleFailureMessage("Device disappeared, couldn't delete port mappings: " + activeEntry.getValue().size());

                it.remove();
            }
        }
    }

    @Override
    synchronized public void beforeShutdown(Registry registry) {
        for (Map.Entry<Service, List<PortMapping>> activeEntry : activePortMappings.entrySet()) {

            final Iterator<PortMapping> it = activeEntry.getValue().iterator();
            while (it.hasNext()) {
                final PortMapping pm = it.next();
                log.fine("Trying to delete port mapping on IGD: " + pm);
                new PortMappingDelete(activeEntry.getKey(), registry.getUpnpService().getControlPoint(), pm) {

                    @Override
                    public void success(ActionInvocation invocation) {
                        log.fine("Port mapping deleted: " + pm);
                        it.remove();
                    }

                    @Override
                    public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                        handleFailureMessage("Failed to delete port mapping: " + pm);
                        handleFailureMessage("Reason: " + defaultMsg);
                    }

                }.run(); // Synchronous!
            }
        }
    }

    protected Service discoverConnectionService(Device device) {
        if (!device.getType().equals(IGD_DEVICE_TYPE)) {
            return null;
        }

        Device[] connectionDevices = device.findDevices(CONNECTION_DEVICE_TYPE);
        if (connectionDevices.length == 0) {
            log.fine("IGD doesn't support '" + CONNECTION_DEVICE_TYPE + "': " + device);
            return null;
        }

        Device connectionDevice = connectionDevices[0];
        log.fine("Using first discovered WAN connection device: " + connectionDevice);

        Service ipConnectionService = connectionDevice.findService(IP_SERVICE_TYPE);
        Service pppConnectionService = connectionDevice.findService(PPP_SERVICE_TYPE);

        if (ipConnectionService == null && pppConnectionService == null) {
            log.fine("IGD doesn't support IP or PPP WAN connection service: " + device);
        }

        return ipConnectionService != null ? ipConnectionService : pppConnectionService;
    }

    protected void handleFailureMessage(String s) {
        log.warning(s);
    }

}