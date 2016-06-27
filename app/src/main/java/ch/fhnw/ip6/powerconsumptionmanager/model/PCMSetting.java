package ch.fhnw.ip6.powerconsumptionmanager.model;

public abstract class PCMSetting {
    private String mName;

    abstract void inflateLayout();
    abstract void save();

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }
}
