package android.app.printerapp.model;

import java.util.List;

/**
 * Created by SAMSUNG on 2017-12-05.
 */

public class OperatorList {
    private List<Operator> operatorApi;

    public List<Operator> getOperators() {
        return operatorApi;
    }

    public void setOperators(List<Operator> operators) {
        this.operatorApi = operators;
    }
}
