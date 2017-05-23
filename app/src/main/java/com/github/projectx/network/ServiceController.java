package com.github.projectx.network;


import android.content.Context;

import com.github.projectx.model.NewServiceRequest;
import com.github.projectx.model.Service;
import com.github.projectx.network.api.ServiceAPI;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by ivan on 15.04.17.
 */

public class ServiceController {

    private static final String TAG = ServiceController.class.getSimpleName();
    private static ServiceController instance;
    private final ServiceAPI api;
    private ServiceListListener serviceListListener;
    private ServiceInfoListener serviceInfoListener;
    private ServiceEditListener serviceEditListener;

    private ServiceController(Context context) {
        api = NetHelper.getRetrofit(context).create(ServiceAPI.class);
    }

    public static synchronized ServiceController getInstance(Context context) {
        if (instance == null) {
            instance = new ServiceController(context);
        }
        return instance;
    }

    public void createService(final NewServiceRequest request) {
        api.createService(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                boolean success = response.code() == 200;
                if (serviceEditListener != null) {
                    serviceEditListener.onRequestComplete(success);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                if (serviceEditListener != null) {
                    serviceEditListener.onRequestComplete(false);
                }
            }
        });
    }

    public void requestServiceInfo(long id) {
        api.getService(id).enqueue(new Callback<Service>() {
            @Override
            public void onResponse(Call<Service> call, Response<Service> response) {
                Service service = response.body();
                if (serviceInfoListener != null) {
                    if (service != null) {
                        serviceInfoListener.onDataLoaded(service);
                    } else {
                        serviceInfoListener.dataLoadingFailed();
                    }
                }
            }

            @Override
            public void onFailure(Call<Service> call, Throwable t) {
                if (serviceInfoListener != null) {
                    serviceInfoListener.dataLoadingFailed();
                }
            }
        });
    }

    public void requestServiceList(String category, String sort, Integer page, int limit) {
        api.getListServices(category, sort, page, limit).enqueue(new Callback<List<Service>>() {
            @Override
            public void onResponse(Call<List<Service>> call, Response<List<Service>> response) {
                List<Service> services = response.body();
                if (serviceListListener != null) {
                    if (services != null) {
                        serviceListListener.onDataLoaded(services);
                    } else {
                        serviceListListener.dataLoadingFailed();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Service>> call, Throwable t) {
                if (serviceListListener != null) {
                    serviceListListener.dataLoadingFailed();
                }
            }
        });
    }

    public void requestMyServices(Integer page, int limit) {
        api.getMyServices(page, limit).enqueue(new Callback<List<Service>>() {
            @Override
            public void onResponse(Call<List<Service>> call, Response<List<Service>> response) {
                List<Service> services = response.body();
                if (serviceListListener != null) {
                    if (services != null) {
                        serviceListListener.onDataLoaded(services);
                    } else {
                        serviceListListener.dataLoadingFailed();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Service>> call, Throwable t) {
                if (serviceListListener != null) {
                    serviceListListener.dataLoadingFailed();
                }
            }
        });
    }

    public void setServiceListListener(ServiceListListener serviceListListener) {
        this.serviceListListener = serviceListListener;
    }

    public void setServiceInfoListener(ServiceInfoListener serviceInfoListener) {
        this.serviceInfoListener = serviceInfoListener;
    }

    public void setServiceEditListener(ServiceEditListener serviceEditListener) {
        this.serviceEditListener = serviceEditListener;
    }

    public interface ServiceListListener {
        void onDataLoaded(List<Service> services);

        void dataLoadingFailed();
    }

    public interface ServiceInfoListener {
        void onDataLoaded(Service service);

        void dataLoadingFailed();
    }

    public interface ServiceEditListener {
        void onRequestComplete(boolean success);
    }
}
