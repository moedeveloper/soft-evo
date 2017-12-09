package android.app.printerapp.model.postModels;

/**
 * Created by SAMSUNG on 2017-12-07.
 */

public class HallflowTestPost{
    String operatorId;
    String relativehumidity;
    String temperature;
    String tap;
    String materialId;
    String machineId;

    public String getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
    }

    public String getRelativehumidity() {
        return relativehumidity;
    }

    public void setRelativehumidity(String relativehumidity) {
        this.relativehumidity = relativehumidity;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getTap() {
        return tap;
    }

    public void setTap(String tap) {
        this.tap = tap;
    }

    public String getMaterialId() {
        return materialId;
    }

    public void setMaterialId(String materialId) {
        this.materialId = materialId;
    }

    public String getMachineId() {
        return machineId;
    }

    public void setMachineId(String machineId) {
        this.machineId = machineId;
    }
}