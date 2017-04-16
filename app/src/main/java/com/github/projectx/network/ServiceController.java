package com.github.projectx.network;


import android.content.Context;

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
    private final ExecutorService serviceExecutor = Executors.newSingleThreadExecutor();
    private final ExecutorService serviceListExecutor = Executors.newSingleThreadExecutor();
    private ServiceListCallback serviceListCallback;
    private ServiceCallback serviceCallback;


    private ServiceController(Context context) {
        super(context);
        api = retrofit.create(ServiceAPI.class);
    }

    public static ServiceController getInstance(Context context) {
        if (instance == null) {
            instance = new ServiceController(context);
        }
        return instance;
    }

    public void setServiceListCallback(ServiceListCallback serviceListCallback) {
        this.serviceListCallback = serviceListCallback;
    }

    public void setServiceCallback(ServiceCallback serviceCallback) {
        this.serviceCallback = serviceCallback;
    }

    public void queryForService(final long id) {
        serviceExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    final Service service = requestServiceInfo(id);
                    UiThread.run(new Runnable() {
                        @Override
                        public void run() {
                            if (serviceCallback != null) {
                                serviceCallback.onDataLoaded(service);
                            }
                        }
                    });
                } catch (IOException ex) {
                    UiThread.run(new Runnable() {
                        @Override
                        public void run() {
                            if (serviceCallback != null) {
                                serviceCallback.dataLoadingFailed();
                            }
                        }
                    });
                }
            }
        });
    }

   private Service requestServiceInfo(long id) throws IOException {
       Call<Service> call = api.getService(id);
       Response<Service> response = call.execute();
       if (!response.isSuccessful()) {
           throw new IOException("Response is not successful");
       }
       return response.body();
   }

    public void queryForServiceList(final String category,
                                    final String sort,
                                    final Integer page,
                                    final int limit) {

        serviceListExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    final List<Service> services = requestListService(category, sort, page, limit);
                    UiThread.run(new Runnable() {
                        @Override
                        public void run() {
                            if (serviceListCallback != null) {
                                serviceListCallback.onDataLoaded(services);
                            }
                        }
                    });
                }
                catch (IOException ex) {
                    UiThread.run(new Runnable() {
                        @Override
                        public void run() {
                            if (serviceListCallback != null) {
                                serviceListCallback.dataLoadingFailed();
                            }
                        }
                    });
                }
        }});
    }

    private List<Service> requestListService(String category, String sort, Integer page, int limit) throws IOException {
        Call<List<Service>> call = api.getListServices(category, sort, page, limit);
        Response<List<Service>> response = call.execute();
        if (!response.isSuccessful()) {
            throw new IOException("Response is not successful");
        }
        return response.body();
    }


    public interface ServiceListCallback {
        void onDataLoaded(List<Service> services);

        void dataLoadingFailed();
    }

    public interface ServiceCallback {
        void onDataLoaded(Service service);

        void dataLoadingFailed();
    }
}
