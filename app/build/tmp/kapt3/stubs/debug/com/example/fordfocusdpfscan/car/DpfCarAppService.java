package com.example.fordfocusdpfscan.car;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u0003\u001a\u00020\u0004H\u0016J\b\u0010\u0005\u001a\u00020\u0006H\u0016\u00a8\u0006\u0007"}, d2 = {"Lcom/example/fordfocusdpfscan/car/DpfCarAppService;", "Landroidx/car/app/CarAppService;", "()V", "createHostValidator", "Landroidx/car/app/validation/HostValidator;", "onCreateSession", "Landroidx/car/app/Session;", "app_debug"})
public final class DpfCarAppService extends androidx.car.app.CarAppService {
    
    public DpfCarAppService() {
        super();
    }
    
    /**
     * Returns a [HostValidator] that allows connections from all Android Auto hosts.
     * For production / Play Store release, restrict to known Google hosts using
     * [HostValidator.ALLOW_ALL_HOSTS_VALIDATOR] only during development.
     */
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public androidx.car.app.validation.HostValidator createHostValidator() {
        return null;
    }
    
    /**
     * Called by the Android Auto host when a new session starts
     * (i.e., the user connects to the car or opens the app in AA).
     */
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public androidx.car.app.Session onCreateSession() {
        return null;
    }
}