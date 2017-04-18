package com.github.projectx.network;


import android.content.Context;
import android.util.Log;

import com.github.projectx.model.NewServiceRequest;
import com.github.projectx.model.Service;
import com.github.projectx.network.api.ServiceAPI;
import com.github.projectx.utils.UiThread;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Response;


/**
 * Created by ivan on 15.04.17.
 */

public class ServiceController extends BaseController {

    private static ServiceController instance;
    private final ServiceAPI api;

    private final ExecutorService serviceInfoExecutor = Executors.newSingleThreadExecutor();
    private final ExecutorService serviceListExecutor = Executors.newSingleThreadExecutor();
    private final ExecutorService serviceEditExecutor = Executors.newSingleThreadExecutor();

    private ServiceListCallback serviceListCallback;
    private ServiceInfoCallback serviceInfoCallback;
    private ServiceEditCallback serviceEditCallback;


    private ServiceController(Context context) {
        super(context);
        api = retrofit.create(ServiceAPI.class);
    }

    public static synchronized ServiceController getInstance(Context context) {
        if (instance == null) {
            instance = new ServiceController(context);
        }
        return instance;
    }


    public void sendNewService(final NewServiceRequest request) {
        serviceEditExecutor.execute(new Runnable() {
            @Override
            public void run() {
                final boolean success = createService(request);
                UiThread.run(new Runnable() {
                    @Override
                    public void run() {
                        if (serviceEditCallback != null) {
                            serviceEditCallback.onRequestComplete(success);
                        }
                    }
                });
            }
        });

    }

    public void queryForService(final long id) {
        serviceInfoExecutor.execute(new Runnable() {
            @Override
            public void run() {
                final Service service = requestServiceInfo(id);
                UiThread.run(new Runnable() {
                    @Override
                    public void run() {
                        if (serviceInfoCallback != null) {
                            if (service != null) {
                                serviceInfoCallback.onDataLoaded(service);
                            }
                            else {
                                serviceInfoCallback.dataLoadingFailed();
                            }
                        }
                    }
                });
            }
        });
    }




    public void queryForServiceList(final String category,
                                    final String sort,
                                    final Integer page,
                                    final int limit) {
        serviceListExecutor.execute(new Runnable() {
            @Override
            public void run() {
                final List<Service> services = requestServiceList(category, sort, page, limit);
                UiThread.run(new Runnable() {
                    @Override
                    public void run() {
                        if (serviceListCallback != null) {
                            if (services != null) {
                                serviceListCallback.onDataLoaded(services);
                            }
                            else {
                                serviceListCallback.dataLoadingFailed();
                            }
                        }
                    }
                });
            }});
    }


    public void queryForMyServices(final Integer page,
                                    final int limit) {
        serviceListExecutor.execute(new Runnable() {
            @Override
            public void run() {
                final List<Service> services = requestMyServices(page, limit);
                UiThread.run(new Runnable() {
                    @Override
                    public void run() {
                        if (serviceListCallback != null) {
                            if (services != null) {
                                serviceListCallback.onDataLoaded(services);
                            }
                            else {
                                serviceListCallback.dataLoadingFailed();
                            }
                        }
                    }
                });
            }});
    }








    private boolean createService(NewServiceRequest request) {
        Call<Void> call = api.createService(request);
        try {
            Response<Void> response = call.execute();
            return response.isSuccessful() && response.code() == 200;
        }
        catch (IOException ex) {
            Log.d(TAG, "Failed creating service! " + ex.getMessage());
            return false;
        }
    }


    private Service requestServiceInfo(long id) {
        Call<Service> call = api.getService(id);
        try {
            Response<Service> response = call.execute();
            if (response.isSuccessful()) {
                return response.body();
            }
            return null;
        }
        catch (IOException ex) {
            Log.d(TAG, "Failed querying service info! " + ex.getMessage());
            return null;
        }
    }


    private List<Service> requestServiceList(String category, String sort, Integer page, int limit) {
        Call<List<Service>> call = api.getListServices(category, sort, page, limit);
        try {
            Response<List<Service>> response = call.execute();
            if (response.isSuccessful()) {
                return response.body();
            }
            return null;
        }
        catch (IOException ex) {
            Log.d(TAG, "Failed querying service list! " + ex.getMessage());
            return null;
        }
    }


    private List<Service> requestMyServices(Integer page, int limit) {
        Call<List<Service>> call = api.getMyServices(page, limit);
        try {
            Response<List<Service>> response = call.execute();
            if (response.isSuccessful()) {
                return response.body();
            }
            return null;
        }
        catch (IOException ex) {
            Log.d(TAG, "Failed querying service list! " + ex.getMessage());
            return null;
        }
    }



    public interface ServiceListCallback {
        void onDataLoaded(List<Service> services);
        void dataLoadingFailed();
    }

    public interface ServiceInfoCallback {
        void onDataLoaded(Service service);
        void dataLoadingFailed();
    }

    public interface ServiceEditCallback {
        void onRequestComplete(boolean success);
    }

    public void setServiceListCallback(ServiceListCallback serviceListCallback) {
        this.serviceListCallback = serviceListCallback;
    }

    public void setServiceInfoCallback(ServiceInfoCallback serviceInfoCallback) {
        this.serviceInfoCallback = serviceInfoCallback;
    }

    public void setServiceEditCallback(ServiceEditCallback serviceEditCallback) {
        this.serviceEditCallback = serviceEditCallback;
    }
    private static final String TAG = ServiceController.class.getSimpleName();
}
