package com.github.projectx.network;


import com.github.projectx.utils.UiThread;
import com.github.projectx.model.Service;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Response;


/**
 * Created by ivan on 15.04.17.
 */

public class NetworkService {

    private static NetworkService instance = new NetworkService();
    private final ApiService apiService = ApiService.retrofit.create(ApiService.class);
    private ServiceListCallback serviceListCallback;
    private ServiceCallback serviceCallback;

    private final ExecutorService serviceExecutor = Executors.newSingleThreadExecutor();
    private final ExecutorService serviceListExecutor = Executors.newSingleThreadExecutor();


    private NetworkService() {}

    public static NetworkService getInstance() {
        return instance;
    }

    public interface ServiceListCallback {
        void onDataLoaded(List<Service> services);
        void dataLoadingFailed();
    }

    public interface ServiceCallback {
        void onDataLoaded(Service service);
        void dataLoadingFailed();
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
       Call<Service> call = apiService.getService(id);
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
        Call<List<Service>> call = apiService.getListServices(category, sort, page, limit);
        Response<List<Service>> response = call.execute();
        if (!response.isSuccessful()) {
            throw new IOException("Response is not successful");
        }
        return response.body();
    }
}
